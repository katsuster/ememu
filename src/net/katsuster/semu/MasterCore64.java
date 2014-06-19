package net.katsuster.semu;

/**
 * アドレス 32bits、データ 64bits のマスターコア。
 *
 * @author katsuhiro
 */
public abstract class MasterCore64 implements MasterCore<Word64> {
    public static long ADDR_MASK_8 = ~0x0L;
    public static long ADDR_MASK_16 = ~0x1L;
    public static long ADDR_MASK_32 = ~0x3L;
    public static long ADDR_MASK_64 = ~0x7L;

    private Bus<Word64> slaveBus;

    @Override
    public Bus<Word64> getSlaveBus() {
        return slaveBus;
    }

    @Override
    public void setSlaveBus(Bus<Word64> bus) {
        bus.setMasterCore(this);
        slaveBus = bus;
    }

    @Override
    public int getBusBits() {
        return 64;
    }

    protected long readWord(int addr) {
        long addrl = addr & 0xffffffffL;
        addrl &= getAddressMask(getBusBits());
        return slaveBus.read(addrl, 8).getData();
    }

    protected void writeWord(int addr, long data) {
        long addrl = addr & 0xffffffffL;
        addrl &= getAddressMask(getBusBits());
        slaveBus.write(addrl, new Word64(data));
    }

    /**
     * 指定されたデータ幅に対応するアドレスマスクを返します。
     *
     * アドレスマスクの値の例:
     * 8bits = ~0L
     *   => マスクなし
     * 16bits = ~1L
     *   => 下位 1ビットを消去するマスク => アドレスは 2の倍数
     * 32bits = ~3L
     *   => 下位 2ビットを消去するマスク => アドレスは 4の倍数
     *
     * @param dataLen データ幅
     * @return アドレスマスク
     */
    public static long getAddressMask(int dataLen) {
        switch (dataLen) {
        case 64:
            return ADDR_MASK_64;
        case 32:
            return ADDR_MASK_32;
        case 16:
            return ADDR_MASK_16;
        case 8:
            return ADDR_MASK_8;
        default:
            throw new IllegalArgumentException("Data length" +
                    String.format("(0x%08x) is not supported.", dataLen));
        }
    }

    /**
     * リトルエンディアンにて、
     * 指定されたアドレスにあるワードを取得します。
     *
     * データバスにはバス幅の倍数のアドレスでのみアクセスできるものとします。
     * 例えば、
     * バス幅が 32bits であればアドレス 0, 4, 8, 12, ... 4n のみ、
     * バス幅が 64bits であればアドレス 0, 8, 16, 24, ... 8n のみ、
     * です。
     *
     * データバス幅より小さいデータ幅を取得するとき、
     * バス幅のデータを取得した後、
     * データをシフトして目的のアドレスにあるデータを取得する必要があります。
     *
     * 例えば、データバス幅が 64bits、データ幅が 16bits のシステムにて、
     * アドレス 0x12 のデータを取得するとします。
     *
     * データバスは 64bits 幅のためアドレス 0x12 はアクセスできません。
     * 従って最も近い 8の倍数であるアドレス 0x10 から 64bits を読み出します。
     *
     * このときバスから読み出したデータが 0x1234_5678_0246_8ace だとします。
     * バスから読み出したデータを 16bits ごとに分割し、
     * 符号ビットから近い順（上位ビットから）から並べると、
     * 0x1234:
     * 0x5678:
     * 0x0246:
     * 0x8ace:
     * となります。
     *
     * リトルエンディアンシステムの場合、データの上位から、
     * アドレス+6, アドレス+4, アドレス+2, アドレス, に対応しますので、
     * 0x1234: アドレス+6
     * 0x5678: アドレス+4
     * 0x0246: アドレス+2
     * 0x8ace: アドレス
     * と対応します。
     *
     * 従って、目的のアドレス 0x12 にあるデータは 0x0246 となり、
     * バスから読み出したデータをシフトする量は 16bits です。
     *
     * 同様に 0x14 ならばデータは 0x5678 となり、シフトする量は 32bits です。
     *
     * @param addr     データのアドレス
     * @param data     バスから読んだデータ
     * @param busLen   データバス幅
     * @param dataLen  データ幅
     * @return addr にあるデータ
     */
    public static long readMasked(long addr, long data, int busLen, int dataLen) {
        long busMask = getAddressMask(busLen);
        long dataMask = getAddressMask(dataLen);
        int sh = (int)(addr & ~busMask & dataMask) * 8;

        return data >> sh;
    }

    /**
     * 指定されたアドレスからデータを読み出せるかどうかを取得します。
     *
     * @param addr アドレス
     * @return 読み出しが可能ならば true、不可能ならば false
     */
    public boolean tryRead(int addr) {
        long addrl = addr & 0xffffffffL;
        addrl &= getAddressMask(getBusBits());
        return slaveBus.tryRead(addrl, 8);
    }

    /**
     * 指定したアドレスから 1バイトを読み出します。
     *
     * @param addr アドレス
     * @return 指定したアドレスにある 1バイトデータ
     */
    public byte read8(int addr) {
        long v = readWord(addr);

        return (byte)readMasked(addr, v, getBusBits(), 8);
    }

    /**
     * 指定したアドレスから 2バイトを読み出します。
     *
     * @param addr アドレス
     * @return 指定したアドレスにある 2バイトデータ
     */
    public short read16(int addr) {
        long v = readWord(addr);

        return (short)readMasked(addr, v, getBusBits(), 16);
    }

    /**
     * 指定したアドレスから 4バイトを読み出します。
     *
     * @param addr アドレス
     * @return 指定したアドレスにある 4バイトデータ
     */
    public int read32(int addr) {
        long v = readWord(addr);

        return (int)readMasked(addr, v, getBusBits(), 32);
    }

    /**
     * 指定したアドレスから 8バイトを読み出します。
     *
     * @param addr アドレス
     * @return 指定したアドレスにある 8バイトデータ
     */
    public long read64(int addr) {
        return readWord(addr);
    }

    /**
     * 指定されたデータ幅に対応するマスクを返します。
     *
     * マスクの値の例:
     * 8bits = 0xffL
     * 16bits = 0xffffL
     * 32bits = 0xffffffffL
     *
     * @param dataLen データ幅
     * @return データマスク
     */
    public static long getDataMask(int dataLen) {
        switch (dataLen) {
        case 64:
            return 0xffffffffffffffffL;
        case 32:
            return 0xffffffffL;
        case 16:
            return 0xffffL;
        case 8:
            return 0xffL;
        default:
            throw new IllegalArgumentException("Data length" +
                    String.format("(0x%08x) is not supported.", dataLen));
        }
    }

    /**
     * リトルエンディアンにて、
     * 指定されたアドレスにあるワードを変更します。
     *
     * データバスにはバス幅の倍数のアドレスでのみアクセスできるものとします。
     * 例えば、
     * バス幅が 32bits であればアドレス 0, 4, 8, 12, ... 4n のみ、
     * バス幅が 64bits であればアドレス 0, 8, 16, 24, ... 8n のみ、
     * です。
     *
     * データバス幅より小さいデータ幅を変更するとき、
     * バス幅のデータを取得した後、
     * データをマスクして目的のアドレスにあるデータを変更する必要があります。
     *
     * 例えば、データバス幅が 64bits、データ幅が 16bits のシステムにて、
     * アドレス 0x12 のデータを変更するとします。
     *
     * データバスは 64bits 幅のためアドレス 0x12 はアクセスできません。
     * 従って最も近い 8の倍数であるアドレス 0x10 から 64bits を読み出します。
     *
     * このときバスから読み出したデータが 0x1234_5678_0246_8ace だとします。
     * バスから読み出したデータを 16bits ごとに分割し、
     * 符号ビットから近い順（上位ビットから）から並べると、
     * 0x1234:
     * 0x5678:
     * 0x0246:
     * 0x8ace:
     * となります。
     *
     * リトルエンディアンシステムの場合、データの上位から、
     * アドレス+6, アドレス+4, アドレス+2, アドレス, に対応しますので、
     * 0x1234: アドレス+6
     * 0x5678: アドレス+4
     * 0x0246: アドレス+2
     * 0x8ace: アドレス
     * と対応します。
     *
     * 従って、目的のアドレス 0x12 にあるデータは 0x0246 となり、
     * バスから読み出したデータを変更するためのシフト量は 16bits です。
     *
     * 同様に 0x14 ならばデータは 0x5678 となり、シフトする量は 32bits です。
     *
     * @param addr     データのアドレス
     * @param data     バスから読んだデータ
     * @param busLen   データバス幅
     * @param dataLen  データ幅
     * @param newData  addr に書き込むデータ
     * @return addr に newData を書き込んだ後のデータ
     */
    public static long writeMasked(long addr, long data, long newData, int busLen, int dataLen) {
        long busMask = getAddressMask(busLen);
        long dataMask = getAddressMask(dataLen);
        long eraseMask = getDataMask(dataLen);
        int sh = (int)(addr & ~busMask & dataMask) * 8;

        return (data & ~(eraseMask << sh)) | ((newData & eraseMask) << sh);
    }

    /**
     * 指定したアドレスにデータを書き込めるかどうかを取得します。
     *
     * @param addr アドレス
     * @return 書き込みが可能ならば true、不可能ならば false
     */
    public boolean tryWrite(int addr) {
        long addrl = addr & 0xffffffffL;
        addrl &= getAddressMask(getBusBits());
        return slaveBus.tryWrite(addrl, 8);
    }

    /**
     * 指定したアドレスに 1バイトを書き込みます。
     *
     * @param addr アドレス
     * @param data 書き込むデータ
     */
    public void write8(int addr, byte data) {
        long v = readWord(addr);
        long w = writeMasked(addr, v, data, getBusBits(), 8);

        writeWord(addr, w);
    }

    /**
     * 指定したアドレスに 2バイトを書き込みます。
     *
     * @param addr アドレス
     * @param data 書き込むデータ
     */
    public void write16(int addr, short data) {
        long v = readWord(addr);
        long w = writeMasked(addr, v, data, getBusBits(), 16);

        writeWord(addr, w);
    }

    /**
     * 指定したアドレスに 4バイトを書き込みます。
     *
     * @param addr アドレス
     * @param data 書き込むデータ
     */
    public void write32(int addr, int data) {
        long v = readWord(addr);
        long w = writeMasked(addr, v, data, getBusBits(), 32);

        writeWord(addr, w);
    }

    /**
     * 指定したアドレスに 8バイトを書き込みます。
     *
     * @param addr アドレス
     * @param data 書き込むデータ
     */
    public void write64(int addr, long data) {
        writeWord(addr, data);
    }
}
