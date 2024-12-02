package archive

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UtilityTest {
    @Test
    fun conversionPreservesData() {
        val long1: Long = -0x4919L
        val long2: Long = 0x0L
        val long3: Long = -0x1L
        val long4: Long = (0x9aaaaaaaaaaaaaaaUL).toLong()
        assertEquals(long1, long1.toByteArray(2).toLong())
        assertEquals(long1, long1.toByteArray(4).toLong())
        assertEquals(long1, long1.toByteArray(Long.SIZE_BYTES).toLong())
        assertEquals(long2, long2.toByteArray(Long.SIZE_BYTES).toLong())
        assertEquals(long3, long3.toByteArray(7).toLong())
        assertEquals(long3, long3.toByteArray(Long.SIZE_BYTES).toLong())
        assertEquals(long4, long4.toByteArray(Long.SIZE_BYTES).toLong())
    }
}
