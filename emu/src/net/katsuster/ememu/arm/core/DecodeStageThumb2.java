package net.katsuster.ememu.arm.core;

import net.katsuster.ememu.generic.*;

/**
 * Thumb-2 命令のデコードステージ。
 *
 * 参考: ARM アーキテクチャリファレンスマニュアル ARMv7-A および ARMv7-R
 * ARM DDI0406BJ
 *
 * 最新版は、日本語版 ARM DDI0406BJ, 英語版 ARM DDI0406C
 *
 * @author katsuhiro
 */
public class DecodeStageThumb2 extends Stage {
    /**
     * CPU コア c の実行ステージを生成します。
     *
     * @param c 実行ステージの持ち主となる CPU コア
     */
    public DecodeStageThumb2(ARMv5 c) {
        super(c);
    }

    /**
     * Thumb-2 命令をデコードします。
     *
     * @param inst Thumb-2 命令
     * @return 命令の種類
     */
    public OpIndex decode(InstructionThumb inst) {
        int op1 = inst.getField(11 + 16, 2);
        int op2 = inst.getField(4 + 16, 7);

        switch (op1) {
        case 0x1:
            if ((op2 & 0b1100100) == 0b0000000) {
                //0b00xx0xx, ロード、ストアマルチプル命令
                return decodeLdmStmT2(inst);
            } else if ((op2 & 0b1100100) == 0b0000100) {
                //0b00xx1xx, デュアルロード、ストア、排他ロード、ストア命令
                return decodeDualLdSt(inst);
            } else if ((op2 & 0b1100000) == 0b0100000) {
                //0b01xxxxx, データ処理（シフトしたレジスタ）
                return decodeALUShiftRegT2(inst);
            } else if ((op2 & 0b1000000) == 0b1000000) {
                //0b1000000, コプロセッサ命令
                return decodeCoproc(inst);
            } else {
                throw new IllegalArgumentException("Unknown op2 of Thumb-2" +
                        String.format("(%d, %d).", op1, op2));
            }
        case 0x2:
            //データ処理修正イミディエート、データ処理イミディエート、分岐命令
            boolean op = inst.getBit(15);

            if (!op) {
                if ((op2 & 0b0100000) == 0b0000000) {
                    //0bx0xxxxx, データ処理（修飾イミディエート）
                    return decodeALUModimmT2(inst);
                } else if ((op2 & 0b0100000) == 0b0100000) {
                    //0bx1xxxxx, データ処理（イミディエート）
                    return decodeALUImmT2(inst);
                } else {
                    throw new IllegalArgumentException("Unknown op2 of Thumb-2" +
                            String.format("(%d, %d).", op1, op2));
                }
            } else {
                //分岐およびその他
                return decodeBlBlxT2(inst);
            }
        case 0x3:
            //ロードストア、データ処理レジスタ、乗算、飽和演算
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        case 0x0:
            //16bit 命令、ここに来るのはおかしい
        default:
            throw new IllegalArgumentException("Unknown op1 of Thumb-2" +
                    String.format("(%d).", op1));
        }
    }

    /**
     * ロード、ストアマルチプル命令をデコードします。
     *
     * @param inst Thumb-2 命令
     * @return 命令の種類
     */
    public OpIndex decodeLdmStmT2(InstructionThumb inst) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * デュアルロード、ストア命令をデコードします。
     *
     * @param inst Thumb-2 命令
     * @return 命令の種類
     */
    public OpIndex decodeDualLdSt(InstructionThumb inst) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * デュアルロード、ストア命令をデコードします。
     *
     * @param inst Thumb-2 命令
     * @return 命令の種類
     */
    public OpIndex decodeALUShiftRegT2(InstructionThumb inst) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * コプロセッサ命令をデコードします。
     *
     * @param inst Thumb-2 命令
     * @return 命令の種類
     */
    public OpIndex decodeCoproc(InstructionThumb inst) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * データ処理（修飾イミディエート）命令をデコードします。
     *
     * @param inst Thumb-2 命令
     * @return 命令の種類
     */
    public OpIndex decodeALUModimmT2(InstructionThumb inst) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * データ処理（イミディエート）命令をデコードします。
     *
     * @param inst Thumb-2 命令
     * @return 命令の種類
     */
    public OpIndex decodeALUImmT2(InstructionThumb inst) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * 分岐およびその他の命令をデコードします。
     *
     * 上位ワード
     * [15:11] = 0b111_10
     *
     * 下位ワード
     * [15] = 0b1
     *
     * @param inst Thumb-2 命令
     * @return 命令の種類
     */
    public OpIndex decodeBlBlxT2(InstructionThumb inst) {
        int op1 = inst.getField(12, 3);

        switch (op1) {
        case 0:
        case 2:
            //0b0x0
            return decodeBlBlxMsrT2(inst);
        case 1:
        case 3:
            //0b0x1, 分岐命令
            return OpIndex.INS_THUMB2_B;
        case 4:
        case 6:
            //0b1x0, リンク付き分岐と状態遷移命令
            return OpIndex.INS_THUMB2_BLX;
        case 5:
        case 7:
            //0b1x1, リンク付き分岐命令
            return OpIndex.INS_THUMB2_BL;
        default:
            throw new IllegalArgumentException("Unknown op1 of Thumb-2 BlBlxT2" +
                    String.format("(%d).", op1));
        }
    }

    /**
     * 条件付き分岐、特殊レジスタへの移動命令をデコードします。
     *
     * 上位ワード
     * [15:11] = 0b111_10
     *
     * 下位ワード
     * [15:12] = 0b1_0x0
     *
     * @param inst Thumb-2 命令
     * @return 命令の種類
     */
    public OpIndex decodeBlBlxMsrT2(InstructionThumb inst) {
        int op = inst.getField(4 + 16, 7);
        int op1 = inst.getField(12, 3);
        int op2 = inst.getField(8, 4);

        if (op1 == 0 && op == 0b1111111) {
            //セキュアモニタコール
            return OpIndex.INS_THUMB2_SMC;
        } else if (op1 == 2 && op == 0b1111111) {
            //恒久的に未定義
            return OpIndex.INS_THUMB2_UND;
        }

        if ((op & 0b0111000) == 0b0111000) {
            //0bx111xxx, 条件付き分岐
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
        }

        switch (op) {
        case 0b0111000:
            //
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        case 0b0111001:
            //特殊レジスタへの移動
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        case 0b0111010:
            //プロセッサ状態の変更とヒント
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        case 0b0111011:
            //その他の制御命令
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        case 0b0111100:
            //分岐と Jazelle 状態への遷移命令
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        case 0b0111101:
            //例外からの復帰
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        case 0b0111110:
        case 0b0111111:
            //0b011111x, 特殊レジスタからの移動
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        default:
            throw new IllegalArgumentException("Unknown op1 of Thumb-2 BlBlxMsrT2" +
                    String.format("(%d).", op));
        }
    }
}
