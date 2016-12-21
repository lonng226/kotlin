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

package org.w3c.dom.url

import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.css.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

public external open class URL(url: String, base: String = noImpl) {
    var href: String
    open val origin: String
    var protocol: String
    var username: String
    var password: String
    var host: String
    var hostname: String
    var port: String
    var pathname: String
    var search: String
    open val searchParams: URLSearchParams
    var hash: String

    companion object {
        fun createObjectURL(blob: Blob): String
        fun createFor(blob: Blob): String
        fun revokeObjectURL(url: String): Unit
        fun domainToASCII(domain: String): String
        fun domainToUnicode(domain: String): String
    }
}

public external open class URLSearchParams(init: dynamic = noImpl) {
    fun append(name: String, value: String): Unit
    fun delete(name: String): Unit
    fun get(name: String): String?
    fun getAll(name: String): Array<String>
    fun has(name: String): Boolean
    fun set(name: String, value: String): Unit
}

