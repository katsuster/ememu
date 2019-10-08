package net.katsuster.ememu.riscv;

import net.katsuster.ememu.generic.*;
import net.katsuster.ememu.riscv.core.*;

/**
 * Power Reset Clocking Interrupt (PRCI)
 *
 * 参考: SiFive FU540-C000 Manual: v1p0
 */
public class PRCI implements ParentCore {
    private RV64[] cores;
    private PRCI.PRCISlave slave;

    public static final int REG_HFXOSCCFG         = 0x0000;
    public static final int REG_COREPLLCFG0       = 0x0004;
    public static final int REG_COREPLLCFG1       = 0x0008;
    public static final int REG_DDRPLLCFG0        = 0x000c;
    public static final int REG_DDRPLLCFG1        = 0x0010;
    public static final int REG_GEMGXLPLLCFG0     = 0x001c;
    public static final int REG_GEMGXLPLLCFG1     = 0x0020;
    public static final int REG_CORECLKSEL        = 0x0024;
    public static final int REG_DEVICESRESETREG   = 0x0028;

    public static final int REG_UNDOCUMENTD0      = 0x002c;

    public PRCI(RV64[] c) {
        cores = c;
        slave = new PRCISlave();
    }

    @Override
    public SlaveCore64 getSlaveCore() {
        return slave;
    }

    class PRCISlave extends Controller32 {
        public PRCISlave() {
            addReg(REG_HFXOSCCFG,       "HFXOSCCFG", 0x80000000);
            addReg(REG_COREPLLCFG0,     "COREPLLCFG0", 0x030187c1);
            addReg(REG_COREPLLCFG1,     "COREPLLCFG1", 0x00000000);
            addReg(REG_DDRPLLCFG0,      "DDRPLLCFG0", 0x030187c1);
            addReg(REG_DDRPLLCFG1,      "DDRPLLCFG1", 0x00000000);
            addReg(REG_GEMGXLPLLCFG0,   "GEMGXLPLLCFG0", 0x030187c1);
            addReg(REG_GEMGXLPLLCFG1,   "GEMGXLPLLCFG1", 0x00000000);
            addReg(REG_CORECLKSEL,      "CORECLKSEL", 0x00000000);
            addReg(REG_DEVICESRESETREG, "DEVICESRESETREG", 0x00000000);

            addReg(REG_UNDOCUMENTD0, "UNDOCUMENTED0", 0x00000004);
        }

        @Override
        public int readWord(BusMaster64 m, long addr) {
            int regaddr;
            int result;

            regaddr = (int) (addr & BitOp.getAddressMask(LEN_WORD_BITS));

            switch (regaddr) {
            case REG_UNDOCUMENTD0:
                result = super.readWord(m, regaddr);

                System.out.printf("prci: RD: UNDOCUMENTED0: %08x\n", result);

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
            case REG_UNDOCUMENTD0:
                System.out.printf("prci: WR: UNDOCUMENTED0: %08x\n", data);
                super.writeWord(m, regaddr, data);
                break;
            default:
                super.writeWord(m, regaddr, data);
                break;
            }
        }
    }
}
