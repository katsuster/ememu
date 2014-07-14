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

    public static final int REG_VICPERIPHID0    = 0x0fe0;
    public static final int REG_VICPERIPHID1    = 0x0fe4;
    public static final int REG_VICPERIPHID2    = 0x0fe8;
    public static final int REG_VICPERIPHID3    = 0x0fec;

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
