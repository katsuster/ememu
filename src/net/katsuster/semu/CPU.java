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
    private CoProc[] coProcs;

    private boolean jumped;

    private int modeDisasm;

    public CPU() {
        regs = new int[16];
        coProcs = new CoProc[16];
        coProcs[15] = new StdCoProc(15, this);
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

    public void printDisasm(Instruction inst, String operation, String operand) {
        System.out.printf("%08x:    %08x    %-7s %s\n",
                getPC() - 8, inst.getInst(), operation, operand);
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
     * コプロセッサ Pn を取得します。
     *
     * @param cpnum コプロセッサ番号
     * @return コプロセッサ
     */
    public CoProc getCoproc(int cpnum) {
        return coProcs[cpnum];
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

    public static final int PSR_BIT_N = 31;
    public static final int PSR_BIT_Z = 30;
    public static final int PSR_BIT_C = 29;
    public static final int PSR_BIT_V = 28;
    public static final int PSR_BIT_I = 7;
    public static final int PSR_BIT_F = 6;
    public static final int PSR_BIT_T = 5;

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
                BitOp.getBit(val, PSR_BIT_N) ? "N" : "n",
                BitOp.getBit(val, PSR_BIT_Z) ? "Z" : "z",
                BitOp.getBit(val, PSR_BIT_C) ? "C" : "c",
                BitOp.getBit(val, PSR_BIT_V) ? "V" : "v",
                BitOp.getBit(val, PSR_BIT_I) ? "I" : "i",
                BitOp.getBit(val, PSR_BIT_F) ? "F" : "f",
                BitOp.getBit(val, PSR_BIT_T) ? "T" : "t",
                getPSR_ModeName(getPSR_Mode(val)));
    }

    /**
     * CPSR（カレントプログラムステートレジスタ）の
     * N ビット（ビット 31）を取得します。
     *
     * N ビットは演算結果のビット 31 が 1 の場合に設定されます。
     * すなわち演算結果を 2の補数の符号付き整数としてみたとき、
     * 演算結果が正の数であれば N=0、負の数であれば N=1 となります。
     *
     * @return N ビットがセットされていれば true, そうでなければ false
     */
    public boolean getCPSR_N() {
        return BitOp.getBit(getCPSR(), PSR_BIT_N);
    }

    /**
     * CPSR（カレントプログラムステートレジスタ）の
     * N ビット（ビット 31）を設定します。
     *
     * N ビットは演算結果のビット 31 が 1 の場合に設定されます。
     * すなわち演算結果を 2の補数の符号付き整数としてみたとき、
     * 演算結果が正の数であれば N=0、負の数であれば N=1 となります。
     *
     * @param nv N ビットをセットするなら true, クリアするなら false
     */
    public void setCPSR_N(boolean nv) {
        setCPSR(BitOp.setBit(getCPSR(), PSR_BIT_N, nv));
    }

    /**
     * CPSR（カレントプログラムステートレジスタ）の
     * Z ビット（ビット 30）を取得します。
     *
     * Z ビットは演算結果が 0 の場合に設定されます。
     * 演算結果が 0 以外ならば Z=0、0 ならば Z=1 となります。
     *
     * @return Z ビットがセットされていれば true, そうでなければ false
     */
    public boolean getCPSR_Z() {
        return BitOp.getBit(getCPSR(), PSR_BIT_Z);
    }

    /**
     * CPSR（カレントプログラムステートレジスタ）の
     * Z ビット（ビット 30）を設定します。
     *
     * Z ビットは演算結果が 0 の場合に設定されます。
     * 演算結果が 0 以外ならば Z=0、0 ならば Z=1 となります。
     *
     * @param nv Z ビットをセットするなら true, クリアするなら false
     */
    public void setCPSR_Z(boolean nv) {
        setCPSR(BitOp.setBit(getCPSR(), PSR_BIT_Z, nv));
    }

    /**
     * CPSR（カレントプログラムステートレジスタ）の
     * C ビット（ビット 29）を取得します。
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
     * @return C ビットがセットされていれば true, そうでなければ false
     */
    public boolean getCPSR_C() {
        return BitOp.getBit(getCPSR(), PSR_BIT_C);
    }

    /**
     * CPSR（カレントプログラムステートレジスタ）の
     * C ビット（ビット 29）を設定します。
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
     * @param nv C ビットをセットするなら true, クリアするなら false
     */
    public void setCPSR_C(boolean nv) {
        setCPSR(BitOp.setBit(getCPSR(), PSR_BIT_C, nv));
    }

    /**
     * CPSR（カレントプログラムステートレジスタ）の
     * V ビット（ビット 28）を取得します。
     *
     * V ビットは演算結果に符号付きオーバーフローした場合に設定されます。
     *
     * - 演算が加算または減算で、
     * 演算により符号付きオーバーフローしなければ V=0、
     * 符号付きオーバーフローしたならば V=1 となります。
     *
     * @return V ビットがセットされていれば true, そうでなければ false
     */
    public boolean getCPSR_V() {
        return BitOp.getBit(getCPSR(), PSR_BIT_V);
    }

    /**
     * CPSR（カレントプログラムステートレジスタ）の
     * V ビット（ビット 28）を設定します。
     *
     * V ビットは演算結果に符号付きオーバーフローした場合に設定されます。
     *
     * - 演算が加算または減算で、
     * 演算により符号付きオーバーフローしなければ V=0、
     * 符号付きオーバーフローしたならば V=1 となります。
     *
     * @param nv V ビットをセットするなら true, クリアするなら false
     */
    public void setCPSR_V(boolean nv) {
        setCPSR(BitOp.setBit(getCPSR(), PSR_BIT_V, nv));
    }

    /**
     * CPSR（カレントプログラムステートレジスタ）の
     * I ビット（ビット 7）を取得します。
     *
     * I=0 ならば IRQ 割り込みが有効となります。
     * I=1 ならば IRQ 割り込みが無効となります。
     *
     * @return I ビットがセットされていれば true, そうでなければ false
     */
    public boolean getCPSR_I() {
        return BitOp.getBit(getCPSR(), PSR_BIT_I);
    }

    /**
     * CPSR（カレントプログラムステートレジスタ）の
     * I ビット（ビット 7）を設定します。
     *
     * I=0 ならば IRQ 割り込みが有効となります。
     * I=1 ならば IRQ 割り込みが無効となります。
     *
     * @param nv I ビットをセットするなら true, クリアするなら false
     */
    public void setCPSR_I(boolean nv) {
        setCPSR(BitOp.setBit(getCPSR(), PSR_BIT_I, nv));
    }

    /**
     * CPSR（カレントプログラムステートレジスタ）の
     * F ビット（ビット 6）を取得します。
     *
     * F=0 ならば FIQ 割り込みが有効となります。
     * F=1 ならば FIQ 割り込みが無効となります。
     *
     * @return F ビットがセットされていれば true, そうでなければ false
     */
    public boolean getCPSR_F() {
        return BitOp.getBit(getCPSR(), PSR_BIT_F);
    }

    /**
     * CPSR（カレントプログラムステートレジスタ）の
     * F ビット（ビット 6）を設定します。
     *
     * F=0 ならば FIQ 割り込みが有効となります。
     * F=1 ならば FIQ 割り込みが無効となります。
     *
     * @param nv  F ビットをセットするなら true, クリアするなら false
     */
    public void setCPSR_F(boolean nv) {
        setCPSR(BitOp.setBit(getCPSR(), PSR_BIT_F, nv));
    }

    /**
     * CPSR（カレントプログラムステートレジスタ）の
     * T ビット（ビット 5）を取得します。
     *
     * T=0 ならば ARM 命令を実行します。
     * T=1 ならば Thumb 命令を実行します。
     *
     * ARMv5 以上の非 T バリアント（Thumb 命令非対応）の場合、
     * T=1 ならば次に実行される命令で未定義命令例外を発生させます。
     *
     * @return T ビットがセットされていれば true, そうでなければ false
     */
    public boolean getCPSR_T() {
        return BitOp.getBit(getCPSR(), PSR_BIT_T);
    }

    /**
     * CPSR（カレントプログラムステートレジスタ）の
     * T ビット（ビット 5）を設定します。
     *
     * T=0 ならば ARM 命令を実行します。
     * T=1 ならば Thumb 命令を実行します。
     *
     * ARMv5 以上の非 T バリアント（Thumb 命令非対応）の場合、
     * T=1 ならば次に実行される命令で未定義命令例外を発生させます。
     *
     * @param nv  T ビットをセットするなら true, クリアするなら false
     */
    public void setCPSR_T(boolean nv) {
        setCPSR(BitOp.setBit(getCPSR(), PSR_BIT_T, nv));
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

    /**
     * アドレシングモード 1 - データ処理オペランドを取得します。
     *
     * @param inst ARM 命令
     * @return イミディエート
     */
    public int getShifterOperand(Instruction inst) {
        boolean i = inst.getIBit();
        boolean b7 = inst.getBit(7);
        boolean b4 = inst.getBit(4);

        if (i) {
            //32bits イミディエート
            return getImm32Operand(inst);
        } else if (!b4) {
            //イミディエートシフト
            return getImmShiftOperand(inst);
        } else if (b4 && !b7) {
            //レジスタシフト
            return getRegShiftOperand(inst);
        } else {
            throw new IllegalArgumentException("Unknown shifter_operand " +
                    String.format("0x%08x, I:%b, b7:%b, b4:%b.",
                            inst.getInst(), i, b7, b4));
        }
    }

    /**
     * アドレシングモード 1 - データ処理オペランド、
     * 32ビットイミディエートを取得します。
     *
     * 条件:
     * I ビット（ビット[25]）: 1
     *
     * rotate_imm: ビット[11:8]
     * immed_8: ビット[7:0]
     * とすると、イミディエート imm32 は下記のように求められます。
     *
     * imm32 = rotateRight(immed_8, rotate_imm * 2)
     *
     * @param inst ARM 命令
     * @return イミディエート
     */
    public int getImm32Operand(Instruction inst) {
        int rotR = (inst.getInst() >> 8) & 0xf;
        int imm8 = inst.getInst() & 0xff;

        return Integer.rotateRight(imm8, rotR * 2);
    }

    /**
     * アドレシングモード 1 - データ処理オペランド、
     * イミディエートシフトを取得します。
     *
     * 条件:
     * I ビット（ビット[25]）: 0
     * ビット[4]: 0
     *
     * 該当するオペランド:
     * 数値はビット[6:4] の値を示す。
     * 0b000: データ処理オペランド - レジスタ
     * 0b000: データ処理オペランド - イミディエート論理左シフト
     * 0b010: データ処理オペランド - イミディエート論理右シフト
     * 0b100: データ処理オペランド - イミディエート算術右シフト
     * 0b110: データ処理オペランド - イミディエート右ローテート
     * 0b110: データ処理オペランド - 拡張付き右ローテート
     *
     * @param inst ARM 命令
     * @return イミディエートシフトオペランド
     */
    public int getImmShiftOperand(Instruction inst) {
        int shift_imm = (inst.getInst() >> 7) & 0x1f;
        int b5_6 = (inst.getInst() >> 5) & 0x3;
        int rm = inst.getInst() & 0xf;

        switch (b5_6) {
        case 0:
            if (shift_imm == 0) {
                //レジスタ
                return getReg(rm);
            } else {
                //イミディエート論理左シフト
                return getReg(rm) << shift_imm;
            }
        case 1:
            //イミディエート論理右シフト
            return getReg(rm) >>> shift_imm;
        case 2:
            //イミディエート算術右シフト
            return getReg(rm) >> shift_imm;
        case 3:
            if (shift_imm == 0) {
                //拡張付き右ローテート
                if (getCPSR_C()) {
                    return (1 << 31) | (getReg(rm) >>> 1);
                } else {
                    return getReg(rm) >>> 1;
                }
            } else {
                //イミディエート右ローテート
                return Integer.rotateRight(getReg(rm), shift_imm);
            }
        default:
            throw new IllegalArgumentException("Unknown Imm Shift " +
                    String.format("0x%08x, b5_6:%d.",
                            inst.getInst(), b5_6));
        }
    }

    public int getRegShiftOperand(Instruction inst) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * アドレシングモード 1 - データ処理オペランドのキャリーアウトを取得します。
     *
     * @param inst ARM 命令
     * @return キャリーアウトがあれば true、なければ false
     */
    public boolean getShifterCarry(Instruction inst) {
        boolean i = inst.getIBit();
        boolean b7 = inst.getBit(7);
        boolean b4 = inst.getBit(4);

        if (i) {
            //32bits イミディエート
            return getImm32Carry(inst);
        } else if (!b4) {
            //イミディエートシフト
            return getImmShiftCarry(inst);
        } else if (b4 && !b7) {
            //レジスタシフト
            return getRegShiftCarry(inst);
        } else {
            throw new IllegalArgumentException("Unknown shifter_operand " +
                    String.format("0x%08x, I:%b, b7:%b, b4:%b.",
                            inst.getInst(), i, b7, b4));
        }
    }

    /**
     * アドレシングモード 1 - データ処理オペランド、
     * 32ビットイミディエートのキャリーアウトを取得します。
     *
     * @param inst ARM 命令
     * @return キャリーアウトする場合は 1、そうでなければ 0
     */
    public boolean getImm32Carry(Instruction inst) {
        int rotR = (inst.getInst() >> 8) & 0xf;

        if (rotR == 0) {
            return getCPSR_C();
        } else {
            return BitOp.getBit(getImm32Operand(inst), 31);
        }
    }

    public boolean getImmShiftCarry(Instruction inst) {
        int shift_imm = (inst.getInst() >> 7) & 0x1f;
        int b5_6 = (inst.getInst() >> 5) & 0x3;
        int rm = inst.getInst() & 0xf;

        switch (b5_6) {
        case 0:
            if (shift_imm == 0) {
                //レジスタ
            } else {
                //イミディエート論理左シフト
            }
            break;
        case 1:
            //イミディエート論理右シフト
            break;
        case 2:
            //イミディエート算術右シフト
            break;
        case 3:
            if (shift_imm == 0) {
                //拡張付き右ローテート
            } else {
                //イミディエート右ローテート
            }
            break;
        default:
            throw new IllegalArgumentException("Unknown Imm Shift " +
                    String.format("0x%08x, b5_6:%d.",
                            inst.getInst(), b5_6));
        }

        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    public boolean getRegShiftCarry(Instruction inst) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    public String getShifterOperandName(Instruction inst) {
        boolean i = inst.getIBit();
        boolean b7 = inst.getBit(7);
        boolean b4 = inst.getBit(4);

        if (i) {
            //32bits イミディエート
            return getImm32OperandName(inst);
        } else if (!b4) {
            //イミディエートシフト
            return getImmShiftOperandName(inst);
        } else if (b4 && !b7) {
            //レジスタシフト
            return getRegShiftOperandName(inst);
        } else {
            throw new IllegalArgumentException("Unknown shifter_operand " +
                    String.format("0x%08x, I:%b, b7:%b, b4:%b.",
                            inst.getInst(), i, b7, b4));
        }
    }

    /**
     * データ処理オペランドの 32ビットイミディエートの、
     * 文字列表現を取得します。
     *
     * @param inst 命令コード
     * @return イミディエートの文字列表現
     */
    public String getImm32OperandName(Instruction inst) {
        int imm32 = getImm32Operand(inst);

        return String.format("#%d    ; 0x%x", imm32, imm32);
    }

    public String getImmShiftOperandName(Instruction inst) {
        int shift_imm = (inst.getInst() >> 7) & 0x1f;
        int b5_6 = (inst.getInst() >> 5) & 0x3;
        int rm = inst.getInst() & 0xf;

        switch (b5_6) {
        case 0:
            if (shift_imm == 0) {
                //レジスタ
                return String.format("r%d", rm);
            } else {
                //イミディエート論理左シフト

            }
            break;
        case 1:
            //イミディエート論理右シフト
            break;
        case 2:
            //イミディエート算術右シフト
            break;
        case 3:
            if (shift_imm == 0) {
                //拡張付き右ローテート
            } else {
                //イミディエート右ローテート
            }
            break;
        default:
            throw new IllegalArgumentException("Unknown Imm Shift " +
                    String.format("0x%08x, b5_6:%d.",
                            inst.getInst(), b5_6));
        }

        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    public String getRegShiftOperandName(Instruction inst) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * ステータスレジスタへ値を設定する。
     *
     * @param inst ARM 命令
     * @param cond cond フィールド
     */
    public void executeMsr(Instruction inst, int cond) {
        boolean flag_r = inst.getBit(22);
        boolean mask_f = inst.getBit(19);
        boolean mask_s = inst.getBit(18);
        boolean mask_x = inst.getBit(17);
        boolean mask_c = inst.getBit(16);
        int sbo = (inst.getInst() >> 12) & 0xf;
        int imm32 = getImm32Operand(inst);
        int v, m = 0;

        if (!inst.getIBit()) {
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
        }

        if (isDisasmMode()) {
            printDisasm(inst,
                    String.format("msr%s", inst.getCondFieldName()),
                    String.format("%s_%s%s%s%s, #%d    ; 0x%x",
                            (flag_r) ? "SPSR" : "CPSR",
                            (mask_f) ? "f" : "",
                            (mask_s) ? "s" : "",
                            (mask_x) ? "x" : "",
                            (mask_c) ? "c" : "",
                            imm32, imm32));
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        if (sbo != 0xf) {
            System.out.println("Warning: Illegal instruction, " +
                    String.format("msr SBO[15:12](0x%01x) has zero.", sbo));
        }

        if (!flag_r) {
            v = getCPSR();
        } else {
            v = getSPSR();
        }

        if (mask_c) {
            m |= 0x000000ff;
        }
        if (mask_x) {
            m |= 0x0000ff00;
        }
        if (mask_s) {
            m |= 0x00ff0000;
        }
        if (mask_f) {
            m |= 0xff000000;
        }
        v &= ~m;
        v |= imm32 & m;

        if (!flag_r) {
            setCPSR(v);
        } else {
            setSPSR(v);
        }
    }

    /**
     * 論理積命令。
     *
     * @param inst ARM 命令
     * @param cond cond フィールド
     */
    public void executeAnd(Instruction inst, int cond) {
        boolean s = inst.getSBit();
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int opr = getShifterOperand(inst);
        int left, right, dest;

        if (isDisasmMode()) {
            printDisasm(inst,
                    String.format("%s%s%s", inst.getOpcodeFieldName(),
                            inst.getCondFieldName(),
                            (s) ? "s" : ""),
                    String.format("r%d, r%d, %s", rd, rn,
                            getShifterOperandName(inst)));
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        left = getReg(rn);
        right = opr;
        dest = left & right;

        if (s && rd == 15) {
            setCPSR(getSPSR());
        } else if (s) {
            //TODO: set flags
            throw new IllegalArgumentException("Sorry, not implemented.");
        }

        setReg(rd, dest);
    }

    /**
     * 減算命令。
     *
     * @param inst ARM 命令
     * @param cond cond フィールド
     */
    public void executeSub(Instruction inst, int cond) {
        boolean s = inst.getSBit();
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int opr = getShifterOperand(inst);
        int left, right, dest;

        if (isDisasmMode()) {
            printDisasm(inst,
                    String.format("%s%s%s", inst.getOpcodeFieldName(),
                            inst.getCondFieldName(),
                            (s) ? "s" : ""),
                    String.format("r%d, r%d, %s", rd, rn,
                            getShifterOperandName(inst)));
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        left = getReg(rn);
        right = opr;
        dest = left - right;

        if (s && rd == 15) {
            setCPSR(getSPSR());
        } else if (s) {
            //TODO: set flags
            throw new IllegalArgumentException("Sorry, not implemented.");
        }

        setReg(rd, dest);
    }

    /**
     * 加算命令。
     *
     * @param inst ARM 命令
     * @param cond cond フィールド
     */
    public void executeAdd(Instruction inst, int cond) {
        boolean s = inst.getSBit();
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int opr = getShifterOperand(inst);
        int left, right, dest;

        if (isDisasmMode()) {
            printDisasm(inst,
                    String.format("%s%s%s", inst.getOpcodeFieldName(),
                            inst.getCondFieldName(),
                            (s) ? "s" : ""),
                    String.format("r%d, r%d, %s", rd, rn,
                            getShifterOperandName(inst)));
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        left = getReg(rn);
        right = opr;
        dest = left + right;

        if (s && rd == 15) {
            setCPSR(getSPSR());
        } else if (s) {
            //TODO: set flags
            throw new IllegalArgumentException("Sorry, not implemented.");
        }

        setReg(rd, dest);
    }

    /**
     * 等価テスト命令。
     *
     * @param inst ARM 命令
     * @param cond cond フィールド
     */
    public void executeTeq(Instruction inst, int cond) {
        int rn = inst.getRnField();
        int opr = getShifterOperand(inst);
        int left, right, dest;

        if (isDisasmMode()) {
            printDisasm(inst,
                    String.format("%s%s", inst.getOpcodeFieldName(),
                            inst.getCondFieldName()),
                    String.format("r%d, %s", rn,
                            getShifterOperandName(inst)));
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        left = getReg(rn);
        right = opr;
        dest = left ^ right;

        setCPSR_N(BitOp.getBit(dest, 31));
        setCPSR_Z(dest == 0);
        setCPSR_C(getShifterCarry(inst));
        //V flag is unaffected
    }

    /**
     * LDM, STM 命令の転送開始アドレスを取得します。
     *
     * @param pu    P, U ビット
     * @param rn    レジスタ番号
     * @param rlist レジスタリスト
     * @return 転送開始アドレス
     */
    public int getLdmStartAddress(int pu, int rn, int rlist) {
        switch (pu) {
        case Instruction.PU_ADDR4_IA:
            return getReg(rn);
        case Instruction.PU_ADDR4_IB:
            return getReg(rn) + 4;
        case Instruction.PU_ADDR4_DA:
            return getReg(rn) - (Integer.bitCount(rlist) * 4) + 4;
        case Instruction.PU_ADDR4_DB:
            return getReg(rn) - (Integer.bitCount(rlist) * 4);
        default:
            throw new IllegalArgumentException("Illegal PU field " +
                    pu + ".");
        }
    }

    /**
     * LDM, STM 命令が転送するデータの長さを取得します。
     *
     * @param pu    P, U ビット
     * @param rlist レジスタリスト
     * @return 転送するデータの長さ
     */
    public int getLdmLength(int pu, int rlist) {
        switch (pu) {
        case Instruction.PU_ADDR4_IA:
        case Instruction.PU_ADDR4_IB:
            return Integer.bitCount(rlist) * 4;
        case Instruction.PU_ADDR4_DA:
        case Instruction.PU_ADDR4_DB:
            return -(Integer.bitCount(rlist) * 4);
        default:
            throw new IllegalArgumentException("Illegal PU field " +
                    pu + ".");
        }
    }

    public void executeLdm1(Instruction inst, int cond) {
        boolean w = inst.getBit(21);
        int rn = inst.getRnField();
        int rlist = inst.getRegListField();
        int addr, len;

        if (isDisasmMode()) {
            printDisasm(inst,
                    String.format("ldm%s%s",
                            inst.getCondFieldName(),
                            inst.getPUFieldName()),
                    String.format("r%d%s, {%s}",
                            rn, (w) ? "!" : "",
                            inst.getRegListFieldName()));
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        //r15 以外
        addr = getLdmStartAddress(inst.getPUField(), rn, rlist);
        len = getLdmLength(inst.getPUField(), rlist);
        for (int i = 0; i < 15; i++) {
            if ((rlist & (1 << i)) == 0) {
                continue;
            }
            setReg(i, read32(addr));
            addr += 4;
        }
        //r15
        if ((rlist & 0x8000) != 0) {
            int v = read32(addr);

            setPC(v & 0xfffffffe);
            setCPSR_T(BitOp.getBit(v, 0));
            addr += 4;
        }

        if (w) {
            setReg(rn, getReg(rn) + len);
        }
    }

    public void executeLdm2(Instruction inst, int cond) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    public void executeLdm3(Instruction inst, int cond) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    public void executeStm1(Instruction inst, int cond) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    public void executeStm2(Instruction inst, int cond) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    public void executeBl(Instruction inst, int cond) {
        boolean l = inst.getBit(24);
        int imm24 = inst.getInst() & 0xffffff;
        int simm24 = (int)signext(imm24, 24) << 2;

        if (isDisasmMode()) {
            printDisasm(inst,
                    String.format("b%s%s",
                            (l) ? "l" : "", inst.getCondFieldName()),
                    String.format("%08x", getPC() + simm24));
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        if (l) {
            setReg(14, getPC() - 4);
        }
        jumpRel(simm24);
    }

    public void executeBlx(Instruction inst, int cond) {
        boolean h = inst.getBit(24);
        int vh = BitOp.toBit(h) << 1;
        int imm24 = inst.getInst() & 0xffffff;
        int simm24 = (int)signext(imm24, 24) << 2;

        if (isDisasmMode()) {
            printDisasm(inst,
                    String.format("blx"),
                    String.format("%08x", getPC() + simm24 + vh));
        }

        //blx は条件判定不可です

        setReg(14, getPC() - 4);
        //T ビットをセット
        setCPSR(getCPSR() | 0x20);
        jumpRel(simm24 + vh);

        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    public void executeCdp(Instruction inst, int cond) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    public void executeMcr(Instruction inst, int cond) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    public void executeMrc(Instruction inst, int cond) {
        int opcode1 = (inst.getInst() >> 21) & 0x7;
        int crn = (inst.getInst() >> 16) & 0xf;
        int rd = inst.getRdField();
        int cpnum = (inst.getInst() >> 8) & 0xf;
        int opcode2 = (inst.getInst() >> 5) & 0x7;
        int crm = inst.getInst() & 0xf;
        CoProc cp;
        int crid, crval, rval;

        if (isDisasmMode()) {
            printDisasm(inst,
                    String.format("mrc%s", inst.getCondFieldName()),
                    String.format("%s, %d, %s, %s, %s, {%d}",
                            getCoproc(cpnum).toString(), opcode1, getRegName(rd),
                            getCoprocRegName(cpnum, crn), getCoprocRegName(cpnum, crm),
                            opcode2));
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        cp = getCoproc(cpnum);
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

    public void executeSwi(Instruction inst, int cond) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    public void executeUnd(Instruction inst, int cond) {
        exceptionInst("Warning: Undefined instruction " +
                String.format("inst:0x%08x, cond:%d.",
                        inst.getInst(), cond));
    }

    public void execute(Instruction inst) {
        int cond = inst.getCondField();
        int subcode = inst.getSubCodeField();

        switch (subcode) {
        case Instruction.SUBCODE_USEALU:
            executeSubUseALU(inst, cond);
            break;
        case Instruction.SUBCODE_LDRSTR:
            executeSubLdrStr(inst, cond);
            break;
        case Instruction.SUBCODE_LDMSTM:
            executeSubLdmStm(inst, cond);
            break;
        case Instruction.SUBCODE_COPSWI:
            executeSubCopSwi(inst, cond);
            break;
        default:
            throw new IllegalStateException("Unknown Subcode" +
                    String.format("(%d).", subcode));
        }
    }

    /**
     * データ処理命令を実行します。
     *
     * subcode = 0b00
     *
     * @param inst ARM 命令
     * @param cond cond フィールド
     */
    public void executeSubUseALU(Instruction inst, int cond) {
        boolean i = inst.getIBit();
        boolean b7 = inst.getBit(7);
        boolean b4 = inst.getBit(4);

        if (!i) {
            //b4, b7 の値が、
            //  0, 0: イミディエートシフト
            //  0, 1: イミディエートシフト
            //  1, 0: レジスタシフト
            //  1, 1: 算術命令拡張空間: 乗算、追加ロードストア
            if (!b4) {
                //イミディエートシフト
                executeSubUseALUShiftImm(inst, cond);
            } else if (b4 && !b7) {
                //レジスタシフト
                executeSubUseALUShiftReg(inst, cond);
            } else {
                //乗算、追加ロードストア
                //TODO: Not implemented
                throw new IllegalArgumentException("Sorry, not implemented.");
            }
        } else {
            //イミディエート
            executeSubUseALUImm32(inst, cond);
        }
    }

    /**
     * イミディエートシフトオペランドを取るデータ処理命令、
     * その他の命令を実行します。
     *
     * @param inst ARM 命令
     * @param cond cond フィールド
     */
    public void executeSubUseALUShiftImm(Instruction inst, int cond) {
        int id = inst.getOpcodeSBitShiftID();

        switch (id) {
        case Instruction.OPCODE_S_OTH:
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        default:
            executeSubUseALUGen(inst, cond, id);
            break;
        }
    }

    /**
     * レジスタシフトオペランドを取るデータ処理命令、
     * その他の命令を実行します。
     *
     * @param inst ARM 命令
     * @param cond cond フィールド
     */
    public void executeSubUseALUShiftReg(Instruction inst, int cond) {
        int id = inst.getOpcodeSBitShiftID();

        switch (id) {
        case Instruction.OPCODE_S_OTH:
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        default:
            executeSubUseALUGen(inst, cond, id);
            break;
        }
    }

    /**
     * イミディエートのみを取るデータ処理命令、その他の命令を実行します。
     *
     * データ処理イミディエート命令、
     * ステータスレジスタへのイミディエート移動命令、
     * の実行
     *
     * @param inst ARM 命令
     * @param cond cond フィールド
     */
    public void executeSubUseALUImm32(Instruction inst, int cond) {
        int id = inst.getOpcodeSBitImmID();

        switch (id) {
        case Instruction.OPCODE_S_MSR:
            executeMsr(inst, cond);
            break;
        case Instruction.OPCODE_S_UND:
            executeUnd(inst, cond);
            break;
        default:
            executeSubUseALUGen(inst, cond, id);
            break;
        }
    }

    /**
     * シフトオペランド、イミディエートオペランド、
     * どちらも取り得るデータ処理命令を実行します。
     *
     * 下記の種類の命令を扱います。
     * and, eor, sub, rsb,
     * add, adc, sbc, rsc,
     * tst, teq, cmp, cmn,
     * orr, mov, bic, mvn,
     *
     * @param inst ARM 命令
     * @param cond cond フィールド
     * @param id   オペコードフィールドと S ビットが示す演算の ID
     */
    public void executeSubUseALUGen(Instruction inst, int cond, int id) {
        switch (id) {
        case Instruction.OPCODE_S_AND:
            executeAnd(inst, cond);
            break;
        case Instruction.OPCODE_S_EOR:
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        case Instruction.OPCODE_S_SUB:
            executeSub(inst, cond);
            break;
        case Instruction.OPCODE_S_RSB:
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        case Instruction.OPCODE_S_ADD:
            executeAdd(inst, cond);
            break;
        case Instruction.OPCODE_S_ADC:
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        case Instruction.OPCODE_S_SBC:
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        case Instruction.OPCODE_S_RSC:
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        case Instruction.OPCODE_S_TST:
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        case Instruction.OPCODE_S_TEQ:
            executeTeq(inst, cond);
            break;
        case Instruction.OPCODE_S_CMP:
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        case Instruction.OPCODE_S_CMN:
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        case Instruction.OPCODE_S_ORR:
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        case Instruction.OPCODE_S_MOV:
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        case Instruction.OPCODE_S_BIC:
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        case Instruction.OPCODE_S_MVN:
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        default:
            throw new IllegalArgumentException("Unknown opcode S-bit ID " +
                    String.format("%d.", id));
        }
    }

    /**
     * ロード、ストア命令を実行します。
     *
     * subcode = 0b01
     *
     * @param inst ARM 命令
     * @param cond cond フィールド
     */
    public void executeSubLdrStr(Instruction inst, int cond) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * ロードマルチプル、ストアマルチプル、分岐命令を実行します。
     *
     * subcode = 0b10
     *
     * @param inst ARM 命令
     * @param cond cond フィールド
     */
    public void executeSubLdmStm(Instruction inst, int cond) {
        boolean b25 = inst.getBit(25);
        boolean l = inst.getLBit();

        if (!b25) {
            //ロードマルチプル、ストアマルチプル
            if (cond == Instruction.COND_NV) {
                //未定義命令
                executeUnd(inst, cond);
            } else {
                if (l) {
                    //ldm(1), ldm(2), ldm(3)
                    executeSubLdm(inst, cond);
                } else {
                    //stm(1), stm(2)
                    executeSubStm(inst, cond);
                }
            }
        } else {
            //分岐命令
            if (cond == Instruction.COND_NV) {
                //blx
                executeBlx(inst, cond);
            } else {
                //b, bl
                executeBl(inst, cond);
            }
        }
    }

    /**
     * ロードマルチプル命令を実行します。
     *
     * subcode = 0b10
     *
     * @param inst ARM 命令
     * @param cond cond フィールド
     */
    public void executeSubLdm(Instruction inst, int cond) {
        boolean s = inst.getBit(22);
        boolean b15 = inst.getBit(15);

        if (!s) {
            //ldm(1)
            executeLdm1(inst, cond);
        } else {
            if (!b15) {
                //ldm(2)
                executeLdm2(inst, cond);
            } else {
                //ldm(3)
                executeLdm3(inst, cond);
            }
        }
    }

    /**
     * ストアマルチプル命令を実行します。
     *
     * subcode = 0b10
     *
     * @param inst ARM 命令
     * @param cond cond フィールド
     */
    public void executeSubStm(Instruction inst, int cond) {
        boolean s = inst.getBit(22);
        boolean w = inst.getBit(21);

        if (!s) {
            //stm(1)
            executeStm1(inst, cond);
        } else {
            if (!w) {
                //stm(2)
                executeStm2(inst, cond);
            } else {
                //未定義
                executeUnd(inst, cond);
            }
        }
    }

    /**
     * コプロセッサ、ソフトウェア割り込み命令を実行します。
     *
     * subcode = 0b11
     *
     * @param inst ARM 命令
     * @param cond cond フィールド
     */
    public void executeSubCopSwi(Instruction inst, int cond) {
        int subsub = (inst.getInst() >> 24) & 0x3;
        boolean b20 = inst.getBit(20);
        boolean b4 = inst.getBit(4);

        switch (subsub) {
        case 0:
        case 1:
            break;
        case 2:
            if (!b4) {
                //cdp
                executeCdp(inst, cond);
            } else {
                if (!b20) {
                    //mcr
                    executeMcr(inst, cond);
                } else {
                    //mrc
                    executeMrc(inst, cond);
                }
            }
            break;
        case 3:
            if (cond == Instruction.COND_NV) {
                //未定義
                executeUnd(inst, cond);
            } else {
                //swi
                executeSwi(inst, cond);
            }
            break;
        }
    }

    public void run() {
        Instruction inst;
        int v;

        while (true) {
            v = read32(getPC() - 8);
            inst = new Instruction(v);
            try {
                execute(inst);
            } catch (IllegalStateException e) {
                System.out.printf("%08x: %08x: %s\n",
                        getPC(), inst.getInst(), e);
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
     *
     * @param dbgmsg デバッグ用のメッセージ
     */
    public void exceptionReset(String dbgmsg) {
        System.out.printf("Exception: Reset by '%s'.\n",
                dbgmsg);
        jumpAbs(0x00000000);
    }

    /**
     * 未定義命令例外を発生させます。
     *
     * @param dbgmsg デバッグ用のメッセージ
     */
    public void exceptionInst(String dbgmsg) {
        System.out.printf("Exception: Undefined Instruction by '%s'.\n",
                dbgmsg);
        jumpAbs(0x00000004);
    }
}
