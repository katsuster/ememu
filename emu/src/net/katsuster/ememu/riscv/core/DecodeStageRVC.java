package net.katsuster.ememu.riscv.core;

import net.katsuster.ememu.generic.*;

public class DecodeStageRVC extends Stage64 {
    /**
     * RVC 命令のデコードステージを生成します。
     *
     * @param c デコードステージの持ち主となる CPU コア
     */
    public DecodeStageRVC(RV64 c) {
        super(c);
    }

    /**
     * RVC 命令のデコードステージの持ち主となる CPU コアを取得します。
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
     * ADDI 命令をデコードします。
     *
     * @param inst 16bit 命令
     * @return 命令の種類
     */
    public OpIndex decodeAddi(InstructionRV16 inst) {
        int rd = inst.getRd();
        int imm6 = inst.getImm6CI();

        if (rd != 0) {
            //C.ADDI
            return OpIndex.INS_RVC_ADDI;
        } else if (rd == 0 && imm6 == 0) {
            //C.NOP
            return OpIndex.INS_RVC_NOP;
        }

        throw new IllegalArgumentException("Unknown ADDI " +
                String.format("rd %d.", rd));
    }

    /**
     * JAL, ADDIW 命令をデコードします。
     *
     * @param inst 16bit 命令
     * @return 命令の種類
     */
    public OpIndex decodeJalAddiw(InstructionRV16 inst) {
        int rd = inst.getRd();

        if (rd != 0 && getRVBits() != 32) {
            //C.ADDIW
            return OpIndex.INS_RVC_ADDIW;
        } else if (getRVBits() == 32) {
            //C.JAL
            return OpIndex.INS_RVC_JAL;
        }

        throw new IllegalArgumentException("Unknown JAL, ADDIW " +
                String.format("rd %d, ", rd) +
                String.format("%dbit.", getRVBits()));
    }

    /**
     * LI 命令をデコードします。
     *
     * @param inst 16bit 命令
     * @return 命令の種類
     */
    public OpIndex decodeLi(InstructionRV16 inst) {
        int rd = inst.getRd();

        if (rd != 0) {
            //C.LI
            return OpIndex.INS_RVC_LI;
        }

        throw new IllegalArgumentException("Unknown LI " +
                String.format("rd %d.", rd));
    }

    /**
     * LUI, ADDI16SP 命令をデコードします。
     *
     * @param inst 16bit 命令
     * @return 命令の種類
     */
    public OpIndex decodeLuiAddi16sp(InstructionRV16 inst) {
        int rd = inst.getRd();

        if (rd == 2) {
            //C.ADDI16SP
            return OpIndex.INS_RVC_ADDI16SP;
        } else if (rd != 0 && rd != 2) {
            //C.LUI
            return OpIndex.INS_RVC_LUI;
        }

        throw new IllegalArgumentException("Unknown LUI, ADDI16SP " +
                String.format("rd %d.", rd));
    }

    /**
     * MISC-ALU 命令をデコードします。
     *
     * @param inst 16bit 命令
     * @return 命令の種類
     */
    public OpIndex decodeMiscALU(InstructionRV16 inst) {
        int funct6 = inst.getFunct6();
        int imm6 = inst.getImm6CI();
        int funct;

        switch (funct6 & 0x7) {
        case 0:
        case 4:
            //C.SRLI, C.SRLI64
            if (imm6 != 0) {
                return OpIndex.INS_RVC_SRLI;
            } else if (getRVBits() == 128) {
                return OpIndex.INS_RVC_SRLI64;
            }
        case 1:
        case 5:
            //C.SRAI, C.SRAI64
            if (imm6 != 0) {
                return OpIndex.INS_RVC_SRAI;
            } else if (getRVBits() == 128) {
                return OpIndex.INS_RVC_SRAI64;
            }
        case 2:
        case 6:
            //C.ANDI
            return OpIndex.INS_RVC_ANDI;
        case 3:
            //C.SUB, C.XOR, C.OR, C.AND
            funct = inst.getFunct();

            switch (funct) {
            case 0:
                return OpIndex.INS_RVC_SUB;
            case 1:
                return OpIndex.INS_RVC_XOR;
            case 2:
                return OpIndex.INS_RVC_OR;
            case 3:
                return OpIndex.INS_RVC_AND;
            default:
                throw new IllegalArgumentException("Unknown MISC-ALU SUB/XOR/OR/AND " +
                        String.format("funct 0x%x.", funct));
            }
        case 7:
            //C.SUBW, C.ADDW
            funct = inst.getFunct();

            switch (funct) {
            case 0:
                return OpIndex.INS_RVC_SUBW;
            case 1:
                return OpIndex.INS_RVC_ADDW;
            default:
                throw new IllegalArgumentException("Unknown MISC-ALU SUBW/ADDW " +
                        String.format("funct 0x%x.", funct));
            }
        }

        throw new IllegalArgumentException("Unknown MISC-ALU " +
                String.format("funct6 0x%x.", funct6));
    }

    /**
     * SLLI 命令をデコードします。
     *
     * @param inst 16bit 命令
     * @return 命令の種類
     */
    public OpIndex decodeSlli(InstructionRV16 inst) {
        int rd = inst.getRd();
        int imm6 = inst.getImm6CI();

        if (rd != 0 && imm6 != 0) {
            if (getRVBits() != 32) {
                //C.SLLI
                return OpIndex.INS_RVC_SLLI;
            } else if (getRVBits() == 32 && (imm6 & 32) == 0) {
                //C.SLLI
                //  RV32 imm[5] = 1: Non standard extension
                return OpIndex.INS_RVC_SLLI;
            }
        }

        throw new IllegalArgumentException("Unknown SLLI " +
                String.format("rd %d.", rd));
    }

    /**
     * J[AL]R, MV, ADD 命令をデコードします。
     *
     * @param inst 16bit 命令
     * @return 命令の種類
     */
    public OpIndex decodeJrMvAdd(InstructionRV16 inst) {
        int funct4 = inst.getFunct4();
        int rd = inst.getRd();
        int rs2 = inst.getRs2();
        int imm = inst.getImm6CI();

        if (funct4 == 0x8) {
            //c.jr, c.mv (funct4 = 0b1000)
            if (rd != 0 && rs2 == 0) {
                return OpIndex.INS_RVC_JR;
            } else if (rd != 0 && rs2 != 0) {
                return OpIndex.INS_RVC_MV;
            }
        } else if (funct4 == 0x9) {
            //c.ebreak, c.jalr, c.add (funct4 = 0b1001)
            if (rd == 0 && rs2 == 0) {
                return OpIndex.INS_RVC_EBREAK;
            } else if (rd != 0 && rs2 == 0) {
                return OpIndex.INS_RVC_JALR;
            } else if (rd != 0 && rs2 != 0) {
                return OpIndex.INS_RVC_ADD;
            }
        }

        throw new IllegalArgumentException("Unknown J[AL]R, MV, ADD " +
                String.format("funct4 %x, ", funct4) +
                String.format("rd %d, ", rd) +
                String.format("rs2 %d.", rs2));
    }

    /**
     * FSWSP, SDSP命令をデコードします。
     *
     * @param inst 16bit 命令
     * @return 命令の種類
     */
    public OpIndex decodeFswspSdsp(InstructionRV16 inst) {
        if (getRVBits() == 32) {
            return OpIndex.INS_RVC_FSWSP;
        } else if (getRVBits() == 64 || getRVBits() == 128) {
            return OpIndex.INS_RVC_SDSP;
        }

        throw new IllegalArgumentException("Unknown FSWSP, SDSP " +
                String.format("%dbit.", getRVBits()));
    }

    /**
     * 16bit 命令をデコードします。
     *
     * @param inst 32bit 命令
     * @return 命令の種類
     */
    public OpIndex decode(InstructionRV16 inst) {
        int code = inst.getOpcode();

        switch (code) {
        case InstructionRV16.OPCODE_LW:
            return OpIndex.INS_RVC_LW;
        case InstructionRV16.OPCODE_SW:
            return OpIndex.INS_RVC_SW;
        case InstructionRV16.OPCODE_ADDI:
            return decodeAddi(inst);
        case InstructionRV16.OPCODE_JAL_ADDIW:
            return decodeJalAddiw(inst);
        case InstructionRV16.OPCODE_LI:
            return decodeLi(inst);
        case InstructionRV16.OPCODE_LUI_ADDI16SP:
            return decodeLuiAddi16sp(inst);
        case InstructionRV16.OPCODE_MISC_ALU:
            return decodeMiscALU(inst);
        case InstructionRV16.OPCODE_J:
            return OpIndex.INS_RVC_J;
        case InstructionRV16.OPCODE_BNEZ:
            return OpIndex.INS_RVC_BNEZ;
        case InstructionRV16.OPCODE_SLLI:
            return decodeSlli(inst);
        case InstructionRV16.OPCODE_JR_MV_ADD:
            return decodeJrMvAdd(inst);
        case InstructionRV16.OPCODE_FSWSP_SDSP:
            return decodeFswspSdsp(inst);
        default:
            throw new IllegalArgumentException("Unknown opcode " +
                    String.format("%d.", code));
        }
    }
}
