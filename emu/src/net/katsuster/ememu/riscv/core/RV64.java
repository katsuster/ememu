package net.katsuster.ememu.riscv.core;

import net.katsuster.ememu.generic.*;

import static net.katsuster.ememu.riscv.core.RV64CSRFile.*;

/**
 * RISC-V 64bit
 *
 * RISC-V User-Level ISA V2.2
 * RISC-V Privileged ISA V1.10
 */
public class RV64 extends CPU64 {
    //特権レベル
    public static final int PRIV_U = 0;
    public static final int PRIV_S = 1;
    public static final int PRIV_RESERVED = 2;
    public static final int PRIV_M = 3;
    public static final int PRIV_MASK = 3;

    //割り込み
    public static final int INTR_SOFT_U = 0;
    public static final int INTR_SOFT_S = 1;
    public static final int INTR_RESERVED1 = 2;
    public static final int INTR_SOFT_M = 3;

    public static final int INTR_TIMER_U = 4;
    public static final int INTR_TIMER_S = 5;
    public static final int INTR_RESERVED2 = 6;
    public static final int INTR_TIMER_M = 7;

    public static final int INTR_EXTERNAL_U = 8;
    public static final int INTR_EXTERNAL_S = 9;
    public static final int INTR_RESERVED3 = 10;
    public static final int INTR_EXTERNAL_M = 11;

    public static final int INTR_MAX = 16;

    //例外
    public static final int EXCEPT_BASE = INTR_MAX;

    public static final int EXCEPT_INS_ALIGN = EXCEPT_BASE + 0;
    public static final int EXCEPT_INS_FAULT = EXCEPT_BASE + 1;
    public static final int EXCEPT_ILL = EXCEPT_BASE + 2;
    public static final int EXCEPT_BRK = EXCEPT_BASE + 3;
    public static final int EXCEPT_LDR_ALIGN = EXCEPT_BASE + 4;
    public static final int EXCEPT_LDR_FAULT = EXCEPT_BASE + 5;
    public static final int EXCEPT_STR_ALIGN = EXCEPT_BASE + 6;
    public static final int EXCEPT_STR_FAULT = EXCEPT_BASE + 7;
    public static final int EXCEPT_ENV_UMODE = EXCEPT_BASE + 8;
    public static final int EXCEPT_ENV_SMODE = EXCEPT_BASE + 9;
    public static final int EXCEPT_RESERVED1 = EXCEPT_BASE + 10;
    public static final int EXCEPT_ENV_MMODE = EXCEPT_BASE + 11;
    public static final int EXCEPT_INS_PAGEF = EXCEPT_BASE + 12;
    public static final int EXCEPT_LDR_PAGEF = EXCEPT_BASE + 13;
    public static final int EXCEPT_RESERVED2 = EXCEPT_BASE + 14;
    public static final int EXCEPT_STR_PAGEF = EXCEPT_BASE + 15;

    //割り込み線を持つ
    public static final int MAX_INTSRCS = 1;
    public static final int INTSRC_IRQ = 0;

    private RV64RegFile regfile;
    private RV64CSRFile csrfile;
    private NormalINTC intc;
    private int privMode;

    private boolean[] exceptions;
    private String[] exceptionReasons;

    private InstructionRV16 instRV16;
    private InstructionRV32 instRV32;
    private Opcode decinstAll;
    private DecodeStageRVI rviDec;
    private DecodeStageRVC rvcDec;
    private ExecStageRVI rviExe;
    private ExecStageRVC rvcExe;

    public RV64() {
        regfile = new RV64RegFile();
        csrfile = new RV64CSRFile();
        intc = new NormalINTC(MAX_INTSRCS);
        intc.connectINTDestination(this);
        privMode = PRIV_M;

        exceptions = new boolean[16];
        exceptionReasons = new String[16];

        instRV16 = new InstructionRV16(0);
        instRV32 = new InstructionRV32(0);
        decinstAll = new Opcode(instRV32, OpType.INS_TYPE_UNKNOWN, OpIndex.INS_UNKNOWN);
        rviDec = new DecodeStageRVI(this);
        rvcDec = new DecodeStageRVC(this);
        rviExe = new ExecStageRVI(this);
        rvcExe = new ExecStageRVC(this);
    }

    @Override
    public void init() {
        //doExceptionReset("Init.");
        setPrivMode(PRIV_M);
        setPC(0x1004);
        setJumped(false);
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
        return getReg(RV64RegFile.REG_PC);
    }

    @Override
    public void setPC(long val) {
        setJumped(true);
        setReg(RV64RegFile.REG_PC, val);
    }

    @Override
    public long getPCRaw() {
        return getRegRaw(RV64RegFile.REG_PC);
    }

    @Override
    public void setPCRaw(long val) {
        setRegRaw(RV64RegFile.REG_PC, val);
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
     * 特権モードを取得します。
     *
     * @return 特権モード
     */
    int getPrivMode() {
        return privMode;
    }

    /**
     * 特権モードを設定します。
     *
     * @param priv 特権モード
     */
    void setPrivMode(int priv) {
        privMode = priv;
    }

    private int[] xstatusRegs = {
            CSR_USTATUS,
            CSR_SSTATUS,
            -1,
            CSR_MSTATUS,
    };

    /**
     * mstatus/sstatus/ustatus xIE ビットを取得します。
     *
     * @param priv 特権レベル
     * @return xIE ビット
     */
    public boolean getXIE(int priv) {
        int r = xstatusRegs[priv];
        return BitOp.getBit64(getCSR(r), XSTATUS_XIE + priv);
    }

    /**
     * mstatus/sstatus/ustatus xIE ビットを取得します。
     *
     * @param priv 特権レベル
     * @param val xIE ビットの値
     */
    public void setXIE(int priv, boolean val) {
        int r = xstatusRegs[priv];
        long v = BitOp.setBit64(getCSR(r), XSTATUS_XIE + priv, val);
        setCSR(r, v);
    }

    private int[] xtvecRegs = {
            CSR_UTVEC,
            CSR_STVEC,
            -1,
            CSR_MTVEC,
    };

    /**
     * mtvec/stvec/utvec MODE を取得します。
     *
     * @param priv 特権レベル
     * @return ベクトルモード
     */
    public int getXTVEC_MODE(int priv) {
        int r = xtvecRegs[priv];
        return (int)(getCSR(r) & XTVEC_MODE_MASK);
    }

    private int[] xieRegs = {
            CSR_UIE,
            CSR_SIE,
            -1,
            CSR_MIE,
    };

    /**
     * mie/sie/uie xSIE ビットを取得します。
     *
     * @param priv 特権モード
     * @return xSIE ビット
     */
    public boolean getXIE_XSIE(int priv) {
        int r = xieRegs[priv];
        return BitOp.getBit64(getCSR(r), XIE_XSIE + priv);
    }

    /**
     * mie/sie/uie xSIE ビットを取得します。
     *
     * @param priv 特権モード
     * @param val xSIE ビット
     */
    public void setXIE_XSIE(int priv, boolean val) {
        int r = xieRegs[priv];
        long v = BitOp.setBit64(getCSR(r), XIE_XSIE + priv, val);
        setCSR(r, v);
    }

    /**
     * mie/sie/uie xTIE ビットを取得します。
     *
     * @param priv 特権モード
     * @return xTIE ビット
     */
    public boolean getXIE_XTIE(int priv) {
        int r = xieRegs[priv];
        return BitOp.getBit64(getCSR(r), XIE_XTIE + priv);
    }

    /**
     * mie/sie/uie xTIE ビットを取得します。
     *
     * @param priv 特権モード
     * @param val xTIE ビット
     */
    public void setXIE_XTIE(int priv, boolean val) {
        int r = xieRegs[priv];
        long v = BitOp.setBit64(getCSR(r), XIE_XTIE + priv, val);
        setCSR(r, v);
    }

    /**
     * mie/sie/uie xEIE ビットを取得します。
     *
     * @param priv 特権モード
     * @return xEIE ビット
     */
    public boolean getXIE_XEIE(int priv) {
        int r = xieRegs[priv];
        return BitOp.getBit64(getCSR(r), XIE_XEIE + priv);
    }

    /**
     * mie/sie/uie xEIE ビットを取得します。
     *
     * @param priv 特権モード
     * @param val xEIE ビット
     */
    public void setXIE_XEIE(int priv, boolean val) {
        int r = xieRegs[priv];
        long v = BitOp.setBit64(getCSR(r), XIE_XEIE + priv, val);
        setCSR(r, v);
    }

    private int[] xipRegs = {
            CSR_UIP,
            CSR_SIP,
            -1,
            CSR_MIP,
    };

    /**
     * mip/sip/uip xSIP ビットを取得します。
     *
     * @param priv 特権モード
     * @return xSIP ビット
     */
    public boolean getXIP_XSIP(int priv) {
        int r = xipRegs[priv];
        return BitOp.getBit64(getCSR(r), XIP_XSIP + priv);
    }

    /**
     * mip/sip/uip xSIP ビットを取得します。
     *
     * @param priv 特権モード
     * @param val xSIP ビット
     */
    public void setXIP_XSIP(int priv, boolean val) {
        int r = xipRegs[priv];
        long v = BitOp.setBit64(getCSR(r), XIP_XSIP + priv, val);
        setCSR(r, v);
    }

    /**
     * mip/sip/uip xTIP ビットを取得します。
     *
     * @param priv 特権モード
     * @return xTIP ビット
     */
    public boolean getXIP_XTIP(int priv) {
        int r = xipRegs[priv];
        return BitOp.getBit64(getCSR(r), XIP_XTIP + priv);
    }

    /**
     * mip/sip/uip xTIP ビットを取得します。
     *
     * @param priv 特権モード
     * @param val xTIP ビット
     */
    public void setXIP_XTIP(int priv, boolean val) {
        int r = xipRegs[priv];
        long v = BitOp.setBit64(getCSR(r), XIP_XTIP + priv, val);
        setCSR(r, v);
    }

    /**
     * mip/sip/uip xEIP ビットを取得します。
     *
     * @param priv 特権モード
     * @return xEIP ビット
     */
    public boolean getXIP_XEIP(int priv) {
        int r = xipRegs[priv];
        return BitOp.getBit64(getCSR(r), XIP_XEIP + priv);
    }

    /**
     * mip/sip/uip xEIP ビットを取得します。
     *
     * @param priv 特権モード
     * @param val xEIP ビット
     */
    public void setXIP_XEIP(int priv, boolean val) {
        int r = xipRegs[priv];
        long v = BitOp.setBit64(getCSR(r), XIP_XEIP + priv, val);
        setCSR(r, v);
    }

    public boolean isRaisedInternalInterrupt() {
        int ie = xieRegs[getPrivMode()];
        int ip = xipRegs[getPrivMode()];

        return (getCSR(ie) & getCSR(ip)) != 0;
    }

    private int[] xcauseRegs = {
            CSR_UCAUSE,
            CSR_SCAUSE,
            -1,
            CSR_MCAUSE,
    };

    /**
     * mcause/scause/ucause Exception Code ビットを取得します。
     *
     * @param priv 特権モード
     * @return 例外コード
     */
    public long getXCAUSE_CODE(int priv) {
        int r = xcauseRegs[priv];
        int l = getRVBits() - 1;
        return BitOp.getField64(getCSR(r), XCAUSE_CODE, l);
    }

    /**
     * mcause/scause/ucause Exception Code ビットを取得します。
     *
     * @param priv 特権モード
     * @param val 例外コード
     */
    public void setXCAUSE_CODE(int priv, long val) {
        int r = xcauseRegs[priv];
        int l = getRVBits() - 1;
        long v = BitOp.setField64(getCSR(r), XCAUSE_CODE, l, val);
        setCSR(r, v);
    }

    /**
     * mcause/scause/ucause Interrupt ビットを取得します。
     *
     * @param priv 特権モード
     * @return Interrupt ビット
     */
    public boolean getXCAUSE_INTR(int priv) {
        int r = xcauseRegs[priv];
        return BitOp.getBit64(getCSR(r), XCAUSE_INTERRUPT);
    }

    /**
     * mcause/scause/ucause Interrupt ビットを取得します。
     *
     * @param priv 特権モード
     * @param val Interrupt ビット
     */
    public void setXCAUSE_INTR(int priv, boolean val) {
        int r = xcauseRegs[priv];
        long v = BitOp.setBit64(getCSR(r), XCAUSE_INTERRUPT, val);
        setCSR(r, v);
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
        vaddr = getPCRaw();

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
            v32 = read_ua32(paddr);
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
        OpType optype;
        OpIndex opind;

        if (instgen.getLength() == 4) {
            //RVI 命令
            InstructionRV32 inst = (InstructionRV32) instgen;

            opind = rviDec.decode(inst);
            //TODO: opind から決める
            optype = OpType.INS_TYPE_RVI;
        } else if (instgen.getLength() == 2) {
            //RVC 命令
            InstructionRV16 inst = (InstructionRV16) instgen;

            opind = rvcDec.decode(inst);
            //TODO: opind から決める
            optype = OpType.INS_TYPE_RVC;
        } else {
            throw new IllegalArgumentException(String.format(
                    "Unknown instruction length %d.", instgen.getLength()));
        }

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
        case INS_TYPE_RVC:
            rvcExe.execute(decinst, exec);
            break;
        default:
            throw new IllegalArgumentException("Unknown instruction type " +
                    decinst.getType());
        }
    }

    /**
     * 例外を要求します。
     *
     * @param num    例外番号（INTR_xxxx, EXCEPT_xxxx）
     * @param dbgmsg デバッグ用のメッセージ
     */
    public void raiseException(int num, String dbgmsg) {
        if (num < 0 || exceptions.length <= num) {
            throw new IllegalArgumentException("Illegal exception number " + num);
        }

        if (isRaisedException()) {
            //例外状態がクリアされず残っている
            //一度の命令で二度、例外が起きるのはおそらくバグでしょう
            throw new IllegalStateException("Exception status is not cleared.");
        }

        exceptions[num] = true;
        exceptionReasons[num] = dbgmsg;

        setRaisedException(true);
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
        if (isRaisedException()) {
            setRaisedException(false);
            return;
        }

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
        if (isRaisedException()) {
            setRaisedException(false);
            return;
        }
        nextPC(inst);
    }
}
