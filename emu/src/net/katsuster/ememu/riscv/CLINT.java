package net.katsuster.ememu.riscv;

import net.katsuster.ememu.generic.*;
import net.katsuster.ememu.riscv.core.*;

/**
 * Core Local Interruptor (CLINT)
 *
 * 参考: SiFive FU540-C000 Manual: v1p0
 */
public class CLINT implements ParentCore {
    private RV64[] cores;
    private CLINTSlave slave;

    //4bytes registers
    public static final int REG_MSIP0         = 0x0000;
    public static final int REG_MSIP1         = 0x0004;
    public static final int REG_MSIP2         = 0x0008;
    public static final int REG_MSIP3         = 0x000c;
    public static final int REG_MSIP4         = 0x0010;
    public static final int REG_MSIP_RES5     = 0x0014;
    public static final int REG_MSIP_RES6     = 0x0018;
    public static final int REG_MSIP_RES7     = 0x001c;
    public static final int REG_MSIP_RES8     = 0x0020;
    public static final int REG_MSIP_RES9     = 0x0024;
    public static final int REG_MSIP_RES10    = 0x0028;
    public static final int REG_MSIP_RES11    = 0x002c;
    public static final int REG_MSIP_RES12    = 0x0030;
    public static final int REG_MSIP_RES13    = 0x0034;
    public static final int REG_MSIP_RES14    = 0x0038;
    public static final int REG_MSIP_RES15    = 0x003c;
    public static final int REG_MSIP_RES16    = 0x0040;
    public static final int REG_MSIP_RES17    = 0x0044;
    public static final int REG_MSIP_RES18    = 0x0048;
    public static final int REG_MSIP_RES19    = 0x004c;
    public static final int REG_MSIP_RES20    = 0x0050;
    public static final int REG_MSIP_RES21    = 0x0054;
    public static final int REG_MSIP_RES22    = 0x0058;
    public static final int REG_MSIP_RES23    = 0x005c;
    public static final int REG_MSIP_RES24    = 0x0060;
    public static final int REG_MSIP_RES25    = 0x0064;
    public static final int REG_MSIP_RES26    = 0x0068;
    public static final int REG_MSIP_RES27    = 0x006c;
    public static final int REG_MSIP_RES28    = 0x0070;
    public static final int REG_MSIP_RES29    = 0x0074;
    public static final int REG_MSIP_RES30    = 0x0078;
    public static final int REG_MSIP_RES31    = 0x007c;

    //8bytes registers
    public static final int REG_MTIMECMP0_L   = 0x4000;
    public static final int REG_MTIMECMP0_H   = 0x4004;
    public static final int REG_MTIMECMP1_L   = 0x4008;
    public static final int REG_MTIMECMP1_H   = 0x400c;
    public static final int REG_MTIMECMP2_L   = 0x4010;
    public static final int REG_MTIMECMP2_H   = 0x4014;
    public static final int REG_MTIMECMP3_L   = 0x4018;
    public static final int REG_MTIMECMP3_H   = 0x401c;
    public static final int REG_MTIMECMP4_L   = 0x4020;
    public static final int REG_MTIMECMP4_H   = 0x4024;

    public static final int REG_MTIME_L       = 0xbff8;
    public static final int REG_MTIME_H       = 0xbffc;

    public CLINT(RV64[] c) {
        cores = c;
        slave = new CLINTSlave();
    }

    @Override
    public SlaveCore64 getSlaveCore() {
        return slave;
    }

    class CLINTSlave extends Controller32 {
        public CLINTSlave() {
            addReg(REG_MSIP0, "MSIP0", 0x00000000);
            addReg(REG_MSIP1, "MSIP1", 0x00000000);
            addReg(REG_MSIP2, "MSIP2", 0x00000000);
            addReg(REG_MSIP3, "MSIP3", 0x00000000);
            addReg(REG_MSIP4, "MSIP4", 0x00000000);
            addReg(REG_MSIP_RES5, "MSIP_RES5", 0x00000000);
            addReg(REG_MSIP_RES6, "MSIP_RES6", 0x00000000);
            addReg(REG_MSIP_RES7, "MSIP_RES7", 0x00000000);
            addReg(REG_MSIP_RES8, "MSIP_RES8", 0x00000000);
            addReg(REG_MSIP_RES9, "MSIP_RES9", 0x00000000);
            addReg(REG_MSIP_RES10, "MSIP_RES10", 0x00000000);
            addReg(REG_MSIP_RES11, "MSIP_RES11", 0x00000000);
            addReg(REG_MSIP_RES12, "MSIP_RES12", 0x00000000);
            addReg(REG_MSIP_RES13, "MSIP_RES13", 0x00000000);
            addReg(REG_MSIP_RES14, "MSIP_RES14", 0x00000000);
            addReg(REG_MSIP_RES15, "MSIP_RES15", 0x00000000);
            addReg(REG_MSIP_RES16, "MSIP_RES16", 0x00000000);
            addReg(REG_MSIP_RES17, "MSIP_RES17", 0x00000000);
            addReg(REG_MSIP_RES18, "MSIP_RES18", 0x00000000);
            addReg(REG_MSIP_RES19, "MSIP_RES19", 0x00000000);
            addReg(REG_MSIP_RES20, "MSIP_RES20", 0x00000000);
            addReg(REG_MSIP_RES21, "MSIP_RES21", 0x00000000);
            addReg(REG_MSIP_RES22, "MSIP_RES22", 0x00000000);
            addReg(REG_MSIP_RES23, "MSIP_RES23", 0x00000000);
            addReg(REG_MSIP_RES24, "MSIP_RES24", 0x00000000);
            addReg(REG_MSIP_RES25, "MSIP_RES25", 0x00000000);
            addReg(REG_MSIP_RES26, "MSIP_RES26", 0x00000000);
            addReg(REG_MSIP_RES27, "MSIP_RES27", 0x00000000);
            addReg(REG_MSIP_RES28, "MSIP_RES28", 0x00000000);
            addReg(REG_MSIP_RES29, "MSIP_RES29", 0x00000000);
            addReg(REG_MSIP_RES30, "MSIP_RES30", 0x00000000);
            addReg(REG_MSIP_RES31, "MSIP_RES31", 0x00000000);

            addReg(REG_MTIMECMP0_L, "MTIMECMP0_L", 0x00000000);
            addReg(REG_MTIMECMP0_H, "MTIMECMP0_H", 0x00000000);
            addReg(REG_MTIMECMP1_L, "MTIMECMP1_L", 0x00000000);
            addReg(REG_MTIMECMP1_H, "MTIMECMP1_H", 0x00000000);
            addReg(REG_MTIMECMP2_L, "MTIMECMP2_L", 0x00000000);
            addReg(REG_MTIMECMP2_H, "MTIMECMP2_H", 0x00000000);
            addReg(REG_MTIMECMP3_L, "MTIMECMP3_L", 0x00000000);
            addReg(REG_MTIMECMP3_H, "MTIMECMP3_H", 0x00000000);
            addReg(REG_MTIMECMP4_L, "MTIMECMP4_L", 0x00000000);
            addReg(REG_MTIMECMP4_H, "MTIMECMP4_H", 0x00000000);

            addReg(REG_MTIME_L, "MTIME_L", 0x00000000);
            addReg(REG_MTIME_H, "MTIME_H", 0x00000000);
        }

        @Override
        public int readWord(BusMaster64 m, long addr) {
            int regaddr;
            int result;

            regaddr = (int) (addr & BitOp.getAddressMask(LEN_WORD_BITS));

            // Reserved area always return 0
            if (0x14 <= regaddr && regaddr <= 0x3fff) {
                return 0;
            }

            switch (regaddr) {
            case REG_MSIP0:
            case REG_MSIP1:
            case REG_MSIP2:
            case REG_MSIP3:
            case REG_MSIP4:
                int id = (regaddr - REG_MSIP0) / 4;

                if (cores[id].getXIP_XSIP(RV64.PRIV_M)) {
                    result = 1;
                } else {
                    result = 0;
                }
                //System.out.printf("rd MSIP[%d] val:%08x\n", id, result);
                break;
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
            case REG_MSIP0:
            case REG_MSIP1:
            case REG_MSIP2:
            case REG_MSIP3:
            case REG_MSIP4:
                int id = (regaddr - REG_MSIP0) / 4;
                boolean b = (data & 1) != 0;

                cores[id].setXIP_XSIP(RV64.PRIV_M, b);

                System.out.printf("MSIP[%d] val:%08x\n", id, data);
                break;
            default:
                super.writeWord(m, regaddr, data);
                break;
            }
        }

        @Override
        public long read64(BusMaster64 m, long addr) {
            int regaddr = (int) (addr & BitOp.getAddressMask(LEN_WORD_BITS));
            long data;

            switch (regaddr) {
            case REG_MTIMECMP0_L:
            case REG_MTIMECMP1_L:
            case REG_MTIMECMP2_L:
            case REG_MTIMECMP3_L:
            case REG_MTIMECMP4_L:
            case REG_MTIME_L:
                break;
            default:
                throw new IllegalArgumentException(String.format(
                        "Cannot read 64bit from 0x%08x.", addr));
            }

            data = (((long)readWord(m, addr + 0) & 0xffffffffL) << 0) |
                    (((long)readWord(m, addr + 4) & 0xffffffffL) << 32);

            return data;
        }

        @Override
        public void write64(BusMaster64 m, long addr, long data) {
            int regaddr = (int) (addr & BitOp.getAddressMask(LEN_WORD_BITS));

            switch (regaddr) {
            case REG_MTIMECMP0_L:
            case REG_MTIMECMP1_L:
            case REG_MTIMECMP2_L:
            case REG_MTIMECMP3_L:
            case REG_MTIMECMP4_L:
            case REG_MTIME_L:
                break;
            default:
                throw new IllegalArgumentException(String.format(
                        "Cannot write 64bit to 0x%08x.", addr));
            }

            writeWord(m, addr + 0, (int)(data >>> 0));
            writeWord(m, addr + 4, (int)(data >>> 32));
        }

        @Override
        public void run() {
            //do nothing
        }
    }
}
