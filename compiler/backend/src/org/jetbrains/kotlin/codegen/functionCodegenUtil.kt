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

package org.jetbrains.kotlin.codegen

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ParameterDescriptor
import org.jetbrains.kotlin.psi.KtPureElement
import org.jetbrains.kotlin.types.KotlinType

fun <T: KtPureElement> MemberCodegen<T>.addInnerClassesUsedInDescriptor(functionDescriptor: FunctionDescriptor) {
    functionDescriptor.extensionReceiverParameter?.let {
        addInnerClass(it)
    }

    functionDescriptor.valueParameters.forEach {
        addInnerClass(it)
    }

    functionDescriptor.returnType?.let {
        addInnerClass(it)
    }
}

private fun <T : KtPureElement> MemberCodegen<T>.addInnerClass(parameter: ParameterDescriptor) {
    addInnerClass(parameter.type)
}

private fun <T : KtPureElement> MemberCodegen<T>.addInnerClass(type: KotlinType) {
    (type.constructor.declarationDescriptor as? ClassDescriptor)?.let {
        this.addInnerClass(it)
    }
}