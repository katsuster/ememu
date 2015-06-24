package net.katsuster.ememu.arm.core;

import net.katsuster.ememu.generic.BitOp;
import net.katsuster.ememu.generic.Instruction;

/**
 * Thumb 命令。
 *
 * @author katsuhiro
 */
public class InstructionThumb extends Instruction {
    public InstructionThumb(int inst) {
        super(inst & 0x0000ffff);
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
}
