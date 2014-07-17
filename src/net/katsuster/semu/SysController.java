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

    public static final int REG_SCCTRL      = 0x000;
    public static final int REG_SCSYSSTAT   = 0x004;
    public static final int REG_SCIMCTRL    = 0x008;
    public static final int REG_SCIMSTAT    = 0x00c;
    public static final int REG_SCXTALCTRL  = 0x010;
    public static final int REG_SCPLLCTRL   = 0x014;
    public static final int REG_SCPLLFCTRL  = 0x018;
    public static final int REG_SCPERCTRL0  = 0x01c;
    public static final int REG_SCPERCTRL1  = 0x020;
    public static final int REG_SCPEREN     = 0x024;
    public static final int REG_SCPERDIS    = 0x028;
    public static final int REG_SCPERCLKEN  = 0x02c;
    public static final int REG_SCPERSTAT   = 0x030;
    public static final int REG_SCSysID0    = 0xee0;
    public static final int REG_SCSysID1    = 0xee4;
    public static final int REG_SCSysID2    = 0xee8;
    public static final int REG_SCSysID3    = 0xeec;
    public static final int REG_SCITCR      = 0xf00;
    public static final int REG_SCITIR0     = 0xf04;
    public static final int REG_SCITIR1     = 0xf08;
    public static final int REG_SCITOR      = 0xf0c;
    public static final int REG_SCCNTCTRL   = 0xf10;
    public static final int REG_SCCNTDATA   = 0xf14;
    public static final int REG_SCCNTSTEP   = 0xf18;
    public static final int REG_SCPeriphID0 = 0xfe0;
    public static final int REG_SCPeriphID1 = 0xfe4;
    public static final int REG_SCPeriphID2 = 0xfe8;
    public static final int REG_SCPeriphID3 = 0xfec;
    public static final int REG_SCPCellID0  = 0xff0;
    public static final int REG_SCPCellID1  = 0xff4;
    public static final int REG_SCPCellID2  = 0xff8;
    public static final int REG_SCPCellID3  = 0xffc;

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
