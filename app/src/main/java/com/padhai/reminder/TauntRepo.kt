package com.padhai.reminder

import android.content.Context
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.util.Random

/**
 * Picks one random line from the big taunt files.
 * Distribution: 0-55 -> Hinglish (56%), 56-79 -> Hindi Devanagari (24%), 80-99 -> English (20%)
 * Uses random seek so the large asset files are never fully loaded into RAM.
 */
object TauntRepo {
    private val rnd = Random()
    private const val HINGLISH = "taunts_hinglish.txt"
    private const val HINDI = "taunts_hindi.txt"
    private const val ENGLISH = "taunts_english.txt"
    private const val FALLBACK = "Padhai karo yaar. Abhi. Isi waqt."

    fun next(c: Context): String {
        val r = rnd.nextInt(100)
        val file = when {
            r < 56 -> HINGLISH
            r < 80 -> HINDI
            else -> ENGLISH
        }
        return randomLine(c, file) ?: FALLBACK
    }

    private fun randomLine(c: Context, name: String): String? {
        return try {
            c.assets.openFd(name).use { afd ->
                val ch = FileInputStream(afd.fileDescriptor).channel
                // 2048 bytes: large enough for Devanagari lines (3 bytes/char) and any script
                val window = 2048
                val maxOff = (afd.length - window).coerceAtLeast(0L)
                val off = (rnd.nextDouble() * maxOff).toLong()
                ch.position(afd.startOffset + off)
                val bb = ByteBuffer.allocate(window)
                ch.read(bb)
                val a = bb.array()
                val n = bb.position()
                val nl = '\n'.code.toByte()
                var s = 0
                while (s < n && a[s] != nl) s++
                s++ // skip to start of the next full line
                var e = s
                while (e < n && a[e] != nl) e++
                if (e > s) String(a, s, e - s, Charsets.UTF_8) else null
            }
        } catch (ex: Exception) {
            null
        }
    }
}
