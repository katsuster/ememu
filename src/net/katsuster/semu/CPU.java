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
    private CoProc[] coprocs;

    private boolean jumped;

    private int modeDisasm;

    public CPU() {
        int i;

        regs = new int[16];
        coprocs = new CoProc[16];
        coprocs[15] = new StdCoProc(15, this);
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

    public void printDisasm(int op, String operation, String operand) {
        System.out.printf("%08x:    %08x    %-7s %s\n",
                getPC() - 8, op, operation, operand);
    }

    /**
     * 符号拡張を行います。
     *
     * @param v 任意の値
     * @param n 値のビット数
     */
    public static long signext(long v, int n) {
        long sb, mb;

        if (n == 0) {
            return 0;
        }

        sb = 1L << (n - 1);
        mb = (-1L << (n - 1)) << 1;
        v &= ~mb;
        if ((v & sb) != 0) {
            v = mb + v;
        }

        return v;
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
     * レジスタ Rn の名前を取得します。
     *
     * @param n レジスタ番号（0 ～ 15）
     * @return レジスタの名前
     */
    public static String getRegName(int n) {
        return String.format("r%d", n);
    }

    /**
     * コプロセッサ Pn の名前を取得します。
     *
     * @param cpnum コプロセッサ番号
     * @return コプロセッサの名前
     */
    public static String getCoprocName(int cpnum) {
        return String.format("p%d", cpnum);
    }

    /**
     * コプロセッサレジスタ CRn の名前を取得します。
     *
     * @param cpnum コプロセッサ番号
     * @param n コプロセッサレジスタ番号（0 ～ 7）
     * @return コプロセッサレジスタの名前
     */
    public static String getCoprocRegName(int cpnum, int n) {
        return String.format("cr%d", n);
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
     * 指定したアドレスに絶対ジャンプします。
     *
     * PC には次に実行する命令のアドレス +8 を格納します。
     *
     * また命令実行後は自動的に PC に 4 が加算されますが、
     * ジャンプ後は加算が実行されません。
     *
     * @param val 次に実行する命令のアドレス
     */
    public void jumpAbs(int val) {
        setPC(val + 8);
        setJumped(true);
    }

    /**
     * 指定したアドレス分だけ相対ジャンプします。
     *
     * PC（実行中の命令のアドレス +8）+ 相対アドレス + 8 を、
     * 新たな PC として設定します。
     *
     * また命令実行後は自動的に PC に 4 が加算されますが、
     * ジャンプ後は加算が実行されません。
     *
     * @param val 次に実行する命令の相対アドレス
     */
    public void jumpRel(int val) {
        setPC(getPC() + val + 8);
        setJumped(true);
    }

    /**
     * ジャンプが行われたかどうかを取得します。
     *
     * @return ジャンプが行われたならば true、そうでなければ false
     */
    public boolean isJumped() {
        return jumped;
    }

    /**
     * ジャンプが行われたかどうかを設定します。
     *
     * @param b ジャンプが行われたならば true、そうでなければ false
     */
    public void setJumped(boolean b) {
        jumped = b;
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
     * APSR（アプリケーションプログラムステートレジスタ）の値を取得します。
     *
     * @return APSR の値
     */
    public int getAPSR() {
        return getCPSR() & 0xf80f0000;
    }

    /**
     * APSR（アプリケーションプログラムステートレジスタ）の値を設定します。
     *
     * @param val 新しい APSR の値
     */
    public void setAPSR(int val) {
        int r;

        //N, Z, C, V, Q, GE のみ変更可能
        r = getCPSR();
        r &= ~0xf80f0000;
        r |= val & 0xf80f0000;
        setCPSR(r);
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

    public static final int MODE_USR = 0x10;
    public static final int MODE_FIQ = 0x11;
    public static final int MODE_IRQ = 0x12;
    public static final int MODE_SVC = 0x13;
    public static final int MODE_ABT = 0x17;
    public static final int MODE_UND = 0x1b;
    public static final int MODE_SYS = 0x1f;

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
     * ステータスレジスタの値が条件 cond を満たしているかどうか判定します。
     *
     * cond が NV の場合は常に true を返し、条件の判定は行いません。
     * 各命令ごとに適切な判定を行って下さい。
     *
     * @param cond 条件オペコード
     * @param psr  プログラムステータスレジスタの値
     * @return 条件を満たしていれば true、満たしていなければ false
     */
    public static boolean satisfiesCondition(int cond, int psr) {
        switch (cond) {
        case COND_EQ:
            return getPSR_Z(psr) == 1;
        case COND_NE:
            return getPSR_Z(psr) == 0;
        case COND_CS:
            return getPSR_C(psr) == 1;
        case COND_CC:
            return getPSR_C(psr) == 0;
        case COND_MI:
            return getPSR_N(psr) == 1;
        case COND_PL:
            return getPSR_N(psr) == 0;
        case COND_VS:
            return getPSR_V(psr) == 1;
        case COND_VC:
            return getPSR_V(psr) == 0;
        case COND_HI:
            return (getPSR_C(psr) == 1) && (getPSR_Z(psr) == 0);
        case COND_LS:
            return (getPSR_C(psr) == 0) || (getPSR_Z(psr) == 1);
        case COND_GE:
            return getPSR_N(psr) == getPSR_V(psr);
        case COND_LT:
            return getPSR_N(psr) != getPSR_V(psr);
        case COND_GT:
            return (getPSR_Z(psr) == 0) && (getPSR_N(psr) == getPSR_V(psr));
        case COND_LE:
            return (getPSR_Z(psr) == 1) || (getPSR_N(psr) != getPSR_V(psr));
        case COND_AL:
        case COND_NV:
            return true;
        default:
            throw new IllegalArgumentException(String.format(
                    "Unknown cond %d, psr 0x%08x.", cond, psr));
        }
    }

    /**
     * ARM 命令セットのオペコードフィールド（27:20ビット）を取得します。
     *
     * @param op ARM 命令
     * @return cond フィールド
     */
    public static int getSubcode(int op) {
        return (op >> 20) & 0xff;
    }

    public static final int OP_ADDSFT = 0;
    public static final int OP_MRSREG = 1;
    public static final int OP_MSRREG = 2;
    public static final int OP_ANDIMM = 100;
    public static final int OP_EORIMM = 101;
    public static final int OP_SUBIMM = 102;
    public static final int OP_RSBIMM = 103;
    public static final int OP_ADDIMM = 104;
    public static final int OP_ADCIMM = 105;
    public static final int OP_SBCIMM = 106;
    public static final int OP_RSCIMM = 107;
    public static final int OP_TSTIMM = 108;
    public static final int OP_TEQIMM = 109;
    public static final int OP_CMPIMM = 110;
    public static final int OP_CMNIMM = 111;
    public static final int OP_ORRIMM = 112;
    public static final int OP_MOVIMM = 113;
    public static final int OP_BICIMM = 114;
    public static final int OP_MVNIMM = 115;
    public static final int OP_UNDIMM = 116;
    public static final int OP_MSRIMM = 117;
    public static final int OP_LDRIMM = 6;
    public static final int OP_LDRREG = 7;
    public static final int OP_LDMSTM = 8;
    public static final int OP_BL_BLX = 10;
    public static final int OP_LDCSTC = 12;
    public static final int OP_CDPMCR = 14;
    public static final int OP_CDPMRC = 15;
    public static final int OP_SWIIMM = 16;

    public static final int[] optable = {
            //0b000_00000: データ処理
            //  0b000_10x00: mrs ステータスレジスタへレジスタ転送
            //               16, 20
            //  0b000_10x10: msr ステータスレジスタへレジスタ転送
            //               18, 22
            OP_ADDSFT, OP_ADDSFT, OP_ADDSFT, OP_ADDSFT,
            OP_ADDSFT, OP_ADDSFT, OP_ADDSFT, OP_ADDSFT,
            OP_ADDSFT, OP_ADDSFT, OP_ADDSFT, OP_ADDSFT,
            OP_ADDSFT, OP_ADDSFT, OP_ADDSFT, OP_ADDSFT,

            OP_MRSREG, OP_ADDSFT, OP_MSRREG, OP_ADDSFT,
            OP_MRSREG, OP_ADDSFT, OP_MSRREG, OP_ADDSFT,
            OP_ADDSFT, OP_ADDSFT, OP_ADDSFT, OP_ADDSFT,
            OP_ADDSFT, OP_ADDSFT, OP_ADDSFT, OP_ADDSFT,

            //0b001_00000
            //  0b001_0000x: and 32, 33
            //  0b001_0001x: eor 34, 35
            //  0b001_0010x: sub 36, 37
            //  0b001_0011x: rsb 38, 39
            //  0b001_0100x: add 40, 41
            //  0b001_0101x: adc 42, 43
            //  0b001_0110x: sbc 44, 45
            //  0b001_0111x: rsc 46, 47
            //  0b001_10001: tst 49
            //  0b001_10011: teq 51
            //  0b001_10101: cmp 53
            //  0b001_10111: cmn 55
            //  0b001_1100x: orr 56, 57
            //  0b001_1101x: mov 58, 59
            //  0b001_1110x: bic 60, 61
            //  0b001_1111x: mvn 62, 63
            //  0b001_10x10: und 未定義命令
            //               48, 52
            //  0b001_10x10: msr ステータスレジスタへ即値転送
            //               50, 54
            OP_ANDIMM, OP_ANDIMM, OP_EORIMM, OP_EORIMM,
            OP_SUBIMM, OP_SUBIMM, OP_RSBIMM, OP_RSBIMM,
            OP_ADDIMM, OP_ADDIMM, OP_ADCIMM, OP_ADCIMM,
            OP_SBCIMM, OP_SBCIMM, OP_RSCIMM, OP_RSCIMM,

            OP_UNDIMM, OP_TSTIMM, OP_MSRIMM, OP_TEQIMM,
            OP_UNDIMM, OP_CMPIMM, OP_MSRIMM, OP_CMNIMM,
            OP_ORRIMM, OP_ORRIMM, OP_MOVIMM, OP_MOVIMM,
            OP_BICIMM, OP_BICIMM, OP_MVNIMM, OP_MVNIMM,

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
            //  0b1110xxx0: cdp コプロセッサデータ処理
            //              mrc コプロセッサから ARM レジスタへ転送
            //              224, 226, 228, 230, 232, 234, 236, 238
            //  0b1110xxx1: cdp コプロセッサデータ処理
            //              mrc コプロセッサから ARM レジスタへ転送
            //              225, 227, 229, 231, 233, 235, 237, 239
            //  0b1111xxxx: swi ソフトウェア割り込み
            //              240, ..., 255
            OP_CDPMCR, OP_CDPMRC, OP_CDPMCR, OP_CDPMRC,
            OP_CDPMCR, OP_CDPMRC, OP_CDPMCR, OP_CDPMRC,
            OP_CDPMCR, OP_CDPMRC, OP_CDPMCR, OP_CDPMRC,
            OP_CDPMCR, OP_CDPMRC, OP_CDPMCR, OP_CDPMRC,

            OP_SWIIMM, OP_SWIIMM, OP_SWIIMM, OP_SWIIMM,
            OP_SWIIMM, OP_SWIIMM, OP_SWIIMM, OP_SWIIMM,
            OP_SWIIMM, OP_SWIIMM, OP_SWIIMM, OP_SWIIMM,
            OP_SWIIMM, OP_SWIIMM, OP_SWIIMM, OP_SWIIMM,
    };

    /**
     * データ処理オペランドの 32ビットイミディエートを取得します。
     *
     * rotate_imm: ビット[11:8]
     * immed_8: ビット[7:0]
     * とすると、イミディエート imm32 は下記のように求められます。
     *
     * imm32 = rotateRight(immed_8, rotate_imm * 2)
     *
     * @param op 命令コード
     * @return イミディエート
     */
    public static int getOperandImm32(int op) {
        int rotR = (op >> 8) & 0xf;
        int imm8 = op & 0xff;

        return Integer.rotateRight(imm8, rotR * 2);
    }

    public void executeAddSft(int op, int cond, int subcode) {

    }

    public void executeMrsReg(int op, int cond, int subcode) {
        op = op;
    }

    public void executeMsrReg(int op, int cond, int subcode) {
        op = op;
    }

    public void executeAddImm(int op, int cond, int subcode) {
        int st = (op >> 20) & 0xf;
        int rn = (op >> 16) & 0xf;
        int rd = (op >> 12) & 0xf;
        int imm32 = getOperandImm32(op);

        if (isDisasmMode()) {
            printDisasm(op,
                    String.format("add%s%s", getCondName(cond),
                            (st == 1) ? "s" : ""),
                    String.format("r%d, r%d, #%d    ; 0x%x",
                            rd, rn, imm32, imm32));
        }

        if (!satisfiesCondition(cond, getCPSR())) {
            return;
        }

        setReg(rd, getReg(rn) + imm32);
        if (st == 1 && rd == 15) {
            setCPSR(getSPSR());
        } else if (st == 1) {
            //TODO: set flags
        }
    }

    public void executeMsrImm(int op, int cond, int subcode) {
        int flag_r = (op >> 22) & 0x1;
        int mask_f = (op >> 19) & 0x1;
        int mask_s = (op >> 18) & 0x1;
        int mask_x = (op >> 17) & 0x1;
        int mask_c = (op >> 16) & 0x1;
        int sbo = (op >> 12) & 0xf;
        int imm32 = getOperandImm32(op);
        int v, m = 0;

        if (isDisasmMode()) {
            printDisasm(op,
                    String.format("msr%s", getCondName(cond)),
                    String.format("%s_%s%s%s%s, #%d    ; 0x%x",
                            (flag_r == 1) ? "SPSR" : "CPSR",
                            (mask_f == 1) ? "f" : "",
                            (mask_s == 1) ? "s" : "",
                            (mask_x == 1) ? "x" : "",
                            (mask_c == 1) ? "c" : "",
                            imm32, imm32));
        }

        if (!satisfiesCondition(cond, getCPSR())) {
            return;
        }

        if (sbo != 0xf) {
            System.out.println("Warning: Illegal instruction, " +
                    String.format("msr SBO[15:12](0x%01x) has zero.", sbo));
        }

        if (flag_r == 0) {
            v = getCPSR();
        } else {
            v = getSPSR();
        }

        if (mask_c == 1) {
            m |= 0x000000ff;
        }
        if (mask_x == 1) {
            m |= 0x0000ff00;
        }
        if (mask_s == 1) {
            m |= 0x00ff0000;
        }
        if (mask_f == 1) {
            m |= 0xff000000;
        }
        v &= ~m;
        v |= imm32 & m;

        if (flag_r == 0) {
            setCPSR(v);
        } else {
            setSPSR(v);
        }
    }

    public void executeBlBlx(int op, int cond, int subcode) {
        int l = (op >> 24) & 0x1;
        int imm24 = op & 0xffffff;
        int simm24 = (int)signext(imm24, 24) << 2;

        //cond = 0b1111 ならば blx 命令
        if (cond == COND_NV) {
            executeBlx(op, cond, subcode);
            return;
        }

        if (isDisasmMode()) {
            printDisasm(op,
                    String.format("b%s%s",
                            (l == 1) ? "l" : "",
                            getCondName(cond)),
                    String.format("%08x", getPC() + simm24));
        }

        if (!satisfiesCondition(cond, getCPSR())) {
            return;
        }

        if (l == 1) {
            setReg(14, getPC() - 4);
        }
        jumpRel(simm24);
    }

    public void executeBlx(int op, int cond, int subcode) {
        int h = (op >> 24) & 0x1;
        int imm24 = op & 0xffffff;
        int simm24 = (int)signext(imm24, 24) << 2;
        int psr;

        if (isDisasmMode()) {
            printDisasm(op,
                    String.format("blx"),
                    String.format("%08x", getPC() + simm24 + (h << 1)));
        }

        //blx は条件判定不可です

        setReg(14, getPC() - 4);
        //T ビットをセット
        setCPSR(getCPSR() | 0x20);
        jumpRel(simm24 + (h << 1));

        throw new IllegalStateException("not support blx.");
    }

    public void executeLdcStc(int op, int cond, int subcode) {

    }

    public void executeCdpMcr(int op, int cond, int subcode) {
        int bit4 = (op >> 4) & 0x1;

        //ビット 4 が 0 ならば cdp 命令, 1 ならば mcr 命令
        if (bit4 == 0) {
            executeCdp(op, cond, subcode);
            return;
        }
    }

    public void executeCdpMrc(int op, int cond, int subcode) {
        int opcode1 = (op >> 21) & 0x7;
        int crn = (op >> 16) & 0xf;
        int rd = (op >> 12) & 0xf;
        int cpnum = (op >> 8) & 0xf;
        int opcode2 = (op >> 5) & 0x7;
        int bit4 = (op >> 4) & 0x1;
        int crm = op & 0xf;
        CoProc cp;
        int crid, crval, rval;

        //ビット 4 が 0 ならば cdp 命令, 1 ならば mrc 命令
        if (bit4 == 0) {
            executeCdp(op, cond, subcode);
            return;
        }

        if (isDisasmMode()) {
            printDisasm(op,
                    String.format("mrc%s", getCondName(cond)),
                    String.format("%s, %d, %s, %s, %s, {%d}",
                            getCoprocName(cpnum), opcode1, getRegName(rd),
                            getCoprocRegName(cpnum, crn), getCoprocRegName(cpnum, crm),
                            opcode2));
        }

        if (!satisfiesCondition(cond, getCPSR())) {
            return;
        }

        cp = coprocs[cpnum];
        if (cp == null) {
            exceptionInst("Unimplemented coprocessor, " +
                    String.format("p%d selected.", cpnum));
            return;
        }

        crid = CoProc.getCRegID(crn, opcode1, crm, opcode2);
        if (!cp.validCRegNumber(crid)) {
            exceptionInst("Unimplemented coprocessor register, " +
                    String.format("p%d id(%08x, crn:%d, opc1:%d, crm:%d, opc2:%d) selected.",
                            cpnum, crid, crn, opcode1, crm, opcode2));
            return;

        }

        crval = cp.getCReg(crid);
        if (rd == 15) {
            //r15 の場合 r15 を変更せず、APSR の N, Z, C, V ビットを変更する
            rval = getSPSR();
            rval &= ~0xf0000000;
            rval |= crval & 0xf0000000;
            setAPSR(rval);
        } else {
            setReg(rd, crval);
        }
    }

    public void executeCdp(int op, int cond, int subcode) {

    }

    public void executeSwiImm(int op, int cond, int subcode) {

    }

    public void execute(int op) {
        int cond = getCond(op);
        int subcode = getSubcode(op);
        int subcodeId = optable[subcode];

        switch (subcodeId) {
        case OP_ADDSFT:
            executeAddSft(op, cond, subcode);
            break;
        case OP_MRSREG:
            executeMrsReg(op, cond, subcode);
            break;
        case OP_MSRREG:
            executeMsrReg(op, cond, subcode);
            break;
        case OP_ADDIMM:
            executeAddImm(op, cond, subcode);
            break;
        case OP_MSRIMM:
            executeMsrImm(op, cond, subcode);
            break;
        case OP_LDRIMM:
            break;
        case OP_LDRREG:
            break;
        case OP_LDMSTM:
            break;
        case OP_BL_BLX:
            executeBlBlx(op, cond, subcode);
            break;
        case OP_LDCSTC:
            executeLdcStc(op, cond, subcode);
            break;
        case OP_CDPMCR:
            executeCdpMcr(op, cond, subcode);
            break;
        case OP_CDPMRC:
            executeCdpMrc(op, cond, subcode);
            break;
        case OP_SWIIMM:
            executeSwiImm(op, cond, subcode);
            break;
        default:
            throw new IllegalStateException("Unknown subcodeId" +
                    String.format("(%d).", subcodeId));
        }
    }

    public void run() {
        int v;

        while (true) {
            v = read32(getPC() - 8);
            try {
                execute(v);
            } catch (IllegalStateException e) {
                System.out.printf("%08x: %s\n", getPC(), e);
                //ignore
            }

            if (isJumped()) {
                setJumped(false);
            } else {
                setPC(getPC() + 4);
            }
        }
    }

    /**
     * リセット例外を発生させます。
     */
    public void exceptionReset(String dbgmsg) {
        System.out.printf("Exception: Reset by '%s'.\n",
                dbgmsg);
        jumpAbs(0x00000000);
    }

    /**
     * 未定義命令例外を発生させます。
     */
    public void exceptionInst(String dbgmsg) {
        System.out.printf("Exception: Undefined Instruction by '%s'.\n",
                dbgmsg);
        jumpAbs(0x00000004);
    }
}
