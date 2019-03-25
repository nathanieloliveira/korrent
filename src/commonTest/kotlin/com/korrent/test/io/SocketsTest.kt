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

package com.korrent.test.io

import com.korrent.http.parseHttpResponse
import com.korrent.http.toChannel
import com.korrent.io.*
import com.korrent.upnp.*
import io.ktor.util.InternalAPI
import kotlinx.coroutines.*
import kotlinx.coroutines.selects.select
import kotlinx.io.core.buildPacket
import kotlinx.io.core.writeText
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class SocketsTest {

    @Test
    fun testEcho() = kRunBlocking {
        val localAddress = InetSocketAddress("127.0.0.1", 3500)
        val server = obtainUdpSocket(localAddress, false)

        server.outgoing.send(Datagram(buildPacket { writeText("Hello World") }, localAddress))
        val received = server.incoming.receive()
        println("Received packet from ${received.address}. Message was ${received.packet.readText()}")
        assertEquals(received.packet.readText(), "Hello World")
        server.close()
    }

    @Test
    fun testBroadcast() = kRunBlocking {
        val broadcastAddress = getBroadcastAddresses()
        println(broadcastAddress)
        val socket = obtainUdpSocket(reusePort = false)

        for (a in broadcastAddress) {
            socket.outgoing.send(
                Datagram(
                    buildPacket { writeText("Hello World") },
                    InetSocketAddress(a.getHostAddress(), 1900)
                )
            )
        }
        socket.close()
    }

    @Test
    fun testMulticast() = kRunBlocking {
        println(MSEARCH_MSG_FMT)
        println("end")

        println(formatMSearchMsg(UPNP_MCAST_ADDR, SSDP_PORT, deviceTypes.last(), 1000))

        val socket = obtainUdpSocket()
        repeat(10) {
            socket.outgoing.send(
                Datagram(
                    buildPacket { writeText("Hello World") },
                    InetSocketAddress(UPNP_MCAST_ADDR, SSDP_PORT)
                )
            )
        }
        socket.close()
    }

    @InternalAPI
    @Test
    fun testDiscover() = kRunBlocking {
        val socket = obtainUdpSocket()

        val recvList = mutableListOf<Deferred<Datagram?>>()
        coroutineScope {
            for (type in deviceTypes) {
                val recv = async {
                    socket.outgoing.send(
                        Datagram(
                            buildPacket { writeText(formatMSearchMsg(UPNP_MCAST_ADDR, SSDP_PORT, type, 5)) },
                            InetSocketAddress(UPNP_MCAST_ADDR, SSDP_PORT)
                        )
                    )
                    select<Datagram?> {
                        socket.incoming.onReceive {
                            it
                        }
                        onTimeout(5000) { null }
                    }
                }
                recvList.add(recv)
            }
            val datagrams = recvList.awaitAll()
            datagrams.filterNotNull().forEach {
                val response = parseHttpResponse(it.packet.toChannel())
                println("Got response Status: ${response.status}")
                println(response.headers)
            }
        }
        socket.close()
    }

}
