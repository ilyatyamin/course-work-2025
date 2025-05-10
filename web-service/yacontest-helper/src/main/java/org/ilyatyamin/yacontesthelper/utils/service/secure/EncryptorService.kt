package org.ilyatyamin.yacontesthelper.utils.service.secure

import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@Service
class EncryptorService(env: Environment) {
    private val key: String = env["ENCRYPTOR_KEY"] ?: throw Exception("ENCRYPTOR_KEY not set")

    companion object {
        private val log = LoggerFactory.getLogger(EncryptorService::class.java)
    }

    fun encrypt(text: String): String {
        try {
            val key = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "AES")
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val encryptedBytes = cipher.doFinal(text.toByteArray(Charsets.UTF_8))
            return Base64.getEncoder().encodeToString(encryptedBytes)
        } catch (e: Exception) {
            log.warn("Exception while encrypting text", e)
            return text
        }
    }

    fun decrypt(text: String): String {
        try {
            val key = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "AES")
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, key)
            val decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(text))
            return String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            log.warn("Exception while decrypting text", e)
            return text
        }
    }
}