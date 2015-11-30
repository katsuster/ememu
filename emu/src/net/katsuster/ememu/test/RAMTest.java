package net.katsuster.ememu.test;

import net.katsuster.ememu.generic.*;
import org.junit.*;

public class RAMTest {
    @Test
    public void testAlignedAccessRAM() throws Exception {
        RAM16 ram16 = new RAM16(0x1000);
        RAM32 ram32 = new RAM32(0x1000);
        RAM64 ram64 = new RAM64(0x1000);
        long start = 0x800;

        alignedAccess(start, ram16);
        alignedAccess(start, ram32);
        alignedAccess(start, ram64);
    }

    public void alignedAccess(long start, SlaveCore obj) throws Exception {
        alignedAccess8(start, obj);
        alignedAccess16(start, obj);
        alignedAccess32(start, obj);
        alignedAccess64(start, obj);
    }

    public void alignedAccess8(long start, SlaveCore obj) throws Exception {
        String msg1 = "RAM aligned access 8bits failed.";

        byte[] actual8 = {
                (byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67,
                (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                (byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78,
                (byte)0x9a, (byte)0xbc, (byte)0xde, (byte)0xf9,
        };
        for (int i = 0; i < actual8.length; i++) {
            obj.write8(start + i, actual8[i]);
        }

        long[] expected8_8= {
                0x01L, 0x23L, 0x45L, 0x67L, 0x89L, 0xabL, 0xcdL, 0xefL,
                0x12L, 0x34L, 0x56L, 0x78L, 0x9aL, 0xbcL, 0xdeL, 0xf9L,
        };
        for (int i = 0; i < expected8_8.length; i++) {
            Assert.assertEquals(msg1, expected8_8[i], obj.read8(start + i) & 0xffL);
        }

        long[] expected8_16 = {
                0x2301L, 0x6745L, 0xab89L, 0xefcdL,
                0x3412L, 0x7856L, 0xbc9aL, 0xf9deL,
        };
        for (int i = 0; i < expected8_16.length; i++) {
            Assert.assertEquals(msg1, expected8_16[i], obj.read16(start + i * 2) & 0xffffL);
        }

        long[] expected8_32 = {
                0x67452301L, 0xefcdab89L, 0x78563412L, 0xf9debc9aL,
        };
        for (int i = 0; i < expected8_32.length; i++) {
            Assert.assertEquals(msg1, expected8_32[i], obj.read32(start + i * 4) & 0xffffffffL);
        }

        long[] expected8_64 = {
                0xefcdab8967452301L, 0xf9debc9a78563412L,
        };
        for (int i = 0; i < expected8_64.length; i++) {
            Assert.assertEquals(msg1, expected8_64[i], obj.read64(start + i * 8));
        }
    }

    public void alignedAccess16(long start, SlaveCore obj) throws Exception {
        String msg1 = "RAM aligned access 16bits failed.";

        short[] actual16 = {
                (short)0x0123, (short)0x4567, (short)0x89ab, (short)0xcdef,
                (short)0x1234, (short)0x5678, (short)0x9abc, (short)0xdef9,
        };
        for (int i = 0; i < actual16.length; i++) {
            obj.write16(start + i * 2, actual16[i]);
        }

        long[] expected16_8 = {
                0x23L, 0x01L, 0x67L, 0x45L, 0xabL, 0x89L, 0xefL, 0xcdL,
                0x34L, 0x12L, 0x78L, 0x56L, 0xbcL, 0x9aL, 0xf9L, 0xdeL,
        };
        for (int i = 0; i < expected16_8.length; i++) {
            Assert.assertEquals(msg1, expected16_8[i], obj.read8(start + i) & 0xffL);
        }

        long[] expected16_16 = {
                0x0123L, 0x4567L, 0x89abL, 0xcdefL,
                0x1234L, 0x5678L, 0x9abcL, 0xdef9L,
        };
        for (int i = 0; i < expected16_16.length; i++) {
            Assert.assertEquals(msg1, expected16_16[i], obj.read16(start + i * 2) & 0xffffL);
        }

        long[] expected16_32 = {
                0x45670123L, 0xcdef89abL, 0x56781234L, 0xdef99abcL,
        };
        for (int i = 0; i < expected16_32.length; i++) {
            Assert.assertEquals(msg1, expected16_32[i], obj.read32(start + i * 4) & 0xffffffffL);
        }

        long[] expected16_64 = {
                0xcdef89ab45670123L, 0xdef99abc56781234L,
        };
        for (int i = 0; i < expected16_64.length; i++) {
            Assert.assertEquals(msg1, expected16_64[i], obj.read64(start + i * 8));
        }
    }

    public void alignedAccess32(long start, SlaveCore obj) throws Exception {
        String msg1 = "RAM aligned access 32bits failed.";

        int[] actual32 = {
                0x01234567, 0x89abcdef, 0x12345678, 0x9abcdef9,
        };
        for (int i = 0; i < actual32.length; i++) {
            obj.write32(start + i * 4, actual32[i]);
        }

        long[] expected32_8 = {
                0x67L, 0x45L, 0x23L, 0x01L, 0xefL, 0xcdL, 0xabL, 0x89L,
                0x78L, 0x56L, 0x34L, 0x12L, 0xf9L, 0xdeL, 0xbcL, 0x9aL,
        };
        for (int i = 0; i < expected32_8.length; i++) {
            Assert.assertEquals(msg1, expected32_8[i], obj.read8(start + i) & 0xffL);
        }

        long[] expected32_16 = {
                0x4567L, 0x0123L, 0xcdefL, 0x89abL,
                0x5678L, 0x1234L, 0xdef9L, 0x9abcL,
        };
        for (int i = 0; i < expected32_16.length; i++) {
            Assert.assertEquals(msg1, expected32_16[i], obj.read16(start + i * 2) & 0xffffL);
        }

        long[] expected32_32 = {
                0x01234567L, 0x89abcdefL, 0x12345678L, 0x9abcdef9L,
        };
        for (int i = 0; i < expected32_32.length; i++) {
            Assert.assertEquals(msg1, expected32_32[i], obj.read32(start + i * 4) & 0xffffffffL);
        }

        long[] expected32_64 = {
                0x89abcdef01234567L, 0x9abcdef912345678L,
        };
        for (int i = 0; i < expected32_64.length; i++) {
            Assert.assertEquals(msg1, expected32_64[i], obj.read64(start + i * 8));
        }
    }

    public void alignedAccess64(long start, SlaveCore obj) throws Exception {
        String msg1 = "RAM aligned access 64bits failed.";

        long[] actual64 = {
                0x0123456789abcdefL, 0x89abcdeffedcba98L,
        };
        for (int i = 0; i < actual64.length; i++) {
            obj.write64(start + i * 8, actual64[i]);
        }

        long[] expected64_8 = {
                0xefL, 0xcdL, 0xabL, 0x89L, 0x67L, 0x45L, 0x23L, 0x01L,
                0x98L, 0xbaL, 0xdcL, 0xfeL, 0xefL, 0xcdL, 0xabL, 0x89L,
        };
        for (int i = 0; i < expected64_8.length; i++) {
            Assert.assertEquals(msg1, expected64_8[i], obj.read8(start + i) & 0xffL);
        }

        long[] expected64_16 = {
                0xcdefL, 0x89abL, 0x4567L, 0x0123L,
                0xba98L, 0xfedcL, 0xcdefL, 0x89abL,
        };
        for (int i = 0; i < expected64_16.length; i++) {
            Assert.assertEquals(msg1, expected64_16[i], obj.read16(start + i * 2) & 0xffffL);
        }

        long[] expected64_32 = {
                0x89abcdefL, 0x01234567L, 0xfedcba98L, 0x89abcdefL,
        };
        for (int i = 0; i < expected64_32.length; i++) {
            Assert.assertEquals(msg1, expected64_32[i], obj.read32(start + i * 4) & 0xffffffffL);
        }

        long[] expected64_64 = {
                0x0123456789abcdefL, 0x89abcdeffedcba98L,
        };
        for (int i = 0; i < expected64_64.length; i++) {
            Assert.assertEquals(msg1, expected64_64[i], obj.read64(start + i * 8));
        }
    }
}