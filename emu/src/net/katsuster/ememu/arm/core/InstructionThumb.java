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
