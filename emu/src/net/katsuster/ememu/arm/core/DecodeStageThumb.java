package net.katsuster.ememu.arm.core;

import net.katsuster.ememu.generic.*;

/**
 * Thumb 命令（Thumb v1, v2, v3）のデコードステージ。
 *
 * 参考: ARM アーキテクチャリファレンスマニュアル Second Edition
 * ARM DDI0100DJ
 *
 * 最新版は、日本語版 ARM DDI0100HJ, 英語版 ARM DDI0100I
 *
 * @author katsuhiro
 */
public class DecodeStageThumb extends Stage {
    /**
     * ARMv5 CPU コア c のデコードステージを生成します。
     *
     * @param c デコードステージの持ち主となる ARMv5 CPU コア
     */
    public DecodeStageThumb(ARMv5 c) {
        super(c);
    }

    /**
     * Thumbv1, v2, v3 命令をデコードします。
     *
     * @param inst Thumb 命令
     * @return 命令の種類
     */
    public OpIndex decode(InstructionThumb inst) {
        int subcode = inst.getSubCodeField();

        switch (subcode) {
        case InstructionThumb.SUBCODE_ADDSUB:
            return decodeAddSub(inst);
        case InstructionThumb.SUBCODE_ALUIMM:
            return decodeALUImm(inst);
        case InstructionThumb.SUBCODE_ALUREG:
            return decodeALUReg(inst);
        case InstructionThumb.SUBCODE_LDWORD:
            return decodeLdWord(inst);
        case InstructionThumb.SUBCODE_LDHALF:
            return decodeLdHalf(inst);
        case InstructionThumb.SUBCODE_OTHERS:
            return decodeOthers(inst);
        case InstructionThumb.SUBCODE_LDMULT:
            return decodeLdmult(inst);
        case InstructionThumb.SUBCODE_BL_BLX:
            return decodeBlBlx(inst);
        default:
            throw new IllegalArgumentException("Unknown Subcode" +
                    String.format("(%d).", subcode));
        }
    }

    /**
     * レジスタ加算、減算命令をデコードします。
     *
     * subcode = 0b000
     *
     * @param inst Thumb 命令
     * @return 命令の種類
     */
    public OpIndex decodeAddSub(InstructionThumb inst) {
        int op = inst.getField(11, 2);
        int opc = inst.getField(9, 2);

        switch (op) {
        case 0x0:
            //lsl(1)
            return OpIndex.INS_THUMB_LSL1;
        case 0x1:
            //lsr(1)
            return OpIndex.INS_THUMB_LSR1;
        case 0x2:
            //asr(1)
            return OpIndex.INS_THUMB_ASR1;
        case 0x3:
            //レジスタ、イミディエート加算、減算
            switch (opc) {
            case 0x0:
                //add(3), レジスタ加算
                return OpIndex.INS_THUMB_ADD3;
            case 0x1:
                //sub(3), レジスタ減算
                return OpIndex.INS_THUMB_SUB3;
            case 0x2:
                //add(1), 小さいイミディエート加算
                return OpIndex.INS_THUMB_ADD1;
            case 0x3:
                //sub(1), 小さいイミディエート減算
                return OpIndex.INS_THUMB_SUB1;
            default:
                throw new IllegalArgumentException("Illegal opc(AddSub) bits " +
                        String.format("opc:0x%02x.", opc));
            }
        default:
            throw new IllegalArgumentException("Illegal op(AddSub) bits " +
                    String.format("op:0x%02x.", op));
        }
    }

    /**
     * イミディエート加算、減算命令をデコードします。
     *
     * subcode = 0b001
     *
     * @param inst Thumb 命令
     * @return 命令の種類
     */
    public OpIndex decodeALUImm(InstructionThumb inst) {
        int op = inst.getField(11, 2);

        switch (op) {
        case 0x0:
            //mov(1)
            return OpIndex.INS_THUMB_MOV1;
        case 0x1:
            //cmp(1)
            return OpIndex.INS_THUMB_CMP1;
        case 0x2:
            //add(2)
            return OpIndex.INS_THUMB_ADD2;
        case 0x3:
            //sub(2)
            return OpIndex.INS_THUMB_SUB2;
        default:
            throw new IllegalArgumentException("Illegal op(ALLUImm) bits " +
                    String.format("op:0x%02x.", op));
        }
    }

    /**
     * レジスタへのデータ処理命令をデコードします。
     *
     * subcode = 0b010
     *
     * @param inst Thumb 命令
     * @return 命令の種類
     */
    public OpIndex decodeALUReg(InstructionThumb inst) {
        int op = inst.getField(10, 3);

        switch (op) {
        case 0x0:
            //データ処理レジスタ命令
            return decodeALURegData(inst);
        case 0x1:
            //特殊データ処理、分岐、交換命令
            return decodeALURegSpecial(inst);
        case 0x2:
        case 0x3:
            //リテラルプールからのロード命令
            return OpIndex.INS_THUMB_LDR3;
        case 0x4:
        case 0x5:
        case 0x6:
        case 0x7:
            //ロード、ストアレジスタオフセット命令
            return decodeALURegOffset(inst);
        default:
            throw new IllegalArgumentException("Illegal op(ALUReg) bits " +
                    String.format("op:0x%02x.", op));
        }
    }

    /**
     * レジスタへのデータ処理命令をデコードします。
     *
     * subcode = 0b010
     * op[12:10] = 0b000
     *
     * @param inst Thumb 命令
     * @return 命令の種類
     */
    public OpIndex decodeALURegData(InstructionThumb inst) {
        int op = inst.getOpcode5Field();

        switch (op) {
        case InstructionThumb.OPCODE5_AND:
            return OpIndex.INS_THUMB_AND;
        case InstructionThumb.OPCODE5_EOR:
            return OpIndex.INS_THUMB_EOR;
        case InstructionThumb.OPCODE5_LSL:
            return OpIndex.INS_THUMB_LSL2;
        case InstructionThumb.OPCODE5_LSR:
            return OpIndex.INS_THUMB_LSR2;
        case InstructionThumb.OPCODE5_ASR:
            return OpIndex.INS_THUMB_ASR2;
        case InstructionThumb.OPCODE5_ADC:
            return OpIndex.INS_THUMB_ADC;
        case InstructionThumb.OPCODE5_SBC:
            return OpIndex.INS_THUMB_SBC;
        case InstructionThumb.OPCODE5_ROR:
            return OpIndex.INS_THUMB_ROR;
        case InstructionThumb.OPCODE5_TST:
            return OpIndex.INS_THUMB_TST;
        case InstructionThumb.OPCODE5_NEG:
            return OpIndex.INS_THUMB_NEG;
        case InstructionThumb.OPCODE5_CMP:
            return OpIndex.INS_THUMB_CMP2;
        case InstructionThumb.OPCODE5_CMN:
            return OpIndex.INS_THUMB_CMN;
        case InstructionThumb.OPCODE5_ORR:
            return OpIndex.INS_THUMB_ORR;
        case InstructionThumb.OPCODE5_MUL:
            return OpIndex.INS_THUMB_MUL;
        case InstructionThumb.OPCODE5_BIC:
            return OpIndex.INS_THUMB_BIC;
        case InstructionThumb.OPCODE5_MVN:
            return OpIndex.INS_THUMB_MVN;
        default:
            throw new IllegalArgumentException("Illegal op(ALURegData) bits " +
                    String.format("op:0x%02x.", op));
        }
    }

    /**
     * 特殊データ処理、分岐、交換命令をデコードします。
     *
     * subcode = 0b010
     * op[12:10] = 0b001
     *
     * @param inst Thumb 命令
     * @return 命令の種類
     */
    public OpIndex decodeALURegSpecial(InstructionThumb inst) {
        int op = inst.getField(6, 4);

        switch (op) {
        case 0x0:
            //下位レジスタの加算（ADD）、ARMv6T2 以降
            return OpIndex.INS_THUMB_ADD4;
        case 0x1:
        case 0x2:
        case 0x3:
            //上位レジスタの加算（ADD）
            return OpIndex.INS_THUMB_ADD4;
        //case 0x4:
        //（予測不能）
        case 0x5:
        case 0x6:
        case 0x7:
            //上位レジスタの比較（CMP）
            return OpIndex.INS_THUMB_CMP3;
        case 0x8:
            //下位レジスタの移動（MOV）、ARMv6 以降
            return OpIndex.INS_THUMB_MOV3;
        case 0x9:
        case 0xa:
        case 0xb:
            //上位レジスタの移動（MOV）
            return OpIndex.INS_THUMB_MOV3;
        case 0xc:
        case 0xd:
            //分岐と状態遷移（BX）
            return OpIndex.INS_THUMB_BX;
        case 0xe:
        case 0xf:
            //リンク付き分岐と状態遷移（BLX）
            return OpIndex.INS_THUMB_BLX2;
        default:
            throw new IllegalArgumentException("Illegal op(ALURegSpecial) bits " +
                    String.format("op:0x%02x.", op));
        }
    }

    /**
     * ロード、ストアレジスタオフセット命令をデコードします。
     *
     * subcode = 0b010
     * op[12:10] = 0b1xx
     *
     * @param inst Thumb 命令
     * @return 命令の種類
     */
    public OpIndex decodeALURegOffset(InstructionThumb inst) {
        int op = inst.getField(9, 3);

        switch (op) {
        case 0x0:
            //レジスタストア（STR）
            return OpIndex.INS_THUMB_STR2;
        case 0x1:
            //レジスタストア ハーフワード（STRH）
            return OpIndex.INS_THUMB_STRH2;
        case 0x2:
            //レジスタストア バイト（STRB）
            return OpIndex.INS_THUMB_STRB2;
        case 0x3:
            //レジスタロード 符号付きバイト（LDRSB）
            return OpIndex.INS_THUMB_LDRSB;
        case 0x4:
            //レジスタロード（LDR）
            return OpIndex.INS_THUMB_LDR2;
        case 0x5:
            //レジスタロード ハーフワード（LDRH）
            return OpIndex.INS_THUMB_LDRH2;
        case 0x6:
            //レジスタロード バイト（LDRB）
            return OpIndex.INS_THUMB_LDRB2;
        case 0x7:
            //レジスタロード 符号付きハーフワード（LDRSH）
            return OpIndex.INS_THUMB_LDRSH;
        default:
            throw new IllegalArgumentException("Illegal op(RegOffset) bits " +
                    String.format("op:0x%02x.", op));
        }
    }

    /**
     * ワード、バイトのロード、ストア命令をデコードします。
     *
     * subcode = 0b011
     *
     * @param inst Thumb 命令
     * @return 命令の種類
     */
    public OpIndex decodeLdWord(InstructionThumb inst) {
        int op = inst.getField(11, 2);

        switch (op) {
        case 0x0:
            //レジスタストア（STR）
            return OpIndex.INS_THUMB_STR1;
        case 0x1:
            //レジスタロード（LDR）
            return OpIndex.INS_THUMB_LDR1;
        case 0x2:
            //レジスタストア バイト（STRB）
            return OpIndex.INS_THUMB_STRB1;
        case 0x3:
            //レジスタロード バイト（LDRB）
            return OpIndex.INS_THUMB_LDRB1;
        default:
            throw new IllegalArgumentException("Illegal op(LdWord) bits " +
                    String.format("op:0x%02x.", op));
        }
    }

    /**
     * ハーフワードのロード、ストア命令、スタックのロード、ストア命令をデコードします。
     *
     * subcode = 0b100
     *
     * @param inst Thumb 命令
     * @return 命令の種類
     */
    public OpIndex decodeLdHalf(InstructionThumb inst) {
        int op = inst.getField(11, 2);

        switch (op) {
        case 0x0:
            //ストア ハーフワード（STRH）
            return OpIndex.INS_THUMB_STRH1;
        case 0x1:
            //ロード ハーフワード（LDRH）
            return OpIndex.INS_THUMB_LDRH1;
        case 0x2:
            //ストア SP 相対（STR）
            return OpIndex.INS_THUMB_STR3;
        case 0x3:
            //ロード SP 相対（LDR）
            return OpIndex.INS_THUMB_LDR4;
        default:
            throw new IllegalArgumentException("Illegal op(LdHalf) bits " +
                    String.format("op:0x%02x.", op));
        }
    }

    /**
     * SP, PC 加算命令、その他の命令をデコードします。
     *
     * subcode = 0b101
     *
     * @param inst Thumb 命令
     * @return 命令の種類
     */
    public OpIndex decodeOthers(InstructionThumb inst) {
        int op = inst.getField(11, 2);

        switch (op) {
        case 0x0:
            //PC への加算（ADD）
            return OpIndex.INS_THUMB_ADD5;
        case 0x1:
            //SP への加算（ADD）
            return OpIndex.INS_THUMB_ADD6;
        case 0x2:
            //その他の命令
            int op2 = inst.getField(5, 6);

            switch (op2) {
            case 0x00: //0b0000xx
            case 0x01:
            case 0x02:
            case 0x03:
                //スタックポインタの加算（ADD）
                return OpIndex.INS_THUMB_ADD7;
            case 0x04: //0b0001xx
            case 0x05:
            case 0x06:
            case 0x07:
                //スタックポインタの減算（SUB）
                return OpIndex.INS_THUMB_SUB4;
            case 0x20: case 0x21: case 0x22: case 0x23: //0b10xxxx
            case 0x24: case 0x25: case 0x26: case 0x27:
            case 0x28: case 0x29: case 0x2a: case 0x2b:
            case 0x2c: case 0x2d: case 0x2e: case 0x2f:
                //複数レジスタプッシュ（PUSH）
                return OpIndex.INS_THUMB_PUSH;
            default:
                throw new IllegalArgumentException("Illegal op2(op=0b10, Others) bits " +
                        String.format("op2:0x%02x.", op2));
            }
        case 0x3:
            //その他の命令
            int op3 = inst.getField(5, 6);

            switch (op3) {
            case 0x20: case 0x21: case 0x22: case 0x23: //0b10xxxx
            case 0x24: case 0x25: case 0x26: case 0x27:
            case 0x28: case 0x29: case 0x2a: case 0x2b:
            case 0x2c: case 0x2d: case 0x2e: case 0x2f:
                //複数レジスタポップ（POP）
                return OpIndex.INS_THUMB_POP;
            case 0x30: case 0x31: case 0x32: case 0x33: //0b10xxxx
            case 0x34: case 0x35: case 0x36: case 0x37:
                //ブレークポイント（BKPT）
                return OpIndex.INS_THUMB_BKPT;
            default:
                throw new IllegalArgumentException("Illegal op2(op=0x11, Others) bits " +
                        String.format("op3:0x%02x.", op3));
            }
        default:
            throw new IllegalArgumentException("Illegal op(Others) bits " +
                    String.format("op:0x%02x.", op));
        }
    }

    /**
     * ロード、ストアマルチプル命令、条件付き分岐命令をデコードします。
     *
     * subcode = 0b110
     *
     * @param inst Thumb 命令
     * @return 命令の種類
     */
    public OpIndex decodeLdmult(InstructionThumb inst) {
        int op = inst.getField(11, 2);

        switch (op) {
        case 0x0:
            //ストアマルチプル（STMIA）
            return OpIndex.INS_THUMB_STMIA;
        case 0x1:
            //ロードマルチプル（LDMIA）
            return OpIndex.INS_THUMB_LDMIA;
        case 0x2:
        case 0x3:
            //分岐命令
            int cond = inst.getField(8, 4);

            switch (cond) {
            case InstructionARM.COND_AL:
                //未定義命令
                return OpIndex.INS_THUMB_UND;
            case InstructionARM.COND_NV:
                //swi
                return OpIndex.INS_THUMB_SWI;
            default:
                //b
                return OpIndex.INS_THUMB_B1;
            }
        default:
            throw new IllegalArgumentException("Illegal op(Others) bits " +
                    String.format("op:0x%02x.", op));
        }
    }

    /**
     * 分岐命令をデコードします。
     *
     * subcode = 0b111
     *
     * @param inst Thumb 命令
     * @return 命令の種類
     */
    public OpIndex decodeBlBlx(InstructionThumb inst) {
        int h = inst.getField(11, 2);

        switch (h) {
        case 0x0:
            //b(無条件分岐)命令
            return OpIndex.INS_THUMB_B2;
        case 0x1:
        case 0x2:
        case 0x3:
            //Thumb-2 命令
            throw new IllegalArgumentException("Thumb-2 instruction detected.");
        default:
            //異常な値
            throw new IllegalArgumentException("Illegal h bits " +
                    String.format("h:0x%02x.", h));
        }
    }
}
