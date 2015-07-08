package net.katsuster.ememu.arm.core;

import net.katsuster.ememu.generic.*;

/**
 * Thumb 命令。
 *
 * @author katsuhiro
 */
public class InstructionThumb extends Instruction {
    /**
     * 指定されたバイナリ値の Thumb 命令を作成します。
     *
     * @param inst Thumb 命令のバイナリ値
     */
    public InstructionThumb(int inst) {
        super(inst & 0x0000ffff, 2);
    }

    public static final int SUBCODE_ADDSUB = 0;
    public static final int SUBCODE_ALUIMM = 1;
    public static final int SUBCODE_ALUREG = 2;
    public static final int SUBCODE_LDWORD = 3;
    public static final int SUBCODE_LDHALF = 4;
    public static final int SUBCODE_OTHERS = 5;
    public static final int SUBCODE_LDMULT = 6;
    public static final int SUBCODE_BL_BLX = 7;

    /**
     * Thumb 命令セットのサブコードフィールド（ビット [15:13]）を取得します。
     *
     * 注: ARM の仕様書にはサブコードのフィールドという定義はありません。
     * このアプリケーション独自の定義です。
     *
     * @return サブコード
     */
    public int getSubCodeField() {
        return getSubCodeField(getInst());
    }

    /**
     * Thumb 命令セットのサブコードフィールド（ビット [15:13]）を取得します。
     *
     * 注: ARM の仕様書にはサブコードのフィールドという定義はありません。
     * このアプリケーション独自の定義です。
     *
     * @param inst Thumb 命令
     * @return サブコード
     */
    public static int getSubCodeField(int inst) {
        return BitOp.getField32(inst, 13, 3);
    }

    public static final int OPCODE5_AND = 0;
    public static final int OPCODE5_EOR = 1;
    public static final int OPCODE5_LSL = 2;
    public static final int OPCODE5_LSR = 3;
    public static final int OPCODE5_ASR = 4;
    public static final int OPCODE5_ADC = 5;
    public static final int OPCODE5_SBC = 6;
    public static final int OPCODE5_ROR = 7;
    public static final int OPCODE5_TST = 8;
    public static final int OPCODE5_NEG = 9;
    public static final int OPCODE5_CMP = 10;
    public static final int OPCODE5_CMN = 11;
    public static final int OPCODE5_ORR = 12;
    public static final int OPCODE5_MUL = 13;
    public static final int OPCODE5_BIC = 14;
    public static final int OPCODE5_MVN = 15;

    /**
     * Thumb 命令セットのデータ処理命令の op_5フィールド（ビット [9:6]）を取得します。
     *
     * データ処理命令に存在し、命令が行う演算の内容を示します。
     *
     * @return オペコード
     */
    public int getOpcode5Field() {
        return getOpcode5Field(getInst());
    }

    /**
     * Thumb 命令セットのデータ処理命令の op_5 フィールド（ビット [9:6]）を取得します。
     *
     * データ処理命令に存在し、命令が行う演算の内容を示します。
     *
     * @param inst Thumb 命令
     * @return オペコード
     */
    public static int getOpcode5Field(int inst) {
        return BitOp.getField32(inst, 6, 4);
    }

    /**
     * Thumb 命令セットのデータ処理命令の op_5 フィールド（ビット [9:6]）が示す、
     * 演算の名前を取得します。
     *
     * データ処理命令に存在し、命令が行う演算の内容を示します。
     *
     * @return opcode フィールドが示す演算の名前
     */
    public String getOpcode5FieldName() {
        return getOpcode5FieldName(getOpcode5Field());
    }

    /**
     * Thumb 命令セットのデータ処理命令の op_5 フィールド（ビット [9:6]）が示す、
     * 演算の名前を取得します。
     *
     * データ処理命令に存在し、命令が行う演算の内容を示します。
     *
     * @param opcode Thumb 命令のデータ処理命令の op_5 フィールド
     * @return opcode フィールドが示す演算の名前
     */
    public static String getOpcode5FieldName(int opcode) {
        final String[] names = {
                "and", "eor", "lsl", "lsr",
                "asr", "adc", "sbc", "ror",
                "tst", "neg", "cmp", "cmn",
                "orr", "mul", "bic", "mvn",
        };

        if (0 <= opcode && opcode <= 15) {
            return names[opcode];
        } else {
            throw new IllegalArgumentException("Invalid opcode " +
                    opcode + ".");
        }
    }

    /**
     * Thumb 命令の Rm フィールド（ビット [5:3]）を取得します。
     *
     * @return Rm フィールド
     */
    public int getRmField() {
        return getRmField(getInst());
    }

    /**
     * Thumb 命令の Rm フィールド（ビット [5:3]）を取得します。
     *
     * @param inst Thumb 命令
     * @return Rm フィールド
     */
    public static int getRmField(int inst) {
        return BitOp.getField32(inst, 3, 3);
    }

    /**
     * Thumb 命令の Rd フィールド（ビット [2:0]）を取得します。
     *
     * イミディエートシフト命令、レジスタ加算減算命令、
     * データ処理命令、特殊データ処理命令、
     * イミディエート加算減算命令、ロードストア命令、
     * に存在し、
     * データ格納先となるレジスタを示します。
     *
     * @return Rd フィールド
     */
    public int getRdField() {
        return getRdField(getInst());
    }

    /**
     * Thumb 命令の Rd フィールド（ビット [2:0]）を取得します。
     *
     * イミディエートシフト命令、レジスタ加算減算命令、
     * データ処理命令、特殊データ処理命令、
     * イミディエート加算減算命令、ロードストア命令、
     * に存在し、
     * データ格納先となるレジスタを示します。
     *
     * @param inst Thumb 命令
     * @return Rd フィールド
     */
    public static int getRdField(int inst) {
        return BitOp.getField32(inst, 0, 3);
    }

    /**
     * Thumb 命令のレジスタリストフィールド（ビット [7:0]）を取得します。
     *
     * ロードマルチプル、ストアマルチプル命令に存在し、
     * ロード、ストア対象となるレジスタの一覧を示します。
     *
     * @return レジスタリストフィールド
     */
    public int getRegListField() {
        return getRegListField(getInst());
    }

    /**
     * Thumb 命令のレジスタリストフィールド（ビット [7:0]）を取得します。
     *
     * ロードマルチプル、ストアマルチプル命令に存在し、
     * ロード、ストア対象となるレジスタの一覧を示します。
     *
     * @param inst Thumb 命令
     * @return レジスタリストフィールド
     */
    public static int getRegListField(int inst) {
        return BitOp.getField32(inst, 0, 8);
    }

    /**
     * Thumb 命令のレジスタリストフィールドの名前を取得します。
     *
     * ロードマルチプル、ストアマルチプル命令に存在し、
     * ロード、ストア対象となるレジスタの一覧を示します。
     *
     * @return レジスタリストに含まれるレジスタの名前一覧
     */
    public String getRegListFieldName() {
        return getRegListFieldName(getRegListField(), 8);
    }

    /**
     * 命令のレジスタリストフィールドの名前を取得します。
     *
     * このフィールドは、
     * ロードマルチプル、ストアマルチプル命令にのみ存在します。
     *
     * @param rlist レジスタリストフィールド
     * @param len   レジスタリストフィールドのビット長
     * @return レジスタリストに含まれるレジスタの名前一覧
     */
    public static String getRegListFieldName(int rlist, int len) {
        StringBuilder sb = new StringBuilder();
        int i, cnt;

        cnt = 0;
        for (i = 0; i < len; i++) {
            if (BitOp.getBit32(rlist, i)) {
                if (cnt != 0) {
                    sb.append(", ");
                }
                sb.append(String.format("r%d", i));
                cnt += 1;
            }
        }

        return sb.toString();
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
