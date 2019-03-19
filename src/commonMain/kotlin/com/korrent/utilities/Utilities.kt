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

import com.korrent.io.getCurrentTimeMillis
import kotlinx.io.ByteArrayOutputStream
import kotlin.random.Random

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

private val bytesToHexMap = Array(256) { "" }.apply {
    for (b in 0 until 256) {
        this[b] = byteToHexString(b.toByte())
    }
}

private val hexStringToByteMap = mutableMapOf<String, Byte>().apply {
    for (b in 0 until 256) {
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

@ExperimentalUnsignedTypes
fun ByteArray.toHexString(): String {
    val builder = StringBuilder()
    for (b in this) {
        builder.append(b.toHexString())
    }
    return builder.toString()
}

@ExperimentalUnsignedTypes
fun byteArrayFromHexString(hexString: String): ByteArray {
    require(hexString.length % 2 == 0) { "String length was not even (length='${hexString.length}')" }
    val buffer = ByteArrayOutputStream()
    for (i in 0 until hexString.length step 2) {
        val subs = hexString.substring(i, i + 2).toUpperCase()
        val byte = hexStringToByteMap.getValue(subs).toUByte()
        buffer.write(byte.toInt())
    }
    return buffer.toByteArray()
}

fun String.fromUrlEncodedByteArray(): ByteArray {
    val os = ByteArrayOutputStream()
    var currentIndex = 0
    while (currentIndex < length) {
        val char = this[currentIndex]
        currentIndex += when (char) {
            '%' -> {
                val byte = hexStringToByteMap.getValue(substring(currentIndex + 1, currentIndex + 3))
                os.write(byte.toInt())
                3
            }
            else -> {
                os.write(char.toInt())
                1
            }
        }
    }
    return os.toByteArray()
}

fun generatePeerId(): ByteArray {
    val array = ByteArray(20)
    val random = Random(getCurrentTimeMillis())
    random.nextBytes(array)
    return array
}