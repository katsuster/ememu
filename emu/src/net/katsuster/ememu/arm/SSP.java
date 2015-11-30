package net.katsuster.ememu.arm;

import net.katsuster.ememu.generic.*;

/**
 * 同期シリアルポート
 *
 * 参考: ARM PrimeCell Synchronous Serial Port (PL022)
 * ARM DDI0194G
 *
 * @author katsuhiro
 */
public class SSP implements BusSlave {
    private SSPSlave slave;

    public static final int REG_SSPCR0       = 0x000;
    public static final int REG_SSPCR1       = 0x004;
    public static final int REG_SSPDR        = 0x008;
    public static final int REG_SSPSR        = 0x00c;
    public static final int REG_SSPCPSR      = 0x010;
    public static final int REG_SSPIMSC      = 0x014;
    public static final int REG_SSPRIS       = 0x018;
    public static final int REG_SSPMIS       = 0x01c;
    public static final int REG_SSPICR       = 0x020;
    public static final int REG_SSPDMACR     = 0x024;

    public static final int REG_SSPPeriphID0 = 0xfe0;
    public static final int REG_SSPPeriphID1 = 0xfe4;
    public static final int REG_SSPPeriphID2 = 0xfe8;
    public static final int REG_SSPPeriphID3 = 0xfec;
    public static final int REG_SSPPCellID0  = 0xff0;
    public static final int REG_SSPPCellID1  = 0xff4;
    public static final int REG_SSPPCellID2  = 0xff8;
    public static final int REG_SSPPCellID3  = 0xffc;

    public SSP() {
        slave = new SSPSlave();
    }

    @Override
    public SlaveCore getSlaveCore() {
        return slave;
    }

    class SSPSlave extends Controller32 {
        public SSPSlave() {
            //addReg(REG_SSPCR0, "SSPCR0", 0x0000);
            //addReg(REG_SSPCR1, "SSPCR1", 0x0);
            //addReg(REG_SSPDR, "SSPDR", 0x----);
            //addReg(REG_SSPSR, "SSPSR", 0x03);
            //addReg(REG_SSPCPSR, "SSPCPSR", 0x00);
            //addReg(REG_SSPIMSC, "SSPIMSC", 0x0);
            //addReg(REG_SSPRIS, "SSPRIS", 0x8);
            //addReg(REG_SSPMIS, "SSPMIS", 0x0);
            //addReg(REG_SSPICR, "SSPICR", 0x0);
            //addReg(REG_SSPDMACR, "SSPDMACR", 0x0);

            addReg(REG_SSPPeriphID0, "SSPPeriphID0", 0x22);
            addReg(REG_SSPPeriphID1, "SSPPeriphID1", 0x10);
            addReg(REG_SSPPeriphID2, "SSPPeriphID2", 0x24);
            addReg(REG_SSPPeriphID3, "SSPPeriphID3", 0x00);
            addReg(REG_SSPPCellID0, "SSPPCellID0", 0x0d);
            addReg(REG_SSPPCellID1, "SSPPCellID1", 0xf0);
            addReg(REG_SSPPCellID2, "SSPPCellID2", 0x05);
            addReg(REG_SSPPCellID3, "SSPPCellID3", 0xb1);
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
            case REG_SSPPeriphID0:
            case REG_SSPPeriphID1:
            case REG_SSPPeriphID2:
            case REG_SSPPeriphID3:
            case REG_SSPPCellID0:
            case REG_SSPPCellID1:
            case REG_SSPPCellID2:
            case REG_SSPPCellID3:
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
