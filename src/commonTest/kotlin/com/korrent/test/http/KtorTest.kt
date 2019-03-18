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

package com.korrent.test.http

import com.korrent.io.runBlocking
import com.korrent.utilities.toUrlEncodedString
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.URLBuilder
import kotlin.test.Test
import kotlin.test.assertEquals

class KtorTest {

    @Test
    fun testSimpleRequest() = runBlocking {
        val client = HttpClient()
        val request = client.get<String>("http://google.com")
        URLBuilder()
        print(request)
    }

    @ExperimentalUnsignedTypes
    @Test
    fun testUrlEncode() {
        val str = "%124Vx%9A%BC%DE%F1%23Eg%89%AB%CD%EF%124Vx%9A"
        val array = arrayOf(
            0x12,
            0x34,
            0x56,
            0x78,
            0x9a,
            0xbc,
            0xde,
            0xf1,
            0x23,
            0x45,
            0x67,
            0x89,
            0xab,
            0xcd,
            0xef,
            0x12,
            0x34,
            0x56,
            0x78,
            0x9a
        )
            .map { it.toByte() }
            .toByteArray()
            .toUrlEncodedString()
        assertEquals(str, array)
    }

    @Test
    fun getRequestFromTracker() = runBlocking {

    }

}