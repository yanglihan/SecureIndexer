package archive

import java.io.RandomAccessFile

/**
 * An archive file has a head of the following structure:
 *
 * `SEG_COUNT (6 bytes) | SEG_NEXT (6 bytes) | MAGIC (8 bytes) | ...`
 */
class ArchiveFile(
    private val fileName: String,
    cipher: ArchiveCipher,
) {
    companion object {
        /** A block has a size of 1 MB. */
        private const val BLOCK_SIZE = 0x100000

        private const val ADDR_SIZE: Int = 6

        /** `HEAD_SIZE` is only used by methods directly access [RandomAccessFile] methods. */
        private const val HEAD_SIZE: Long = 32

        private const val MAGIC_SIZE: Int = 8
        private const val MAGIC: ULong = 0xac1708b2cfUL

        fun createNewArchiveFile(
            fileName: String,
            cipher: ArchiveCipher,
        ): ArchiveFile {
            val file = RandomAccessFile(fileName, "rw")
            file.setLength(HEAD_SIZE)
            file.write(
                cipher
                    .encode(
                        (0L).toByteArray(ADDR_SIZE) + (0L).toByteArray(ADDR_SIZE) +
                            MAGIC.toByteArray(MAGIC_SIZE),
                    ),
            )
            return ArchiveFile(fileName, cipher)
        }
    }

    private fun getFile(): RandomAccessFile = RandomAccessFile(fileName, "rw")

    val size: Long get() = getFile().length() - HEAD_SIZE

    private var segCount: Long = 0L

    private var segNext: Long = 0L

    fun nextSegment(): Long = segNext++

    fun peekNextSegment(): Long = segNext

    init {
        with(getHead(getFile(), cipher)) {
            segCount = first
            segNext = second
        }
    }

    private fun getHead(
        file: RandomAccessFile,
        cipher: ArchiveCipher,
    ): Pair<Long, Long> {
        with(file) {
            if (length() < HEAD_SIZE) throw ArchiveInvalidFileException()

            val head = cipher.decode(get(-HEAD_SIZE, HEAD_SIZE.toInt()))

            val magic = head.takeBytes(ADDR_SIZE + ADDR_SIZE, MAGIC_SIZE).toULong()
            if (magic != MAGIC) {
                throw ArchiveInvalidFileException()
            }

            return Pair(head.takeBytes(ADDR_SIZE, ADDR_SIZE).toLong(), head.takeBytes(0, ADDR_SIZE).toLong())
        }
    }

    fun get(
        pos: Long,
        size: Int,
    ): ByteArray {
        if (pos + size > this.size) throw IndexOutOfBoundsException()

        val buffer: ByteArray = ByteArray(size)
        with(getFile()) {
            try {
                seek(HEAD_SIZE + pos)
                read(buffer, 0, size)
            } catch (e: IndexOutOfBoundsException) {
                throw IndexOutOfBoundsException()
            }
        }
        return buffer
    }

    fun set(
        pos: Long,
        size: Int,
        data: ByteArray,
    ) {
        if (pos + size > this.size) throw IndexOutOfBoundsException()

        with(getFile()) {
            try {
                seek(HEAD_SIZE + pos)
                write(data, 0, size)
            } catch (e: IndexOutOfBoundsException) {
                throw IndexOutOfBoundsException()
            }
        }
    }

    fun addBlock() {
        with(getFile()) { setLength(HEAD_SIZE + size + BLOCK_SIZE) }
    }

    fun getSegment(
        offset: Long,
        cipher: ArchiveCipher,
    ): Segment = Segment(cipher.decode(get(offset * Segment.ACTUAL_SIZE, Segment.ACTUAL_SIZE)))

    fun setSegment(
        offset: Long,
        segment: Segment,
        cipher: ArchiveCipher,
    ) {
        while ((offset + 1) * Segment.ACTUAL_SIZE > size) addBlock()
        set(offset * Segment.ACTUAL_SIZE, Segment.ACTUAL_SIZE, cipher.encode(segment.content))
    }

    fun setNextSegment(
        segment: Segment,
        cipher: ArchiveCipher,
    ): Long {
        setSegment(peekNextSegment(), segment, cipher)
        return nextSegment()
    }
}
