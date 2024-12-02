package archive

import archive.TestData.hash1
import archive.TestData.salt1
import archive.TestData.salt2
import archive.TestData.strKey1
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.io.path.deleteExisting
import kotlin.io.path.exists

class ArchiveFileTest {
    val data1 = "Hello, world!".toByteArray()
    val data2 = "你好，世界！".toByteArray()
    val cipher1: ArchiveCipher = ArchiveCipher(hash1, salt1)
    val cipher2: ArchiveCipher = ArchiveCipher(strKey1, salt2)

    @Test
    fun canCreateArchiveFile() {
        ArchiveFile.createNewArchiveFile("data/test/new_archive", cipher = ArchiveCipher((1L).toByteArray(16)))
        assertTrue(Path("data/test/new_archive").exists())
        Path("data/test/new_archive").deleteExisting()
    }

    @Test
    fun canCreateAndModify() {
        val archive = ArchiveFile.createNewArchiveFile("data/test/new_archive", cipher = cipher1)
        assertTrue(Path("data/test/new_archive").exists())
        archive.addBlock()
        val offset = archive.nextSegment().also(::println)
        archive.setSegment(offset, Segment("Hello, world!".toByteArray().copyOf(128)), cipher2)
        println(archive.getSegment(offset, cipher2))
        Path("data/test/new_archive").deleteExisting()
    }
}
