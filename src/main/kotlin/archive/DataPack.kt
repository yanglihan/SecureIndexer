package archive

class DataPack(
    val descAddr: Long,
    val segAddr: Long,
    val salt: ByteArray,
    val flag: Int,
) {
    companion object {
        const val SIZE: Int = 32
        private const val DESC_ADDR_BYTES: Int = 6
        private const val SEG_ADDR_BYTES: Int = 6
        private const val SALT_BYTES: Int = 16
        private const val MAGIC_BYTES: Int = 4
        private val MAGIC: LongArray = longArrayOf(0xe2a8L, 0xc39fL)

        fun pack(dataPack: DataPack): ByteArray {
            with(dataPack) {
                require(salt.size == SALT_BYTES)
                require(flag in MAGIC.indices)
                return descAddr.toByteArray(DESC_ADDR_BYTES) +
                    segAddr.toByteArray(SEG_ADDR_BYTES) +
                    salt +
                    (MAGIC[flag]).toByteArray(MAGIC_BYTES)
            }
        }

        fun unpack(data: ByteArray): DataPack {
            require(data.size == SIZE) { "data must be of size $SIZE." }
            require(data.takeBytes(SIZE - MAGIC_BYTES, MAGIC_BYTES).toLong() in MAGIC) {
                "data must contains a DataPack."
            }
            return DataPack(
                data.takeBytes(0, DESC_ADDR_BYTES).toLong(),
                data.takeBytes(DESC_ADDR_BYTES, SEG_ADDR_BYTES).toLong(),
                data.takeBytes(DESC_ADDR_BYTES + SEG_ADDR_BYTES, SALT_BYTES),
                MAGIC.indexOf(data.takeBytes(SIZE - MAGIC_BYTES, MAGIC_BYTES).toLong()),
            )
        }
    }
}
