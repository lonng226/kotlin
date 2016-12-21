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

package org.w3c.performance

import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.css.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.workers.*
import org.w3c.xhr.*

public external abstract class Performance : EventTarget() {
    open val timing: PerformanceTiming
    open val navigation: PerformanceNavigation
    fun now(): Double
}

public external interface GlobalPerformance {
    val performance: Performance
}

public external abstract class PerformanceTiming {
    open val navigationStart: Int
    open val unloadEventStart: Int
    open val unloadEventEnd: Int
    open val redirectStart: Int
    open val redirectEnd: Int
    open val fetchStart: Int
    open val domainLookupStart: Int
    open val domainLookupEnd: Int
    open val connectStart: Int
    open val connectEnd: Int
    open val secureConnectionStart: Int
    open val requestStart: Int
    open val responseStart: Int
    open val responseEnd: Int
    open val domLoading: Int
    open val domInteractive: Int
    open val domContentLoadedEventStart: Int
    open val domContentLoadedEventEnd: Int
    open val domComplete: Int
    open val loadEventStart: Int
    open val loadEventEnd: Int
}

public external abstract class PerformanceNavigation {
    open val type: Short
    open val redirectCount: Short

    companion object {
        val TYPE_NAVIGATE: Short
        val TYPE_RELOAD: Short
        val TYPE_BACK_FORWARD: Short
        val TYPE_RESERVED: Short
    }
}

