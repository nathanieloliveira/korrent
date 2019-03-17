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

package com.korrent.test.bencode

import com.korrent.bencode.Ben
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.stringify
import kotlin.test.Test
import kotlin.test.assertEquals

@Serializable
data class NestedList(val list: List<Simple>)

class StreamingBenOutputTest {

    @ImplicitReflectionSerializer
    @Test
    fun testEncodeDict() {
        val obj = mapOf("a" to "hello", "b" to "world")
        val str = "d1:a5:hello1:b5:worlde"
        val enc = Ben.plain.stringify(obj)
        assertEquals(str, enc)
    }

    @ImplicitReflectionSerializer
    @Test
    fun testEncodeList() {
        val obj = listOf("hello", "world")
        val str = "l5:hello5:worlde"
        val enc = Ben.plain.stringify(obj)
        assertEquals(str, enc)
    }

    @ImplicitReflectionSerializer
    @Test
    fun testEncodeIntList() {
        val obj = listOf(1, 2, 3)
        val str = "li1ei2ei3ee"
        val enc = Ben.plain.stringify(obj)
        assertEquals(str, enc)
    }

    @Test
    fun testEncodeSimple() {
        val obj = Simple("hello", 10)
        val str = "d1:a5:hello1:bi10ee"
        val enc = Ben.plain.stringify(Simple.serializer(), obj)
        assertEquals(str, enc)
    }

    @Test
    fun testEncodeNested() {
        val obj = Nested(Simple("hello", 10))
        val str = "d6:simpled1:a5:hello1:bi10eee"
        val enc = Ben.plain.stringify(Nested.serializer(), obj)
        assertEquals(str, enc)
    }

}