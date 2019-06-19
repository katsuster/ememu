package net.katsuster.ememu.test;

import net.katsuster.ememu.generic.*;
import org.junit.*;

public class BusTest {
    @Test
    public void testAddSlaveCore() throws Exception {
        String msg1 = "addSlaveCore() failed.";
        String msg2 = "addSlaveCore() address check failed.";
        String msg3 = "addSlaveCore() duplicate check failed.";
        String msg4 = "addSlaveCore() null check failed.";

        Bus bus = new Bus();
        RAM16 ram1 = new RAM16(0x1000);
        RAM32 ram2 = new RAM32(0x1000);
        RAM64 ram3 = new RAM64(0x1000);

        try {
            bus.addSlaveCore(ram1, 0x0, 0xfff);
            bus.addSlaveCore(ram2, 0x8000, 0x8fff);
            bus.addSlaveCore(ram3, 0x10000, 0x10fff);

            //mirror
            bus.addSlaveCore(ram1, 0x2000, 0x2fff);

            //over 32bit
            bus.addSlaveCore(ram1, 0x100000000L, 0x1ffffffffL);
        } catch (Exception e) {
            Assert.fail(msg1);
        }

        try {
            //wrong range
            bus.addSlaveCore(ram1, 0x1000, 0xff);
            Assert.fail(msg2);
        } catch (Exception e) {
            //OK
        }

        try {
            //duplicate
            bus.addSlaveCore(ram1, 0x0, 0xfff);
            Assert.fail(msg3);
        } catch (Exception e) {
            //OK
        }

        try {
            //null
            bus.addSlaveCore(null, 0x0, 0xfff);
            Assert.fail(msg4);
        } catch (Exception e) {
            //OK
        }
    }

    @Test
    public void testGetSlaveCore() throws Exception {
        String msg1 = "getSlaveCore() failed.";
        String msg2 = "getSlaveCore() non-exist check failed.";
        String msg3 = "getSlaveCore() address check failed.";

        Bus bus = new Bus();
        RAM16 ram1 = new RAM16(0x1000);
        RAM32 ram2 = new RAM32(0x1000);

        bus.addSlaveCore(ram1, 0x0, 0xfff);
        bus.addSlaveCore(ram1, 0x100000000L, 0x100000fffL);
        bus.addSlaveCore(ram2, 0x20000, 0x207ff);

        //simple
        Assert.assertEquals(msg1, ram1, bus.getSlaveCore(0x100, 0x200));
        Assert.assertEquals(msg1, ram1, bus.getSlaveCore(0x100000100L, 0x100000200L));
        Assert.assertEquals(msg1, ram2, bus.getSlaveCore(0x20100, 0x20200));

        //non-exist
        Assert.assertNull(msg2, bus.getSlaveCore(0x1000, 0x1100));
        Assert.assertNull(msg2, bus.getSlaveCore(0x100001000L, 0x100001100L));
        Assert.assertNull(msg2, bus.getSlaveCore(0x20800, 0x20900));

        try {
            //wrong range
            bus.getSlaveCore(0x100, 0x80);
            Assert.fail(msg3);
        } catch (Exception e) {
            //OK
        }

        try {
            //wrong range
            bus.getSlaveCore(0x4000, 0x3000);
            Assert.fail(msg3);
        } catch (Exception e) {
            //OK
        }
    }

    @Test
    public void testRemoveSlaveCore() throws Exception {
        String msg1 = "removeSlaveCore() failed.";
        String msg2 = "removeSlaveCore() re-add failed.";
        String msg3 = "removeSlaveCore(null) ignore failed.";

        Bus bus = new Bus();
        RAM16 ram1 = new RAM16(0x1000);
        RAM32 ram2 = new RAM32(0x1000);
        RAM64 ram3 = new RAM64(0x1000);
        boolean result;

        bus.addSlaveCore(ram1, 0x0, 0xfff);
        bus.addSlaveCore(ram1, 0x2000, 0x7fff);
        bus.addSlaveCore(ram2, 0x8000, 0x8fff);
        bus.addSlaveCore(ram3, 0x10000, 0x10fff);

        try {
            //simple
            result = bus.removeSlaveCore(ram1);
            Assert.assertTrue(msg1, result);
            result = bus.removeSlaveCore(ram2);
            Assert.assertTrue(msg1, result);
            result = bus.removeSlaveCore(ram3);
            Assert.assertTrue(msg1, result);

            //non exist
            result = bus.removeSlaveCore(ram1);
            Assert.assertFalse(msg1, result);
        } catch (Exception e) {
            Assert.fail(msg1);
        }

        try {
            //re-add
            bus.addSlaveCore(ram1, 0x0, 0xfff);
            bus.addSlaveCore(ram1, 0x20000, 0x20fff);
        } catch (Exception e) {
            Assert.fail(msg2);
        }

        try {
            result = bus.removeSlaveCore(null);
            Assert.assertFalse(msg3, result);
        } catch (Exception e) {
            Assert.fail(msg1);
        }
    }

    @Test
    public void testAlignedAccess() throws Exception {
        Bus bus = new Bus();
        RAM16 ram16 = new RAM16(0x2000);
        RAM32 ram32 = new RAM32(0x2000);
        RAM64 ram64 = new RAM64(0x2000);

        bus.addSlaveCore(ram16, 0x10000, 0x11fff);
        bus.addSlaveCore(ram32, 0x12000, 0x13fff);
        bus.addSlaveCore(ram64, 0x14000, 0x15fff);
        bus.addSlaveCore(ram16, 0x100000000L, 0x100001fffL);
        bus.addSlaveCore(ram32, 0x100002000L, 0x100003fffL);
        bus.addSlaveCore(ram64, 0x100004000L, 0x100005fffL);

        //Aligned
        unalignedAccess(0x10ff0, bus);
        unalignedAccess(0x12ff0, bus);
        unalignedAccess(0x14ff0, bus);

        //Unaligned
        unalignedAccess(0x10ff0, bus);
        unalignedAccess(0x10ff1, bus);
        unalignedAccess(0x10ff2, bus);
        unalignedAccess(0x10ff3, bus);
        unalignedAccess(0x10ff4, bus);
        unalignedAccess(0x10ff5, bus);
        unalignedAccess(0x10ff6, bus);
        unalignedAccess(0x10ff7, bus);
    }

    public void unalignedAccess(long start, Bus bus) throws Exception {
        unalignedAccess8(start, bus);
        unalignedAccess16(start, bus);
        unalignedAccess32(start, bus);
        unalignedAccess64(start, bus);
    }

    public void unalignedAccess8(long start, Bus bus) throws Exception {
        String msg1 = "Bus aligned access 8bits failed.";

        byte[] actual8 = {
                (byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67,
                (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                (byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78,
                (byte)0x9a, (byte)0xbc, (byte)0xde, (byte)0xf9,

                (byte)0x01, (byte)0x23, (byte)0x67, (byte)0xcd,
                (byte)0x89, (byte)0xab, (byte)0xef, (byte)0x45,
                (byte)0x12, (byte)0x56, (byte)0x78, (byte)0xbc,
                (byte)0x9a, (byte)0xde, (byte)0xf9, (byte)0x34,
        };
        for (int i = 0; i < actual8.length; i++) {
            bus.write8(start + i, actual8[i]);
        }

        long[] expected8_8= {
                0x01L, 0x23L, 0x45L, 0x67L, 0x89L, 0xabL, 0xcdL, 0xefL,
                0x12L, 0x34L, 0x56L, 0x78L, 0x9aL, 0xbcL, 0xdeL, 0xf9L,
                0x01L, 0x23L, 0x67L, 0xcdL, 0x89L, 0xabL, 0xefL, 0x45L,
                0x12L, 0x56L, 0x78L, 0xbcL, 0x9aL, 0xdeL, 0xf9L, 0x34L,
        };
        for (int i = 0; i < expected8_8.length; i++) {
            Assert.assertEquals(msg1, expected8_8[i], bus.read8(start + i) & 0xffL);
        }

        long[] expected8_16 = {
                0x2301L, 0x6745L, 0xab89L, 0xefcdL,
                0x3412L, 0x7856L, 0xbc9aL, 0xf9deL,
                0x2301L, 0xcd67L, 0xab89L, 0x45efL,
                0x5612L, 0xbc78L, 0xde9aL, 0x34f9L,
        };
        for (int i = 0; i < expected8_16.length; i++) {
            Assert.assertEquals(msg1, expected8_16[i], bus.read16(start + i * 2) & 0xffffL);
        }

        long[] expected8_32 = {
                0x67452301L, 0xefcdab89L, 0x78563412L, 0xf9debc9aL,
                0xcd672301L, 0x45efab89L, 0xbc785612L, 0x34f9de9aL,
        };
        for (int i = 0; i < expected8_32.length; i++) {
            Assert.assertEquals(msg1, expected8_32[i], bus.read32(start + i * 4) & 0xffffffffL);
        }

        long[] expected8_64 = {
                0xefcdab8967452301L, 0xf9debc9a78563412L,
                0x45efab89cd672301L, 0x34f9de9abc785612L,
        };
        for (int i = 0; i < expected8_64.length; i++) {
            Assert.assertEquals(msg1, expected8_64[i], bus.read64(start + i * 8));
        }
    }

    public void unalignedAccess16(long start, Bus bus) throws Exception {
        String msg1 = "Bus aligned access 16bits failed.";

        short[] actual16 = {
                (short)0x0123, (short)0x4567, (short)0x89ab, (short)0xcdef,
                (short)0x1234, (short)0x5678, (short)0x9abc, (short)0xdef9,
                (short)0x4567, (short)0x89ab, (short)0xcdef, (short)0x0123,
                (short)0xdef9, (short)0x1234, (short)0x5678, (short)0x9abc,
        };
        for (int i = 0; i < actual16.length; i++) {
            bus.write16(start + i * 2, actual16[i]);
        }

        long[] expected16_8 = {
                0x23L, 0x01L, 0x67L, 0x45L, 0xabL, 0x89L, 0xefL, 0xcdL,
                0x34L, 0x12L, 0x78L, 0x56L, 0xbcL, 0x9aL, 0xf9L, 0xdeL,
                0x67L, 0x45L, 0xabL, 0x89L, 0xefL, 0xcdL, 0x23L, 0x01L,
                0xf9L, 0xdeL, 0x34L, 0x12L, 0x78L, 0x56L, 0xbcL, 0x9aL,
        };
        for (int i = 0; i < expected16_8.length; i++) {
            Assert.assertEquals(msg1, expected16_8[i], bus.read8(start + i) & 0xffL);
        }

        long[] expected16_16 = {
                0x0123L, 0x4567L, 0x89abL, 0xcdefL,
                0x1234L, 0x5678L, 0x9abcL, 0xdef9L,
                0x4567L, 0x89abL, 0xcdefL, 0x0123L,
                0xdef9L, 0x1234L, 0x5678L, 0x9abcL,
        };
        for (int i = 0; i < expected16_16.length; i++) {
            Assert.assertEquals(msg1, expected16_16[i], bus.read16(start + i * 2) & 0xffffL);
        }

        long[] expected16_32 = {
                0x45670123L, 0xcdef89abL, 0x56781234L, 0xdef99abcL,
                0x89ab4567L, 0x0123cdefL, 0x1234def9L, 0x9abc5678L,
        };
        for (int i = 0; i < expected16_32.length; i++) {
            Assert.assertEquals(msg1, expected16_32[i], bus.read32(start + i * 4) & 0xffffffffL);
        }

        long[] expected16_64 = {
                0xcdef89ab45670123L, 0xdef99abc56781234L,
                0x0123cdef89ab4567L, 0x9abc56781234def9L,
        };
        for (int i = 0; i < expected16_64.length; i++) {
            Assert.assertEquals(msg1, expected16_64[i], bus.read64(start + i * 8));
        }
    }

    public void unalignedAccess32(long start, Bus bus) throws Exception {
        String msg1 = "Bus aligned access 32bits failed.";

        int[] actual32 = {
                0x01234567, 0x89abcdef, 0x12345678, 0x9abcdef9,
                0x23456789, 0xabcdef12, 0x3456789a, 0xbcdef9ba,
        };
        for (int i = 0; i < actual32.length; i++) {
            bus.write_ua32(start + i * 4, actual32[i]);
        }

        long[] expected32_8 = {
                0x67L, 0x45L, 0x23L, 0x01L, 0xefL, 0xcdL, 0xabL, 0x89L,
                0x78L, 0x56L, 0x34L, 0x12L, 0xf9L, 0xdeL, 0xbcL, 0x9aL,
                0x89L, 0x67L, 0x45L, 0x23L, 0x12L, 0xefL, 0xcdL, 0xabL,
                0x9aL, 0x78L, 0x56L, 0x34L, 0xbaL, 0xf9L, 0xdeL, 0xbcL,
        };
        for (int i = 0; i < expected32_8.length; i++) {
            Assert.assertEquals(msg1, expected32_8[i], bus.read8(start + i) & 0xffL);
        }

        long[] expected32_16 = {
                0x4567L, 0x0123L, 0xcdefL, 0x89abL,
                0x5678L, 0x1234L, 0xdef9L, 0x9abcL,
                0x6789L, 0x2345L, 0xef12L, 0xabcdL,
                0x789aL, 0x3456L, 0xf9baL, 0xbcdeL,
        };
        for (int i = 0; i < expected32_16.length; i++) {
            Assert.assertEquals(msg1, expected32_16[i], bus.read16(start + i * 2) & 0xffffL);
        }

        long[] expected32_32 = {
                0x01234567L, 0x89abcdefL, 0x12345678L, 0x9abcdef9L,
                0x23456789L, 0xabcdef12L, 0x3456789aL, 0xbcdef9baL,
        };
        for (int i = 0; i < expected32_32.length; i++) {
            Assert.assertEquals(msg1, expected32_32[i], bus.read32(start + i * 4) & 0xffffffffL);
        }

        long[] expected32_64 = {
                0x89abcdef01234567L, 0x9abcdef912345678L,
                0xabcdef1223456789L, 0xbcdef9ba3456789aL,
        };
        for (int i = 0; i < expected32_64.length; i++) {
            Assert.assertEquals(msg1, expected32_64[i], bus.read64(start + i * 8));
        }
    }

    public void unalignedAccess64(long start, Bus bus) throws Exception {
        String msg1 = "Bus aligned access 64bits failed.";

        long[] actual64 = {
                0x0123456789abcdefL, 0x89abcdeffedcba98L,
                0x23456789abcdef89L, 0xabcdeffedcba9801L,
        };
        for (int i = 0; i < actual64.length; i++) {
            bus.write64(start + i * 8, actual64[i]);
        }

        long[] expected64_8 = {
                0xefL, 0xcdL, 0xabL, 0x89L, 0x67L, 0x45L, 0x23L, 0x01L,
                0x98L, 0xbaL, 0xdcL, 0xfeL, 0xefL, 0xcdL, 0xabL, 0x89L,
                0x89L, 0xefL, 0xcdL, 0xabL, 0x89L, 0x67L, 0x45L, 0x23L,
                0x01L, 0x98L, 0xbaL, 0xdcL, 0xfeL, 0xefL, 0xcdL, 0xabL,
        };
        for (int i = 0; i < expected64_8.length; i++) {
            Assert.assertEquals(msg1, expected64_8[i], bus.read8(start + i) & 0xffL);
        }

        long[] expected64_16 = {
                0xcdefL, 0x89abL, 0x4567L, 0x0123L,
                0xba98L, 0xfedcL, 0xcdefL, 0x89abL,
                0xef89L, 0xabcdL, 0x6789L, 0x2345L,
                0x9801L, 0xdcbaL, 0xeffeL, 0xabcdL,
        };
        for (int i = 0; i < expected64_16.length; i++) {
            Assert.assertEquals(msg1, expected64_16[i], bus.read16(start + i * 2) & 0xffffL);
        }

        long[] expected64_32 = {
                0x89abcdefL, 0x01234567L, 0xfedcba98L, 0x89abcdefL,
                0xabcdef89L, 0x23456789L, 0xdcba9801L, 0xabcdeffeL,
        };
        for (int i = 0; i < expected64_32.length; i++) {
            Assert.assertEquals(msg1, expected64_32[i], bus.read32(start + i * 4) & 0xffffffffL);
        }

        long[] expected64_64 = {
                0x0123456789abcdefL, 0x89abcdeffedcba98L,
                0x23456789abcdef89L, 0xabcdeffedcba9801L,
        };
        for (int i = 0; i < expected64_64.length; i++) {
            Assert.assertEquals(msg1, expected64_64[i], bus.read64(start + i * 8));
        }
    }
}