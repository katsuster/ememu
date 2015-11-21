package net.katsuster.ememu.generic;

/**
 * CPU のパイプラインのステージ。
 *
 * ステージは CPU が命令を解釈、実行するため、各々が独立して実行できる工程を指します。
 * 例えばフェッチ、デコード、実行ステージに分けることができます。
 *
 * @author katsuhiro
 */
public class Stage {
    private CPU core;

    /**
     * CPU コアのステージを生成します。
     *
     * @param c ステージを使う CPU コア
     */
    public Stage(CPU c) {
        core = c;
    }

    /**
     * 実行ステージを使う CPU コアを取得します。
     *
     * @return 実行ステージを使う CPU コア
     */
    public CPU getCore() {
        return core;
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
    public void printDisasm(Instruction inst, String operation, String operand) {
        getCore().printDisasm(inst, operation, operand);
    }

    /**
     * PC（プログラムカウンタ）の値を取得します。
     *
     * @return PC の値
     */
    public int getPC() {
        return getCore().getPC();
    }

    /**
     * PC（プログラムカウンタ）の値を設定します。
     *
     * @param val 新しい PC の値
     */
    public void setPC(int val) {
        getCore().setPC(val);
    }

    /**
     * PC を次の命令に移します。
     *
     * PC の増分は、現在実行している命令により異なります。
     *
     * @param inst 命令
     */
    public void nextPC(Instruction inst) {
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
    public int getReg(int n) {
        return getCore().getReg(n);
    }

    /**
     * レジスタ Rn の値を設定します。
     *
     * @param n   レジスタ番号
     * @param val 新しいレジスタの値
     */
    public void setReg(int n, int val) {
        getCore().setReg(n, val);
    }

    /**
     * レジスタ Rn そのものの値を取得します。
     *
     * @param n レジスタ番号
     * @return レジスタの値
     */
    public int getRegRaw(int n) {
        return getCore().getRegRaw(n);
    }

    /**
     * レジスタ Rn そのもの値を設定します。
     *
     * @param n   レジスタ番号
     * @param val 新しいレジスタの値
     */
    public void setRegRaw(int n, int val) {
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
