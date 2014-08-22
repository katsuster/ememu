package net.katsuster.semu.arm.test;

import org.junit.*;

import net.katsuster.semu.arm.*;

public class CPUTest {
    @org.junit.Test
    public void testCarryFrom32() throws Exception {
        String msg1 = "CPU.carryFrom32() failed.";

        Assert.assertEquals(msg1, true, CPU.carryFrom32(1, 0xffffffff));
        Assert.assertEquals(msg1, true, CPU.carryFrom32(2, 0xffffffff));
        Assert.assertEquals(msg1, true, CPU.carryFrom32(2, 0xfffffffe));
        Assert.assertEquals(msg1, true, CPU.carryFrom32(0xffffffff, 0xffffffff));
        Assert.assertEquals(msg1, true, CPU.carryFrom32(0x80000000, 0x80000000));

        Assert.assertEquals(msg1, false, CPU.carryFrom32(0, 0));
        Assert.assertEquals(msg1, false, CPU.carryFrom32(0, 1));
        Assert.assertEquals(msg1, false, CPU.carryFrom32(1, 0));
        Assert.assertEquals(msg1, false, CPU.carryFrom32(0xffffffff, 0));
        Assert.assertEquals(msg1, false, CPU.carryFrom32(0, 0xffffffff));
    }

    @org.junit.Test
    public void testBorrowFrom32() throws Exception {
        String msg1 = "CPU.borrowFrom32() failed.";

        Assert.assertEquals(msg1, true, CPU.borrowFrom32(0, 1));
        Assert.assertEquals(msg1, true, CPU.borrowFrom32(0, 2));
        Assert.assertEquals(msg1, true, CPU.borrowFrom32(0, 0x80000000));
        Assert.assertEquals(msg1, true, CPU.borrowFrom32(0x80000000, 0x80000001));
        Assert.assertEquals(msg1, true, CPU.borrowFrom32(0x7ffffffe, 0x7fffffff));

        Assert.assertEquals(msg1, false, CPU.borrowFrom32(0, 0));
        Assert.assertEquals(msg1, false, CPU.borrowFrom32(1, 1));
        Assert.assertEquals(msg1, false, CPU.borrowFrom32(0xffffffff, 0x7fffffff));
        Assert.assertEquals(msg1, false, CPU.borrowFrom32(0x80000000, 0x80000000));
        Assert.assertEquals(msg1, false, CPU.borrowFrom32(0x80000001, 0x80000000));
        Assert.assertEquals(msg1, false, CPU.borrowFrom32(0x80000000, 0x7fffffff));
        Assert.assertEquals(msg1, false, CPU.borrowFrom32(0x7fffffff, 0x7fffffff));
        Assert.assertEquals(msg1, false, CPU.borrowFrom32(0x7fffffff, 0x7ffffffe));
    }

    @org.junit.Test
    public void testOverflowFrom32() throws Exception {
        String msg1 = "CPU.overflowFrom32() failed.";

        //加算
        Assert.assertEquals(msg1, true, CPU.overflowFrom32(0x40000000, 0x40000000, true));
        Assert.assertEquals(msg1, true, CPU.overflowFrom32(0x7fffffff, 1, true));
        Assert.assertEquals(msg1, true, CPU.overflowFrom32(0x7fffffff, 0x7fffffff, true));
        Assert.assertEquals(msg1, true, CPU.overflowFrom32(0x80000000, 0x80000001, true));

        Assert.assertEquals(msg1, false, CPU.overflowFrom32(0, 0xffffffff, true));
        Assert.assertEquals(msg1, false, CPU.overflowFrom32(0xffffffff, 0, true));

        Assert.assertEquals(msg1, false, CPU.overflowFrom32(1000, 1000, true));
        Assert.assertEquals(msg1, false, CPU.overflowFrom32(1000, -1000, true));
        Assert.assertEquals(msg1, false, CPU.overflowFrom32(-1000, 1000, true));
        Assert.assertEquals(msg1, false, CPU.overflowFrom32(-1000, -1000, true));

        Assert.assertEquals(msg1, false, CPU.overflowFrom32(10, 100, true));
        Assert.assertEquals(msg1, false, CPU.overflowFrom32(0x80000000, 0x7fffffff, true));
        Assert.assertEquals(msg1, false, CPU.overflowFrom32(0x7fffffff, 0x8fffffff, true));

        //減算
        Assert.assertEquals(msg1, true, CPU.overflowFrom32(0x7fffffff, 0x80000000, false));
        Assert.assertEquals(msg1, true, CPU.overflowFrom32(0x8fffffff, 0x7fffffff, false));
        Assert.assertEquals(msg1, true, CPU.overflowFrom32(0x80000000, 1, false));

        Assert.assertEquals(msg1, false, CPU.overflowFrom32(0, 0xffffffff, false));
        Assert.assertEquals(msg1, false, CPU.overflowFrom32(0xffffffff, 0, false));

        Assert.assertEquals(msg1, false, CPU.overflowFrom32(1000, 1000, false));
        Assert.assertEquals(msg1, false, CPU.overflowFrom32(1000, -1000, false));
        Assert.assertEquals(msg1, false, CPU.overflowFrom32(-1000, 1000, false));
        Assert.assertEquals(msg1, false, CPU.overflowFrom32(-1000, -1000, false));

        Assert.assertEquals(msg1, false, CPU.overflowFrom32(10, 100, false));
        Assert.assertEquals(msg1, false, CPU.overflowFrom32(100, 10, false));
        Assert.assertEquals(msg1, false, CPU.overflowFrom32(-10, 100, false));
        Assert.assertEquals(msg1, false, CPU.overflowFrom32(10, -100, false));
        Assert.assertEquals(msg1, false, CPU.overflowFrom32(-10, -100, false));
        Assert.assertEquals(msg1, false, CPU.overflowFrom32(-10, -100, false));
    }
}
