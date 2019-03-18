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

package com.korrent.test.io

import com.korrent.readTorrentFile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FilesTest {

    @Test
    fun testReadTorrentFile() {
        val path = "spider-man.torrent"
        val torrent = readTorrentFile(path)
        val name = "Spider-Man Into The Spider-Verse (2018) [WEBRip] [1080p] [YTS.AM]"
        print(torrent)
        assertTrue { torrent.info.pieces.isNotEmpty() }
        assertEquals(name, torrent.info.name)
//        assertEquals(name, torrent.info?.name)

    }

}