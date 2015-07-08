package net.katsuster.ememu.arm.core;

import net.katsuster.ememu.generic.*;

/**
 * PSR（プログラムステートレジスタ）。
 */
public class PSR extends Reg32 {
    public static final int BIT_N = 31;
    public static final int BIT_Z = 30;
    public static final int BIT_C = 29;
    public static final int BIT_V = 28;
    public static final int BIT_I = 7;
    public static final int BIT_F = 6;
    public static final int BIT_T = 5;

    public static final int MODE_USR = 0x10;
    public static final int MODE_FIQ = 0x11;
    public static final int MODE_IRQ = 0x12;
    public static final int MODE_SVC = 0x13;
    public static final int MODE_ABT = 0x17;
    public static final int MODE_UND = 0x1b;
    public static final int MODE_SYS = 0x1f;

    //PSR の変化を通知するレジスタファイル
    ARMRegFile regfile;

    /**
     * 値 0 を持つ PSR（プログラムステートレジスタ）を作成します。
     */
    public PSR() {
        super("psr", 0);
    }

    /**
     * 初期値を指定して PSR（プログラムステートレジスタ）を作成します。
     *
     * @param name レジスタ名
     * @param val  レジスタの初期値
     * @param rf   PSR が変化したとき通知するレジスタファイル、通知が不要なら null
     */
    public PSR(String name, int val, ARMRegFile rf) {
        super(name, val);
        regfile = rf;
    }

    /**
     * PSR の値を設定します。
     *
     * PSR の値が変化したことをレジスタファイルに通知します。
     *
     * @param v 新しい PSR の値
     */
    @Override
    public void setValue(int v) {
        super.setValue(v);
        if (regfile != null) {
            regfile.notifyChangedPSR();
        }
    }

    /**
     * 別の PSR から値を設定します。
     *
     * @param psr 別の PSR
     */
    public void setValue(PSR psr) {
        setValue(psr.getValue());
    }

    /**
     * 現在のプロセッサの動作モードを取得します。
     *
     * PSR の M フィールド（ビット[4:0]）を返します。
     *
     * @return プロセッサの動作モード
     */
    public int getMode() {
        return getMode(getValue());
    }

    /**
     * PSR（プログラムステートレジスタ）の M フィールド
     * （ビット [4:0]）を取得します。
     *
     * @param val PSR の値
     * @return M フィールドの値
     */
    public static int getMode(int val) {
        return val & 0x1f;
    }

    /**
     * プロセッサの動作モードを設定します。
     *
     * PSR の M フィールド（ビット[4:0]）を変更します。
     *
     * @param mod 新たなプロセッサの動作モード
     */
    public void setMode(int mod) {
        setValue(setMode(getValue(), mod));
    }

    /**
     * PSR（プログラムステートレジスタ）の M フィールド
     * （ビット [4:0]）を設定します。
     *
     * @param val PSR の値
     * @param mod 新たなモード
     * @return 新たな PSR の値
     */
    public static int setMode(int val, int mod) {
        int mask = 0x1f;

        val &= ~mask;
        val |= mod & mask;

        return val;
    }

    /**
     * プロセッサの動作モードの名前を取得します。
     *
     * @return 動作モードの名前
     */
    public String getModeName() {
        return getModeName(getMode());
    }

    /**
     * プロセッサの動作モードの名前を取得します。
     *
     * @param mode プロセッサの動作モード
     * @return 動作モードの名前
     */
    public static String getModeName(int mode) {
        switch (mode) {
        case 0x10:
            return "usr";
        case 0x11:
            return "fiq";
        case 0x12:
            return "irq";
        case 0x13:
            return "svc";
        case 0x17:
            return "abt";
        case 0x1b:
            return "und";
        case 0x1f:
            return "sys";
        default:
            return "???";
        }
    }

    /**
     * PSR（プログラムステートレジスタ）の状態を表す文字列を取得します。
     *
     * @return PSR の状態を表す文字列
     */
    public String getStatusName() {
        return getStatusName(getValue());
    }

    /**
     * PSR（プログラムステートレジスタ）の状態を表す文字列を取得します。
     *
     * @param val PSR の値
     * @return PSR の状態を表す文字列
     */
    public static String getStatusName(int val) {
        return String.format("%s%s%s%s_%s%s%s%5s",
                BitOp.getBit32(val, BIT_N) ? "N" : "n",
                BitOp.getBit32(val, BIT_Z) ? "Z" : "z",
                BitOp.getBit32(val, BIT_C) ? "C" : "c",
                BitOp.getBit32(val, BIT_V) ? "V" : "v",
                BitOp.getBit32(val, BIT_I) ? "I" : "i",
                BitOp.getBit32(val, BIT_F) ? "F" : "f",
                BitOp.getBit32(val, BIT_T) ? "T" : "t",
                getModeName(getMode(val)));
    }

    /**
     * PSR（プログラムステートレジスタ）の
     * N ビット（ビット 31）を取得します。
     *
     * N ビットは演算結果のビット 31 が 1 の場合に設定されます。
     * すなわち演算結果を 2の補数の符号付き整数としてみたとき、
     * 演算結果が正の数であれば N=0、負の数であれば N=1 となります。
     *
     * @return N ビットがセットされていれば true, そうでなければ false
     */
    public boolean getNBit() {
        return BitOp.getBit32(getValue(), BIT_N);
    }

    /**
     * PSR（プログラムステートレジスタ）の
     * N ビット（ビット 31）を設定します。
     *
     * N ビットは演算結果のビット 31 が 1 の場合に設定されます。
     * すなわち演算結果を 2の補数の符号付き整数としてみたとき、
     * 演算結果が正の数であれば N=0、負の数であれば N=1 となります。
     *
     * @param nv N ビットをセットするなら true, クリアするなら false
     */
    public void setNBit(boolean nv) {
        setValue(BitOp.setBit32(getValue(), BIT_N, nv));
    }

    /**
     * PSR（プログラムステートレジスタ）の
     * Z ビット（ビット 30）を取得します。
     *
     * Z ビットは演算結果が 0 の場合に設定されます。
     * 演算結果が 0 以外ならば Z=0、0 ならば Z=1 となります。
     *
     * @return Z ビットがセットされていれば true, そうでなければ false
     */
    public boolean getZBit() {
        return BitOp.getBit32(getValue(), BIT_Z);
    }

    /**
     * PSR（プログラムステートレジスタ）の
     * Z ビット（ビット 30）を設定します。
     *
     * Z ビットは演算結果が 0 の場合に設定されます。
     * 演算結果が 0 以外ならば Z=0、0 ならば Z=1 となります。
     *
     * @param nv Z ビットをセットするなら true, クリアするなら false
     */
    public void setZBit(boolean nv) {
        setValue(BitOp.setBit32(getValue(), BIT_Z, nv));
    }

    /**
     * PSR（プログラムステートレジスタ）の
     * C ビット（ビット 29）を取得します。
     *
     * C ビットは演算結果にキャリー（加算の場合）が生じた場合に設定され、
     * ボロー（減算の場合）が生じた場合にクリアされます。
     * または、シフト演算によりあふれた値が設定されます。
     *
     * - 演算が加算で、
     * 演算によりキャリーが生じなければ C=0、
     * 符号無しオーバーフローしキャリーが生じたならば C=1 となります。
     * - 演算が減算で、
     * 演算により符号無しアンダーフローしボローが生じれば C=0、
     * ボローが生じなければ C=1 となります。
     * - 演算がシフトで、演算によりシフトアウトされた値が 0 ならば C=0、
     * シフトアウトされた値が 1 ならば C=1 となります。
     *
     * @return C ビットがセットされていれば true, そうでなければ false
     */
    public boolean getCBit() {
        return BitOp.getBit32(getValue(), BIT_C);
    }

    /**
     * PSR（プログラムステートレジスタ）の
     * C ビット（ビット 29）を設定します。
     *
     * C ビットは演算結果にキャリー（加算の場合）が生じた場合に設定され、
     * ボロー（減算の場合）が生じた場合にクリアされます。
     * または、シフト演算によりあふれた値が設定されます。
     *
     * - 演算が加算で、
     * 演算によりキャリーが生じなければ C=0、
     * 符号無しオーバーフローしキャリーが生じたならば C=1 となります。
     * - 演算が減算で、
     * 演算により符号無しアンダーフローしボローが生じれば C=0、
     * ボローが生じなければ C=1 となります。
     * - 演算がシフトで、演算によりシフトアウトされた値が 0 ならば C=0、
     * シフトアウトされた値が 1 ならば C=1 となります。
     *
     * @param nv C ビットをセットするなら true, クリアするなら false
     */
    public void setCBit(boolean nv) {
        setValue(BitOp.setBit32(getValue(), BIT_C, nv));
    }

    /**
     * PSR（プログラムステートレジスタ）の
     * V ビット（ビット 28）を取得します。
     *
     * V ビットは演算結果に符号付きオーバーフローした場合に設定されます。
     *
     * - 演算が加算または減算で、
     * 演算により符号付きオーバーフローしなければ V=0、
     * 符号付きオーバーフローしたならば V=1 となります。
     *
     * @return V ビットがセットされていれば true, そうでなければ false
     */
    public boolean getVBit() {
        return BitOp.getBit32(getValue(), BIT_V);
    }

    /**
     * PSR（プログラムステートレジスタ）の
     * V ビット（ビット 28）を設定します。
     *
     * V ビットは演算結果に符号付きオーバーフローした場合に設定されます。
     *
     * - 演算が加算または減算で、
     * 演算により符号付きオーバーフローしなければ V=0、
     * 符号付きオーバーフローしたならば V=1 となります。
     *
     * @param nv V ビットをセットするなら true, クリアするなら false
     */
    public void setVBit(boolean nv) {
        setValue(BitOp.setBit32(getValue(), BIT_V, nv));
    }

    /**
     * PSR（プログラムステートレジスタ）の
     * I ビット（ビット 7）を取得します。
     *
     * I=0 ならば IRQ 割り込みが有効となります。
     * I=1 ならば IRQ 割り込みが無効となります。
     *
     * @return I ビットがセットされていれば true, そうでなければ false
     */
    public boolean getIBit() {
        return BitOp.getBit32(getValue(), BIT_I);
    }

    /**
     * PSR（プログラムステートレジスタ）の
     * I ビット（ビット 7）を設定します。
     *
     * I=0 ならば IRQ 割り込みが有効となります。
     * I=1 ならば IRQ 割り込みが無効となります。
     *
     * @param nv I ビットをセットするなら true, クリアするなら false
     */
    public void setIBit(boolean nv) {
        setValue(BitOp.setBit32(getValue(), BIT_I, nv));
    }

    /**
     * PSR（プログラムステートレジスタ）の
     * F ビット（ビット 6）を取得します。
     *
     * F=0 ならば FIQ 割り込みが有効となります。
     * F=1 ならば FIQ 割り込みが無効となります。
     *
     * @return F ビットがセットされていれば true, そうでなければ false
     */
    public boolean getFBit() {
        return BitOp.getBit32(getValue(), BIT_F);
    }

    /**
     * PSR（プログラムステートレジスタ）の
     * F ビット（ビット 6）を設定します。
     *
     * F=0 ならば FIQ 割り込みが有効となります。
     * F=1 ならば FIQ 割り込みが無効となります。
     *
     * @param nv F ビットをセットするなら true, クリアするなら false
     */
    public void setFBit(boolean nv) {
        setValue(BitOp.setBit32(getValue(), BIT_F, nv));
    }

    /**
     * PSR（プログラムステートレジスタ）の
     * T ビット（ビット 5）を取得します。
     *
     * T=0 ならば ARM 命令を実行します。
     * T=1 ならば Thumb 命令を実行します。
     *
     * ARMv5 以上の非 T バリアント（Thumb 命令非対応）の場合、
     * T=1 ならば次に実行される命令で未定義命令例外を発生させます。
     *
     * @return T ビットがセットされていれば true, そうでなければ false
     */
    public boolean getTBit() {
        return BitOp.getBit32(getValue(), BIT_T);
    }

    /**
     * PSR（プログラムステートレジスタ）の
     * T ビット（ビット 5）を設定します。
     *
     * T=0 ならば ARM 命令を実行します。
     * T=1 ならば Thumb 命令を実行します。
     *
     * ARMv5 以上の非 T バリアント（Thumb 命令非対応）の場合、
     * T=1 ならば次に実行される命令で未定義命令例外を発生させます。
     *
     * @param nv T ビットをセットするなら true, クリアするなら false
     */
    public void setTBit(boolean nv) {
        setValue(BitOp.setBit32(getValue(), BIT_T, nv));
    }

    /**
     * プロセッサモードが特権モードか否かを取得します。
     *
     * @return 特権モードであれば true、特権モードでなければ false
     */
    public boolean isPrivMode() {
        return getMode() != MODE_USR;
    }

    /**
     * この PSR から APSR（アプリケーションプログラムステートレジスタ）を作成します。
     *
     * @return APSR
     */
    public APSR getAPSR() {
        return new APSR("apsr", this);
    }

    @Override
    public String toString() {
        return String.format("%s: 0x%08x(%s)",
                getName(), getValue(), getStatusName());
    }
}
