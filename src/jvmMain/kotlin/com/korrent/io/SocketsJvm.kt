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

import io.ktor.application.call
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.net.InetAddress
import java.net.NetworkInterface

actual typealias SocketAddress = java.net.SocketAddress

actual typealias InetAddress = java.net.InetAddress

actual typealias InetSocketAddress = java.net.InetSocketAddress

actual typealias Datagram = io.ktor.network.sockets.Datagram

actual typealias UdpSocket = io.ktor.network.sockets.BoundDatagramSocket

@ExperimentalCoroutinesApi
@KtorExperimentalAPI
actual fun obtainUdpSocket(localAddress: SocketAddress?, reusePort: Boolean, reuseAddress: Boolean): UdpSocket {
    val configure = aSocket(ActorSelectorManager(Dispatchers.IO)).udp().configure {
        this.reuseAddress = reuseAddress
        this.reusePort = reusePort
    }
    return configure.bind(localAddress)

    /*kRunBlocking {
        server.outgoing.send(getEmptyDatagram())
    }

        kotlinx.coroutines.kRunBlocking {
            val datagram = select<Datagram?> {
                server.incoming.onReceiveOrNull
                onTimeout(1000) { null }
            }
        }
    }*/
}

actual fun getBroadcastAddresses(): List<InetAddress> {
    val broadcastAddresses = mutableListOf<InetAddress>()
    NetworkInterface.getNetworkInterfaces().asSequence()
        .filter { !it.isLoopback && it.isUp }
        .forEach { iface ->
            iface.interfaceAddresses.filter { it.broadcast != null }.forEach {
                broadcastAddresses.add(it.broadcast)
            }
        }
    return broadcastAddresses
}

fun getAddressInfo() {
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/") {
                call.respondText { "Hello World" }
            }
        }
    }

}