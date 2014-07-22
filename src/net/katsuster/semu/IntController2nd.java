package net.katsuster.semu;

/**
 * 2nd 割り込みコントローラ
 *
 * 参考: Versatile Application Baseboard for ARM926EJ-S User Guide
 * ARM DUI0225D
 *
 * @author katsuhiro
 */
public class IntController2nd extends SlaveCore64 {
    //データ幅（バイト単位）
    public static final int LEN_WORD = 4;
    //データ幅（ビット単位）
    public static final int LEN_WORD_BITS = LEN_WORD * 8;

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

    public IntController2nd() {

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
        case REG_SIC_ENCLR:
        case REG_SIC_PICENSET:
            return true;
        default:
            //do nothing
        }

        throw new IllegalArgumentException(String.format("Illegal address 0x%08x.",
                regaddr));
    }

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
            throw new IllegalArgumentException(String.format("Illegal address 0x%08x.",
                    regaddr));
        }

        return result;
    }

    public void writeWord(long addr, long data) {
        boolean notfound = false;
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
            notfound = true;
        }

        if (notfound) {
            throw new IllegalArgumentException(String.format("Illegal address 0x%08x.",
                    regaddr));
        }
    }
}
