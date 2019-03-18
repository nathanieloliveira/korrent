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

import io.ktor.http.Parameters
import io.ktor.http.formUrlEncode
import kotlinx.serialization.Optional
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class Event {
    STARTED,
    STOPPED,
    COMPLETED;

    override fun toString(): String {
        return super.toString().toLowerCase()
    }
}

data class TrackerRequest(
    val infoHash: String,
    val peerId: String,
    val port: Int,
    val uploaded: Long,
    val downloaded: Long,
    val left: Long,
    val compact: Boolean,
    @Optional val noPeerId: Boolean? = null,
    @Optional val event: Event? = null,
    @Optional val ip: String? = null,
    @Optional val numWant: Int? = null,
    @Optional val key: String? = null,
    @Optional val trackerId: String? = null
) {
    fun urlEncode(): String {
        return Parameters.build {
            append("info_hash", infoHash)
            append("peer_id", peerId)
            append("port", port.toString())
            append("uploaded", uploaded.toString())
            append("downloaded", downloaded.toString())
            append("left", left.toString())
            append("compact", (if (compact) 1 else 0).toString())
            noPeerId?.run {
                append("no_peer_id", (if (noPeerId) 1 else 0).toString())
            }
            event?.run {
                append("event", event.toString())
            }
            ip?.run {
                append("ip", ip)
            }
            numWant?.run {
                append("numwant", numWant.toString())
            }
            key?.run {
                append("key", key)
            }
            trackerId?.run {
                append("trackerid", trackerId)
            }
        }.formUrlEncode()
    }
}

@Serializable
data class Peer(
    @SerialName("peer id") val peerId: String,
    val ip: String,
    val port: Int
)

sealed class TrackerResponse

@Serializable
data class ErrorTrackerResponse(
    @Optional @SerialName("failure reason") val failureReason: String? = null
) : TrackerResponse()

@Serializable
data class SuccessTrackerResponse(
    @Optional @SerialName("warning message") val warningMessage: String? = null,
    val interval: Int,
    @Optional @SerialName("min interval") val minInterval: Int = -1,
    @Optional @SerialName("tracker id") val trackerId: String? = null,
    val complete: Int,
    val incomplete: Int,
    val peers: List<Peer>
)