package net.katsuster.ememu.riscv;

import net.katsuster.ememu.generic.*;

/**
 * Power Reset Clocking Interrupt (PRCI)
 *
 * 参考: SiFive FU540-C000 Manual: v1p0
 */
public class PRCI implements ParentCore {
    private PRCISlave slave;

    private PLLCFG corePll;
    private PLLCFG ddrPll;
    private PLLCFG gemgxlPll;

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

    public PRCI() {
        slave = new PRCISlave();

        corePll = new PLLCFG(0x030187c1);
        ddrPll = new PLLCFG(0x030187c1);
        gemgxlPll = new PLLCFG(0x030187c1);
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
            case REG_COREPLLCFG0:
                result = corePll.getData();
                break;
            case REG_DDRPLLCFG0:
                result = ddrPll.getData();
                break;
            case REG_GEMGXLPLLCFG0:
                result = gemgxlPll.getData();
                break;
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
            case REG_COREPLLCFG0:
                corePll.setData(data);
                corePll.setLock(1);
                System.out.printf("PRCI: wr COREPLLCFG0: %s\n", corePll);
                break;
            case REG_DDRPLLCFG0:
                ddrPll.setData(data);
                ddrPll.setLock(1);
                System.out.printf("PRCI: wr DDRPLLCFG0: %s\n", ddrPll);
                break;
            case REG_GEMGXLPLLCFG0:
                gemgxlPll.setData(data);
                gemgxlPll.setLock(1);
                System.out.printf("PRCI: wr GEMGXLPLLCFG0: %s\n", gemgxlPll);
                break;
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

    /**
     * PLL の設定
     */
    class PLLCFG {
        private int divr;
        private int divf;
        private int divq;
        private int range;
        private int bypass;
        private int fse;
        private int lock;

        public PLLCFG() {
            this(0);
        }

        public PLLCFG(int v) {
            setData(v);
        }

        /**
         * 現在の PLL 設定値を表す 32ビットのレジスタ値を取得します。
         *
         * @return レジスタの値
         */
        public int getData() {
            int v = 0;

            v = BitOp.setField32(v, 0, 6, divr);
            v = BitOp.setField32(v, 6, 9, divf);
            v = BitOp.setField32(v, 15, 3, divq);
            v = BitOp.setField32(v, 18, 3, range);
            v = BitOp.setField32(v, 24, 1, bypass);
            v = BitOp.setField32(v, 25, 1, fse);
            v = BitOp.setField32(v, 31, 1, lock);

            return v;
        }

        /**
         * 32ビットのレジスタ値から、PLL 設定値を更新します。
         *
         * @param v レジスタの値
         */
        public void setData(int v) {
            divr = BitOp.getField32(v, 0, 6);
            divf = BitOp.getField32(v, 6, 9);
            divq = BitOp.getField32(v, 15, 3);
            range = BitOp.getField32(v, 18, 3);
            bypass = BitOp.getField32(v, 24, 1);
            fse = BitOp.getField32(v, 25, 1);
            lock = 0;
        }

        /**
         * PLL ロックしているかどうかを設定します。
         *
         * @param v ロックしている場合は 0 以外、ロックしていない場合は 0
         */
        public void setLock(int v) {
            if (v != 0) {
                lock = 1;
            } else {
                lock = 0;
            }
        }

        public String toString() {
            return String.format("data: 0x%x\n" +
                            "  %s: 0x%x, \n" +
                            "  %s: 0x%x, \n" +
                            "  %s: 0x%x, \n" +
                            "  %s: 0x%x, \n" +
                            "  %s: 0x%x, \n" +
                            "  %s: 0x%x, \n" +
                            "  %s: 0x%x",
                    getData(),
                    "divr", divr,
                    "divf", divf,
                    "divq", divq,
                    "range", range,
                    "bypass", bypass,
                    "fse", fse,
                    "lock", lock);
        }
    }
}
