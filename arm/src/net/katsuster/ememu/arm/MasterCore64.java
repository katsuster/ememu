package net.katsuster.ememu.arm;

/**
 * 64 ビットアドレスバスのマスターコア。
 *
 * 自身のタイミングで動作します。
 * バスに read/write 要求を出します。
 *
 * バスへのアクセス時に用いるアドレスは 64 ビット幅です。
 *
 * @author katsuhiro
 */
public abstract class MasterCore64 extends Core {
    private Bus64 slaveBus;

    /**
     * コアが接続されているスレーブバスを取得します。
     *
     * @return スレーブバス
     */
    public Bus64 getSlaveBus() {
        return slaveBus;
    }

    /**
     * スレーブバスにコアを接続します。
     *
     * @param bus スレーブバス
     */
    public void setSlaveBus(Bus64 bus) {
        slaveBus = bus;
    }

    /**
     * 指定されたアドレスからデータを読み出せるかどうかを取得します。
     *
     * @param addr アドレス
     * @param len  読み取るデータのサイズ
     * @return 読み出しが可能ならば true、不可能ならば false
     */
    public boolean tryRead(long addr, int len) {
        return slaveBus.tryRead(addr, len);
    }

    /**
     * 指定したアドレスから 8 ビットを読み出します。
     *
     * @param addr アドレス
     * @return 指定したアドレスにあるデータ
     */
    public byte read8(long addr) {
        return slaveBus.read8(addr);
    }

    /**
     * 指定したアドレスから 16 ビットを読み出します。
     *
     * @param addr アドレス
     * @return 指定したアドレスにあるデータ
     */
    public short read16(long addr) {
        return slaveBus.read16(addr);
    }

    /**
     * 指定したアドレスから 32 ビットを読み出します。
     *
     * @param addr アドレス
     * @return 指定したアドレスにあるデータ
     */
    public int read32(long addr) {
        return slaveBus.read32(addr);
    }

    /**
     * 指定したアドレスから 64 ビットを読み出します。
     *
     * @param addr アドレス
     * @return 指定したアドレスにあるデータ
     */
    public long read64(long addr) {
        return slaveBus.read64(addr);
    }

    /**
     * 指定したアドレスにデータを書き込めるかどうかを取得します。
     *
     * @param addr アドレス
     * @param len  書き込むデータのサイズ
     * @return 書き込みが可能ならば true、不可能ならば false
     */
    public boolean tryWrite(long addr, int len) {
        return slaveBus.tryWrite(addr, len);
    }

    /**
     * 指定したアドレスに 8 ビットを書き込みます。
     *
     * @param addr アドレス
     * @param data 書き込むデータ
     */
    public void write8(long addr, byte data) {
        slaveBus.write8(addr, data);
    }

    /**
     * 指定したアドレスに 16 ビットを書き込みます。
     *
     * @param addr アドレス
     * @param data 書き込むデータ
     */
    public void write16(long addr, short data) {
        slaveBus.write16(addr, data);
    }

    /**
     * 指定したアドレスに 32 ビットを書き込みます。
     *
     * @param addr アドレス
     * @param data 書き込むデータ
     */
    public void write32(long addr, int data) {
        slaveBus.write32(addr, data);
    }

    /**
     * 指定したアドレスに 64 ビットを書き込みます。
     *
     * @param addr アドレス
     * @param data 書き込むデータ
     */
    public void write64(long addr, long data) {
        slaveBus.write64(addr, data);
    }
}
