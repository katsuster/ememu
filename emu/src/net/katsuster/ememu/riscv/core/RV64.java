package net.katsuster.ememu.riscv.core;

import net.katsuster.ememu.generic.*;

/**
 * RISC-V 64bit
 *
 * RISC-V User-Level ISA V2.2
 */
public class RV64 extends CPU64 {
    private RV64RegFile regfile;
    private RV64CSRFile csrfile;

    private InstructionRV16 instRV16;
    private InstructionRV32 instRV32;
    private Opcode decinstAll;
    private DecodeStageRVI rviDec;
    private ExecStageRVI rviExe;

    public RV64() {
        regfile = new RV64RegFile();
        csrfile = new RV64CSRFile();

        instRV16 = new InstructionRV16(0);
        instRV32 = new InstructionRV32(0);
        decinstAll = new Opcode(instRV32, OpType.INS_TYPE_UNKNOWN, OpIndex.INS_UNKNOWN);
        rviDec = new DecodeStageRVI(this);
        rviExe = new ExecStageRVI(this);
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
        if (isJumped()) {
            setJumped(false);
            return;
        }
        setPCRaw(getPCRaw() + inst.getLength());
    }

    @Override
    public long getPC() {
        return getReg(32);
    }

    @Override
    public void setPC(long val) {
        setJumped(true);
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
     * CSR の値を取得します。
     *
     * @param n レジスタ番号
     * @return レジスタの値
     */
    public long getCSR(int n) {
        return csrfile.getReg(n).getValue();
    }

    /**
     * CSR の値を設定します。
     *
     * @param n   レジスタ番号
     * @param val 新しいレジスタの値
     */
    public void setCSR(int n, long val) {
        csrfile.getReg(n).setValue(val);
    }

    /**
     * CSR の名前を取得します。
     *
     * @param n レジスタ番号
     * @return レジスタの名前
     */
    public String getCSRName(int n) {
        return csrfile.getReg(n).getName();
    }

    /**
     * RISC-V アーキテクチャのビット数を返します。
     *
     * @return RV32 なら 32、RV64 なら 64
     */
    public int getRVBits() {
        return 64;
    }

    /**
     * 命令を取得します。
     *
     * @return 命令
     */
    public Inst32 fetch() {
        long vaddr, paddr;
        short v16;
        int v32;

        //現在の PC の指すアドレスから命令を取得します
        vaddr = getRegRaw(32);

        paddr = vaddr;

        if (!tryRead(paddr, 2)) {
            //raiseException(EXCEPT_ABT_INST,
            //        String.format("exec [%08x]", paddr));
            return null;
        }
        v16 = read16(paddr);

        int aa = BitOp.getField32(v16, 0, 2);
        if (aa != 3) {
            //16bit
            instRV16.reuse(v16, 2);
            return instRV16;
        }

        int bbb = BitOp.getField32(v16, 2, 3);
        if (bbb != 7) {
            //32bit
            v32 = read32(paddr);
            instRV32.reuse(v32, 4);
            return instRV32;
        }

        //TODO: Over 32bit is not support
        throw new IllegalArgumentException("Not support over 32bit length instructions");
    }

    /**
     * 命令をデコードします。
     *
     * @param instgen 命令
     * @return デコードされた命令
     */
    public Opcode decode(Inst32 instgen) {
        InstructionRV32 inst = (InstructionRV32) instgen;
        OpType optype;
        OpIndex opind;

        //RVI 命令
        optype = OpType.INS_TYPE_RVI;
        opind = rviDec.decode(inst);

        decinstAll.reuse(instgen, optype, opind);

        return decinstAll;
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
        switch (decinst.getType()) {
        case INS_TYPE_RVI:
            rviExe.execute(decinst, exec);
            break;
        default:
            throw new IllegalArgumentException("Unknown instruction type " +
                    decinst.getType());
        }

    }

    @Override
    public void step() {
        Inst32 inst;
        Opcode decinst;

        ////////////////////
        setPrintInstruction(true);
        setEnabledDisasm(true);
        setPrintRegs(true);

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
