package archive

internal object TestData {
    val data1: ByteArray = "A short text.".toByteArray()
    val data2: ByteArray = ("A very long text." + "aB&f62本一二三§à".repeat(100)).toByteArray()
    val salt1: ByteArray = (0xca8f1840L).toByteArray(8) + (0xbb3d010fL).toByteArray(8)
    val hash1: ByteArray = "Password".to128BitHash()
    val salt2: ByteArray = (0x1a2b3c4dL).toByteArray(8) + (0xd1c2b3a4L).toByteArray(8)
    val strKey1: ByteArray = "MoreSecurePassword![E=m*c^2]".to128BitHash()
    val addr1: Long = 0x112233
    val addr2: Long = 0x998877
}
