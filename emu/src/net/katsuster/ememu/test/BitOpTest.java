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

        //31bit
        Assert.assertEquals(msg1, 0x7ffffff0, BitOp.getField32(0xfffffff0, 0, 31));
        Assert.assertEquals(msg1, 0x7fffffff, BitOp.getField32(0xffffffff, 32, 31));
        Assert.assertEquals(msg1, 0x7ffffff8, BitOp.getField32(0xfffffff0, 1, 31));
        Assert.assertEquals(msg1, 0x3ffffffc, BitOp.getField32(0xfffffff0, 2, 31));

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

        //63bit
        Assert.assertEquals(msg1, 0x7ffffffffffffff0L, BitOp.getField64(0xfffffffffffffff0L, 0, 63));
        Assert.assertEquals(msg1, 0x7fffffffffffffffL, BitOp.getField64(0xffffffffffffffffL, 64, 63));
        Assert.assertEquals(msg1, 0x7ffffffffffffff8L, BitOp.getField64(0xfffffffffffffff0L, 1, 63));
        Assert.assertEquals(msg1, 0x3ffffffffffffffcL, BitOp.getField64(0xfffffffffffffff0L, 2, 63));

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

        //31bits
        Assert.assertEquals(msg1, 0x80000001, BitOp.setField32(0x80005600, 0, 31, 0x00000001));
        Assert.assertEquals(msg1, 0x00000001, BitOp.setField32(0x00340000, 0, 31, 0x80000001));
        Assert.assertEquals(msg1, 0x7f4321ff, BitOp.setField32(0x12000000, 0, 31, 0xff4321ff));
        Assert.assertEquals(msg1, 0xf4321ff0, BitOp.setField32(0x82000000, 4, 31, 0xff4321ff));
        Assert.assertEquals(msg1, 0x4321ff00, BitOp.setField32(0x82000000, 8, 31, 0xff4321ff));

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

        //63bits
        Assert.assertEquals(msg1, 0x8000000000000001L, BitOp.setField64(0x80005600, 0, 63, 0x0000000000000001L));
        Assert.assertEquals(msg1, 0x0000000000000001L, BitOp.setField64(0x00340000, 0, 63, 0x8000000000000001L));
        Assert.assertEquals(msg1, 0x7f430000000021ffL, BitOp.setField64(0x12000000, 0, 63, 0xff430000000021ffL));
        Assert.assertEquals(msg1, 0xf430000000021ff0L, BitOp.setField64(0x82000000, 4, 63, 0xff430000000021ffL));
        Assert.assertEquals(msg1, 0x430000000021ff00L, BitOp.setField64(0x82000000, 8, 63, 0xff430000000021ffL));

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

    @org.junit.Test
    public void testReadMasked() throws Exception {
        String msg1 = "BitOp.readMasked() failed.";

        //bus:8bits, data:8bits
        Assert.assertEquals(msg1, (byte) 0xfe, (byte) BitOp.readMasked(0x60, 0xfe, 8, 8));
        Assert.assertEquals(msg1, (byte) 0x54, (byte) BitOp.readMasked(0x61, 0x54, 8, 8));

        Assert.assertEquals(msg1, (byte) 0xfd, (byte) BitOp.readMasked(0xfffffffffffffff0L, 0xfd, 8, 8));
        Assert.assertEquals(msg1, (byte) 0x53, (byte) BitOp.readMasked(0xfffffffffffffff1L, 0x53, 8, 8));

        //bus:16bits, data:8bits
        Assert.assertEquals(msg1, (byte) 0x54, (byte) BitOp.readMasked(0x0, 0xfe54, 16, 8));
        Assert.assertEquals(msg1, (byte) 0xfe, (byte) BitOp.readMasked(0x1, 0xfe54, 16, 8));

        Assert.assertEquals(msg1, (byte) 0x54, (byte) BitOp.readMasked(0xfffffffffffffff0L, 0xfe54, 16, 8));
        Assert.assertEquals(msg1, (byte) 0xfe, (byte) BitOp.readMasked(0xfffffffffffffff1L, 0xfe54, 16, 8));

        //bus:32bits, data:8, 16bits
        Assert.assertEquals(msg1, (byte) 0x32, (byte) BitOp.readMasked(0x10, 0xfedc5432, 32, 8));
        Assert.assertEquals(msg1, (byte) 0x54, (byte) BitOp.readMasked(0x11, 0xfedc5432, 32, 8));
        Assert.assertEquals(msg1, (byte) 0xdc, (byte) BitOp.readMasked(0x12, 0xfedc5432, 32, 8));
        Assert.assertEquals(msg1, (byte) 0xfe, (byte) BitOp.readMasked(0x13, 0xfedc5432, 32, 8));

        Assert.assertEquals(msg1, (short) 0x5432, (short) BitOp.readMasked(0x20, 0xfedc5432, 32, 16));
        Assert.assertEquals(msg1, (short) 0x5432, (short) BitOp.readMasked(0x21, 0xfedc5432, 32, 16));
        Assert.assertEquals(msg1, (short) 0xfedc, (short) BitOp.readMasked(0x22, 0xfedc5432, 32, 16));
        Assert.assertEquals(msg1, (short) 0xfedc, (short) BitOp.readMasked(0x23, 0xfedc5432, 32, 16));

        //bus:64bits, data:8, 16, 32bits
        Assert.assertEquals(msg1, (byte) 0x10, (byte) BitOp.readMasked(0x30, 0xfedcba9876543210L, 64, 8));
        Assert.assertEquals(msg1, (byte) 0x32, (byte) BitOp.readMasked(0x31, 0xfedcba9876543210L, 64, 8));
        Assert.assertEquals(msg1, (byte) 0x54, (byte) BitOp.readMasked(0x32, 0xfedcba9876543210L, 64, 8));
        Assert.assertEquals(msg1, (byte) 0x76, (byte) BitOp.readMasked(0x33, 0xfedcba9876543210L, 64, 8));
        Assert.assertEquals(msg1, (byte) 0x98, (byte) BitOp.readMasked(0x34, 0xfedcba9876543210L, 64, 8));
        Assert.assertEquals(msg1, (byte) 0xba, (byte) BitOp.readMasked(0x35, 0xfedcba9876543210L, 64, 8));
        Assert.assertEquals(msg1, (byte) 0xdc, (byte) BitOp.readMasked(0x36, 0xfedcba9876543210L, 64, 8));
        Assert.assertEquals(msg1, (byte) 0xfe, (byte) BitOp.readMasked(0x37, 0xfedcba9876543210L, 64, 8));

        Assert.assertEquals(msg1, (short) 0x3210, (short) BitOp.readMasked(0x40, 0xfedcba9876543210L, 64, 16));
        Assert.assertEquals(msg1, (short) 0x3210, (short) BitOp.readMasked(0x41, 0xfedcba9876543210L, 64, 16));
        Assert.assertEquals(msg1, (short) 0x7654, (short) BitOp.readMasked(0x42, 0xfedcba9876543210L, 64, 16));
        Assert.assertEquals(msg1, (short) 0x7654, (short) BitOp.readMasked(0x43, 0xfedcba9876543210L, 64, 16));
        Assert.assertEquals(msg1, (short) 0xba98, (short) BitOp.readMasked(0x44, 0xfedcba9876543210L, 64, 16));
        Assert.assertEquals(msg1, (short) 0xba98, (short) BitOp.readMasked(0x45, 0xfedcba9876543210L, 64, 16));
        Assert.assertEquals(msg1, (short) 0xfedc, (short) BitOp.readMasked(0x46, 0xfedcba9876543210L, 64, 16));
        Assert.assertEquals(msg1, (short) 0xfedc, (short) BitOp.readMasked(0x47, 0xfedcba9876543210L, 64, 16));

        Assert.assertEquals(msg1, 0x76543210, (int) BitOp.readMasked(0x50, 0xfedcba9876543210L, 64, 32));
        Assert.assertEquals(msg1, 0x76543210, (int) BitOp.readMasked(0x51, 0xfedcba9876543210L, 64, 32));
        Assert.assertEquals(msg1, 0x76543210, (int) BitOp.readMasked(0x52, 0xfedcba9876543210L, 64, 32));
        Assert.assertEquals(msg1, 0x76543210, (int) BitOp.readMasked(0x53, 0xfedcba9876543210L, 64, 32));
        Assert.assertEquals(msg1, 0xfedcba98, (int) BitOp.readMasked(0x54, 0xfedcba9876543210L, 64, 32));
        Assert.assertEquals(msg1, 0xfedcba98, (int) BitOp.readMasked(0x55, 0xfedcba9876543210L, 64, 32));
        Assert.assertEquals(msg1, 0xfedcba98, (int) BitOp.readMasked(0x56, 0xfedcba9876543210L, 64, 32));
        Assert.assertEquals(msg1, 0xfedcba98, (int) BitOp.readMasked(0x57, 0xfedcba9876543210L, 64, 32));
    }

    @org.junit.Test
    public void testWriteMasked() throws Exception {
        String msg1 = "BitOp.writeMasked() failed.";

        //bus:8bits, data:8bits
        Assert.assertEquals(msg1, (byte) 0xf0, (byte) BitOp.writeMasked(0x60, 0xfe, 0xf0, 8, 8));
        Assert.assertEquals(msg1, (byte) 0xf2, (byte) BitOp.writeMasked(0x61, 0x54, 0xf2, 8, 8));

        Assert.assertEquals(msg1, (byte) 0xf4, (byte) BitOp.writeMasked(0xfffffffffffffff0L, 0xfe, 0xf4, 8, 8));
        Assert.assertEquals(msg1, (byte) 0xf6, (byte) BitOp.writeMasked(0xfffffffffffffff1L, 0x54, 0xf6, 8, 8));

        //bus:16bits, data:8bits
        Assert.assertEquals(msg1, (short) 0xfef0, (short) BitOp.writeMasked(0x60, 0xfe54, 0x1f0, 16, 8));
        Assert.assertEquals(msg1, (short) 0xf054, (short) BitOp.writeMasked(0x61, 0xfe54, 0x2f0, 16, 8));

        Assert.assertEquals(msg1, (short) 0xfe0f, (short) BitOp.writeMasked(0xfffffffffffffff0L, 0xfe54, 0xe0f, 16, 8));
        Assert.assertEquals(msg1, (short) 0x0f54, (short) BitOp.writeMasked(0xfffffffffffffff1L, 0xfe54, 0xf0f, 16, 8));

        //bus:32bits, data:8, 16bits
        Assert.assertEquals(msg1, 0xcdef23f9, (int) BitOp.writeMasked(0x70, 0xcdef2345, 0x1f9, 32, 8));
        Assert.assertEquals(msg1, 0xcdeff845, (int) BitOp.writeMasked(0x71, 0xcdef2345, 0x2f8, 32, 8));
        Assert.assertEquals(msg1, 0xcdf72345, (int) BitOp.writeMasked(0x72, 0xcdef2345, 0x3f7, 32, 8));
        Assert.assertEquals(msg1, 0xf6ef2345, (int) BitOp.writeMasked(0x73, 0xcdef2345, 0x4f6, 32, 8));

        Assert.assertEquals(msg1, 0xcdefe123, (int) BitOp.writeMasked(0x80, 0xcdef2345, 0x5e123, 32, 16));
        Assert.assertEquals(msg1, 0xcdefe124, (int) BitOp.writeMasked(0x81, 0xcdef2345, 0x6e124, 32, 16));
        Assert.assertEquals(msg1, 0xe1252345, (int) BitOp.writeMasked(0x82, 0xcdef2345, 0x7e125, 32, 16));
        Assert.assertEquals(msg1, 0xe1262345, (int) BitOp.writeMasked(0x83, 0xcdef2345, 0x8e126, 32, 16));

        //bus:64bits, data:8, 16, 32bits
        Assert.assertEquals(msg1, 0x89abcdef234567f9L, BitOp.writeMasked(0x90, 0x89abcdef23456789L, 0x11f9, 64, 8));
        Assert.assertEquals(msg1, 0x89abcdef2345f889L, BitOp.writeMasked(0x91, 0x89abcdef23456789L, 0x21f8, 64, 8));
        Assert.assertEquals(msg1, 0x89abcdef23f76789L, BitOp.writeMasked(0x92, 0x89abcdef23456789L, 0x31f7, 64, 8));
        Assert.assertEquals(msg1, 0x89abcdeff6456789L, BitOp.writeMasked(0x93, 0x89abcdef23456789L, 0x41f6, 64, 8));
        Assert.assertEquals(msg1, 0x89abcdf523456789L, BitOp.writeMasked(0x94, 0x89abcdef23456789L, 0x51f5, 64, 8));
        Assert.assertEquals(msg1, 0x89abf4ef23456789L, BitOp.writeMasked(0x95, 0x89abcdef23456789L, 0x61f4, 64, 8));
        Assert.assertEquals(msg1, 0x89f3cdef23456789L, BitOp.writeMasked(0x96, 0x89abcdef23456789L, 0x71f3, 64, 8));
        Assert.assertEquals(msg1, 0xf2abcdef23456789L, BitOp.writeMasked(0x97, 0x89abcdef23456789L, 0x81f2, 64, 8));

        Assert.assertEquals(msg1, 0x89abcdef2345f9e8L, BitOp.writeMasked(0xa0, 0x89abcdef23456789L, 0x12f9e8, 64, 16));
        Assert.assertEquals(msg1, 0x89abcdef2345f9e7L, BitOp.writeMasked(0xa1, 0x89abcdef23456789L, 0x13f9e7, 64, 16));
        Assert.assertEquals(msg1, 0x89abcdeff9e66789L, BitOp.writeMasked(0xa2, 0x89abcdef23456789L, 0x14f9e6, 64, 16));
        Assert.assertEquals(msg1, 0x89abcdeff9e56789L, BitOp.writeMasked(0xa3, 0x89abcdef23456789L, 0x15f9e5, 64, 16));
        Assert.assertEquals(msg1, 0x89abf9e423456789L, BitOp.writeMasked(0xa4, 0x89abcdef23456789L, 0x16f9e4, 64, 16));
        Assert.assertEquals(msg1, 0x89abf9e323456789L, BitOp.writeMasked(0xa5, 0x89abcdef23456789L, 0x17f9e3, 64, 16));
        Assert.assertEquals(msg1, 0xf9e2cdef23456789L, BitOp.writeMasked(0xa6, 0x89abcdef23456789L, 0x18f9e2, 64, 16));
        Assert.assertEquals(msg1, 0xf9e1cdef23456789L, BitOp.writeMasked(0xa7, 0x89abcdef23456789L, 0x19f9e1, 64, 16));

        Assert.assertEquals(msg1, 0x89abcdef98765432L, BitOp.writeMasked(0xb0, 0x89abcdef23456789L, 0x98765432, 64, 32));
        Assert.assertEquals(msg1, 0x89abcdef98765433L, BitOp.writeMasked(0xb1, 0x89abcdef23456789L, 0x98765433, 64, 32));
        Assert.assertEquals(msg1, 0x89abcdef98765434L, BitOp.writeMasked(0xb2, 0x89abcdef23456789L, 0x98765434, 64, 32));
        Assert.assertEquals(msg1, 0x89abcdef98765435L, BitOp.writeMasked(0xb3, 0x89abcdef23456789L, 0x98765435, 64, 32));
        Assert.assertEquals(msg1, 0x9876543623456789L, BitOp.writeMasked(0xb4, 0x89abcdef23456789L, 0x98765436, 64, 32));
        Assert.assertEquals(msg1, 0x9876543723456789L, BitOp.writeMasked(0xb5, 0x89abcdef23456789L, 0x98765437, 64, 32));
        Assert.assertEquals(msg1, 0x9876543823456789L, BitOp.writeMasked(0xb6, 0x89abcdef23456789L, 0x98765438, 64, 32));
        Assert.assertEquals(msg1, 0x9876543923456789L, BitOp.writeMasked(0xb7, 0x89abcdef23456789L, 0x98765439, 64, 32));
    }

    @org.junit.Test
    public void testUnalignedReadMasked() throws Exception {
        String msg1 = "BitOp.unalignedReadMasked() failed.";
        String msg2 = "BitOp.unalignedReadMasked() length check failed.";

        //bus:16bits, data:8bits
        Assert.assertEquals(msg1, (byte) 0x54, (byte) BitOp.unalignedReadMasked(0x0, 0xfe54L, 16, 8));
        Assert.assertEquals(msg1, (byte) 0xfe, (byte) BitOp.unalignedReadMasked(0x1, 0xfe54L, 16, 8));

        Assert.assertEquals(msg1, (byte) 0x54, (byte) BitOp.unalignedReadMasked(0xfffffffffffffff0L, 0xfe54L, 16, 8));
        Assert.assertEquals(msg1, (byte) 0xfe, (byte) BitOp.unalignedReadMasked(0xfffffffffffffff1L, 0xfe54L, 16, 8));

        //bus:32bits, data:8, 16, 24bits
        Assert.assertEquals(msg1, (byte) 0x32, (byte) BitOp.unalignedReadMasked(0x10, 0xfedc5432L, 32, 8));
        Assert.assertEquals(msg1, (byte) 0x54, (byte) BitOp.unalignedReadMasked(0x11, 0xfedc5432L, 32, 8));
        Assert.assertEquals(msg1, (byte) 0xdc, (byte) BitOp.unalignedReadMasked(0x12, 0xfedc5432L, 32, 8));
        Assert.assertEquals(msg1, (byte) 0xfe, (byte) BitOp.unalignedReadMasked(0x13, 0xfedc5432L, 32, 8));

        Assert.assertEquals(msg1, (short) 0x5432, (short) BitOp.unalignedReadMasked(0x20, 0xfedc5432L, 32, 16));
        Assert.assertEquals(msg1, (short) 0xdc54, (short) BitOp.unalignedReadMasked(0x21, 0xfedc5432L, 32, 16));
        Assert.assertEquals(msg1, (short) 0xfedc, (short) BitOp.unalignedReadMasked(0x22, 0xfedc5432L, 32, 16));
        try {
            Assert.assertEquals(msg1, (short) 0x00fe, BitOp.unalignedReadMasked(0x23, 0xfedc5432L, 32, 16));
            Assert.fail(msg2);
        } catch (Exception e) {
            //OK
        }

        Assert.assertEquals(msg1, (short) 0xdc5432, (short) BitOp.unalignedReadMasked(0x30, 0xfedc5432L, 32, 24));
        Assert.assertEquals(msg1, (short) 0xfedc54, (short) BitOp.unalignedReadMasked(0x31, 0xfedc5432L, 32, 24));
        try {
            Assert.assertEquals(msg1, (short) 0x00fedc, (short) BitOp.unalignedReadMasked(0x32, 0xfedc5432L, 32, 24));
            Assert.fail(msg2);
        } catch (Exception e) {
            //OK
        }
        try {
            Assert.assertEquals(msg1, (short) 0x0000fe, BitOp.unalignedReadMasked(0x33, 0xfedc5432L, 32, 24));
            Assert.fail(msg2);
        } catch (Exception e) {
            //OK
        }

        //bus:64bits, data:8, 16, 32, 40bits
        Assert.assertEquals(msg1, (byte) 0x10, (byte) BitOp.unalignedReadMasked(0x30, 0xfedcba9876543210L, 64, 8));
        Assert.assertEquals(msg1, (byte) 0x32, (byte) BitOp.unalignedReadMasked(0x31, 0xfedcba9876543210L, 64, 8));
        Assert.assertEquals(msg1, (byte) 0x54, (byte) BitOp.unalignedReadMasked(0x32, 0xfedcba9876543210L, 64, 8));
        Assert.assertEquals(msg1, (byte) 0x76, (byte) BitOp.unalignedReadMasked(0x33, 0xfedcba9876543210L, 64, 8));
        Assert.assertEquals(msg1, (byte) 0x98, (byte) BitOp.unalignedReadMasked(0x34, 0xfedcba9876543210L, 64, 8));
        Assert.assertEquals(msg1, (byte) 0xba, (byte) BitOp.unalignedReadMasked(0x35, 0xfedcba9876543210L, 64, 8));
        Assert.assertEquals(msg1, (byte) 0xdc, (byte) BitOp.unalignedReadMasked(0x36, 0xfedcba9876543210L, 64, 8));
        Assert.assertEquals(msg1, (byte) 0xfe, (byte) BitOp.unalignedReadMasked(0x37, 0xfedcba9876543210L, 64, 8));

        Assert.assertEquals(msg1, (short) 0x3210, (short) BitOp.unalignedReadMasked(0x40, 0xfedcba9876543210L, 64, 16));
        Assert.assertEquals(msg1, (short) 0x5432, (short) BitOp.unalignedReadMasked(0x41, 0xfedcba9876543210L, 64, 16));
        Assert.assertEquals(msg1, (short) 0x7654, (short) BitOp.unalignedReadMasked(0x42, 0xfedcba9876543210L, 64, 16));
        Assert.assertEquals(msg1, (short) 0x9876, (short) BitOp.unalignedReadMasked(0x43, 0xfedcba9876543210L, 64, 16));
        Assert.assertEquals(msg1, (short) 0xba98, (short) BitOp.unalignedReadMasked(0x44, 0xfedcba9876543210L, 64, 16));
        Assert.assertEquals(msg1, (short) 0xdcba, (short) BitOp.unalignedReadMasked(0x45, 0xfedcba9876543210L, 64, 16));
        Assert.assertEquals(msg1, (short) 0xfedc, (short) BitOp.unalignedReadMasked(0x46, 0xfedcba9876543210L, 64, 16));
        try {
            Assert.assertEquals(msg1, (short) 0x00fe, (short) BitOp.unalignedReadMasked(0x47, 0xfedcba9876543210L, 64, 16));
            Assert.fail(msg2);
        } catch (Exception e) {
            //OK
        }

        Assert.assertEquals(msg1, 0x76543210, (int) BitOp.unalignedReadMasked(0x50, 0xfedcba9876543210L, 64, 32));
        Assert.assertEquals(msg1, 0x98765432, (int) BitOp.unalignedReadMasked(0x51, 0xfedcba9876543210L, 64, 32));
        Assert.assertEquals(msg1, 0xba987654, (int) BitOp.unalignedReadMasked(0x52, 0xfedcba9876543210L, 64, 32));
        Assert.assertEquals(msg1, 0xdcba9876, (int) BitOp.unalignedReadMasked(0x53, 0xfedcba9876543210L, 64, 32));
        Assert.assertEquals(msg1, 0xfedcba98, (int) BitOp.unalignedReadMasked(0x54, 0xfedcba9876543210L, 64, 32));
        try {
            Assert.assertEquals(msg1, 0x00fedcba, (int) BitOp.unalignedReadMasked(0x55, 0xfedcba9876543210L, 64, 32));
            Assert.fail(msg2);
        } catch (Exception e) {
            //OK
        }
        try {
            Assert.assertEquals(msg1, 0x0000fedc, (int) BitOp.unalignedReadMasked(0x56, 0xfedcba9876543210L, 64, 32));
            Assert.fail(msg2);
        } catch (Exception e) {
            //OK
        }
        try {
            Assert.assertEquals(msg1, 0x000000fe, (int) BitOp.unalignedReadMasked(0x57, 0xfedcba9876543210L, 64, 32));
            Assert.fail(msg2);
        } catch (Exception e) {
            //OK
        }

        Assert.assertEquals(msg1, 0x9876543210L, BitOp.unalignedReadMasked(0x60, 0xfedcba9876543210L, 64, 40));
        Assert.assertEquals(msg1, 0xba98765432L, BitOp.unalignedReadMasked(0x61, 0xfedcba9876543210L, 64, 40));
        Assert.assertEquals(msg1, 0xdcba987654L, BitOp.unalignedReadMasked(0x62, 0xfedcba9876543210L, 64, 40));
        Assert.assertEquals(msg1, 0xfedcba9876L, BitOp.unalignedReadMasked(0x63, 0xfedcba9876543210L, 64, 40));
        try {
            Assert.assertEquals(msg1, 0x00fedcba98L, BitOp.unalignedReadMasked(0x64, 0xfedcba9876543210L, 64, 40));
            Assert.fail(msg2);
        } catch (Exception e) {
            //OK
        }
        try {
            Assert.assertEquals(msg1, 0x0000dcba, (int) BitOp.unalignedReadMasked(0x65, 0xfedcba9876543210L, 64, 40));
            Assert.fail(msg2);
        } catch (Exception e) {
            //OK
        }
        try {
            Assert.assertEquals(msg1, 0x000000fe, (int) BitOp.unalignedReadMasked(0x66, 0xfedcba9876543210L, 64, 40));
            Assert.fail(msg2);
        } catch (Exception e) {
            //OK
        }
    }

    @org.junit.Test
    public void testUnalignedWriteMasked() throws Exception {
        String msg1 = "BitOp.unalignedWriteMasked() failed.";
        String msg2 = "BitOp.unalignedWriteMasked() length check failed.";

        //bus:8bits, data:8bits
        Assert.assertEquals(msg1, (byte) 0xf0, (byte) BitOp.unalignedWriteMasked(0x60, 0xfe, 0xf0, 8, 8));
        Assert.assertEquals(msg1, (byte) 0xf2, (byte) BitOp.unalignedWriteMasked(0x61, 0x54, 0xf2, 8, 8));

        Assert.assertEquals(msg1, (byte) 0xf4, (byte) BitOp.unalignedWriteMasked(0xfffffffffffffff0L, 0xfe, 0xf4, 8, 8));
        Assert.assertEquals(msg1, (byte) 0xf6, (byte) BitOp.unalignedWriteMasked(0xfffffffffffffff1L, 0x54, 0xf6, 8, 8));

        //bus:16bits, data:8bits
        Assert.assertEquals(msg1, (short) 0xfef0, (short) BitOp.unalignedWriteMasked(0x60, 0xfe54, 0x1f0, 16, 8));
        Assert.assertEquals(msg1, (short) 0xf054, (short) BitOp.unalignedWriteMasked(0x61, 0xfe54, 0x2f0, 16, 8));

        Assert.assertEquals(msg1, (short) 0xfe0f, (short) BitOp.unalignedWriteMasked(0xfffffffffffffff0L, 0xfe54, 0xe0f, 16, 8));
        Assert.assertEquals(msg1, (short) 0x0f54, (short) BitOp.unalignedWriteMasked(0xfffffffffffffff1L, 0xfe54, 0xf0f, 16, 8));

        //bus:32bits, data:8, 16, 24bits
        Assert.assertEquals(msg1, 0xcdef23f9, (int) BitOp.unalignedWriteMasked(0x70, 0xcdef2345, 0x1f9, 32, 8));
        Assert.assertEquals(msg1, 0xcdeff845, (int) BitOp.unalignedWriteMasked(0x71, 0xcdef2345, 0x2f8, 32, 8));
        Assert.assertEquals(msg1, 0xcdf72345, (int) BitOp.unalignedWriteMasked(0x72, 0xcdef2345, 0x3f7, 32, 8));
        Assert.assertEquals(msg1, 0xf6ef2345, (int) BitOp.unalignedWriteMasked(0x73, 0xcdef2345, 0x4f6, 32, 8));

        Assert.assertEquals(msg1, 0xcdefe123, (int) BitOp.unalignedWriteMasked(0x80, 0xcdef2345, 0x5e123, 32, 16));
        Assert.assertEquals(msg1, 0xcde12445, (int) BitOp.unalignedWriteMasked(0x81, 0xcdef2345, 0x6e124, 32, 16));
        Assert.assertEquals(msg1, 0xe1252345, (int) BitOp.unalignedWriteMasked(0x82, 0xcdef2345, 0x7e125, 32, 16));
        try {
            Assert.assertEquals(msg1, 0x26ef2345, (int) BitOp.unalignedWriteMasked(0x83, 0xcdef2345, 0x8e126, 32, 16));
            Assert.fail(msg2);
        } catch (Exception e) {
            //OK
        }

        Assert.assertEquals(msg1, 0xcdf12342, (int) BitOp.unalignedWriteMasked(0x80, 0xcdef2345, 0xaf12342, 32, 24));
        Assert.assertEquals(msg1, 0xf1234345, (int) BitOp.unalignedWriteMasked(0x81, 0xcdef2345, 0xbf12343, 32, 24));
        try {
            Assert.assertEquals(msg1, 0x23442345, (int) BitOp.unalignedWriteMasked(0x82, 0xcdef2345, 0xcf12344, 32, 24));
            Assert.fail(msg2);
        } catch (Exception e) {
            //OK
        }
        try {
            Assert.assertEquals(msg1, 0x45ef2345, (int) BitOp.unalignedWriteMasked(0x83, 0xcdef2345, 0xdf12345, 32, 24));
            Assert.fail(msg2);
        } catch (Exception e) {
            //OK
        }

        //bus:64bits, data:8, 16, 32, 40bits
        Assert.assertEquals(msg1, 0x89abcdef234567f9L, BitOp.unalignedWriteMasked(0x90, 0x89abcdef23456789L, 0x11f9, 64, 8));
        Assert.assertEquals(msg1, 0x89abcdef2345f889L, BitOp.unalignedWriteMasked(0x91, 0x89abcdef23456789L, 0x21f8, 64, 8));
        Assert.assertEquals(msg1, 0x89abcdef23f76789L, BitOp.unalignedWriteMasked(0x92, 0x89abcdef23456789L, 0x31f7, 64, 8));
        Assert.assertEquals(msg1, 0x89abcdeff6456789L, BitOp.unalignedWriteMasked(0x93, 0x89abcdef23456789L, 0x41f6, 64, 8));
        Assert.assertEquals(msg1, 0x89abcdf523456789L, BitOp.unalignedWriteMasked(0x94, 0x89abcdef23456789L, 0x51f5, 64, 8));
        Assert.assertEquals(msg1, 0x89abf4ef23456789L, BitOp.unalignedWriteMasked(0x95, 0x89abcdef23456789L, 0x61f4, 64, 8));
        Assert.assertEquals(msg1, 0x89f3cdef23456789L, BitOp.unalignedWriteMasked(0x96, 0x89abcdef23456789L, 0x71f3, 64, 8));
        Assert.assertEquals(msg1, 0xf2abcdef23456789L, BitOp.unalignedWriteMasked(0x97, 0x89abcdef23456789L, 0x81f2, 64, 8));

        Assert.assertEquals(msg1, 0x89abcdef2345f9e8L, BitOp.unalignedWriteMasked(0xa0, 0x89abcdef23456789L, 0x12f9e8, 64, 16));
        Assert.assertEquals(msg1, 0x89abcdef23f9e789L, BitOp.unalignedWriteMasked(0xa1, 0x89abcdef23456789L, 0x13f9e7, 64, 16));
        Assert.assertEquals(msg1, 0x89abcdeff9e66789L, BitOp.unalignedWriteMasked(0xa2, 0x89abcdef23456789L, 0x14f9e6, 64, 16));
        Assert.assertEquals(msg1, 0x89abcdf9e5456789L, BitOp.unalignedWriteMasked(0xa3, 0x89abcdef23456789L, 0x15f9e5, 64, 16));
        Assert.assertEquals(msg1, 0x89abf9e423456789L, BitOp.unalignedWriteMasked(0xa4, 0x89abcdef23456789L, 0x16f9e4, 64, 16));
        Assert.assertEquals(msg1, 0x89f9e3ef23456789L, BitOp.unalignedWriteMasked(0xa5, 0x89abcdef23456789L, 0x17f9e3, 64, 16));
        Assert.assertEquals(msg1, 0xf9e2cdef23456789L, BitOp.unalignedWriteMasked(0xa6, 0x89abcdef23456789L, 0x18f9e2, 64, 16));
        try {
            Assert.assertEquals(msg1, 0xe1abcdef23456789L, BitOp.unalignedWriteMasked(0xa7, 0x89abcdef23456789L, 0x19f9e1, 64, 16));
            Assert.fail(msg2);
        } catch (Exception e) {
            //OK
        }

        Assert.assertEquals(msg1, 0x89abcdef98765432L, BitOp.unalignedWriteMasked(0xb0, 0x89abcdef23456789L, 0x298765432L, 64, 32));
        Assert.assertEquals(msg1, 0x89abcd9876543389L, BitOp.unalignedWriteMasked(0xb1, 0x89abcdef23456789L, 0x298765433L, 64, 32));
        Assert.assertEquals(msg1, 0x89ab987654346789L, BitOp.unalignedWriteMasked(0xb2, 0x89abcdef23456789L, 0x298765434L, 64, 32));
        Assert.assertEquals(msg1, 0x8998765435456789L, BitOp.unalignedWriteMasked(0xb3, 0x89abcdef23456789L, 0x298765435L, 64, 32));
        Assert.assertEquals(msg1, 0x9876543623456789L, BitOp.unalignedWriteMasked(0xb4, 0x89abcdef23456789L, 0x298765436L, 64, 32));
        try {
            Assert.assertEquals(msg1, 0x765437ef23456789L, BitOp.unalignedWriteMasked(0xb5, 0x89abcdef23456789L, 0x298765437L, 64, 32));
            Assert.fail(msg2);
        } catch (Exception e) {
            //OK
        }
        try {
            Assert.assertEquals(msg1, 0x5438cdef23456789L, BitOp.unalignedWriteMasked(0xb6, 0x89abcdef23456789L, 0x298765438L, 64, 32));
            Assert.fail(msg2);
        } catch (Exception e) {
            //OK
        }
        try {
            Assert.assertEquals(msg1, 0x39abcdef23456789L, BitOp.unalignedWriteMasked(0xb7, 0x89abcdef23456789L, 0x298765439L, 64, 32));
            Assert.fail(msg2);
        } catch (Exception e) {
            //OK
        }

        Assert.assertEquals(msg1, 0x89abcdba98765431L, BitOp.unalignedWriteMasked(0xb0, 0x89abcdef23456789L, 0x3ba98765431L, 64, 40));
        Assert.assertEquals(msg1, 0x89abba9876543289L, BitOp.unalignedWriteMasked(0xb1, 0x89abcdef23456789L, 0x3ba98765432L, 64, 40));
        Assert.assertEquals(msg1, 0x89ba987654336789L, BitOp.unalignedWriteMasked(0xb2, 0x89abcdef23456789L, 0x3ba98765433L, 64, 40));
        Assert.assertEquals(msg1, 0xba98765434456789L, BitOp.unalignedWriteMasked(0xb3, 0x89abcdef23456789L, 0x3ba98765434L, 64, 40));
        try {
            Assert.assertEquals(msg1, 0x9876543523456789L, BitOp.unalignedWriteMasked(0xb4, 0x89abcdef23456789L, 0x3ba98765435L, 64, 40));
            Assert.fail(msg2);
        } catch (Exception e) {
            //OK
        }
        try {
            Assert.assertEquals(msg1, 0x765436ef23456789L, BitOp.unalignedWriteMasked(0xb5, 0x89abcdef23456789L, 0x3ba98765436L, 64, 40));
            Assert.fail(msg2);
        } catch (Exception e) {
            //OK
        }
        try {
            Assert.assertEquals(msg1, 0x5437cdef23456789L, BitOp.unalignedWriteMasked(0xb6, 0x89abcdef23456789L, 0x3ba98765437L, 64, 40));
            Assert.fail(msg2);
        } catch (Exception e) {
            //OK
        }
        try {
            Assert.assertEquals(msg1, 0x38abcdef23456789L, BitOp.unalignedWriteMasked(0xb7, 0x89abcdef23456789L, 0x3ba98765438L, 64, 40));
            Assert.fail(msg2);
        } catch (Exception e) {
            //OK
        }
    }
}