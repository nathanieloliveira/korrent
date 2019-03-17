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
import kotlinx.serialization.internal.EnumDescriptor

internal class StreamingBenInput(
    override val ben: Ben,
    private val mode: WriteMode,
    private val reader: BenReader
) : BenInput, ElementValueDecoder() {

    private var currentIndex = -1
    private var entryIndex = 0

    init {
        context = ben.context
    }

    override val updateMode: UpdateMode
        get() = ben.updateMode

    override fun beginStructure(desc: SerialDescriptor, vararg typeParams: KSerializer<*>): CompositeDecoder {
        val newMode = switchMode(desc, typeParams)
        if (newMode == WriteMode.POLY_OBJ) {
            // if poly object skip check
            reader.nextToken()
        } else if (newMode.begin != INVALID) {
            reader.requireTokenClass(newMode.beginTc) { "Expected '${newMode.begin}', kind:' ${desc.kind}'" }
            reader.nextToken()
        }
        return when (newMode) {
            WriteMode.INT, WriteMode.LIST, WriteMode.DICT -> StreamingBenInput(
                ben, newMode, reader
            )
            else -> if (mode == newMode) this else StreamingBenInput(ben, newMode, reader)
        }
    }

    override fun endStructure(desc: SerialDescriptor) {
        if (mode.end != INVALID) {
            reader.requireTokenClass(mode.endTc) { "Expected '${mode.end}'" }
            reader.nextToken()
        }
    }

    override fun decodeNotNullMark(): Boolean = true

    override fun decodeNull(): Nothing? {
        throw BenParsingException(reader.currentPosition, "Can't decode null values")
    }

    override fun decodeElementIndex(desc: SerialDescriptor): Int {
        while (true) {
//            if (reader.tokenClass in setOf(TC_END_STRUCT)) reader.nextToken()
            when (mode) {
                WriteMode.LIST -> {
                    return if (!reader.canBeginValue) {
                        val nestedList =
                            (desc.elementsCount >= 1/* && desc.getElementDescriptor(0).kind is StructureKind.LIST*/)
                        if (nestedList) {
                            if (reader.tokenClass !in setOf(TC_BEGIN_LIST, TC_BEGIN_DICT)) {
//                                reader.nextToken()
                                CompositeDecoder.READ_DONE
                            } else {
                                ++currentIndex
                            }
                        } else {
                            CompositeDecoder.READ_DONE
                        }
                    } else {
                        ++currentIndex
                    }
                }
                WriteMode.DICT -> {
//                    if (currentIndex >= 2) currentIndex = 0
                    return if (!reader.canBeginValue) CompositeDecoder.READ_DONE else ++currentIndex
                }
                WriteMode.INT -> {
                    return if (!reader.canBeginValue) CompositeDecoder.READ_DONE else ++currentIndex
                }
                WriteMode.POLY_OBJ -> {
                    return when (entryIndex++) {
                        0 -> 0
                        1 -> 1
                        else -> {
                            entryIndex = 0
                            CompositeDecoder.READ_DONE
                        }
                    }
                }
                WriteMode.OBJ -> {
                    if (!reader.canBeginValue) return CompositeDecoder.READ_DONE
                    val key = reader.takeString()
                    val index = desc.getElementIndex(key)
                    if (index != CompositeDecoder.UNKNOWN_NAME) {
                        return index
                    } else reader.skipElement()
                }
            }
        }
    }

    override fun decodeBoolean(): Boolean = reader.takeString().toBoolean()
    override fun decodeByte(): Byte = reader.takeString().toByte()
    override fun decodeShort(): Short = reader.takeString().toShort()
    override fun decodeInt(): Int = reader.takeString().toInt()
    override fun decodeLong(): Long = reader.takeString().toLong()
    override fun decodeFloat(): Float = reader.takeString().toFloat()
    override fun decodeDouble(): Double = reader.takeString().toDouble()
    override fun decodeChar(): Char = reader.takeString().single()
    override fun decodeString(): String = reader.takeString()
    override fun decodeEnum(enumDescription: EnumDescriptor): Int = enumDescription.getElementIndex(reader.takeString())
}