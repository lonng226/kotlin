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

/*
 * Generated file
 * DO NOT EDIT
 * 
 * See libraries/tools/idl2k for details
 */

package org.w3c.files

import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.css.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

public external open class Blob(blobParts: Array<dynamic> = noImpl, options: BlobPropertyBag = noImpl) {
    open val size: Int
    open val type: String
    open val isClosed: Boolean
    fun slice(start: Int = noImpl, end: Int = noImpl, contentType: String = noImpl): Blob
    fun close(): Unit
}

public external interface BlobPropertyBag {
    var type: String? /* = "" */
        get() = noImpl
        set(value) = noImpl
}

@Suppress("NOTHING_TO_INLINE")
public inline fun BlobPropertyBag(type: String? = ""): BlobPropertyBag {
    val o = js("({})")

    o["type"] = type

    return o
}

public external open class File(fileBits: Array<dynamic>, fileName: String, options: FilePropertyBag = noImpl) : Blob(noImpl, options) {
    open val name: String
    open val lastModified: Int
}

public external interface FilePropertyBag : BlobPropertyBag {
    var lastModified: Int?
        get() = noImpl
        set(value) = noImpl
}

@Suppress("NOTHING_TO_INLINE")
public inline fun FilePropertyBag(lastModified: Int?, type: String? = ""): FilePropertyBag {
    val o = js("({})")

    o["lastModified"] = lastModified
    o["type"] = type

    return o
}

public external abstract class FileList {
    open val length: Int
    fun item(index: Int): File?
    @nativeGetter
    operator fun get(index: Int): File?
}

public external open class FileReader : EventTarget() {
    open val readyState: Short
    open val result: dynamic
    open val error: dynamic
    var onloadstart: ((Event) -> dynamic)?
    var onprogress: ((Event) -> dynamic)?
    var onload: ((Event) -> dynamic)?
    var onabort: ((Event) -> dynamic)?
    var onerror: ((Event) -> dynamic)?
    var onloadend: ((Event) -> dynamic)?
    fun readAsArrayBuffer(blob: Blob): Unit
    fun readAsBinaryString(blob: Blob): Unit
    fun readAsText(blob: Blob, label: String = noImpl): Unit
    fun readAsDataURL(blob: Blob): Unit
    fun abort(): Unit

    companion object {
        val EMPTY: Short
        val LOADING: Short
        val DONE: Short
    }
}

public external open class FileReaderSync {
    fun readAsArrayBuffer(blob: Blob): ArrayBuffer
    fun readAsBinaryString(blob: Blob): String
    fun readAsText(blob: Blob, label: String = noImpl): String
    fun readAsDataURL(blob: Blob): String
}

