package net.katsuster.ememu.generic;

import java.util.concurrent.locks.*;

/**
 * 64bit CPU のパイプラインのステージ。
 *
 * ステージは CPU が命令を解釈、実行するため、各々が独立して実行できる工程を指します。
 * 例えばフェッチ、デコード、実行ステージに分けることができます。
 */
public class Stage64 {
    private CPU64 core;

    /**
     * CPU コアのステージを生成します。
     *
     * @param c ステージを使う CPU コア
     */
    public Stage64(CPU64 c) {
        core = c;
    }

    /**
     * ステージが属する CPU コアを取得します。
     *
     * @return CPU コア
     */
    public CPU64 getCore() {
        return core;
    }

    /**
     * スレーブバスの読み込みロックを取得します。
     *
     * @return 読み込みロック
     */
    public Lock getReadLock() {
        return getCore().getReadLock();
    }

    /**
     * スレーブバスの書き込み、読み込みロックを取得します。
     *
     * @return 書き込みロック
     */
    public Lock getWriteLock() {
        return getCore().getWriteLock();
    }

    /**
     * 指定されたアドレスからの読み取りが可能かどうかを判定します。
     *
     * @param addr アドレス
     * @param len  読み取るデータのサイズ
     * @return 読み取りが可能な場合は true、不可能な場合は false
     */
    public boolean tryRead(long addr, int len) {
        return getCore().tryRead(addr, len);
    }

    /**
     * 指定されたアドレスから 8 ビットのデータを読み取ります。
     *
     * @param addr アドレス
     * @return データ
     */
    public byte read8(long addr) {
        return getCore().read8(addr);
    }

    /**
     * 指定されたアドレスから 16 ビットのデータを読み取ります。
     *
     * @param addr アドレス
     * @return データ
     */
    public short read16(long addr) {
        return getCore().read16(addr);
    }

    /**
     * 指定されたアドレスから 32 ビットのデータを読み取ります。
     *
     * @param addr アドレス
     * @return データ
     */
    public int read32(long addr) {
        return getCore().read32(addr);
    }

    /**
     * 指定されたアドレスから 64 ビットのデータを読み取ります。
     *
     * @param addr アドレス
     * @return データ
     */
    public long read64(long addr) {
        return getCore().read64(addr);
    }

    /**
     * 指定されたアドレスへの書き込みが可能かどうかを判定します。
     *
     * @param addr アドレス
     * @param len  書き込むデータのサイズ
     * @return 書き込みが可能な場合は true、不可能な場合は false
     */
    public boolean tryWrite(long addr, int len) {
        return getCore().tryWrite(addr, len);
    }

    /**
     * 指定したアドレスへ 8 ビットのデータを書き込みます。
     *
     * @param addr アドレス
     * @param data データ
     */
    public void write8(long addr, byte data) {
        getCore().write8(addr, data);
    }

    /**
     * 指定したアドレスへ 16 ビットのデータを書き込みます。
     *
     * @param addr アドレス
     * @param data データ
     */
    public void write16(long addr, short data) {
        getCore().write16(addr, data);
    }

    /**
     * 指定したアドレスへ 32 ビットのデータを書き込みます。
     *
     * @param addr アドレス
     * @param data データ
     */
    public void write32(long addr, int data) {
        getCore().write32(addr, data);
    }

    /**
     * 指定したアドレスへ 64 ビットのデータを書き込みます。
     *
     * @param addr アドレス
     * @param data データ
     */
    public void write64(long addr, long data) {
        getCore().write64(addr, data);
    }

    /**
     * 指定されたアドレスからデータを読み出せるかどうかを取得します。
     *
     * アドレス幅は符号無し 32 ビットとして解釈し、
     * 64ビットに変換された後にスレーブバスに渡されます。
     *
     * @param addr アドレス
     * @param len  読み取るデータのサイズ
     * @return 読み出しが可能ならば true、不可能ならば false
     */
    public boolean tryRead_a32(int addr, int len) {
        return getCore().tryRead_a32(addr, len);
    }

    /**
     * 指定したアドレスから 8 ビットを読み出します。
     *
     * アドレス幅は符号無し 32 ビットとして解釈し、
     * 64ビットに変換された後にスレーブバスに渡されます。
     *
     * @param addr アドレス
     * @return 指定したアドレスにあるデータ
     */
    public byte read8_a32(int addr) {
        return getCore().read8_a32(addr);
    }

    /**
     * 指定したアドレスから 16 ビットを読み出します。
     *
     * アドレス幅は符号無し 32 ビットとして解釈し、
     * 64ビットに変換された後にスレーブバスに渡されます。
     *
     * @param addr アドレス
     * @return 指定したアドレスにあるデータ
     */
    public short read16_a32(int addr) {
        return getCore().read16_a32(addr);
    }

    /**
     * 指定したアドレスから 32 ビットを読み出します。
     *
     * アドレス幅は符号無し 32 ビットとして解釈し、
     * 64ビットに変換された後にスレーブバスに渡されます。
     *
     * @param addr アドレス
     * @return 指定したアドレスにあるデータ
     */
    public int read32_a32(int addr) {
        return getCore().read32_a32(addr);
    }

    /**
     * 指定したアドレスから 64 ビットを読み出します。
     *
     * アドレス幅は符号無し 32 ビットとして解釈し、
     * 64ビットに変換された後にスレーブバスに渡されます。
     *
     * @param addr アドレス
     * @return 指定したアドレスにあるデータ
     */
    public long read64_a32(int addr) {
        return getCore().read64_a32(addr);
    }

    /**
     * 指定したアドレスにデータを書き込めるかどうかを取得します。
     *
     * アドレス幅は符号無し 32 ビットとして解釈し、
     * 64ビットに変換された後にスレーブバスに渡されます。
     *
     * @param addr アドレス
     * @param len  書き込むデータのサイズ
     * @return 書き込みが可能ならば true、不可能ならば false
     */
    public boolean tryWrite_a32(int addr, int len) {
        return getCore().tryWrite_a32(addr, len);
    }

    /**
     * 指定したアドレスに 8 ビットを書き込みます。
     *
     * アドレス幅は符号無し 32 ビットとして解釈し、
     * 64ビットに変換された後にスレーブバスに渡されます。
     *
     * @param addr アドレス
     * @param data 書き込むデータ
     */
    public void write8_a32(int addr, byte data) {
        getCore().write8_a32(addr, data);
    }

    /**
     * 指定したアドレスに 16 ビットを書き込みます。
     *
     * アドレス幅は符号無し 32 ビットとして解釈し、
     * 64ビットに変換された後にスレーブバスに渡されます。
     *
     * @param addr アドレス
     * @param data 書き込むデータ
     */
    public void write16_a32(int addr, short data) {
        getCore().write16_a32(addr, data);
    }

    /**
     * 指定したアドレスに 32 ビットを書き込みます。
     *
     * アドレス幅は符号無し 32 ビットとして解釈し、
     * 64ビットに変換された後にスレーブバスに渡されます。
     *
     * @param addr アドレス
     * @param data 書き込むデータ
     */
    public void write32_a32(int addr, int data) {
        getCore().write32_a32(addr, data);
    }

    /**
     * 指定したアドレスに 64 ビットを書き込みます。
     *
     * アドレス幅は符号無し 32 ビットとして解釈し、
     * 64ビットに変換された後にスレーブバスに渡されます。
     *
     * @param addr アドレス
     * @param data 書き込むデータ
     */
    public void write64_a32(int addr, long data) {
        getCore().write64_a32(addr, data);
    }

    /**
     * 逆アセンブルした命令を表示します。
     *
     * @param inst      ARM 命令
     * @param operation 命令の文字列表記
     * @param operand   オペランドの文字列表記
     */
    public void printDisasm(Inst32 inst, String operation, String operand) {
        getCore().printDisasm(inst, operation, operand);
    }

    /**
     * PC（プログラムカウンタ）の値を取得します。
     *
     * @return PC の値
     */
    public long getPC() {
        return getCore().getPC();
    }

    /**
     * PC（プログラムカウンタ）の値を設定します。
     *
     * @param val 新しい PC の値
     */
    public void setPC(long val) {
        getCore().setPC(val);
    }

    /**
     * PC を次の命令に移します。
     *
     * PC の増分は、現在実行している命令により異なります。
     *
     * @param inst 命令
     */
    public void nextPC(Inst32 inst) {
        getCore().nextPC(inst);
    }

    /**
     * 指定したアドレス分だけ相対ジャンプします。
     *
     * PC + 相対アドレス を、
     * 新たな PC として設定します。
     *
     * @param val 次に実行する命令の相対アドレス
     */
    public void jumpRel(int val) {
        getCore().jumpRel(val);
    }

    /**
     * レジスタ Rn の値を取得します。
     *
     * @param n レジスタ番号
     * @return レジスタの値
     */
    public long getReg(int n) {
        return getCore().getReg(n);
    }

    /**
     * レジスタ Rn の値を設定します。
     *
     * @param n   レジスタ番号
     * @param val 新しいレジスタの値
     */
    public void setReg(int n, long val) {
        getCore().setReg(n, val);
    }

    /**
     * レジスタ Rn そのものの値を取得します。
     *
     * @param n レジスタ番号
     * @return レジスタの値
     */
    public long getRegRaw(int n) {
        return getCore().getRegRaw(n);
    }

    /**
     * レジスタ Rn そのもの値を設定します。
     *
     * @param n   レジスタ番号
     * @param val 新しいレジスタの値
     */
    public void setRegRaw(int n, long val) {
        getCore().setRegRaw(n, val);
    }

    /**
     * レジスタ Rn の名前を取得します。
     *
     * @param n レジスタ番号
     * @return レジスタの名前
     */
    public String getRegName(int n) {
        return getCore().getRegName(n);
    }
}
