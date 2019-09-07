package net.katsuster.ememu.generic;

/**
 * CPU の基本クラス
 */
public abstract class CPU extends MasterCore64
        implements INTDestination {
    private int threadId;
    private boolean fEnabledDisasm;
    private boolean fPrintInstruction;
    private boolean fPrintRegs;
    private boolean raisedException;
    private boolean raisedInterrupt;
    private boolean jumped;

    public CPU() {
        threadId = -1;
        fEnabledDisasm = false;
        fPrintInstruction = false;
        fPrintRegs = false;
        raisedException = false;
        raisedInterrupt = false;
        jumped = false;
    }

    /**
     * ハードウェアスレッド ID を取得します。
     *
     * @return スレッド ID
     */
    public int getThreadID() {
        return threadId;
    }

    /**
     * ハードウェアスレッド ID を設定します。
     *
     * @param id スレッド ID
     */
    public void setThreadID(int id) {
        threadId = id;
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

    /**
     * 最後に行われた命令実行において、
     * CPU が例外を要求したかどうかを取得します。
     *
     * @return CPU が例外を要求した場合 true、要求していない場合 false
     */
    public boolean isRaisedException() {
        return raisedException;
    }

    /**
     * CPU が例外を要求したかどうかを設定します。
     *
     * @param m CPU が例外を要求した場合 true、要求していない場合 false
     */
    public void setRaisedException(boolean m) {
        raisedException = m;
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
     * 指定したアドレスから 16 ビットを読み出します。
     * アドレスは 16ビット境界でなくても構いません。
     *
     * アドレス幅は符号無し 32 ビットとして解釈し、
     * 64ビットに変換された後にスレーブバスに渡されます。
     *
     * @param addr アドレス
     * @return 指定したアドレスにあるデータ
     */
    public short read_ua16_a32(int addr) {
        long addrl = addr & 0xffffffffL;
        return read_ua16(addrl);
    }

    /**
     * 指定したアドレスから 32 ビットを読み出します。
     * アドレスは 32ビット境界でなくても構いません。
     *
     * アドレス幅は符号無し 32 ビットとして解釈し、
     * 64ビットに変換された後にスレーブバスに渡されます。
     *
     * @param addr アドレス
     * @return 指定したアドレスにあるデータ
     */
    public int read_ua32_a32(int addr) {
        long addrl = addr & 0xffffffffL;
        return read_ua32(addrl);
    }

    /**
     * 指定したアドレスから 64 ビットを読み出します。
     * アドレスは 64ビット境界でなくても構いません。
     *
     * アドレス幅は符号無し 32 ビットとして解釈し、
     * 64ビットに変換された後にスレーブバスに渡されます。
     *
     * @param addr アドレス
     * @return 指定したアドレスにあるデータ
     */
    public long read_ua64_a32(int addr) {
        long addrl = addr & 0xffffffffL;
        return read_ua64(addrl);
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
     * 指定したアドレスに 16 ビットを書き込みます。
     * アドレスは 16ビット境界でなくても構いません。
     *
     * アドレス幅は符号無し 32 ビットとして解釈し、
     * 64ビットに変換された後にスレーブバスに渡されます。
     *
     * @param addr アドレス
     * @param data 書き込むデータ
     */
    public void write_ua16_a32(int addr, short data) {
        long addrl = addr & 0xffffffffL;
        write_ua16(addrl, data);
    }

    /**
     * 指定したアドレスに 32 ビットを書き込みます。
     * アドレスは 32ビット境界でなくても構いません。
     *
     * アドレス幅は符号無し 32 ビットとして解釈し、
     * 64ビットに変換された後にスレーブバスに渡されます。
     *
     * @param addr アドレス
     * @param data 書き込むデータ
     */
    public void write_ua32_a32(int addr, int data) {
        long addrl = addr & 0xffffffffL;
        write_ua32(addrl, data);
    }

    /**
     * 指定したアドレスに 64 ビットを書き込みます。
     * アドレスは 64ビット境界でなくても構いません。
     *
     * アドレス幅は符号無し 32 ビットとして解釈し、
     * 64ビットに変換された後にスレーブバスに渡されます。
     *
     * @param addr アドレス
     * @param data 書き込むデータ
     */
    public void write_ua64_a32(int addr, long data) {
        long addrl = addr & 0xffffffffL;
        write_ua64(addrl, data);
    }

    /**
     * 逆アセンブルした命令を表示します。
     *
     * @param inst      ARM 命令
     * @param operation 命令の文字列表記
     * @param operand   オペランドの文字列表記
     */
    public void printDisasm(Inst32 inst, String operation, String operand) {
        synchronized (System.out) {
            printInstruction(inst, operation, operand);
            printRegs();
        }
    }

    /**
     * 命令を逆アセンブルした結果を表示します。
     *
     * @param inst      命令
     * @param operation 命令を表す文字列
     * @param operand   オペランドを表す文字列
     */
    public void printInstruction(Inst32 inst, String operation, String operand) {
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
    public abstract String instructionToString(Inst32 inst, String operation, String operand);

    /**
     * 現在のレジスタの文字列表記を取得します。
     *
     * @return レジスタ名を表す文字列
     */
    public abstract String regsToString();

    /**
     * レジスタ Rn の名前を取得します。
     *
     * @param n レジスタ番号
     * @return レジスタの名前
     */
    public abstract String getRegName(int n);

    /**
     * 割り込み線にコアを接続します。
     *
     * 割り込み線の番号の意味はアーキテクチャにより異なります。
     *
     * @param n 割り込み線の番号
     * @param c 割り込みを発生させるコア
     */
    public abstract void connectINTSource(int n, INTSource c);

    /**
     * 割り込み線からコアを切断します。
     *
     * 割り込み線の番号の意味はアーキテクチャにより異なります。
     *
     * @param n 割り込み線の番号
     */
    public abstract void disconnectINTSource(int n);

    /**
     * ジャンプが行われたかどうかを取得します。
     *
     * @return ジャンプが行われたならば true、そうでなければ false
     */
    public boolean isJumped() {
        return jumped;
    }

    /**
     * ジャンプが行われたかどうかを設定します。
     *
     * @param b ジャンプが行われたならば true、そうでなければ false
     */
    public void setJumped(boolean b) {
        jumped = b;
    }

    /**
     * PC（プログラムカウンタ）を次の命令に移します。
     *
     * PC の増分は、現在実行している命令により異なります。
     *
     * @param inst 命令
     */
    public abstract void nextPC(Inst32 inst);

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
