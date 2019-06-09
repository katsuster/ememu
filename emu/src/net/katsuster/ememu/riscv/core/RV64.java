package net.katsuster.ememu.riscv.core;

import net.katsuster.ememu.generic.*;

public class RV64 extends CPU {
    private RV64RegFile regfile;

    public RV64() {
        regfile = new RV64RegFile();
    }

    @Override
    public void init() {
        //doExceptionReset("Init.");
    }

    @Override
    public String instructionToString(Instruction inst, String operation, String operand) {
        return String.format("%08x:    %-12s    %-7s %s\n",
                getRegRaw(15), inst.toHex(), operation, operand);
    }

    @Override
    public String regsToString() {
        return null;
    }

    @Override
    public int getPC() {
        return 0;
    }

    @Override
    public void setPC(int val) {

    }

    @Override
    public void nextPC(Instruction inst) {

    }

    @Override
    public void jumpRel(int val) {

    }

    @Override
    public int getReg(int n) {
        return 0;
    }

    @Override
    public void setReg(int n, int val) {

    }

    @Override
    public int getRegRaw(int n) {
        return 0;
    }

    @Override
    public void setRegRaw(int n, int val) {

    }

    @Override
    public String getRegName(int n) {
        return null;
    }

    @Override
    public void connectINTSource(int n, INTSource c) {

    }

    @Override
    public void disconnectINTSource(int n) {

    }
    /**
     * 命令を取得します。
     *
     * @return 命令
     */
    public Instruction fetch() {
        return null;
    }

    /**
     * 命令をデコードします。
     *
     * @param instgen 命令
     * @return デコードされた命令
     */
    public Opcode decode(Instruction instgen) {
        return null;
    }

    /**
     * 命令を逆アセンブルします。
     *
     * @param decinst デコードされた命令
     */
    public void disasm(Opcode decinst) {
        executeInst(decinst, false);
    }

    /**
     * 命令を実行します。
     *
     * @param decinst デコードされた命令
     */
    public void execute(Opcode decinst) {
        executeInst(decinst, true);
    }

    /**
     * 命令を逆アセンブル、実行します。
     *
     * @param decinst デコードされた命令
     * @param exec デコード、逆アセンブルと実行なら true、
     *             デコード、逆アセンブルのみなら false
     */
    public void executeInst(Opcode decinst, boolean exec) {

    }

    @Override
    public void step() {
        Instruction inst;
        Opcode decinst;

        //要求された例外のうち、優先度の高い例外を 1つだけ処理します
        //doImportantException();

        if (isRaisedInterrupt()) {
            //高速割り込み線がアサートされていれば、FIQ 例外を要求します
            //acceptFIQ();
            //if (isRaisedException()) {
            //    setRaisedException(false);
            //    return;
            //}

            //割り込み線がアサートされていれば、IRQ 例外を要求します
            //acceptIRQ();
            //if (isRaisedException()) {
            //    setRaisedException(false);
            //    return;
            //}

            //if (!intc.getINTSource(INTSRC_IRQ).isAssert() &&
            //        !intc.getINTSource(INTSRC_FIQ).isAssert()) {
            //    setRaisedInterrupt(false);
            //}
        }

        //命令を取得します
        inst = fetch();
        //if (isRaisedException()) {
        //    setRaisedException(false);
        //    return;
        //}

        //デコードします
        decinst = decode(inst);

        //逆アセンブルします
        if (isEnabledDisasm()) {
            disasm(decinst);
        }

        //FIXME: for debug
        //if (getCPSR().getTBit()) {
        //    setPrintInstruction(true);
        //    disasm(decinst);
        //}

        //実行して、次の命令へ
        execute(decinst);
        //if (isRaisedException()) {
        //    setRaisedException(false);
        //    return;
        //}
        nextPC(inst);
    }
}
