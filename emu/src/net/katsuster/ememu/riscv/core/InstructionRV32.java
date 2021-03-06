package net.katsuster.ememu.riscv.core;

import net.katsuster.ememu.generic.core.Inst32;

public class InstructionRV32 extends Inst32 {
    /**
     * 指定されたバイナリ値の 32bit RISC-V 命令を作成します。
     *
     * @param inst 32bit RISC-V 命令のバイナリ値
     */
    public InstructionRV32(int inst) {
        super(inst, 4);
    }

    //opcode[6:2] (opcode[1:0] = 0b11)
    public static final int OPCODE_LOAD = 0;
    public static final int OPCODE_LOAD_FP = 1;
    public static final int OPCODE_CUSTOM_0 = 2;
    public static final int OPCODE_MISC_MEM = 3;
    public static final int OPCODE_OP_IMM = 4;
    public static final int OPCODE_AUIPC = 5;
    public static final int OPCODE_OP_IMM_32 = 6;
    public static final int OPCODE_48B_1 = 7;
    public static final int OPCODE_STORE = 8;
    public static final int OPCODE_STORE_FP = 9;
    public static final int OPCODE_CUSTOM_1 = 10;
    public static final int OPCODE_AMO = 11;
    public static final int OPCODE_OP = 12;
    public static final int OPCODE_LUI = 13;
    public static final int OPCODE_OP_32 = 14;
    public static final int OPCODE_64B = 15;
    public static final int OPCODE_MADD = 16;
    public static final int OPCODE_MSUB = 17;
    public static final int OPCODE_NMSUB = 18;
    public static final int OPCODE_NMADD = 19;
    public static final int OPCODE_OP_FP = 20;
    public static final int OPCODE_RESERVED_0 = 21;
    public static final int OPCODE_CUSTOM_2 = 22;
    public static final int OPCODE_48B_2 = 23;
    public static final int OPCODE_BRANCH = 24;
    public static final int OPCODE_JALR = 25;
    public static final int OPCODE_RESERVED_1 = 26;
    public static final int OPCODE_JAL = 27;
    public static final int OPCODE_SYSTEM = 28;
    public static final int OPCODE_RESERVED_2 = 29;
    public static final int OPCODE_CUSTOM_3 = 30;
    public static final int OPCODE_80B = 31;

    /**
     * 32bit 命令の opcode フィールド（ビット [6:2]）を取得します。
     *
     * @return opcode フィールド
     */
    public int getOpcode() {
        return getField(2, 5);
    }

    //opcode: LOAD (0b00000)
    public static final int FUNC_LOAD_LB = 0;
    public static final int FUNC_LOAD_LH = 1;
    public static final int FUNC_LOAD_LW = 2;
    public static final int FUNC_LOAD_LD = 3;
    public static final int FUNC_LOAD_LBU = 4;
    public static final int FUNC_LOAD_LHU = 5;
    public static final int FUNC_LOAD_LWU = 6;

    //opcode: MISC_MEM (0b00011)
    public static final int FUNC_MISC_MEM_FENCE = 0;
    public static final int FUNC_MISC_MEM_FENCE_I = 1;

    //opcode: OP-IMM (0b00100)
    public static final int FUNC_OP_IMM_ADDI = 0;
    public static final int FUNC_OP_IMM_SLTI = 2;
    public static final int FUNC_OP_IMM_SLTIU = 3;
    public static final int FUNC_OP_IMM_XORI = 4;
    public static final int FUNC_OP_IMM_ORI = 6;
    public static final int FUNC_OP_IMM_ANDI = 7;
    public static final int FUNC_OP_IMM_SLLI = 1;
    public static final int FUNC_OP_IMM_SRLI_SRAI = 5;

    //opcode: OP-IMM-32 (0b00110)
    public static final int FUNC_OP_IMM_32_ADDIW = 0;
    public static final int FUNC_OP_IMM_32_SLLIW = 1;
    public static final int FUNC_OP_IMM_32_SRLIW_SRAIW = 5;

    //opcode: STORE (0b01000)
    public static final int FUNC_STORE_SB = 0;
    public static final int FUNC_STORE_SH = 1;
    public static final int FUNC_STORE_SW = 2;
    public static final int FUNC_STORE_SD = 3;

    //opcode: AMO (0b01011)
    public static final int FUNC_AMO_W = 2;

    //opcode: OP (0b01100), funct7 = 0, 32
    public static final int FUNC_OP_ADD_SUB = 0;
    public static final int FUNC_OP_SLL = 1;
    public static final int FUNC_OP_SLT = 2;
    public static final int FUNC_OP_SLTU = 3;
    public static final int FUNC_OP_XOR = 4;
    public static final int FUNC_OP_SRL_SRA = 5;
    public static final int FUNC_OP_OR = 6;
    public static final int FUNC_OP_AND = 7;

    //opcode: OP (0b01100), funct7 = 1
    public static final int FUNC_OP_MUL = 0;
    public static final int FUNC_OP_MULH = 1;
    public static final int FUNC_OP_MULHSU = 2;
    public static final int FUNC_OP_MULHU = 3;
    public static final int FUNC_OP_DIV = 4;
    public static final int FUNC_OP_DIVU = 5;
    public static final int FUNC_OP_REM = 6;
    public static final int FUNC_OP_REMU = 7;

    //opcode: OP-32 (0b01110), funct7 = 0, 32
    public static final int FUNC_OP_32_ADDW_SUBW = 0;
    public static final int FUNC_OP_32_SLLW = 1;
    public static final int FUNC_OP_32_SRLW_SRAW = 5;

    //opcode: OP-32 (0b01110), funct7 = 1
    public static final int FUNC_OP_32_MULW = 0;
    public static final int FUNC_OP_32_DIVW = 4;
    public static final int FUNC_OP_32_DIVUW = 5;
    public static final int FUNC_OP_32_REMW = 6;
    public static final int FUNC_OP_32_REMUW = 7;

    //opcode: BRANCH (0b11000)
    public static final int FUNC_BRANCH_BEQ = 0;
    public static final int FUNC_BRANCH_BNE = 1;
    public static final int FUNC_BRANCH_BLT = 4;
    public static final int FUNC_BRANCH_BGE = 5;
    public static final int FUNC_BRANCH_BLTU = 6;
    public static final int FUNC_BRANCH_BGEU = 7;

    //opcode: JALR (0b11011)
    public static final int FUNC_JALR_JALR = 0;

    //opcode: SYSTEM (0b11100)
    public static final int FUNC_SYSTEM_EX = 0;
    public static final int FUNC_SYSTEM_CSRRW = 1;
    public static final int FUNC_SYSTEM_CSRRS = 2;
    public static final int FUNC_SYSTEM_CSRRC = 3;
    public static final int FUNC_SYSTEM_CSRRWI = 5;
    public static final int FUNC_SYSTEM_CSRRSI = 6;
    public static final int FUNC_SYSTEM_CSRRCI = 7;

    //R-type, funct3 = 0b010
    public static final int FUNC5_AMO_LR_W = 2;
    public static final int FUNC5_AMO_SC_W = 3;
    public static final int FUNC5_AMO_AMOSWAP_W = 1;
    public static final int FUNC5_AMO_AMOADD_W = 0;
    public static final int FUNC5_AMO_AMOXOR_W = 4;
    public static final int FUNC5_AMO_AMOAND_W = 12;
    public static final int FUNC5_AMO_AMOOR_W = 8;
    public static final int FUNC5_AMO_AMOMIN_W = 16;
    public static final int FUNC5_AMO_AMOMAX_W = 20;
    public static final int FUNC5_AMO_AMOMINU_W = 24;
    public static final int FUNC5_AMO_AMOMAXU_W = 28;

    /**
     * 32bit 命令の funct3 フィールド（ビット [14:12]）を取得します。
     *
     * @return funct3 フィールド
     */
    public int getFunct3() {
        return getField(12, 3);
    }

    /**
     * 32bit 命令 R-type の funct7 フィールド（ビット [31:25]）を取得します。
     *
     * RV32I の ADD, SLLI 命令、
     * RV64I の SLLIW, SRLIW 命令などに使われます。
     *
     * @return funct7 フィールド
     */
    public int getFunct7R() {
        return getField(25, 7);
    }

    /**
     * 32bit 命令の rd フィールド（ビット [11:7]）を取得します。
     *
     * @return rd フィールド
     */
    public int getRd() {
        return getField(7, 5);
    }

    /**
     * 32bit 命令の rs1 フィールド（ビット [19:15]）を取得します。
     *
     * @return rs1 フィールド
     */
    public int getRs1() {
        return getField(15, 5);
    }

    /**
     * 32bit 命令の rs2 フィールド（ビット [24:20]）を取得します。
     *
     * @return rs2 フィールド
     */
    public int getRs2() {
        return getField(20, 5);
    }

    /**
     * RV64 命令 I-type の imm フィールドの上位 6ビットを取得します。
     *
     * RV64I の SLLI, SRLI, SRAI 命令などに使われます。
     *
     * imm[ 5: 0]: 31:26
     *
     * @return imm フィールドの上位 6ビット
     */
    public int getImm6I() {
        return getField(26, 6);
    }

    /**
     * RV32 命令 I-type の imm フィールドの上位 7ビットを取得します。
     *
     * RV32I の SLLI, SLLIW 命令などに使われます。
     *
     * imm[ 6: 0]: 31:25
     *
     * @return imm フィールドの上位 7ビット
     */
    public int getImm7I() {
        return getField(25, 7);
    }

    /**
     * 32bit 命令 I-type の imm フィールドを取得します。
     *
     * imm[11: 0]: 31:20
     *
     * @return imm フィールド
     */
    public int getImm12I() {
        return getField(20, 12);
    }

    /**
     * 32bit 命令 S-type の imm フィールドを取得します。
     *
     * imm[11: 5]: 31:25
     * imm[ 4: 0]: 11: 7
     *
     * @return imm フィールド
     */
    public int getImm12S() {
        return (getField(25, 7) << 5) |
                (getField(7, 5) << 0);
    }

    /**
     * 32bit 命令 B-type の imm フィールドを取得します。
     *
     * imm[   12]:    31
     * imm[   11]:     7
     * imm[10: 5]: 30:25
     * imm[ 4: 1]: 11: 8
     *
     * @return imm フィールド
     */
    public int getImm13B() {
        return (getField(31, 1) << 12) |
                (getField(7, 1) << 11) |
                (getField(25, 6) << 5) |
                (getField(8, 4) << 1);
    }

    /**
     * 32bit 命令 J-type の imm フィールドを取得します。
     *
     *   imm[   20]:    31
     *   imm[19:12]: 19:12
     *   imm[   11]:    20
     *   imm[10: 1]: 30:21
     *
     * @return imm フィールド
     */
    public int getImm20J() {
        return (getField(31, 1) << 20) |
                (getField(12, 8) << 12) |
                (getField(20, 1) << 11) |
                (getField(21, 10) << 1);
    }

    /**
     * 32bit 命令 U-type の imm フィールド（ビット [31:12]）を取得します。
     *
     * @return imm フィールド
     */
    public int getImm20U() {
        return getField(12, 20);
    }

    /**
     * 命令の 16進数表記を取得します。
     *
     * @return 命令の 16進数表記
     */
    @Override
    public String toHex() {
        return String.format("%08x", getInst());
    }
}
