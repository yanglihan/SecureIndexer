package archive
import archive.TestData.data1
import archive.TestData.data2
import archive.TestData.hash1
import archive.TestData.salt1
import archive.TestData.salt2
import archive.TestData.strKey1
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test

class ArchiveCipherTest {
    @Test
    fun dataUnchangedAfterEncodingAndDecoding() {
        val cipher1: ArchiveCipher = ArchiveCipher(hash1, salt1)
        val cipher2: ArchiveCipher = ArchiveCipher(salt2, strKey1)
        assertArrayEquals(cipher1.decode(cipher1.encode(data1)), data1)
        assertArrayEquals(cipher1.decode(cipher1.encode(data2)), data2)
        assertArrayEquals(cipher2.decode(cipher2.encode(data1)), data1)
        assertArrayEquals(cipher2.decode(cipher2.encode(data2)), data2)
    }
}
