/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.js.coroutine

import com.google.dart.compiler.backend.js.ast.*
import com.google.dart.compiler.backend.js.ast.metadata.coroutineMetadata
import org.jetbrains.kotlin.js.inline.clean.FunctionPostProcessor
import org.jetbrains.kotlin.js.inline.util.collectLocalVariables
import org.jetbrains.kotlin.js.inline.util.getInnerFunction
import org.jetbrains.kotlin.js.translate.context.Namer
import org.jetbrains.kotlin.js.translate.utils.JsAstUtils

class CoroutineFunctionTransformer(private val program: JsProgram, private val function: JsFunction) {
    private val innerFunction = function.getInnerFunction()
    private val functionWithBody = innerFunction ?: function
    private val body = functionWithBody.body
    private val localVariables = (function.collectLocalVariables() + functionWithBody.collectLocalVariables()).toMutableSet()
    private val className = function.scope.parent.declareFreshName("Coroutine\$${function.name}")

    fun transform(): List<JsStatement> {
        val context = CoroutineTransformationContext(function.scope, function)
        val bodyTransformer = CoroutineBodyTransformer(program, context)
        bodyTransformer.preProcess(body)
        body.statements.forEach { it.accept(bodyTransformer) }
        val coroutineBlocks = bodyTransformer.postProcess()
        val globalCatchBlockIndex = coroutineBlocks.indexOf(context.globalCatchBlock)

        coroutineBlocks.forEach { it.jsBlock.collectAdditionalLocalVariables() }
        coroutineBlocks.forEach { it.jsBlock.replaceLocalVariables(function.scope, context, localVariables) }

        val additionalStatements = mutableListOf<JsStatement>()
        generateDoResume(coroutineBlocks, context, additionalStatements)
        generateContinuationConstructor(context, additionalStatements, globalCatchBlockIndex)

        generateCoroutineInstantiation()

        return additionalStatements
    }

    private fun generateContinuationConstructor(
            context: CoroutineTransformationContext,
            statements: MutableList<JsStatement>,
            globalCatchBlockIndex: Int
    ) {
        val constructor = JsFunction(function.scope.parent, JsBlock(), "Continuation")
        constructor.name = className
        constructor.parameters += function.parameters.map { JsParameter(it.name) }
        if (innerFunction != null) {
            constructor.parameters += innerFunction.parameters.map { JsParameter(it.name) }
        }

        val controllerName = if (context.metadata.hasController) {
            function.scope.declareFreshName("controller").apply { constructor.parameters += JsParameter(this) }
        }
        else {
            null
        }

        val interceptorName = function.scope.declareFreshName("interceptor")
        constructor.parameters += JsParameter(interceptorName)

        val parameterNames = (function.parameters.map { it.name } + innerFunction?.parameters?.map { it.name }.orEmpty()).toSet()

        constructor.body.statements.run {
            val baseClass = context.metadata.baseClassRef.deepCopy()
            this += JsInvocation(Namer.getFunctionCallRef(baseClass), JsLiteral.THIS, interceptorName.makeRef()).makeStmt()
            if (controllerName != null) {
                assignToField(context.controllerFieldName, controllerName.makeRef())
            }
            assignToField(context.metadata.exceptionStateName, program.getNumberLiteral(globalCatchBlockIndex))
            for (localVariable in localVariables) {
                val value = if (localVariable !in parameterNames) Namer.getUndefinedExpression() else localVariable.makeRef()
                assignToField(function.scope.getFieldName(localVariable), value)
            }
        }

        statements.addAll(0, listOf(constructor.makeStmt(), generateCoroutineMetadata(constructor.name)) +
                generateCoroutinePrototype(constructor.name))
    }

    private fun generateCoroutinePrototype(constructorName: JsName): List<JsStatement> {
        val prototype = JsAstUtils.prototypeOf(JsNameRef(constructorName))

        val baseClass = Namer.createObjectWithPrototypeFrom(function.coroutineMetadata!!.baseClassRef.deepCopy())
        val assignPrototype = JsAstUtils.assignment(prototype, baseClass)
        val assignConstructor = JsAstUtils.assignment(JsNameRef("constructor", prototype.deepCopy()), JsNameRef(constructorName))
        return listOf(assignPrototype.makeStmt(), assignConstructor.makeStmt())
    }

    private fun generateCoroutineMetadata(constructorName: JsName): JsStatement {
        val baseClassRefRef = function.coroutineMetadata!!.baseClassRef.deepCopy()

        val metadataObject = JsObjectLiteral(true)
        metadataObject.propertyInitializers += JsPropertyInitializer(
                JsNameRef("type"), JsNameRef("CLASS", JsNameRef("TYPE", Namer.KOTLIN_NAME)))
        metadataObject.propertyInitializers += JsPropertyInitializer(
                JsNameRef("classIndex"), JsInvocation(JsNameRef("newClassIndex", Namer.KOTLIN_NAME)))
        metadataObject.propertyInitializers += JsPropertyInitializer(JsNameRef("simpleName"), JsLiteral.NULL)
        metadataObject.propertyInitializers += JsPropertyInitializer(JsNameRef("baseClasses"), JsArrayLiteral(listOf(baseClassRefRef)))

        return JsAstUtils.assignment(JsNameRef(Namer.METADATA, constructorName.makeRef()), metadataObject).makeStmt()
    }

    private fun generateDoResume(
            coroutineBlocks: List<CoroutineBlock>,
            context: CoroutineTransformationContext,
            statements: MutableList<JsStatement>
    ) {
        val resumeFunction = JsFunction(function.scope.parent, JsBlock(), "resume function")

        val coroutineBody = generateCoroutineBody(context, coroutineBlocks)
        functionWithBody.body.statements.clear()

        resumeFunction.body.statements.apply {
            this += coroutineBody
        }

        val resumeName = function.coroutineMetadata!!.doResumeName
        statements.apply {
            assignToPrototype(resumeName, resumeFunction)
        }

        FunctionPostProcessor(resumeFunction).apply()
    }

    private fun generateCoroutineInstantiation() {
        val instantiation = JsNew(className.makeRef())
        instantiation.arguments += function.parameters.map { it.name.makeRef() }
        if (innerFunction != null) {
            instantiation.arguments += innerFunction.parameters.map { it.name.makeRef() }
        }

        if (function.coroutineMetadata!!.hasController) {
            instantiation.arguments += JsLiteral.THIS
        }

        val interceptorParamName = functionWithBody.scope.declareFreshName("interceptor")
        functionWithBody.parameters += JsParameter(interceptorParamName)

        instantiation.arguments += interceptorParamName.makeRef()

        functionWithBody.body.statements += JsReturn(instantiation)
    }

    private fun generateCoroutineBody(
            context: CoroutineTransformationContext,
            blocks: List<CoroutineBlock>
    ): List<JsStatement> {
        val indexOfGlobalCatch = blocks.indexOf(context.globalCatchBlock)
        val stateRef = JsNameRef(context.metadata.stateName, JsLiteral.THIS)

        val isFromGlobalCatch = JsAstUtils.equality(stateRef, program.getNumberLiteral(indexOfGlobalCatch))
        val catch = JsCatch(functionWithBody.scope, "e")
        val continueWithException = JsBlock(
                JsAstUtils.assignment(stateRef.deepCopy(), JsNameRef(context.metadata.exceptionStateName, JsLiteral.THIS)).makeStmt(),
                JsAstUtils.assignment(JsNameRef(context.metadata.exceptionName, JsLiteral.THIS),
                                      catch.parameter.name.makeRef()).makeStmt()
        )
        catch.body = JsBlock(JsIf(isFromGlobalCatch, JsThrow(catch.parameter.name.makeRef()), continueWithException))

        val throwResultRef = JsNameRef(context.metadata.exceptionName, JsLiteral.THIS)
        context.globalCatchBlock.statements += JsThrow(throwResultRef)

        val cases = blocks.withIndex().map { (index, block) ->
            JsCase().apply {
                caseExpression = program.getNumberLiteral(index)
                statements += block.statements
            }
        }
        val switchStatement = JsSwitch(stateRef.deepCopy(), cases)
        val loop = JsDoWhile(JsLiteral.TRUE, JsTry(JsBlock(switchStatement), catch, null))

        return listOf(loop)
    }

    private fun JsBlock.collectAdditionalLocalVariables() {
        accept(object : RecursiveJsVisitor() {
            override fun visit(x: JsVars.JsVar) {
                super.visit(x)
                localVariables += x.name
            }
        })
    }

    private fun MutableList<JsStatement>.assignToField(fieldName: JsName, value: JsExpression) {
        this += JsAstUtils.assignment(JsNameRef(fieldName, JsLiteral.THIS), value).makeStmt()
    }

    private fun MutableList<JsStatement>.assignToPrototype(fieldName: JsName, value: JsExpression) {
        this += JsAstUtils.assignment(JsNameRef(fieldName, JsAstUtils.prototypeOf(className.makeRef())), value).makeStmt()
    }
}