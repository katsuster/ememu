package net.katsuster.ememu.riscv.core;

import net.katsuster.ememu.generic.*;

public class DecodeStage extends Stage64 {
    /**
     * RISC-V 64 コア c の実行ステージを生成します。
     *
     * @param c 実行ステージの持ち主となる RISC-V 64 コア
     */
    public DecodeStage(RV64 c) {
        super(c);
    }

    /**
     * 実行ステージの持ち主となる CPU コアを取得します。
     *
     * @return 実行ステージの持ち主となる CPU コア
     */
    @Override
    public RV64 getCore() {
        return (RV64)super.getCore();
    }

    /**
     * 32bit LOAD 命令をデコードします。
     *
     * @param inst 32bit 命令
     * @return 命令の種類
     */
    public OpIndex decodeLoad(InstructionRV32 inst) {
        int funct3 = inst.getFunct3();

        switch (funct3) {
        case InstructionRV32.FUNC_LOAD_LB:
            return OpIndex.INS_RV32I_LB;
        case InstructionRV32.FUNC_LOAD_LH:
            return OpIndex.INS_RV32I_LH;
        case InstructionRV32.FUNC_LOAD_LW:
            return OpIndex.INS_RV32I_LW;
        case InstructionRV32.FUNC_LOAD_LBU:
            return OpIndex.INS_RV32I_LBU;
        case InstructionRV32.FUNC_LOAD_LHU:
            return OpIndex.INS_RV32I_LHU;
        default:
            throw new IllegalArgumentException("Unknown load funct3 " +
                    String.format("%d.", funct3));
        }
    }

    /**
     * 32bit BRANCH 命令をデコードします。
     *
     * @param inst 32bit 命令
     * @return 命令の種類
     */
    public OpIndex decodeBranch(InstructionRV32 inst) {
        int funct3 = inst.getFunct3();

        switch (funct3) {
        case InstructionRV32.FUNC_BRANCH_BEQ:
            return OpIndex.INS_RV32I_BEQ;
        case InstructionRV32.FUNC_BRANCH_BNE:
            return OpIndex.INS_RV32I_BNE;
        case InstructionRV32.FUNC_BRANCH_BLT:
            return OpIndex.INS_RV32I_BLT;
        case InstructionRV32.FUNC_BRANCH_BGE:
            return OpIndex.INS_RV32I_BGE;
        case InstructionRV32.FUNC_BRANCH_BLTU:
            return OpIndex.INS_RV32I_BLTU;
        case InstructionRV32.FUNC_BRANCH_BGEU:
            return OpIndex.INS_RV32I_BGEU;
        default:
            throw new IllegalArgumentException("Unknown branch funct3 " +
                    String.format("%d.", funct3));
        }
    }

    /**
     * 32bit 命令をデコードします。
     *
     * @param inst 32bit 命令
     * @return 命令の種類
     */
    public OpIndex decode(InstructionRV32 inst) {
        int code = inst.getOpcode();

        switch (code) {
        case InstructionRV32.OPCODE_LOAD:
            return decodeLoad(inst);
        case InstructionRV32.OPCODE_AUIPC:
            return OpIndex.INS_RV32I_AUIPC;
        case InstructionRV32.OPCODE_BRANCH:
            return decodeBranch(inst);
        default:
            throw new IllegalArgumentException("Unknown opcode " +
                    String.format("%d.", code));
        }
    }
}
