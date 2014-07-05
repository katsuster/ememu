package net.katsuster.semu;

/**
 * debug UART
 *
 * @author katsuhiro
 */
public class UART extends SlaveCore64 {
    public UART() {

    }

    public boolean tryAccess(long addr) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    @Override
    public boolean tryRead(long addr) {
        return tryAccess(addr);
    }

    @Override
    public byte read8(long addr) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    @Override
    public short read16(long addr) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    @Override
    public int read32(long addr) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
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
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    @Override
    public void write16(long addr, short data) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");

    }

    @Override
    public void write32(long addr, int data) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    @Override
    public void write64(long addr, long data) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }
}
