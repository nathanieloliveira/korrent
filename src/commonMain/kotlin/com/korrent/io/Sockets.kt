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

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.io.core.ByteReadPacket

expect abstract class SocketAddress

expect class InetAddress {
    fun getAddress(): ByteArray
    fun getHostAddress(): String
    override fun toString(): String
}

expect class InetSocketAddress(hostname: String, port: Int) : SocketAddress {
    override fun toString(): String
    fun getAddress(): InetAddress
}

expect class Datagram(packet: ByteReadPacket, address: SocketAddress) {
    val packet: ByteReadPacket
    val address: SocketAddress
}

expect interface UdpSocket {
    val incoming: ReceiveChannel<Datagram>
    val outgoing: SendChannel<Datagram>

    fun close()
}

expect fun getBroadcastAddresses(): List<InetAddress>

expect fun obtainUdpSocket(
    localAddress: SocketAddress? = null,
    reusePort: Boolean = false,
    reuseAddress: Boolean = true
): UdpSocket