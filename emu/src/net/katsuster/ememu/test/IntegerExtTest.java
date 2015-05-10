package net.katsuster.ememu.test;

import net.katsuster.ememu.generic.IntegerExt;
import org.junit.*;

public class IntegerExtTest {
    @org.junit.Test
    public void testCarryFrom() throws Exception {
        String msg1 = "CPU.carryFrom() failed.";

        Assert.assertEquals(msg1, true, IntegerExt.carryFrom(1, 0xffffffff));
        Assert.assertEquals(msg1, true, IntegerExt.carryFrom(2, 0xffffffff));
        Assert.assertEquals(msg1, true, IntegerExt.carryFrom(2, 0xfffffffe));
        Assert.assertEquals(msg1, true, IntegerExt.carryFrom(0xffffffff, 0xffffffff));
        Assert.assertEquals(msg1, true, IntegerExt.carryFrom(0x80000000, 0x80000000));

        Assert.assertEquals(msg1, false, IntegerExt.carryFrom(0, 0));
        Assert.assertEquals(msg1, false, IntegerExt.carryFrom(0, 1));
        Assert.assertEquals(msg1, false, IntegerExt.carryFrom(1, 0));
        Assert.assertEquals(msg1, false, IntegerExt.carryFrom(0xffffffff, 0));
        Assert.assertEquals(msg1, false, IntegerExt.carryFrom(0, 0xffffffff));
    }

    @org.junit.Test
    public void testBorrowFrom() throws Exception {
        String msg1 = "CPU.borrowFrom() failed.";

        Assert.assertEquals(msg1, true, IntegerExt.borrowFrom(0, 1));
        Assert.assertEquals(msg1, true, IntegerExt.borrowFrom(0, 2));
        Assert.assertEquals(msg1, true, IntegerExt.borrowFrom(0, 0x80000000));
        Assert.assertEquals(msg1, true, IntegerExt.borrowFrom(0x80000000, 0x80000001));
        Assert.assertEquals(msg1, true, IntegerExt.borrowFrom(0x7ffffffe, 0x7fffffff));

        Assert.assertEquals(msg1, false, IntegerExt.borrowFrom(0, 0));
        Assert.assertEquals(msg1, false, IntegerExt.borrowFrom(1, 1));
        Assert.assertEquals(msg1, false, IntegerExt.borrowFrom(0xffffffff, 0x7fffffff));
        Assert.assertEquals(msg1, false, IntegerExt.borrowFrom(0x80000000, 0x80000000));
        Assert.assertEquals(msg1, false, IntegerExt.borrowFrom(0x80000001, 0x80000000));
        Assert.assertEquals(msg1, false, IntegerExt.borrowFrom(0x80000000, 0x7fffffff));
        Assert.assertEquals(msg1, false, IntegerExt.borrowFrom(0x7fffffff, 0x7fffffff));
        Assert.assertEquals(msg1, false, IntegerExt.borrowFrom(0x7fffffff, 0x7ffffffe));
    }

    @org.junit.Test
    public void testOverflowFrom() throws Exception {
        String msg1 = "CPU.overflowFrom() failed.";

        //‰ÁŽZ
        Assert.assertEquals(msg1, true, IntegerExt.overflowFrom(0x40000000, 0x40000000, true));
        Assert.assertEquals(msg1, true, IntegerExt.overflowFrom(0x7fffffff, 1, true));
        Assert.assertEquals(msg1, true, IntegerExt.overflowFrom(0x7fffffff, 0x7fffffff, true));
        Assert.assertEquals(msg1, true, IntegerExt.overflowFrom(0x80000000, 0x80000001, true));

        Assert.assertEquals(msg1, false, IntegerExt.overflowFrom(0, 0xffffffff, true));
        Assert.assertEquals(msg1, false, IntegerExt.overflowFrom(0xffffffff, 0, true));

        Assert.assertEquals(msg1, false, IntegerExt.overflowFrom(1000, 1000, true));
        Assert.assertEquals(msg1, false, IntegerExt.overflowFrom(1000, -1000, true));
        Assert.assertEquals(msg1, false, IntegerExt.overflowFrom(-1000, 1000, true));
        Assert.assertEquals(msg1, false, IntegerExt.overflowFrom(-1000, -1000, true));

        Assert.assertEquals(msg1, false, IntegerExt.overflowFrom(10, 100, true));
        Assert.assertEquals(msg1, false, IntegerExt.overflowFrom(0x80000000, 0x7fffffff, true));
        Assert.assertEquals(msg1, false, IntegerExt.overflowFrom(0x7fffffff, 0x8fffffff, true));

        //Œ¸ŽZ
        Assert.assertEquals(msg1, true, IntegerExt.overflowFrom(0x7fffffff, 0x80000000, false));
        Assert.assertEquals(msg1, true, IntegerExt.overflowFrom(0x8fffffff, 0x7fffffff, false));
        Assert.assertEquals(msg1, true, IntegerExt.overflowFrom(0x80000000, 1, false));

        Assert.assertEquals(msg1, false, IntegerExt.overflowFrom(0, 0xffffffff, false));
        Assert.assertEquals(msg1, false, IntegerExt.overflowFrom(0xffffffff, 0, false));

        Assert.assertEquals(msg1, false, IntegerExt.overflowFrom(1000, 1000, false));
        Assert.assertEquals(msg1, false, IntegerExt.overflowFrom(1000, -1000, false));
        Assert.assertEquals(msg1, false, IntegerExt.overflowFrom(-1000, 1000, false));
        Assert.assertEquals(msg1, false, IntegerExt.overflowFrom(-1000, -1000, false));

        Assert.assertEquals(msg1, false, IntegerExt.overflowFrom(10, 100, false));
        Assert.assertEquals(msg1, false, IntegerExt.overflowFrom(100, 10, false));
        Assert.assertEquals(msg1, false, IntegerExt.overflowFrom(-10, 100, false));
        Assert.assertEquals(msg1, false, IntegerExt.overflowFrom(10, -100, false));
        Assert.assertEquals(msg1, false, IntegerExt.overflowFrom(-10, -100, false));
        Assert.assertEquals(msg1, false, IntegerExt.overflowFrom(-10, -100, false));
    }
}
