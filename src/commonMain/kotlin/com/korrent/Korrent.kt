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

package com.korrent

import com.korrent.bencode.Ben
import com.korrent.io.readWholeFile
import com.korrent.io.sha1Hash
import com.korrent.model.Torrent
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.io.charsets.Charset
import kotlinx.io.charsets.Charsets
import kotlinx.io.core.ByteReadPacket
import kotlinx.io.core.IoBuffer
import kotlinx.io.core.readLongLittleEndian
import kotlinx.io.pool.useInstance
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext

@Serializable
data class KorrentFile(val name: String, val id: Long)

class Korrent : CoroutineScope {
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job

    init {
        val file = Json.plain.stringify(KorrentFile.serializer(), KorrentFile("Hellow World", 2))
        println("Initializing Korrent Lib: $file")
        GlobalScope.launch {
        }
    }

    fun parse(packet: ByteReadPacket, channel: SendChannel<ByteReadPacket>) {
        IoBuffer.Pool.useInstance {
            it.readLongLittleEndian()
        }
    }

    suspend fun getVal(): Int {
        delay(1000)
        return 10
    }
}

fun parseTorrentFile(path: String): Torrent {
    return Ben.plain.parse(Torrent.serializer(), readWholeFile(path, Charset.forName("ASCII")))
}

fun parseTorrentHash(path: String): ByteArray {
    val content = readWholeFile(path, Charsets.ISO_8859_1)
    // find where is info key
    val (infoIndex, _) = content.findAnyOf(listOf("4:info")) ?: -1 to ""
    if (infoIndex != -1) {
        val infoValue = content.substring(infoIndex + 6, content.length - 1)
        return sha1Hash(infoValue)
    } else {
        throw RuntimeException("couldn't find info index")
    }
}
