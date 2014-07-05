package net.katsuster.semu.test;

import net.katsuster.semu.CPU;

import static org.junit.Assert.assertEquals;

public class CPUTest {
    @org.junit.Test
    public void testCarryFrom() throws Exception {
        String msg1 = "CPU.carryFrom() failed.";

        assertEquals(msg1, true, CPU.carryFrom(1, 0xffffffff));
        assertEquals(msg1, true, CPU.carryFrom(2, 0xffffffff));
        assertEquals(msg1, true, CPU.carryFrom(2, 0xfffffffe));
        assertEquals(msg1, true, CPU.carryFrom(0xffffffff, 0xffffffff));
        assertEquals(msg1, true, CPU.carryFrom(0x80000000, 0x80000000));

        assertEquals(msg1, false, CPU.carryFrom(0, 0));
        assertEquals(msg1, false, CPU.carryFrom(0, 1));
        assertEquals(msg1, false, CPU.carryFrom(1, 0));
        assertEquals(msg1, false, CPU.carryFrom(0xffffffff, 0));
        assertEquals(msg1, false, CPU.carryFrom(0, 0xffffffff));
    }

    @org.junit.Test
    public void testBorrowFrom() throws Exception {
        String msg1 = "CPU.borrowFrom() failed.";

        assertEquals(msg1, true, CPU.borrowFrom(0, 1));
        assertEquals(msg1, true, CPU.borrowFrom(0, 2));
        assertEquals(msg1, true, CPU.borrowFrom(0, 0x80000000));
        assertEquals(msg1, true, CPU.borrowFrom(0x80000000, 0x80000001));
        assertEquals(msg1, true, CPU.borrowFrom(0x7ffffffe, 0x7fffffff));

        assertEquals(msg1, false, CPU.borrowFrom(0, 0));
        assertEquals(msg1, false, CPU.borrowFrom(1, 1));
        assertEquals(msg1, false, CPU.borrowFrom(0xffffffff, 0x7fffffff));
        assertEquals(msg1, false, CPU.borrowFrom(0x80000000, 0x80000000));
        assertEquals(msg1, false, CPU.borrowFrom(0x80000001, 0x80000000));
        assertEquals(msg1, false, CPU.borrowFrom(0x80000000, 0x7fffffff));
        assertEquals(msg1, false, CPU.borrowFrom(0x7fffffff, 0x7fffffff));
        assertEquals(msg1, false, CPU.borrowFrom(0x7fffffff, 0x7ffffffe));
    }

    @org.junit.Test
    public void testOverflowFrom() throws Exception {
        String msg1 = "CPU.overflowFrom() failed.";

        //加算
        assertEquals(msg1, true, CPU.overflowFrom(0x40000000, 0x40000000, true));
        assertEquals(msg1, true, CPU.overflowFrom(0x7fffffff, 1, true));
        assertEquals(msg1, true, CPU.overflowFrom(0x7fffffff, 0x7fffffff, true));
        assertEquals(msg1, true, CPU.overflowFrom(0x80000000, 0x80000001, true));

        assertEquals(msg1, false, CPU.overflowFrom(0, 0xffffffff, true));
        assertEquals(msg1, false, CPU.overflowFrom(0xffffffff, 0, true));

        assertEquals(msg1, false, CPU.overflowFrom(1000, 1000, true));
        assertEquals(msg1, false, CPU.overflowFrom(1000, -1000, true));
        assertEquals(msg1, false, CPU.overflowFrom(-1000, 1000, true));
        assertEquals(msg1, false, CPU.overflowFrom(-1000, -1000, true));

        assertEquals(msg1, false, CPU.overflowFrom(10, 100, true));
        assertEquals(msg1, false, CPU.overflowFrom(0x80000000, 0x7fffffff, true));
        assertEquals(msg1, false, CPU.overflowFrom(0x7fffffff, 0x8fffffff, true));

        //減算
        assertEquals(msg1, true, CPU.overflowFrom(0x7fffffff, 0x80000000, false));
        assertEquals(msg1, true, CPU.overflowFrom(0x8fffffff, 0x7fffffff, false));
        assertEquals(msg1, true, CPU.overflowFrom(0x80000000, 1, false));

        assertEquals(msg1, false, CPU.overflowFrom(0, 0xffffffff, false));
        assertEquals(msg1, false, CPU.overflowFrom(0xffffffff, 0, false));

        assertEquals(msg1, false, CPU.overflowFrom(1000, 1000, false));
        assertEquals(msg1, false, CPU.overflowFrom(1000, -1000, false));
        assertEquals(msg1, false, CPU.overflowFrom(-1000, 1000, false));
        assertEquals(msg1, false, CPU.overflowFrom(-1000, -1000, false));

        assertEquals(msg1, false, CPU.overflowFrom(10, 100, false));
        assertEquals(msg1, false, CPU.overflowFrom(100, 10, false));
        assertEquals(msg1, false, CPU.overflowFrom(-10, 100, false));
        assertEquals(msg1, false, CPU.overflowFrom(10, -100, false));
        assertEquals(msg1, false, CPU.overflowFrom(-10, -100, false));
        assertEquals(msg1, false, CPU.overflowFrom(-10, -100, false));
    }
}
