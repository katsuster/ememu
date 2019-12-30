package net.katsuster.ememu.riscv;

import net.katsuster.ememu.generic.BitOp;
import net.katsuster.ememu.generic.Controller32;
import net.katsuster.ememu.generic.core.AbstractParentCore;
import net.katsuster.ememu.generic.bus.BusMaster64;
import net.katsuster.ememu.riscv.core.RV64;

/**
 * Core Local Interruptor (CLINT)
 *
 * 参考: SiFive FU540-C000 Manual: v1p0
 */
public class CLINT extends AbstractParentCore {
    public static final int NUM_REG_MSIP = 32;
    public static final long RTCCLK = 1000000; //1MHz

    private RV64[] cores;

    private int numCores;
    private int REG_MSIP_LAST;
    private int REG_MSIP_RES0;
    private long mtime;

    //4bytes registers
    public static final int REG_MSIP0         = 0x0000;
    public static final int REG_MSIP_RES_LAST = 0x007c;

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

    public CLINT(String n, RV64[] c) {
        super(n);

        cores = c;
        numCores = 5;
        REG_MSIP_LAST = Math.max(REG_MSIP0, REG_MSIP0 + (numCores - 1) * 4);
        REG_MSIP_RES0 = REG_MSIP0 + numCores * 4;

        setSlaveCore(new CLINTSlave());
    }

    class CLINTSlave extends Controller32 {
        public CLINTSlave() {
            for (int i = 0; i < NUM_REG_MSIP; i++) {
                addReg(REG_MSIP0 + i * 4, "MSIP" + i, 0x00000000);
            }

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

            if (REG_MSIP0 <= regaddr && regaddr <= REG_MSIP_LAST) {
                int id = (regaddr - REG_MSIP0) / 4;

                if (cores[id].getXIP_XSIP(RV64.PRIV_M)) {
                    result = 1;
                } else {
                    result = 0;
                }

                //System.out.printf("rd MSIP[%d] val:%08x\n", id, result);

                return result;
            }

            if (REG_MSIP_RES0 <= regaddr && regaddr <= REG_MSIP_RES_LAST) {
                // Ignore reserved registers
                return 0;
            }

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

            if (REG_MSIP0 <= regaddr && regaddr <= REG_MSIP_LAST) {
                int id = (regaddr - REG_MSIP0) / 4;
                boolean b = (data & 1) != 0;

                cores[id].setXIP_XSIP(RV64.PRIV_M, b);
                cores[id].interrupt();

                System.out.printf("MSIP[%d] val:%08x\n", id, data);

                return;
            }

            if (REG_MSIP_RES0 <= regaddr && regaddr <= REG_MSIP_RES_LAST) {
                // Ignore reserved registers
                return;
            }

            switch (regaddr) {
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
                break;
            case REG_MTIME_L:
                return mtime;
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
                break;
            case REG_MTIME_L:
                System.out.printf("CLINT: wr MTIME: 0x%x\n", data);
                return;
            default:
                throw new IllegalArgumentException(String.format(
                        "Cannot write 64bit to 0x%08x.", addr));
            }

            writeWord(m, addr + 0, (int)(data >>> 0));
            writeWord(m, addr + 4, (int)(data >>> 32));
        }

        @Override
        public void run() {
            while (!shouldHalt()) {
                //FIXME: 100Hz polling
                int hz = 100;

                try {
                    Thread.sleep(1000 / hz);

                    synchronized (this) {
                        mtime += RTCCLK / 100;
                    }
                } catch (InterruptedException e) {
                    //ignore
                }
            }
        }
    }
}
