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

package com.korrent.upnp

import com.korrent.io.InetSocketAddress
import com.korrent.io.formatString
import com.korrent.io.obtainUdpSocket

val deviceTypes = listOf(
    "urn:schemas-upnp-org:device:InternetGatewayDevice:1",
    "urn:schemas-upnp-org:service:WANIPConnection:1",
    "urn:schemas-upnp-org:service:WANPPPConnection:1",
    "upnp:rootdevice"
)

const val UPNP_MCAST_ADDR = "239.255.255.250"
const val SSDP_PORT = 1900

val MSEARCH_MSG_FMT =
    """M-SEARCH * HTTP/1.1
        |Host: %s:%d
        |ST: %s
        |Man: "ssdp:discover"
        |MX: %d
        |
        |""".trimMargin().replace("\n", "\r\n")

fun formatMSearchMsg(host: String, port: Int, deviceType: String, mx: Int): String {
    return formatString(MSEARCH_MSG_FMT, host, port, deviceType, mx)
}

class UpnpManager {

    fun discover(multicastInterface: String, localPort: Int, useIpv6: Boolean, timeout: Int) {

    }

    private fun discoverDevices(
        deviceList: List<String>,
        multicastInterface: String,
        localPort: Int = 0,
        useIpv6: Boolean,
        timeout: Int
    ) {
        InetSocketAddress(multicastInterface, localPort)
        val socket = obtainUdpSocket()


    }

}