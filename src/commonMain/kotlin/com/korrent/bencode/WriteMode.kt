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

import kotlinx.serialization.*
import kotlin.jvm.JvmField

internal enum class WriteMode(@JvmField val begin: Char, @JvmField val end: Char) {
    OBJ(BEGIN_DICT, END_STRUCT),
    POLY_OBJ(BEGIN_DICT, END_STRUCT),
    INT(BEGIN_INT, END_STRUCT),
    LIST(BEGIN_LIST, END_STRUCT),
    DICT(BEGIN_DICT, END_STRUCT);

    val beginTc: Byte = charToTokenClass(begin)
    val endTc: Byte = charToTokenClass(end)
}

internal fun switchMode(desc: SerialDescriptor, typeParams: Array<out KSerializer<*>>): WriteMode =
    when (desc.kind) {
        UnionKind.POLYMORPHIC -> WriteMode.POLY_OBJ
        StructureKind.LIST -> WriteMode.LIST
        StructureKind.MAP -> {
            val keyKind = typeParams[0].descriptor.kind
            if (keyKind == PrimitiveKind.STRING || keyKind == UnionKind.ENUM_KIND) {
                WriteMode.DICT
            } else {
                throw BenSpecificationException("cannot serialize $keyKind as Map Key")
            }
        }

        UnionKind.OBJECT,
        UnionKind.SEALED,

        PrimitiveKind.UNIT,
        StructureKind.CLASS -> WriteMode.OBJ

        PrimitiveKind.INT,
        PrimitiveKind.BYTE,
        PrimitiveKind.SHORT,
        PrimitiveKind.LONG -> WriteMode.INT

        PrimitiveKind.BOOLEAN,
        PrimitiveKind.STRING,
        UnionKind.ENUM_KIND,// -> WriteMode.OBJ
//                maybe /\ should be string mode???

        PrimitiveKind.FLOAT,
        PrimitiveKind.DOUBLE,
        PrimitiveKind.CHAR -> throw BenSpecificationException("cannot serialize ${desc.kind}")
    }