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

        if (rd != 0) {
            //C.ADDI
            return OpIndex.INS_RVC_ADDI;
        }

        throw new IllegalArgumentException("Unknown ADDI " +
                String.format("rd %d.", rd));
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
     * SLLI 命令をデコードします。
     *
     * @param inst 16bit 命令
     * @return 命令の種類
     */
    public OpIndex decodeSlli(InstructionRV16 inst) {
        int rd = inst.getRd();
        int imm = inst.getImm6CI();

        if (rd != 0 && imm != 1) {
            //C.SLLI
            return OpIndex.INS_RVC_SLLI;
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
        case InstructionRV16.OPCODE_ADDI:
            return decodeAddi(inst);
        case InstructionRV16.OPCODE_LI:
            return decodeLi(inst);
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
