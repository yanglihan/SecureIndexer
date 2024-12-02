package archive

import archive.TestData.strKey1
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import kotlin.random.Random

class ArchiveIndexTest {
    @Test
    fun canWriteAndRead() {
        val data1 = Random.nextBytes(1996)
        val archive =
            ArchiveFile.createNewArchiveFile("data/test/new_archive", ArchiveCipher(strKey1))
        val index =
            ArchiveFile.createNewArchiveFile("data/test/new_index", ArchiveCipher(strKey1))
        val archiveIndex = ArchiveIndex(index, archive)
        val pos = archiveIndex.writeIndex(ArchiveIndex.ADDR_EMPTY, data1, ArchiveCipher("data1"))
        val retrievedData1 = archiveIndex.readIndex(pos, ArchiveCipher("data1"))
        assertArrayEquals(retrievedData1.takeBytes(0, 1996), data1)
    }
}
