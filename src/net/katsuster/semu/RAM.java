package net.katsuster.semu;

import java.io.*;

/**
 * RAM
 *
 * @author katsuhiro
 */
public class RAM<T extends ByteSeq> implements SlaveCore<T> {
    private T[] words;
    private int lenWord;
    private Bus<T> masterBus;

    public RAM(T[] words) {
        if (words.length == 0) {
            throw new IllegalArgumentException("words is empty.");
        }

        this.words = words;
        this.lenWord = words[0].length();
    }

    @Override
    public Bus<T> getMasterBus() {
        return masterBus;
    }

    @Override
    public void setMasterBus(Bus<T> bus) {
        masterBus = bus;
    }

    protected void checkAddress(long addr) {
        if (addr % lenWord != 0) {
            throw new IllegalArgumentException(String.format(
                    "addr(0x%08x) is not aligned %d.", addr, lenWord));
        }
        if (addr / lenWord > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(String.format(
                    "addr(0x%08x) is too large.", addr));
        }
    }

    public boolean tryAccess(long addr) {
        int wordAddr;

        wordAddr = (int)(addr / lenWord);

        return words.length > wordAddr;
    }

    @Override
    public boolean tryRead(long addr) {
        return tryAccess(addr);
    }

    @Override
    public T read(long addr) {
        int wordAddr;

        checkAddress(addr);

        wordAddr = (int)(addr / lenWord);

        return words[wordAddr];
    }

    @Override
    public boolean tryWrite(long addr) {
        return tryAccess(addr);
    }

    @Override
    public void write(long addr, T data) {
        int wordAddr;

        checkAddress(addr);

        wordAddr = (int)(addr / lenWord);

        words[wordAddr] = data;
    }
}
