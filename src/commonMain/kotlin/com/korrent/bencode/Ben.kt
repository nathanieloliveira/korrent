package com.korrent.bencode

import kotlinx.serialization.AbstractSerialFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat

class Ben : AbstractSerialFormat(), StringFormat {

    override fun <T> parse(deserializer: DeserializationStrategy<T>, string: String): T {

        TODO()
    }

    override fun <T> stringify(serializer: SerializationStrategy<T>, obj: T): String {
        TODO()
    }
}