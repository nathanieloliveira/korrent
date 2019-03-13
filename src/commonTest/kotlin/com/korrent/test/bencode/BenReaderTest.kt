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

import com.korrent.bencode.BenReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class BenReaderTest {

    @Test
    fun testReadHelloString() {
        val testString = "5:hello"
        assertEquals(BenReader(testString).takeString(), "hello")
    }

    @Test
    fun failOnNoSeparator() {
        assertFails {
            val testString = "5hello"
            BenReader(testString)
        }
    }

    @Test
    fun `fail on not enough data`() {
        assertFails {
            val testString = "10:hello"
            BenReader(testString)
        }
    }

    @Test
    fun testReadUrl() {
        val (original, encoded) = bencodeString("https://wiki.theory.org/index.php/BitTorrentSpecification#Bencoding")
        assertEquals(BenReader(encoded).takeString(), original)
    }

    @Test
    fun testReadMagnet() {
        val (original, encoded) = bencodeString("magnet:?xt.1=urn:sha1:YNCKHTQCWBTRNJIV4WNAE52SJUQCZO5C&xt.2=urn:sha1:TXGCZQTH26NL6OUQAJJPFALHG2LTGBC7")
        assertEquals(BenReader(encoded).takeString(), original)
    }

    @Test
    fun testReadIp() {
        val (original, encoded) = bencodeString("154.124.155.123")
        assertEquals(BenReader(encoded).takeString(), original)
    }

    @Test
    fun testReadLoremIpsum() {
        val testString = """
            Lorem ipsum dolor sit amet, consectetur adipiscing elit.
            Duis neque justo, dictum ut urna nec, dignissim fringilla risus.
            Proin non rutrum diam, eget luctus justo. Cras sed dolor in felis volutpat convallis. Nam consectetur leo nec tortor faucibus, et ultrices augue sagittis. Suspendisse vulputate lacinia magna at lacinia. Sed fringilla dictum quam eu interdum. Pellentesque in arcu vel purus malesuada fringilla. Vestibulum quis congue neque. Aliquam finibus sollicitudin auctor. Duis faucibus, lorem eget maximus pellentesque, nisl magna tincidunt nisl, congue sodales ipsum elit molestie leo. Praesent id massa ut lectus laoreet interdum. Nunc et dictum purus. Quisque venenatis sit amet massa in tincidunt. Duis a porta elit. Phasellus mollis sit amet ante ac elementum.
            Cras at bibendum lectus. Phasellus ac velit luctus, molestie lectus sit amet, imperdiet mauris. Suspendisse in condimentum dui. Sed placerat arcu non leo bibendum, sit amet viverra diam blandit. Maecenas eget urna dolor. Duis sollicitudin augue sit amet nunc feugiat feugiat. In rutrum felis vel turpis facilisis, ut porta elit viverra. Aenean massa augue, pulvinar id dui quis, euismod aliquam arcu. Aenean dapibus libero feugiat magna suscipit volutpat. Nunc at scelerisque nisl. Sed mollis ipsum viverra fringilla condimentum.
        """.trimIndent()
        assertEquals(BenReader("${testString.length}:$testString").takeString(), testString)
    }

    @Test
    fun testReadInt() {
        val (original, encoded) = bencodeInt(Int.MAX_VALUE)
        assertEquals(BenReader(encoded).takeString().toInt(), original)
    }

    @Test
    fun testReadNegativeInt() {
        val (original, encoded) = bencodeInt(Int.MIN_VALUE)
        assertEquals(BenReader(encoded).takeString().toInt(), original)
    }

    @Test
    fun testReadLong() {
        val (original, encoded) = bencodeLong(Long.MAX_VALUE)
        assertEquals(BenReader(encoded).takeString().toLong(), original)
    }

    @Test
    fun testReadNegativeLong() {
        val (original, encoded) = bencodeLong(Long.MIN_VALUE)
        assertEquals(BenReader(encoded).takeString().toLong(), original)
    }

    @Test
    fun testReadZeroInt() {
        val (original, encoded) = bencodeInt(0)
        assertEquals(BenReader(encoded).takeString().toInt(), original)
    }

    @Test
    fun failOnNoEndInt() {
        assertFails {
            BenReader("i12312")
        }
    }

    private data class BencodedString(val original: String, val encoded: String)

    private fun bencodeString(string: String): BencodedString {
        return BencodedString(string, "${string.length}:$string")
    }

    private data class BencodedInt(val original: Int, val encoded: String)

    private fun bencodeInt(i: Int) = BencodedInt(i, "i${i}e")

    private data class BencodedLong(val original: Long, val encoded: String)

    private fun bencodeLong(l: Long) = BencodedLong(l, "i${l}e")
}