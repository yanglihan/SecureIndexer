package archive

class ArchiveIndex(
    private val indexFile: ArchiveFile,
    private val archiveFile: ArchiveFile,
) {
    companion object {
        const val ADDR_SIZE: Int = 6
        const val INDEX_SEG_CAP: Int = 122
        const val ADDR_EMPTY: Long = (-1)
    }

    fun readIndex(
        offset: Long,
        cipher: ArchiveCipher,
    ): ByteArray {
        var buffer: ByteArray = ByteArray(0)
        var pos: Long = offset
        do {
            val nextSegment = indexFile.getSegment(pos, cipher)
            pos = nextSegment.content.takeBytes(Segment.SIZE - ADDR_SIZE, ADDR_SIZE).toLong()
            buffer += nextSegment.content.takeBytes(0, Segment.SIZE - ADDR_SIZE)
        } while (pos != ADDR_EMPTY)
        return buffer
    }

    fun writeIndex(
        offset: Long,
        data: ByteArray,
        cipher: ArchiveCipher,
    ): Long {
        var extendedData = data.copyOf(((data.size - 1) / INDEX_SEG_CAP + 1) * INDEX_SEG_CAP)
        val actualOffset = offset.takeIf { it != ADDR_EMPTY } ?: indexFile.peekNextSegment()
        var nextOffset = offset
        var i = 0
        while (i < extendedData.size && nextOffset != ADDR_EMPTY) {
            val seg = indexFile.getSegment(nextOffset, cipher)
            val buffer = extendedData.takeBytes(i, INDEX_SEG_CAP)
            i += INDEX_SEG_CAP
            val peekNextOffset = seg.content.takeBytes(Segment.SIZE - ADDR_SIZE, ADDR_SIZE).toLong()
            if (peekNextOffset != ADDR_EMPTY) {
                indexFile.setSegment(nextOffset, Segment(buffer + peekNextOffset.toByteArray(ADDR_SIZE)), cipher)
            } else {
                val actualNextOffset = indexFile.peekNextSegment()
                indexFile.setSegment(nextOffset, Segment(buffer + actualNextOffset.toByteArray(ADDR_SIZE)), cipher)
                break
            }
            nextOffset = peekNextOffset
        }

        while (i < extendedData.size) {
            val buffer = extendedData.takeBytes(i, INDEX_SEG_CAP)
            i += INDEX_SEG_CAP
            nextOffset = indexFile.nextSegment()
            val peekNextOffset = if (i < extendedData.size) indexFile.peekNextSegment() else ADDR_EMPTY
            indexFile.setSegment(nextOffset, Segment(buffer + peekNextOffset.toByteArray(ADDR_SIZE)), cipher)
        }

        return actualOffset
    }

    fun parse(
        offset: Long,
        cipher: ArchiveCipher,
    ): List<DataPack> {
        val buffer: ByteArray = readIndex(offset, cipher)
        val ret: MutableList<DataPack> = mutableListOf()
        for (pos in buffer.indices step DataPack.SIZE) {
            val pack: DataPack = DataPack.unpack(buffer.takeBytes(pos, DataPack.SIZE))
            ret.add(pack)
        }
        return ret.toList()
    }
}
