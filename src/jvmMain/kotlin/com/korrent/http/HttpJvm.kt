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
import io.ktor.client.engine.cio.CIO
import io.ktor.client.utils.buildHeaders
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.ConnectionOptions
import io.ktor.http.cio.parseHttpBody
import io.ktor.http.cio.parseResponse
import io.ktor.util.InternalAPI
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.coroutines.io.writer
import java.io.EOFException

actual fun getHttpClient(): HttpClient = HttpClient(CIO)

private suspend fun parseBody(
    method: HttpMethod,
    status: Int,
    contentLength: Long,
    transferEncoding: CharSequence?,
    connectionType: ConnectionOptions?,
    input: ByteReadChannel
) {

}

@KtorExperimentalAPI
@InternalAPI
actual suspend fun parseHttpResponse(
    input: ByteReadChannel
): Response {
    return coroutineScope {
        val rawResponse = parseResponse(input) ?: throw EOFException("Failed to parse HTTP response: unexpected EOF")

        val status = rawResponse.status
        val contentLength = rawResponse.headers[HttpHeaders.ContentLength]?.toString()?.toLong() ?: -1L
        val transferEncoding = rawResponse.headers[HttpHeaders.TransferEncoding]
        val connectionType = ConnectionOptions.parse(rawResponse.headers[HttpHeaders.Connection])

        val body = when (status) {
            HttpStatusCode.SwitchingProtocols.value -> {
                ByteReadChannel.Empty
            }
            else -> {
                val httpBodyParser = writer(autoFlush = true) {
                    parseHttpBody(contentLength, transferEncoding, connectionType, input, channel)
                }
                httpBodyParser.channel
            }
        }
        val headers = buildHeaders {
            for (i in 0 until rawResponse.headers.size) {
                val name = rawResponse.headers.nameAt(i)
                val value = rawResponse.headers.valueAt(i)
                this.append(name.toString(), value.toString())
            }
        }
        rawResponse.release()
        Response(status, headers, body)
    }
}