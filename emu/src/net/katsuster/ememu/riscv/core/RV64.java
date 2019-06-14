package net.katsuster.ememu.riscv.core;

import net.katsuster.ememu.generic.*;

public class RV64 extends CPU64 {
    private RV64RegFile regfile;

    public RV64() {
        regfile = new RV64RegFile();
    }

    @Override
    public void init() {
        //doExceptionReset("Init.");
        setPC(0x1004);
    }

    @Override
    public String instructionToString(Inst32 inst, String operation, String operand) {
        return String.format("%08x:    %-12s    %-7s %s\n",
                getPCRaw(), inst.toHex(), operation, operand);
    }

    @Override
    public String regsToString() {
        return regfile.toString();
    }

    @Override
    public String getRegName(int n) {
        return regfile.getReg(n).getName();
    }

    @Override
    public void connectINTSource(int n, INTSource c) {

    }

    @Override
    public void disconnectINTSource(int n) {

    }

    @Override
    public void nextPC(Inst32 inst) {
        setPCRaw(getPCRaw() + inst.getLength());
    }

    @Override
    public long getPC() {
        return getReg(32);
    }

    @Override
    public void setPC(long val) {
        setReg(32, val);
    }

    @Override
    public long getPCRaw() {
        return getRegRaw(32);
    }

    @Override
    public void setPCRaw(long val) {
        setRegRaw(32, val);
    }

    @Override
    public void jumpRel(long val) {
        setPC(getPC() + val);
    }

    @Override
    public long getReg(int n) {
        return getRegRaw(n);
    }

    @Override
    public void setReg(int n, long val) {
        setRegRaw(n, val);
    }

    @Override
    public long getRegRaw(int n) {
        return regfile.getReg(n).getValue();
    }

    @Override
    public void setRegRaw(int n, long val) {
        regfile.getReg(n).setValue(val);
    }

    /**
     * 命令を取得します。
     *
     * @return 命令
     */
    public Inst32 fetch() {
        long vaddr, paddr;
        short v16;

        //現在の PC の指すアドレスから命令を取得します
        vaddr = getRegRaw(32);

        paddr = vaddr;

        if (!tryRead(paddr, 2)) {
            //raiseException(EXCEPT_ABT_INST,
            //        String.format("exec [%08x]", paddr));
            return null;
        }
        v16 = read16(paddr);

        //int ubw = inst.getField(0, 2);


        return null;
    }

    /**
     * 命令をデコードします。
     *
     * @param instgen 命令
     * @return デコードされた命令
     */
    public Opcode decode(Inst32 instgen) {
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
        Inst32 inst;
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
