package net.katsuster.ememu.test;

import net.katsuster.ememu.generic.IntegerExt;
import org.junit.*;

public class IntegerExtTest {
    @org.junit.Test
    public void testCarryFrom() throws Exception {
        String msg1 = "IntegerExt.carryFrom() failed.";

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
        String msg1 = "IntegerExt.borrowFrom() failed.";

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
        String msg1 = "IntegerExt.overflowFrom() failed.";

        //加算
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

        //減算
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

    @org.junit.Test
    public void testCompareUnsigned() throws Exception {
        String msg1 = "IntegerExt.compareUnsigned() failed.";

        int vz0_1 = 0;
        int vz0_2 = 0;
        int vp1_1 = 1;
        int vp1_2 = 1;
        int vp2_1 = 2;
        int vp2_2 = 2;
        int vm1_1 = -1;
        int vm1_2 = -1;
        int vm2_1 = -2;
        int vm2_2 = -2;
        int vh1_1 = 0x7ffffffe;
        int vh1_2 = 0x7ffffffe;
        int vh2_1 = 0x7fffffff;
        int vh2_2 = 0x7fffffff;
        int vh3_1 = 0x80000000;
        int vh3_2 = 0x80000000;
        int vh4_1 = 0x80000001;
        int vh4_2 = 0x80000001;

        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vz0_1, vz0_1) == 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vp1_1, vp1_1) == 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vp2_1, vp2_1) == 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vm1_1, vm1_1) == 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vm2_1, vm2_1) == 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vh1_1, vh1_1) == 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vh2_1, vh2_1) == 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vh3_1, vh3_1) == 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vh4_1, vh4_1) == 0);

        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vz0_1, vz0_2) == 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vp1_1, vp1_2) == 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vp2_1, vp2_2) == 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vm1_1, vm1_2) == 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vm2_1, vm2_2) == 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vh1_1, vh1_2) == 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vh2_1, vh2_2) == 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vh3_1, vh3_2) == 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vh4_1, vh4_2) == 0);

        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vz0_1, vh4_2) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vp1_1, vz0_2) > 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vp2_1, vp1_2) > 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vm1_1, vp2_2) > 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vm2_1, vm1_2) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vh1_1, vm2_2) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vh2_1, vh1_2) > 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vh3_1, vh2_2) > 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vh4_1, vh3_2) > 0);

        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vz0_1, vp1_2) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vp1_1, vz0_2) > 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vm1_1, vz0_2) > 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUnsigned(vz0_1, vm1_2) < 0);
    }
}
