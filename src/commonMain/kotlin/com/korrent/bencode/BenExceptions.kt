package com.korrent.bencode

import kotlinx.serialization.SerializationException

sealed class BenException(message: String): SerializationException(message)

class BenParsingException(position: Int, message: String): BenException("Invalid Bencoding at $position: $message")