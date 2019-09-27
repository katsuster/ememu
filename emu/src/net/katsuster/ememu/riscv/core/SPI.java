package net.katsuster.ememu.riscv.core;

import net.katsuster.ememu.generic.*;

/**
 * Serial Peripheral Interface (SPI)
 *
 * 参考: SiFive FU540-C000 Manual: v1p0
 */
public class SPI implements ParentCore {
    private SPI.SPISlave slave;

    public static final int REG_SCKDIV  = 0x0000;
    public static final int REG_SCKMODE = 0x0004;
    public static final int REG_CSID    = 0x0010;
    public static final int REG_CSDEF   = 0x0014;
    public static final int REG_CSMODE  = 0x0018;
    public static final int REG_DELAY0  = 0x0028;
    public static final int REG_DELAY1  = 0x002c;
    public static final int REG_FMT     = 0x0040;
    public static final int REG_TXDATA  = 0x0048;
    public static final int REG_RXDATA  = 0x004c;
    public static final int REG_TXMARK  = 0x0050;
    public static final int REG_RXMARK  = 0x0054;
    public static final int REG_FCTRL   = 0x0060;
    public static final int REG_FFMT    = 0x0064;
    public static final int REG_IE      = 0x0070;
    public static final int REG_IP      = 0x0074;

    public SPI() {
        slave = new SPISlave();
    }

    @Override
    public SlaveCore64 getSlaveCore() {
        return slave;
    }

    class SPISlave extends Controller32 {
        public SPISlave() {
            addReg(REG_SCKDIV,  "SCKDIV", 0x00000000);
            /*addReg(REG_SCKMODE, "SCKMODE", 0x00000000);
            addReg(REG_CSID,    "CSID", 0x00000000);
            addReg(REG_CSDEF,   "CSDEF", 0x00000000);
            addReg(REG_CSMODE,  "CSMODE", 0x00000000);
            addReg(REG_DELAY0,  "DELAY0", 0x00000000);
            addReg(REG_DELAY1,  "DELAY1", 0x00000000);
            addReg(REG_FMT,     "FMT", 0x00000000);
            addReg(REG_TXDATA,  "TXDATA", 0x00000000);
            addReg(REG_RXDATA,  "RXDATA", 0x00000000);
            addReg(REG_TXMARK,  "TXMARK", 0x00000000);
            addReg(REG_RXMARK,  "RXMARK", 0x00000000);*/
            addReg(REG_FCTRL,   "FCTRL", 0x00000000);
            /*addReg(REG_FFMT,    "FFMT", 0x00000000);
            addReg(REG_IE,      "IE", 0x00000000);
            addReg(REG_IP,      "IP", 0x00000000);*/
        }

        @Override
        public int readWord(BusMaster64 m, long addr) {
            int regaddr;
            int result;

            regaddr = (int) (addr & BitOp.getAddressMask(LEN_WORD_BITS));

            switch (regaddr) {
            default:
                result = super.readWord(m, regaddr);
                break;
            }

            return result;
        }

        @Override
        public void writeWord(BusMaster64 m, long addr, int data) {
            int regaddr;

            regaddr = (int) (addr & BitOp.getAddressMask(LEN_WORD_BITS));

            switch (regaddr) {
            default:
                super.writeWord(m, regaddr, data);
                break;
            }
        }
    }
}
