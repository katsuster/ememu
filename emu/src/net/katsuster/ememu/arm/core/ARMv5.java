package net.katsuster.ememu.arm.core;

import net.katsuster.ememu.generic.*;

/**
 * ARMv5TE CPU
 *
 * 参考: ARM アーキテクチャリファレンスマニュアル Second Edition
 * ARM DDI0100DJ
 *
 * 最新版は、日本語版 ARM DDI0100HJ, 英語版 ARM DDI0100I
 *
 * T は Thumb 命令、
 * E はエンハンスド DSP 命令、
 * のことらしい。
 *
 * @author katsuhiro
 */
public class ARMv5 extends CPU {
    //IRQ, FIQ の 2つの割り込み線を持つ
    public static final int MAX_INTSRCS = 2;
    public static final int INTSRC_IRQ = 0;
    public static final int INTSRC_FIQ = 1;

    private ARMv5ExecStage armExec;
    private Thumbv2ExecStage thumbExec;

    private ARMRegFile regfile;
    private CoProc[] coProcs;
    private MMUv5 mmu;
    private NormalINTC intc;

    private boolean exceptions[];
    private String exceptionReasons[];

    private boolean raisedException;
    private boolean jumped;
    private boolean highVector;

    public ARMv5() {
        CoProcVFPv2 cpVfps;
        CoProcStdv5 cpStd;

        cpVfps = new CoProcVFPv2(10, this);
        cpStd = new CoProcStdv5(15, this);

        armExec = new ARMv5ExecStage(this);
        thumbExec = new Thumbv2ExecStage(this);

        regfile = new ARMRegFile();
        coProcs = new CoProc[16];
        coProcs[10] = cpVfps;
        coProcs[15] = cpStd;
        mmu = new MMUv5(this, cpStd);
        intc = new NormalINTC(MAX_INTSRCS);
        intc.connectINTDestination(this);

        exceptions = new boolean[7];
        exceptionReasons = new String[7];

        raisedException = false;
        jumped = false;
        highVector = false;
    }

    @Override
    public String instructionToString(Instruction inst, String operation, String operand) {
        return String.format("%08x:    %08x    %-7s %s\n",
                getRegRaw(15), inst.getInst(), operation, operand);
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
    public void nextPC() {
        if (isJumped()) {
            setJumped(false);
            return;
        }
        if (getCPSR().getTBit()) {
            //Thumb モード
            setRegRaw(15, getRegRaw(15) + 2);
        } else {
            //ARM モード
            setRegRaw(15, getRegRaw(15) + 4);
        }
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
        setJumped(true);
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
            InstructionThumb inst;

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
            inst = new InstructionThumb(v);

            return inst;
        } else {
            //ARM モード
            InstructionARM inst;

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
            inst = new InstructionARM(v);

            return inst;
        }
    }

    /**
     * 命令を逆アセンブルします。
     *
     * @param instgen 命令
     */
    public void disasm(Instruction instgen) {
        executeInst(instgen, false);
    }

    /**
     * 命令を実行します。
     *
     * @param instgen 命令
     */
    public void execute(Instruction instgen) {
        executeInst(instgen, true);
    }

    /**
     * 命令をデコード、逆アセンブル、実行します。
     *
     * @param instgen 命令
     * @param exec デコード、逆アセンブルと実行なら true、
     *             デコード、逆アセンブルのみなら false
     */
    public void executeInst(Instruction instgen, boolean exec) {
        if (getCPSR().getTBit()) {
            InstructionThumb inst = (InstructionThumb)instgen;
            //int cond = inst.getCondField();
            int subcode = inst.getSubCodeField();

            //Thumb モード
            switch (subcode) {
            case InstructionThumb.SUBCODE_ADDSUB:
                decodeAddSub(inst, exec);
                return;
            case InstructionThumb.SUBCODE_ALUIMM:
                decodeALUImm(inst, exec);
                return;
            case InstructionThumb.SUBCODE_ALUREG:
                decodeALUReg(inst, exec);
                return;
            case InstructionThumb.SUBCODE_LDWORD:
                decodeLdWord(inst, exec);
                return;
            case InstructionThumb.SUBCODE_LDHALF:
                decodeLdHalf(inst, exec);
                return;
            case InstructionThumb.SUBCODE_OTHERS:
                decodeOthers(inst, exec);
                return;
            case InstructionThumb.SUBCODE_LDMULT:
                decodeLdmult(inst, exec);
                return;
            case InstructionThumb.SUBCODE_BL_BLX:
                decodeBlBlx(inst, exec);
                return;
            default:
                //do nothing
                break;
            }

            throw new IllegalArgumentException("Unknown Subcode" +
                    String.format("(%d).", subcode));
        } else {
            InstructionARM inst = (InstructionARM)instgen;
            //int cond = inst.getCondField();
            int subcode = inst.getSubCodeField();

            //ARM モード
            switch (subcode) {
            case InstructionARM.SUBCODE_USEALU:
                decodeALU(inst, exec);
                return;
            case InstructionARM.SUBCODE_LDRSTR:
                decodeLdrStr(inst, exec);
                return;
            case InstructionARM.SUBCODE_LDMSTM:
                decodeLdmStm(inst, exec);
                return;
            case InstructionARM.SUBCODE_COPSWI:
                decodeCopSwi(inst, exec);
                return;
            default:
                //do nothing
                break;
            }

            throw new IllegalArgumentException("Unknown Subcode" +
                    String.format("(%d).", subcode));
        }
    }

    /**
     * レジスタ加算、減算命令をデコードします。
     *
     * subcode = 0b000
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void decodeAddSub(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * イミディエート加算、減算命令をデコードします。
     *
     * subcode = 0b001
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void decodeALUImm(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * レジスタへのデータ処理命令をデコードします。
     *
     * subcode = 0b010
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void decodeALUReg(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * ワード、バイトのロード、ストア命令をデコードします。
     *
     * subcode = 0b011
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void decodeLdWord(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * ハーフワードのロード、ストア命令、スタックのロード、ストア命令をデコードします。
     *
     * subcode = 0b100
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void decodeLdHalf(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * SP, PC 加算命令、その他の命令をデコードします。
     *
     * subcode = 0b101
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void decodeOthers(InstructionThumb inst, boolean exec) {
        boolean b12 = inst.getBit(12);

        if (!b12) {
            //PC, SP への add
            boolean sp = inst.getBit(11);

            if (!sp) {
                //add(5), PC への加算
                thumbExec.executeAdd5(inst, exec);
            } else {
                //add(6), SP への加算
                thumbExec.executeAdd6(inst, exec);
            }
        } else {
            //その他の命令
            int op = inst.getField(8, 4);
            boolean b7 = inst.getBit(7);

            switch (op) {
            case 0x0:
                //スタックポインタの加減算
                if (!b7) {
                    //add(7)
                    thumbExec.executeAdd7(inst, exec);
                } else {
                    //sub(7)
                    thumbExec.executeSub4(inst, exec);
                }
                break;
            case 0x4: //0b0100
            case 0x5: //0b0101
                //push
                thumbExec.executePush(inst, exec);
                break;
            case 0xc: //0b1100
            case 0xd: //0b1101
                //pop
                thumbExec.executePop(inst, exec);
                break;
            case 0xe:
                //bkpt
                thumbExec.executeBkpt(inst, exec);
                break;
            }
        }
    }

    /**
     * ロード、ストアマルチプル命令、条件付き分岐命令をデコードします。
     *
     * subcode = 0b110
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void decodeLdmult(InstructionThumb inst, boolean exec) {
        boolean b12 = inst.getBit(12);

        if (!b12) {
            //ロードストアマルチプル
            boolean l = inst.getBit(11);

            if (l) {
                //ldmia
                //thumbExec.executeLdmia(inst, exec);
            } else {
                //stmia
                //thumbExec.executeStmia(inst, exec);
            }
        } else {
            //分岐命令
            int cond = inst.getField(8, 4);

            switch (cond) {
            case InstructionARM.COND_AL:
                //未定義命令
                //thumbExec.executeUnd(inst, exec);
                break;
            case InstructionARM.COND_NV:
                //swi
                //thumbExec.executeSwi(inst, exec);
                break;
            default:
                //b
                //thumbExec.executeB(inst, exec);
                break;
            }
        }

        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * 分岐命令をデコードします。
     *
     * subcode = 0b111
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void decodeBlBlx(InstructionThumb inst, boolean exec) {
        int h = inst.getField(11, 2);

        switch (h) {
        case 0x0:
            //b(無条件分岐)命令
            break;
        case 0x1:
            //blx, 未定義命令
            break;
        case 0x2:
            //bl/blx 命令
            break;
        case 0x3:
            //bl 命令
            break;
        default:
            //異常な値
            throw new IllegalArgumentException("Illegal h bits " +
                    String.format("h:0x%02x.", h));
        }
    }

    /**
     * データ処理命令をデコードします。
     *
     * subcode = 0b00
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void decodeALU(InstructionARM inst, boolean exec) {
        boolean i = inst.getIBit();
        boolean b7 = inst.getBit(7);
        boolean b4 = inst.getBit(4);

        if (!i) {
            //b7, b4 の値が、
            //  0, 0: イミディエートシフト
            //  1, 0: イミディエートシフト
            //  0, 1: レジスタシフト
            //  1, 1: 算術命令拡張空間、ロードストア命令拡張空間
            if (!b4) {
                //イミディエートシフト
                decodeALUShiftImm(inst, exec);
            } else if (!b7 && b4) {
                //レジスタシフト
                decodeALUShiftReg(inst, exec);
            } else {
                //算術命令拡張空間、ロードストア命令拡張空間
                int cond = inst.getCondField();
                boolean p = inst.getBit(24);
                int op = inst.getField(5, 2);

                if (cond != InstructionARM.COND_NV && !p && op == 0) {
                    //算術命令拡張空間
                    decodeExtALU(inst, exec);
                } else {
                    //ロードストア命令拡張空間
                    decodeExtLdrStr(inst, exec);
                }
            }
        } else {
            //イミディエート
            decodeALUImm(inst, exec);
        }
    }

    /**
     * イミディエートシフトオペランドを取るデータ処理命令、
     * または、その他の命令をデコードします。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void decodeALUShiftImm(InstructionARM inst, boolean exec) {
        int id = inst.getOpcodeSBitShiftID();

        switch (id) {
        case InstructionARM.OPCODE_S_OTH:
            decodeALUOther(inst, exec);
            break;
        default:
            armExec.executeALU(inst, exec, id);
            break;
        }
    }

    /**
     * レジスタシフトオペランドを取るデータ処理命令、
     * その他の命令をデコードします。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void decodeALUShiftReg(InstructionARM inst, boolean exec) {
        int id = inst.getOpcodeSBitShiftID();

        switch (id) {
        case InstructionARM.OPCODE_S_OTH:
            decodeALUOther(inst, exec);
            break;
        default:
            armExec.executeALU(inst, exec, id);
            break;
        }
    }

    /**
     * 算術命令拡張空間（乗算）、
     * をデコードします。
     *
     * cond != NV
     * bit[27:24] = 0b0000
     * bit[7:4] = 0b1001
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void decodeExtALU(InstructionARM inst, boolean exec) {
        //U, B, W ビット[23:21]
        int ubw = inst.getField(21, 3);

        //算術命令拡張空間
        switch (ubw) {
        case 1:
            //mla
            armExec.executeMla(inst, exec);
            break;
        case 0:
            //mul
            armExec.executeMul(inst, exec);
            break;
        case 7:
            //smlal
            armExec.executeSmlal(inst, exec);
            break;
        case 6:
            //smull
            armExec.executeSmull(inst, exec);
            break;
        case 5:
            //umlal
            armExec.executeUmlal(inst, exec);
            break;
        case 4:
            //umull
            armExec.executeUmull(inst, exec);
            break;
        default:
            //未定義
            //TODO: Not implemented
            armExec.executeUnd(inst, exec);
            break;
        }
    }

    /**
     * ロードストア命令拡張空間（ハーフワードロード、ストア）、
     * をデコードします。
     *
     * cond != NV
     * bit[27:25] = 0b000
     * bit[7] = 0b1
     * bit[4] = 0b1
     *
     * なおかつ、下記を含まない命令です。
     *
     * bit[24] = 0b0
     * bit[6:5] = 0b00
     *
     * 各ビットと命令の対応は下記の通りです。
     *
     *        |  P  |  U  |  B  |  W  |  L  ||          op1          |
     *        | 24  | 23  | 22  | 21  | 20  ||  7  |  6  |  5  |  4  |
     * -------+-----+-----+-----+-----+-----++-----+-----+-----+-----+
     * SWP    |  1  |  0  |  0  |  0  |  0  ||  1  |  0  |  0  |  1  |
     * SWPB   |  1  |  0  |  1  |  0  |  0  ||  1  |  0  |  0  |  1  |
     * -------+-----+-----+-----+-----+-----++-----+-----+-----+-----+
     * LDRH   |  x  |  x  |  x  |  x  |  1  ||  1  |  0  |  1  |  1  |
     * STRH   |  x  |  x  |  x  |  x  |  0  ||  1  |  0  |  1  |  1  |
     * -------+-----+-----+-----+-----+-----++-----+-----+-----+-----+
     * LDRSB  |  x  |  x  |  x  |  x  |  1  ||  1  |  1  |  0  |  1  |
     * LDRD   |  x  |  x  |  x  |  x  |  0  ||  1  |  1  |  0  |  1  |
     * -------+-----+-----+-----+-----+-----++-----+-----+-----+-----+
     * LDRSH  |  x  |  x  |  x  |  x  |  1  ||  1  |  1  |  1  |  1  |
     * STRD   |  x  |  x  |  x  |  x  |  0  ||  1  |  1  |  1  |  1  |
     * -------+-----+-----+-----+-----+-----++-----+-----+-----+-----+
     *
     * これ以外のパターンは全て未定義命令です。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void decodeExtLdrStr(InstructionARM inst, boolean exec) {
        boolean p = inst.getBit(24);
        //U, B, W ビット[23:21]
        int ubw = inst.getField(21, 3);
        boolean l = inst.getBit(20);
        int op = inst.getField(5, 2);

        //ロードストア命令拡張空間
        if (p && op == 0) {
            switch (ubw) {
            case 0:
                //swp
                armExec.executeSwp(inst, exec);
                break;
            case 1:
                //swpb
                armExec.executeSwpb(inst, exec);
                break;
            default:
                //未定義
                //TODO: Not implemented
                armExec.executeUnd(inst, exec);
                break;
            }
        } else if (op == 1) {
            if (l) {
                //ldrh
                armExec.executeLdrh(inst, exec);
            } else {
                //strh
                armExec.executeStrh(inst, exec);
            }
        } else if (op == 2) {
            if (l) {
                //ldrsb
                armExec.executeLdrsb(inst, exec);
            } else {
                //ldrd
                armExec.executeLdrd(inst, exec);
            }
        } else if (op == 3) {
            if (l) {
                //ldrsh
                armExec.executeLdrsh(inst, exec);
            } else {
                //strd
                armExec.executeStrd(inst, exec);
            }
        } else {
            //未定義
            //TODO: Not implemented
            armExec.executeUnd(inst, exec);
        }
    }

    /**
     * イミディエートのみを取るデータ処理命令、その他の命令をデコードします。
     *
     * データ処理イミディエート命令、
     * ステータスレジスタへのイミディエート移動命令、
     * の実行
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void decodeALUImm(InstructionARM inst, boolean exec) {
        int id = inst.getOpcodeSBitImmID();

        switch (id) {
        case InstructionARM.OPCODE_S_MSR:
            armExec.executeMsr(inst, exec);
            break;
        case InstructionARM.OPCODE_S_UND:
            armExec.executeUnd(inst, exec);
            break;
        default:
            armExec.executeALU(inst, exec, id);
            break;
        }
    }

    /**
     * その他のデータ処理命令、
     * をデコードします。
     *
     * bit[27:23] = 0b00010
     * bit[20] = 0
     *
     * 各ビットと命令の対応は下記の通りです。
     *
     *         | 22  | 21  ||  7  |  6  |  5  |  4  |
     * --------+-----+-----++-----+-----+-----+-----+
     * MRS     |  x  |  0  ||  0  |  0  |  0  |  0  |
     * MSR     |  x  |  1  ||  0  |  0  |  0  |  0  |
     * BX      |  0  |  1  ||  0  |  0  |  0  |  1  |
     * CLZ     |  1  |  1  ||  0  |  0  |  0  |  1  |
     * BLX(2)  |  0  |  1  ||  0  |  0  |  1  |  1  |
     * BKPT    |  0  |  1  ||  0  |  1  |  1  |  1  |
     * --------+-----+-----++-----+-----+-----+-----+
     * QADD    |  0  |  0  ||  0  |  1  |  0  |  1  |
     * QSUB    |  0  |  1  ||  0  |  1  |  0  |  1  |
     * QDADD   |  1  |  0  ||  0  |  1  |  0  |  1  |
     * QDSUB   |  1  |  1  ||  0  |  1  |  0  |  1  |
     * --------+-----+-----++-----+-----+-----+-----+
     * SMLAxy  |  0  |  0  ||  1  |  y  |  x  |  0  |
     * SMLAWxy |  0  |  1  ||  1  |  y  |  0  |  0  |
     * SMULWxy |  0  |  1  ||  1  |  y  |  1  |  0  |
     * SMLALxy |  1  |  0  ||  1  |  y  |  x  |  0  |
     * SMULxy  |  1  |  1  ||  1  |  y  |  x  |  0  |
     * --------+-----+-----++-----+-----+-----+-----+
     *
     * これ以外のパターンは全て未定義命令です。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void decodeALUOther(InstructionARM inst, boolean exec) {
        int cond = inst.getCondField();
        boolean b22 = inst.getBit(22);
        boolean b21 = inst.getBit(21);
        int type = inst.getField(4, 4);

        switch (type) {
        case 0x0:
            if (!b21) {
                //mrs
                armExec.executeMrs(inst, exec);
            } else {
                //msr
                armExec.executeMsr(inst, exec);
            }
            break;
        case 0x1:
            if (!b22 && b21) {
                //bx
                armExec.executeBx(inst, exec);
            } else if (b22 && b21) {
                //clz
                armExec.executeClz(inst, exec);
            } else {
                //未定義
                armExec.executeUnd(inst, exec);
            }
            break;
        case 0x3:
            if (!b22 && b21) {
                //blx(2)
                armExec.executeBlx2(inst, exec);
            } else {
                //未定義
                armExec.executeUnd(inst, exec);
            }
            break;
        case 0x5:
            if (!b22 && !b21) {
                //qdsub
                armExec.executeQdsub(inst, exec);
            } else if (!b22 && b21) {
                //qdadd
                armExec.executeQdadd(inst, exec);
            } else if (b22 && !b21) {
                //qsub
                armExec.executeQsub(inst, exec);
            } else {
                //qadd
                armExec.executeQadd(inst, exec);
            }
            break;
        case 0x7:
            if (cond == InstructionARM.COND_AL && !b22 && b21) {
                //bkpt
                armExec.executeBkpt(inst, exec);
            } else {
                //未定義
                armExec.executeUnd(inst, exec);
            }
            break;
        case 0x8:
        case 0xc:
            if (!b22 && !b21) {
                //smla
                armExec.executeSmlaxy(inst, exec);
            } else if (!b22 && b21) {
                //smlaw
                armExec.executeSmlawy(inst, exec);
            } else if (b22 && !b21) {
                //smlal
                armExec.executeSmlalxy(inst, exec);
            } else {
                //smul
                armExec.executeSmulxy(inst, exec);
            }
            break;
        case 0xa:
        case 0xe:
            if (!b22 && !b21) {
                //smla
                armExec.executeSmlaxy(inst, exec);
            } else if (!b22 && b21) {
                //smulw
                armExec.executeSmulwy(inst, exec);
            } else if (b22 && !b21) {
                //smlal
                armExec.executeSmlalxy(inst, exec);
            } else {
                //smul
                armExec.executeSmulxy(inst, exec);
            }
            break;
        default:
            //未定義
            //TODO: Not implemented
            armExec.executeUnd(inst, exec);
            break;
        }
    }

    /**
     * ロード、ストア命令をデコードします。
     *
     * subcode = 0b01
     *
     * 各ビットと命令の対応は下記の通りです。
     *
     *        |  I  |  P  |  B  |  W  |  L  |     |
     *        | 25  | 24  | 22  | 21  | 20  |  4  |
     * -------+-----+-----+-----+-----+-----+-----+
     * LDR    |  x  |  x  |  0  |  x  |  1  |  x  |
     * LDRB   |  x  |  x  |  1  |  x  |  1  |  x  |
     * LDRBT  |  x  |  0  |  1  |  1  |  1  |  x  |
     * LDRT   |  x  |  0  |  0  |  1  |  1  |  x  |
     * -------+-----+-----+-----+-----+-----+-----+
     * STR    |  x  |  x  |  0  |  x  |  0  |  x  |
     * STRB   |  x  |  x  |  1  |  x  |  0  |  x  |
     * STRBT  |  x  |  0  |  1  |  1  |  0  |  x  |
     * STRT   |  x  |  0  |  0  |  1  |  0  |  x  |
     * -------+-----+-----+-----+-----+-----+-----+
     * UND    |  1  |  x  |  x  |  x  |  x  |  1  |
     * -------+-----+-----+-----+-----+-----+-----+
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void decodeLdrStr(InstructionARM inst, boolean exec) {
        int cond = inst.getCondField();
        boolean i = inst.getBit(25);
        boolean p = inst.getBit(24);
        boolean b = inst.getBit(22);
        boolean w = inst.getBit(21);
        boolean l = inst.getLBit();
        int rd = inst.getRdField();
        boolean b4 = inst.getBit(4);

        if (i && b4) {
            //未定義命令
            //TODO: Not implemented
            armExec.executeUnd(inst, exec);
        } else if (l) {
            if (!p && !b && w) {
                //ldrt
                armExec.executeLdrt(inst, exec);
            } else if (!p && b && w) {
                //ldrbt
                armExec.executeLdrbt(inst, exec);
            } else if (b) {
                if (cond == InstructionARM.COND_NV && p && !w && rd == 15) {
                    //pld
                    armExec.executePld(inst, exec);
                } else {
                    //ldrb
                    armExec.executeLdrb(inst, exec);
                }
            } else if (!b) {
                //ldr
                armExec.executeLdr(inst, exec);
            } else {
                throw new IllegalArgumentException("Illegal P,B,W bits " +
                        String.format("p:%b, b:%b, w:%b.", p, b, w));
            }
        } else if (!l) {
            if (!p && !b && w) {
                //strt
                armExec.executeStrt(inst, exec);
            } else if (!p && b && w) {
                //strbt
                armExec.executeStrbt(inst, exec);
            } else if (b) {
                //strb
                armExec.executeStrb(inst, exec);
            } else if (!b) {
                //str
                armExec.executeStr(inst, exec);
            } else {
                throw new IllegalArgumentException("Illegal P,B,W bits " +
                        String.format("p:%b, b:%b, w:%b.", p, b, w));
            }
        } else {
            throw new IllegalArgumentException("Illegal P,B,W,L bits " +
                    String.format("p:%b, b:%b, w:%b, l:%b.", p, b, w, l));
        }
    }

    /**
     * ロードマルチプル、ストアマルチプル、分岐命令をデコードします。
     *
     * subcode = 0b10
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void decodeLdmStm(InstructionARM inst, boolean exec) {
        int cond = inst.getCondField();
        boolean b25 = inst.getBit(25);
        boolean l = inst.getLBit();

        if (!b25) {
            //ロードマルチプル、ストアマルチプル
            if (cond == InstructionARM.COND_NV) {
                //未定義
                //TODO: Not implemented
                armExec.executeUnd(inst, exec);
            } else {
                if (l) {
                    //ldm(1), ldm(2), ldm(3)
                    decodeLdm(inst, exec);
                } else {
                    //stm(1), stm(2)
                    decodeStm(inst, exec);
                }
            }
        } else {
            //分岐命令
            if (cond == InstructionARM.COND_NV) {
                //blx
                armExec.executeBlx1(inst, exec);
            } else {
                //b, bl
                armExec.executeBl(inst, exec);
            }
        }
    }

    /**
     * ロードマルチプル命令をデコードします。
     *
     * subcode = 0b10
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void decodeLdm(InstructionARM inst, boolean exec) {
        boolean s = inst.getBit(22);
        boolean b15 = inst.getBit(15);

        if (!s) {
            //ldm(1)
            armExec.executeLdm1(inst, exec);
        } else {
            if (!b15) {
                //ldm(2)
                armExec.executeLdm2(inst, exec);
            } else {
                //ldm(3)
                armExec.executeLdm3(inst, exec);
            }
        }
    }

    /**
     * ストアマルチプル命令をデコードします。
     *
     * subcode = 0b10
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void decodeStm(InstructionARM inst, boolean exec) {
        boolean s = inst.getBit(22);
        boolean w = inst.getBit(21);

        if (!s) {
            //stm(1)
            armExec.executeStm1(inst, exec);
        } else {
            if (!w) {
                //stm(2)
                armExec.executeStm2(inst, exec);
            } else {
                //未定義
                armExec.executeUnd(inst, exec);
            }
        }
    }

    /**
     * コプロセッサ、ソフトウェア割り込み命令をデコードします。
     *
     * subcode = 0b11
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void decodeCopSwi(InstructionARM inst, boolean exec) {
        int cond = inst.getCondField();
        int subsub = inst.getField(24, 2);
        boolean b20 = inst.getBit(20);
        boolean b4 = inst.getBit(4);

        switch (subsub) {
        case 0:
        case 1:
            if (b20) {
                //ldc
                //TODO: Not implemented
                throw new IllegalArgumentException("Sorry, not implemented.");
            } else {
                //stc
                //TODO: Not implemented
                throw new IllegalArgumentException("Sorry, not implemented.");
            }
            //break;
        case 2:
            if (!b4) {
                //cdp
                armExec.executeCdp(inst, exec);
            } else {
                if (!b20) {
                    //mcr
                    armExec.executeMcr(inst, exec);
                } else {
                    //mrc
                    armExec.executeMrc(inst, exec);
                }
            }
            return;
        case 3:
            if (cond == InstructionARM.COND_NV) {
                //未定義
                armExec.executeUnd(inst, exec);
            } else {
                //swi
                armExec.executeSwi(inst, exec);
            }
            return;
        default:
            //do nothing
            break;
        }

        throw new IllegalArgumentException("Illegal b25, b24 bits " +
                String.format("b25b24:%d.", subsub));
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
        synchronized(this) {
            raisedException = m;
        }
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
        synchronized(this) {
            jumped = b;
        }
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
        synchronized(this) {
            highVector = m;
        }
    }

    @Override
    public void step() {
        Instruction inst;

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

        //逆アセンブルします
        if (isEnabledDisasm()) {
            disasm(inst);
        }

        //FIXME: Thumb モードの時は必ず逆アセンブルします
        if (getCPSR().getTBit()) {
            disasm(inst);
        }

        //実行して、次の命令へ
        execute(inst);
        if (isRaisedException()) {
            setRaisedException(false);
            return;
        }
        nextPC();
    }
}
