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
    public void testGetField64() throws Exception {
        String msg1 = "BitOp.getField64() failed.";

        //0bit
        Assert.assertEquals(msg1, 0x0, BitOp.getField64(0xffffffffffffffffL, 0, 0));
        Assert.assertEquals(msg1, 0x0, BitOp.getField64(0xffffffffffffffffL, 64, 0));
        Assert.assertEquals(msg1, 0x0, BitOp.getField64(0xffffffffffffffffL, 1, 0));
        Assert.assertEquals(msg1, 0x0, BitOp.getField64(0xffffffffffffffffL, 65, 0));
        Assert.assertEquals(msg1, 0x0, BitOp.getField64(0xffffffffffffffffL, 63, 0));
        Assert.assertEquals(msg1, 0x0, BitOp.getField64(0xffffffffffffffffL, 64, 0));

        //1bit
        Assert.assertEquals(msg1, 0x0, BitOp.getField64(0xfffffffffffffff0L, 0, 1));
        Assert.assertEquals(msg1, 0x1, BitOp.getField64(0xffffffffffffffffL, 64, 1));
        Assert.assertEquals(msg1, 0x0, BitOp.getField64(0xfffffffffffffff0L, 1, 1));
        Assert.assertEquals(msg1, 0x1, BitOp.getField64(0xffffffffffffffffL, 65, 1));
        Assert.assertEquals(msg1, 0x0, BitOp.getField64(0x7fffffffffffffffL, 63, 1));
        Assert.assertEquals(msg1, 0x1, BitOp.getField64(0xffffffffffffffffL, 64, 1));

        //5bits
        Assert.assertEquals(msg1, 0x10, BitOp.getField64(0x0000000000002110L, 0, 5));
        Assert.assertEquals(msg1, 0x00, BitOp.getField64(0x0000000000004300L, 0, 5));
        Assert.assertEquals(msg1, 0x13, BitOp.getField64(0x8000000000de4d00L, 10, 5));
        Assert.assertEquals(msg1, 0x02, BitOp.getField64(0x70000000008b4300L, 18, 5));
        Assert.assertEquals(msg1, 0x02, BitOp.getField64(0x70000000008b4300L, 82, 5));
        Assert.assertEquals(msg1, 0x11, BitOp.getField64(0x44be650000000000L, 58, 5));
        Assert.assertEquals(msg1, 0x00, BitOp.getField64(0x07ad870000000000L, 59, 5));
        Assert.assertEquals(msg1, 0x04, BitOp.getField64(0x87ad870000000000L, 61, 5));

        //64bit
        Assert.assertEquals(msg1, 0xfffffffffffffff0L, BitOp.getField64(0xfffffffffffffff0L, 0, 64));
        Assert.assertEquals(msg1, 0xffffffffffffffffL, BitOp.getField64(0xffffffffffffffffL, 64, 64));
        Assert.assertEquals(msg1, 0x7ffffffffffffff8L, BitOp.getField64(0xfffffffffffffff0L, 1, 64));
        Assert.assertEquals(msg1, 0x7fffffffffffffffL, BitOp.getField64(0xffffffffffffffffL, 65, 64));
        Assert.assertEquals(msg1, 0x0000000000000000L, BitOp.getField64(0x7fffffffffffffffL, 63, 64));
        Assert.assertEquals(msg1, 0x0000000000000001L, BitOp.getField64(0xffffffffffffffffL, 63, 64));
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
    public void testSetField64() throws Exception {
        String msg1 = "BitOp.setField64() failed.";

        //0bit
        Assert.assertEquals(msg1, 0x0000000000000000L, BitOp.setField64(0x0000000000000000L, 0, 0, 0x0000));
        Assert.assertEquals(msg1, 0x0000000000000000L, BitOp.setField64(0x0000000000000000L, 0, 0, 0xffff));
        Assert.assertEquals(msg1, 0xffffffffffffffffL, BitOp.setField64(0xffffffffffffffffL, 0, 0, 0x0000));
        Assert.assertEquals(msg1, 0xffffffffffffffffL, BitOp.setField64(0xffffffffffffffffL, 0, 0, 0xffff));
        Assert.assertEquals(msg1, 0x0000000000000000L, BitOp.setField64(0x0000000000000000L, 8, 0, 0x0000));
        Assert.assertEquals(msg1, 0x0000000000000000L, BitOp.setField64(0x0000000000000000L, 8, 0, 0xffff));
        Assert.assertEquals(msg1, 0xffffffffffffffffL, BitOp.setField64(0xffffffffffffffffL, 8, 0, 0x0000));
        Assert.assertEquals(msg1, 0xffffffffffffffffL, BitOp.setField64(0xffffffffffffffffL, 8, 0, 0xffff));

        //1bit
        Assert.assertEquals(msg1, 0x8000000000000009L, BitOp.setField64(0x8000000000000008L, 0, 1, 0x1));
        Assert.assertEquals(msg1, 0x9000000000000008L, BitOp.setField64(0x1000000000000008L, 63, 1, 0x1));
        Assert.assertEquals(msg1, 0x7000000000008000L, BitOp.setField64(0x7000000000000000L, 15, 1, 0x1));
        Assert.assertEquals(msg1, 0x7000000000008000L, BitOp.setField64(0x7000000000000000L, 79, 1, 0x1));
        Assert.assertEquals(msg1, 0x7001000000000000L, BitOp.setField64(0x7000000000000000L, 48, 1, 0x1));
        Assert.assertEquals(msg1, 0x8000000000000000L, BitOp.setField64(0x8000000000000001L, 0, 1, 0x0));
        Assert.assertEquals(msg1, 0x0000000000000001L, BitOp.setField64(0x8000000000000001L, 63, 1, 0x0));

        //5bits
        Assert.assertEquals(msg1, 0x8000000000002110L, BitOp.setField64(0x8000000000002100L, 0, 5, 0x10));
        Assert.assertEquals(msg1, 0x8000000000004300L, BitOp.setField64(0x8000000000004300L, 0, 5, 0x100));
        Assert.assertEquals(msg1, 0x80de000000004d00L, BitOp.setField64(0x80de000000002100L, 10, 5, 0xf3));
        Assert.assertEquals(msg1, 0x70000000008b4300L, BitOp.setField64(0x7000000000cf4300L, 18, 5, 0xe2));
        Assert.assertEquals(msg1, 0x70000000008b4300L, BitOp.setField64(0x7000000000cf4300L, 82, 5, 0xe2));
        Assert.assertEquals(msg1, 0x44be650000000000L, BitOp.setField64(0x60be650000000000L, 58, 5, 0xd1));
        Assert.assertEquals(msg1, 0x07ad870000000000L, BitOp.setField64(0x5fad870000000000L, 59, 5, 0xc0));

        //64bits
        Assert.assertEquals(msg1, 0x0000000000000001L, BitOp.setField64(0x0000000056000000L, 0, 64, 0x0000000000000001L));
        Assert.assertEquals(msg1, 0x8000000000000001L, BitOp.setField64(0x0000003400000000L, 0, 64, 0x8000000000000001L));
        Assert.assertEquals(msg1, 0xffffff4321ffffffL, BitOp.setField64(0x1200000000000000L, 0, 64, 0xffffff4321ffffffL));
    }

    @org.junit.Test
    public void testToInt() throws Exception {
        String msg1 = "BitOp.toInt() failed.";

        Assert.assertEquals(msg1, 0x0, BitOp.toInt(false));
        Assert.assertEquals(msg1, 0x1, BitOp.toInt(true));
    }

    @org.junit.Test
    public void testSignExt32() throws Exception {
        String msg1 = "BitOp.signExt32() failed.";

        Assert.assertEquals(msg1, 0, BitOp.signExt32(0x1, -1));
        Assert.assertEquals(msg1, 0, BitOp.signExt32(0x1, 0));

        Assert.assertEquals(msg1, -2, BitOp.signExt32(0xe, 34));
        Assert.assertEquals(msg1, 0, BitOp.signExt32(0xe, 33));
        Assert.assertEquals(msg1, -2, BitOp.signExt32(0xfffffffe, 32));
        Assert.assertEquals(msg1, -2, BitOp.signExt32(0x7ffffffe, 31));

        Assert.assertEquals(msg1, -1, BitOp.signExt32(0x1, 1));
        Assert.assertEquals(msg1, 1, BitOp.signExt32(0x1, 2));
        Assert.assertEquals(msg1, -3, BitOp.signExt32(0x5, 3));
        Assert.assertEquals(msg1, 5, BitOp.signExt32(0x5, 4));

        Assert.assertEquals(msg1, -16, BitOp.signExt32(0xff0, 12));
        Assert.assertEquals(msg1, -57360, BitOp.signExt32(0xff1ff0, 24));

        Assert.assertEquals(msg1, -16L, BitOp.signExt32(0xbffffff0, 30));
        Assert.assertEquals(msg1, 0x3ffffff0, BitOp.signExt32(0xbffffff0, 31));
        Assert.assertEquals(msg1, -1073741840, BitOp.signExt32(0xbffffff0, 32));
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