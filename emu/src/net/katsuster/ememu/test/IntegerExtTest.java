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
    public void testCompareUint32() throws Exception {
        String msg1 = "IntegerExt.compareUint32() failed.";

        int vz0 = 0;
        int vp1 = 1;
        int vp2 = 2;
        int vh1 = 0x7ffffffe;
        int vh2 = 0x7fffffff;
        int vh3 = 0x80000000;
        int vh4 = 0x80000001;
        int vm2 = -2;
        int vm1 = -1;

        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vz0, vz0) == 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vp1, vp1) == 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vp2, vp2) == 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vh1, vh1) == 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vh2, vh2) == 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vh3, vh3) == 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vh4, vh4) == 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vm2, vm2) == 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vm1, vm1) == 0);

        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vz0, vp1) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vz0, vp2) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vz0, vh1) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vz0, vh2) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vz0, vh3) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vz0, vh4) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vz0, vm2) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vz0, vm1) < 0);

        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vp1, vp2) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vp1, vh1) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vp1, vh2) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vp1, vh3) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vp1, vh4) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vp1, vm2) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vp1, vm1) < 0);

        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vp2, vh1) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vp2, vh2) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vp2, vh3) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vp2, vh4) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vp2, vm2) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vp2, vm1) < 0);

        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vh1, vh2) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vh1, vh3) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vh1, vh4) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vh1, vm2) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vh1, vm1) < 0);

        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vh2, vh3) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vh2, vh4) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vh2, vm2) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vh2, vm1) < 0);

        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vh3, vh4) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vh3, vm2) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vh3, vm1) < 0);

        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vh4, vm2) < 0);
        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vh4, vm1) < 0);

        Assert.assertEquals(msg1, true, IntegerExt.compareUint32(vm2, vm1) < 0);
    }
}
