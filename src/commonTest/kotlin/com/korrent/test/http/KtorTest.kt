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

import com.korrent.http.getHttpClient
import com.korrent.io.kRunBlocking
import com.korrent.model.Event
import com.korrent.model.TrackerRequest
import com.korrent.parseTorrentFile
import com.korrent.parseTorrentHash
import com.korrent.utilities.*
import io.ktor.client.request.get
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KtorTest {

    private val client by lazy { getHttpClient() }

    @Test
    fun testSimpleRequest() = kRunBlocking {
        val request = client.get<String>("http://google.com")
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
    fun testUrlDecode() {
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
        ).map { it.toByte() }
            .toByteArray()
        val str = "%124Vx%9A%BC%DE%F1%23Eg%89%AB%CD%EF%124Vx%9A"
        val decoded = str.fromUrlEncodedByteArray()
        assertTrue { array.contentEquals(decoded) }
    }

    @ExperimentalUnsignedTypes
    @Test
    fun testInfoHashParsing() {
        val hash = "e8d22ebc30ca08dae976d701be686b94df74f01b"
        val array = byteArrayFromHexString(hash)
        val infoHash = parseTorrentHash("testfile.torrent")
        assertTrue { array.contentEquals(infoHash) }
    }

    @ExperimentalUnsignedTypes
    @Test
    fun getRequestFromTracker() = kRunBlocking {
        val torrent = parseTorrentFile("spider-man.torrent")
        val url = torrent.announce
        val parsedHash = parseTorrentHash("spider-man.torrent")
        println("hash from parsing=${parsedHash.toHexString()}")

        val trackerParameters = TrackerRequest(
            parsedHash.toUrlEncodedString(),
            generatePeerId().toUrlEncodedString(),
            6881,
            0,
            0,
            torrent.getTotalBytes(),
            false,
            false,
            Event.STARTED
        ).urlEncode()

        val requestUrl = "$url?$trackerParameters"
        println(requestUrl)

        val response = client.get<String>(requestUrl)
        println(response)
    }

}