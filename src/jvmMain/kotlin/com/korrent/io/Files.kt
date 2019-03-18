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

import kotlinx.io.Reader
import kotlinx.io.charsets.Charset
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import kotlin.coroutines.CoroutineContext

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

actual inline fun runBlocking(context: CoroutineContext, crossinline block: suspend () -> Unit) {
    kotlinx.coroutines.runBlocking(context) {
        block()
    }
}