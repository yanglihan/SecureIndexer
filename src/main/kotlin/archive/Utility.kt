package archive

import java.security.MessageDigest

internal fun ByteArray.toULong(): ULong = foldRight(0UL) { it, acc -> acc.shl(8) + it.toUByte() }

internal fun ByteArray.toLong(): Long =
    toULong()
        .shl(Long.SIZE_BITS - (size) * 8)
        .toLong()
        .shr(Long.SIZE_BITS - (size) * 8)

internal fun ULong.toByteArray(size: Int): ByteArray = ByteArray(size) { shr(it * 8).toByte() }

internal fun Long.toByteArray(size: Int): ByteArray = toULong().toByteArray(size)

internal fun ByteArray.takeBytes(
    start: Int,
    size: Int,
): ByteArray = sliceArray(start..<start + size)

internal fun String.to128BitHash(): ByteArray {
    val sha256 = MessageDigest.getInstance("SHA-256")
    val fullHash = sha256.digest(toByteArray())
    return fullHash.copyOf(16)
}
