package net.katsuster.ememu.test;

import net.katsuster.ememu.generic.BitOp;
import org.junit.*;

public class BitOpTest {
    @org.junit.Test
    public void testGetField32() throws Exception {
        String msg1 = "BitOp.getField32() failed.";

        //0bit
        Assert.assertEquals(msg1, 0x0, BitOp.getField32(0xffffffff, 0, 0));
        Assert.assertEquals(msg1, 0x0, BitOp.getField32(0xffffffff, 32, 0));
        Assert.assertEquals(msg1, 0x0, BitOp.getField32(0xffffffff, 1, 0));
        Assert.assertEquals(msg1, 0x0, BitOp.getField32(0xffffffff, 33, 0));
        Assert.assertEquals(msg1, 0x0, BitOp.getField32(0xffffffff, 31, 0));
        Assert.assertEquals(msg1, 0x0, BitOp.getField32(0xffffffff, 32, 0));

        //1bit
        Assert.assertEquals(msg1, 0x0, BitOp.getField32(0xfffffff0, 0, 1));
        Assert.assertEquals(msg1, 0x1, BitOp.getField32(0xffffffff, 32, 1));
        Assert.assertEquals(msg1, 0x0, BitOp.getField32(0xfffffff0, 1, 1));
        Assert.assertEquals(msg1, 0x1, BitOp.getField32(0xffffffff, 33, 1));
        Assert.assertEquals(msg1, 0x0, BitOp.getField32(0x7fffffff, 31, 1));
        Assert.assertEquals(msg1, 0x1, BitOp.getField32(0xffffffff, 32, 1));

        //5bits
        Assert.assertEquals(msg1, 0x10, BitOp.getField32(0x00002110, 0, 5));
        Assert.assertEquals(msg1, 0x00, BitOp.getField32(0x00004300, 0, 5));
        Assert.assertEquals(msg1, 0x13, BitOp.getField32(0x80de4d00, 10, 5));
        Assert.assertEquals(msg1, 0x02, BitOp.getField32(0x708b4300, 18, 5));
        Assert.assertEquals(msg1, 0x02, BitOp.getField32(0x708b4300, 82, 5));
        Assert.assertEquals(msg1, 0x11, BitOp.getField32(0x44be6500, 26, 5));
        Assert.assertEquals(msg1, 0x00, BitOp.getField32(0x07ad8700, 27, 5));
        Assert.assertEquals(msg1, 0x04, BitOp.getField32(0x87ad8700, 29, 5));

        //32bit
        Assert.assertEquals(msg1, 0xfffffff0, BitOp.getField32(0xfffffff0, 0, 32));
        Assert.assertEquals(msg1, 0xffffffff, BitOp.getField32(0xffffffff, 32, 32));
        Assert.assertEquals(msg1, 0x7ffffff8, BitOp.getField32(0xfffffff0, 1, 32));
        Assert.assertEquals(msg1, 0x7fffffff, BitOp.getField32(0xffffffff, 33, 32));
        Assert.assertEquals(msg1, 0x00000000, BitOp.getField32(0x7fffffff, 31, 32));
        Assert.assertEquals(msg1, 0x00000001, BitOp.getField32(0xffffffff, 31, 32));
    }

    @org.junit.Test
    public void testSetField32() throws Exception {
        String msg1 = "BitOp.setField32() failed.";

        //0bit
        Assert.assertEquals(msg1, 0x00000000, BitOp.setField32(0x00000000, 0, 0, 0x0000));
        Assert.assertEquals(msg1, 0x00000000, BitOp.setField32(0x00000000, 0, 0, 0xffff));
        Assert.assertEquals(msg1, 0xffffffff, BitOp.setField32(0xffffffff, 0, 0, 0x0000));
        Assert.assertEquals(msg1, 0xffffffff, BitOp.setField32(0xffffffff, 0, 0, 0xffff));
        Assert.assertEquals(msg1, 0x00000000, BitOp.setField32(0x00000000, 8, 0, 0x0000));
        Assert.assertEquals(msg1, 0x00000000, BitOp.setField32(0x00000000, 8, 0, 0xffff));
        Assert.assertEquals(msg1, 0xffffffff, BitOp.setField32(0xffffffff, 8, 0, 0x0000));
        Assert.assertEquals(msg1, 0xffffffff, BitOp.setField32(0xffffffff, 8, 0, 0xffff));

        //1bit
        Assert.assertEquals(msg1, 0x80000009, BitOp.setField32(0x80000008, 0, 1, 0x1));
        Assert.assertEquals(msg1, 0x90000008, BitOp.setField32(0x10000008, 31, 1, 0x1));
        Assert.assertEquals(msg1, 0x70008000, BitOp.setField32(0x70000000, 15, 1, 0x1));
        Assert.assertEquals(msg1, 0x70008000, BitOp.setField32(0x70000000, 47, 1, 0x1));
        Assert.assertEquals(msg1, 0x70010000, BitOp.setField32(0x70000000, 16, 1, 0x1));
        Assert.assertEquals(msg1, 0x80000000, BitOp.setField32(0x80000001, 0, 1, 0x0));
        Assert.assertEquals(msg1, 0x00000001, BitOp.setField32(0x80000001, 31, 1, 0x0));

        //5bits
        Assert.assertEquals(msg1, 0x00002110, BitOp.setField32(0x00002100, 0, 5, 0x10));
        Assert.assertEquals(msg1, 0x00004300, BitOp.setField32(0x00004300, 0, 5, 0x100));
        Assert.assertEquals(msg1, 0x80de4d00, BitOp.setField32(0x80de2100, 10, 5, 0xf3));
        Assert.assertEquals(msg1, 0x708b4300, BitOp.setField32(0x70cf4300, 18, 5, 0xe2));
        Assert.assertEquals(msg1, 0x708b4300, BitOp.setField32(0x70cf4300, 82, 5, 0xe2));
        Assert.assertEquals(msg1, 0x44be6500, BitOp.setField32(0x60be6500, 26, 5, 0xd1));
        Assert.assertEquals(msg1, 0x07ad8700, BitOp.setField32(0x5fad8700, 27, 5, 0xc0));

        //32bits
        Assert.assertEquals(msg1, 0x00000001, BitOp.setField32(0x00005600, 0, 32, 0x00000001));
        Assert.assertEquals(msg1, 0x80000001, BitOp.setField32(0x00340000, 0, 32, 0x80000001));
        Assert.assertEquals(msg1, 0xff4321ff, BitOp.setField32(0x12000000, 0, 32, 0xff4321ff));
    }

    @org.junit.Test
    public void testToInt() throws Exception {
        String msg1 = "BitOp.toInt() failed.";

        Assert.assertEquals(msg1, 0x0, BitOp.toInt(false));
        Assert.assertEquals(msg1, 0x1, BitOp.toInt(true));
    }

    @org.junit.Test
    public void testSignExt64() throws Exception {
        String msg1 = "BitOp.signExt64() failed.";

        Assert.assertEquals(msg1, 0L, BitOp.signExt64(0x1L, -1));
        Assert.assertEquals(msg1, 0L, BitOp.signExt64(0x1L, 0));

        Assert.assertEquals(msg1, -2L, BitOp.signExt64(0xeL, 66));
        Assert.assertEquals(msg1, 0L, BitOp.signExt64(0xeL, 65));
        Assert.assertEquals(msg1, -2L, BitOp.signExt64(0xfffffffffffffffeL, 64));
        Assert.assertEquals(msg1, -2L, BitOp.signExt64(0x7ffffffffffffffeL, 63));

        Assert.assertEquals(msg1, -1L, BitOp.signExt64(0x1L, 1));
        Assert.assertEquals(msg1, 1L, BitOp.signExt64(0x1L, 2));
        Assert.assertEquals(msg1, -3L, BitOp.signExt64(0x5L, 3));
        Assert.assertEquals(msg1, 5L, BitOp.signExt64(0x5L, 4));

        Assert.assertEquals(msg1, -16L, BitOp.signExt64(0xff0L, 12));
        Assert.assertEquals(msg1, -57360L, BitOp.signExt64(0xff1ff0L, 24));
        Assert.assertEquals(msg1, -218161168L, BitOp.signExt64(0xff2ff1ff0L, 36));
        Assert.assertEquals(msg1, -824851882000L, BitOp.signExt64(0xff3ff2ff1ff0L, 48));
        Assert.assertEquals(msg1, -3097049595699216L, BitOp.signExt64(0xff4ff3ff2ff1ff0L, 60));

        Assert.assertEquals(msg1, -16L, BitOp.signExt64(0xbffffff0L, 30));
        Assert.assertEquals(msg1, 0x3ffffff0L, BitOp.signExt64(0xbffffff0L, 31));
        Assert.assertEquals(msg1, -1073741840L, BitOp.signExt64(0xbffffff0L, 32));
        Assert.assertEquals(msg1, 0xbffffff0L, BitOp.signExt64(0xbffffff0L, 33));
    }
}