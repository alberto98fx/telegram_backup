/* Telegram_Backup
 * Copyright (C) 2016 Fabian Schlenz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package de.fabianonline.telegram_backup.mediafilemanager

import de.fabianonline.telegram_backup.UserManager
import de.fabianonline.telegram_backup.Config

import com.github.badoualy.telegram.tl.api.*
import com.github.badoualy.telegram.tl.exception.RpcErrorException
import de.fabianonline.telegram_backup.Settings

import java.io.IOException
import java.io.File
import java.util.concurrent.TimeoutException

abstract class AbstractMediaFileManager(protected var message: TLMessage, protected var user: UserManager, val file_base: String) {
	open var isEmpty = false
	abstract val size: Int
	abstract val extension: String

	open val downloaded: Boolean
		get() = File(targetPathAndFilename).isFile()

	val downloading: Boolean
		get() = File("${targetPathAndFilename}.downloading").isFile()

	open val targetPath: String
		get() {
			val path = file_base + Config.FILE_FILES_BASE + File.separatorChar
			File(path).mkdirs()
			return path
		}

	open val targetFilename: String
		get() {
			val message_id = message.getId()
			var to = message.getToId()
			if (to is TLPeerChannel) {
				val channel_id = to.getChannelId()
				return "channel_${channel_id}_${message_id}.$extension"
			} else return "${message_id}.$extension"
		}

	open val targetPathAndFilename: String
		get() = targetPath + targetFilename

	abstract val letter: String
	abstract val name: String
	abstract val description: String
	@Throws(RpcErrorException::class, IOException::class, TimeoutException::class)
	abstract fun download(): Boolean

	protected fun extensionFromMimetype(mime: String): String {
		when (mime) {
			"text/plain" -> return "txt"
		}

		val i = mime.lastIndexOf('/')
		val ext = mime.substring(i + 1).toLowerCase()

		return if (ext === "unknown") "dat" else ext

	}

	companion object {
		fun throwUnexpectedObjectError(o: Any) {
			throw RuntimeException("Unexpected " + o.javaClass.getName())
		}
	}
}
