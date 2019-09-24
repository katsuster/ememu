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
        return (getField(0, 2) << 3) |
                (getField(13, 3) << 0);
    }

    /**
     * 16bit 命令の funct フィールド（ビット [6:5]）を取得します。
     *
     * @return funct フィールド
     */
    public int getFunct() {
        return getField(5, 2);
    }

    /**
     * 16bit 命令の funct4 フィールド（ビット [15:12]）を取得します。
     *
     * @return funct4 フィールド
     */
    public int getFunct4() {
        return getField(12, 4);
    }

    /**
     * 16bit 命令の funct6 フィールド（ビット [15:10]）を取得します。
     *
     * @return funct6 フィールド
     */
    public int getFunct6() {
        return getField(10, 6);
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
     * 16bit 命令の rs2 フィールド（ビット [6:2]）を取得します。
     *
     * @return rs2 フィールド
     */
    public int getRs2() {
        return getField(2, 5);
    }

    /**
     * 16bit 命令の rs1' フィールド（ビット [9:7]）を取得します。
     *
     * @return rs1' フィールド
     */
    public int getRs1dash() {
        return getField(7, 3);
    }

    /**
     * 16bit 命令の rs2' フィールド（ビット [4:2]）を取得します。
     *
     * @return rs2' フィールド
     */
    public int getRs2dash() {
        return getField(2, 3);
    }

    /**
     * 16bit 命令 imm フィールドを取得します。
     * CI (Immediate) Format が使います。
     *
     *   imm[  5]:   12
     *   imm[4:0]: 6: 2
     *
     * @return imm フィールド
     */
    public int getImm6CI() {
        return (getField(12, 1) << 5) |
                (getField(2, 5) << 0);
    }

    /**
     * 16bit 命令 imm フィールドを取得します。
     * LW, FLW, SW, FSW が使います。
     *
     *   imm[  6]:     5
     *   imm[5:3]: 12:10
     *   imm[  2]:     6
     *
     * @return imm フィールド
     */
    public int getImm7LWSW() {
        return (getField(5, 1) << 6) |
                (getField(10, 3) << 3) |
                (getField(6, 1) << 2);
    }

    /**
     * 16bit 命令 imm フィールドを取得します。
     * SWSP, FSWSP が使います。
     *
     *   imm[7:6]:  8: 7
     *   imm[5:2]: 12: 9
     *
     * @return imm フィールド
     */
    public int getImm8SWSP() {
        return (getField(7, 2) << 6) |
                (getField(9, 4) << 2);
    }

    /**
     * 16bit 命令 imm フィールドを取得します。
     * SDSP, FSDSP が使います。
     *
     *   imm[8:6]:  9: 7
     *   imm[5:3]: 12:10
     *
     * @return imm フィールド
     */
    public int getImm9SDSP() {
        return (getField(7, 3) << 6) |
                (getField(10, 3) << 3);
    }

    /**
     * 16bit 命令 imm フィールドを取得します。
     * ADDI16SP が使います。
     *
     *   imm[  9]:   12
     *   imm[8:7]: 4: 3
     *   imm[  6]:    5
     *   imm[  5]:    2
     *   imm[  4]:    6
     *
     * @return imm フィールド
     */
    public int getImm10ADDI16SP() {
        return (getField(12, 1) << 9) |
                (getField(3, 2) << 7) |
                (getField(5, 1) << 6) |
                (getField(2, 1) << 5) |
                (getField(6, 1) << 4);
    }


    /**
     * 16bit 命令 imm フィールドを取得します。
     * SQSP が使います。
     *
     *   imm[9:6]: 10: 7
     *   imm[5:4]: 12:11
     *
     * @return imm フィールド
     */
    public int getImm10SQSP() {
        return (getField(7, 4) << 6) |
                (getField(11, 2) << 4);
    }

    /**
     * 16bit 命令の offset フィールドを取得します。
     * BEQZ, BNEZ が使います。
     *
     * offset[    8]:    12
     * offset[ 7: 6]:  6: 5
     * offset[    5]:     2
     * offset[ 4: 3]: 11:10
     * offset[ 2: 1]:  4: 3
     *
     * @return offset フィールド
     */
    public int getOffset9B() {
        return (getField(12, 1) << 8) |
                (getField(5, 2) << 6) |
                (getField(2, 1) << 5) |
                (getField(10, 2) << 3) |
                (getField(3, 2) << 1);
    }

    /**
     * 命令の 16進数表記を取得します。
     *
     * @return 命令の 16進数表記
     */
    @Override
    public String toHex() {
        return String.format("%04x", getInst() & 0xffff);
    }
}
