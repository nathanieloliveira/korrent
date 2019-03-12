package com.korrent

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.io.core.ByteReadPacket
import kotlinx.io.core.IoBuffer
import kotlinx.io.core.readLongLittleEndian
import kotlinx.io.core.use
import kotlinx.io.pool.useBorrowed
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