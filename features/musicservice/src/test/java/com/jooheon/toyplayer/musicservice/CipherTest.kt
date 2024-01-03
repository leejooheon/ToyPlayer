package com.jooheon.toyplayer.musicservice

import com.jooheon.toyplayer.features.musicservice.playback.PlaybackCacheManager.Companion.SECRET_IV
import com.jooheon.toyplayer.features.musicservice.playback.PlaybackCacheManager.Companion.SECRET_KEY
import com.jooheon.toyplayer.testing.MainDispatcherRule
import org.junit.Rule
import org.junit.Test
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class CipherTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    @Test
    fun byteArrayConvertTest() {
        val charArray = SECRET_KEY.map { it }
        val byteArray = charArray.map { it.code.toByte() }.toByteArray()

        val test2 = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")
        val directConvert = SECRET_KEY.toByteArray()

        assert(byteArray.toString() == directConvert.toString())
    }

    @Test
    fun encryptDecryptTest() {
        val buffer = byteArrayOf(73, 68, 51, 4, 0, 0, 0, 9, 18, 115, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        val offset = 10
        val length = 1

        val encryptedByte = encrypt(buffer.clone(), offset, length)
        val decryptedByte = decrypt(encryptedByte.clone(), offset, length)

        assert(buffer.toList() == decryptedByte.toList())
    }

    private fun decrypt(byteArray: ByteArray, offset: Int, length: Int): ByteArray {
        val key = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")
        val iv = IvParameterSpec(SECRET_IV.toByteArray())

        val cipher = Cipher.getInstance("AES/CFB8/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, iv)

        val target = byteArray.copyOfRange(offset, offset + length)
        val decrypted = cipher.doFinal(target)

        decrypted.copyInto(
            destination = byteArray,
            destinationOffset = offset
        )

        return byteArray
    }

    private fun encrypt(byteArray: ByteArray, offset: Int, length: Int): ByteArray {
        val key = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")
        val iv = IvParameterSpec(SECRET_IV.toByteArray())

        val cipher = Cipher.getInstance("AES/CFB8/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key,iv)

        val target = byteArray.copyOfRange(offset, offset + length)
        val encrypted = cipher.doFinal(target)

        encrypted.copyInto(
            destination = byteArray,
            destinationOffset = offset
        )

        return byteArray
    }
}