package net.katsuster.semu;

/**
 * 割り込みコントローラ
 *
 * 参考: PrimeCell Vectored Interrupt Controller (PL190)
 * ARM DDI0181E
 *
 * @author katsuhiro
 */
public class PrimaryINTC extends Controller64Reg32 {
    public static final int REG_VICIRQSTATUS    = 0x000;
    public static final int REG_VICFIQSTATUS    = 0x004;
    public static final int REG_VICRAWINTR      = 0x008;
    public static final int REG_VICINTSELECT    = 0x00c;
    public static final int REG_VICINTENABLE    = 0x010;
    public static final int REG_VICINTENCLEAR   = 0x014;
    public static final int REG_VICSOFTINT      = 0x018;
    public static final int REG_VICSOFTINTCLEAR = 0x01c;
    public static final int REG_VICPROTECTION   = 0x020;
    public static final int REG_VICVECTADDR     = 0x030;
    public static final int REG_VICDEFVECTADDR  = 0x034;
    public static final int REG_VICVECTADDR0    = 0x100;
    public static final int REG_VICVECTADDR1    = 0x104;
    public static final int REG_VICVECTADDR2    = 0x108;
    public static final int REG_VICVECTADDR3    = 0x10c;
    public static final int REG_VICVECTADDR4    = 0x110;
    public static final int REG_VICVECTADDR5    = 0x114;
    public static final int REG_VICVECTADDR6    = 0x118;
    public static final int REG_VICVECTADDR7    = 0x11c;
    public static final int REG_VICVECTADDR8    = 0x120;
    public static final int REG_VICVECTADDR9    = 0x124;
    public static final int REG_VICVECTADDR10   = 0x128;
    public static final int REG_VICVECTADDR11   = 0x12c;
    public static final int REG_VICVECTADDR12   = 0x130;
    public static final int REG_VICVECTADDR13   = 0x134;
    public static final int REG_VICVECTADDR14   = 0x138;
    public static final int REG_VICVECTADDR15   = 0x13c;
    public static final int REG_VICVECTCNTL0    = 0x200;
    public static final int REG_VICVECTCNTL1    = 0x204;
    public static final int REG_VICVECTCNTL2    = 0x208;
    public static final int REG_VICVECTCNTL3    = 0x20c;
    public static final int REG_VICVECTCNTL4    = 0x210;
    public static final int REG_VICVECTCNTL5    = 0x214;
    public static final int REG_VICVECTCNTL6    = 0x218;
    public static final int REG_VICVECTCNTL7    = 0x21c;
    public static final int REG_VICVECTCNTL8    = 0x220;
    public static final int REG_VICVECTCNTL9    = 0x224;
    public static final int REG_VICVECTCNTL10   = 0x228;
    public static final int REG_VICVECTCNTL11   = 0x22c;
    public static final int REG_VICVECTCNTL12   = 0x230;
    public static final int REG_VICVECTCNTL13   = 0x234;
    public static final int REG_VICVECTCNTL14   = 0x238;
    public static final int REG_VICVECTCNTL15   = 0x23c;
    public static final int REG_VICITCR         = 0x300;
    public static final int REG_VICITIP1        = 0x304;
    public static final int REG_VICITIP2        = 0x308;
    public static final int REG_VICITOP1        = 0x30c;
    public static final int REG_VICITOP2        = 0x310;
    public static final int REG_VICPERIPHID0    = 0xfe0;
    public static final int REG_VICPERIPHID1    = 0xfe4;
    public static final int REG_VICPERIPHID2    = 0xfe8;
    public static final int REG_VICPERIPHID3    = 0xfec;
    public static final int REG_VICPCELLID0     = 0xff0;
    public static final int REG_VICPCELLID1     = 0xff4;
    public static final int REG_VICPCELLID2     = 0xff8;
    public static final int REG_VICPCELLID3     = 0xffc;

    public PrimaryINTC() {
        addReg(REG_VICINTSELECT, "VICINTSELECT", 0x00000000);
        addReg(REG_VICINTENABLE, "VICINTENABLE", 0x00000000);
        addReg(REG_VICINTENCLEAR, "VICINTENCLEAR", 0x00000000);
        addReg(REG_VICSOFTINTCLEAR, "VICSOFTINTCLEAR", 0x00000000);
        addReg(REG_VICVECTADDR, "VICVECTADDR", 0x00000000);
        addReg(REG_VICDEFVECTADDR, "VICDEFVECTADDR", 0x00000000);

        addReg(REG_VICVECTCNTL0, "VICVECTCNTL0", 0x00000000);
        addReg(REG_VICVECTCNTL1, "VICVECTCNTL1", 0x00000000);
        addReg(REG_VICVECTCNTL2, "VICVECTCNTL2", 0x00000000);
        addReg(REG_VICVECTCNTL3, "VICVECTCNTL3", 0x00000000);
        addReg(REG_VICVECTCNTL4, "VICVECTCNTL4", 0x00000000);
        addReg(REG_VICVECTCNTL5, "VICVECTCNTL5", 0x00000000);
        addReg(REG_VICVECTCNTL6, "VICVECTCNTL6", 0x00000000);
        addReg(REG_VICVECTCNTL7, "VICVECTCNTL7", 0x00000000);
        addReg(REG_VICVECTCNTL8, "VICVECTCNTL8", 0x00000000);
        addReg(REG_VICVECTCNTL9, "VICVECTCNTL9", 0x00000000);
        addReg(REG_VICVECTCNTL10, "VICVECTCNTL10", 0x00000000);
        addReg(REG_VICVECTCNTL11, "VICVECTCNTL11", 0x00000000);
        addReg(REG_VICVECTCNTL12, "VICVECTCNTL12", 0x00000000);
        addReg(REG_VICVECTCNTL13, "VICVECTCNTL13", 0x00000000);
        addReg(REG_VICVECTCNTL14, "VICVECTCNTL14", 0x00000000);
        addReg(REG_VICVECTCNTL15, "VICVECTCNTL15", 0x00000000);

        addReg(REG_VICITCR, "VICITCR", 0x00000000);

        //[ 7: 0]: Partnumber0: must be 0x90
        addReg(REG_VICPERIPHID0, "VICPERIPHID0", 0x00000090);
        //[ 7: 4]: Designer0  : must be 0x1
        //[ 3: 0]: Partnumber1: must be 0x1
        addReg(REG_VICPERIPHID1, "VICPERIPHID1", 0x00000011);
        //レジスタの説明（3.3.14）とリセット後の初期値（Table 3-1）が
        //矛盾しているため、レジスタの説明を正しいものとして実装する
        //[ 7: 4]: Revision   : must be 0x1
        //[ 3: 0]: Designer1  : must be 0x0
        addReg(REG_VICPERIPHID2, "VICPERIPHID2", 0x00000010);
        //[ 7: 0]: Configuration: must be 0x00
        addReg(REG_VICPERIPHID3, "VICPERIPHID3", 0x00000000);

        //[ 7: 0]: VICPCellID0: must be 0x0d
        addReg(REG_VICPCELLID0, "VICPCELLID0", 0x0000000d);
        //[ 7: 0]: VICPCellID1: must be 0xf0
        addReg(REG_VICPCELLID1, "VICPCELLID1", 0x000000f0);
        //[ 7: 0]: VICPCellID2: must be 0x05
        addReg(REG_VICPCELLID2, "VICPCELLID2", 0x00000005);
        //[ 7: 0]: VICPCellID3: must be 0xb1
        addReg(REG_VICPCELLID3, "VICPCELLID3", 0x000000b1);
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
        case REG_VICVECTADDR:
            //TODO: not implemented
            System.out.printf("VICVECTADDR: read 0x%08x\n", 0);
            result = 0x0;
            break;
        case REG_VICDEFVECTADDR:
            //TODO: not implemented
            System.out.printf("VICDEFVECTADDR: read 0x%08x\n", 0);
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
        case REG_VICINTSELECT:
            //TODO: not implemented
            System.out.printf("VICINTSELECT: 0x%08x\n", data);
            break;
        case REG_VICINTENABLE:
            //TODO: not implemented
            System.out.printf("VICINTENABLE: 0x%08x\n", data);
            break;
        case REG_VICINTENCLEAR:
            //TODO: not implemented
            System.out.printf("VICINTENCLEAR: 0x%08x\n", data);
            break;
        case REG_VICSOFTINTCLEAR:
            //TODO: not implemented
            System.out.printf("VICSOFTINTCLEAR: 0x%08x\n", data);
            break;
        case REG_VICVECTADDR:
            //TODO: not implemented
            System.out.printf("VICVECTADDR: 0x%08x\n", data);
            break;
        case REG_VICDEFVECTADDR:
            //TODO: not implemented
            System.out.printf("VICDEFVECTADDR: 0x%08x\n", data);
            break;
        case REG_VICITCR:
            //TODO: not implemented
            System.out.printf("VICITCR: 0x%08x\n", data);
            break;
        case REG_VICVECTCNTL0:
        case REG_VICVECTCNTL1:
        case REG_VICVECTCNTL2:
        case REG_VICVECTCNTL3:
        case REG_VICVECTCNTL4:
        case REG_VICVECTCNTL5:
        case REG_VICVECTCNTL6:
        case REG_VICVECTCNTL7:
        case REG_VICVECTCNTL8:
        case REG_VICVECTCNTL9:
        case REG_VICVECTCNTL10:
        case REG_VICVECTCNTL11:
        case REG_VICVECTCNTL12:
        case REG_VICVECTCNTL13:
        case REG_VICVECTCNTL14:
        case REG_VICVECTCNTL15:
            //TODO: not implemented
            System.out.printf("VICVECTCNTL[%d]: 0x%08x\n",
                    (regaddr - REG_VICVECTCNTL0) / 4, data);
            break;
        case REG_VICPERIPHID0:
        case REG_VICPERIPHID1:
        case REG_VICPERIPHID2:
        case REG_VICPERIPHID3:
        case REG_VICPCELLID0:
        case REG_VICPCELLID1:
        case REG_VICPCELLID2:
        case REG_VICPCELLID3:
            //read only, ignored
            break;
        default:
            super.setReg(regaddr, data);
            break;
        }
    }
}
