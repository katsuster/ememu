package net.katsuster.semu;

/**
 * システムコントローラ
 *
 * 参考: PrimeXsys System Controller (SP810)
 * ARM DDI0254B
 *
 * @author katsuhiro
 */
public class SysController extends SlaveCore64 {
    //データ幅（バイト単位）
    public static final int LEN_WORD = 4;
    //データ幅（ビット単位）
    public static final int LEN_WORD_BITS = LEN_WORD * 8;

    public static final int REG_SCCTRL        = 0x0000;

    public SysController() {

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
        int result;

        regaddr = (int)(addr & getAddressMask(LEN_WORD_BITS));

        switch (regaddr) {
        case REG_SCCTRL:
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
        case REG_SCCTRL:
            result = 0x9;
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
        case REG_SCCTRL:
            //do nothing
            break;
        default:
            throw new IllegalArgumentException(String.format("Illegal address 0x%08x.",
                    regaddr));
        }
    }
}