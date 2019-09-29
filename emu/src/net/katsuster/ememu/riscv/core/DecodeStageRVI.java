package net.katsuster.ememu.riscv.core;

import net.katsuster.ememu.generic.*;

public class DecodeStageRVI extends Stage64 {
    /**
     * RVI 命令のデコードステージを生成します。
     *
     * @param c デコードステージの持ち主となる CPU コア
     */
    public DecodeStageRVI(RV64 c) {
        super(c);
    }

    /**
     * RVI 命令のデコードステージの持ち主となる CPU コアを取得します。
     *
     * @return デコードステージの持ち主となる CPU コア
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
            if (getRVBits() == 64) {
                return OpIndex.INS_RV64I_LD;
            } else {
                throw new IllegalArgumentException("Unknown LOAD, LD " +
                        String.format("funct3 %d. RV%d", funct3, getRVBits()));
            }
        case InstructionRV32.FUNC_LOAD_LBU:
            return OpIndex.INS_RV32I_LBU;
        case InstructionRV32.FUNC_LOAD_LHU:
            return OpIndex.INS_RV32I_LHU;
        case InstructionRV32.FUNC_LOAD_LWU:
            if (getRVBits() == 64) {
                return OpIndex.INS_RV64I_LWU;
            } else {
                throw new IllegalArgumentException("Unknown LOAD, LWU " +
                        String.format("funct3 %d. RV%d", funct3, getRVBits()));
            }
        default:
            throw new IllegalArgumentException("Unknown LOAD " +
                    String.format("funct3 %d.", funct3));
        }
    }

    /**
     * 32bit MISC-MEM 命令をデコードします。
     *
     * @param inst 32bit 命令
     * @return 命令の種類
     */
    public OpIndex decodeMiscMem(InstructionRV32 inst) {
        int funct3 = inst.getFunct3();

        switch (funct3) {
        case InstructionRV32.FUNC_MISC_MEM_FENCE:
            return OpIndex.INS_RV32I_FENCE;
        case InstructionRV32.FUNC_MISC_MEM_FENCE_I:
            return OpIndex.INS_RV32I_FENCE_I;
        default:
            throw new IllegalArgumentException("Unknown MISC-MEM " +
                    String.format("funct3 %d.", funct3));
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

                throw new IllegalArgumentException("Unknown OP-IMM SLI 64bit " +
                        String.format("imm6 0x%x.", imm6));
            } else if (getRVBits() == 32) {
                int imm7 = inst.getImm7I();

                if (imm7 == 0) {
                    //RV32I SLLI
                    return OpIndex.INS_RV32I_SLLI;
                }

                throw new IllegalArgumentException("Unknown OP-IMM SLI 32bit " +
                        String.format("imm7 0x%x.", imm7));
            }

            throw new IllegalArgumentException("Unknown OP-IMM SLI " +
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

                throw new IllegalArgumentException("Unknown OP-IMM SRI 64bit " +
                        String.format("imm6 0x%x.", imm6));
            } else if (getRVBits() == 32) {
                int imm7 = inst.getImm7I();

                if (imm7 == 0) {
                    //RV32I SRAI (imm7 = 0b0100000)
                    return OpIndex.INS_RV32I_SRLI;
                } else if (imm7 == 32) {
                    //RV32I SRAI (imm7 = 0b0100000)
                    return OpIndex.INS_RV32I_SRAI;
                }

                throw new IllegalArgumentException("Unknown OP-IMM SRI 32bit " +
                        String.format("imm7 0x%x.", imm7));
            }

            throw new IllegalArgumentException("Unknown OP-IMM SRI " +
                    String.format("%dbit.", getRVBits()));
        default:
            throw new IllegalArgumentException("Unknown OP-IMM " +
                    String.format("funct3 %d.", funct3));
        }
    }

    /**
     * OP-IMM-32 命令をデコードします。
     *
     * @param inst 32bit 命令
     * @return 命令の種類
     */
    public OpIndex decodeOpImm32(InstructionRV32 inst) {
        int funct3 = inst.getFunct3();

        switch (funct3) {
        case InstructionRV32.FUNC_OP_IMM_32_ADDIW:
            return OpIndex.INS_RV64I_ADDIW;
        case InstructionRV32.FUNC_OP_IMM_32_SLLIW:
            if (getRVBits() == 64) {
                int imm7 = inst.getImm7I();

                if (imm7 == 0) {
                    //RV64I SLLIW
                    return OpIndex.INS_RV64I_SLLI;
                }

                throw new IllegalArgumentException("Unknown OP-IMM-32 SLLIW 64bit " +
                        String.format("imm7 0x%x.", imm7));
            }

            throw new IllegalArgumentException("Unknown OP-IMM-32 SLLIW " +
                    String.format("%dbit.", getRVBits()));
        case InstructionRV32.FUNC_OP_IMM_32_SRLIW_SRAIW:
            if (getRVBits() == 64) {
                int imm7 = inst.getImm7I();

                if (imm7 == 0) {
                    //RV64I SRLIW
                    return OpIndex.INS_RV64I_SRLIW;
                } else if (imm7 == 32) {
                    //RV64I SRAIW (imm7 = 0b0100000)
                    return OpIndex.INS_RV64I_SRAIW;
                }

                throw new IllegalArgumentException("Unknown OP-IMM-32 SRLIW/SRAIW 64bit " +
                        String.format("imm7 0x%x.", imm7));
            }

            throw new IllegalArgumentException("Unknown OP-IMM-32 SRLIW/SRAIW " +
                    String.format("%dbit.", getRVBits()));
        default:
            throw new IllegalArgumentException("Unknown OP-IMM-32 " +
                    String.format("funct3 %d.", funct3));
        }
    }

    /**
     * 32bit STORE 命令をデコードします。
     *
     * @param inst 32bit 命令
     * @return 命令の種類
     */
    public OpIndex decodeStore(InstructionRV32 inst) {
        int funct3 = inst.getFunct3();

        switch (funct3) {
        case InstructionRV32.FUNC_STORE_SB:
            return OpIndex.INS_RV32I_SB;
        case InstructionRV32.FUNC_STORE_SH:
            return OpIndex.INS_RV32I_SH;
        case InstructionRV32.FUNC_STORE_SW:
            return OpIndex.INS_RV32I_SW;
        case InstructionRV32.FUNC_STORE_SD:
            if (getRVBits() == 64) {
                return OpIndex.INS_RV64I_SD;
            } else {
                throw new IllegalArgumentException("Unknown STORE, SD " +
                        String.format("funct3 %d. RV%d", funct3, getRVBits()));
            }
        default:
            throw new IllegalArgumentException("Unknown STORE " +
                    String.format("funct3 %d.", funct3));
        }
    }

    /**
     * 32bit AMO 命令をデコードします。
     *
     * @param inst 32bit 命令
     * @return 命令の種類
     */
    public OpIndex decodeAmo(InstructionRV32 inst) {
        int funct3 = inst.getFunct3();
        int funct5 = inst.getImm7I() >>> 2;

        if (funct3 == 2) {
            switch (funct5) {
            case InstructionRV32.FUNC5_AMO_LR_W:
                return OpIndex.INS_RV32A_LR_W;
            case InstructionRV32.FUNC5_AMO_SC_W:
                return OpIndex.INS_RV32A_SC_W;
            case InstructionRV32.FUNC5_AMO_AMOSWAP_W:
                return OpIndex.INS_RV32A_AMOSWAP_W;
            case InstructionRV32.FUNC5_AMO_AMOADD_W:
                return OpIndex.INS_RV32A_AMOADD_W;
            case InstructionRV32.FUNC5_AMO_AMOXOR_W:
                return OpIndex.INS_RV32A_AMOXOR_W;
            case InstructionRV32.FUNC5_AMO_AMOAND_W:
                return OpIndex.INS_RV32A_AMOAND_W;
            case InstructionRV32.FUNC5_AMO_AMOOR_W:
                return OpIndex.INS_RV32A_AMOOR_W;
            case InstructionRV32.FUNC5_AMO_AMOMIN_W:
                return OpIndex.INS_RV32A_AMOMIN_W;
            case InstructionRV32.FUNC5_AMO_AMOMAX_W:
                return OpIndex.INS_RV32A_AMOMAX_W;
            case InstructionRV32.FUNC5_AMO_AMOMINU_W:
                return OpIndex.INS_RV32A_AMOMINU_W;
            case InstructionRV32.FUNC5_AMO_AMOMAXU_W:
                return OpIndex.INS_RV32A_AMOMAXU_W;
            }
        }

        throw new IllegalArgumentException("Unknown AMO " +
                String.format("funct3 %d funct5 0x%x.", funct3, funct5));
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

        switch (imm7) {
        case 0:
            switch (funct3) {
            case InstructionRV32.FUNC_OP_ADD_SUB:
                return OpIndex.INS_RV32I_ADD;
            case InstructionRV32.FUNC_OP_SLL:
                return OpIndex.INS_RV32I_SLL;
            case InstructionRV32.FUNC_OP_SLT:
                return OpIndex.INS_RV32I_SLT;
            case InstructionRV32.FUNC_OP_SLTU:
                return OpIndex.INS_RV32I_SLTU;
            case InstructionRV32.FUNC_OP_XOR:
                return OpIndex.INS_RV32I_XOR;
            case InstructionRV32.FUNC_OP_SRL_SRA:
                return OpIndex.INS_RV32I_SRL;
            case InstructionRV32.FUNC_OP_OR:
                return OpIndex.INS_RV32I_OR;
            case InstructionRV32.FUNC_OP_AND:
                return OpIndex.INS_RV32I_AND;
            }
            break;
        case 32:
            switch (funct3) {
            case InstructionRV32.FUNC_OP_ADD_SUB:
                return OpIndex.INS_RV32I_SUB;
            case InstructionRV32.FUNC_OP_SRL_SRA:
                return OpIndex.INS_RV32I_SRA;
            }
            break;
        case 1:
            switch (funct3) {
            case InstructionRV32.FUNC_OP_MUL:
                return OpIndex.INS_RV32M_MUL;
            case InstructionRV32.FUNC_OP_MULH:
                return OpIndex.INS_RV32M_MULH;
            case InstructionRV32.FUNC_OP_MULHSU:
                return OpIndex.INS_RV32M_MULHSU;
            case InstructionRV32.FUNC_OP_MULHU:
                return OpIndex.INS_RV32M_MULHU;
            case InstructionRV32.FUNC_OP_DIV:
                return OpIndex.INS_RV32M_DIV;
            case InstructionRV32.FUNC_OP_DIVU:
                return OpIndex.INS_RV32M_DIVU;
            case InstructionRV32.FUNC_OP_REM:
                return OpIndex.INS_RV32M_REM;
            case InstructionRV32.FUNC_OP_REMU:
                return OpIndex.INS_RV32M_REMU;
            }
            break;
        }

        throw new IllegalArgumentException("Unknown OP " +
                String.format("funct3 %d imm7 0x%x.", funct3, imm7));
    }

    /**
     * 32bit OP-32 命令 (opcode = 0b0111011) をデコードします。
     *
     * @param inst 32bit 命令
     * @return 命令の種類
     */
    public OpIndex decodeOp32(InstructionRV32 inst) {
        int funct3 = inst.getFunct3();
        int imm7 = inst.getImm7I();

        switch (imm7) {
        case 1:
            switch (funct3) {
            case InstructionRV32.FUNC_OP_MULW:
                return OpIndex.INS_RV64M_MULW;
            case InstructionRV32.FUNC_OP_DIVW:
                return OpIndex.INS_RV64M_DIVW;
            case InstructionRV32.FUNC_OP_DIVUW:
                return OpIndex.INS_RV64M_DIVUW;
            case InstructionRV32.FUNC_OP_REMW:
                return OpIndex.INS_RV64M_REMW;
            case InstructionRV32.FUNC_OP_REMUW:
                return OpIndex.INS_RV64M_REMUW;
            }
            break;
        }

        throw new IllegalArgumentException("Unknown OP-32 " +
                String.format("funct3 %d imm7 0x%x.", funct3, imm7));
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
            throw new IllegalArgumentException("Unknown BRANCH " +
                    String.format("funct3 %d.", funct3));
        }
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
            throw new IllegalArgumentException("Unknown JALR " +
                    String.format("funct3 %d.", funct3));
        }
    }

    /**
     * 32bit SYSTEM 命令をデコードします。
     *
     * @param inst 32bit 命令
     * @return 命令の種類
     */
    public OpIndex decodeSystem(InstructionRV32 inst) {
        int funct3 = inst.getFunct3();

        switch (funct3) {
        case InstructionRV32.FUNC_SYSTEM_EX: {
            int rd = inst.getRd();
            int rs1 = inst.getRs1();
            int imm12 = inst.getImm12I();

            if (imm12 == 0 && rs1 == 0 && rd == 0) {
                //ECALL
                return OpIndex.INS_RV32I_ECALL;
            } else if (imm12 == 1 && rs1 == 0 && rd == 0) {
                //EBREAK
                return OpIndex.INS_RV32I_EBREAK;
            } else if (imm12 == 0x105 && rs1 == 0 && rd == 0) {
                //WFI, imm12 = 0b0001_0000_0101
                return OpIndex.INS_RV32_WFI;
            }

            throw new IllegalArgumentException("Unknown SYSTEM " +
                    String.format("imm12 0x%x, rs1 0x%x, rd 0x%x.",
                            imm12, rs1, rd));
        }
        case InstructionRV32.FUNC_SYSTEM_CSRRW:
            return OpIndex.INS_RV32I_CSRRW;
        case InstructionRV32.FUNC_SYSTEM_CSRRS:
            return OpIndex.INS_RV32I_CSRRS;
        case InstructionRV32.FUNC_SYSTEM_CSRRC:
            return OpIndex.INS_RV32I_CSRRC;
        case InstructionRV32.FUNC_SYSTEM_CSRRWI:
            return OpIndex.INS_RV32I_CSRRWI;
        case InstructionRV32.FUNC_SYSTEM_CSRRSI:
            return OpIndex.INS_RV32I_CSRRSI;
        case InstructionRV32.FUNC_SYSTEM_CSRRCI:
            return OpIndex.INS_RV32I_CSRRCI;
        default:
            throw new IllegalArgumentException("Unknown SYSTEM " +
                    String.format("funct3 %d.", funct3));
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
        case InstructionRV32.OPCODE_MISC_MEM:
            return decodeMiscMem(inst);
        case InstructionRV32.OPCODE_OP_IMM:
            return decodeOpImm(inst);
        case InstructionRV32.OPCODE_AUIPC:
            return OpIndex.INS_RV32I_AUIPC;
        case InstructionRV32.OPCODE_OP_IMM_32:
            return decodeOpImm32(inst);
        case InstructionRV32.OPCODE_STORE:
            return decodeStore(inst);
        case InstructionRV32.OPCODE_AMO:
            return decodeAmo(inst);
        case InstructionRV32.OPCODE_OP:
            return decodeOp(inst);
        case InstructionRV32.OPCODE_LUI:
            return OpIndex.INS_RV32I_LUI;
        case InstructionRV32.OPCODE_OP_32:
            return decodeOp32(inst);
        case InstructionRV32.OPCODE_BRANCH:
            return decodeBranch(inst);
        case InstructionRV32.OPCODE_JALR:
            return decodeJalr(inst);
        case InstructionRV32.OPCODE_JAL:
            return OpIndex.INS_RV32I_JAL;
        case InstructionRV32.OPCODE_SYSTEM:
            return decodeSystem(inst);
        default:
            throw new IllegalArgumentException("Unknown opcode " +
                    String.format("%d.", code));
        }
    }
}
