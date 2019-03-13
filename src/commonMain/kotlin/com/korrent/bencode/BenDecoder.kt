package com.korrent.bencode

import kotlinx.io.Reader
import kotlinx.io.StringReader
import kotlinx.serialization.*
import kotlinx.serialization.context.SerialContext
import kotlinx.serialization.internal.EnumDescriptor
import kotlinx.serialization.json.Json

class BenDecoder(private val ben: Ben, private val string: String) : Decoder {
    override val context: SerialContext
        get() = ben.context
    override val updateMode: UpdateMode
        get() = UpdateMode.BANNED

    private val reader = StringReader(string)

    override fun beginStructure(desc: SerialDescriptor, vararg typeParams: KSerializer<*>): CompositeDecoder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun decodeBoolean(): Boolean {
        TODO()
    }

    override fun decodeByte(): Byte {
        TODO()
    }

    override fun decodeChar(): Char {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun decodeDouble(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun decodeEnum(enumDescription: EnumDescriptor): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun decodeFloat(): Float {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun decodeInt(): Int {
        TODO()
    }

    override fun decodeLong(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun decodeNotNullMark(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun decodeNull(): Nothing? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun decodeShort(): Short {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun decodeString(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun decodeUnit() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}