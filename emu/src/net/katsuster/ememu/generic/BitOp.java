package net.katsuster.ememu.generic;

/**
 * 整数値へのビット演算ユーティリティクラス。
 */
public class BitOp {
    final public static long ADDR_MASK_8 = ~0x0L;
    final public static long ADDR_MASK_16 = ~0x1L;
    final public static long ADDR_MASK_32 = ~0x3L;
    final public static long ADDR_MASK_64 = ~0x7L;
    final public static long DATA_MASK_8 = 0xffL;
    final public static long DATA_MASK_16 = 0xffffL;
    final public static long DATA_MASK_32 = 0xffffffffL;
    final public static long DATA_MASK_64 = 0xffffffffffffffffL;

    /**
     * 整数値の bit ビット目の値を取得します。
     *
     * ビット位置に 32 ビット以上を指定した場合、
     * 下位 5 ビットが有効となります。
     *
     * @param val 整数値
     * @param bit ビット位置
     * @return ビットがセットされていれば true、クリアされていれば false
     */
    public static boolean getBit32(int val, int bit) {
        return ((val >> bit) & 0x1) == 1;
    }

    /**
     * 整数値の bit ビット目の値を取得します。
     *
     * ビット位置に 64 ビット以上を指定した場合、
     * 下位 6 ビットが有効となります。
     *
     * @param val 整数値
     * @param bit ビット位置
     * @return ビットがセットされていれば true、クリアされていれば false
     */
    public static boolean getBit64(long val, int bit) {
        return ((val >> bit) & 0x1) == 1;
    }

    /**
     * 整数値の bit ビット目の値を設定します。
     *
     * ビット位置に 32 ビット以上を指定した場合、
     * 下位 5 ビットが有効となります。
     *
     * @param val 整数値
     * @param bit ビット位置
     * @param nv  新しいビットの値、セットするなら true、クリアするなら false
     * @return val の指定されたビット位置を変更した後の値
     */
    public static int setBit32(int val, int bit, boolean nv) {
        int m = 1 << bit;

        if (nv) {
            return val | m;
        } else {
            return val & ~m;
        }
    }

    /**
     * 整数値の bit ビット目の値を設定します。
     *
     * ビット位置に 64 ビット以上を指定した場合、
     * 下位 6 ビットが有効となります。
     *
     * @param val 整数値
     * @param bit ビット位置
     * @param nv  新しいビットの値、セットするなら true、クリアするなら false
     * @return val の指定されたビット位置を変更した後の値
     */
    public static long setBit64(long val, int bit, boolean nv) {
        long m = 1 << bit;

        if (nv) {
            return val | m;
        } else {
            return val & ~m;
        }
    }

    /**
     * 整数値の指定された位置にあるビットフィールドの値を取得します。
     *
     * ビット位置に 32 ビット以上を指定した場合、
     * 下位 5 ビットが有効となります。
     *
     * ビットフィールドの長さに 32 ビット以上を指定した場合、
     * 長さは 32 ビットとなります。
     *
     * ビット位置とビットフィールドの長さの合計が 32 ビットを超えた位置を参照する場合、
     * 全てのビットは 0 で埋められます。
     * すなわち 32 ビットより上位のビットに全て 0 が入っているかのように振舞います。
     *
     * @param val 整数値
     * @param pos ビット位置
     * @param len ビットフィールドの長さ
     * @return ビットフィールドの値
     */
    public static int getField32(int val, int pos, int len) {
        int mask;

        if (len >= 32) {
            mask = 0xffffffff;
        } else {
            mask = (1 << len) - 1;
        }

        return (val >>> pos) & mask;
    }

    /**
     * 整数値の指定された位置にあるビットフィールドの値を取得します。
     *
     * ビット位置に 64 ビット以上を指定した場合、
     * 下位 6 ビットが有効となります。
     *
     * ビットフィールドの長さに 64 ビット以上を指定した場合、
     * 長さは 64 ビットとなります。
     *
     * ビット位置とビットフィールドの長さの合計が 64 ビットを超えた位置を参照する場合、
     * 全てのビットは 0 で埋められます。
     * すなわち 64 ビットより上位のビットに全て 0 が入っているかのように振舞います。
     *
     * @param val 整数値
     * @param pos ビット位置
     * @param len ビットフィールドの長さ
     * @return ビットフィールドの値
     */
    public static long getField64(long val, int pos, int len) {
        long mask;

        if (len >= 64) {
            mask = 0xffffffffffffffffL;
        } else {
            mask = (1L << len) - 1;
        }

        return (val >>> pos) & mask;
    }

    /**
     * 整数値の指定された位置にあるビットフィールドの値を設定します。
     *
     * ビット位置に 32 ビット以上を指定した場合、
     * 下位 5 ビットが有効となります。
     *
     * ビットフィールドの長さに 32 ビット以上を指定した場合、
     * 長さは 32 ビットとなります。
     *
     * @param val 整数値
     * @param pos ビット位置
     * @param len ビットフィールドの長さ
     * @param nv  ビットフィールドに設定する値
     * @return val の指定されたビットフィールドを変更した後の値
     */
    public static int setField32(int val, int pos, int len, int nv) {
        int mask;

        if (len >= 32) {
            mask = 0xffffffff;
        } else {
            mask = (1 << len) - 1;
        }
        mask <<= pos;
        nv <<= pos;

        return (val & ~mask) | (nv & mask);
    }

    /**
     * 整数値の指定された位置にあるビットフィールドの値を設定します。
     *
     * ビット位置に 64 ビット以上を指定した場合、
     * 下位 6 ビットが有効となります。
     *
     * ビットフィールドの長さに 64 ビット以上を指定した場合、
     * 長さは 64 ビットとなります。
     *
     * @param val 整数値
     * @param pos ビット位置
     * @param len ビットフィールドの長さ
     * @param nv  ビットフィールドに設定する値
     * @return val の指定されたビットフィールドを変更した後の値
     */
    public static long setField64(long val, int pos, int len, long nv) {
        long mask;

        if (len >= 64) {
            mask = 0xffffffffffffffffL;
        } else {
            mask = (1L << len) - 1;
        }
        mask <<= pos;
        nv <<= pos;

        return (val & ~mask) | (nv & mask);
    }

    /**
     * ブール値を 1/0 に変換します。
     *
     * ビットフラグを 1 または 0 の数値として扱うときに使用します。
     *
     * @param b ブール値
     * @return true の場合は 1、false の場合は 0
     */
    public static int toInt(boolean b) {
        if (b) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 符号拡張を行います。
     *
     * @param v 任意の値
     * @param n 値のビット数、32 を指定すると v を返します。
     *          33 以上を指定すると 32 で割った余りを使います。
     * @return v を符号拡張した値
     */
    public static int signExt32(int v, int n) {
        int sb, mb;

        if (n <= 0) {
            return 0;
        }

        sb = 1 << (n - 1);
        mb = (-1 << (n - 1)) << 1;
        v &= ~mb;
        if ((v & sb) != 0) {
            v = mb + v;
        }

        return v;
    }

    /**
     * 符号拡張を行います。
     *
     * @param v 任意の値
     * @param n 値のビット数、64 を指定すると v を返します。
     *          65 以上を指定すると 64 で割った余りを使います。
     * @return v を符号拡張した値
     */
    public static long signExt64(long v, int n) {
        long sb, mb;

        if (n <= 0) {
            return 0;
        }

        sb = 1L << (n - 1);
        mb = (-1L << (n - 1)) << 1;
        v &= ~mb;
        if ((v & sb) != 0) {
            v = mb + v;
        }

        return v;
    }

    /**
     * 指定されたデータ幅に対応するアドレスマスクを返します。
     *
     * <pre>
     * アドレスマスクの値の例:
     * 8bits = ~0L
     *   -- マスクなし
     * 16bits = ~1L
     *   -- 下位 1ビットを消去するマスク -- アドレスは 2の倍数
     * 32bits = ~3L
     *   -- 下位 2ビットを消去するマスク -- アドレスは 4の倍数
     * </pre>
     *
     * @param dataLen データ幅
     * @return アドレスマスク
     */
    public static long getAddressMask(int dataLen) {
        switch (dataLen) {
        case 8:
            return ADDR_MASK_8;
        case 16:
            return ADDR_MASK_16;
        case 32:
            return ADDR_MASK_32;
        case 64:
            return ADDR_MASK_64;
        default:
            throw new IllegalArgumentException("Data length" +
                    String.format("(0x%08x) is not supported.", dataLen));
        }
    }

    /**
     * 指定されたデータ幅に対応するマスクを返します。
     *
     * <pre>
     * マスクの値の例:
     * 8bits = 0xffL
     * 16bits = 0xffffL
     * 32bits = 0xffffffffL
     * </pre>
     *
     * @param dataLen データ幅
     * @return データマスク
     */
    public static long getDataMask(int dataLen) {
        switch (dataLen) {
        case 8:
            return DATA_MASK_8;
        case 16:
            return DATA_MASK_16;
        case 32:
            return DATA_MASK_32;
        case 64:
            return DATA_MASK_64;
        default:
            throw new IllegalArgumentException("Data length" +
                    String.format("(0x%08x) is not supported.", dataLen));
        }
    }

    /**
     * リトルエンディアンにて、
     * 指定されたアドレスにあるワードを取得します。
     *
     * バスにはデータ幅の倍数のアドレスでのみアクセスできるものとします。
     * 例えば、
     * <pre>
     * バスのデータ幅が 32bits であればアドレス 0, 4, 8, 12, ... 4n のみ、
     * バスのデータ幅が 64bits であればアドレス 0, 8, 16, 24, ... 8n のみ、
     * </pre>
     * です。
     *
     * バスのデータ幅より小さいデータを取得するとき、
     * バス幅のデータを取得した後に得られた値をシフトして、
     * 目的のアドレスにあるデータを取得する必要があります。
     *
     * 例えば、バスのデータ幅が 64bits、データ幅が 16bits のシステムにて、
     * アドレス 0x12 のデータを取得するとします。
     *
     * バスのデータ幅は 64bits 幅のためアドレス 0x12 はアクセスできません。
     * 従って最も近い 8の倍数であるアドレス 0x10 から 64bits を読み出します。
     *
     * このときバスから読み出したデータが 0x1234_5678_0246_8ace だとします。
     * バスから読み出したデータを 16bits ごとに分割し、
     * 符号ビットから近い順（上位ビットから）から並べると、
     * <pre>
     * 0x1234:
     * 0x5678:
     * 0x0246:
     * 0x8ace:
     * </pre>
     * となります。
     *
     * リトルエンディアンシステムの場合、データの上位から、
     * アドレス+6, アドレス+4, アドレス+2, アドレス, に対応しますので、
     * <pre>
     * 0x1234: アドレス+6
     * 0x5678: アドレス+4
     * 0x0246: アドレス+2
     * 0x8ace: アドレス
     * </pre>
     * と対応します。
     *
     * 従って、目的のアドレス 0x12 にあるデータは 0x0246 となり、
     * バスから読み出したデータをシフトする量は 16bits です。
     *
     * 同様に 0x14 ならばデータは 0x5678 となり、シフトする量は 32bits です。
     *
     * バス幅およびデータ幅に指定可能な数値は、2のべき乗（8, 16, 32, 64）のみです。
     *
     * バス幅の倍数ではないアドレスを指定した場合、アドレス以下の最も近いバス幅の倍数に丸められます。
     * 例えば、32bit バスに 0x11 を指定した場合、0x10 と同様に扱われます。
     * 同様に 0x12, 0x13 を指定した場合、0x10 と同様に扱われます。
     *
     * @param addr     データのアドレス（バイト単位）
     * @param data     バスから読んだデータ
     * @param busLen   バスのデータ幅（ビット単位）
     * @param dataLen  データ幅（ビット単位）
     * @return addr にあるデータ
     */
    public static long readMasked(long addr, long data, int busLen, int dataLen) {
        long busMask = getAddressMask(busLen);
        long dataMask = getAddressMask(dataLen);
        int sh = (int)(addr & ~busMask & dataMask) * 8;

        return data >> sh;
    }

    /**
     * リトルエンディアンにて、
     * 指定されたアドレスにあるワードを変更します。
     *
     * バスにはバスのデータ幅の倍数のアドレスでのみアクセスできるものとします。
     * 例えば、
     * <pre>
     * バス幅が 32bits であればアドレス 0, 4, 8, 12, ... 4n のみ、
     * バス幅が 64bits であればアドレス 0, 8, 16, 24, ... 8n のみ、
     * </pre>
     * です。
     *
     * バスのデータ幅より小さいデータ幅を変更するとき、
     * バスのデータ幅のデータを取得した後に得られた値をマスクして、
     * 目的のアドレスにあるデータを変更する必要があります。
     *
     * 例えば、データのバス幅が 64bits、データ幅が 16bits のシステムにて、
     * アドレス 0x12 のデータを変更するとします。
     *
     * バスのデータ幅は 64bits のためアドレス 0x12 はアクセスできません。
     * 従って最も近い 8の倍数であるアドレス 0x10 から 64bits を読み出します。
     *
     * このときバスから読み出したデータが 0x1234_5678_0246_8ace だとします。
     * バスから読み出したデータを 16bits ごとに分割し、
     * 符号ビットから近い順（上位ビットから）から並べると、
     * <pre>
     * 0x1234:
     * 0x5678:
     * 0x0246:
     * 0x8ace:
     * </pre>
     * となります。
     *
     * リトルエンディアンシステムの場合、データの上位から、
     * アドレス+6, アドレス+4, アドレス+2, アドレス, に対応しますので、
     * <pre>
     * 0x1234: アドレス+6
     * 0x5678: アドレス+4
     * 0x0246: アドレス+2
     * 0x8ace: アドレス
     * </pre>
     * と対応します。
     *
     * 従って、目的のアドレス 0x12 にあるデータは 0x0246 となり、
     * バスから読み出したデータを変更するためのシフト量は 16bits です。
     *
     * 同様に 0x14 ならばデータは 0x5678 となり、シフトする量は 32bits です。
     *
     * バス幅およびデータ幅に指定可能な数値は、2のべき乗（8, 16, 32, 64）のみです。
     *
     * バス幅の倍数ではないアドレスを指定した場合、アドレス以下の最も近いバス幅の倍数に丸められます。
     * 例えば、32bit バスに 0x11 を指定した場合、0x10 と同様に扱われます。
     * 同様に 0x12, 0x13 を指定した場合、0x10 と同様に扱われます。
     *
     * @param addr     データのアドレス（バイト単位）
     * @param data     バスから読んだデータ
     * @param busLen   バスのデータ幅（ビット単位）
     * @param dataLen  書き込むデータ幅（ビット単位）
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
     * リトルエンディアンにて、
     * アラインされていないアドレスから、指定された長さのデータを取得します。
     *
     * バスにはデータ幅の倍数のアドレスでのみアクセスできるものとします。
     * 例えば、
     * <pre>
     * バスのデータ幅が 32bits であればアドレス 0, 4, 8, 12, ... 4n のみ、
     * バスのデータ幅が 64bits であればアドレス 0, 8, 16, 24, ... 8n のみ、
     * </pre>
     * です。
     *
     * バスのデータ幅より小さいデータを取得するとき、
     * バス幅のデータを取得した後に得られた値をシフトして、
     * 目的のアドレスにあるデータを取得する必要があります。
     *
     * 例えば、バスのデータ幅が 64bits、データ幅が 16bits のシステムにて、
     * アドレス 0x12 のデータを取得するとします。
     *
     * バスのデータ幅は 64bits 幅のためアドレス 0x12 はアクセスできません。
     * 従って最も近い 8の倍数であるアドレス 0x10 から 64bits を読み出します。
     *
     * このときバスから読み出したデータが 0x1234_5678_0246_8ace だとします。
     * バスから読み出したデータを 16bits ごとに分割し、
     * 符号ビットから近い順（上位ビットから）から並べると、
     * <pre>
     * 0x1234:
     * 0x5678:
     * 0x0246:
     * 0x8ace:
     * </pre>
     * となります。
     *
     * リトルエンディアンシステムの場合、データの上位から、
     * アドレス+6, アドレス+4, アドレス+2, アドレス, に対応しますので、
     * <pre>
     * 0x1234: アドレス+6
     * 0x5678: アドレス+4
     * 0x0246: アドレス+2
     * 0x8ace: アドレス
     * </pre>
     * と対応します。
     *
     * 従って、目的のアドレス 0x12 にあるデータは 0x0246 となり、
     * バスから読み出したデータをシフトする量は 16bits です。
     *
     * 同様に 0x13 ならばデータは 0x468a となり、シフトする量は 24bits です。
     *
     * バス幅に指定可能な数値は、2のべき乗（8, 16, 32, 64）のみです。
     * データ幅に指定可能な数値は、8の倍数のみです。
     *
     * アドレスのバス内でのオフセットと、データ幅の合計がバス幅を超える場合、
     * IllegalArgumentException 例外をスローします。
     *
     * @param addr     データのアドレス（バイト単位）
     * @param data     バスから読んだデータ
     * @param busLen   バスのデータ幅（ビット単位）
     * @param dataLen  データ幅（ビット単位）
     * @return addr にあるデータ
     */
    public static long unalignedReadMasked(long addr, long data, int busLen, int dataLen) {
        long busMask = getAddressMask(busLen);
        int offLen = (int)(addr & ~busMask) << 3;
        if (offLen + dataLen > busLen) {
            throw new IllegalArgumentException(String.format(
                    "Offset %d + data %d is exceeded bus %dbits.",
                    offLen, dataLen, busLen));
        }

        return BitOp.getField64(data, offLen, dataLen);
    }

    /**
     * リトルエンディアンにて、
     * アラインされていないアドレスにあるワードを変更します。
     *
     * バスにはバスのデータ幅の倍数のアドレスでのみアクセスできるものとします。
     * 例えば、
     * <pre>
     * バス幅が 32bits であればアドレス 0, 4, 8, 12, ... 4n のみ、
     * バス幅が 64bits であればアドレス 0, 8, 16, 24, ... 8n のみ、
     * </pre>
     * です。
     *
     * バスのデータ幅より小さいデータ幅を変更するとき、
     * バスのデータ幅のデータを取得した後に得られた値をマスクして、
     * 目的のアドレスにあるデータを変更する必要があります。
     *
     * 例えば、データのバス幅が 64bits、データ幅が 16bits のシステムにて、
     * アドレス 0x12 のデータを変更するとします。
     *
     * バスのデータ幅は 64bits のためアドレス 0x12 はアクセスできません。
     * 従って最も近い 8の倍数であるアドレス 0x10 から 64bits を読み出します。
     *
     * このときバスから読み出したデータが 0x1234_5678_0246_8ace だとします。
     * バスから読み出したデータを 16bits ごとに分割し、
     * 符号ビットから近い順（上位ビットから）から並べると、
     * <pre>
     * 0x1234:
     * 0x5678:
     * 0x0246:
     * 0x8ace:
     * </pre>
     * となります。
     *
     * リトルエンディアンシステムの場合、データの上位から、
     * アドレス+6, アドレス+4, アドレス+2, アドレス, に対応しますので、
     * <pre>
     * 0x1234: アドレス+6
     * 0x5678: アドレス+4
     * 0x0246: アドレス+2
     * 0x8ace: アドレス
     * </pre>
     * と対応します。
     *
     * 従って、目的のアドレス 0x12 にあるデータは 0x0246 となり、
     * バスから読み出したデータを変更するためのシフト量は 16bits です。
     *
     * 同様に 0x13 ならばデータは 0x468a となり、シフトする量は 24bits です。
     *
     * バス幅に指定可能な数値は、2のべき乗（8, 16, 32, 64）のみです。
     * データ幅に指定可能な数値は、8の倍数のみです。
     *
     * アドレスのバス内でのオフセットと、データ幅の合計がバス幅を超える場合、
     * IllegalArgumentException 例外をスローします。
     *
     * @param addr     データのアドレス（バイト単位）
     * @param data     バスから読んだデータ
     * @param busLen   バスのデータ幅（ビット単位）
     * @param dataLen  書き込むデータ幅（ビット単位）
     * @param newData  addr に書き込むデータ
     * @return addr に newData を書き込んだ後のデータ
     */
    public static long unalignedWriteMasked(long addr, long data, long newData, int busLen, int dataLen) {
        long busMask = getAddressMask(busLen);
        int offLen = (int)(addr & ~busMask) << 3;
        if (offLen + dataLen > busLen) {
            throw new IllegalArgumentException(String.format(
                    "Offset %d + data %d is exceeded bus %dbits.",
                    offLen, dataLen, busLen));
        }

        return BitOp.setField64(data, offLen, dataLen, newData);
    }
}
