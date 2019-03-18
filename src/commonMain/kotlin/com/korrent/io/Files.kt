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
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

expect fun readFile(path: String, useFile: (Reader) -> Unit)
expect fun readWholeFile(path: String, charset: Charset = Charset.forName("UTF-8")): String
expect fun threadSleep(ms: Long)

expect inline fun runBlocking(context: CoroutineContext = EmptyCoroutineContext, crossinline block: suspend () -> Unit)