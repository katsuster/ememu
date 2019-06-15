package net.katsuster.ememu.riscv.core;

import net.katsuster.ememu.generic.*;

public class DecodeStageRVI extends Stage64 {
    /**
     * RISC-V I コア c の実行ステージを生成します。
     *
     * @param c 実行ステージの持ち主となる RISC-V I コア
     */
    public DecodeStageRVI(RV64 c) {
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
     * RISC-V アーキテクチャのビット数を返します。
     *
     * @return RV32 なら 32、RV64 なら 64
     */
    public int getRVBits() {
        return getCore().getRVBits();
    }

    /**
     * 32bit JALR 命令をデコードします。
     *
     * @param inst 32bit 命令
     * @return 命令の種類
     */
    public OpIndex decodeJalr(InstructionRV32 inst) {
        int funct3 = inst.getFunct3();

        switch (funct3) {
        case InstructionRV32.FUNC_JALR_JALR:
            return OpIndex.INS_RV32I_JALR;
        default:
            throw new IllegalArgumentException("Unknown JALR funct3 " +
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
            throw new IllegalArgumentException("Unknown BRANCH funct3 " +
                    String.format("%d.", funct3));
        }
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
        case InstructionRV32.FUNC_LOAD_LD:
            return OpIndex.INS_RV64I_LD;
        case InstructionRV32.FUNC_LOAD_LBU:
            return OpIndex.INS_RV32I_LBU;
        case InstructionRV32.FUNC_LOAD_LHU:
            return OpIndex.INS_RV32I_LHU;
        case InstructionRV32.FUNC_LOAD_LWU:
            return OpIndex.INS_RV64I_LWU;
        default:
            throw new IllegalArgumentException("Unknown LOAD funct3 " +
                    String.format("%d.", funct3));
        }
    }

    /**
     * 32bit OP-IMM 命令をデコードします。
     *
     * @param inst 32bit 命令
     * @return 命令の種類
     */
    public OpIndex decodeOpImm(InstructionRV32 inst) {
        int funct3 = inst.getFunct3();

        switch (funct3) {
        case InstructionRV32.FUNC_OP_IMM_ADDI:
            return OpIndex.INS_RV32I_ADDI;
        case InstructionRV32.FUNC_OP_IMM_SLTI:
            return OpIndex.INS_RV32I_SLTI;
        case InstructionRV32.FUNC_OP_IMM_SLTIU:
            return OpIndex.INS_RV32I_SLTIU;
        case InstructionRV32.FUNC_OP_IMM_XORI:
            return OpIndex.INS_RV32I_XORI;
        case InstructionRV32.FUNC_OP_IMM_ORI:
            return OpIndex.INS_RV32I_ORI;
        case InstructionRV32.FUNC_OP_IMM_ANDI:
            return OpIndex.INS_RV32I_ANDI;
        case InstructionRV32.FUNC_OP_IMM_SLI:
            if (getRVBits() == 64) {
                int imm6 = inst.getImm6I();

                if (imm6 == 0) {
                    //RV64I SLLI
                    return OpIndex.INS_RV64I_SLLI;
                }

                throw new IllegalArgumentException("Unknown op-imm SLI 64bit imm6 " +
                        String.format("0x%x.", imm6));
            } else if (getRVBits() == 32) {
                int imm7 = inst.getImm7I();

                if (imm7 == 0) {
                    //RV32I SLLI
                    return OpIndex.INS_RV32I_SLLI;
                }

                throw new IllegalArgumentException("Unknown op-imm SLI 32bit imm7 " +
                        String.format("0x%x.", imm7));
            }

            throw new IllegalArgumentException("Unknown op-imm SLI " +
                    String.format("%dbit.", getRVBits()));
        case InstructionRV32.FUNC_OP_IMM_SRI:
            if (getCore().getRVBits() == 64) {
                int imm6 = inst.getImm6I();

                if (imm6 == 0) {
                    //RV64I SRLI (imm6 = 0b000000)
                    return OpIndex.INS_RV64I_SRLI;
                } else if (imm6 == 16) {
                    //RV64I SRAI (imm6 = 0b010000)
                    return OpIndex.INS_RV64I_SRAI;
                }

                throw new IllegalArgumentException("Unknown op-imm SRI 64bit imm6 " +
                        String.format("0x%x.", imm6));
            } else if (getRVBits() == 32) {
                int imm7 = inst.getImm7I();

                if (imm7 == 0) {
                    //RV32I SRAI (imm7 = 0b0100000)
                    return OpIndex.INS_RV32I_SRLI;
                } else if (imm7 == 32) {
                    //RV32I SRAI (imm7 = 0b0100000)
                    return OpIndex.INS_RV32I_SRAI;
                }

                throw new IllegalArgumentException("Unknown op-imm SRI 32bit imm7 " +
                        String.format("0x%x.", imm7));
            }

            throw new IllegalArgumentException("Unknown op-imm SRI " +
                    String.format("%dbit.", getRVBits()));
        default:
            throw new IllegalArgumentException("Unknown op-imm funct3 " +
                    String.format("%d.", funct3));
        }
    }

    /**
     * 32bit OP 命令をデコードします。
     *
     * @param inst 32bit 命令
     * @return 命令の種類
     */
    public OpIndex decodeOp(InstructionRV32 inst) {
        int funct3 = inst.getFunct3();
        int imm7 = inst.getImm7I();
        String opname = "unknown";

        switch (funct3) {
        case InstructionRV32.FUNC_OP_ADDSUB:
            if (imm7 == 0) {
                //ADD (imm7 = 0b0000000)
                return OpIndex.INS_RV32I_ADD;
            } else if (imm7 == 32) {
                //SUB (imm7 = 0b0100000)
                return OpIndex.INS_RV32I_SUB;
            }

            opname = "ADD/SUB";
            break;
        case InstructionRV32.FUNC_OP_SLL:
            if (imm7 == 0) {
                //SLL
                return OpIndex.INS_RV32I_SLL;
            }

            opname = "SLL";
            break;
        case InstructionRV32.FUNC_OP_SLT:
            if (imm7 == 0) {
                //SLT
                return OpIndex.INS_RV32I_SLT;
            }

            opname = "SLT";
            break;
        case InstructionRV32.FUNC_OP_SLTU:
            if (imm7 == 0) {
                //SLTU
                return OpIndex.INS_RV32I_SLTU;
            }

            opname = "SLTU";
            break;
        case InstructionRV32.FUNC_OP_XOR:
            if (imm7 == 0) {
                //XOR
                return OpIndex.INS_RV32I_XOR;
            }

            opname = "XOR";
            break;
        case InstructionRV32.FUNC_OP_SR:
            if (imm7 == 0) {
                //SRL (imm7 = 0b0000000)
                return OpIndex.INS_RV32I_SRL;
            } else if (imm7 == 32) {
                //SRA (imm7 = 0b0100000)
                return OpIndex.INS_RV32I_SRA;
            }

            opname = "SR";
            break;
        case InstructionRV32.FUNC_OP_OR:
            if (imm7 == 0) {
                //OR
                return OpIndex.INS_RV32I_OR;
            }

            opname = "OR";
            break;
        case InstructionRV32.FUNC_OP_AND:
            if (imm7 == 0) {
                //AND
                return OpIndex.INS_RV32I_AND;
            }

            opname = "AND";
            break;
        default:
            throw new IllegalArgumentException("Unknown op funct3 " +
                    String.format("%d.", funct3));
        }

        throw new IllegalArgumentException("Unknown op-imm 32bit imm7 " +
                String.format("%s", opname) +
                String.format("0x%x.", imm7));
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
        case InstructionRV32.OPCODE_AUIPC:
            return OpIndex.INS_RV32I_AUIPC;
        case InstructionRV32.OPCODE_JALR:
            return decodeJalr(inst);
        case InstructionRV32.OPCODE_BRANCH:
            return decodeBranch(inst);
        case InstructionRV32.OPCODE_LOAD:
            return decodeLoad(inst);
        case InstructionRV32.OPCODE_OP_IMM:
            return decodeOpImm(inst);
        case InstructionRV32.OPCODE_OP:
            return decodeOp(inst);
        default:
            throw new IllegalArgumentException("Unknown opcode " +
                    String.format("%d.", code));
        }
    }
}
