package net.katsuster.ememu.arm;

/**
 * 64 ビットアドレスバスのマスターコア。
 *
 * 自身のタイミングで動作します。
 * バスに read/write 要求を出します。
 *
 * バスへのアクセス時に用いるアドレスは 64 ビット幅です。
 *
 * TODO: 現在、コア内部アドレスは 32 ビット幅を用いています。
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
     * @return 読み出しが可能ならば true、不可能ならば false
     */
    public boolean tryRead(int addr) {
        long addrl = addr & 0xffffffffL;
        return slaveBus.tryRead(addrl, 4);
    }

    /**
     * 指定したアドレスから 8 ビットを読み出します。
     *
     * @param addr アドレス
     * @return 指定したアドレスにあるデータ
     */
    public byte read8(int addr) {
        long addrl = addr & 0xffffffffL;
        return slaveBus.read8(addrl);
    }

    /**
     * 指定したアドレスから 16 ビットを読み出します。
     *
     * @param addr アドレス
     * @return 指定したアドレスにあるデータ
     */
    public short read16(int addr) {
        long addrl = addr & 0xffffffffL;
        return slaveBus.read16(addrl);
    }

    /**
     * 指定したアドレスから 32 ビットを読み出します。
     *
     * @param addr アドレス
     * @return 指定したアドレスにあるデータ
     */
    public int read32(int addr) {
        long addrl = addr & 0xffffffffL;
        return slaveBus.read32(addrl);
    }

    /**
     * 指定したアドレスから 64 ビットを読み出します。
     *
     * @param addr アドレス
     * @return 指定したアドレスにあるデータ
     */
    public long read64(int addr) {
        long addrl = addr & 0xffffffffL;
        return slaveBus.read64(addrl);
    }

    /**
     * 指定したアドレスにデータを書き込めるかどうかを取得します。
     *
     * @param addr アドレス
     * @return 書き込みが可能ならば true、不可能ならば false
     */
    public boolean tryWrite(int addr) {
        long addrl = addr & 0xffffffffL;
        return slaveBus.tryWrite(addrl, 4);
    }

    /**
     * 指定したアドレスに 8 ビットを書き込みます。
     *
     * @param addr アドレス
     * @param data 書き込むデータ
     */
    public void write8(int addr, byte data) {
        long addrl = addr & 0xffffffffL;
        slaveBus.write8(addrl, data);
    }

    /**
     * 指定したアドレスに 16 ビットを書き込みます。
     *
     * @param addr アドレス
     * @param data 書き込むデータ
     */
    public void write16(int addr, short data) {
        long addrl = addr & 0xffffffffL;
        slaveBus.write16(addrl, data);
    }

    /**
     * 指定したアドレスに 32 ビットを書き込みます。
     *
     * @param addr アドレス
     * @param data 書き込むデータ
     */
    public void write32(int addr, int data) {
        long addrl = addr & 0xffffffffL;
        slaveBus.write32(addrl, data);
    }

    /**
     * 指定したアドレスに 64 ビットを書き込みます。
     *
     * @param addr アドレス
     * @param data 書き込むデータ
     */
    public void write64(int addr, long data) {
        long addrl = addr & 0xffffffffL;
        slaveBus.write64(addrl, data);
    }
}
