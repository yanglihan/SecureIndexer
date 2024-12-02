package archive

import java.math.BigInteger
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * A encryption for a directory or document.
 */
class ArchiveCipher(
    hash: ByteArray = ByteArray(16),
    salt: ByteArray = ByteArray(16),
) {
    private val raw: ByteArray = (BigInteger(salt) + BigInteger(hash)).toByteArray().copyOf(16)
    private val key = SecretKeySpec(raw, "AES")
    private val eCipher: Cipher = Cipher.getInstance("AES")
    private val dCipher: Cipher = Cipher.getInstance("AES")

    constructor(stringKey: String, salt: ByteArray = ByteArray(16)) : this(stringKey.to128BitHash(), salt)

    init {
        if (salt.size != 16 || hash.size != 16) {
            throw IllegalArgumentException("Seed and salt must be 128-bit.")
        }
        eCipher.init(Cipher.ENCRYPT_MODE, key)
        dCipher.init(Cipher.DECRYPT_MODE, key)
    }

    fun encode(data: ByteArray): ByteArray = eCipher.doFinal(data)

    fun decode(data: ByteArray): ByteArray = dCipher.doFinal(data)
}
