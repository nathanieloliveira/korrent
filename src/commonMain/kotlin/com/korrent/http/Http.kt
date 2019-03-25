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

package com.korrent.http

import io.ktor.client.HttpClient
import io.ktor.http.Headers
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.io.core.ByteReadPacket

class Request

data class Response(
    val status: Int,
    val headers: Headers,
    val body: ByteReadChannel
)

expect fun getHttpClient(): HttpClient

expect suspend fun parseHttpResponse(input: ByteReadChannel): Response

fun ByteReadPacket.toChannel(): ByteReadChannel = ByteReadChannel(readText())