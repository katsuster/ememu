package net.katsuster.semu.arm;

/**
 * 2nd 割り込みコントローラ
 *
 * 参考: Versatile Application Baseboard for ARM926EJ-S User Guide
 * ARM DUI0225D
 *
 * @author katsuhiro
 */
public class SecondaryINTC extends Controller64Reg32 {
    public static final int REG_SIC_STATUS     = 0x000;
    public static final int REG_SIC_RAWSTAT    = 0x004;
    public static final int REG_SIC_ENABLE     = 0x008;
    public static final int REG_SIC_ENSET      = 0x008;
    public static final int REG_SIC_ENCLR      = 0x00c;
    public static final int REG_SIC_SOFTINTSET = 0x010;
    public static final int REG_SIC_SOFTINTCLR = 0x014;
    public static final int REG_SIC_PICENABLE  = 0x020;
    public static final int REG_SIC_PICENSET   = 0x020;
    public static final int REG_SIC_PICENCLR   = 0x024;

    public SecondaryINTC() {
        addReg(REG_SIC_ENCLR, "SIC_ENCLR", 0x00000000);
        addReg(REG_SIC_PICENSET, "SIC_PICENSET", 0x00000000);
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
        case REG_SIC_ENCLR:
            //TODO: not implemented
            System.out.printf("SIC_ENCLR: read 0x%08x\n", 0);
            result = 0x0;
            break;
        case REG_SIC_PICENSET:
            //TODO: not implemented
            System.out.printf("SIC_PICENSET: read 0x%08x\n", 0);
            result = 0x0;
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

        regaddr = (int) (addr & getAddressMask(LEN_WORD_BITS));

        switch (regaddr) {
        case REG_SIC_ENCLR:
            //TODO: not implemented
            System.out.printf("SIC_ENCLR: 0x%08x\n", data);
            break;
        case REG_SIC_PICENSET:
            //TODO: not implemented
            System.out.printf("SIC_PICENSET: 0x%08x\n", data);
            break;
        default:
            super.setReg(regaddr, data);
            break;
        }
    }
}
