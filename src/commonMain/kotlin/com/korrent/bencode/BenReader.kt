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

package com.korrent.bencode

import kotlinx.serialization.SharedImmutable

internal const val NULL = "null"

// special chars
internal const val STR_SEP    = ':'
internal const val BEGIN_LIST = 'l'
internal const val BEGIN_DICT = 'd'
internal const val BEGIN_INT  = 'i'
internal const val END_STRUCT = 'e'

internal const val INVALID = 0.toChar()

// token classes
internal const val TC_STR_SEP: Byte = 0
internal const val TC_BEGIN_STR: Byte = 1
internal const val TC_BEGIN_LIST: Byte = 2
internal const val TC_BEGIN_DICT: Byte = 3
internal const val TC_BEGIN_INT: Byte = 4
internal const val TC_END_STRUCT: Byte = 5
internal const val TC_BEGIN_OBJ: Byte = 6
internal const val TC_END_OBJ: Byte = 7
internal const val TC_INVALID: Byte = 8
internal const val TC_WS: Byte = 9
internal const val TC_OTHER: Byte = 10
internal const val TC_EOF: Byte = 11

// mapping from chars to token classes
private const val CTC_MAX = 0x7e

@SharedImmutable
internal val C2TC = ByteArray(CTC_MAX).apply {
    for (i in 0..0x20) {
        initC2TC(i, TC_INVALID)
    }

    for (c in '0'..'9') {
        initC2TC(c, TC_BEGIN_STR)
    }

    initC2TC(0x09, TC_WS)
    initC2TC(0x0a, TC_WS)
    initC2TC(0x0d, TC_WS)
    initC2TC(0x20, TC_WS)
    initC2TC(STR_SEP, TC_STR_SEP)
    initC2TC(BEGIN_LIST, TC_BEGIN_LIST)
    initC2TC(BEGIN_DICT, TC_BEGIN_DICT)
    initC2TC(BEGIN_INT, TC_BEGIN_INT)
    initC2TC(END_STRUCT, TC_END_STRUCT)
}

private fun ByteArray.initC2TC(c: Int, cl: Byte) {
    this[c] = cl
}

private fun ByteArray.initC2TC(c: Char, cl: Byte) {
    initC2TC(c.toInt(), cl)
}

private val Char.isNumeral: Boolean
    get() = this in '0'..'9'

internal fun charToTokenClass(c: Char) = if (c.toInt() < CTC_MAX) C2TC[c.toInt()] else TC_BEGIN_OBJ

class BenReader(val source: String) {

    val bytesTokenMap = mapOf(
        TC_STR_SEP to "TC_STR_SEP",
        TC_BEGIN_STR to "TC_BEGIN_STR",
        TC_BEGIN_LIST to "TC_BEGIN_LIST",
        TC_BEGIN_DICT to "TC_BEGIN_DICT",
        TC_BEGIN_INT to "TC_BEGIN_INT",
        TC_END_STRUCT to "TC_END_STRUCT",
        TC_BEGIN_OBJ to "TC_BEGIN_OBJ",
        TC_END_OBJ to "TC_END_OBJ",
        TC_INVALID to "TC_INVALID",
        TC_WS to "TC_WS",
        TC_OTHER to "TC_OTHER",
        TC_EOF to "TC_EOF"
    )

    var currentPosition: Int = 0 // position in source
    var tokenClass: Byte = TC_EOF

    public val isDone: Boolean
        get() = tokenClass == TC_EOF || currentPosition >= source.length

    public val canBeginValue: Boolean
        get() = when (tokenClass) {
            TC_BEGIN_INT, TC_BEGIN_STR, TC_OTHER -> true
            else -> false
        }

    // updated by nextToken
    private var tokenPosition: Int = 0

    // updated by nextString/nextLiteral
    private var offset = -1 // when offset >= 0 string is in source, otherwise in buf
    private var length = 0 // length of string
    private var buf = CharArray(64)

    internal inline fun requireTokenClass(expected: Byte, lazyErrorMsg: () -> String) {
        val equivalentBegin = setOf(TC_BEGIN_DICT, TC_BEGIN_OBJ)
        val equivalentEnd = setOf(TC_END_STRUCT, TC_END_OBJ)
        when (expected) {
            in equivalentBegin -> if (!equivalentBegin.contains(tokenClass)) fail(tokenPosition, lazyErrorMsg())
            in equivalentEnd -> if (!equivalentEnd.contains(tokenClass)) fail(tokenPosition, lazyErrorMsg())
            else -> if (tokenClass != expected) fail(tokenPosition, lazyErrorMsg())
        }
//        if (tokenClass != expected) fail(tokenPosition, lazyErrorMsg())
    }

    init {
        nextToken()
    }

    fun takeString(): String {
        if (tokenClass != TC_OTHER && tokenClass != TC_BEGIN_STR) fail(tokenPosition, "Expected string literal")
        val prevStr = if (offset < 0) {
            String(buf, 0, length)
        } else {
            source.substring(offset, offset + length)
        }
        nextToken()
        return prevStr
    }

    private fun append(c: Char) {
        if (length >= buf.size) buf = buf.copyOf(2 * buf.size)
        buf[length++] = c
    }

    private fun appendRange(source: String, fromIndex: Int, toIndex: Int) {
        val addLen = toIndex - fromIndex
        val oldLen = length
        val newLen = oldLen + addLen
        if (newLen > buf.size) buf = buf.copyOf(newLen.coerceAtLeast(2 * buf.size))
        for (i in 0..addLen) {
            buf[oldLen + i] = source[fromIndex + i]
        }
        length += addLen
    }

    fun nextToken() {
        val source = source
        var curPos = currentPosition
        val maxLen = source.length
        while (true) {
//            if (curPos == 0 && tokenClass == TC_EOF) {
//                tokenPosition = curPos
//                tokenClass = TC_BEGIN_OBJ
//                return
//            }
            if (curPos == maxLen && tokenClass == TC_END_STRUCT) {
                tokenPosition = curPos
                tokenClass = TC_END_OBJ
                currentPosition = curPos + 1
                return
            }
            if (curPos >= maxLen || tokenClass == TC_END_OBJ) {
                tokenPosition = curPos
                tokenClass = TC_EOF
                return
            }

            val ch = source[curPos]
            val tc = charToTokenClass(ch)
            when (tc) {
                TC_WS -> curPos++
                TC_BEGIN_INT -> {
                    nextInteger(source, curPos)
                    return
                }
                TC_BEGIN_STR -> {
                    nextString(source, curPos)
                    return
                }
                else -> {
                    tokenPosition = curPos
                    tokenClass = tc
                    currentPosition = curPos + 1
                    return
                }
            }
        }
    }

    private fun nextInteger(source: String, startPos: Int) {
        tokenPosition = startPos
        val c = source[startPos]
        require(c == BEGIN_INT, startPos) {"Unexpected beginning of integer"}
        val curPos = startPos + 1
        val endIndex = source.indexOf(END_STRUCT, startPos)
        require(endIndex != -1, startPos) {"Couldn't determine end of integer"}
        offset = curPos
        length = endIndex - curPos
        currentPosition = endIndex + 1
        tokenClass = TC_OTHER
    }

    private fun nextString(source: String, startPos: Int) {
        tokenPosition = startPos
        val c = source[startPos]
        require(c.isNumeral, startPos) {"Unexpected beginning of string"}
        val sepIndex = source.indexOf(STR_SEP, startPos)
        require(sepIndex != -1, startPos) {"Couldn't determine string size"}
        val curPos = sepIndex + 1
        val maxLen = source.substring(startPos, sepIndex).toInt()
        val remaining = source.length - curPos
        require(maxLen <= remaining, startPos) {"Not enough data: strLen: $maxLen remaining: $remaining"}
        // set string in source
        offset = curPos
        length = maxLen
        currentPosition = curPos + maxLen
        tokenClass = TC_OTHER
    }

    fun skipElement() {
        if (tokenClass !in setOf(TC_BEGIN_INT, TC_BEGIN_STR, TC_BEGIN_DICT, TC_BEGIN_LIST)) {
            nextToken()
            return
        }
        val tokenStack = mutableListOf<Byte>()
        do {
            when (tokenClass) {
                TC_BEGIN_LIST, TC_BEGIN_DICT -> tokenStack.add(tokenClass)
                TC_END_STRUCT -> {
                    tokenStack.removeAt(tokenStack.size - 1)
                }
            }
        } while (tokenStack.isNotEmpty())
    }

    override fun toString(): String {
        return "BenReader(source='$source', maxLen=${source.length} currentPosition=$currentPosition, tokenClass=$tokenClass, tokenPosition=$tokenPosition, offset=$offset, length=$length)"
    }

}

private fun rangeEquals(source: String, start: Int, length: Int, str: String): Boolean {
    val n = str.length
    if (length != n) return false
    for (i in 0 until n) if (source[start + i] != str[i]) return false
    return true
}


internal inline fun require(condition: Boolean, pos: Int, msg: () -> String) {
    if (!condition)
        fail(pos, msg())
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun fail(pos: Int, msg: String): Nothing {
    throw BenParsingException(pos, msg)
}