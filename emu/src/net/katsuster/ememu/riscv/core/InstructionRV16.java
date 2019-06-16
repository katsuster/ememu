package net.katsuster.ememu.riscv.core;

import net.katsuster.ememu.generic.*;

public class InstructionRV16 extends Inst32 {
    /**
     * 指定されたバイナリ値の 16bit RISC-V 命令を作成します。
     *
     * @param inst 16bit RISC-V 命令のバイナリ値
     */
    public InstructionRV16(int inst) {
        super(inst & 0x0000ffff, 2);
    }

    public static final int OPCODE_ADDI4SPN = 0;
    public static final int OPCODE_FLD_LQ = 1;
    public static final int OPCODE_LW = 2;
    public static final int OPCODE_FLW_LD = 3;
    public static final int OPCODE_RESERVED0 = 4;
    public static final int OPCODE_FSD_SQ = 5;
    public static final int OPCODE_SW = 6;
    public static final int OPCODE_FSW_SD = 7;
    public static final int OPCODE_ADDI = 8;
    public static final int OPCODE_JAL_ADDIW = 9;
    public static final int OPCODE_LI = 10;
    public static final int OPCODE_LUI_ADDI16SP = 11;
    public static final int OPCODE_MISC_ALU = 12;
    public static final int OPCODE_J = 13;
    public static final int OPCODE_BEQZ = 14;
    public static final int OPCODE_BNEZ = 15;
    public static final int OPCODE_SLLI = 16;
    public static final int OPCODE_FLDSP_LQ = 17;
    public static final int OPCODE_LWSP = 18;
    public static final int OPCODE_FLWSP_LDSP = 19;
    public static final int OPCODE_JR_MV_ADD = 20;
    public static final int OPCODE_FSDSP_SQ = 21;
    public static final int OPCODE_SWSP = 22;
    public static final int OPCODE_FSWSP_SDSP = 23;

    /**
     * 16bit 命令の opcode フィールド（ビット [1:0], [15:13]）を取得します。
     *
     * @return opcode フィールド
     */
    public int getOpcode() {
        return (getField(0, 2) << 3) | getField(13, 3);
    }

    /**
     * 16bit 命令の rd フィールド（ビット [11:7]）を取得します。
     *
     * @return rd フィールド
     */
    public int getRd() {
        return getField(7, 5);
    }

    /**
     * 16bit 命令 imm フィールド（6ビット）を取得します。
     *
     *   [12]: imm[5]
     *   [6:2]: imm[4:0]
     *
     * @return imm[5 | 4:0] フィールド
     */
    public int getImm6() {
        return (getField(12, 1) << 5) | getField(2, 5);
    }

    /**
     * 命令の 16進数表記を取得します。
     *
     * @return 命令の 16進数表記
     */
    @Override
    public String toHex() {
        return String.format("%04x", getInst());
    }
}
