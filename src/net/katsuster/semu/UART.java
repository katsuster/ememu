package net.katsuster.semu;

/**
 * debug UART
 *
 * @author katsuhiro
 */
public class UART implements SlaveCore<Word32> {
    private Bus<Word32> masterBus;

    public UART() {

    }

    @Override
    public Bus<Word32> getMasterBus() {
        return masterBus;
    }

    @Override
    public void setMasterBus(Bus<Word32> bus) {
        masterBus = bus;
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
    public Word32 read(long addr) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    @Override
    public boolean tryWrite(long addr) {
        return tryAccess(addr);
    }

    @Override
    public void write(long addr, Word32 data) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }
}
