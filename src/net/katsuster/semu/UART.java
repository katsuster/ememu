package net.katsuster.semu;

/**
 * debug UART
 *
 * 参考: ARM PrimeCell UART (PL011)
 *
 * @author katsuhiro
 */
public class UART extends SlaveCore64 {
    //データ幅（バイト単位）
    public static final int LEN_WORD = 4;
    //データ幅（ビット単位）
    public static final int LEN_WORD_BITS = LEN_WORD * 8;

    private StringBuilder strBuffer;

    public static final int REG_UARTDR        = 0x000;
    public static final int REG_UARTRSR       = 0x004;
    public static final int REG_UARTFR        = 0x018;
    public static final int REG_UARTILPR      = 0x020;
    public static final int REG_UARTIBRD      = 0x024;
    public static final int REG_UARTFBRD      = 0x028;
    public static final int REG_UARTLCR_H     = 0x02c;
    public static final int REG_UARTCR        = 0x030;
    public static final int REG_UARTIFLS      = 0x034;
    public static final int REG_UARTIMSC      = 0x038;
    public static final int REG_UARTRIS       = 0x03c;
    public static final int REG_UARTMIS       = 0x040;
    public static final int REG_UARTICR       = 0x044;
    public static final int REG_UARTDMACR     = 0x048;
    public static final int REG_UARTPeriphID0 = 0xfe0;
    public static final int REG_UARTPeriphID1 = 0xfe4;
    public static final int REG_UARTPeriphID2 = 0xfe8;
    public static final int REG_UARTPeriphID3 = 0xfec;
    public static final int REG_UARTPCellID0  = 0xff0;
    public static final int REG_UARTPCellID1  = 0xff4;
    public static final int REG_UARTPCellID2  = 0xff8;
    public static final int REG_UARTPCellID3  = 0xffc;

    public UART() {
        strBuffer = new StringBuilder();
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
        case REG_UARTDR:
        case REG_UARTFR:
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
        case REG_UARTDR:
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        case REG_UARTFR:
            result = 0;
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
        case REG_UARTDR:
            char ascii = (char)(data & 0xff);

            strBuffer.append(ascii);
            System.out.printf("%c", ascii);

            break;
        default:
            throw new IllegalArgumentException(String.format("Illegal address 0x%08x.",
                    regaddr));
        }
    }
}
