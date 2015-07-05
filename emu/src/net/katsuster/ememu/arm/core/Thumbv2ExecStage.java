package net.katsuster.ememu.arm.core;

import net.katsuster.ememu.generic.*;

/**
 * Thumb v2 命令の実行ステージ。
 *
 * 参考: ARM アーキテクチャリファレンスマニュアル Second Edition
 * ARM DDI0100DJ
 *
 * 最新版は、日本語版 ARM DDI0100HJ, 英語版 ARM DDI0100I
 *
 * @author katsuhiro
 */
public class Thumbv2ExecStage extends ExecStage {
    /**
     * ARMv5 CPU コア c の実行ステージを生成します。
     *
     * @param c 実行ステージの持ち主となる ARMv5 CPU コア
     */
    public Thumbv2ExecStage(ARMv5 c) {
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
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * 排他的論理和命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeEor(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * レジスタ論理左シフト命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLsl2(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * レジスタ論理右シフト命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLsr2(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * レジスタ算術右シフト命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAsr2(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * キャリー付き加算命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAdc(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * キャリー付き減算命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSbc(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * レジスタ右ローテート命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeRor(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * テスト命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeTst(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
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
            printDisasm(inst, "negs",
                    String.format("%s, %s",
                            getRegName(rd), getRegName(rm)));
            return;
        }

        left = 0;
        right = -getReg(rm);
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
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
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
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * 乗算命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeMul(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * ビットクリア命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeBic(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * 移動否定命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeMvn(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
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
            printDisasm(inst, "adds",
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
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * レジスタ加算命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAdd3(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
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
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * SP への加算（7ビットイミディエート）命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAdd7(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * 小さいイミディエート減算命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSub1(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * イミディエート減算命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSub2(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * レジスタ減算命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSub3(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * SP への減算（7ビットイミディエート）命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSub4(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * イミディエートとの比較命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeCmp1(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
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
            printDisasm(inst, "movs",
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
            printDisasm(inst, "lsls",
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
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * イミディエート算術右シフト命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAsr1(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * リテラルプールのロード命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLdr3(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
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
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * ロードマルチプル命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLdmia(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * ストアマルチプル命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeStmia(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
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
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }
}
