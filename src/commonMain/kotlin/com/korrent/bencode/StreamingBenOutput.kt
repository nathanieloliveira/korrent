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

import kotlinx.serialization.CompositeEncoder
import kotlinx.serialization.ElementValueEncoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.internal.EnumDescriptor

internal class StreamingBenOutput(
    private val composer: Composer,
    override val ben: Ben,
    private val mode: WriteMode,
    private val modeReuseCache: Array<BenOutput?>
) : BenOutput, ElementValueEncoder() {
    internal constructor(
        ben: Ben,
        mode: WriteMode,
        modeReuseCache: Array<BenOutput?>
    ) : this(Composer(), ben, mode, modeReuseCache)

    init {
        context = ben.context
        val i = mode.ordinal
        if (modeReuseCache[i] !== null || modeReuseCache[i] !== this) {
            modeReuseCache[i] = this
        }
    }

    fun getResult(): String {
        return composer.toString()
    }

    override fun shouldEncodeElementDefault(desc: SerialDescriptor, index: Int): Boolean {
        return ben.encodeDefaults
    }

    override fun beginStructure(desc: SerialDescriptor, vararg typeParams: KSerializer<*>): CompositeEncoder {
        val newMode = switchMode(desc, typeParams)
        if (newMode.begin != INVALID) {
            composer.print(newMode.begin)
        }

        return if (mode == newMode) {
            this
        } else {
            modeReuseCache[newMode.ordinal] ?: StreamingBenOutput(composer, ben, newMode, modeReuseCache)
        }
    }

    override fun endStructure(desc: SerialDescriptor) {
        if (mode.end != INVALID) {
            composer.print(mode.end)
        }
    }

    override fun encodeElement(desc: SerialDescriptor, index: Int): Boolean {
        when (mode) {
            WriteMode.LIST -> {
            }
            WriteMode.OBJ -> {
                composer.print(desc.getElementName(index))
            }
            WriteMode.POLY_OBJ -> {
            }
            WriteMode.INT -> {
            }
            WriteMode.DICT -> {
            }
        }
        return true
    }

    override fun encodeNull() {
        composer.print(NULL)
    }

    override fun encodeBoolean(value: Boolean) {
        composer.print(value)
    }

    override fun encodeByte(value: Byte) {
        composer.print(value)
    }

    override fun encodeShort(value: Short) {
        composer.print(value)
    }

    override fun encodeInt(value: Int) {
        composer.print(value)
    }

    override fun encodeLong(value: Long) {
        composer.print(value)
    }

    override fun encodeFloat(value: Float) {
        throw BenEncodingException("Cannot encode Float values")
    }

    override fun encodeDouble(value: Double) {
        throw BenEncodingException("Cannot encode Float values")
    }

    override fun encodeChar(value: Char) {
        composer.print(value.toString())
    }

    override fun encodeString(value: String) {
        composer.print(value)
    }

    override fun encodeEnum(enumDescription: EnumDescriptor, ordinal: Int) {
        encodeString(enumDescription.getElementName(ordinal))
    }

    internal class Composer {
        /**
         * This Composer is not ideal because of memory allocations of output string.
         * should be upgraded to an OutputStream when kotlinx.serialization library
         * starts implementing kotlinx.io interfaces.
         *
         * Was chosen to be this way to not impose dependency on JVM StringBuilder.
         */

        private var output: String = ""

//        var writingFirst = true
//            private set

        fun print(v: Char) {
            output += v
        }

        fun print(v: String) {
            output += "${v.length}:$v"
        }

        fun print(v: Boolean) {
            print(v.toString())
        }

        fun print(v: Long) {
            output += "$BEGIN_INT$v$END_STRUCT"
        }

        fun print(v: Byte) {
            print(v.toLong())
        }

        fun print(v: Int) {
            print(v.toLong())
        }

        fun print(v: Short) {
            print(v.toLong())
        }

        override fun toString(): String {
            return output
        }
    }
}