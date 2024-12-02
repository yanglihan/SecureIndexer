package archive

/**
 * Wrapper for a ByteArray of size 128. The [toString] method returns the decoded string.
 *
 * @throws [IllegalArgumentException]
 */
class Segment(
    val content: ByteArray,
) {
    companion object {
        /** A segment has a size of 128 bytes. */
        const val SIZE: Int = 128

        /** A segment is saved as a chunk of 144 bytes. */
        const val ACTUAL_SIZE: Int = 144
    }

    init {
        require(content.size == SIZE) { "Segment must have content size of 128 bytes." }
    }

    override fun toString(): String = content.decodeToString().substringBefore((0).toChar())
}
