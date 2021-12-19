package ru.itmo.mpi.auth.login_password

import java.math.BigInteger
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class PBKDF2WithHmacSHA1PasswordHasher : PasswordHasher {
    override fun getPasswordHash(password: String): String {
        val iterations = 1000
        val chars = password.toCharArray()
        val salt: ByteArray = getSalt()

        val spec = PBEKeySpec(chars, salt, iterations, 64 * 8)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")

        val hash = skf.generateSecret(spec).encoded
        return iterations.toString() + ":" + toHex(salt) + ":" + toHex(hash)
    }

    private fun getSalt(): ByteArray {
        val sr: SecureRandom = SecureRandom.getInstance("SHA1PRNG")
        val salt = ByteArray(16)
        sr.nextBytes(salt)
        return salt
    }

    private fun toHex(array: ByteArray): String {
        val bi = BigInteger(1, array)
        val hex = bi.toString(16)
        val paddingLength = array.size * 2 - hex.length
        return if (paddingLength > 0) {
            String.format("%0" + paddingLength + "d", 0) + hex
        } else {
            hex
        }
    }

    override fun isPasswordValid(password: String, passwordHash: String): Boolean {
        val parts = passwordHash.split(":")
        val iterations = parts[0].toInt()

        val salt: ByteArray = fromHex(parts[1])
        val hash: ByteArray = fromHex(parts[2])

        val spec = PBEKeySpec(
            password.toCharArray(),
            salt,
            iterations,
            hash.size * 8
        )
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val testHash = skf.generateSecret(spec).encoded

        hash.size != testHash.size
        for (i in hash.indices) {
            if (hash[i] != testHash[i]) {
                return false
            }
        }
        return true
    }

    private fun fromHex(hex: String): ByteArray {
        val bytes = ByteArray(hex.length / 2)
        for (i in bytes.indices) {
            bytes[i] = hex.substring(2 * i, 2 * i + 2).toInt(16).toByte()
        }
        return bytes
    }
}