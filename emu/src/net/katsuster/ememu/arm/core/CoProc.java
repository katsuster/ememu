package net.katsuster.ememu.arm.core;

import java.util.*;

import net.katsuster.ememu.generic.*;

/**
 * ARM コプロセッサ。
 *
 * @author katsuhiro
 */
public class CoProc {
    private int no;
    private ARMv5 cpu;
    private Map<Integer, Reg32> cregs;

    /**
     * コプロセッサを生成します。
     *
     * @param no  コプロセッサ番号
     * @param cpu コプロセッサが接続されている CPU
     */
    public CoProc(int no, ARMv5 cpu) {
        this.no = no;
        this.cpu = cpu;
        this.cregs = new HashMap<Integer, Reg32>();
    }

    /**
     * コプロセッサ番号を取得します。
     *
     * @return コプロセッサ番号
     */
    public int getNumber() {
        return no;
    }

    /**
     * コプロセッサが接続されている CPU を取得します。
     *
     * @return コプロセッサが接続されている CPU
     */
    public ARMv5 getCPU() {
        return cpu;
    }

    /**
     * コプロセッサレジスタの定義を追加します。
     *
     * コプロセッサレジスタ識別番号は、
     * 4ビットずつのフィールドに分かれています。
     * ビット [15:12]: CRn
     * ビット [11:8]: opcode_1
     * ビット [7:4]: CRm
     * ビット [3:0]: opcode_2
     * を意味します。
     *
     * @param cn   コプロセッサレジスタ識別番号
     * @param name レジスタ名
     */
    public void addCReg(int cn, String name) {
        cregs.put(cn, new Reg32(name, 0));
    }

    /**
     * コプロセッサレジスタの定義を追加します。
     *
     * コプロセッサレジスタ識別番号は、
     * 4ビットずつのフィールドに分かれています。
     * ビット [15:12]: CRn
     * ビット [11:8]: opcode_1
     * ビット [7:4]: CRm
     * ビット [3:0]: opcode_2
     * を意味します。
     *
     * @param cn   コプロセッサレジスタ識別番号
     * @param name レジスタ名
     * @param val  レジスタの初期値
     */
    public void addCReg(int cn, String name, int val) {
        cregs.put(cn, new Reg32(name, val));
    }

    /**
     * コプロセッサレジスタ識別番号を取得します。
     *
     * コプロセッサレジスタ識別番号は、
     * 4ビットずつのフィールドに分かれています。
     * ビット [15:12]: CRn
     * ビット [11:8]: opcode_1
     * ビット [7:4]: CRm
     * ビット [3:0]: opcode_2
     * を意味します。
     *
     * @param crn      命令の第一オペランドを含むコプロセッサレジスタ
     * @param opcode1 コプロセッサ命令（その1）
     * @param crm      命令の第二オペランド
     * @param opcode2 コプロセッサ命令（その2）
     * @return コプロセッサレジスタ識別番号
     */
    public static int getCRegID(int crn, int opcode1, int crm, int opcode2) {
        return ((crn & 0x0f) << 12) |
                ((opcode1 & 0x07) << 8) |
                ((crm & 0x0f) << 4) |
                ((opcode2 & 0x07) << 0);
    }

    /**
     * コプロセッサレジスタ識別番号が有効かどうかを取得します。
     *
     * @param cn コプロセッサレジスタ識別番号
     * @return 指定した識別番号のレジスタが存在すれば true、なければ false
     */
    public boolean isValidCRegNumber(int cn) {
        return cregs.containsKey(cn);
    }

    /**
     * コプロセッサレジスタの値を取得します。
     *
     * コプロセッサレジスタ識別番号は、
     * 4ビットずつのフィールドに分かれています。
     * ビット [15:12]: CRn
     * ビット [11:8]: opcode_1
     * ビット [7:4]: CRm
     * ビット [3:0]: opcode_2
     * を意味します。
     *
     * @param cn コプロセッサレジスタ識別番号
     * @return レジスタの値
     */
    public int getCReg(int cn) {
        Reg32 r;

        r = cregs.get(cn);
        if (r == null) {
            throw new IllegalArgumentException(String.format(
                    "Illegal coproc %d reg(%08x).", getNumber(), cn));
        }

        return r.getValue();
    }

    /**
     * コプロセッサレジスタの値を設定します。
     *
     * コプロセッサレジスタ識別番号は、
     * 4ビットずつのフィールドに分かれています。
     * ビット [15:12]: CRn
     * ビット [11:8]: opcode_1
     * ビット [7:4]: CRm
     * ビット [3:0]: opcode_2
     * を意味します。
     *
     * @param cn  コプロセッサレジスタ識別番号
     * @param val 新しいレジスタの値
     */
    public void setCReg(int cn, int val) {
        Reg32 r;

        r = cregs.get(cn);
        if (r == null) {
            throw new IllegalArgumentException(String.format(
                    "Illegal coproc %d reg(%08x).", getNumber(), cn));
        }

        r.setValue(val);
    }

    @Override
    public String toString() {
        return String.format("p%d", getNumber());
    }
}
