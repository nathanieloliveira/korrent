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

package com.korrent.utilities

import kotlinx.io.StringReader

private val bytesToHexChar = CharArray(16).apply {
    this[0] = '0'
    this[1] = '1'
    this[2] = '2'
    this[3] = '3'
    this[4] = '4'
    this[5] = '5'
    this[6] = '6'
    this[7] = '7'
    this[8] = '8'
    this[9] = '9'
    this[10] = 'A'
    this[11] = 'B'
    this[12] = 'C'
    this[13] = 'D'
    this[14] = 'E'
    this[15] = 'F'
}

private fun byteToHexString(byte: Byte): String {
    val b = byte.toInt()
    val flagMs = 0xf0
    val flagLs = 0x0f
    val ms = (b and flagMs) ushr 4
    val ls = b and flagLs
    return "${bytesToHexChar[ms]}${bytesToHexChar[ls]}"
}

private val bytesToHexMap = Array(255) { "" }.apply {
    for (b in 0 until 255) {
        this[b] = byteToHexString(b.toByte())
    }
}

private val hexStringToByteMap = mutableMapOf<String, Byte>().apply {
    for (b in 0 until 255) {
        put(byteToHexString(b.toByte()), b.toByte())
    }
}.toMap()

@ExperimentalUnsignedTypes
fun Byte.toHexString(): String {
    return bytesToHexMap[this.toUByte().toInt()]
}

@ExperimentalUnsignedTypes
fun ByteArray.toUrlEncodedString(): String {
    val builder = StringBuilder()
    for (b in this) {
        val c = b.toChar()
        when (c) {
            in '0'..'9',
            in 'a'..'z',
            in 'A'..'Z',
            '.', '-', '_', '~' -> builder.append(c)
            else -> {
                builder.append('%').append(b.toHexString())
            }
        }
    }
    return builder.toString()
}

fun String.fromUrlEncodedByteArray(): ByteArray {
    val reader = StringReader(this)
    TODO()
}