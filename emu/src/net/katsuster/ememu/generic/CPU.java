package net.katsuster.ememu.generic;

/**
 * CPU の基本クラス
 *
 * @author katsuhiro
 */
public abstract class CPU extends MasterCore
        implements INTDestination {
    private boolean fEnabledDisasm;
    private boolean fPrintInstruction;
    private boolean fPrintRegs;
    private boolean raisedInterrupt;

    public CPU() {
        fEnabledDisasm = false;
        fPrintInstruction = false;
        fPrintRegs = false;
        raisedInterrupt = false;
    }

    public boolean isEnabledDisasm() {
        return fEnabledDisasm;
    }

    public void setEnabledDisasm(boolean b) {
        fEnabledDisasm = b;
    }

    public boolean isPrintInstruction() {
        return fPrintInstruction;
    }

    public void setPrintInstruction(boolean b) {
        fPrintInstruction = b;
    }

    public boolean isPrintRegs() {
        return fPrintRegs;
    }

    public void setPrintRegs(boolean b) {
        fPrintRegs = b;
    }

    @Override
    public boolean isRaisedInterrupt() {
        return raisedInterrupt;
    }

    @Override
    public void setRaisedInterrupt(boolean m) {
        synchronized(this) {
            raisedInterrupt = m;
            if (m) {
                notifyAll();
            }
        }
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
        long addrl = addr & 0xffffffffL;
        return tryRead(addrl, len);
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
        long addrl = addr & 0xffffffffL;
        return read8(addrl);
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
        long addrl = addr & 0xffffffffL;
        return read16(addrl);
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
        long addrl = addr & 0xffffffffL;
        return read32(addrl);
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
        long addrl = addr & 0xffffffffL;
        return read64(addrl);
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
        long addrl = addr & 0xffffffffL;
        return tryWrite(addrl, len);
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
        long addrl = addr & 0xffffffffL;
        write8(addrl, data);
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
        long addrl = addr & 0xffffffffL;
        write16(addrl, data);
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
        long addrl = addr & 0xffffffffL;
        write32(addrl, data);
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
        long addrl = addr & 0xffffffffL;
        write64(addrl, data);
    }

    /**
     * 逆アセンブルした命令を表示します。
     *
     * @param inst      ARM 命令
     * @param operation 命令の文字列表記
     * @param operand   オペランドの文字列表記
     */
    public void printDisasm(Instruction inst, String operation, String operand) {
        printInstruction(inst, operation, operand);
        printRegs();
    }

    /**
     * 命令を逆アセンブルした結果を表示します。
     *
     * @param inst      命令
     * @param operation 命令を表す文字列
     * @param operand   オペランドを表す文字列
     */
    public void printInstruction(Instruction inst, String operation, String operand) {
        if (!isPrintInstruction()) {
            return;
        }

        System.out.print(instructionToString(inst, operation, operand));
    }

    /**
     * 現在のレジスタを表示します。
     */
    public void printRegs() {
        if (!isPrintRegs()) {
            return;
        }

        System.out.print(regsToString());
    }

    /**
     * 命令を逆アセンブルした結果の文字列表記を取得します。
     *
     * @param inst      命令
     * @param operation 命令を表す文字列
     * @param operand   オペランドを表す文字列
     * @return 命令を逆アセンブルした文字列
     */
    public abstract String instructionToString(Instruction inst, String operation, String operand);

    /**
     * 現在のレジスタの文字列表記を取得します。
     *
     * @return レジスタ名を表す文字列
     */
    public abstract String regsToString();

    /**
     * PC（プログラムカウンタ）の値を取得します。
     *
     * @return PC の値
     */
    public abstract int getPC();

    /**
     * PC（プログラムカウンタ）の値を設定します。
     *
     * @param val 新しい PC の値
     */
    public abstract void setPC(int val);

    /**
     * PC（プログラムカウンタ）を次の命令に移します。
     *
     * PC の増分は、現在実行している命令により異なります。
     *
     * @param inst 命令
     */
    public abstract void nextPC(Instruction inst);

    /**
     * 指定したアドレス分だけ相対ジャンプします。
     *
     * PC + 相対アドレス を、
     * 新たな PC（プログラムカウンタ）として設定します。
     *
     * @param val 次に実行する命令の相対アドレス
     */
    public abstract void jumpRel(int val);

    /**
     * 命令から見えるレジスタ Rn の値を取得します。
     *
     * アーキテクチャによっては、
     * レジスタそのものの値と、命令から見えるレジスタの値は異なる場合があります。
     *
     * @param n レジスタ番号
     * @return レジスタの値
     */
    public abstract int getReg(int n);

    /**
     * 命令から見えるレジスタ Rn の値を設定します。
     *
     * アーキテクチャによっては、
     * レジスタそのものの値と、命令から見えるレジスタの値は異なる場合があります。
     *
     * @param n   レジスタ番号
     * @param val 新しいレジスタの値
     */
    public abstract void setReg(int n, int val);

    /**
     * レジスタ Rn そのものの値を取得します。
     *
     * アーキテクチャによっては、
     * レジスタそのものの値と、命令から見えるレジスタの値は異なる場合があります。
     *
     * @param n レジスタ番号
     * @return レジスタの値
     */
    public abstract int getRegRaw(int n);

    /**
     * レジスタ Rn そのもの値を設定します。
     *
     * アーキテクチャによっては、
     * レジスタそのものの値と、命令から見えるレジスタの値は異なる場合があります。
     *
     * @param n   レジスタ番号
     * @param val 新しいレジスタの値
     */
    public abstract void setRegRaw(int n, int val);

    /**
     * レジスタ Rn の名前を取得します。
     *
     * @param n レジスタ番号
     * @return レジスタの名前
     */
    public abstract String getRegName(int n);

    /**
     * 現在位置から 1命令だけ実行します。
     */
    public abstract void step();

    @Override
    public void run() {
        try {
            while (!shouldHalt()) {
                step();
            }
        } catch (IllegalArgumentException e) {
            setPrintRegs(true);
            printRegs();
            throw e;
        }
    }
}
