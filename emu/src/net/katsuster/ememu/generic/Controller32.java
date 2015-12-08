package net.katsuster.ememu.generic;

import java.util.*;

/**
 * 64 ビットアドレスバス、32 ビットレジスタを持つコントローラ。
 *
 * @author katsuhiro
 */
public abstract class Controller32 extends SlaveCore {
    //データ幅（バイト単位）
    public static final int LEN_WORD = 4;
    //データ幅（ビット単位）
    public static final int LEN_WORD_BITS = LEN_WORD * 8;

    private Map<Long, Reg32> regs;

    public Controller32() {
        regs = new HashMap<Long, Reg32>();
    }

    /**
     * レジスタの定義を追加します。
     *
     * @param addr レジスタアドレス
     * @param name レジスタ名
     */
    public void addReg(long addr, String name) {
        regs.put(addr, new Reg32(name, 0));
    }

    /**
     * レジスタの定義を追加します。
     *
     * @param addr レジスタアドレス
     * @param name レジスタ名
     * @param val  レジスタの初期値
     */
    public void addReg(long addr, String name, int val) {
        regs.put(addr, new Reg32(name, val));
    }

    /**
     * レジスタの定義を削除します。
     *
     * @param addr レジスタアドレス
     */
    public void removeReg(long addr) {
        regs.remove(addr);
    }

    /**
     * 指定したアドレスに対応するレジスタを取得します。
     *
     * @param addr レジスタアドレス
     * @return 32ビットレジスタ
     */
    public Reg32 getReg(long addr) {
        Reg32 r;

        r = regs.get(addr);
        if (r == null) {
            throw new IllegalArgumentException(String.format(
                    "Get illegal address 0x%08x.", addr));
        }

        return r;
    }

    /**
     * 指定したアドレスにレジスタが存在するか確認します。
     *
     * @param addr レジスタアドレス
     * @return レジスタが存在すれば true、存在しなければ false
     */
    public boolean isValidReg(long addr) {
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

    @Override
    public boolean tryRead(long addr, int len) {
        return tryAccess(addr, len);
    }

    @Override
    public boolean tryWrite(long addr, int len) {
        return tryAccess(addr, len);
    }

    /**
     * 指定されたアドレスからの読み書きが可能かどうかを判定します。
     *
     * @param addr アドレス
     * @param len  データのサイズ
     * @return 読み書きが可能な場合は true、不可能な場合は false
     */
    public boolean tryAccess(long addr, int len) {
        int regaddr;

        regaddr = (int)(addr & getAddressMask(LEN_WORD_BITS));

        switch (regaddr) {
        default:
            return isValidReg(regaddr);
        }
    }

    /**
     * 指定されたアドレスから 32 ビットのデータを読み取ります。
     *
     * @param addr アドレス
     * @return データ
     */
    public int readWord(long addr) {
        return getReg(addr).getValue();
    }

    /**
     * 指定したアドレスへ 32 ビットのデータを書き込みます。
     *
     * @param addr アドレス
     * @param data データ
     */
    public void writeWord(long addr, int data) {
        getReg(addr).setValue(data);
    }

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
