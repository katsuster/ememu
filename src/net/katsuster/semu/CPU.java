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
        int i;

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
        return BitOp.getBit(val, 31);
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
        return BitOp.getBit(val, 30);
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
        return BitOp.getBit(val, 29);
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
        return BitOp.getBit(val, 28);
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
        return BitOp.getBit(val, 7);
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
        return BitOp.getBit(val, 6);
    }

    /**
     * PSR（プログラムステートレジスタ）の F ビット（ビット 6）を設定します。
     *
     * F=0 ならば FIQ 割り込みが有効となります。
     * F=1 ならば FIQ 割り込みが無効となります。
     *
     * @param val PSR の値
     * @param nv  新しい F ビットの値
     */
    public static int setPSR_F(int val, boolean nv) {
        return BitOp.setBit(val, 6, nv);
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
        return BitOp.getBit(val, 5);
    }

    /**
     * PSR（プログラムステートレジスタ）の T ビット（ビット 5）を設定します。
     *
     * T=0 ならば ARM 命令を実行します。
     * T=1 ならば Thumb 命令を実行します。
     *
     * ARMv5 以上の非 T バリアント（Thumb 命令非対応）の場合、
     * T=1 ならば次に実行される命令で未定義命令例外を発生させます。
     *
     * @param val PSR の値
     * @param nv  新しい T ビットの値
     */
    public static int setPSR_T(int val, boolean nv) {
        return BitOp.setBit(val, 5, nv);
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

    public int getShifterOperand(Instruction inst) {
        int i = inst.getBit(24);
        int b7 = inst.getBit(7);
        int b4 = inst.getBit(4);

        if (i == 1) {
            //32bits イミディエート
            return inst.getImm32Operand();
        } else if (b4 == 0) {
            //イミディエートシフト
            return getImmShiftOperand(inst);
        } else if (b4 == 1 && b7 == 0) {
            //レジスタシフト
            return getRegShiftOperand(inst);
        } else {
            throw new IllegalArgumentException("Unknown shifter_operand " +
                    String.format("0x%08x, I:%d, b7:%d, b4:%d.", inst, b7, b4));
        }
    }

    public int getImmShiftOperand(Instruction inst) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    public int getRegShiftOperand(Instruction inst) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    public int getImmShiftCarry(Instruction inst) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    public int getRegShiftCarry(Instruction inst) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * データ処理イミディエート
     *
     * @param inst ARM 命令
     * @param cond cond フィールド
     */
    public void executeAdd(Instruction inst, int cond) {
        int s = inst.getSBit();
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int imm32 = inst.getImm32Operand();

        if (inst.getIBit() == 0) {
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
        }

        if (isDisasmMode()) {
            printDisasm(inst,
                    String.format("add%s%s", inst.getCondFieldName(),
                            (s == 1) ? "s" : ""),
                    String.format("r%d, r%d, #%d    ; 0x%x",
                            rd, rn, imm32, imm32));
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        setReg(rd, getReg(rn) + imm32);
        if (s == 1 && rd == 15) {
            setCPSR(getSPSR());
        } else if (s == 1) {
            //TODO: set flags
            throw new IllegalArgumentException("Sorry, not implemented.");
        }
    }

    public void executeMsr(Instruction inst, int cond) {
        int flag_r = inst.getBit(22);
        int mask_f = inst.getBit(19);
        int mask_s = inst.getBit(18);
        int mask_x = inst.getBit(17);
        int mask_c = inst.getBit(16);
        int sbo = (inst.getInst() >> 12) & 0xf;
        int imm32 = inst.getImm32Operand();
        int v, m = 0;

        if (inst.getIBit() == 0) {
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
        }

        if (isDisasmMode()) {
            printDisasm(inst,
                    String.format("msr%s", inst.getCondFieldName()),
                    String.format("%s_%s%s%s%s, #%d    ; 0x%x",
                            (flag_r == 1) ? "SPSR" : "CPSR",
                            (mask_f == 1) ? "f" : "",
                            (mask_s == 1) ? "s" : "",
                            (mask_x == 1) ? "x" : "",
                            (mask_c == 1) ? "c" : "",
                            imm32, imm32));
        }

        if (!inst.satisfiesCond(getCPSR())) {
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
        int w = inst.getBit(21);
        int rn = inst.getRnField();
        int rlist = inst.getRegListField();
        int addr, len;

        if (isDisasmMode()) {
            printDisasm(inst,
                    String.format("ldm%s%s",
                            inst.getCondFieldName(),
                            inst.getPUFieldName()),
                    String.format("r%d%s, {%s}",
                            rn, (w == 1) ? "!" : "",
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
            setCPSR(setPSR_T(getCPSR(), (v & 0x1) != 0));
            addr += 4;
        }

        if (w == 1) {
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
        int l = inst.getBit(24);
        int imm24 = inst.getInst() & 0xffffff;
        int simm24 = (int)signext(imm24, 24) << 2;

        if (isDisasmMode()) {
            printDisasm(inst,
                    String.format("b%s%s",
                            (l == 1) ? "l" : "", inst.getCondFieldName()),
                    String.format("%08x", getPC() + simm24));
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        if (l == 1) {
            setReg(14, getPC() - 4);
        }
        jumpRel(simm24);
    }

    public void executeBlx(Instruction inst, int cond) {
        int h = inst.getBit(24);
        int imm24 = inst.getInst() & 0xffffff;
        int simm24 = (int)signext(imm24, 24) << 2;
        int psr;

        if (isDisasmMode()) {
            printDisasm(inst,
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
                String.format("inst:0x%08x, cond:%d.", inst, cond));
    }

    public void execute(Instruction inst) {
        int cond = inst.getCondField();
        int subcode = inst.getSubCodeField();

        switch (subcode) {
        case Instruction.SUBCODE_ADDSUB:
            executeSubAddSub(inst, cond);
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
    public void executeSubAddSub(Instruction inst, int cond) {
        int i = inst.getIBit();
        int b7 = inst.getBit(7);
        int b4 = inst.getBit(4);

        if (i == 0) {
            //b4, b7 の値が、
            //  0, 0: イミディエートシフト
            //  0, 1: イミディエートシフト
            //  1, 0: レジスタシフト
            //  1, 1: 乗算、追加ロードストア
            if (b4 == 0) {
                //イミディエートシフト
                //TODO: Not implemented
                throw new IllegalArgumentException("Sorry, not implemented.");
            } else if ((b4 == 1) && (b7 == 0)) {
                //レジスタシフト
                //TODO: Not implemented
                throw new IllegalArgumentException("Sorry, not implemented.");
            } else {
                //乗算、追加ロードストア
                //TODO: Not implemented
                throw new IllegalArgumentException("Sorry, not implemented.");
            }
        } else {
            //イミディエート
            executeSubAddSubImm32(inst, cond);
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
    public void executeSubAddSubImm32(Instruction inst, int cond) {
        int id = inst.getOpcodeSBitImmID();

        switch (id) {
        case Instruction.OPCODE_S_MSR:
            executeMsr(inst, cond);
            break;
        case Instruction.OPCODE_S_UND:
            executeUnd(inst, cond);
            break;
        default:
            executeSubAddSubGen(inst, cond, id);
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
    public void executeSubAddSubGen(Instruction inst, int cond, int id) {
        switch (id) {
        case Instruction.OPCODE_S_AND:
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        case Instruction.OPCODE_S_EOR:
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        case Instruction.OPCODE_S_SUB:
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
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
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
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
        int b25 = inst.getBit(25);
        int l = inst.getLBit();

        if (b25 == 0) {
            //ロードマルチプル、ストアマルチプル
            if (cond == Instruction.COND_NV) {
                //未定義命令
                executeUnd(inst, cond);
            } else {
                if (l == 1) {
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
        int s = inst.getBit(22);
        int b15 = inst.getBit(15);

        if (s == 0) {
            //ldm(1)
            executeLdm1(inst, cond);
        } else {
            if (b15 == 0) {
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
        int s = inst.getBit(22);
        int w = inst.getBit(21);

        if (s == 0) {
            //stm(1)
            executeStm1(inst, cond);
        } else {
            if (w == 0) {
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
        int b20 = inst.getBit(20);
        int b4 = inst.getBit(4);

        switch (subsub) {
        case 0:
        case 1:
            break;
        case 2:
            if (b4 == 0) {
                //cdp
                executeCdp(inst, cond);
            } else {
                if (b20 == 0) {
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
