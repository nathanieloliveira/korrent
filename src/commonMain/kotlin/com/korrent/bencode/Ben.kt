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
import kotlinx.serialization.context.SerialContext
import kotlinx.serialization.context.SerialModule

class Ben(
    val updateMode: UpdateMode = UpdateMode.OVERWRITE,
    val encodeDefaults: Boolean = true
) : AbstractSerialFormat(), StringFormat {

    override fun <T> parse(deserializer: DeserializationStrategy<T>, string: String): T {
        val reader = BenReader(string)
        val input = StreamingBenInput(this, WriteMode.OBJ, reader)
        val result = input.decode(deserializer)
        if (!reader.isDone) {
            error("Reader has not consumed the whole input: $reader")
        }
        return result
    }

    override fun <T> stringify(serializer: SerializationStrategy<T>, obj: T): String {
        val encoder = StreamingBenOutput(
            this,
            WriteMode.OBJ,
            arrayOfNulls(WriteMode.values().size)
        )
        encoder.encode(serializer, obj)
        return encoder.getResult()
    }

    companion object : StringFormat {
        val plain = Ben()
        override val context: SerialContext
            get() = plain.context

        override fun install(module: SerialModule) = plain.install(module)

        override fun <T> parse(deserializer: DeserializationStrategy<T>, string: String): T =
            plain.parse(deserializer, string)

        override fun <T> stringify(serializer: SerializationStrategy<T>, obj: T): String =
            plain.stringify(serializer, obj)

    }
}