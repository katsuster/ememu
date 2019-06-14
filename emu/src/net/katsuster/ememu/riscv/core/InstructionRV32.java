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
     * RV32 命令の opcode フィールド（ビット [6:2]）を取得します。
     *
     * @return opcode フィールド
     */
    public int getOpcode() {
        return getField(2, 5);
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
