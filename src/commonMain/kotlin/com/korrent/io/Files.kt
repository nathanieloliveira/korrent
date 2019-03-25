/*
 * Copyright (C) 2018 Nathaniel Salvador de Oliveira
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.korrent.io

import io.ktor.client.HttpClient
import io.ktor.util.pipeline.PipelinePhase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.io.Reader
import kotlinx.io.charsets.Charset
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

expect fun formatString(format: String, vararg args: Any?): String

expect fun readFile(path: String, useFile: (Reader) -> Unit)
expect fun readWholeFile(path: String, charset: Charset = Charset.forName("UTF-8")): String
expect fun threadSleep(ms: Long)

expect inline fun <T> kRunBlocking(
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline block: suspend CoroutineScope.() -> T
): T

expect fun getCurrentTimeMillis(): Long

expect fun sha1Hash(string: String): ByteArray

expect fun <T> CoroutineScope.runAsync(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
): Deferred<T>

fun test() {
    val client = HttpClient { }


    client.responsePipeline.intercept(PipelinePhase(" ")) {
        this.context.request.content
    }
}