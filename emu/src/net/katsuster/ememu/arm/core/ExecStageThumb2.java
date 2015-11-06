package net.katsuster.ememu.arm.core;

import net.katsuster.ememu.generic.*;

/**
 * Thumb-2 命令の実行ステージ。
 *
 * 参考: ARM アーキテクチャリファレンスマニュアル ARMv7-A および ARMv7-R
 * ARM DDI0406BJ
 *
 * 最新版は、日本語版 ARM DDI0406BJ, 英語版 ARM DDI0406C
 *
 * @author katsuhiro
 */
public class ExecStageThumb2 extends Stage {
    /**
     * CPU コア c の実行ステージを生成します。
     *
     * @param c 実行ステージの持ち主となる CPU コア
     */
    public ExecStageThumb2(ARMv5 c) {
        super(c);
    }

    /**
     * 実行ステージの持ち主となる ARMv5 CPU コアを取得します。
     *
     * @return 実行ステージの持ち主となる ARMv5 CPU コア
     */
    @Override
    public ARMv5 getCore() {
        //FIXME: ARMv5 は古い
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
     * 分岐命令。
     *
     * @param inst Thumb2 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeB(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * リンク付き分岐命令。
     *
     * @param inst Thumb2 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeBl(InstructionThumb inst, boolean exec) {
        boolean s = inst.getBit(10 + 16);
        int imm10 = inst.getField(0 + 16, 10);
        boolean j1 = inst.getBit(13);
        boolean j2 = inst.getBit(11);
        int imm11 = inst.getField(0, 11);

        int si = BitOp.toInt(s);
        int i1 = BitOp.toInt(!(j1 ^ s));
        int i2 = BitOp.toInt(!(j2 ^ s));
        int imm25 = si << 24 | i1 << 23 | i2 << 22 |
                imm10 << 12 | imm11 << 1;
        int imm32 = (int)BitOp.signExt64(imm25, 25);

        if (!exec) {
            printDisasm(inst,
                    String.format("bl%s",
                            /*inst.getCondFieldName()*/""),
                    String.format("%08x", getPC() + imm32));
            return;
        }

        //FIXME: Not implemented IF-THEN
        //if InITBlock() && !LastInITBlock() then UNPREDICTABLE;

        // /if ConditionPassed() then
        //EncodingSpecificOperations();

        //if CurrentInstrSet == InstrSet_ARM then
        //        next_instr_addr = PC - 4;
        //LR = next_instr_addr;
        //else
        //next_instr_addr = PC;
        //LR = next_instr_addr<31:1> : ‘1’;

        //if toARM then
        //SelectInstrSet(InstrSet_ARM);
        //BranchWritePC(Align(PC,4) + imm32);
        //else
        //SelectInstrSet(InstrSet_Thumb);
        //BranchWritePC(PC + imm32);

        if (!getCPSR().getTBit()) {
            setReg(14, getPC() - 4);
        } else {
            setReg(14, (getPC() & 0xfffffffe) | 1);
        }
        jumpRel(imm32);

        //TODO: Not implemented
        //throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * リンク付き分岐と状態遷移命令。
     *
     * @param inst Thumb2 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeBlx(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * セキュアモニタコール命令。
     *
     * @param inst Thumb2 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSmc(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * 未定義命令。
     *
     * @param inst Thumb2 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeUnd(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * Thumb2 命令。
     *
     * @param decinst デコードされた命令
     * @param exec    実行するなら true、実行しないなら false
     */
    public void execute(Opcode decinst, boolean exec) {
        InstructionThumb inst = (InstructionThumb) decinst.getInstruction();

        switch (decinst.getIndex()) {
        case INS_THUMB2_B:
            executeB(inst, exec);
            break;
        case INS_THUMB2_BL:
            executeBl(inst, exec);
            break;
        case INS_THUMB2_BLX:
            executeBlx(inst, exec);
            break;
        case INS_THUMB2_SMC:
            executeSmc(inst, exec);
            break;
        case INS_THUMB2_UND:
            executeUnd(inst, exec);
            break;
        default:
            throw new IllegalArgumentException("Unknown Thumb2 instruction " +
                    decinst.getIndex());
        }
    }
}
