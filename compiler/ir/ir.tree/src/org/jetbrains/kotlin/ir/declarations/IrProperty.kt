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

package org.jetbrains.kotlin.ir.declarations

import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

interface IrProperty : IrMemberDeclaration {
    override val descriptor: PropertyDescriptor
    var getter: IrPropertyGetter?
    var setter: IrPropertySetter?

    override val declarationKind: IrDeclarationKind
        get() = IrDeclarationKind.PROPERTY

    fun <D> acceptAccessors(visitor: IrElementVisitor<Unit, D>, data: D)
}

interface IrSimpleProperty : IrProperty {
    val valueInitializer: IrBody?
}

interface IrDelegatedProperty : IrProperty {
    val delegateInitializer: IrBody
}

// TODO synchronization?
abstract class IrPropertyBase(
        startOffset: Int,
        endOffset: Int,
        originKind: IrDeclarationOriginKind,
        override val descriptor: PropertyDescriptor
) : IrMemberDeclarationBase(startOffset, endOffset, originKind), IrProperty {
    override var getter: IrPropertyGetter? = null
        set(newGetter) {
            newGetter?.property = this
            field = newGetter
        }

    override var setter: IrPropertySetter? = null
        set(newSetter) {
            newSetter?.property = this
            field = newSetter
        }

    override fun <D> acceptAccessors(visitor: IrElementVisitor<Unit, D>, data: D) {
        getter?.accept(visitor, data)
        setter?.accept(visitor, data)
    }
}

class IrSimplePropertyImpl(
        startOffset: Int,
        endOffset: Int,
        originKind: IrDeclarationOriginKind,
        descriptor: PropertyDescriptor,
        override val valueInitializer: IrBody?
) : IrPropertyBase(startOffset, endOffset, originKind, descriptor), IrSimpleProperty {
    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R =
            visitor.visitSimpleProperty(this, data)

    override fun <D> acceptChildren(visitor: IrElementVisitor<Unit, D>, data: D) {
        valueInitializer?.accept(visitor, data)
    }
}

class IrDelegatedPropertyImpl(
        startOffset: Int,
        endOffset: Int,
        originKind: IrDeclarationOriginKind,
        descriptor: PropertyDescriptor,
        override val delegateInitializer: IrBody
) : IrPropertyBase(startOffset, endOffset, originKind, descriptor), IrDelegatedProperty {
    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R =
            visitor.visitDelegatedProperty(this, data)

    override fun <D> acceptChildren(visitor: IrElementVisitor<Unit, D>, data: D) {
        delegateInitializer.accept(visitor, data)
    }
}