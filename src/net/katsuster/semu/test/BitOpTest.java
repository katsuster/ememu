package net.katsuster.semu.test;

import net.katsuster.semu.BitOp;

import static org.junit.Assert.assertEquals;

public class BitOpTest {
    @org.junit.Test
    public void testSetField32() throws Exception {
        String msg1 = "BitOp.setField32() failed.";

        //0bit
        assertEquals(msg1, 0x00000000, BitOp.setField32(0x00000000, 0, 0, 0x0000));
        assertEquals(msg1, 0x00000000, BitOp.setField32(0x00000000, 0, 0, 0xffff));
        assertEquals(msg1, 0xffffffff, BitOp.setField32(0xffffffff, 0, 0, 0x0000));
        assertEquals(msg1, 0xffffffff, BitOp.setField32(0xffffffff, 0, 0, 0xffff));
        assertEquals(msg1, 0x00000000, BitOp.setField32(0x00000000, 8, 0, 0x0000));
        assertEquals(msg1, 0x00000000, BitOp.setField32(0x00000000, 8, 0, 0xffff));
        assertEquals(msg1, 0xffffffff, BitOp.setField32(0xffffffff, 8, 0, 0x0000));
        assertEquals(msg1, 0xffffffff, BitOp.setField32(0xffffffff, 8, 0, 0xffff));

        //1bit
        assertEquals(msg1, 0x00000001, BitOp.setField32(0x00000000, 0, 1, 0x1));
        assertEquals(msg1, 0x80000000, BitOp.setField32(0x00000000, 31, 1, 0x1));
        assertEquals(msg1, 0x70008000, BitOp.setField32(0x70000000, 15, 1, 0x1));
        assertEquals(msg1, 0x70010000, BitOp.setField32(0x70000000, 16, 1, 0x1));
        assertEquals(msg1, 0x80000000, BitOp.setField32(0x80000001, 0, 1, 0x0));
        assertEquals(msg1, 0x00000001, BitOp.setField32(0x80000001, 31, 1, 0x0));

        //5bits
        assertEquals(msg1, 0x00002110, BitOp.setField32(0x00002100, 0, 5, 0x10));
        assertEquals(msg1, 0x00004300, BitOp.setField32(0x00004300, 0, 5, 0x100));
        assertEquals(msg1, 0x80de4d00, BitOp.setField32(0x80de2100, 10, 5, 0xf3));
        assertEquals(msg1, 0x708b4300, BitOp.setField32(0x70cf4300, 18, 5, 0xe2));
        assertEquals(msg1, 0x44be6500, BitOp.setField32(0x60be6500, 26, 5, 0xd1));
        assertEquals(msg1, 0x07ad8700, BitOp.setField32(0x5fad8700, 27, 5, 0xc0));

        //32bits
        assertEquals(msg1, 0x00000001, BitOp.setField32(0x00005600, 0, 32, 0x00000001));
        assertEquals(msg1, 0x80000001, BitOp.setField32(0x00340000, 0, 32, 0x80000001));
        assertEquals(msg1, 0xff4321ff, BitOp.setField32(0x12000000, 0, 32, 0xff4321ff));
    }
}
