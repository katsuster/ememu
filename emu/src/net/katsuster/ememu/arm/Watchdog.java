package net.katsuster.ememu.arm;

import net.katsuster.ememu.generic.*;

/**
 * Watchdog
 *
 * 参考: ARM Watchdog Module (SP805)
 * ARM DDI0270B
 *
 * @author katsuhiro
 */
public class Watchdog implements BusSlave {
    private WatchdogSlave slave;

    public static final int REG_WdogLoad      = 0x00;
    public static final int REG_WdogValue     = 0x04;
    public static final int REG_WdogControl   = 0x08;
    public static final int REG_WdogIntClr    = 0x0c;
    public static final int REG_WdogRIS       = 0x10;
    public static final int REG_WdogMIS       = 0x14;

    public static final int REG_WdogLock      = 0xc00;

    public static final int REG_WdogITCR      = 0xf00;
    public static final int REG_WdogITOP      = 0xf04;

    public static final int REG_WdogPeriphID0 = 0xfe0;
    public static final int REG_WdogPeriphID1 = 0xfe4;
    public static final int REG_WdogPeriphID2 = 0xfe8;
    public static final int REG_WdogPeriphID3 = 0xfec;
    public static final int REG_WdogPCellID0  = 0xff0;
    public static final int REG_WdogPCellID1  = 0xff4;
    public static final int REG_WdogPCellID2  = 0xff8;
    public static final int REG_WdogPCellID3  = 0xffc;

    public Watchdog() {
        slave = new WatchdogSlave();
    }

    @Override
    public SlaveCore getSlaveCore() {
        return slave;
    }

    class WatchdogSlave extends Controller32 {
        public WatchdogSlave() {
            //addReg(REG_WdogLoad, "WdogLoad", 0xffffffff);
            //addReg(REG_WdogValue, "WdogValue", 0xffffffff);
            //addReg(REG_WdogControl, "WdogControl", 0x0);
            //addReg(REG_WdogIntClr, "WdogIntClr", -);
            //addReg(REG_WdogRIS, "WdogRIS", 0x0);
            //addReg(REG_WdogMIS, "WdogMIS", 0x0);

            //addReg(REG_WdogLock, "WdogLock", 0x0);

            //addReg(REG_WdogITCR, "WdogITCR", 0x0);
            //addReg(REG_WdogITOP, "WdogITOP", 0x0);

            addReg(REG_WdogPeriphID0, "WdogPeriphID0", 0x05);
            addReg(REG_WdogPeriphID1, "WdogPeriphID1", 0x18);
            addReg(REG_WdogPeriphID2, "WdogPeriphID2", 0x14);
            addReg(REG_WdogPeriphID3, "WdogPeriphID3", 0x00);
            addReg(REG_WdogPCellID0, "WdogPCellID0", 0x0d);
            addReg(REG_WdogPCellID1, "WdogPCellID1", 0xf0);
            addReg(REG_WdogPCellID2, "WdogPCellID2", 0x05);
            addReg(REG_WdogPCellID3, "WdogPCellID3", 0xb1);
        }

        @Override
        public int readWord(long addr) {
            int regaddr;
            int result;

            regaddr = (int) (addr & getAddressMask(LEN_WORD_BITS));

            switch (regaddr) {
            default:
                result = super.readWord(regaddr);
                break;
            }

            return result;
        }

        @Override
        public void writeWord(long addr, int data) {
            int regaddr;

            regaddr = (int) (addr & getAddressMask(LEN_WORD_BITS));

            switch (regaddr) {
            case REG_WdogPeriphID0:
            case REG_WdogPeriphID1:
            case REG_WdogPeriphID2:
            case REG_WdogPeriphID3:
            case REG_WdogPCellID0:
            case REG_WdogPCellID1:
            case REG_WdogPCellID2:
            case REG_WdogPCellID3:
                //read only, ignored
                break;
            default:
                super.writeWord(regaddr, data);
                break;
            }
        }

        @Override
        public void run() {
            //do nothing
        }
    }

}
