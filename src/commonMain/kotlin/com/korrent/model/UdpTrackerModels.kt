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

package com.korrent.model

import kotlinx.io.charsets.Charsets
import kotlinx.io.core.ByteReadPacket
import kotlinx.io.core.buildPacket
import kotlinx.io.core.writeText

const val CONNECTION_ID: Long = 0x41727101980L

enum class UdpTrackerAction(val value: Int) {
    CONNECT(0),
    ANNOUNCE(1),
    SCRAPE(2),
    ERROR(3);

    companion object {
        fun fromValue(value: Int): UdpTrackerAction {
            return when (value) {
                0 -> CONNECT
                1 -> ANNOUNCE
                2 -> SCRAPE
                3 -> ERROR
                else -> throw IllegalArgumentException("Action value passed is not valid ('$value')")
            }
        }
    }
}

enum class UdpTrackerEvent(val value: Int) {
    NONE(0),
    COMPLETED(1),
    STARTED(2),
    STOPPED(3);

    companion object {
        fun fromValue(value: Int): UdpTrackerEvent {
            return when (value) {
                0 -> NONE
                1 -> COMPLETED
                2 -> STARTED
                3 -> STOPPED
                else -> throw IllegalArgumentException("Event value passed is not valid ('$value')")
            }
        }
    }
}

data class ConnectPacket(
    val connectionId: Long = CONNECTION_ID,
    val action: UdpTrackerAction = UdpTrackerAction.CONNECT,
    val transactionId: Int
) {
    companion object {
        fun fromPacket(packet: ByteReadPacket): ConnectPacket {
            val action = packet.readInt()
            val transactionId = packet.readInt()
            val connectionId = packet.readLong()
            return ConnectPacket(connectionId, UdpTrackerAction.fromValue(action), transactionId)
        }
    }

    fun toPacket() = buildPacket {
        writeLong(connectionId)
        writeInt(action.value)
        writeInt(transactionId)
    }
}

data class AnnounceRequestPacket(
    val connectionId: Long,
    val action: UdpTrackerAction = UdpTrackerAction.ANNOUNCE,
    val transactionId: Int,
    val infoHash: ByteArray,
    val peerId: ByteArray,
    val downloaded: Long,
    val left: Long,
    val uploaded: Long,
    val event: UdpTrackerEvent,
    val ipAddress: Int = 0,
    val key: Int,
    val numWant: Int = -1,
    val port: Short
) {
    fun toPacket() = buildPacket {
        require(infoHash.size == 20 && peerId.size == 20) { "InfoHash and PeerId arrays must have size 20" }
        writeLong(connectionId)
        writeInt(action.value)
        writeInt(transactionId)
        writeFully(infoHash, 0, infoHash.size)
        writeFully(peerId, 0, peerId.size)
        writeLong(downloaded)
        writeLong(left)
        writeLong(uploaded)
        writeInt(event.value)
        writeInt(ipAddress)
        writeInt(key)
        writeInt(numWant)
        writeShort(port)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as AnnounceRequestPacket

        if (connectionId != other.connectionId) return false
        if (action != other.action) return false
        if (transactionId != other.transactionId) return false
        if (!infoHash.contentEquals(other.infoHash)) return false
        if (!peerId.contentEquals(other.peerId)) return false
        if (downloaded != other.downloaded) return false
        if (left != other.left) return false
        if (uploaded != other.uploaded) return false
        if (event != other.event) return false
        if (ipAddress != other.ipAddress) return false
        if (key != other.key) return false
        if (numWant != other.numWant) return false
        if (port != other.port) return false

        return true
    }

    override fun hashCode(): Int {
        var result = connectionId.hashCode()
        result = 31 * result + action.value
        result = 31 * result + transactionId
        result = 31 * result + infoHash.contentHashCode()
        result = 31 * result + peerId.contentHashCode()
        result = 31 * result + downloaded.hashCode()
        result = 31 * result + left.hashCode()
        result = 31 * result + uploaded.hashCode()
        result = 31 * result + event.value
        result = 31 * result + ipAddress
        result = 31 * result + key
        result = 31 * result + numWant
        result = 31 * result + port
        return result
    }
}

data class BinaryPeer(val ip: Int, val port: Short)
data class AnnounceResponsePacket(
    val action: UdpTrackerAction = UdpTrackerAction.ANNOUNCE,
    val transactionId: Int,
    val interval: Int,
    val leechers: Int,
    val seeders: Int,
    val peers: List<BinaryPeer>
) {
    companion object {
        fun fromPacket(packet: ByteReadPacket): AnnounceResponsePacket {
            with(packet) {
                val action = readInt()
                val transactionId = readInt()
                val interval = readInt()
                val leechers = readInt()
                val seeders = readInt()

                val capacity: Int = (remaining / 6).toInt()
                val peers = ArrayList<BinaryPeer>(capacity)
                while (packet.canRead()) {
                    val ip = readInt()
                    val port = readShort()
                    peers.add(BinaryPeer(ip, port))
                }
                return AnnounceResponsePacket(
                    UdpTrackerAction.fromValue(action),
                    transactionId,
                    interval,
                    leechers,
                    seeders,
                    peers
                )
            }
        }
    }
}

data class ScrapeRequestPacket(
    val connectionId: Long,
    val action: UdpTrackerAction = UdpTrackerAction.SCRAPE,
    val transactionId: Int,
    val hashes: List<ByteArray>
) {
    fun toPacket() = buildPacket {
        writeLong(connectionId)
        writeInt(action.value)
        writeInt(transactionId)
        require(hashes.size <= 74) { "Hashes list size must not exceed 74. Size was '${hashes.size}'" }
        for (h in hashes) {
            require(h.size == 20) { "Hashes must be size 20. Size was '${h.size}'" }
            writeFully(h, 0, h.size)
        }
    }
}

data class ScrapeInfo(val seeders: Int, val completed: Int, val leechers: Int)
data class ScrapeResponsePacket(
    val action: UdpTrackerAction = UdpTrackerAction.SCRAPE,
    val transactionId: Int,
    val infos: List<ScrapeInfo>
) {
    companion object {
        fun fromPacket(packet: ByteReadPacket): ScrapeResponsePacket {
            with(packet) {
                val action = readInt()
                val transactionId = readInt()

                val infoSize = (remaining / 12).toInt()
                val infos = ArrayList<ScrapeInfo>(infoSize)
                while (canRead()) {
                    val seeders = readInt()
                    val completed = readInt()
                    val leechers = readInt()
                    infos.add(ScrapeInfo(seeders, completed, leechers))
                }
                return ScrapeResponsePacket(UdpTrackerAction.fromValue(action), transactionId, infos)
            }
        }
    }
}

data class ErrorResponsePacket(
    val action: UdpTrackerAction = UdpTrackerAction.ERROR,
    val transactionId: Int,
    val message: String
) {
    companion object {
        fun fromPacket(packet: ByteReadPacket): ErrorResponsePacket {
            with(packet) {
                val action = readInt()
                val transactionId = readInt()
                val message = readText()
                return ErrorResponsePacket(UdpTrackerAction.fromValue(action), transactionId, message)
            }
        }
    }
}

data class AuthenticatePacket(
    val username: String,
    val hash: ByteArray
) {
    fun toPacket() = buildPacket {
        val usr = if (username.length > 8) {
            username.substring(0..8)
        } else {
            username.padEnd(8, 0.toChar())
        }
        writeText(usr, 0, 8, Charsets.ISO_8859_1)
        writeFully(hash, 0, hash.size)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as AuthenticatePacket

        if (username != other.username) return false
        if (!hash.contentEquals(other.hash)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + hash.contentHashCode()
        return result
    }
}

