package net.katsuster.semu;

/**
 * システムレジスタ
 *
 * 参考: Versatile Application Baseboard for ARM926EJ-S User Guide
 * ARM DUI0225D
 *
 * @author katsuhiro
 */
public class SysBaseboard extends Controller64Reg32 {
    public static final int REG_SYS_24MHZ        = 0x005c;

    private long start24MHz;

    public SysBaseboard() {
        addReg(REG_SYS_24MHZ, "SYS_24MHZ", 0x00000000);

        start24MHz = System.nanoTime();
    }

    @Override
    public boolean tryRead(long addr) {
        return tryAccess(addr);
    }

    @Override
    public boolean tryWrite(long addr) {
        return tryAccess(addr);
    }

    public boolean tryAccess(long addr) {
        int regaddr;

        regaddr = (int)(addr & getAddressMask(LEN_WORD_BITS));

        switch (regaddr) {
        default:
            return super.isValidReg(regaddr);
        }
    }

    @Override
    public int readWord(long addr) {
        int regaddr;
        int result;

        regaddr = (int)(addr & getAddressMask(LEN_WORD_BITS));

        switch (regaddr) {
        case REG_SYS_24MHZ:
            result = (int)((System.nanoTime() - start24MHz) * 24);
            break;
        default:
            result = super.getReg(regaddr);
            break;
        }

        return result;
    }

    @Override
    public void writeWord(long addr, int data) {
        int regaddr;

        regaddr = (int)(addr & getAddressMask(LEN_WORD_BITS));

        switch (regaddr) {
        case REG_SYS_24MHZ:
            //read only, ignored
            break;
        default:
            super.setReg(regaddr, data);
            break;
        }
    }
}
