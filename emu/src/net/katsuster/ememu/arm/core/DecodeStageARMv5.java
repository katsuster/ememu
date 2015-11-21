package net.katsuster.ememu.arm.core;

import net.katsuster.ememu.generic.*;

/**
 * ARMv5 命令のデコードステージ。
 *
 * 参考: ARM アーキテクチャリファレンスマニュアル Second Edition
 * ARM DDI0100DJ
 *
 * 最新版は、日本語版 ARM DDI0100HJ, 英語版 ARM DDI0100I
 *
 * @author katsuhiro
 */
public class DecodeStageARMv5 extends Stage {
    /**
     * ARMv5 CPU コア c のデコードステージを生成します。
     *
     * @param c 実行ステージの持ち主となる ARMv5 CPU コア
     */
    public DecodeStageARMv5(ARMv5 c) {
        super(c);
    }

    /**
     * ARM 命令をデコードします。
     *
     * @param inst ARM 命令
     * @return 命令の種類
     */
    public OpIndex decode(InstructionARM inst) {
        int subcode = inst.getSubCodeField();

        //ARM モード
        switch (subcode) {
        case InstructionARM.SUBCODE_USEALU:
            return decodeALU(inst);
        case InstructionARM.SUBCODE_LDRSTR:
            return decodeLdrStr(inst);
        case InstructionARM.SUBCODE_LDMSTM:
            return decodeLdmStm(inst);
        case InstructionARM.SUBCODE_COPSWI:
            return decodeCopSwi(inst);
        default:
            throw new IllegalArgumentException("Unknown Subcode" +
                    String.format("(%d).", subcode));
        }
    }

    /**
     * データ処理命令をデコードします。
     *
     * subcode = 0b00
     *
     * @param inst ARM 命令
     * @return 命令の種類
     */
    public OpIndex decodeALU(InstructionARM inst) {
        boolean i = inst.getIBit();
        boolean b7 = inst.getBit(7);
        boolean b4 = inst.getBit(4);

        if (!i) {
            //b7, b4 の値が、
            //  0, 0: イミディエートシフト
            //  1, 0: イミディエートシフト
            //  0, 1: レジスタシフト
            //  1, 1: 算術命令拡張空間、ロードストア命令拡張空間
            if (!b4) {
                //イミディエートシフト
                return decodeALUShiftImm(inst);
            } else if (!b7 && b4) {
                //レジスタシフト
                return decodeALUShiftReg(inst);
            } else {
                //算術命令拡張空間、ロードストア命令拡張空間
                int cond = inst.getCondField();
                boolean p = inst.getBit(24);
                int op = inst.getField(5, 2);

                if (cond != InstructionARM.COND_NV && !p && op == 0) {
                    //算術命令拡張空間
                    return decodeExtALU(inst);
                } else {
                    //ロードストア命令拡張空間
                    return decodeExtLdrStr(inst);
                }
            }
        } else {
            //イミディエート
            return decodeALUImm(inst);
        }
    }

    /**
     * イミディエートシフトオペランドを取るデータ処理命令、
     * または、その他の命令をデコードします。
     *
     * @param inst ARM 命令
     * @return 命令の種類
     */
    public OpIndex decodeALUShiftImm(InstructionARM inst) {
        int id = inst.getOpcodeSBitShiftID();

        switch (id) {
        case InstructionARM.OPCODE_S_OTH:
            return decodeALUOther(inst);
        default:
            return decodeALUCommon(inst, id);
        }
    }

    /**
     * レジスタシフトオペランドを取るデータ処理命令、
     * その他の命令をデコードします。
     *
     * @param inst ARM 命令
     * @return 命令の種類
     */
    public OpIndex decodeALUShiftReg(InstructionARM inst) {
        int id = inst.getOpcodeSBitShiftID();

        switch (id) {
        case InstructionARM.OPCODE_S_OTH:
            return decodeALUOther(inst);
        default:
            return decodeALUCommon(inst, id);
        }
    }

    /**
     * 算術命令拡張空間（乗算）、
     * をデコードします。
     *
     * cond != NV
     * bit[27:24] = 0b0000
     * bit[7:4] = 0b1001
     *
     * @param inst ARM 命令
     * @return 命令の種類
     */
    public OpIndex decodeExtALU(InstructionARM inst) {
        //U, B, W ビット[23:21]
        int ubw = inst.getField(21, 3);

        //算術命令拡張空間
        switch (ubw) {
        case 1:
            //mla
            return OpIndex.INS_ARM_MLA;
        case 0:
            //mul
            return OpIndex.INS_ARM_MUL;
        case 7:
            //smlal
            return OpIndex.INS_ARM_SMLAL;
        case 6:
            //smull
            return OpIndex.INS_ARM_SMULL;
        case 5:
            //umlal
            return OpIndex.INS_ARM_UMLAL;
        case 4:
            //umull
            return OpIndex.INS_ARM_UMULL;
        default:
            //未定義
            //TODO: Not implemented
            return OpIndex.INS_ARM_UND;
        }
    }

    /**
     * ロードストア命令拡張空間（ハーフワードロード、ストア）、
     * をデコードします。
     *
     * cond != NV
     * bit[27:25] = 0b000
     * bit[7] = 0b1
     * bit[4] = 0b1
     *
     * なおかつ、下記を含まない命令です。
     *
     * bit[24] = 0b0
     * bit[6:5] = 0b00
     *
     * 各ビットと命令の対応は下記の通りです。
     *
     *        |  P  |  U  |  B  |  W  |  L  ||          op1          |
     *        | 24  | 23  | 22  | 21  | 20  ||  7  |  6  |  5  |  4  |
     * -------+-----+-----+-----+-----+-----++-----+-----+-----+-----+
     * SWP    |  1  |  0  |  0  |  0  |  0  ||  1  |  0  |  0  |  1  |
     * SWPB   |  1  |  0  |  1  |  0  |  0  ||  1  |  0  |  0  |  1  |
     * -------+-----+-----+-----+-----+-----++-----+-----+-----+-----+
     * LDRH   |  x  |  x  |  x  |  x  |  1  ||  1  |  0  |  1  |  1  |
     * STRH   |  x  |  x  |  x  |  x  |  0  ||  1  |  0  |  1  |  1  |
     * -------+-----+-----+-----+-----+-----++-----+-----+-----+-----+
     * LDRSB  |  x  |  x  |  x  |  x  |  1  ||  1  |  1  |  0  |  1  |
     * LDRD   |  x  |  x  |  x  |  x  |  0  ||  1  |  1  |  0  |  1  |
     * -------+-----+-----+-----+-----+-----++-----+-----+-----+-----+
     * LDRSH  |  x  |  x  |  x  |  x  |  1  ||  1  |  1  |  1  |  1  |
     * STRD   |  x  |  x  |  x  |  x  |  0  ||  1  |  1  |  1  |  1  |
     * -------+-----+-----+-----+-----+-----++-----+-----+-----+-----+
     *
     * これ以外のパターンは全て未定義命令です。
     *
     * @param inst ARM 命令
     * @return 命令の種類
     */
    public OpIndex decodeExtLdrStr(InstructionARM inst) {
        boolean p = inst.getBit(24);
        //U, B, W ビット[23:21]
        int ubw = inst.getField(21, 3);
        boolean l = inst.getBit(20);
        int op = inst.getField(5, 2);

        //ロードストア命令拡張空間
        if (p && op == 0) {
            switch (ubw) {
            case 0:
                //swp
                return OpIndex.INS_ARM_SWP;
            case 1:
                //swpb
                return OpIndex.INS_ARM_SWPB;
            default:
                //未定義
                //TODO: Not implemented
                return OpIndex.INS_ARM_UND;
            }
        } else if (op == 1) {
            if (l) {
                //ldrh
                return OpIndex.INS_ARM_LDRH;
            } else {
                //strh
                return OpIndex.INS_ARM_STRH;
            }
        } else if (op == 2) {
            if (l) {
                //ldrsb
                return OpIndex.INS_ARM_LDRSB;
            } else {
                //ldrd
                return OpIndex.INS_ARM_LDRD;
            }
        } else if (op == 3) {
            if (l) {
                //ldrsh
                return OpIndex.INS_ARM_LDRSH;
            } else {
                //strd
                return OpIndex.INS_ARM_STRD;
            }
        } else {
            //未定義
            //TODO: Not implemented
            return OpIndex.INS_ARM_UND;
        }
    }

    /**
     * イミディエートのみを取るデータ処理命令、その他の命令をデコードします。
     *
     * データ処理イミディエート命令、
     * ステータスレジスタへのイミディエート移動命令、
     * の実行
     *
     * @param inst ARM 命令
     * @return 命令の種類
     */
    public OpIndex decodeALUImm(InstructionARM inst) {
        int id = inst.getOpcodeSBitImmID();

        switch (id) {
        case InstructionARM.OPCODE_S_MSR:
            return OpIndex.INS_ARM_MSR;
        case InstructionARM.OPCODE_S_UND:
            return OpIndex.INS_ARM_UND;
        default:
            return decodeALUCommon(inst, id);
        }
    }

    /**
     * その他のデータ処理命令、
     * をデコードします。
     *
     * bit[27:23] = 0b00010
     * bit[20] = 0
     *
     * 各ビットと命令の対応は下記の通りです。
     *
     *         | 22  | 21  ||  7  |  6  |  5  |  4  |
     * --------+-----+-----++-----+-----+-----+-----+
     * MRS     |  x  |  0  ||  0  |  0  |  0  |  0  |
     * MSR     |  x  |  1  ||  0  |  0  |  0  |  0  |
     * BX      |  0  |  1  ||  0  |  0  |  0  |  1  |
     * CLZ     |  1  |  1  ||  0  |  0  |  0  |  1  |
     * BLX(2)  |  0  |  1  ||  0  |  0  |  1  |  1  |
     * BKPT    |  0  |  1  ||  0  |  1  |  1  |  1  |
     * --------+-----+-----++-----+-----+-----+-----+
     * QADD    |  0  |  0  ||  0  |  1  |  0  |  1  |
     * QSUB    |  0  |  1  ||  0  |  1  |  0  |  1  |
     * QDADD   |  1  |  0  ||  0  |  1  |  0  |  1  |
     * QDSUB   |  1  |  1  ||  0  |  1  |  0  |  1  |
     * --------+-----+-----++-----+-----+-----+-----+
     * SMLAxy  |  0  |  0  ||  1  |  y  |  x  |  0  |
     * SMLAWxy |  0  |  1  ||  1  |  y  |  0  |  0  |
     * SMULWxy |  0  |  1  ||  1  |  y  |  1  |  0  |
     * SMLALxy |  1  |  0  ||  1  |  y  |  x  |  0  |
     * SMULxy  |  1  |  1  ||  1  |  y  |  x  |  0  |
     * --------+-----+-----++-----+-----+-----+-----+
     *
     * これ以外のパターンは全て未定義命令です。
     *
     * @param inst ARM 命令
     * @return 命令の種類
     */
    public OpIndex decodeALUOther(InstructionARM inst) {
        int cond = inst.getCondField();
        boolean b22 = inst.getBit(22);
        boolean b21 = inst.getBit(21);
        int type = inst.getField(4, 4);

        switch (type) {
        case 0x0:
            if (!b21) {
                //mrs
                return OpIndex.INS_ARM_MRS;
            } else {
                //msr
                return OpIndex.INS_ARM_MSR;
            }
        case 0x1:
            if (!b22 && b21) {
                //bx
                return OpIndex.INS_ARM_BX;
            } else if (b22 && b21) {
                //clz
                return OpIndex.INS_ARM_CLZ;
            } else {
                //未定義
                return OpIndex.INS_ARM_UND;
            }
        case 0x3:
            if (!b22 && b21) {
                //blx(2)
                return OpIndex.INS_ARM_BLX2;
            } else {
                //未定義
                return OpIndex.INS_ARM_UND;
            }
        case 0x5:
            if (!b22 && !b21) {
                //qdsub
                return OpIndex.INS_ARM_QDSUB;
            } else if (!b22 && b21) {
                //qdadd
                return OpIndex.INS_ARM_QDADD;
            } else if (b22 && !b21) {
                //qsub
                return OpIndex.INS_ARM_QSUB;
            } else {
                //qadd
                return OpIndex.INS_ARM_QADD;
            }
        case 0x7:
            if (cond == InstructionARM.COND_AL && !b22 && b21) {
                //bkpt
                return OpIndex.INS_ARM_BKPT;
            } else {
                //未定義
                return OpIndex.INS_ARM_UND;
            }
        case 0x8:
        case 0xc:
            if (!b22 && !b21) {
                //smla
                return OpIndex.INS_ARM_SMLAXY;
            } else if (!b22 && b21) {
                //smlaw
                return OpIndex.INS_ARM_SMLAWY;
            } else if (b22 && !b21) {
                //smlal
                return OpIndex.INS_ARM_SMLALXY;
            } else {
                //smul
                return OpIndex.INS_ARM_SMULXY;
            }
        case 0xa:
        case 0xe:
            if (!b22 && !b21) {
                //smla
                return OpIndex.INS_ARM_SMLAXY;
            } else if (!b22 && b21) {
                //smulw
                return OpIndex.INS_ARM_SMULWY;
            } else if (b22 && !b21) {
                //smlal
                return OpIndex.INS_ARM_SMLALXY;
            } else {
                //smul
                return OpIndex.INS_ARM_SMULXY;
            }
        default:
            //未定義
            //TODO: Not implemented
            return OpIndex.INS_ARM_UND;
        }
    }

    /**
     * イミディエートシフトオペランド、レジスタシフトオペランド、
     * イミディエートを取るデータ処理命令に共通する命令をデコードします。
     *
     * 下記の種類の命令を扱います。
     * and, eor, sub, rsb,
     * add, adc, sbc, rsc,
     * tst, teq, cmp, cmn,
     * orr, mov, bic, mvn,
     *
     * @param inst ARM 命令
     * @param id   オペコードフィールドと S ビットが示す演算の ID
     * @return 命令の種類
     */
    public OpIndex decodeALUCommon(InstructionARM inst, int id) {
        switch (id) {
        case InstructionARM.OPCODE_S_AND:
            return OpIndex.INS_ARM_ALUAND;
        case InstructionARM.OPCODE_S_EOR:
            return OpIndex.INS_ARM_ALUEOR;
        case InstructionARM.OPCODE_S_SUB:
            return OpIndex.INS_ARM_ALUSUB;
        case InstructionARM.OPCODE_S_RSB:
            return OpIndex.INS_ARM_ALURSB;
        case InstructionARM.OPCODE_S_ADD:
            return OpIndex.INS_ARM_ALUADD;
        case InstructionARM.OPCODE_S_ADC:
            return OpIndex.INS_ARM_ALUADC;
        case InstructionARM.OPCODE_S_SBC:
            return OpIndex.INS_ARM_ALUSBC;
        case InstructionARM.OPCODE_S_RSC:
            return OpIndex.INS_ARM_ALURSC;
        case InstructionARM.OPCODE_S_TST:
            return OpIndex.INS_ARM_ALUTST;
        case InstructionARM.OPCODE_S_TEQ:
            return OpIndex.INS_ARM_ALUTEQ;
        case InstructionARM.OPCODE_S_CMP:
            return OpIndex.INS_ARM_ALUCMP;
        case InstructionARM.OPCODE_S_CMN:
            return OpIndex.INS_ARM_ALUCMN;
        case InstructionARM.OPCODE_S_ORR:
            return OpIndex.INS_ARM_ALUORR;
        case InstructionARM.OPCODE_S_MOV:
            return OpIndex.INS_ARM_ALUMOV;
        case InstructionARM.OPCODE_S_BIC:
            return OpIndex.INS_ARM_ALUBIC;
        case InstructionARM.OPCODE_S_MVN:
            return OpIndex.INS_ARM_ALUMVN;
        default:
            throw new IllegalArgumentException("Unknown opcode S-bit ID " +
                    String.format("%d.", id));
        }
    }

    /**
     * ロード、ストア命令をデコードします。
     *
     * subcode = 0b01
     *
     * 各ビットと命令の対応は下記の通りです。
     *
     *        |  I  |  P  |  B  |  W  |  L  |     |
     *        | 25  | 24  | 22  | 21  | 20  |  4  |
     * -------+-----+-----+-----+-----+-----+-----+
     * LDR    |  x  |  x  |  0  |  x  |  1  |  x  |
     * LDRB   |  x  |  x  |  1  |  x  |  1  |  x  |
     * LDRBT  |  x  |  0  |  1  |  1  |  1  |  x  |
     * LDRT   |  x  |  0  |  0  |  1  |  1  |  x  |
     * -------+-----+-----+-----+-----+-----+-----+
     * STR    |  x  |  x  |  0  |  x  |  0  |  x  |
     * STRB   |  x  |  x  |  1  |  x  |  0  |  x  |
     * STRBT  |  x  |  0  |  1  |  1  |  0  |  x  |
     * STRT   |  x  |  0  |  0  |  1  |  0  |  x  |
     * -------+-----+-----+-----+-----+-----+-----+
     * UND    |  1  |  x  |  x  |  x  |  x  |  1  |
     * -------+-----+-----+-----+-----+-----+-----+
     *
     * @param inst ARM 命令
     * @return 命令の種類
     */
    public OpIndex decodeLdrStr(InstructionARM inst) {
        int cond = inst.getCondField();
        boolean i = inst.getBit(25);
        boolean p = inst.getBit(24);
        boolean b = inst.getBit(22);
        boolean w = inst.getBit(21);
        boolean l = inst.getLBit();
        int rd = inst.getRdField();
        boolean b4 = inst.getBit(4);

        if (i && b4) {
            //未定義命令
            //TODO: Not implemented
            return OpIndex.INS_ARM_UND;
        } else if (l) {
            if (!p && !b && w) {
                //ldrt
                return OpIndex.INS_ARM_LDRT;
            } else if (!p && b && w) {
                //ldrbt
                return OpIndex.INS_ARM_LDRBT;
            } else if (b) {
                if (cond == InstructionARM.COND_NV && p && !w && rd == 15) {
                    //pld
                    return OpIndex.INS_ARM_PLD;
                } else {
                    //ldrb
                    return OpIndex.INS_ARM_LDRB;
                }
            } else if (!b) {
                //ldr
                return OpIndex.INS_ARM_LDR;
            } else {
                throw new IllegalArgumentException("Illegal P,B,W bits " +
                        String.format("p:%b, b:%b, w:%b.", p, b, w));
            }
        } else if (!l) {
            if (!p && !b && w) {
                //strt
                return OpIndex.INS_ARM_STRT;
            } else if (!p && b && w) {
                //strbt
                return OpIndex.INS_ARM_STRBT;
            } else if (b) {
                //strb
                return OpIndex.INS_ARM_STRB;
            } else if (!b) {
                //str
                return OpIndex.INS_ARM_STR;
            } else {
                throw new IllegalArgumentException("Illegal P,B,W bits " +
                        String.format("p:%b, b:%b, w:%b.", p, b, w));
            }
        } else {
            throw new IllegalArgumentException("Illegal P,B,W,L bits " +
                    String.format("p:%b, b:%b, w:%b, l:%b.", p, b, w, l));
        }
    }

    /**
     * ロードマルチプル、ストアマルチプル、分岐命令をデコードします。
     *
     * subcode = 0b10
     *
     * @param inst ARM 命令
     * @return 命令の種類
     */
    public OpIndex decodeLdmStm(InstructionARM inst) {
        int cond = inst.getCondField();
        boolean b25 = inst.getBit(25);
        boolean l = inst.getLBit();

        if (!b25) {
            //ロードマルチプル、ストアマルチプル
            if (cond == InstructionARM.COND_NV) {
                //未定義
                //TODO: Not implemented
                return OpIndex.INS_ARM_UND;
            } else {
                if (l) {
                    //ldm(1), ldm(2), ldm(3)
                    return decodeLdm(inst);
                } else {
                    //stm(1), stm(2)
                    return decodeStm(inst);
                }
            }
        } else {
            //分岐命令
            if (cond == InstructionARM.COND_NV) {
                //blx
                return OpIndex.INS_ARM_BLX1;
            } else {
                //b, bl
                return OpIndex.INS_ARM_BL;
            }
        }
    }

    /**
     * ロードマルチプル命令をデコードします。
     *
     * subcode = 0b10
     *
     * @param inst ARM 命令
     * @return 命令の種類
     */
    public OpIndex decodeLdm(InstructionARM inst) {
        boolean s = inst.getBit(22);
        boolean b15 = inst.getBit(15);

        if (!s) {
            //ldm(1)
            return OpIndex.INS_ARM_LDM1;
        } else {
            if (!b15) {
                //ldm(2)
                return OpIndex.INS_ARM_LDM2;
            } else {
                //ldm(3)
                return OpIndex.INS_ARM_LDM3;
            }
        }
    }

    /**
     * ストアマルチプル命令をデコードします。
     *
     * subcode = 0b10
     *
     * @param inst ARM 命令
     * @return 命令の種類
     */
    public OpIndex decodeStm(InstructionARM inst) {
        boolean s = inst.getBit(22);
        boolean w = inst.getBit(21);

        if (!s) {
            //stm(1)
            return OpIndex.INS_ARM_STM1;
        } else {
            if (!w) {
                //stm(2)
                return OpIndex.INS_ARM_STM2;
            } else {
                //未定義
                return OpIndex.INS_ARM_UND;
            }
        }
    }

    /**
     * コプロセッサ、ソフトウェア割り込み命令をデコードします。
     *
     * subcode = 0b11
     *
     * @param inst ARM 命令
     * @return 命令の種類
     */
    public OpIndex decodeCopSwi(InstructionARM inst) {
        int cond = inst.getCondField();
        int subsub = inst.getField(24, 2);
        boolean b20 = inst.getBit(20);
        boolean b4 = inst.getBit(4);

        switch (subsub) {
        case 0:
        case 1:
            if (b20) {
                //ldc
                //TODO: Not implemented
                throw new IllegalArgumentException("Sorry, not implemented.");
            } else {
                //stc
                //TODO: Not implemented
                throw new IllegalArgumentException("Sorry, not implemented.");
            }
        case 2:
            if (!b4) {
                //cdp
                return OpIndex.INS_ARM_CDP;
            } else {
                if (!b20) {
                    //mcr
                    return OpIndex.INS_ARM_MCR;
                } else {
                    //mrc
                    return OpIndex.INS_ARM_MRC;
                }
            }
        case 3:
            if (cond == InstructionARM.COND_NV) {
                //未定義
                return OpIndex.INS_ARM_UND;
            } else {
                //swi
                return OpIndex.INS_ARM_SWI;
            }
        default:
            throw new IllegalArgumentException("Illegal b25, b24 bits " +
                    String.format("b25b24:%d.", subsub));
        }
    }
}
