package net.katsuster.semu;

/**
 * CPU
 *
 * @author katsuhiro
 */
public class CPU extends MasterCore64 {
    private int[] regs;
    private int cpsr;
    private int spsr;

    private int modeDisasm;

    public CPU() {
        regs = new int[16];
    }

    public boolean isDisasmMode() {
        return modeDisasm != 0;
    }

    public int getDisasmMode() {
        return modeDisasm;
    }

    public void setDisasmMode(int m) {
        modeDisasm = m;
    }

    /**
     * レジスタ Rn の値を取得します。
     *
     * @param n レジスタ番号（0 ～ 15）
     * @return レジスタの値
     */
    public int getReg(int n) {
        return regs[n];
    }

    /**
     * レジスタ Rn の値を設定します。
     *
     * @param n   レジスタ番号（0 ～ 15）
     * @param val 新しいレジスタの値
     */
    public void setReg(int n, int val) {
        regs[n] = val;
    }

    /**
     * PC（プログラムカウンタ）の値を取得します。
     *
     * 下記の呼び出しと同一です。
     * getReg(15)
     *
     * @return PC の値
     */
    public int getPC() {
        return getReg(15);
    }

    /**
     * PC（プログラムカウンタ）の値を設定します。
     *
     * 下記の呼び出しと同一です。
     * setReg(15, val)
     *
     * @param val 新しい PC の値
     */
    public void setPC(int val) {
        setReg(15, val);
    }

    /**
     * CPSR（カレントプログラムステートレジスタ）の値を取得します。
     *
     * @return CPSR の値
     */
    public int getCPSR() {
        return cpsr;
    }

    /**
     * CPSR（カレントプログラムステートレジスタ）の値を設定します。
     *
     * @param val 新しい CPSR の値
     */
    public void setCPSR(int val) {
        cpsr = val;
    }

    /**
     * SPSR（保存されたプログラムステートレジスタ）の値を取得します。
     *
     * @return SPSR の値
     */
    public int getSPSR() {
        return spsr;
    }

    /**
     * SPSR（保存されたプログラムステートレジスタ）の値を設定します。
     *
     * @param val 新しい SPSR の値
     */
    public void setSPSR(int val) {
        spsr = val;
    }

    /**
     * PSR（プログラムステートレジスタ）の状態を表す文字列を取得します。
     *
     * @param val PSR の値
     * @return PSR の状態を表す文字列
     */
    public static String getPSRName(int val) {
        return String.format("%s%s%s%s_%s%s%s%5s",
                (getPSR_N(val) == 1) ? "N" : "n",
                (getPSR_Z(val) == 1) ? "Z" : "z",
                (getPSR_C(val) == 1) ? "C" : "c",
                (getPSR_V(val) == 1) ? "V" : "v",
                (getPSR_I(val) == 1) ? "I" : "i",
                (getPSR_F(val) == 1) ? "F" : "f",
                (getPSR_T(val) == 1) ? "T" : "t",
                getPSR_ModeName(getPSR_Mode(val)));
    }

    /**
     * PSR（プログラムステートレジスタ）の N ビット（ビット 31）を取得します。
     *
     * N ビットは演算結果のビット 31 が 1 の場合に設定されます。
     * すなわち演算結果を 2の補数の符号付き整数としてみたとき、
     * 演算結果が正の数であれば N=0、負の数であれば N=1 となります。
     *
     * @param val PSR の値
     * @return N ビットの値
     */
    public static int getPSR_N(int val) {
        return (val >> 31) & 0x1;
    }

    /**
     * PSR（プログラムステートレジスタ）の Z ビット（ビット 30）を取得します。
     *
     * Z ビットは演算結果が 0 の場合に設定されます。
     * 演算結果が 0 以外ならば Z=0、0 ならば Z=1 となります。
     *
     * @param val PSR の値
     * @return Z ビットの値
     */
    public static int getPSR_Z(int val) {
        return (val >> 30) & 0x1;
    }

    /**
     * PSR（プログラムステートレジスタ）の C ビット（ビット 29）を取得します。
     *
     * C ビットは演算結果にキャリー（加算の場合）が生じた場合に設定され、
     * ボロー（減算の場合）が生じた場合にクリアされます。
     * または、シフト演算によりあふれた値が設定されます。
     *
     * - 演算が加算で、
     * 演算によりキャリーが生じなければ C=0、
     * 符号無しオーバーフローしキャリーが生じたならば C=1 となります。
     * - 演算が減算で、
     * 演算により符号無しアンダーフローしボローが生じれば C=0、
     * ボローが生じなければ C=1 となります。
     * - 演算がシフトで、演算によりシフトアウトされた値が 0 ならば C=0、
     * シフトアウトされた値が 1 ならば C=1 となります。
     *
     * @param val PSR の値
     * @return C ビットの値
     */
    public static int getPSR_C(int val) {
        return (val >> 29) & 0x1;
    }

    /**
     * PSR（プログラムステートレジスタ）の V ビット（ビット 28）を取得します。
     *
     * V ビットは演算結果に符号付きオーバーフローした場合に設定されます。
     *
     * - 演算が加算または減算で、
     * 演算により符号付きオーバーフローしなければ V=0、
     * 符号付きオーバーフローしたならば V=1 となります。
     *
     * @param val PSR の値
     * @return V ビットの値
     */
    public static int getPSR_V(int val) {
        return (val >> 28) & 0x1;
    }

    /**
     * PSR（プログラムステートレジスタ）の I ビット（ビット 7）を取得します。
     *
     * I=0 ならば IRQ 割り込みが有効となります。
     * I=1 ならば IRQ 割り込みが無効となります。
     *
     * @param val PSR の値
     * @return I ビットの値
     */
    public static int getPSR_I(int val) {
        return (val >> 7) & 0x1;
    }

    /**
     * PSR（プログラムステートレジスタ）の F ビット（ビット 6）を取得します。
     *
     * F=0 ならば FIQ 割り込みが有効となります。
     * F=1 ならば FIQ 割り込みが無効となります。
     *
     * @param val PSR の値
     * @return F ビットの値
     */
    public static int getPSR_F(int val) {
        return (val >> 6) & 0x1;
    }

    /**
     * PSR（プログラムステートレジスタ）の T ビット（ビット 5）を取得します。
     *
     * T=0 ならば ARM 命令を実行します。
     * T=1 ならば Thumb 命令を実行します。
     *
     * ARMv5 以上の非 T バリアント（Thumb 命令非対応）の場合、
     * T=1 ならば次に実行される命令で未定義命令例外を発生させます。
     *
     * @param val PSR の値
     * @return T ビットの値
     */
    public static int getPSR_T(int val) {
        return (val >> 5) & 0x1;
    }

    /**
     * PSR（プログラムステートレジスタ）の M フィールド
     * （ビット [4:0]）を取得します。
     *
     * @param val PSR の値
     * @return M フィールドの値
     */
    public static int getPSR_Mode(int val) {
        return val & 0x1f;
    }

    /**
     * プロセッサの動作モードの名前を取得します。
     *
     * @param mode プロセッサの動作モード
     * @return 動作モードの名前
     */
    public static String getPSR_ModeName(int mode) {
        switch (mode) {
        case 0x10:
            return "usr";
        case 0x11:
            return "fiq";
        case 0x12:
            return "irq";
        case 0x13:
            return "svc";
        case 0x17:
            return "abt";
        case 0x1b:
            return "und";
        case 0x1f:
            return "sys";
        default:
            return "???";
        }
    }

    /**
     * 現在のプロセッサの動作モードを取得します。
     *
     * CPSR の M フィールド（ビット[4:0]）を返します。
     *
     * @return プロセッサの動作モード
     */
    public int getProcessorMode() {
        return getPSR_Mode(getCPSR());
    }

    public static final int COND_EQ = 0;
    public static final int COND_NE = 1;
    public static final int COND_CS = 2;
    public static final int COND_HS = 2;
    public static final int COND_CC = 3;
    public static final int COND_LO = 3;
    public static final int COND_MI = 4;
    public static final int COND_PL = 5;
    public static final int COND_VS = 6;
    public static final int COND_VC = 7;
    public static final int COND_HI = 8;
    public static final int COND_LS = 9;
    public static final int COND_GE = 10;
    public static final int COND_LT = 11;
    public static final int COND_GT = 12;
    public static final int COND_LE = 13;
    public static final int COND_AL = 14;
    public static final int COND_NV = 15;

    /**
     * ARM 命令セットの cond フィールド（31:28ビット）を取得します。
     *
     * @param op ARM 命令
     * @return cond フィールド
     */
    public static int getCond(int op) {
        return (op >> 28) & 0xf;
    }

    /**
     * ARM 命令セットの cond フィールドの名前を取得します。
     *
     * AL の場合は空の文字列を返します。
     *
     * @param cond ARM 命令の cond フィールド
     * @return cond フィールドの名前
     */
    public static String getCondName(int cond) {
        final String[] names = {
                "eq", "ne", "cs", "cc",
                "mi", "pl", "vs", "vc",
                "hi", "ls", "ge", "lt",
                "gt", "le", "", "nv",
        };

        if (0 <= cond && cond <= 15) {
            return names[cond];
        } else {
            throw new IllegalArgumentException("Invalid cond " +
                    cond + ".");
        }
    }

    /**
     * ARM 命令セットのオペコードフィールド（27:20ビット）を取得します。
     *
     * @param op ARM 命令
     * @return cond フィールド
     */
    public static int getOpcode(int op) {
        return (op >> 20) & 0xff;
    }

    public static final int OP_ADDSFT = 0;
    public static final int OP_MSRREG = 1;
    public static final int OP_ADDIMM = 2;
    public static final int OP_MSRIMM = 3;
    public static final int OP_LDRIMM = 4;
    public static final int OP_LDRREG = 6;
    public static final int OP_LDMSTM = 8;
    public static final int OP_BL_BLX = 10;
    public static final int OP_LDCSTC = 12;
    public static final int OP_MCRMRC = 14;

    public static final int[] optable = {
            //0b000_00000: データ処理
            //  0b000_10xx0: ステータスレジスタへレジスタ転送（16, 18, 20, 22）
            OP_ADDSFT, OP_ADDSFT, OP_ADDSFT, OP_ADDSFT,
            OP_ADDSFT, OP_ADDSFT, OP_ADDSFT, OP_ADDSFT,
            OP_ADDSFT, OP_ADDSFT, OP_ADDSFT, OP_ADDSFT,
            OP_ADDSFT, OP_ADDSFT, OP_ADDSFT, OP_ADDSFT,

            OP_MSRREG, OP_ADDSFT, OP_MSRREG, OP_ADDSFT,
            OP_MSRREG, OP_ADDSFT, OP_MSRREG, OP_ADDSFT,
            OP_ADDSFT, OP_ADDSFT, OP_ADDSFT, OP_ADDSFT,
            OP_ADDSFT, OP_ADDSFT, OP_ADDSFT, OP_ADDSFT,

            //0b001_00000
            //  0b001_10x10: ステータスレジスタへの即値転送（50, 54）
            OP_ADDIMM, OP_ADDIMM, OP_ADDIMM, OP_ADDIMM,
            OP_ADDIMM, OP_ADDIMM, OP_ADDIMM, OP_ADDIMM,
            OP_ADDIMM, OP_ADDIMM, OP_ADDIMM, OP_ADDIMM,
            OP_ADDIMM, OP_ADDIMM, OP_ADDIMM, OP_ADDIMM,

            OP_ADDIMM, OP_ADDIMM, OP_MSRIMM, OP_ADDIMM,
            OP_ADDIMM, OP_ADDIMM, OP_MSRIMM, OP_ADDIMM,
            OP_ADDIMM, OP_ADDIMM, OP_ADDIMM, OP_ADDIMM,
            OP_ADDIMM, OP_ADDIMM, OP_ADDIMM, OP_ADDIMM,

            //0b010_00000
            OP_LDRIMM, OP_LDRIMM, OP_LDRIMM, OP_LDRIMM,
            OP_LDRIMM, OP_LDRIMM, OP_LDRIMM, OP_LDRIMM,
            OP_LDRIMM, OP_LDRIMM, OP_LDRIMM, OP_LDRIMM,
            OP_LDRIMM, OP_LDRIMM, OP_LDRIMM, OP_LDRIMM,

            OP_LDRIMM, OP_LDRIMM, OP_LDRIMM, OP_LDRIMM,
            OP_LDRIMM, OP_LDRIMM, OP_LDRIMM, OP_LDRIMM,
            OP_LDRIMM, OP_LDRIMM, OP_LDRIMM, OP_LDRIMM,
            OP_LDRIMM, OP_LDRIMM, OP_LDRIMM, OP_LDRIMM,

            //0b011_00000
            OP_LDRREG, OP_LDRREG, OP_LDRREG, OP_LDRREG,
            OP_LDRREG, OP_LDRREG, OP_LDRREG, OP_LDRREG,
            OP_LDRREG, OP_LDRREG, OP_LDRREG, OP_LDRREG,
            OP_LDRREG, OP_LDRREG, OP_LDRREG, OP_LDRREG,

            OP_LDRREG, OP_LDRREG, OP_LDRREG, OP_LDRREG,
            OP_LDRREG, OP_LDRREG, OP_LDRREG, OP_LDRREG,
            OP_LDRREG, OP_LDRREG, OP_LDRREG, OP_LDRREG,
            OP_LDRREG, OP_LDRREG, OP_LDRREG, OP_LDRREG,

            //0b100_00000
            OP_LDMSTM, OP_LDMSTM, OP_LDMSTM, OP_LDMSTM,
            OP_LDMSTM, OP_LDMSTM, OP_LDMSTM, OP_LDMSTM,
            OP_LDMSTM, OP_LDMSTM, OP_LDMSTM, OP_LDMSTM,
            OP_LDMSTM, OP_LDMSTM, OP_LDMSTM, OP_LDMSTM,

            OP_LDMSTM, OP_LDMSTM, OP_LDMSTM, OP_LDMSTM,
            OP_LDMSTM, OP_LDMSTM, OP_LDMSTM, OP_LDMSTM,
            OP_LDMSTM, OP_LDMSTM, OP_LDMSTM, OP_LDMSTM,
            OP_LDMSTM, OP_LDMSTM, OP_LDMSTM, OP_LDMSTM,

            //0b101_00000
            OP_BL_BLX, OP_BL_BLX, OP_BL_BLX, OP_BL_BLX,
            OP_BL_BLX, OP_BL_BLX, OP_BL_BLX, OP_BL_BLX,
            OP_BL_BLX, OP_BL_BLX, OP_BL_BLX, OP_BL_BLX,
            OP_BL_BLX, OP_BL_BLX, OP_BL_BLX, OP_BL_BLX,

            OP_BL_BLX, OP_BL_BLX, OP_BL_BLX, OP_BL_BLX,
            OP_BL_BLX, OP_BL_BLX, OP_BL_BLX, OP_BL_BLX,
            OP_BL_BLX, OP_BL_BLX, OP_BL_BLX, OP_BL_BLX,
            OP_BL_BLX, OP_BL_BLX, OP_BL_BLX, OP_BL_BLX,

            //0b110_00000
            OP_LDCSTC, OP_LDCSTC, OP_LDCSTC, OP_LDCSTC,
            OP_LDCSTC, OP_LDCSTC, OP_LDCSTC, OP_LDCSTC,
            OP_LDCSTC, OP_LDCSTC, OP_LDCSTC, OP_LDCSTC,
            OP_LDCSTC, OP_LDCSTC, OP_LDCSTC, OP_LDCSTC,

            OP_LDCSTC, OP_LDCSTC, OP_LDCSTC, OP_LDCSTC,
            OP_LDCSTC, OP_LDCSTC, OP_LDCSTC, OP_LDCSTC,
            OP_LDCSTC, OP_LDCSTC, OP_LDCSTC, OP_LDCSTC,
            OP_LDCSTC, OP_LDCSTC, OP_LDCSTC, OP_LDCSTC,

            //0b111_00000
            OP_MCRMRC, OP_MCRMRC, OP_MCRMRC, OP_MCRMRC,
            OP_MCRMRC, OP_MCRMRC, OP_MCRMRC, OP_MCRMRC,
            OP_MCRMRC, OP_MCRMRC, OP_MCRMRC, OP_MCRMRC,
            OP_MCRMRC, OP_MCRMRC, OP_MCRMRC, OP_MCRMRC,

            OP_MCRMRC, OP_MCRMRC, OP_MCRMRC, OP_MCRMRC,
            OP_MCRMRC, OP_MCRMRC, OP_MCRMRC, OP_MCRMRC,
            OP_MCRMRC, OP_MCRMRC, OP_MCRMRC, OP_MCRMRC,
            OP_MCRMRC, OP_MCRMRC, OP_MCRMRC, OP_MCRMRC,
    };

    public void executeAddSft(int op, int opcode, int cond) {

    }

    public void executeMrsReg(int op, int opcode, int cond) {
        op = op;
    }

    public void executeAddImm(int op, int opcode, int cond) {

    }

    public void executeMsrImm(int op, int opcode, int cond) {
        int flag_r = (op >> 22) & 0x1;
        int fmask_f = (op >> 19) & 0x1;
        int fmask_s = (op >> 18) & 0x1;
        int fmask_x = (op >> 17) & 0x1;
        int fmask_c = (op >> 16) & 0x1;
        int sbo = (op >> 12) & 0xf;
        int rotr = (op >> 8) & 0xf;
        int imm8 = op & 0xff;
        int imm = Integer.rotateRight(imm8, rotr * 2);
        int v, m;

        if (sbo != 0xf) {
            throw new IllegalStateException("Illegal instruction, " +
                    String.format("SBO[15:12](0x%01x) of MSR has zero.",
                            sbo));
        }

        if (flag_r == 0) {
            v = getCPSR();
        } else {
            v = getSPSR();
        }

        m = 0;
        if (fmask_c == 1) {
            m |= 0x000000ff;
        }
        if (fmask_x == 1) {
            m |= 0x0000ff00;
        }
        if (fmask_s == 1) {
            m |= 0x00ff0000;
        }
        if (fmask_f == 1) {
            m |= 0xff000000;
        }
        v &= ~m;
        v |= imm & m;

        if (flag_r == 0) {
            setCPSR(v);
        } else {
            setSPSR(v);
        }

        if (isDisasmMode()) {
            System.out.println(String.format(
                    "%08x: %08x  msr%s %s_%s%s%s%s, #%d    ; 0x%x",
                    getPC(), op,
                    getCondName(cond),
                    (flag_r == 1) ? "SPSR" : "CPSR",
                    (fmask_f == 1) ? "f" : "",
                    (fmask_s == 1) ? "s" : "",
                    (fmask_x == 1) ? "x" : "",
                    (fmask_c == 1) ? "c" : "",
                    imm, imm));
        }
    }

    public void execute(int op) {
        int cond = getCond(op);
        int opcode = getOpcode(op);
        int sub = optable[opcode];

        switch (sub) {
        case OP_ADDSFT:
            executeAddSft(op, opcode, cond);
            break;
        case OP_MSRREG:
            executeMrsReg(op, opcode, cond);
            break;
        case OP_ADDIMM:
            executeAddImm(op, opcode, cond);
            break;
        case OP_MSRIMM:
            executeMsrImm(op, opcode, cond);
            break;
        case OP_LDRIMM:
            break;
        case OP_LDRREG:
            break;
        case OP_LDMSTM:
            break;
        case OP_BL_BLX:
            break;
        case OP_LDCSTC:
            break;
        case OP_MCRMRC:
            break;
        default:
            throw new IllegalStateException("Unknown sub execute no." +
                    sub + ".");
        }

        //System.out.println(getCondName(cond));
    }

    public void run() {
        int v;

        while (true) {
            v = read32(getPC());
            try {
                execute(v);
            } catch (IllegalStateException e) {
                System.out.println(String.format("%08x: %s", getPC(), e));
                //ignore
            }

            setPC(getPC() + 4);
        }
    }
}
