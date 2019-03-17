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

package com.korrent.model

import kotlinx.serialization.Optional
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FileInfo(
    val path: List<String>,
    val length: Long,
    @Optional val md5sum: String? = null
)

@Serializable
data class InfoDictionary(
    @Optional val name: String? = null,
    @SerialName("piece length") val pieceLength: Long,
    @Optional val private: Int = 0,

    // single file mode
    @Optional val length: Long = -1,
    @Optional val md5sum: String? = null,

    // multi file mode
    @Optional val files: List<FileInfo>? = null
) {
    lateinit var pieces: String
}

@Serializable
data class Torrent(
    val announce: String,
    @SerialName("announce-list") @Optional val announceList: List<List<String>>? = null,
    @SerialName("creation date") @Optional val creationDate: Long? = null,
    @SerialName("created by") @Optional val createdBy: String? = null,
    val info: InfoDictionary,
    @Optional val encoding: String? = null,
    @Optional val comment: String? = null
)