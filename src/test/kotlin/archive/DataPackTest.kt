package archive

import archive.TestData.addr1
import archive.TestData.addr2
import archive.TestData.salt1
import archive.TestData.salt2
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DataPackTest {
    private val dataPack1: DataPack = DataPack(addr1, addr2, salt1, 0)
    private val dataPack2: DataPack = DataPack(addr2, addr1, salt2, 1)

    @Test
    fun dataUnchangedAfterPackingAndUnpacking() {
        val packedDataPack1 = DataPack.pack(dataPack1)
        val newDataPack1 = DataPack.unpack(packedDataPack1)
        assertEquals(newDataPack1.descAddr, dataPack1.descAddr)
        assertEquals(newDataPack1.segAddr, dataPack1.segAddr)
        assertArrayEquals(newDataPack1.salt, (dataPack1.salt))
        assertEquals(newDataPack1.flag, dataPack1.flag)
        assertArrayEquals(DataPack.pack(newDataPack1), packedDataPack1)
    }
}
