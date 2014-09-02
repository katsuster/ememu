package net.katsuster.semu.arm;

import java.util.*;

/**
 * 64 ビットアドレスバス（内部アドレスは 32 ビット）、
 * 32 ビットレジスタのコントローラ
 *
 * @author katsuhiro
 */
public abstract class Controller64Reg32 extends SlaveCore64 {
    //データ幅（バイト単位）
    public static final int LEN_WORD = 4;
    //データ幅（ビット単位）
    public static final int LEN_WORD_BITS = LEN_WORD * 8;

    private Map<Integer, Reg32> regs;

    public Controller64Reg32() {
        regs = Collections.synchronizedMap(
                new HashMap<Integer, Reg32>());
    }

    /**
     * レジスタの定義を追加します。
     *
     * @param addr レジスタアドレス
     * @param name レジスタ名
     */
    public void addReg(int addr, String name) {
        regs.put(addr, new Reg32(name, 0));
    }

    /**
     * レジスタの定義を追加します。
     *
     * @param addr レジスタアドレス
     * @param name レジスタ名
     * @param val  レジスタの初期値
     */
    public void addReg(int addr, String name, int val) {
        regs.put(addr, new Reg32(name, val));
    }

    /**
     * 指定したアドレスにレジスタが存在するか確認します。
     *
     * @param addr レジスタアドレス
     * @return レジスタが存在すれば true、存在しなければ false
     */
    public boolean isValidReg(int addr) {
        Reg32 r;

        r = regs.get(addr);
        if (r == null) {
            //TODO: for debug, will be removed
            throw new IllegalArgumentException(String.format(
                    "Illegal address 0x%08x.", addr));
        }

        //return (r != null);
        return true;
    }

    /**
     * レジスタの値を取得します。
     *
     * @param addr レジスタアドレス
     * @return レジスタの値
     */
    public int getReg(int addr) {
        Reg32 r;

        r = regs.get(addr);
        if (r == null) {
            throw new IllegalArgumentException(String.format(
                    "Illegal address 0x%08x.", addr));
        }

        return r.getValue();
    }

    /**
     * コプロセッサレジスタの値を設定します。
     *
     * @param addr レジスタアドレス
     * @param val 新しいレジスタの値
     */
    public void setReg(int addr, int val) {
        Reg32 r;

        r = regs.get(addr);
        if (r == null) {
            throw new IllegalArgumentException(String.format(
                    "Illegal address 0x%08x.", addr));
        }

        r.setValue(val);
    }

    @Override
    public abstract boolean tryRead(long addr);

    @Override
    public abstract boolean tryWrite(long addr);

    /**
     * 指定されたアドレスから 32 ビットのデータを読み取ります。
     *
     * @param addr アドレス
     * @return データ
     */
    public abstract int readWord(long addr);

    /**
     * 指定したアドレスへ 32 ビットのデータを書き込みます。
     *
     * @param addr アドレス
     * @param data データ
     */
    public abstract void writeWord(long addr, int data);

    @Override
    public byte read8(long addr) {
        long v = readWord(addr);

        return (byte)readMasked(addr, v, LEN_WORD_BITS, 8);
    }

    @Override
    public short read16(long addr) {
        long v = readWord(addr);

        return (short)readMasked(addr, v, LEN_WORD_BITS, 16);
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
    public void write8(long addr, byte data) {
        int w = (int)writeMasked(addr, 0, data, LEN_WORD_BITS, 8);

        writeWord(addr, w);
    }

    @Override
    public void write16(long addr, short data) {
        int w = (int)writeMasked(addr, 0, data, LEN_WORD_BITS, 16);

        writeWord(addr, w);
    }

    @Override
    public void write32(long addr, int data) {
        int w = (int)writeMasked(addr, 0, data, LEN_WORD_BITS, 32);

        writeWord(addr, w);
    }

    @Override
    public void write64(long addr, long data) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }
}
