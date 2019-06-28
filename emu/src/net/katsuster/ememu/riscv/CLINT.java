package net.katsuster.ememu.riscv;

import net.katsuster.ememu.generic.*;

/**
 * Core Local Interruptor (CLINT)
 *
 * 参考: SiFive FU540-C000 Manual: v1p0
 */
public class CLINT implements ParentCore {
    private CLINTSlave slave;

    //4bytes registers
    public static final int REG_MSIP0         = 0x0000;
    public static final int REG_MSIP1         = 0x0004;
    public static final int REG_MSIP2         = 0x0008;
    public static final int REG_MSIP3         = 0x000c;
    public static final int REG_MSIP4         = 0x0010;

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

    public CLINT() {
        slave = new CLINTSlave();
    }

    @Override
    public SlaveCore getSlaveCore() {
        return slave;
    }

    class CLINTSlave extends Controller32 {
        public CLINTSlave() {
            addReg(REG_MSIP0, "MSIP0", 0x00000000);
            addReg(REG_MSIP1, "MSIP1", 0x00000000);
            addReg(REG_MSIP2, "MSIP2", 0x00000000);
            addReg(REG_MSIP3, "MSIP3", 0x00000000);
            addReg(REG_MSIP4, "MSIP4", 0x00000000);

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
        public int readWord(long addr) {
            int regaddr;
            int result;

            regaddr = (int) (addr & BitOp.getAddressMask(LEN_WORD_BITS));

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

            regaddr = (int) (addr & BitOp.getAddressMask(LEN_WORD_BITS));

            switch (regaddr) {
            default:
                super.writeWord(regaddr, data);
                break;
            }
        }

        @Override
        public long read64(long addr) {
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

            data = (((long)readWord(addr + 0) & 0xffffffffL) << 0) |
                    (((long)readWord(addr + 4) & 0xffffffffL) << 32);

            return data;
        }

        @Override
        public void write64(long addr, long data) {
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

            writeWord(addr + 0, (int)(data >>> 0));
            writeWord(addr + 4, (int)(data >>> 32));
        }

        @Override
        public void run() {
            //do nothing
        }
    }
}
