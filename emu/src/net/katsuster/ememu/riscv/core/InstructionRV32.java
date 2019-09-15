package net.katsuster.ememu.riscv.core;

import net.katsuster.ememu.generic.*;

public class InstructionRV32 extends Inst32 {
    /**
     * 指定されたバイナリ値の 32bit RISC-V 命令を作成します。
     *
     * @param inst 32bit RISC-V 命令のバイナリ値
     */
    public InstructionRV32(int inst) {
        super(inst, 4);
    }

    //opcode[6:2] (opcode[1:0] = 11)
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

    //funct3
    public static final int FUNC_JALR_JALR = 0;

    public static final int FUNC_BRANCH_BEQ = 0;
    public static final int FUNC_BRANCH_BNE = 1;
    public static final int FUNC_BRANCH_BLT = 4;
    public static final int FUNC_BRANCH_BGE = 5;
    public static final int FUNC_BRANCH_BLTU = 6;
    public static final int FUNC_BRANCH_BGEU = 7;

    public static final int FUNC_LOAD_LB = 0;
    public static final int FUNC_LOAD_LH = 1;
    public static final int FUNC_LOAD_LW = 2;
    public static final int FUNC_LOAD_LD = 3;
    public static final int FUNC_LOAD_LBU = 4;
    public static final int FUNC_LOAD_LHU = 5;
    public static final int FUNC_LOAD_LWU = 6;

    public static final int FUNC_STORE_SB = 0;
    public static final int FUNC_STORE_SH = 1;
    public static final int FUNC_STORE_SW = 2;
    public static final int FUNC_STORE_SD = 3;

    public static final int FUNC_OP_IMM_ADDI = 0;
    public static final int FUNC_OP_IMM_SLTI = 2;
    public static final int FUNC_OP_IMM_SLTIU = 3;
    public static final int FUNC_OP_IMM_XORI = 4;
    public static final int FUNC_OP_IMM_ORI = 6;
    public static final int FUNC_OP_IMM_ANDI = 7;
    public static final int FUNC_OP_IMM_SLI = 1;
    public static final int FUNC_OP_IMM_SRI = 5;

    public static final int FUNC_OP_IMM_32_ADDIW = 0;
    public static final int FUNC_OP_IMM_32_SLLIW = 1;
    public static final int FUNC_OP_IMM_32_SRLIW_SRAIW = 5;

    public static final int FUNC_OP_ADDSUB = 0;
    public static final int FUNC_OP_SLL = 1;
    public static final int FUNC_OP_SLT = 2;
    public static final int FUNC_OP_SLTU = 3;
    public static final int FUNC_OP_XOR = 4;
    public static final int FUNC_OP_SR = 5;
    public static final int FUNC_OP_OR = 6;
    public static final int FUNC_OP_AND = 7;

    public static final int FUNC_MISC_MEM_FENCE = 0;
    public static final int FUNC_MISC_MEM_FENCE_I = 1;

    public static final int FUNC_SYSTEM_EX = 0;
    public static final int FUNC_SYSTEM_CSRRW = 1;
    public static final int FUNC_SYSTEM_CSRRS = 2;
    public static final int FUNC_SYSTEM_CSRRC = 3;
    public static final int FUNC_SYSTEM_CSRRWI = 5;
    public static final int FUNC_SYSTEM_CSRRSI = 6;
    public static final int FUNC_SYSTEM_CSRRCI = 7;

    /**
     * 32bit 命令の funct3 フィールド（ビット [14:12]）を取得します。
     *
     * @return funct3 フィールド
     */
    public int getFunct3() {
        return getField(12, 3);
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
     * 32bit 命令 B-type の offset フィールドを取得します。
     *
     * offset[   12]:    31
     * offset[   11]:     7
     * offset[10: 5]: 30:25
     * offset[ 4: 1]: 11: 8
     *
     * @return offset フィールド
     */
    public int getOffset13B() {
        int off12 = getField(31, 1);
        int off11 = getField(7, 1);
        int off5 = getField(25, 6);
        int off1 = getField(8, 4);

        return (off12 << 12) | (off11 << 11) | (off5 << 5) | (off1 << 1);
    }

    /**
     * 32bit 命令 S-type の offset フィールドを取得します。
     *
     * offset[11: 5]: 31:25
     * offset[ 4: 0]: 11: 7
     *
     * @return offset フィールド
     */
    public int getOffset12S() {
        int off5 = getField(31, 1);
        int off0 = getField(7, 1);

        return (off5 << 5) | off0;
    }

    /**
     * 32bit 命令 I-type の imm フィールド（ビット [31:20]）を取得します。
     *
     * @return imm[11:0] フィールド
     */
    public int getImm12I() {
        return getField(20, 12);
    }

    /**
     * 32bit 命令 I-type の imm フィールドの上位 7ビット（ビット [31:25]）を取得します。
     *
     * RV32I の ADD, SLLI 命令、
     * RV64I の SLLIW, SRLIW 命令などに使われます。
     *
     * @return imm[11:0] フィールドの上位 7ビット
     */
    public int getImm7I() {
        return getField(25, 7);
    }

    /**
     * RV64 命令 I-type の imm フィールドの上位 6ビット（ビット [31:26]）を取得します。
     *
     * RV64I の ADD, SLLI 命令などに使われます。
     *
     * @return imm[11:0] フィールドの上位 6ビット
     */
    public int getImm6I() {
        return getField(26, 6);
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
