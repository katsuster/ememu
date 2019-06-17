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
}