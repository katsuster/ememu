package net.katsuster.semu;

/**
 * 割り込みコントローラ
 *
 * 参考: PrimeCell Vectored Interrupt Controller (PL190)
 * ARM DDI0181E
 *
 * @author katsuhiro
 */
public class IntController extends SlaveCore64 {
    //データ幅（バイト単位）
    public static final int LEN_WORD = 4;
    //データ幅（ビット単位）
    public static final int LEN_WORD_BITS = LEN_WORD * 8;

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
    public static final int REG_VICPERIPHID0    = 0xfe0;
    public static final int REG_VICPERIPHID1    = 0xfe4;
    public static final int REG_VICPERIPHID2    = 0xfe8;
    public static final int REG_VICPERIPHID3    = 0xfec;
    public static final int REG_VICPCELLID0     = 0xff0;
    public static final int REG_VICPCELLID1     = 0xff4;
    public static final int REG_VICPCELLID2     = 0xff8;
    public static final int REG_VICPCELLID3     = 0xffc;

    public IntController() {

    }

    @Override
    public boolean tryRead(long addr) {
        return tryAccess(addr);
    }

    @Override
    public byte read8(long addr) {
        long v = readWord(addr);

        return (byte)readMasked(addr, v, LEN_WORD_BITS, 8);
    }

    @Override
    public short read16(long addr) {
        long v = readWord(addr);

        return (byte)readMasked(addr, v, LEN_WORD_BITS, 16);
    }

    @Override
    public int read32(long addr) {
        return readWord(addr);
    }

    @Override
    public long read64(long addr) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    @Override
    public boolean tryWrite(long addr) {
        return tryAccess(addr);
    }

    @Override
    public void write8(long addr, byte data) {
        long w = writeMasked(addr, 0, data, LEN_WORD_BITS, 8);

        writeWord(addr, w);
    }

    @Override
    public void write16(long addr, short data) {
        long w = writeMasked(addr, 0, data, LEN_WORD_BITS, 16);

        writeWord(addr, w);
    }

    @Override
    public void write32(long addr, int data) {
        long w = writeMasked(addr, 0, data, LEN_WORD_BITS, 32);

        writeWord(addr, w);
    }

    @Override
    public void write64(long addr, long data) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    public boolean tryAccess(long addr) {
        int regaddr;

        regaddr = (int)(addr & getAddressMask(LEN_WORD_BITS));

        switch (regaddr) {
        case REG_VICPERIPHID0:
        case REG_VICPERIPHID1:
        case REG_VICPERIPHID2:
        case REG_VICPERIPHID3:
            return true;
        default:
            throw new IllegalArgumentException(String.format("Illegal address 0x%08x.",
                    regaddr));
        }
    }

    public int readWord(long addr) {
        int regaddr;
        int result;

        regaddr = (int)(addr & getAddressMask(LEN_WORD_BITS));

        switch (regaddr) {
        case REG_VICPERIPHID0:
            //[ 7: 0]: Partnumber0: must be 0x90
            result = 0x90;
            break;
        case REG_VICPERIPHID1:
            //[ 7: 4]: Designer0  : must be 0x1
            //[ 3: 0]: Partnumber1: must be 0x1
            result = 0x11;
            break;
        case REG_VICPERIPHID2:
            //レジスタの説明（3.3.14）とリセット後の初期値（Table 3-1）が
            //矛盾しているため、レジスタの説明を正しいものとして実装する
            //[ 7: 4]: Revision   : must be 0x1
            //[ 3: 0]: Designer1  : must be 0x0
            result = 0x10;
            break;
        case REG_VICPERIPHID3:
            //[ 7: 0]: Configuration: must be 0x00
            result = 0x00;
            break;
        default:
            throw new IllegalArgumentException(String.format("Illegal address 0x%08x.",
                    regaddr));
        }

        return result;
    }

    public void writeWord(long addr, long data) {
        int regaddr;

        regaddr = (int)(addr & getAddressMask(LEN_WORD_BITS));

        switch (regaddr) {
        case REG_VICPERIPHID0:
        case REG_VICPERIPHID1:
        case REG_VICPERIPHID2:
        case REG_VICPERIPHID3:
            //read only
            break;
        default:
            throw new IllegalArgumentException(String.format("Illegal address 0x%08x.",
                    regaddr));
        }
    }
}
