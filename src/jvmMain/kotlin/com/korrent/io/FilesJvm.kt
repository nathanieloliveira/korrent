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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.io.Reader
import kotlinx.io.charsets.Charset
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.security.MessageDigest
import kotlin.coroutines.CoroutineContext

actual fun formatString(format: String, vararg args: Any?): String {
    val string = String.format(format, *args)
    return string
}

actual fun readFile(path: String, useFile: (Reader) -> Unit) {
    val file = File(path)
    InputStreamReader(FileInputStream(file)).use { reader ->
        useFile(reader)
    }
}

actual fun readWholeFile(path: String, charset: Charset): String {
    InputStreamReader(FileInputStream(File(path)), charset).use { reader ->
        return reader.readText()
    }
}

actual fun threadSleep(ms: Long) {
    Thread.sleep(ms)
}

actual inline fun <T> kRunBlocking(context: CoroutineContext, crossinline block: suspend CoroutineScope.() -> T): T {
    return kotlinx.coroutines.runBlocking(context) {
        block(this)
    }
}

actual fun <T> CoroutineScope.runAsync(
    context: CoroutineContext,
    start: CoroutineStart,
    block: suspend CoroutineScope.() -> T
): Deferred<T> {
    return async {
        block()
    }
}

actual fun getCurrentTimeMillis(): Long {
    return System.currentTimeMillis()
}

actual fun sha1Hash(string: String): ByteArray {
    val sha1 = MessageDigest.getInstance("SHA1")
    return sha1.digest(string.toByteArray(Charsets.ISO_8859_1))
}