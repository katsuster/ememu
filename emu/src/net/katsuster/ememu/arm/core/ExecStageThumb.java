package net.katsuster.ememu.arm.core;

import net.katsuster.ememu.generic.*;

/**
 * Thumb 命令（Thumb v1, v2, v3）の実行ステージ。
 *
 * 参考: ARM アーキテクチャリファレンスマニュアル Second Edition
 * ARM DDI0100DJ
 *
 * 最新版は、日本語版 ARM DDI0100HJ, 英語版 ARM DDI0100I
 *
 * @author katsuhiro
 */
public class ExecStageThumb extends Stage {
    /**
     * ARMv5 CPU コア c の実行ステージを生成します。
     *
     * @param c 実行ステージの持ち主となる ARMv5 CPU コア
     */
    public ExecStageThumb(ARMv5 c) {
        super(c);
    }

    /**
     * 実行ステージの持ち主となる ARMv5 CPU コアを取得します。
     *
     * @return 実行ステージの持ち主となる ARMv5 CPU コア
     */
    @Override
    public ARMv5 getCore() {
        return (ARMv5)super.getCore();
    }

    /**
     * CPSR（カレントプログラムステートレジスタ）の値を取得します。
     *
     * @return CPSR
     */
    public PSR getCPSR() {
        return getCore().getCPSR();
    }

    /**
     * APSR（アプリケーションプログラムステートレジスタ）の値を取得します。
     *
     * N, Z, C, V, Q, GE のみ取得され、他の値は 0 でマスクされます。
     *
     * @return APSR の値
     */
    public APSR getAPSR() {
        return getCore().getAPSR();
    }

    /**
     * SPSR（保存されたプログラムステートレジスタ）の値を取得します。
     *
     * @return SPSR の値
     */
    public PSR getSPSR() {
        return getCore().getSPSR();
    }

    /**
     * コプロセッサ Pn を取得します。
     *
     * @param cpnum コプロセッサ番号
     * @return コプロセッサ
     */
    public CoProc getCoproc(int cpnum) {
        return getCore().getCoproc(cpnum);
    }

    /**
     * コプロセッサレジスタ CRn の名前を取得します。
     *
     * @param cpnum コプロセッサ番号
     * @param n     コプロセッサレジスタ番号（0 ～ 7）
     * @return コプロセッサレジスタの名前
     */
    public String getCoprocRegName(int cpnum, int n) {
        return getCore().getCoprocRegName(cpnum, n);
    }

    /**
     * MMU を取得します。
     *
     * @return MMU
     */
    public MMUv5 getMMU() {
        return getCore().getMMU();
    }

    /**
     * 例外を要求します。
     *
     * @param num    例外番号（EXCEPT_xxxx）
     * @param dbgmsg デバッグ用のメッセージ
     */
    public void raiseException(int num, String dbgmsg) {
        getCore().raiseException(num, dbgmsg);
    }

    /**
     * 論理積命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAnd(InstructionThumb inst, boolean exec) {
        int rm = inst.getRmField();
        int rd = inst.getRdField();
        int left, right, dest;

        if (!exec) {
            printDisasm(inst, "and",
                    String.format("%s, %s",
                            getRegName(rd), getRegName(rm)));
            return;
        }

        left = getReg(rd);
        right = getReg(rm);
        dest = left & right;

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        //C flag is unaffected
        //V flag is unaffected

        setReg(rd, dest);
    }

    /**
     * 排他的論理和命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeEor(InstructionThumb inst, boolean exec) {
        int rm = inst.getRmField();
        int rd = inst.getRdField();
        int left, right, dest;

        if (!exec) {
            printDisasm(inst, "eor",
                    String.format("%s, %s",
                            getRegName(rd), getRegName(rm)));
            return;
        }

        left = getReg(rd);
        right = getReg(rm);
        dest = left ^ right;

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        //C flag is unaffected
        //V flag is unaffected

        setReg(rd, dest);
    }

    /**
     * レジスタ論理左シフト命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLsl2(InstructionThumb inst, boolean exec) {
        int rs = inst.getField(3, 3);
        int rd = inst.getRdField();
        int left, right, dest;
        boolean cbit;

        if (!exec) {
            printDisasm(inst, "lsl",
                    String.format("%s, %s",
                            getRegName(rd), getRegName(rs)));
            return;
        }

        left = getReg(rd);
        right = getReg(rs) & 0xff;
        if (right == 0) {
            cbit = getCPSR().getCBit();
            dest = left;
        } else if (right < 32) {
            cbit = BitOp.getBit32(left, 32 - right);
            dest = left << right;
        } else if (right == 32) {
            cbit = BitOp.getBit32(left, 0);
            dest = 0;
        } else {
            cbit = false;
            dest = 0;
        }

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        getCPSR().setCBit(cbit);
        //V flag is unaffected

        setReg(rd, dest);
    }

    /**
     * レジスタ論理右シフト命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLsr2(InstructionThumb inst, boolean exec) {
        int rs = inst.getField(3, 3);
        int rd = inst.getRdField();
        int left, right, dest;
        boolean cbit;

        if (!exec) {
            printDisasm(inst, "lsr",
                    String.format("%s, %s",
                            getRegName(rd), getRegName(rs)));
            return;
        }

        left = getReg(rd);
        right = getReg(rs) & 0xff;
        if (right == 0) {
            cbit = getCPSR().getCBit();
            dest = left;
        } else if (right < 32) {
            cbit = BitOp.getBit32(left, right - 1);
            dest = left >>> right;
        } else if (right == 32) {
            cbit = BitOp.getBit32(left, 31);
            dest = 0;
        } else {
            cbit = false;
            dest = 0;
        }

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        getCPSR().setCBit(cbit);
        //V flag is unaffected

        setReg(rd, dest);
    }

    /**
     * レジスタ算術右シフト命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAsr2(InstructionThumb inst, boolean exec) {
        int rs = inst.getField(3, 3);
        int rd = inst.getRdField();
        int left, right, dest;
        boolean cbit;

        if (!exec) {
            printDisasm(inst, "asr",
                    String.format("%s, %s",
                            getRegName(rd), getRegName(rs)));
            return;
        }

        left = getReg(rd);
        right = getReg(rs) & 0xff;
        if (right == 0) {
            cbit = getCPSR().getCBit();
            dest = left;
        } else if (right < 32) {
            cbit = BitOp.getBit32(left, right - 1);
            dest = left >> right;
        } else {
            cbit = BitOp.getBit32(left, 31);
            if (cbit) {
                dest = 0xffffffff;
            } else {
                dest = 0;
            }
        }

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        getCPSR().setCBit(cbit);
        //V flag is unaffected

        setReg(rd, dest);
    }

    /**
     * キャリー付き加算命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAdc(InstructionThumb inst, boolean exec) {
        int rm = inst.getRmField();
        int rd = inst.getRdField();
        int left, center, right, dest;

        if (!exec) {
            printDisasm(inst, "adc",
                    String.format("%s, %s",
                            getRegName(rd), getRegName(rm)));
            return;
        }

        left = getReg(rd);
        center = getReg(rm);
        right = BitOp.toInt(getCPSR().getCBit());
        dest = left + center + right;

        int lc = left + center;
        boolean lc_c = IntegerExt.carryFrom(left, center);
        boolean lc_v = IntegerExt.overflowFrom(left, center, true);

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        getCPSR().setCBit(lc_c || IntegerExt.carryFrom(lc, right));
        getCPSR().setVBit(lc_v || IntegerExt.overflowFrom(lc, right, true));

        setReg(rd, dest);
    }

    /**
     * キャリー付き減算命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSbc(InstructionThumb inst, boolean exec) {
        int rm = inst.getRmField();
        int rd = inst.getRdField();
        int left, center, right, dest;

        if (!exec) {
            printDisasm(inst, "sbc",
                    String.format("%s, %s",
                            getRegName(rd), getRegName(rm)));
            return;
        }

        left = getReg(rd);
        center = getReg(rm);
        right = BitOp.toInt(!getCPSR().getCBit());
        dest = left - center - right;

        int lc = left - center;
        boolean lc_c = IntegerExt.borrowFrom(left, center);
        boolean lc_v = IntegerExt.overflowFrom(left, center, false);

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        getCPSR().setCBit(!(lc_c || IntegerExt.borrowFrom(lc, right)));
        getCPSR().setVBit(lc_v || IntegerExt.overflowFrom(lc, right, false));

        setReg(rd, dest);
    }

    /**
     * レジスタ右ローテート命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeRor(InstructionThumb inst, boolean exec) {
        int rs = inst.getField(3, 3);
        int rd = inst.getRdField();
        int left, right8, right5, dest;
        boolean cbit;

        if (!exec) {
            printDisasm(inst, "ror",
                    String.format("%s, %s",
                            getRegName(rd), getRegName(rs)));
            return;
        }

        left = getReg(rd);
        right8 = getReg(rs) & 0xff;
        right5 = getReg(rs) & 0x1f;
        if (right8 == 0) {
            cbit = getCPSR().getCBit();
            dest = left;
        } else if (right5 == 32) {
            cbit = BitOp.getBit32(left, 31);
            dest = left;
        } else {
            cbit = BitOp.getBit32(left, right5 - 1);
            dest = Integer.rotateRight(left, right5);
        }

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        getCPSR().setCBit(cbit);
        //V flag is unaffected

        setReg(rd, dest);
    }

    /**
     * テスト命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeTst(InstructionThumb inst, boolean exec) {
        int rm = inst.getRmField();
        int rn = inst.getField(0, 3);
        int left, right, dest;

        if (!exec) {
            printDisasm(inst, "tst",
                    String.format("%s, %s",
                            getRegName(rn), getRegName(rm)));
            return;
        }

        left = getReg(rn);
        right = getReg(rm);
        dest = left & right;

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        //C flag is unaffected
        //V flag is unaffected
    }

    /**
     * 2 の補数命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeNeg(InstructionThumb inst, boolean exec) {
        int rm = inst.getRmField();
        int rd = inst.getRdField();
        int left, right, dest;

        if (!exec) {
            printDisasm(inst, "neg",
                    String.format("%s, %s",
                            getRegName(rd), getRegName(rm)));
            return;
        }

        left = 0;
        right = getReg(rm);
        dest = left - right;

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        getCPSR().setCBit(!IntegerExt.borrowFrom(left, right));
        getCPSR().setVBit(IntegerExt.overflowFrom(left, right, false));

        setReg(rd, dest);
    }

    /**
     * レジスタ比較命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeCmp2(InstructionThumb inst, boolean exec) {
        int rm = inst.getRmField();
        int rn = inst.getField(0, 3);
        int left, right, dest;

        if (!exec) {
            printDisasm(inst, "cmp",
                    String.format("%s, %s",
                            getRegName(rn), getRegName(rm)));
            return;
        }

        left = getReg(rn);
        right = getReg(rm);
        dest = left - right;

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        getCPSR().setCBit(!IntegerExt.borrowFrom(left, right));
        getCPSR().setVBit(IntegerExt.overflowFrom(left, right, false));
    }

    /**
     * 2 の補数比較命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeCmn(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * 論理和命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeOrr(InstructionThumb inst, boolean exec) {
        int rm = inst.getRmField();
        int rd = inst.getRdField();
        int left, right, dest;

        if (!exec) {
            printDisasm(inst, "orr",
                    String.format("%s, %s",
                            getRegName(rd), getRegName(rm)));
            return;
        }

        left = getReg(rd);
        right = getReg(rm);
        dest = left | right;

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        //C flag is unaffected
        //V flag is unaffected

        setReg(rd, dest);
    }

    /**
     * 乗算命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeMul(InstructionThumb inst, boolean exec) {
        int rm = inst.getRmField();
        int rd = inst.getRdField();
        int left, right, dest;

        if (!exec) {
            printDisasm(inst, "mul",
                    String.format("%s, %s",
                            getRegName(rd), getRegName(rm)));
            return;
        }

        left = getReg(rd);
        right = getReg(rm);
        dest = left * right;

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        //C flag is unaffected
        //V flag is unaffected

        setReg(rd, dest);
    }

    /**
     * ビットクリア命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeBic(InstructionThumb inst, boolean exec) {
        int rm = inst.getRmField();
        int rd = inst.getRdField();
        int left, right, dest;

        if (!exec) {
            printDisasm(inst, "bic",
                    String.format("%s, %s",
                            getRegName(rd), getRegName(rm)));
            return;
        }

        left = getReg(rd);
        right = getReg(rm);
        dest = left & ~right;

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        //C flag is unaffected
        //V flag is unaffected

        setReg(rd, dest);
    }

    /**
     * 移動否定命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeMvn(InstructionThumb inst, boolean exec) {
        int rm = inst.getRmField();
        int rd = inst.getRdField();
        int left, dest;

        if (!exec) {
            printDisasm(inst, "mvn",
                    String.format("%s, %s",
                            getRegName(rd), getRegName(rm)));
            return;
        }

        left = getReg(rm);
        dest = ~left;

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        //C flag is unaffected
        //V flag is unaffected

        setReg(rd, dest);
    }

    /**
     * 小さいイミディエート加算命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAdd1(InstructionThumb inst, boolean exec) {
        int imm3 = inst.getField(6, 3);
        int rn = inst.getField(3, 3);
        int rd = inst.getRdField();
        int left, right, dest;

        if (!exec) {
            printDisasm(inst, "add",
                    String.format("%s, %s, %s",
                            getRegName(rd), getRegName(rn),
                            String.format("#%d    ; 0x%x", imm3, imm3)));
            return;
        }

        left = getReg(rn);
        right = imm3;
        dest = left + right;

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        getCPSR().setCBit(IntegerExt.carryFrom(left, right));
        getCPSR().setVBit(IntegerExt.overflowFrom(left, right, true));

        setReg(rd, dest);
    }

    /**
     * イミディエート加算命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAdd2(InstructionThumb inst, boolean exec) {
        int rd = inst.getField(8, 3);
        int imm8 = inst.getField(0, 8);
        int left, right, dest;

        if (!exec) {
            printDisasm(inst, "add",
                    String.format("%s, %s",
                            getRegName(rd),
                            String.format("#%d    ; 0x%x", imm8, imm8)));
            return;
        }

        left = getReg(rd);
        right = imm8;
        dest = left + right;

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        getCPSR().setCBit(IntegerExt.carryFrom(left, right));
        getCPSR().setVBit(IntegerExt.overflowFrom(left, right, true));

        setReg(rd, dest);
    }

    /**
     * レジスタ加算命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAdd3(InstructionThumb inst, boolean exec) {
        int rm = inst.getField(6, 3);
        int rn = inst.getField(3, 3);
        int rd = inst.getRdField();
        int left, right, dest;

        if (!exec) {
            printDisasm(inst, "add",
                    String.format("%s, %s, %s",
                            getRegName(rd), getRegName(rn), getRegName(rm)));
            return;
        }

        left = getReg(rn);
        right = getReg(rm);
        dest = left + right;

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        getCPSR().setCBit(IntegerExt.carryFrom(left, right));
        getCPSR().setVBit(IntegerExt.overflowFrom(left, right, true));

        setReg(rd, dest);
    }

    /**
     * 上位レジスタ加算命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAdd4(InstructionThumb inst, boolean exec) {
        int rm = inst.getField(3, 4);
        int rd = (inst.getField(7, 1) << 3) | inst.getRdField();
        int left, right, dest;

        if (!exec) {
            printDisasm(inst, "add",
                    String.format("%s, %s",
                            getRegName(rd), getRegName(rm)));
            return;
        }

        left = getReg(rd);
        right = getReg(rm);
        dest = left + right;

        setReg(rd, dest);
    }

    /**
     * PC への加算命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAdd5(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * SP への加算（8ビットイミディエート）命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAdd6(InstructionThumb inst, boolean exec) {
        int rd = inst.getField(8, 3);
        int imm8_raw = inst.getField(0, 8);
        int imm8 = imm8_raw << 2;
        int left, right, dest;

        if (!exec) {
            printDisasm(inst, "add",
                    String.format("%s, sp, %s",
                            getRegName(rd),
                            String.format("#%d    ; 0x%x", imm8, imm8)));
            return;
        }

        left = getReg(13);
        right = imm8;
        dest = left + right;

        setReg(rd, dest);
    }

    /**
     * SP への加算（7ビットイミディエート）命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAdd7(InstructionThumb inst, boolean exec) {
        int imm7_raw = inst.getField(0, 7);
        int imm7 = imm7_raw << 2;
        int left, right, dest;

        if (!exec) {
            printDisasm(inst, "add",
                    String.format("sp, %s",
                            String.format("#%d    ; 0x%x", imm7, imm7)));
            return;
        }

        left = getReg(13);
        right = imm7;
        dest = left + right;

        setReg(13, dest);
    }

    /**
     * 小さいイミディエート減算命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSub1(InstructionThumb inst, boolean exec) {
        int imm3 = inst.getField(6, 3);
        int rn = inst.getField(3, 3);
        int rd = inst.getRdField();
        int left, right, dest;

        if (!exec) {
            printDisasm(inst, "sub",
                    String.format("%s, %s, %s",
                            getRegName(rd), getRegName(rn),
                            String.format("#%d    ; 0x%x", imm3, imm3)));
            return;
        }

        left = getReg(rn);
        right = imm3;
        dest = left - right;

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        getCPSR().setCBit(!IntegerExt.borrowFrom(left, right));
        getCPSR().setVBit(IntegerExt.overflowFrom(left, right, false));

        setReg(rd, dest);
    }

    /**
     * イミディエート減算命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSub2(InstructionThumb inst, boolean exec) {
        int rd = inst.getField(8, 3);
        int imm8 = inst.getField(0, 8);
        int left, right, dest;

        if (!exec) {
            printDisasm(inst, "sub",
                    String.format("%s, %s",
                            getRegName(rd),
                            String.format("#%d    ; 0x%x", imm8, imm8)));
            return;
        }

        left = getReg(rd);
        right = imm8;
        dest = left - right;

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        getCPSR().setCBit(!IntegerExt.borrowFrom(left, right));
        getCPSR().setVBit(IntegerExt.overflowFrom(left, right, false));

        setReg(rd, dest);
    }

    /**
     * レジスタ減算命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSub3(InstructionThumb inst, boolean exec) {
        int rm = inst.getField(6, 3);
        int rn = inst.getField(3, 3);
        int rd = inst.getRdField();
        int left, right, dest;

        if (!exec) {
            printDisasm(inst, "sub",
                    String.format("%s, %s, %s",
                            getRegName(rd), getRegName(rn), getRegName(rm)));
            return;
        }

        left = getReg(rn);
        right = getReg(rm);
        dest = left - right;

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        getCPSR().setCBit(!IntegerExt.borrowFrom(left, right));
        getCPSR().setVBit(IntegerExt.overflowFrom(left, right, false));

        setReg(rd, dest);
    }

    /**
     * SP への減算（7ビットイミディエート）命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSub4(InstructionThumb inst, boolean exec) {
        int imm7_raw = inst.getField(0, 7);
        int imm7 = imm7_raw << 2;
        int left, right, dest;

        if (!exec) {
            printDisasm(inst, "sub",
                    String.format("sp, %s",
                            String.format("#%d    ; 0x%x", imm7, imm7)));
            return;
        }

        left = getReg(13);
        right = imm7;
        dest = left - right;

        setReg(13, dest);
    }

    /**
     * イミディエートとの比較命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeCmp1(InstructionThumb inst, boolean exec) {
        int rn = inst.getField(8, 3);
        int imm8 = inst.getField(0, 8);
        int left, right, dest;

        if (!exec) {
            printDisasm(inst, "cmp",
                    String.format("%s, %s",
                            getRegName(rn),
                            String.format("#%d    ; 0x%x", imm8, imm8)));
            return;
        }

        left = getReg(rn);
        right = imm8;
        dest = left - right;

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        getCPSR().setCBit(!IntegerExt.borrowFrom(left, right));
        getCPSR().setVBit(IntegerExt.overflowFrom(left, right, false));
    }

    /**
     * 上位レジスタの比較命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeCmp3(InstructionThumb inst, boolean exec) {
        int rm = inst.getField(3, 4);
        int rn = (inst.getField(7, 1) << 3) | inst.getField(0, 3);
        int left, right, dest;

        if (!exec) {
            printDisasm(inst, "cmp",
                    String.format("%s, %s",
                            getRegName(rn), getRegName(rm)));
            return;
        }

        left = getReg(rn);
        right = getReg(rm);
        dest = left - right;

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        getCPSR().setCBit(!IntegerExt.borrowFrom(left, right));
        getCPSR().setVBit(IntegerExt.overflowFrom(left, right, false));
    }

    /**
     * イミディエートの移動命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeMov1(InstructionThumb inst, boolean exec) {
        int rd = inst.getField(8, 3);
        int imm8 = inst.getField(0, 8);
        int right, dest;

        if (!exec) {
            printDisasm(inst, "mov",
                    String.format("%s, %s",
                            getRegName(rd),
                            String.format("#%d    ; 0x%x", imm8, imm8)));
            return;
        }

        right = imm8;
        dest = right;

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        //C flag is unaffected
        //V flag is unaffected

        setReg(rd, dest);
    }

    /**
     * 上位レジスタの移動命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeMov3(InstructionThumb inst, boolean exec) {
        int rm = inst.getField(3, 4);
        int rd = (inst.getField(7, 1) << 3) | inst.getRdField();
        int right, dest;

        if (!exec) {
            printDisasm(inst, "mov",
                    String.format("%s, %s",
                            getRegName(rd), getRegName(rm)));
            return;
        }

        right = getReg(rm);
        dest = right;

        setReg(rd, dest);
    }

    /**
     * イミディエート論理左シフト命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLsl1(InstructionThumb inst, boolean exec) {
        int imm5 = inst.getField(6, 5);
        int rm = inst.getRmField();
        int rd = inst.getRdField();
        int left, dest;
        boolean cbit;

        if (!exec) {
            printDisasm(inst, "lsl",
                    String.format("%s, %s, %s",
                            getRegName(rd), getRegName(rm),
                            String.format("#%d    ; 0x%x", imm5, imm5)));
            return;
        }

        left = getReg(rm);
        if (imm5 == 0) {
            cbit = getCPSR().getCBit();
            dest = left;
        } else {
            cbit = BitOp.getBit32(left, 32 - imm5);
            dest = left << imm5;
        }

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        getCPSR().setCBit(cbit);
        //V flag is unaffected

        setReg(rd, dest);
    }

    /**
     * イミディエート論理右シフト命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLsr1(InstructionThumb inst, boolean exec) {
        int imm5 = inst.getField(6, 5);
        int rm = inst.getRmField();
        int rd = inst.getRdField();
        int left, dest;
        boolean cbit;

        if (!exec) {
            printDisasm(inst, "lsr",
                    String.format("%s, %s, #%d",
                            getRegName(rd), getRegName(rm), imm5));
            return;
        }

        left = getReg(rm);
        if (imm5 == 0) {
            cbit = BitOp.getBit32(left, 31);
            dest = 0;
        } else {
            cbit = BitOp.getBit32(left, imm5 - 1);
            dest = left >>> imm5;
        }

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        getCPSR().setCBit(cbit);
        //V flag is unaffected

        setReg(rd, dest);
    }

    /**
     * イミディエート算術右シフト命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAsr1(InstructionThumb inst, boolean exec) {
        int imm5 = inst.getField(6, 5);
        int rm = inst.getRmField();
        int rd = inst.getRdField();
        int left, dest;
        boolean cbit;

        if (!exec) {
            printDisasm(inst, "asr",
                    String.format("%s, %s, #%d",
                            getRegName(rd), getRegName(rm), imm5));
            return;
        }

        left = getReg(rm);
        if (imm5 == 0) {
            cbit = BitOp.getBit32(left, 31);
            if (cbit) {
                dest = 0xffffffff;
            } else {
                dest = 0;
            }
        } else {
            cbit = BitOp.getBit32(left, imm5 - 1);
            dest = left >> imm5;
        }

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        getCPSR().setCBit(cbit);
        //V flag is unaffected

        setReg(rd, dest);
    }

    /**
     * ロード命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLdr1(InstructionThumb inst, boolean exec) {
        int imm5_raw = inst.getField(6, 5);
        int rn = inst.getField(3, 3);
        int rd = inst.getRdField();
        int imm5 = imm5_raw << 2;
        int vaddr, paddr;

        if (!exec) {
            printDisasm(inst, "ldr",
                    String.format("%s, [%s, #%d]",
                            getRegName(rd),
                            getRegName(rn), imm5));
            return;
        }

        vaddr = getReg(rn) + imm5;

        paddr = getMMU().translate(vaddr, 4, false, getCPSR().isPrivMode(), true);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryRead_a32(paddr, 4)) {
            raiseException(ARMv5.EXCEPT_ABT_DATA,
                    String.format("ldr [%08x]", paddr));
            return;
        }
        setReg(rd, read32_a32(paddr));
    }

    /**
     * ロード命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLdr2(InstructionThumb inst, boolean exec) {
        int rm = inst.getField(6, 3);
        int rn = inst.getField(3, 3);
        int rd = inst.getRdField();
        int vaddr, paddr, value;

        if (!exec) {
            printDisasm(inst, "ldr",
                    String.format("%s, [%s, %s]",
                            getRegName(rd), getRegName(rn), getRegName(rm)));
            return;
        }

        vaddr = getReg(rn) + getReg(rm);

        paddr = getMMU().translate(vaddr, 4, false, getCPSR().isPrivMode(), true);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryRead_a32(paddr, 4)) {
            raiseException(ARMv5.EXCEPT_ABT_DATA,
                    String.format("ldr [%08x]", paddr));
            return;
        }
        value = read32_a32(paddr);

        setReg(rd, value);
    }

    /**
     * リテラルプールのロード命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLdr3(InstructionThumb inst, boolean exec) {
        int rd = inst.getField(8, 3);
        int imm8_raw = inst.getField(0, 8);
        int imm8 = imm8_raw << 2;
        int vaddr, paddr, value;

        if (!exec) {
            printDisasm(inst, "ldr",
                    String.format("%s, [pc, #%d]",
                            getRegName(rd), imm8));
            return;
        }

        vaddr = (getPC() & 0xfffffffc) + imm8;

        paddr = getMMU().translate(vaddr, 4, false, getCPSR().isPrivMode(), true);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryRead_a32(paddr, 4)) {
            raiseException(ARMv5.EXCEPT_ABT_DATA,
                    String.format("ldr [%08x]", paddr));
            return;
        }
        value = read32_a32(paddr);

        setReg(rd, value);
    }

    /**
     * ロード SP 相対命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLdr4(InstructionThumb inst, boolean exec) {
        int rd = inst.getField(8, 3);
        int imm8_raw = inst.getField(0, 8);
        int imm8 = imm8_raw << 2;
        int vaddr, paddr;

        if (!exec) {
            printDisasm(inst, "ldr",
                    String.format("%s, [sp, #%d]",
                            getRegName(rd), imm8));
            return;
        }

        vaddr = getReg(13) + imm8;

        paddr = getMMU().translate(vaddr, 4, false, getCPSR().isPrivMode(), true);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryRead_a32(paddr, 4)) {
            raiseException(ARMv5.EXCEPT_ABT_DATA,
                    String.format("ldr [%08x]", paddr));
            return;
        }
        setReg(rd, read32_a32(paddr));
    }

    /**
     * バイトロード命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLdrb1(InstructionThumb inst, boolean exec) {
        int imm5 = inst.getField(6, 5);
        int rn = inst.getField(3, 3);
        int rd = inst.getRdField();
        int vaddr, paddr, value;

        if (!exec) {
            printDisasm(inst, "ldrb",
                    String.format("%s, [%s, #%d]",
                            getRegName(rd),
                            getRegName(rn), imm5));
            return;
        }

        vaddr = getReg(rn) + imm5;

        paddr = getMMU().translate(vaddr, 1, false, getCPSR().isPrivMode(), true);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryRead_a32(paddr, 1)) {
            raiseException(ARMv5.EXCEPT_ABT_DATA,
                    String.format("ldrb [%08x]", paddr));
            return;
        }
        value = ((int) read8_a32(paddr)) & 0xff;

        setReg(rd, value);
    }

    /**
     * バイトロード命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLdrb2(InstructionThumb inst, boolean exec) {
        int rm = inst.getField(6, 3);
        int rn = inst.getField(3, 3);
        int rd = inst.getRdField();
        int vaddr, paddr, value;

        if (!exec) {
            printDisasm(inst, "ldrb",
                    String.format("%s, [%s, %s]",
                            getRegName(rd), getRegName(rn), getRegName(rm)));
            return;
        }

        vaddr = getReg(rn) + getReg(rm);

        paddr = getMMU().translate(vaddr, 1, false, getCPSR().isPrivMode(), true);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryRead_a32(paddr, 1)) {
            raiseException(ARMv5.EXCEPT_ABT_DATA,
                    String.format("ldrb [%08x]", paddr));
            return;
        }
        value = ((int) read8_a32(paddr)) & 0xff;

        setReg(rd, value);
    }

    /**
     * ハーフワードロード命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLdrh1(InstructionThumb inst, boolean exec) {
        int imm5_raw = inst.getField(6, 5);
        int rn = inst.getField(3, 3);
        int rd = inst.getRdField();
        int imm5 = imm5_raw << 1;
        int vaddr, paddr, value;

        if (!exec) {
            printDisasm(inst, "ldrh",
                    String.format("%s, [%s, #%d]",
                            getRegName(rd),
                            getRegName(rn), imm5));
            return;
        }

        vaddr = getReg(rn) + imm5;

        paddr = getMMU().translate(vaddr, 2, false, getCPSR().isPrivMode(), true);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryRead_a32(paddr, 2)) {
            raiseException(ARMv5.EXCEPT_ABT_DATA,
                    String.format("ldrh [%08x]", paddr));
            return;
        }
        value = ((int) read16_a32(paddr)) & 0xffff;

        setReg(rd, value);
    }

    /**
     * ハーフワード ロード命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLdrh2(InstructionThumb inst, boolean exec) {
        int rm = inst.getField(6, 3);
        int rn = inst.getField(3, 3);
        int rd = inst.getRdField();
        int vaddr, paddr, value;

        if (!exec) {
            printDisasm(inst, "ldrh",
                    String.format("%s, [%s, %s]",
                            getRegName(rd), getRegName(rn), getRegName(rm)));
            return;
        }

        vaddr = getReg(rn) + getReg(rm);

        paddr = getMMU().translate(vaddr, 2, false, getCPSR().isPrivMode(), true);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryRead_a32(paddr, 2)) {
            raiseException(ARMv5.EXCEPT_ABT_DATA,
                    String.format("ldrh [%08x]", paddr));
            return;
        }
        value = ((int) read16_a32(paddr)) & 0xffff;

        setReg(rd, value);
    }

    /**
     * 符号付きバイトロード命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLdrsb(InstructionThumb inst, boolean exec) {
        int rm = inst.getField(6, 3);
        int rn = inst.getField(3, 3);
        int rd = inst.getRdField();
        int vaddr, paddr, value;

        if (!exec) {
            printDisasm(inst, "ldrsb",
                    String.format("%s, [%s, %s]",
                            getRegName(rd), getRegName(rn), getRegName(rm)));
            return;
        }

        vaddr = getReg(rn) + getReg(rm);

        paddr = getMMU().translate(vaddr, 1, false, getCPSR().isPrivMode(), true);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryRead_a32(paddr, 1)) {
            raiseException(ARMv5.EXCEPT_ABT_DATA,
                    String.format("ldrsb [%08x]", paddr));
            return;
        }
        value = read8_a32(paddr);

        setReg(rd, value);
    }

    /**
     * 符号付きハーフワードロード命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLdrsh(InstructionThumb inst, boolean exec) {
        int rm = inst.getField(6, 3);
        int rn = inst.getField(3, 3);
        int rd = inst.getRdField();
        int vaddr, paddr, value;

        if (!exec) {
            printDisasm(inst, "ldrsh",
                    String.format("%s, [%s, %s]",
                            getRegName(rd), getRegName(rn), getRegName(rm)));
            return;
        }

        vaddr = getReg(rn) + getReg(rm);

        paddr = getMMU().translate(vaddr, 2, false, getCPSR().isPrivMode(), true);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryRead_a32(paddr, 2)) {
            raiseException(ARMv5.EXCEPT_ABT_DATA,
                    String.format("ldrsh [%08x]", paddr));
            return;
        }
        value = read16_a32(paddr);

        setReg(rd, value);
    }

    /**
     * ストア命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeStr1(InstructionThumb inst, boolean exec) {
        int imm5_raw = inst.getField(6, 5);
        int rn = inst.getField(3, 3);
        int rd = inst.getRdField();
        int imm5 = imm5_raw << 2;
        int vaddr, paddr;

        if (!exec) {
            printDisasm(inst, "str",
                    String.format("%s, [%s, #%d]",
                            getRegName(rd),
                            getRegName(rn), imm5));
            return;
        }

        vaddr = getReg(rn) + imm5;

        paddr = getMMU().translate(vaddr, 4, false, getCPSR().isPrivMode(), false);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryWrite_a32(paddr, 4)) {
            raiseException(ARMv5.EXCEPT_ABT_DATA,
                    String.format("str [%08x]", paddr));
            return;
        }
        write32_a32(paddr, getReg(rd));
    }

    /**
     * ストア命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeStr2(InstructionThumb inst, boolean exec) {
        int rm = inst.getField(6, 3);
        int rn = inst.getField(3, 3);
        int rd = inst.getRdField();
        int vaddr, paddr;

        if (!exec) {
            printDisasm(inst, "str",
                    String.format("%s, [%s, %s]",
                            getRegName(rd), getRegName(rn), getRegName(rm)));
            return;
        }

        vaddr = getReg(rn) + getReg(rm);

        paddr = getMMU().translate(vaddr, 4, false, getCPSR().isPrivMode(), false);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryWrite_a32(paddr, 4)) {
            raiseException(ARMv5.EXCEPT_ABT_DATA,
                    String.format("str [%08x]", paddr));
            return;
        }
        write32_a32(paddr, getReg(rd));
    }

    /**
     * ストア SP 相対命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeStr3(InstructionThumb inst, boolean exec) {
        int rd = inst.getField(8, 3);
        int imm8_raw = inst.getField(0, 8);
        int imm8 = imm8_raw << 2;
        int vaddr, paddr;

        if (!exec) {
            printDisasm(inst, "str",
                    String.format("%s, [sp, #%d]",
                            getRegName(rd), imm8));
            return;
        }

        vaddr = getReg(13) + imm8;

        paddr = getMMU().translate(vaddr, 4, false, getCPSR().isPrivMode(), false);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryWrite_a32(paddr, 4)) {
            raiseException(ARMv5.EXCEPT_ABT_DATA,
                    String.format("str [%08x]", paddr));
            return;
        }
        write32_a32(paddr, getReg(rd));
    }

    /**
     * バイトストア命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeStrb1(InstructionThumb inst, boolean exec) {
        int imm5 = inst.getField(6, 5);
        int rn = inst.getField(3, 3);
        int rd = inst.getRdField();
        int vaddr, paddr;

        if (!exec) {
            printDisasm(inst, "strb",
                    String.format("%s, [%s, #%d]",
                            getRegName(rd),
                            getRegName(rn), imm5));
            return;
        }

        vaddr = getReg(rn) + imm5;

        paddr = getMMU().translate(vaddr, 1, false, getCPSR().isPrivMode(), false);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryWrite_a32(paddr, 1)) {
            raiseException(ARMv5.EXCEPT_ABT_DATA,
                    String.format("strb [%08x]", paddr));
            return;
        }
        write8_a32(paddr, (byte) getReg(rd));
    }

    /**
     * バイトストア命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeStrb2(InstructionThumb inst, boolean exec) {
        int rm = inst.getField(6, 3);
        int rn = inst.getField(3, 3);
        int rd = inst.getRdField();
        int vaddr, paddr;

        if (!exec) {
            printDisasm(inst, "strb",
                    String.format("%s, [%s, %s]",
                            getRegName(rd), getRegName(rn), getRegName(rm)));
            return;
        }

        vaddr = getReg(rn) + getReg(rm);

        paddr = getMMU().translate(vaddr, 1, false, getCPSR().isPrivMode(), false);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryWrite_a32(paddr, 1)) {
            raiseException(ARMv5.EXCEPT_ABT_DATA,
                    String.format("strb [%08x]", paddr));
            return;
        }
        write8_a32(paddr, (byte) getReg(rd));
    }

    /**
     * ハーフワードストア命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeStrh1(InstructionThumb inst, boolean exec) {
        int imm5_raw = inst.getField(6, 5);
        int rn = inst.getField(3, 3);
        int rd = inst.getRdField();
        int imm5 = imm5_raw << 1;
        int vaddr, paddr;

        if (!exec) {
            printDisasm(inst, "strh",
                    String.format("%s, [%s, #%d]",
                            getRegName(rd),
                            getRegName(rn), imm5));
            return;
        }

        vaddr = getReg(rn) + imm5;

        paddr = getMMU().translate(vaddr, 2, false, getCPSR().isPrivMode(), false);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryWrite_a32(paddr, 2)) {
            raiseException(ARMv5.EXCEPT_ABT_DATA,
                    String.format("strh [%08x]", paddr));
            return;
        }
        write16_a32(paddr, (short) getReg(rd));
    }


    /**
     * ハーフワードストア命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeStrh2(InstructionThumb inst, boolean exec) {
        int rm = inst.getField(6, 3);
        int rn = inst.getField(3, 3);
        int rd = inst.getRdField();
        int vaddr, paddr;

        if (!exec) {
            printDisasm(inst, "strh",
                    String.format("%s, [%s, %s]",
                            getRegName(rd), getRegName(rn), getRegName(rm)));
            return;
        }

        vaddr = getReg(rn) + getReg(rm);

        paddr = getMMU().translate(vaddr, 2, false, getCPSR().isPrivMode(), false);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryWrite_a32(paddr, 2)) {
            raiseException(ARMv5.EXCEPT_ABT_DATA,
                    String.format("strh [%08x]", paddr));
            return;
        }
        write16_a32(paddr, (short) getReg(rd));
    }

    /**
     * プッシュ命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executePush(InstructionThumb inst, boolean exec) {
        boolean br = inst.getBit(8);
        int rlist = inst.getRegListField();
        int vaddr, paddr, len;

        if (!exec) {
            printDisasm(inst, "push",
                    String.format("{%s%s%s}",
                            inst.getRegListFieldName(),
                            (inst.getRegListField() != 0 && br) ? ", " : "",
                            (br) ? "lr" : ""));
            return;
        }

        vaddr = getReg(13) - (Integer.bitCount(rlist) * 4);
        len = -(Integer.bitCount(rlist) * 4);
        if (br) {
            vaddr -= 4;
            len -= 4;
        }
        for (int i = 0; i < 8; i++) {
            if ((rlist & (1 << i)) == 0) {
                continue;
            }

            paddr = getMMU().translate(vaddr, 4, false, getCPSR().isPrivMode(), false);
            if (getMMU().isFault()) {
                getMMU().clearFault();
                return;
            }

            if (!tryWrite_a32(paddr, 4)) {
                raiseException(ARMv5.EXCEPT_ABT_DATA,
                        String.format("push [%08x]", paddr));
                return;
            }
            write32_a32(paddr, getReg(i));
            vaddr += 4;
        }
        if (br) {
            paddr = getMMU().translate(vaddr, 4, false, getCPSR().isPrivMode(), false);
            if (getMMU().isFault()) {
                getMMU().clearFault();
                return;
            }

            if (!tryWrite_a32(paddr, 4)) {
                raiseException(ARMv5.EXCEPT_ABT_DATA,
                        String.format("push [%08x]", paddr));
                return;
            }
            write32_a32(paddr, getReg(14));
            vaddr += 4;
        }

        setReg(13, getReg(13) + len);
    }

    /**
     * ポップ命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executePop(InstructionThumb inst, boolean exec) {
        boolean br = inst.getBit(8);
        int rlist = inst.getRegListField();
        int vaddr, paddr, v, len;

        if (!exec) {
            printDisasm(inst, "pop",
                    String.format("{%s%s%s}",
                            inst.getRegListFieldName(),
                            (inst.getRegListField() != 0 && br) ? ", " : "",
                            (br) ? "pc" : ""));
            return;
        }

        vaddr = getReg(13);
        len = (Integer.bitCount(rlist) * 4);
        if (br) {
            len += 4;
        }
        for (int i = 0; i < 8; i++) {
            if ((rlist & (1 << i)) == 0) {
                continue;
            }

            paddr = getMMU().translate(vaddr, 4, false, getCPSR().isPrivMode(), true);
            if (getMMU().isFault()) {
                getMMU().clearFault();
                return;
            }

            if (!tryRead_a32(paddr, 4)) {
                raiseException(ARMv5.EXCEPT_ABT_DATA,
                        String.format("pop [%08x]", paddr));
                return;
            }
            setReg(i, read32_a32(paddr));
            vaddr += 4;
        }
        if (br) {
            paddr = getMMU().translate(vaddr, 4, false, getCPSR().isPrivMode(), true);
            if (getMMU().isFault()) {
                getMMU().clearFault();
                return;
            }

            if (!tryRead_a32(paddr, 4)) {
                raiseException(ARMv5.EXCEPT_ABT_DATA,
                        String.format("pop [%08x]", paddr));
                return;
            }
            v = read32_a32(paddr);

            setPC(v & 0xfffffffe);
            getCPSR().setTBit(BitOp.getBit32(v, 0));
            vaddr += 4;
        }

        setReg(13, getReg(13) + len);
    }

    /**
     * ロードマルチプル命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLdmia(InstructionThumb inst, boolean exec) {
        int rn = inst.getField(8, 3);
        int rlist = inst.getRegListField();
        int vaddr, paddr, len;

        if (!exec) {
            printDisasm(inst, "ldmia",
                    String.format("%s!, {%s}",
                            getRegName(rn), inst.getRegListFieldName()));
            return;
        }

        vaddr = getReg(rn);
        len = Integer.bitCount(rlist) * 4;
        for (int i = 0; i < 8; i++) {
            if ((rlist & (1 << i)) == 0) {
                continue;
            }

            paddr = getMMU().translate(vaddr, 4, false, getCPSR().isPrivMode(), true);
            if (getMMU().isFault()) {
                getMMU().clearFault();
                return;
            }

            if (!tryRead_a32(paddr, 4)) {
                raiseException(ARMv5.EXCEPT_ABT_DATA,
                        String.format("ldmia [%08x]", paddr));
                return;
            }
            setReg(i, read32_a32(paddr));
            vaddr += 4;
        }

        setReg(rn, getReg(rn) + len);
    }

    /**
     * ストアマルチプル命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeStmia(InstructionThumb inst, boolean exec) {
        int rn = inst.getField(8, 3);
        int rlist = inst.getRegListField();
        int vaddr, paddr, v, len;

        if (!exec) {
            printDisasm(inst, "stmia",
                    String.format("%s!, {%s}",
                            getRegName(rn), inst.getRegListFieldName()));
            return;
        }

        vaddr = getReg(rn);
        len = Integer.bitCount(rlist) * 4;
        for (int i = 0; i < 8; i++) {
            if ((rlist & (1 << i)) == 0) {
                continue;
            }

            paddr = getMMU().translate(vaddr, 4, false, getCPSR().isPrivMode(), false);
            if (getMMU().isFault()) {
                getMMU().clearFault();
                return;
            }

            if (!tryWrite_a32(paddr, 4)) {
                raiseException(ARMv5.EXCEPT_ABT_DATA,
                        String.format("stmia [%08x]", paddr));
                return;
            }
            write32_a32(paddr, getReg(i));
            vaddr += 4;
        }

        setReg(rn, getReg(rn) + len);
    }

    /**
     * ブレークポイント命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeBkpt(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * 未定義命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeUnd(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * ソフトウェア割り込み命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSwi(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * 条件付き分岐命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeB1(InstructionThumb inst, boolean exec) {
        int cond = inst.getField(8, 4);
        int imm8 = inst.getField(0, 8);
        int simm8 = (int) BitOp.signExt64(imm8, 8) << 1;

        if (!exec) {
            printDisasm(inst,
                    String.format("b%s",
                            InstructionARM.getCondFieldName(cond)),
                    String.format("%08x", getPC() + simm8));
            return;
        }

        if (!InstructionARM.satisfiesCond(cond, getCPSR())) {
            return;
        }

        jumpRel(simm8);
    }

    /**
     * 無条件分岐命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeB2(InstructionThumb inst, boolean exec) {
        int imm11 = inst.getField(0, 11);
        int simm11 = (int) BitOp.signExt64(imm11, 11) << 1;

        if (!exec) {
            printDisasm(inst, "b",
                    String.format("%08x", getPC() + simm11));
            return;
        }

        jumpRel(simm11);
    }

    /**
     * リンク付き分岐と状態遷移命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeBlx2(InstructionThumb inst, boolean exec) {
        int rm = inst.getField(3, 4);
        int dest;

        if (!exec) {
            printDisasm(inst, "blx",
                    String.format("%s", getRegName(rm)));
            return;
        }

        dest = getReg(rm);

        if (!getCPSR().getTBit()) {
            setReg(14, getPC() - 4);
        } else {
            setReg(14, ((getPC() - 2) & 0xfffffffe) | 1);
        }

        //T ビットをセット
        getCPSR().setTBit(BitOp.getBit32(dest, 0));
        setPC(dest & 0xfffffffe);
    }

    /**
     * 分岐と状態遷移命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeBx(InstructionThumb inst, boolean exec) {
        int rm = inst.getField(3, 4);
        int dest;

        if (!exec) {
            printDisasm(inst, "bx",
                    String.format("%s", getRegName(rm)));
            return;
        }

        dest = getReg(rm);

        //T ビットをセット
        getCPSR().setTBit(BitOp.getBit32(dest, 0));
        setPC(dest & 0xfffffffe);
    }

    /**
     * Thumb 命令。
     *
     * @param decinst デコードされた命令
     * @param exec    実行するなら true、実行しないなら false
     */
    public void execute(Opcode decinst, boolean exec) {
        InstructionThumb inst = (InstructionThumb) decinst.getInstruction();

        switch (decinst.getIndex()) {
        case INS_THUMB_AND:
            executeAnd(inst, exec);
            break;
        case INS_THUMB_EOR:
            executeEor(inst, exec);
            break;
        case INS_THUMB_LSL2:
            executeLsl2(inst, exec);
            break;
        case INS_THUMB_LSR2:
            executeLsr2(inst, exec);
            break;
        case INS_THUMB_ASR2:
            executeAsr2(inst, exec);
            break;
        case INS_THUMB_ADC:
            executeAdc(inst, exec);
            break;
        case INS_THUMB_SBC:
            executeSbc(inst, exec);
            break;
        case INS_THUMB_ROR:
            executeRor(inst, exec);
            break;
        case INS_THUMB_TST:
            executeTst(inst, exec);
            break;
        case INS_THUMB_NEG:
            executeNeg(inst, exec);
            break;
        case INS_THUMB_CMP2:
            executeCmp2(inst, exec);
            break;
        case INS_THUMB_CMN:
            executeCmn(inst, exec);
            break;
        case INS_THUMB_ORR:
            executeOrr(inst, exec);
            break;
        case INS_THUMB_MUL:
            executeMul(inst, exec);
            break;
        case INS_THUMB_BIC:
            executeBic(inst, exec);
            break;
        case INS_THUMB_MVN:
            executeMvn(inst, exec);
            break;
        case INS_THUMB_ADD1:
            executeAdd1(inst, exec);
            break;
        case INS_THUMB_ADD2:
            executeAdd2(inst, exec);
            break;
        case INS_THUMB_ADD3:
            executeAdd3(inst, exec);
            break;
        case INS_THUMB_ADD4:
            executeAdd4(inst, exec);
            break;
        case INS_THUMB_ADD5:
            executeAdd5(inst, exec);
            break;
        case INS_THUMB_ADD6:
            executeAdd6(inst, exec);
            break;
        case INS_THUMB_ADD7:
            executeAdd7(inst, exec);
            break;
        case INS_THUMB_SUB1:
            executeSub1(inst, exec);
            break;
        case INS_THUMB_SUB2:
            executeSub2(inst, exec);
            break;
        case INS_THUMB_SUB3:
            executeSub3(inst, exec);
            break;
        case INS_THUMB_SUB4:
            executeSub4(inst, exec);
            break;
        case INS_THUMB_CMP1:
            executeCmp1(inst, exec);
            break;
        case INS_THUMB_CMP3:
            executeCmp3(inst, exec);
            break;
        case INS_THUMB_MOV1:
            executeMov1(inst, exec);
            break;
        case INS_THUMB_MOV3:
            executeMov3(inst, exec);
            break;
        case INS_THUMB_LSL1:
            executeLsl1(inst, exec);
            break;
        case INS_THUMB_LSR1:
            executeLsr1(inst, exec);
            break;
        case INS_THUMB_ASR1:
            executeAsr1(inst, exec);
            break;
        case INS_THUMB_LDR1:
            executeLdr1(inst, exec);
            break;
        case INS_THUMB_LDR2:
            executeLdr2(inst, exec);
            break;
        case INS_THUMB_LDR3:
            executeLdr3(inst, exec);
            break;
        case INS_THUMB_LDR4:
            executeLdr4(inst, exec);
            break;
        case INS_THUMB_LDRB1:
            executeLdrb1(inst, exec);
            break;
        case INS_THUMB_LDRB2:
            executeLdrb2(inst, exec);
            break;
        case INS_THUMB_LDRH1:
            executeLdrh1(inst, exec);
            break;
        case INS_THUMB_LDRH2:
            executeLdrh2(inst, exec);
            break;
        case INS_THUMB_LDRSB:
            executeLdrsb(inst, exec);
            break;
        case INS_THUMB_LDRSH:
            executeLdrsh(inst, exec);
            break;
        case INS_THUMB_STR1:
            executeStr1(inst, exec);
            break;
        case INS_THUMB_STR2:
            executeStr2(inst, exec);
            break;
        case INS_THUMB_STR3:
            executeStr3(inst, exec);
            break;
        case INS_THUMB_STRB1:
            executeStrb1(inst, exec);
            break;
        case INS_THUMB_STRB2:
            executeStrb2(inst, exec);
            break;
        case INS_THUMB_STRH1:
            executeStrh1(inst, exec);
            break;
        case INS_THUMB_STRH2:
            executeStrh2(inst, exec);
            break;
        case INS_THUMB_PUSH:
            executePush(inst, exec);
            break;
        case INS_THUMB_POP:
            executePop(inst, exec);
            break;
        case INS_THUMB_LDMIA:
            executeLdmia(inst, exec);
            break;
        case INS_THUMB_STMIA:
            executeStmia(inst, exec);
            break;
        case INS_THUMB_BKPT:
            executeBkpt(inst, exec);
            break;
        case INS_THUMB_UND:
            executeUnd(inst, exec);
            break;
        case INS_THUMB_SWI:
            executeSwi(inst, exec);
            break;
        case INS_THUMB_B1:
            executeB1(inst, exec);
            break;
        case INS_THUMB_B2:
            executeB2(inst, exec);
            break;
        case INS_THUMB_BLX2:
            executeBlx2(inst, exec);
            break;
        case INS_THUMB_BX:
            executeBx(inst, exec);
            break;
        default:
            throw new IllegalArgumentException("Unknown Thumb instruction " +
                    decinst.getIndex());
        }
    }
}
