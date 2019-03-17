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
import kotlinx.serialization.*
import kotlin.test.Test
import kotlin.test.assertEquals

@Serializable
data class Simple(val a: String, val b: Int)

@Serializable
data class Nested(val simple: Simple)

@Serializable
data class ObjWithMap(val map: Map<String, String>)

@Serializable
data class ObjWithList(val list: List<String>)

@Serializable
data class ObjWithListAndMap(val list: List<String>, val map: Map<String, String>)

@Serializable
data class ObjWithOptional(@Optional val a: String? = null)

@Serializable
data class ObjWithNestedLists(val list: List<List<String>>)

@Serializable
data class ObjWithListInt(val list: List<Int>)

class StreamingBenInputTest {

    @Test
    fun testObjWithOptionalNull() {
        val testObj = ObjWithOptional()
        val testString = "de"
        val des = Ben.plain.parse(ObjWithOptional.serializer(), testString)
        assertEquals(testObj, des)
    }

    @Test
    fun testObjWithOptionalSet() {
        val testObj = ObjWithOptional("test")
        val testString = "d1:a4:teste"
        val des = Ben.plain.parse(ObjWithOptional.serializer(), testString)
        assertEquals(testObj, des)
    }

    @ImplicitReflectionSerializer
    @Test
    fun testEmptyMap() {
        val testObj = mapOf<String, String>()
        val testString = "de"
        val des = Ben.plain.parseMap<String, String>(testString)
        assertEquals(testObj, des)
    }

    @ImplicitReflectionSerializer
    @Test
    fun testEmptyList() {
        val testObj = listOf<String>()
        val testString = "le"
        val des = Ben.plain.parseList<String>(testString)
        assertEquals(testObj, des)
    }

    @ImplicitReflectionSerializer
    @Test
    fun testDeserializeMap() {
        val testObj = mapOf("a" to "Hello", "b" to "World")
        val testString = "d1:a5:Hello1:b5:Worlde"
        val des = Ben.plain.parseMap<String, String>(testString)
        assertEquals(testObj, des)
    }

    @Test
    fun testDeserializeSimpleClass() {
        val testObj = Simple("Hello", 10)
        val testString = "d1:a5:Hello1:bi10ee"
        val des = Ben.plain.parse(Simple.serializer(), testString)
        assertEquals(testObj, des)
    }

    @Test
    fun testDeserializeNested() {
        val testObj = Nested(Simple("Hello", 10))
        val testString = "d6:simpled1:a5:Hello1:bi10eee"
        val des = Ben.plain.parse(Nested.serializer(), testString)
        assertEquals(testObj, des)
    }

    @Test
    fun testDeserializeObjWithMap() {
        val testObj = ObjWithMap(mapOf("a" to "Hello", "b" to "World"))
        val testString = "d3:mapd1:a5:Hello1:b5:Worldee"
        val des = Ben.plain.parse(ObjWithMap.serializer(), testString)
        assertEquals(testObj, des)
    }

    @Test
    fun testDeserializeObjWithList() {
        val testObj = ObjWithList(listOf("Hello", "World"))
        val testString = "d4:listl5:Hello5:Worldee"
        val des = Ben.plain.parse(ObjWithList.serializer(), testString)
        assertEquals(testObj, des)
    }

    @Test
    fun testDeserializeObjWithListAndMap() {
        val testObj = ObjWithListAndMap(
            listOf("Hello", "World"),
            mapOf("a" to "Hello", "b" to "World")
        )
        val testString = "d4:listl5:Hello5:Worlde3:mapd1:a5:Hello1:b5:Worldee"
        val des = Ben.plain.parse(ObjWithListAndMap.serializer(), testString)
        assertEquals(testObj, des)
    }

    @Test
    fun testDeserializeObjWithNestedLists() {
        val obj = ObjWithNestedLists(listOf(listOf("a", "b"), listOf("c", "d")))
        val str = "d4:listll1:a1:bel1:c1:deee"
        val des = Ben.plain.parse(ObjWithNestedLists.serializer(), str)
        assertEquals(obj, des)
    }

    @Test
    fun testDeserializeListSimple() {
        val obj = NestedList(listOf(Simple("hello", 10), Simple("hello", 11)))
        val str = "d4:listld1:a5:hello1:bi10eed1:a5:hello1:bi11eee"
        val des = Ben.plain.parse(NestedList.serializer(), str)
        assertEquals(obj, des)
    }

    @Test
    fun testDeserializeListInt() {
        val obj = ObjWithListInt(listOf(1, 2, 3, 4))
        val str = "d4:listli1ei2ei3ei4eee"
        val des = Ben.plain.parse(ObjWithListInt.serializer(), str)
        assertEquals(obj, des)
    }
}