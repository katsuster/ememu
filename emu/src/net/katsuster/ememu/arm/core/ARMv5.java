package net.katsuster.ememu.arm.core;

import net.katsuster.ememu.generic.*;

/**
 * ARMv5TE CPU
 *
 * <p>
 * 参考: ARM アーキテクチャリファレンスマニュアル Second Edition
 * ARM DDI0100DJ
 * </p>
 * <p>
 * 最新版は、日本語版 ARM DDI0100HJ, 英語版 ARM DDI0100I
 * </p>
 * <p>
 * T は Thumb 命令、
 * E はエンハンスド DSP 命令、
 * のことらしい。
 * </p>
 *
 * @author katsuhiro
 */
public class ARMv5 extends CPU {
    //IRQ, FIQ の 2つの割り込み線を持つ
    public static final int MAX_INTSRCS = 2;
    public static final int INTSRC_IRQ = 0;
    public static final int INTSRC_FIQ = 1;

    private ARMRegFile regfile;
    private CoProc[] coProcs;
    private MMUv5 mmu;
    private NormalINTC intc;

    private boolean exceptions[];
    private String exceptionReasons[];

    private boolean raisedException;
    private boolean jumped;
    private boolean highVector;

    private InstructionARM instA32;
    private InstructionThumb instT32;
    private Opcode decinstAll;
    private DecodeStageARMv5 armDecode;
    private DecodeStageThumb thumbDecode;
    private DecodeStageThumb2 thumb2Decode;
    private ExecStageARMv5 armExec;
    private ExecStageThumb thumbExec;
    private ExecStageThumb2 thumb2Exec;

    public ARMv5() {
        CoProcVFPv2 cpVfp;
        CoProcDebugv1 cpDbg;
        CoProcStdv5 cpStd;

        cpVfp = new CoProcVFPv2(10, this);
        cpDbg = new CoProcDebugv1(14, this);
        cpStd = new CoProcStdv5(15, this);

        regfile = new ARMRegFile();
        coProcs = new CoProc[16];
        coProcs[10] = cpVfp;
        coProcs[14] = cpDbg;
        coProcs[15] = cpStd;
        mmu = new MMUv5(this, cpStd);
        intc = new NormalINTC(MAX_INTSRCS);
        intc.connectINTDestination(this);

        exceptions = new boolean[7];
        exceptionReasons = new String[7];

        raisedException = false;
        jumped = false;
        highVector = false;

        instA32 = new InstructionARM(0);
        instT32 = new InstructionThumb(0);
        decinstAll = new Opcode(instA32, OpType.INS_TYPE_UNKNOWN, OpIndex.INS_UNKNOWN);
        armDecode = new DecodeStageARMv5(this);
        thumbDecode = new DecodeStageThumb(this);
        thumb2Decode = new DecodeStageThumb2(this);
        armExec = new ExecStageARMv5(this);
        thumbExec = new ExecStageThumb(this);
        thumb2Exec = new ExecStageThumb2(this);
    }

    @Override
    public String instructionToString(Instruction inst, String operation, String operand) {
        return String.format("%08x:    %-12s    %-7s %s\n",
                getRegRaw(15), inst.toHex(), operation, operand);
    }

    @Override
    public String regsToString() {
        return regfile.toString();
    }

    /**
     * PC（プログラムカウンタ）の値を取得します。
     *
     * 下記の呼び出しと同一です。
     * getReg(15)
     *
     * @return PC の値
     */
    @Override
    public int getPC() {
        return getReg(15);
    }

    /**
     * PC（プログラムカウンタ）の値を設定します。
     *
     * 下記の呼び出しと同一です。
     * setReg(15, val)
     *
     * @param val 新しい PC の値
     */
    @Override
    public void setPC(int val) {
        setReg(15, val);
    }

    /**
     * PC を次の命令に移します。
     *
     * ただし、ブランチ命令の後は PC を変更しません。
     */
    @Override
    public void nextPC(Instruction inst) {
        if (isJumped()) {
            setJumped(false);
            return;
        }
        setRegRaw(15, getRegRaw(15) + inst.getLength());
    }

    /**
     * 指定したアドレス分だけ相対ジャンプします。
     *
     * PC（実行中の命令のアドレス +8）+ 相対アドレス を、
     * 新たな PC として設定します。
     *
     * また命令実行後は自動的に PC に 4 が加算されますが、
     * ジャンプ後は加算が実行されません。
     *
     * @param val 次に実行する命令の相対アドレス
     */
    @Override
    public void jumpRel(int val) {
        setPC(getPC() + val);
    }

    /**
     * レジスタ Rn の値を取得します。
     *
     * @param n レジスタ番号（0 ～ 15）
     * @return レジスタの値
     */
    @Override
    public int getReg(int n) {
        if (n == 15) {
            if (getCPSR().getTBit()) {
                //Thumb モード
                return getRegRaw(n) + 4;
            } else {
                //ARM モード
                return getRegRaw(n) + 8;
            }
        } else {
            return getRegRaw(n);
        }
    }

    /**
     * レジスタ Rn の値を設定します。
     *
     * @param n   レジスタ番号（0 ～ 15）
     * @param val 新しいレジスタの値
     */
    @Override
    public void setReg(int n, int val) {
        if (n == 15) {
            setJumped(true);
        }
        setRegRaw(n, val);
    }

    /**
     * レジスタ Rn そのものの値を取得します。
     *
     * r15 を返す際に +8 のオフセットを加算しません。
     *
     * @param n レジスタ番号（0 ～ 15）、16 は SPSR を示す
     * @return レジスタの値
     */
    @Override
    public int getRegRaw(int n) {
        return regfile.getReg(n).getValue();
    }

    /**
     * レジスタ Rn そのもの値を設定します。
     *
     * r15 を設定する際にジャンプ済みフラグをセットしません。
     *
     * @param n   レジスタ番号（0 ～ 15）、16 は SPSR を示す
     * @param val 新しいレジスタの値
     */
    @Override
    public void setRegRaw(int n, int val) {
        regfile.getReg(n).setValue(val);
    }

    @Override
    public String getRegName(int n) {
        return regfile.getReg(n).getName();
    }

    /**
     * CPSR（カレントプログラムステートレジスタ）の値を取得します。
     *
     * @return CPSR
     */
    public PSR getCPSR() {
        return regfile.getCPSR();
    }

    /**
     * APSR（アプリケーションプログラムステートレジスタ）の値を取得します。
     *
     * N, Z, C, V, Q, GE のみ取得され、他の値は 0 でマスクされます。
     *
     * @return APSR の値
     */
    public APSR getAPSR() {
        return getCPSR().getAPSR();
    }

    /**
     * SPSR（保存されたプログラムステートレジスタ）の値を取得します。
     *
     * @return SPSR の値
     */
    public PSR getSPSR() {
        return regfile.getSPSR();
    }

    /**
     * コプロセッサ Pn を取得します。
     *
     * @param cpnum コプロセッサ番号
     * @return コプロセッサ
     */
    public CoProc getCoproc(int cpnum) {
        return coProcs[cpnum];
    }

    /**
     * コプロセッサレジスタ CRn の名前を取得します。
     *
     * @param cpnum コプロセッサ番号
     * @param n     コプロセッサレジスタ番号（0 ～ 7）
     * @return コプロセッサレジスタの名前
     */
    public static String getCoprocRegName(int cpnum, int n) {
        return String.format("cr%d", n);
    }

    /**
     * MMU を取得します。
     *
     * @return MMU
     */
    public MMUv5 getMMU() {
        return mmu;
    }

    /**
     * 命令を取得します。
     *
     * @return 命令
     */
    public Instruction fetch() {
        int v, vaddr, paddr;

        //現在の PC の指すアドレスから命令を取得します
        vaddr = getRegRaw(15);

        if (getCPSR().getTBit()) {
            //Thumb モード
            paddr = getMMU().translate(vaddr, 2, true, getCPSR().isPrivMode(), true);
            if (getMMU().isFault()) {
                getMMU().clearFault();
                return null;
            }

            if (!tryRead_a32(paddr, 2)) {
                raiseException(EXCEPT_ABT_INST,
                        String.format("exec [%08x]", paddr));
                return null;
            }
            v = read16_a32(paddr);
            instT32.reuse(v & 0xffff, 2);

            if (instT32.getSubCodeField() == InstructionThumb.SUBCODE_BL_BLX && instT32.getField(11, 2) != 0) {
                //Thumb-2 命令
                vaddr += 2;
                paddr = getMMU().translate(vaddr, 2, true, getCPSR().isPrivMode(), true);
                if (getMMU().isFault()) {
                    getMMU().clearFault();
                    return null;
                }

                if (!tryRead_a32(paddr, 2)) {
                    raiseException(EXCEPT_ABT_INST,
                            String.format("exec [%08x]", paddr));
                    return null;
                }
                v = read16_a32(paddr);
                instT32.reuse((instT32.getInst() << 16) | (v & 0xffff), 4);
            }

            return instT32;
        } else {
            //ARM モード
            paddr = getMMU().translate(vaddr, 4, true, getCPSR().isPrivMode(), true);
            if (getMMU().isFault()) {
                getMMU().clearFault();
                return null;
            }

            if (!tryRead_a32(paddr, 4)) {
                raiseException(EXCEPT_ABT_INST,
                        String.format("exec [%08x]", paddr));
                return null;
            }
            v = read32_a32(paddr);
            instA32.reuse(v, 4);

            return instA32;
        }
    }

    /**
     * 命令をデコードします。
     *
     * @param instgen 命令
     * @return デコードされた命令
     */
    public Opcode decode(Instruction instgen) {
        OpType optype;
        OpIndex opind;

        if (getCPSR().getTBit()) {
            InstructionThumb inst = (InstructionThumb)instgen;

            //Thumb モード
            if (inst.getLength() == 4) {
                //Thumb-2 命令
                optype = OpType.INS_TYPE_THUMB2;
                opind = thumb2Decode.decode(inst);
            } else {
                //Thumb 命令
                optype = OpType.INS_TYPE_THUMB;
                opind = thumbDecode.decode(inst);
            }
        } else {
            InstructionARM inst = (InstructionARM)instgen;

            //ARM 命令
            optype = OpType.INS_TYPE_ARM;
            opind = armDecode.decode(inst);
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
        case INS_TYPE_ARM:
            armExec.execute(decinst, exec);
            break;
        case INS_TYPE_THUMB:
            thumbExec.execute(decinst, exec);
            break;
        case INS_TYPE_THUMB2:
            thumb2Exec.execute(decinst, exec);
            break;
        default:
            throw new IllegalArgumentException("Unknown instruction type " +
                    decinst.getType());
        }
    }

    public static final int EXCEPT_RST = 0;
    public static final int EXCEPT_ABT_DATA = 1;
    public static final int EXCEPT_FIQ = 2;
    public static final int EXCEPT_IRQ = 3;
    public static final int EXCEPT_ABT_INST = 4;
    public static final int EXCEPT_UND = 5;
    public static final int EXCEPT_SVC = 6;

    /**
     * 例外を要求します。
     *
     * @param num    例外番号（EXCEPT_xxxx）
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

    /**
     * 最も優先度の高い例外を 1つだけ発生させます。
     *
     * 優先度の低い例外は後回しにされます。
     */
    public void doImportantException() {
        boolean found = false;
        int i;

        for (i = 0; i < exceptions.length; i++) {
            if (exceptions[i]) {
                exceptions[i] = false;
                found = true;
                break;
            }
        }
        if (!found) {
            return;
        }

        switch (i) {
        case EXCEPT_RST:
            doExceptionReset(exceptionReasons[i]);
            break;
        case EXCEPT_UND:
            doExceptionUndefined(exceptionReasons[i]);
            break;
        case EXCEPT_SVC:
            doExceptionSoftware(exceptionReasons[i]);
            break;
        case EXCEPT_ABT_INST:
            doExceptionPrefetch(exceptionReasons[i]);
            break;
        case EXCEPT_ABT_DATA:
            doExceptionData(exceptionReasons[i]);
            break;
        case EXCEPT_IRQ:
            doExceptionIRQ(exceptionReasons[i]);
            break;
        case EXCEPT_FIQ:
            doExceptionFIQ(exceptionReasons[i]);
            break;
        default:
            throw new IllegalArgumentException("Illegal exception number " + i);
        }
    }

    /**
     * リセット例外を発生させます。
     *
     * @param dbgmsg デバッグ用のメッセージ
     */
    public void doExceptionReset(String dbgmsg) {
        int cpsrOrg;

        System.out.printf("Exception: Reset by '%s'.\n",
                dbgmsg);

        //cpsr の値を取っておく
        cpsrOrg = getCPSR().getValue();

        //スーパーバイザモード、ARM 状態、高速割り込み禁止、割り込み禁止、
        //へ移行
        getCPSR().setMode(PSR.MODE_SVC);
        getCPSR().setTBit(false);
        getCPSR().setFBit(true);
        getCPSR().setIBit(true);

        //spsr にリセット前の cpsr を保存する
        getSPSR().setValue(cpsrOrg);

        //リセット例外ベクタへ
        if (isHighVector()) {
            setRegRaw(15, 0xffff0000);
        } else {
            setRegRaw(15, 0x00000000);
        }
    }

    /**
     * 未定義例外を発生させます。
     *
     * この例外はコプロセッサ命令の実行時、
     * 応答するコプロセッサが存在しないときに発生します。
     *
     * @param dbgmsg デバッグ用のメッセージ
     */
    public void doExceptionUndefined(String dbgmsg) {
        int pcOrg, cpsrOrg;

        System.out.printf("Exception: Undefined instruction by '%s'.\n",
                dbgmsg);

        //pc, cpsr の値を取っておく
        if (getCPSR().getTBit()) {
            //Thumb モード
            pcOrg = getRegRaw(15) + 2;
        } else {
            //ARM モード
            pcOrg = getRegRaw(15) + 4;
        }
        cpsrOrg = getCPSR().getValue();

        //未定義モード、ARM 状態、割り込み禁止、
        //へ移行
        getCPSR().setMode(PSR.MODE_UND);
        getCPSR().setTBit(false);
        //F flag is not affected
        getCPSR().setIBit(true);

        //lr, spsr に例外前の pc, cpsr を保存する
        setReg(14, pcOrg);
        getSPSR().setValue(cpsrOrg);

        //未定義例外ベクタへ
        if (isHighVector()) {
            setRegRaw(15, 0xffff0004);
        } else {
            setRegRaw(15, 0x00000004);
        }

        //tentative...
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * ソフトウェア割り込み例外を発生させます。
     *
     * この例外は swi 命令を実行したときに生成されます。
     *
     * @param dbgmsg デバッグ用のメッセージ
     */
    public void doExceptionSoftware(String dbgmsg) {
        int pcOrg, cpsrOrg;

        //System.out.printf("Exception: Software interrupt by '%s'.\n",
        //        dbgmsg);

        //pc, cpsr の値を取っておく
        if (getCPSR().getTBit()) {
            //Thumb モード
            pcOrg = getRegRaw(15) + 2;
        } else {
            //ARM モード
            pcOrg = getRegRaw(15) + 4;
        }
        cpsrOrg = getCPSR().getValue();

        //スーパバイザモード、ARM 状態、割り込み禁止、
        //へ移行
        getCPSR().setMode(PSR.MODE_SVC);
        getCPSR().setTBit(false);
        //F flag is not affected
        getCPSR().setIBit(true);

        //lr, spsr に例外前の pc, cpsr を保存する
        setReg(14, pcOrg);
        getSPSR().setValue(cpsrOrg);

        //ソフトウェア割り込み例外ベクタへ
        if (isHighVector()) {
            setRegRaw(15, 0xffff0008);
        } else {
            setRegRaw(15, 0x00000008);
        }
    }

    /**
     * プリフェッチアボート例外を発生させます。
     *
     * この例外は無効な命令を実行したときに生成されます。
     *
     * @param dbgmsg デバッグ用のメッセージ
     */
    public void doExceptionPrefetch(String dbgmsg) {
        int pcOrg, cpsrOrg;

        //System.out.printf("Exception: Prefetch abort by '%s'.\n",
        //        dbgmsg);

        //pc, cpsr の値を取っておく
        //Thumb, ARM モード
        pcOrg = getRegRaw(15) + 4;
        cpsrOrg = getCPSR().getValue();

        //アボートモード、ARM 状態、割り込み禁止、
        //へ移行
        getCPSR().setMode(PSR.MODE_ABT);
        getCPSR().setTBit(false);
        //F flag is not affected
        getCPSR().setIBit(true);

        //lr, spsr に例外前の pc, cpsr を保存する
        setReg(14, pcOrg);
        getSPSR().setValue(cpsrOrg);

        //プリフェッチアボート例外ベクタへ
        if (isHighVector()) {
            setRegRaw(15, 0xffff000c);
        } else {
            setRegRaw(15, 0x0000000c);
        }
    }

    /**
     * データアボート例外を発生させます。
     *
     * この例外は無効なロード、あるいはストア命令を実行したときに生成されます。
     *
     * @param dbgmsg デバッグ用のメッセージ
     */
    public void doExceptionData(String dbgmsg) {
        int pcOrg, cpsrOrg;

        //System.out.printf("Exception: Data abort by '%s'.\n",
        //        dbgmsg);

        //pc, cpsr の値を取っておく
        //Thumb, ARM モード
        pcOrg = getRegRaw(15) + 8;
        cpsrOrg = getCPSR().getValue();

        //アボートモード、ARM 状態、割り込み禁止、
        //へ移行
        getCPSR().setMode(PSR.MODE_ABT);
        getCPSR().setTBit(false);
        //F flag is not affected
        getCPSR().setIBit(true);

        //lr, spsr に例外前の pc, cpsr を保存する
        setReg(14, pcOrg);
        getSPSR().setValue(cpsrOrg);

        //データアボート例外ベクタへ
        if (isHighVector()) {
            setRegRaw(15, 0xffff0010);
        } else {
            setRegRaw(15, 0x00000010);
        }
    }

    /**
     * 割り込み要求例外を発生させます。
     *
     * この例外はプロセッサの IRQ のアサートにより生成されます。
     *
     * @param dbgmsg デバッグ用のメッセージ
     */
    public void doExceptionIRQ(String dbgmsg) {
        int pcOrg, cpsrOrg;

        //System.out.printf("Exception: IRQ by '%s'.\n",
        //        dbgmsg);

        //pc, cpsr の値を取っておく
        //Thumb, ARM モード
        pcOrg = getRegRaw(15) + 4;
        cpsrOrg = getCPSR().getValue();

        //IRQ モード、ARM 状態、割り込み禁止、
        //へ移行
        getCPSR().setMode(PSR.MODE_IRQ);
        getCPSR().setTBit(false);
        //F flag is not affected
        getCPSR().setIBit(true);

        //lr, spsr に例外前の pc, cpsr を保存する
        setReg(14, pcOrg);
        getSPSR().setValue(cpsrOrg);

        //IRQ 例外ベクタへ
        if (isHighVector()) {
            setRegRaw(15, 0xffff0018);
        } else {
            setRegRaw(15, 0x00000018);
        }
    }

    /**
     * 高速割り込み要求例外を発生させます。
     *
     * この例外はプロセッサの FIQ のアサートにより生成されます。
     *
     * @param dbgmsg デバッグ用のメッセージ
     */
    public void doExceptionFIQ(String dbgmsg) {
        int pcOrg, cpsrOrg;

        System.out.printf("Exception: FIQ by '%s'.\n",
                dbgmsg);

        //pc, cpsr の値を取っておく
        //Thumb, ARM モード
        pcOrg = getRegRaw(15) + 4;
        cpsrOrg = getCPSR().getValue();

        //FIQ モード、ARM 状態、高速割り込み禁止、割り込み禁止、
        //へ移行
        getCPSR().setMode(PSR.MODE_FIQ);
        getCPSR().setTBit(false);
        getCPSR().setFBit(true);
        getCPSR().setIBit(true);

        //lr, spsr に例外前の pc, cpsr を保存する
        setReg(14, pcOrg);
        getSPSR().setValue(cpsrOrg);

        //FIQ 例外ベクタへ
        if (isHighVector()) {
            setRegRaw(15, 0xffff001c);
        } else {
            setRegRaw(15, 0x0000001c);
        }
    }

    /**
     * 割り込み線にコアを接続します。
     *
     * 割り込み線の番号に INTSRC_IRQ を指定すると割り込み線に、
     * INTSRC_FIQ を指定すると高速割り込み線に接続されます。
     *
     * @param n 割り込み線の番号
     * @param c 割り込みを発生させるコア
     */
    public void connectINTSource(int n, INTSource c) {
        intc.connectINTSource(n, c);
    }

    /**
     * 割り込み線からコアを切断します。
     *
     * 割り込み線の番号に INTSRC_IRQ を指定すると、
     * 割り込み線に接続されていたコアが切断され、
     * INTSRC_FIQ を指定すると、
     * 高速割り込み線に接続されていたコアが切断されます。
     *
     * @param n 割り込み線の番号
     */
    public void disconnectINTSource(int n) {
        intc.disconnectINTSource(n);
    }

    /**
     * いずれかの割り込みコントローラが割り込み線をアサートしていたら、
     * IRQ 例外を要求します。
     */
    public void acceptIRQ() {
        if (getCPSR().getIBit()) {
            //I ビットが 1 の場合は、IRQ 無効を意味する
            return;
        }

        if (!intc.getINTSource(INTSRC_IRQ).isAssert()) {
            //割り込み要求がない
            return;
        }

        //割り込み要求の詳細説明を得る
        String msg = String.format("accept IRQ from '%s'",
                intc.getINTSource(INTSRC_IRQ).getIRQMessage());

        raiseException(EXCEPT_IRQ, msg);
    }

    /**
     * いずれかの割り込みコントローラが割り込み線をアサートしていたら、
     * FIQ 例外を要求します。
     */
    public void acceptFIQ() {
        if (getCPSR().getFBit()) {
            //F ビットが 1 の場合は、FIQ 無効を意味する
            return;
        }

        if (!intc.getINTSource(INTSRC_FIQ).isAssert()) {
            //割り込み要求がない
            return;
        }

        //割り込み要求の詳細説明を得る
        String msg = String.format("accept FIQ from '%s'",
                intc.getINTSource(INTSRC_FIQ).getIRQMessage());

        raiseException(EXCEPT_FIQ, msg);
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
     * 例外ベクタの位置が、ハイベクタ 0xffff0000～0xffff001c にあるか、
     * 正規ベクタ 0x00000000～0x0000001c にあるかを取得します。
     *
     * @return 例外ベクタの位置、ハイベクタの場合は true、
     * 正規ベクタの場合は false
     */
    public boolean isHighVector() {
        return highVector;
    }

    /**
     * 例外ベクタの位置が、ハイベクタ 0xffff0000～0xffff001c にあるか、
     * 正規ベクタ 0x00000000～0x0000001c にあるかを設定します。
     *
     * @param m 新たな例外ベクタの位置、ハイベクタの場合は true、
     *          正規ベクタの場合は false
     */
    public void setHighVector(boolean m) {
        highVector = m;
    }

    @Override
    public void step() {
        Instruction inst;
        Opcode decinst;

        //要求された例外のうち、優先度の高い例外を 1つだけ処理します
        doImportantException();

        if (isRaisedInterrupt()) {
            //高速割り込み線がアサートされていれば、FIQ 例外を要求します
            acceptFIQ();
            if (isRaisedException()) {
                setRaisedException(false);
                return;
            }

            //割り込み線がアサートされていれば、IRQ 例外を要求します
            acceptIRQ();
            if (isRaisedException()) {
                setRaisedException(false);
                return;
            }

            if (!intc.getINTSource(INTSRC_IRQ).isAssert() &&
                    !intc.getINTSource(INTSRC_FIQ).isAssert()) {
                setRaisedInterrupt(false);
            }
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
