package net.katsuster.semu.arm;

/**
 * ARMv5TE CPU
 *
 * 参考: ARM アーキテクチャリファレンスマニュアル Second Edition
 * ARM DDI0100DJ
 *
 * 最新版は、日本語版 ARM DDI0100HJ, 英語版 ARM DDI0100I
 *
 * T は Thumb 命令、
 * E はエンハンスド DSP 命令、
 * のことらしい。
 *
 * @author katsuhiro
 */
public class ARMv5 extends CPU {
    private int prevMode;
    private int[] regs;
    private int[] regs_usr;
    private int[] regs_svc;
    private int[] regs_abt;
    private int[] regs_und;
    private int[] regs_irq;
    private int[] regs_fiq;
    private PSR cpsr;
    private CoProc[] coProcs;
    private MMUv5 mmu;
    private INTC intcIRQ;
    private INTC intcFIQ;

    private boolean exceptions[];
    private String exceptionReasons[];

    private boolean raised;
    private boolean jumped;
    private boolean highVector;

    public ARMv5() {
        CoProcVFPv2 cpVfps;
        CoProcStdv5 cpStd;

        cpVfps = new CoProcVFPv2(10, this);
        cpStd = new CoProcStdv5(15, this);

        prevMode = PSR.MODE_USR;
        regs = new int[17];
        regs_usr = new int[17];
        regs_svc = new int[17];
        regs_abt = new int[17];
        regs_und = new int[17];
        regs_irq = new int[17];
        regs_fiq = new int[17];
        cpsr = new PSR();
        coProcs = new CoProc[16];
        coProcs[10] = cpVfps;
        coProcs[15] = cpStd;
        mmu = new MMUv5(this, cpStd);
        intcIRQ = new NullINTC();
        intcFIQ = new NullINTC();

        exceptions = new boolean[7];
        exceptionReasons = new String[7];

        raised = false;
        jumped = false;
        highVector = false;
    }

    @Override
    public void printDisasm(Instruction inst, String operation, String operand) {
        if (!isPrintDisasm()) {
            return;
        }

        System.out.printf("%08x:    %08x    %-7s %s\n",
                getPC() - 8, inst.getInst(), operation, operand);
    }

    @Override
    public void printPC() {
        System.out.printf("pc: %08x\n", getPC() - 8);
    }

    @Override
    public void printRegs() {
        if (!isPrintRegs()) {
            return;
        }

        for (int i = 0; i < 16; i += 4) {
            System.out.printf("  r%-2d: %08x, r%-2d: %08x, r%-2d: %08x, r%-2d: %08x, \n",
                    i, getRegRaw(i), i + 1, getRegRaw(i + 1),
                    i + 2, getRegRaw(i + 2), i + 3, getRegRaw(i + 3));
        }
        System.out.printf("  cpsr: %s, spsr: %s\n",
                getCPSR().toString(), getSPSR().toString());
    }

    /**
     * 指定された動作モードにおけるレジスタセットを取得します。
     *
     * @param n    レジスタ番号（0 ～ 15）、16 は SPSR を示す
     * @param mode 動作モード
     * @return レジスタセット
     */
    public int[] getRegSet(int n, int mode) {
        switch (mode) {
        case PSR.MODE_USR:
        case PSR.MODE_SYS:
            return regs_usr;
        case PSR.MODE_SVC:
            if ((13 <= n && n <= 14) || n == 16) {
                return regs_svc;
            } else {
                return regs_usr;
            }
        case PSR.MODE_ABT:
            if ((13 <= n && n <= 14) || n == 16) {
                return regs_abt;
            } else {
                return regs_usr;
            }
        case PSR.MODE_UND:
            if ((13 <= n && n <= 14) || n == 16) {
                return regs_und;
            } else {
                return regs_usr;
            }
        case PSR.MODE_IRQ:
            if ((13 <= n && n <= 14) || n == 16) {
                return regs_irq;
            } else {
                return regs_usr;
            }
        case PSR.MODE_FIQ:
            if ((8 <= n && n <= 14) || n == 16) {
                return regs_fiq;
            } else {
                return regs_usr;
            }
        default:
            //do nothing
            break;
        }

        throw new IllegalArgumentException("Illegal mode " +
                String.format("mode:0x%x.", mode));
    }

    /**
     * レジスタ Rn そのものの値を取得します。
     *
     * r15 を返す際に +8 のオフセットを加算しません。
     *
     * @param n レジスタ番号（0 ～ 15）、16 は SPSR を示す
     * @return レジスタの値
     */
    public int getRegRaw(int n) {
        if (prevMode != getCPSR().getMode()) {
            for (int i = 0; i < regs.length; i++) {
                getRegSet(i, prevMode)[i] = regs[i];
                regs[i] = getRegSet(i, getCPSR().getMode())[i];
            }
            prevMode = getCPSR().getMode();
        }

        return regs[n];
    }

    /**
     * レジスタ Rn そのもの値を設定します。
     *
     * r15 を設定する際にジャンプ済みフラグをセットしません。
     *
     * @param n   レジスタ番号（0 ～ 15）、16 は SPSR を示す
     * @param val 新しいレジスタの値
     */
    public void setRegRaw(int n, int val) {
        if (prevMode != getCPSR().getMode()) {
            for (int i = 0; i < regs.length; i++) {
                getRegSet(i, prevMode)[i] = regs[i];
                regs[i] = getRegSet(i, getCPSR().getMode())[i];
            }
            prevMode = getCPSR().getMode();
        }

        regs[n] = val;
    }

    /**
     * レジスタ Rn の値を取得します。
     *
     * @param n レジスタ番号（0 ～ 15）
     * @return レジスタの値
     */
    public int getReg(int n) {
        if (n == 15) {
            return getRegRaw(n) + 8;
        } else {
            return getRegRaw(n);
        }
    }

    /**
     * レジスタ Rn の値を設定します。
     *
     * @param n   レジスタ番号（0 ～ 15）
     * @param val 新しいレジスタの値
     */
    public void setReg(int n, int val) {
        if (n == 15) {
            setJumped(true);
        }
        setRegRaw(n, val);
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
     * PC を次の命令に移します。
     */
    public void nextPC() {
        if (isJumped()) {
            setJumped(false);
        } else {
            setRegRaw(15, getRegRaw(15) + 4);
        }
    }

    /**
     * 指定したアドレス分だけ相対ジャンプします。
     *
     * PC（実行中の命令のアドレス +8）+ 相対アドレス を、
     * 新たな PC として設定します。
     *
     * また命令実行後は自動的に PC に 4 が加算されますが、
     * ジャンプ後は加算が実行されません。
     *
     * @param val 次に実行する命令の相対アドレス
     */
    public void jumpRel(int val) {
        setPC(getPC() + val);
        setJumped(true);
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
     * @param n     コプロセッサレジスタ番号（0 ～ 7）
     * @return コプロセッサレジスタの名前
     */
    public static String getCoprocRegName(int cpnum, int n) {
        return String.format("cr%d", n);
    }

    /**
     * 標準コプロセッサ（Cp15）を取得します。
     *
     * @return 標準コプロセッサ
     */
    public CoProcStdv5 getCoProcStd() {
        return (CoProcStdv5)coProcs[15];
    }

    /**
     * MMU を取得します。
     *
     * @return MMU
     */
    public MMUv5 getMMU() {
        return mmu;
    }

    /**
     * CPSR（カレントプログラムステートレジスタ）の値を取得します。
     *
     * @return CPSR
     */
    public PSR getCPSR() {
        return cpsr;
    }

    /**
     * CPSR（カレントプログラムステートレジスタ）の値を設定します。
     *
     * @param psr コピー元となる PSR の値
     */
    public void setCPSR(PSR psr) {
        setCPSR(psr.getValue());
    }

    /**
     * CPSR（カレントプログラムステートレジスタ）の値を設定します。
     *
     * @param val 新しい CPSR の値
     */
    public void setCPSR(int val) {
        cpsr.setValue(val);
    }

    /**
     * APSR（アプリケーションプログラムステートレジスタ）の値を取得します。
     *
     * N, Z, C, V, Q, GE のみ取得され、他の値は 0 でマスクされます。
     *
     * @return APSR の値
     */
    public PSR getAPSR() {
        return new PSR(getCPSR().getValue() & 0xf80f0000);
    }

    /**
     * APSR（アプリケーションプログラムステートレジスタ）の値を設定します。
     *
     * N, Z, C, V, Q, GE のみ変更可能です。
     *
     * @param psr コピー元となる PSR の値
     */
    public void setAPSR(PSR psr) {
        setAPSR(psr.getValue());
    }

    /**
     * APSR（アプリケーションプログラムステートレジスタ）の値を設定します。
     *
     * N, Z, C, V, Q, GE のみ変更可能です。
     *
     * @param val 新しい APSR の値
     */
    public void setAPSR(int val) {
        int r;

        //N, Z, C, V, Q, GE のみ変更可能
        r = getCPSR().getValue();
        r &= ~0xf80f0000;
        r |= val & 0xf80f0000;
        getCPSR().setValue(r);
    }

    /**
     * SPSR（保存されたプログラムステートレジスタ）の値を取得します。
     *
     * @return SPSR の値
     */
    public PSR getSPSR() {
        return new PSR(getReg(16));
    }

    /**
     * SPSR（保存されたプログラムステートレジスタ）の値を設定します。
     *
     * @param psr コピー元となる PSR の値
     */
    public void setSPSR(PSR psr) {
        setSPSR(psr.getValue());
    }

    /**
     * SPSR（保存されたプログラムステートレジスタ）の値を設定します。
     *
     * @param val 新しい SPSR の値
     */
    public void setSPSR(int val) {
        setReg(16, val);
    }

    /**
     * アドレシングモード 1 - データ処理オペランドを取得します。
     *
     * アセンブラでは shifter_operand と表されます。
     *
     * @param inst ARM 命令
     * @return シフタオペランド
     */
    public int getAddrMode1(Instruction inst) {
        boolean i = inst.getIBit();
        boolean b7 = inst.getBit(7);
        boolean b4 = inst.getBit(4);

        if (i) {
            //32bits イミディエート
            return getAddrMode1Imm(inst);
        } else if (!b4) {
            //イミディエートシフト
            return getAddrMode1ImmShift(inst);
        } else if (b4 && !b7) {
            //レジスタシフト
            return getAddrMode1RegShift(inst);
        } else {
            throw new IllegalArgumentException("Unknown addr mode1 " +
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
    public int getAddrMode1Imm(Instruction inst) {
        int rotR = inst.getField(8, 4);
        int imm8 = inst.getField(0, 8);

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
    public int getAddrMode1ImmShift(Instruction inst) {
        int shift_imm = inst.getField(7, 5);
        int shift = inst.getField(5, 2);
        int rm = inst.getRmField();

        switch (shift) {
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
            if (shift_imm == 0) {
                return 0;
            } else {
                return getReg(rm) >>> shift_imm;
            }
        case 2:
            //イミディエート算術右シフト
            if (shift_imm == 0) {
                if (BitOp.getBit32(getReg(rm), 31)) {
                    return 0xffffffff;
                } else {
                    return 0;
                }
            } else {
                return getReg(rm) >> shift_imm;
            }
        case 3:
            if (shift_imm == 0) {
                //拡張付き右ローテート
                if (getCPSR().getCBit()) {
                    return (1 << 31) | (getReg(rm) >>> 1);
                } else {
                    return getReg(rm) >>> 1;
                }
            } else {
                //イミディエート右ローテート
                return Integer.rotateRight(getReg(rm), shift_imm);
            }
        default:
            //do nothing
            break;
        }

        throw new IllegalArgumentException("Unknown Imm Shift " +
                String.format("0x%08x, shift:%d.",
                        inst.getInst(), shift));
    }

    /**
     * アドレシングモード 1 - データ処理オペランド、
     * レジスタシフトを取得します。
     *
     * 条件:
     * I ビット（ビット[25]）: 0
     * ビット[4]: 1
     *
     * 該当するオペランド:
     * 数値はビット[6:4] の値を示す。
     * 0b001: データ処理オペランド - レジスタ論理左シフト
     * 0b011: データ処理オペランド - レジスタ論理右シフト
     * 0b101: データ処理オペランド - レジスタ算術右シフト
     * 0b111: データ処理オペランド - レジスタ右ローテート
     *
     * @param inst ARM 命令
     * @return レジスタシフトオペランド
     */
    public int getAddrMode1RegShift(Instruction inst) {
        int shift = inst.getField(5, 2);
        int rs = inst.getField(8, 4);
        int rm = inst.getRmField();
        int valRs, valRsLow;

        //Rs[7:0]
        valRs = getReg(rs) & 0xff;
        //Rs[4:0]
        valRsLow = getReg(rs) & 0x1f;

        switch (shift) {
        case 0:
            //レジスタ論理左シフト
            if (valRs == 0) {
                return getReg(rm);
            } else if (valRs < 32) {
                return getReg(rm) << valRs;
            } else {
                return 0;
            }
        case 1:
            //レジスタ論理右シフト
            if (valRs == 0) {
                return getReg(rm);
            } else if (valRs < 32) {
                return getReg(rm) >>> valRs;
            } else {
                return 0;
            }
        case 2:
            //レジスタ算術右シフト
            if (valRs == 0) {
                return getReg(rm);
            } else if (valRs < 32) {
                return getReg(rm) >> valRs;
            } else {
                if (BitOp.getBit32(getReg(rm), 31)) {
                    return 0xffffffff;
                } else {
                    return 0;
                }
            }
        case 3:
            //レジスタ右ローテート
            if (valRs == 0) {
                return getReg(rm);
            } else if (valRsLow == 0) {
                return getReg(rm);
            } else {
                return Integer.rotateRight(getReg(rm), valRsLow);
            }
        default:
            //do nothing
            break;
        }

        throw new IllegalArgumentException("Unknown Reg Shift " +
                String.format("0x%08x, shift:%d.",
                        inst.getInst(), shift));
    }

    /**
     * アドレシングモード 1 - データ処理オペランドのキャリーアウトを取得します。
     *
     * @param inst ARM 命令
     * @return キャリーアウトがあれば true、なければ false
     */
    public boolean getAddrMode1Carry(Instruction inst) {
        boolean i = inst.getIBit();
        boolean b7 = inst.getBit(7);
        boolean b4 = inst.getBit(4);

        if (i) {
            //32bits イミディエート
            return getAddrMode1CarryImm(inst);
        } else if (!b4) {
            //イミディエートシフト
            return getAddrMode1CarryImmShift(inst);
        } else if (b4 && !b7) {
            //レジスタシフト
            return getAddrMode1CarryRegShift(inst);
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
     * @return キャリーアウトする場合は true、そうでなければ false
     */
    public boolean getAddrMode1CarryImm(Instruction inst) {
        int rotR = inst.getField(8, 4);

        if (rotR == 0) {
            return getCPSR().getCBit();
        } else {
            return BitOp.getBit32(getAddrMode1Imm(inst), 31);
        }
    }

    /**
     * アドレシングモード 1 - データ処理オペランド、
     * イミディエートシフトのキャリーアウトを取得します。
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
     * @return キャリーアウトする場合は true、そうでなければ false
     */
    public boolean getAddrMode1CarryImmShift(Instruction inst) {
        int shift_imm = inst.getField(7, 5);
        int shift = inst.getField(5, 2);
        int rm = inst.getRmField();

        switch (shift) {
        case 0:
            if (shift_imm == 0) {
                //レジスタ
                return getCPSR().getCBit();
            } else {
                //イミディエート論理左シフト
                return BitOp.getBit32(getReg(rm), 32 - shift_imm);
            }
        case 1:
            //イミディエート論理右シフト
            if (shift_imm == 0) {
                return BitOp.getBit32(getReg(rm), 31);
            } else {
                return BitOp.getBit32(getReg(rm), shift_imm - 1);
            }
        case 2:
            //イミディエート算術右シフト
            if (shift_imm == 0) {
                return BitOp.getBit32(getReg(rm), 31);
            } else {
                return BitOp.getBit32(getReg(rm), shift_imm - 1);
            }
        case 3:
            if (shift_imm == 0) {
                //拡張付き右ローテート
                return BitOp.getBit32(getReg(rm), 0);
            } else {
                //イミディエート右ローテート
                return BitOp.getBit32(getReg(rm), shift_imm - 1);
            }
        default:
            //do nothing
            break;
        }

        throw new IllegalArgumentException("Unknown Imm Shift " +
                String.format("0x%08x, shift:%d.",
                        inst.getInst(), shift));
    }

    public boolean getAddrMode1CarryRegShift(Instruction inst) {
        int shift = inst.getField(5, 2);
        int rs = inst.getField(8, 4);
        int rm = inst.getRmField();
        int valRs, valRsLow;

        //Rs[7:0]
        valRs = getReg(rs) & 0xff;
        //Rs[4:0]
        valRsLow = getReg(rs) & 0x1f;

        switch (shift) {
        case 0:
            //レジスタ論理左シフト
            if (valRs == 0) {
                return getCPSR().getCBit();
            } else if (valRs <= 32) {
                return BitOp.getBit32(getReg(rm), 32 - valRs);
            } else {
                return false;
            }
        case 1:
            //レジスタ論理右シフト
            if (valRs == 0) {
                return getCPSR().getCBit();
            } else if (valRs <= 32) {
                return BitOp.getBit32(getReg(rm), valRs - 1);
            } else {
                return false;
            }
        case 2:
            //レジスタ算術右シフト
            if (valRs == 0) {
                return getCPSR().getCBit();
            } else if (valRs <= 32) {
                return BitOp.getBit32(getReg(rm), valRs - 1);
            } else {
                return BitOp.getBit32(getReg(rm), 31);
            }
        case 3:
            //レジスタ右ローテート
            if (valRs == 0) {
                return getCPSR().getCBit();
            } else if (valRsLow == 0) {
                return BitOp.getBit32(getReg(rm), 31);
            } else {
                return BitOp.getBit32(getReg(rm), valRsLow - 1);
            }
        default:
            //do nothing
            break;
        }

        throw new IllegalArgumentException("Unknown Reg Shift " +
                String.format("0x%08x, shift:%d.",
                        inst.getInst(), shift));
    }

    /**
     * アドレシングモード 1 - データ処理オペランドの名前を取得します。
     *
     * @param inst ARM 命令
     * @return シフタオペランドの名前
     */
    public String getAddrMode1Name(Instruction inst) {
        boolean i = inst.getIBit();
        boolean b7 = inst.getBit(7);
        boolean b4 = inst.getBit(4);

        if (i) {
            //32bits イミディエート
            return getAddrMode1ImmName(inst);
        } else if (!b4) {
            //イミディエートシフト
            return getAddrMode1ImmShiftName(inst);
        } else if (b4 && !b7) {
            //レジスタシフト
            return getAddrMode1RegShiftName(inst);
        } else {
            throw new IllegalArgumentException("Unknown addr mode1 " +
                    String.format("0x%08x, I:%b, b7:%b, b4:%b.",
                            inst.getInst(), i, b7, b4));
        }
    }

    /**
     * アドレシングモード 1 - データ処理オペランド、
     * 32ビットイミディエートの文字列表現を取得します。
     *
     * @param inst 命令コード
     * @return イミディエートの文字列表現
     */
    public String getAddrMode1ImmName(Instruction inst) {
        int imm32 = getAddrMode1Imm(inst);

        return String.format("#%d    ; 0x%x", imm32, imm32);
    }

    /**
     * アドレシングモード 1 - データ処理オペランド、
     * イミディエートシフトの名前を取得します。
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
     * @return イミディエートシフトオペランドの名前
     */
    public String getAddrMode1ImmShiftName(Instruction inst) {
        int shift_imm = inst.getField(7, 5);
        int shift = inst.getField(5, 2);
        int rm = inst.getRmField();

        switch (shift) {
        case 0:
            if (shift_imm == 0) {
                //レジスタ
                return getRegName(rm);
            } else {
                //イミディエート論理左シフト
                return String.format("%s, lsl #%d",
                        getRegName(rm), shift_imm);
            }
        case 1:
            //イミディエート論理右シフト
            return String.format("%s, lsr #%d",
                    getRegName(rm), shift_imm);
        case 2:
            //イミディエート算術右シフト
            return String.format("%s, asr #%d",
                    getRegName(rm), shift_imm);
        case 3:
            if (shift_imm == 0) {
                //拡張付き右ローテート
                return String.format("%s, rrx",
                        getRegName(rm));
            } else {
                //イミディエート右ローテート
                return String.format("%s, ror #%d",
                        getRegName(rm), shift_imm);
            }
        default:
            //do nothing
            break;
        }

        throw new IllegalArgumentException("Unknown Imm Shift " +
                String.format("0x%08x, shift:%d.",
                        inst.getInst(), shift));
    }

    /**
     * アドレシングモード 1 - データ処理オペランド、
     * レジスタシフトの名前を取得します。
     *
     * 条件:
     * I ビット（ビット[25]）: 0
     * ビット[4]: 1
     *
     * 該当するオペランド:
     * 数値はビット[6:4] の値を示す。
     * 0b001: データ処理オペランド - レジスタ論理左シフト
     * 0b011: データ処理オペランド - レジスタ論理右シフト
     * 0b101: データ処理オペランド - レジスタ算術右シフト
     * 0b111: データ処理オペランド - レジスタ右ローテート
     *
     * @param inst ARM 命令
     * @return レジスタシフトオペランドの名前
     */
    public String getAddrMode1RegShiftName(Instruction inst) {
        int shift = inst.getField(5, 2);
        int rs = inst.getField(8, 4);
        int rm = inst.getRmField();

        switch (shift) {
        case 0:
            //レジスタ論理左シフト
            return String.format("%s, lsl %s",
                    getRegName(rm), getRegName(rs));
        case 1:
            //レジスタ論理右シフト
            return String.format("%s, lsr %s",
                    getRegName(rm), getRegName(rs));
        case 2:
            //レジスタ算術右シフト
            return String.format("%s, asr %s",
                    getRegName(rm), getRegName(rs));
        case 3:
            //レジスタ右ローテート
            return String.format("%s, ror %s",
                    getRegName(rm), getRegName(rs));
        default:
            //do nothing
            break;
        }

        throw new IllegalArgumentException("Unknown Reg Shift " +
                String.format("0x%08x, shift:%d.",
                        inst.getInst(), shift));
    }

    /**
     * アドレシングモード 2 - ワードまたは符号無しバイトロード/ストア、
     * を取得します。
     *
     * I ビットの意味がデータ処理命令と逆で、
     * I=0 のときイミディエートオフセット、
     * I=1 のときレジスタオフセットを表します。
     *
     * オフセット、プリインデクスの場合、
     * アクセス先のアドレスを返します。
     *
     * ポストインデクスの場合、
     * 更新後のベースレジスタの値を返します。
     *
     * @param inst ARM 命令
     * @return アドレス
     */
    public int getAddrMode2(Instruction inst) {
        boolean i = inst.getIBit();
        boolean u = inst.getBit(23);
        int rn = inst.getRnField();
        int shift_imm = inst.getField(7, 5);
        int shift = inst.getField(5, 2);
        int offset;

        if (!i) {
            //12bits イミディエートオフセット
            //I ビットの意味がデータ処理命令と逆なので注意！
            offset = getAddrMode2Imm(inst);
        } else if (shift_imm == 0 && shift == 0) {
            //レジスタオフセット/インデクス
            offset = getAddrMode2Reg(inst);
        } else {
            //スケーリング済みレジスタオフセット/インデクス
            offset = getAddrMode2Scaled(inst);
        }

        if (!u) {
            offset = -offset;
        }

        return getReg(rn) + offset;
    }

    /**
     * アドレシングモード 2 - ワードまたは符号無しバイトロード/ストア、
     * 12bits イミディエートオフセットアドレスを取得します。
     *
     * @param inst ARM 命令
     * @return イミディエートオフセットアドレス
     */
    public int getAddrMode2Imm(Instruction inst) {
        int offset12 = inst.getField(0, 12);

        return offset12;
    }

    /**
     * アドレシングモード 2 - ワードまたは符号無しバイトロード/ストア、
     * レジスタオフセットアドレスを取得します。
     *
     * アドレシングモード 1 - イミディエートシフトの、
     * shifter_operand の取得と同じ処理です。
     *
     * @param inst ARM 命令
     * @return レジスタオフセットアドレス
     */
    public int getAddrMode2Reg(Instruction inst) {
        return getAddrMode1ImmShift(inst);
    }

    /**
     * アドレシングモード 2 - ワードまたは符号無しバイトロード/ストア、
     * スケーリング済みレジスタオフセットアドレスを取得します。
     *
     * アドレシングモード 1 - イミディエートシフトの、
     * shifter_operand の取得と同じ処理です。
     *
     * @param inst ARM 命令
     * @return スケーリング済みレジスタオフセットアドレス
     */
    public int getAddrMode2Scaled(Instruction inst) {
        return getAddrMode1ImmShift(inst);
    }

    /**
     * アドレシングモード 2 - ワードまたは符号無しバイトロード/ストア、
     * の文字列表記を取得します。
     *
     * I ビットの意味がデータ処理命令と逆で、
     * I=0 のときイミディエートオフセット、
     * I=1 のときレジスタオフセットを表します。
     *
     * @param inst ARM 命令
     * @return アドレスの文字列表記
     */
    public String getAddrMode2Name(Instruction inst) {
        boolean i = inst.getIBit();
        boolean p = inst.getBit(24);
        boolean u = inst.getBit(23);
        boolean b = inst.getBit(22);
        boolean w = inst.getBit(21);
        int rn = inst.getRnField();
        int shift_imm = inst.getField(7, 5);
        int shift = inst.getField(5, 2);
        int rm = inst.getRmField();
        String strOffset;

        if (!i) {
            //12bits イミディエートオフセット
            //I ビットの意味がデータ処理命令と逆なので注意！
            return getAddrMode2ImmName(inst);
        } else if (shift_imm == 0 && shift == 0) {
            //レジスタオフセット/インデクス
            strOffset = getAddrMode2RegName(inst);
        } else {
            //スケーリング済みレジスタオフセット/インデクス
            strOffset = getAddrMode2ScaledName(inst);
        }

        if (p && !w) {
            //オフセット
            return String.format("[%s, %s%s]",
                    getRegName(rn), (u) ? "" : "-",
                    strOffset);
        } else if (p && w) {
            //プリインデクス
            return String.format("[%s, %s%s]!",
                    getRegName(rn), (u) ? "" : "-",
                    strOffset);
        } else if (!p) {
            //ポストインデクス
            return String.format("[%s], %s%s",
                    getRegName(rn), (u) ? "" : "-",
                    strOffset);
        } else {
            throw new IllegalArgumentException("Illegal P,W bits " +
                    String.format("p:%b, w:%b.", p, w));
        }
    }

    /**
     * アドレシングモード 2 - ワードまたは符号無しバイトロード/ストア、
     * 12bits イミディエートオフセットアドレスの、
     * 文字列表記を取得します。
     *
     * 下記の特殊記法を採用しているため、個別に処理しています。
     * 正負符号の前に # 記号が入る。
     * 角括弧の後に 16進数表記が入る。
     *
     * @param inst ARM 命令
     * @return イミディエートオフセットアドレスの文字列表記
     */
    public String getAddrMode2ImmName(Instruction inst) {
        boolean p = inst.getBit(24);
        boolean u = inst.getBit(23);
        boolean b = inst.getBit(22);
        boolean w = inst.getBit(21);
        int rn = inst.getRnField();
        int offset12 = inst.getField(0, 12);

        if (p && !w) {
            //イミディエートオフセット
            return String.format("[%s, #%s%d]    ; 0x%x",
                    getRegName(rn), (u) ? "" : "-",
                    offset12, offset12);
        } else if (p && w) {
            //プリインデクスイミディエート
            return String.format("[%s, #%s%d]!    ; 0x%x",
                    getRegName(rn), (u) ? "" : "-",
                    offset12, offset12);
        } else if (!p) {
            //ポストインデクスイミディエート
            return String.format("[%s], #%s%d    ; 0x%x",
                    getRegName(rn), (u) ? "" : "-",
                    offset12, offset12);
        } else {
            throw new IllegalArgumentException("Illegal P,W bits " +
                    String.format("p:%b, w:%b.", p, w));
        }
    }

    /**
     * アドレシングモード 2 - ワードまたは符号無しバイトロード/ストア、
     * レジスタオフセットアドレスを取得します。
     *
     * アドレシングモード 1 - イミディエートシフトの、
     * shifter_operand の取得と同じ処理です。
     *
     * @param inst ARM 命令
     * @return レジスタオフセットアドレス
     */
    public String getAddrMode2RegName(Instruction inst) {
        return getAddrMode1ImmShiftName(inst);
    }

    /**
     * アドレシングモード 2 - ワードまたは符号無しバイトロード/ストア、
     * スケーリング済みレジスタオフセットの文字列表記を取得します。
     *
     * アドレシングモード 1 - イミディエートシフトの、
     * shifter_operand の取得と同じ処理です。
     *
     * @param inst ARM 命令
     * @return アドレスの文字列表記
     */
    public String getAddrMode2ScaledName(Instruction inst) {
        return getAddrMode1ImmShiftName(inst);
    }

    /**
     * アドレシングモード 3 - ハーフワードロード/ストア、符号付きバイトロード、
     * 転送開始アドレスを取得します。
     *
     * @param inst ARM 命令
     * @return アドレス
     */
    public int getAddrMode3(Instruction inst) {
        boolean u = inst.getBit(23);
        boolean b = inst.getBit(22);
        int rn = inst.getRnField();
        int offset;

        if (b) {
            //イミディエートオフセット/インデクス
            offset = getAddrMode3Imm(inst);
        } else {
            //レジスタオフセット/インデクス
            offset = getAddrMode3Reg(inst);
        }

        if (!u) {
            offset = -offset;
        }

        return getReg(rn) + offset;
    }

    /**
     * アドレシングモード 3 - ハーフワードロード/ストア、符号付きバイトロード、
     * イミディエートオフセット/インデクスの転送開始アドレスを取得します。
     *
     * @param inst ARM 命令
     * @return アドレス
     */
    public int getAddrMode3Imm(Instruction inst) {
        int immh = inst.getField(8, 4);
        int imml = inst.getField(0, 4);

        return (immh << 4) | imml;
    }

    /**
     * アドレシングモード 3 - ハーフワードロード/ストア、符号付きバイトロード、
     * レジスタオフセット/インデクスの転送開始アドレスを取得します。
     *
     * @param inst ARM 命令
     * @return アドレス
     */
    public int getAddrMode3Reg(Instruction inst) {
        int rm = inst.getRmField();

        return getReg(rm);
    }

    /**
     * アドレシングモード 3 - ハーフワードロード/ストア、符号付きバイトロード、
     * 文字列表記を取得します。
     *
     * @param inst ARM 命令
     * @return アドレスの文字列表記
     */
    public String getAddrMode3Name(Instruction inst) {
        boolean b = inst.getBit(22);

        if (b) {
            //イミディエートオフセット/インデクス
            return getAddrMode3ImmName(inst);
        } else {
            //レジスタオフセット/インデクス
            return getAddrMode3RegName(inst);
        }
    }

    /**
     * アドレシングモード 3 - ハーフワードロード/ストア、符号付きバイトロード、
     * イミディエートオフセット/インデクスの文字列表記を取得します。
     *
     * @param inst ARM 命令
     * @return アドレスの文字列表記
     */
    public String getAddrMode3ImmName(Instruction inst) {
        boolean p = inst.getBit(24);
        boolean u = inst.getBit(23);
        boolean w = inst.getBit(21);
        int rn = inst.getRnField();
        int imm8 = getAddrMode3Imm(inst);

        if (p && !w) {
            //オフセット
            return String.format("[%s, #%s%d]    ; 0x%x",
                    getRegName(rn), (u) ? "" : "-",
                    imm8, imm8);
        } else if (p && w) {
            //プリインデクス
            return String.format("[%s, #%s%d]!    ; 0x%x",
                    getRegName(rn), (u) ? "" : "-",
                    imm8, imm8);
        } else if (!p) {
            //ポストインデクス
            return String.format("[%s], #%s%d    ; 0x%x",
                    getRegName(rn), (u) ? "" : "-",
                    imm8, imm8);
        } else {
            throw new IllegalArgumentException("Illegal P,W bits " +
                    String.format("p:%b, w:%b.", p, w));
        }
    }

    /**
     * アドレシングモード 3 - ハーフワードロード/ストア、符号付きバイトロード、
     * レジスタオフセット/インデクスの文字列表記を取得します。
     *
     * @param inst ARM 命令
     * @return アドレスの文字列表記
     */
    public String getAddrMode3RegName(Instruction inst) {
        boolean p = inst.getBit(24);
        boolean u = inst.getBit(23);
        boolean w = inst.getBit(21);
        int rn = inst.getRnField();
        int rm = inst.getRmField();

        if (p && !w) {
            //オフセット
            return String.format("[%s, %s%s]",
                    getRegName(rn), (u) ? "" : "-",
                    getRegName(rm));
        } else if (p && w) {
            //プリインデクス
            return String.format("[%s, %s%s]!",
                    getRegName(rn), (u) ? "" : "-",
                    getRegName(rm));
        } else if (!p) {
            //ポストインデクス
            return String.format("[%s], %s%s",
                    getRegName(rn), (u) ? "" : "-",
                    getRegName(rm));
        } else {
            throw new IllegalArgumentException("Illegal P,W bits " +
                    String.format("p:%b, w:%b.", p, w));
        }
    }

    /**
     * アドレシングモード 4 - ロード/ストアマルチプル、
     * 転送開始アドレスを取得します。
     *
     * @param pu    P, U ビット
     * @param rn    レジスタ番号
     * @param rlist レジスタリスト
     * @return 転送開始アドレス
     */
    public int getAddrMode4StartAddress(int pu, int rn, int rlist) {
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
            //do nothing
            break;
        }

        throw new IllegalArgumentException("Illegal PU field " +
                pu + ".");
    }

    /**
     * アドレシングモード 4 - ロード/ストアマルチプル、
     * 転送するデータの長さを取得します。
     *
     * @param pu    P, U ビット
     * @param rlist レジスタリスト
     * @return 転送するデータの長さ
     */
    public int getAddrMode4Length(int pu, int rlist) {
        switch (pu) {
        case Instruction.PU_ADDR4_IA:
        case Instruction.PU_ADDR4_IB:
            return Integer.bitCount(rlist) * 4;
        case Instruction.PU_ADDR4_DA:
        case Instruction.PU_ADDR4_DB:
            return -(Integer.bitCount(rlist) * 4);
        default:
            //do nothing
            break;
        }

        throw new IllegalArgumentException("Illegal PU field " +
                pu + ".");
    }

    /**
     * ステータスレジスタから汎用レジスタへの転送命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeMrs(Instruction inst, boolean exec) {
        boolean r = inst.getBit(22);
        int sbo = inst.getField(16, 4);
        int rd = inst.getRdField();
        int dest;

        if (!exec) {
            disasmInst(inst,
                    String.format("mrs%s", inst.getCondFieldName()),
                    String.format("%s, %s",
                            getRegName(rd), (r) ? "spsr" : "cpsr"));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        if (sbo != 0xf) {
            System.out.println("Warning: Illegal instruction, " +
                    String.format("mrs SBO[19:16](0x%01x) != 0xf.", sbo));
        }

        if (r) {
            dest = getSPSR().getValue();
        } else {
            dest = getCPSR().getValue();
        }

        setReg(rd, dest);
    }

    /**
     * ステータスレジスタへの値の転送命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeMsr(Instruction inst, boolean exec) {
        boolean i = inst.getIBit();
        boolean r = inst.getBit(22);
        boolean mask_f = inst.getBit(19);
        boolean mask_s = inst.getBit(18);
        boolean mask_x = inst.getBit(17);
        boolean mask_c = inst.getBit(16);
        int sbo = inst.getField(12, 4);
        int opr = getAddrMode1(inst);
        int dest, m = 0;

        if (!exec) {
            disasmInst(inst,
                    String.format("msr%s", inst.getCondFieldName()),
                    String.format("%s_%s%s%s%s, %s",
                            (r) ? "SPSR" : "CPSR",
                            (mask_f) ? "f" : "",
                            (mask_s) ? "s" : "",
                            (mask_x) ? "x" : "",
                            (mask_c) ? "c" : "",
                            getAddrMode1Name(inst)));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        if (sbo != 0xf) {
            System.out.println("Warning: Illegal instruction, " +
                    String.format("msr SBO[15:12](0x%01x) != 0xf.", sbo));
        }

        if (!r) {
            dest = getCPSR().getValue();
        } else {
            dest = getSPSR().getValue();
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
        dest &= ~m;
        dest |= opr & m;

        if (!r) {
            setCPSR(dest);
        } else {
            setSPSR(dest);
        }
    }

    /**
     * データ処理命令を実行します。
     *
     * 下記の種類の命令を扱います。
     * and, eor, sub, rsb,
     * add, adc, sbc, rsc,
     * tst, teq, cmp, cmn,
     * orr, mov, bic, mvn,
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     * @param id   オペコードフィールドと S ビットが示す演算の ID
     */
    public void executeALU(Instruction inst, boolean exec, int id) {
        boolean s = inst.getSBit();
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        String strInst, strOperand;

        if (!exec) {
            switch (id) {
            case Instruction.OPCODE_S_ADC:
            case Instruction.OPCODE_S_ADD:
            case Instruction.OPCODE_S_AND:
            case Instruction.OPCODE_S_BIC:
            case Instruction.OPCODE_S_EOR:
            case Instruction.OPCODE_S_ORR:
            case Instruction.OPCODE_S_RSB:
            case Instruction.OPCODE_S_RSC:
            case Instruction.OPCODE_S_SBC:
            case Instruction.OPCODE_S_SUB:
                //with S bit
                strInst = String.format("%s%s%s", inst.getOpcodeFieldName(),
                        inst.getCondFieldName(),
                        (s) ? "s" : "");
                //rd, rn, shifter_operand
                strOperand = String.format("%s, %s, %s", getRegName(rd),
                        getRegName(rn), getAddrMode1Name(inst));
                break;
            case Instruction.OPCODE_S_MOV:
            case Instruction.OPCODE_S_MVN:
                //with S bit
                strInst = String.format("%s%s%s", inst.getOpcodeFieldName(),
                        inst.getCondFieldName(),
                        (s) ? "s" : "");
                //rd, shifter_operand
                strOperand = String.format("%s, %s", getRegName(rd),
                        getAddrMode1Name(inst));
                break;
            case Instruction.OPCODE_S_CMN:
            case Instruction.OPCODE_S_CMP:
            case Instruction.OPCODE_S_TEQ:
            case Instruction.OPCODE_S_TST:
                //S bit is 1
                strInst = String.format("%s%s", inst.getOpcodeFieldName(),
                        inst.getCondFieldName());
                //rn, shifter_operand
                strOperand = String.format("%s, %s", getRegName(rn),
                        getAddrMode1Name(inst));
                break;
            default:
                throw new IllegalArgumentException("Unknown opcode S-bit ID " +
                        String.format("%d.", id));
            }
            disasmInst(inst, strInst, strOperand);

            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        switch (id) {
        case Instruction.OPCODE_S_AND:
            executeALUAnd(inst, exec);
            break;
        case Instruction.OPCODE_S_EOR:
            executeALUEor(inst, exec);
            break;
        case Instruction.OPCODE_S_SUB:
            executeALUSub(inst, exec);
            break;
        case Instruction.OPCODE_S_RSB:
            executeALURsb(inst, exec);
            break;
        case Instruction.OPCODE_S_ADD:
            executeALUAdd(inst, exec);
            break;
        case Instruction.OPCODE_S_ADC:
            executeALUAdc(inst, exec);
            break;
        case Instruction.OPCODE_S_SBC:
            executeALUSbc(inst, exec);
            break;
        case Instruction.OPCODE_S_RSC:
            executeALURsc(inst, exec);
            break;
        case Instruction.OPCODE_S_TST:
            executeALUTst(inst, exec);
            break;
        case Instruction.OPCODE_S_TEQ:
            executeALUTeq(inst, exec);
            break;
        case Instruction.OPCODE_S_CMP:
            executeALUCmp(inst, exec);
            break;
        case Instruction.OPCODE_S_CMN:
            executeALUCmn(inst, exec);
            break;
        case Instruction.OPCODE_S_ORR:
            executeALUOrr(inst, exec);
            break;
        case Instruction.OPCODE_S_MOV:
            executeALUMov(inst, exec);
            break;
        case Instruction.OPCODE_S_BIC:
            executeALUBic(inst, exec);
            break;
        case Instruction.OPCODE_S_MVN:
            executeALUMvn(inst, exec);
            break;
        default:
            throw new IllegalArgumentException("Unknown opcode S-bit ID " +
                    String.format("%d.", id));
        }
    }

    /**
     * 論理積命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeALUAnd(Instruction inst, boolean exec) {
        boolean s = inst.getSBit();
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int opr = getAddrMode1(inst);
        int left, right, dest;

        left = getReg(rn);
        right = opr;
        dest = left & right;

        if (s && rd == 15) {
            setCPSR(getSPSR());
        } else if (s) {
            getCPSR().setNBit(BitOp.getBit32(dest, 31));
            getCPSR().setZBit(dest == 0);
            getCPSR().setCBit(getAddrMode1Carry(inst));
            //V flag is unaffected
        }

        setReg(rd, dest);
    }

    /**
     * 排他的論理和命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeALUEor(Instruction inst, boolean exec) {
        boolean s = inst.getSBit();
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int opr = getAddrMode1(inst);
        int left, right, dest;

        left = getReg(rn);
        right = opr;
        dest = left ^ right;

        if (s && rd == 15) {
            setCPSR(getSPSR());
        } else if (s) {
            getCPSR().setNBit(BitOp.getBit32(dest, 31));
            getCPSR().setZBit(dest == 0);
            getCPSR().setCBit(getAddrMode1Carry(inst));
            //V flag is unaffected
        }

        setReg(rd, dest);
    }

    /**
     * 減算命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeALUSub(Instruction inst, boolean exec) {
        boolean s = inst.getSBit();
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int opr = getAddrMode1(inst);
        int left, right, dest;

        left = getReg(rn);
        right = opr;
        dest = left - right;

        if (s && rd == 15) {
            setCPSR(getSPSR());
        } else if (s) {
            getCPSR().setNBit(BitOp.getBit32(dest, 31));
            getCPSR().setZBit(dest == 0);
            getCPSR().setCBit(!borrowFrom32(left, right));
            getCPSR().setVBit(overflowFrom32(left, right, false));
        }

        setReg(rd, dest);
    }

    /**
     * 逆減算命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeALURsb(Instruction inst, boolean exec) {
        boolean s = inst.getSBit();
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int opr = getAddrMode1(inst);
        int left, right, dest;

        left = opr;
        right = getReg(rn);
        dest = left - right;

        if (s && rd == 15) {
            setCPSR(getSPSR());
        } else if (s) {
            getCPSR().setNBit(BitOp.getBit32(dest, 31));
            getCPSR().setZBit(dest == 0);
            getCPSR().setCBit(!borrowFrom32(left, right));
            getCPSR().setVBit(overflowFrom32(left, right, false));
        }

        setReg(rd, dest);
    }

    /**
     * 加算命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeALUAdd(Instruction inst, boolean exec) {
        boolean s = inst.getSBit();
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int opr = getAddrMode1(inst);
        int left, right, dest;

        left = getReg(rn);
        right = opr;
        dest = left + right;

        if (s && rd == 15) {
            setCPSR(getSPSR());
        } else if (s) {
            getCPSR().setNBit(BitOp.getBit32(dest, 31));
            getCPSR().setZBit(dest == 0);
            getCPSR().setCBit(carryFrom32(left, right));
            getCPSR().setVBit(overflowFrom32(left, right, true));
        }

        setReg(rd, dest);
    }

    /**
     * キャリー付き加算命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeALUAdc(Instruction inst, boolean exec) {
        boolean s = inst.getSBit();
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int opr = getAddrMode1(inst);
        int left, center, right, dest;

        left = getReg(rn);
        center = opr;
        right = BitOp.toInt(getCPSR().getCBit());
        dest = left + center + right;

        if (s && rd == 15) {
            setCPSR(getSPSR());
        } else if (s) {
            int left_center = left + center;
            boolean lc_c = carryFrom32(left, center);
            boolean lc_v = overflowFrom32(left, center, true);

            getCPSR().setNBit(BitOp.getBit32(dest, 31));
            getCPSR().setZBit(dest == 0);
            getCPSR().setCBit(lc_c || carryFrom32(left_center, right));
            getCPSR().setVBit(lc_v || overflowFrom32(left_center, right, true));
        }

        setReg(rd, dest);
    }

    /**
     * キャリー付き減算命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeALUSbc(Instruction inst, boolean exec) {
        boolean s = inst.getSBit();
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int opr = getAddrMode1(inst);
        int left, center, right, dest;

        left = getReg(rn);
        center = opr;
        right = BitOp.toInt(!getCPSR().getCBit());
        dest = left - center - right;

        if (s && rd == 15) {
            setCPSR(getSPSR());
        } else if (s) {
            int left_center = left - center;
            boolean lc_c = borrowFrom32(left, center);
            boolean lc_v = overflowFrom32(left, center, false);

            getCPSR().setNBit(BitOp.getBit32(dest, 31));
            getCPSR().setZBit(dest == 0);
            getCPSR().setCBit(!(lc_c || borrowFrom32(left_center, right)));
            getCPSR().setVBit(lc_v || overflowFrom32(left_center, right, false));
        }

        setReg(rd, dest);
    }

    /**
     * キャリー付き逆減算命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeALURsc(Instruction inst, boolean exec) {
        boolean s = inst.getSBit();
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int opr = getAddrMode1(inst);
        int left, center, right, dest;

        left = opr;
        center = getReg(rn);
        right = BitOp.toInt(!getCPSR().getCBit());
        dest = left - center - right;

        if (s && rd == 15) {
            setCPSR(getSPSR());
        } else if (s) {
            int left_center = left - center;
            boolean lc_c = borrowFrom32(left, center);
            boolean lc_v = overflowFrom32(left, center, false);

            getCPSR().setNBit(BitOp.getBit32(dest, 31));
            getCPSR().setZBit(dest == 0);
            getCPSR().setCBit(!(lc_c || borrowFrom32(left_center, right)));
            getCPSR().setVBit(lc_v || overflowFrom32(left_center, right, false));
        }

        setReg(rd, dest);
    }

    /**
     * テスト命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeALUTst(Instruction inst, boolean exec) {
        int rn = inst.getRnField();
        int sbz = inst.getField(12, 4);
        int opr = getAddrMode1(inst);
        int left, right, dest;

        if (sbz != 0x0) {
            System.out.println("Warning: Illegal instruction, " +
                    String.format("tst SBZ[15:12](0x%01x) != 0x0.", sbz));
        }

        left = getReg(rn);
        right = opr;
        dest = left & right;

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        getCPSR().setCBit(getAddrMode1Carry(inst));
        //V flag is unaffected
    }

    /**
     * 等価テスト命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeALUTeq(Instruction inst, boolean exec) {
        int rn = inst.getRnField();
        int sbz = inst.getField(12, 4);
        int opr = getAddrMode1(inst);
        int left, right, dest;

        if (sbz != 0x0) {
            System.out.println("Warning: Illegal instruction, " +
                    String.format("teq SBZ[15:12](0x%01x) != 0x0.", sbz));
        }

        left = getReg(rn);
        right = opr;
        dest = left ^ right;

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        getCPSR().setCBit(getAddrMode1Carry(inst));
        //V flag is unaffected
    }

    /**
     * 比較命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeALUCmp(Instruction inst, boolean exec) {
        int rn = inst.getRnField();
        int sbz = inst.getField(12, 4);
        int opr = getAddrMode1(inst);
        int left, right, dest;

        if (sbz != 0x0) {
            System.out.println("Warning: Illegal instruction, " +
                    String.format("cmp SBZ[15:12](0x%01x) != 0x0.", sbz));
        }

        left = getReg(rn);
        right = opr;
        dest = left - right;

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        getCPSR().setCBit(!borrowFrom32(left, right));
        getCPSR().setVBit(overflowFrom32(left, right, false));
    }

    /**
     * 比較否定命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeALUCmn(Instruction inst, boolean exec) {
        int rn = inst.getRnField();
        int sbz = inst.getField(12, 4);
        int opr = getAddrMode1(inst);
        int left, right, dest;

        if (sbz != 0x0) {
            System.out.println("Warning: Illegal instruction, " +
                    String.format("cmp SBZ[15:12](0x%01x) != 0x0.", sbz));
        }

        left = getReg(rn);
        right = opr;
        dest = left + right;

        getCPSR().setNBit(BitOp.getBit32(dest, 31));
        getCPSR().setZBit(dest == 0);
        getCPSR().setCBit(carryFrom32(left, right));
        getCPSR().setVBit(overflowFrom32(left, right, true));
    }

    /**
     * 論理和命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeALUOrr(Instruction inst, boolean exec) {
        boolean s = inst.getSBit();
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int opr = getAddrMode1(inst);
        int left, right, dest;

        left = getReg(rn);
        right = opr;
        dest = left | right;

        if (s && rd == 15) {
            setCPSR(getSPSR());
        } else if (s) {
            getCPSR().setNBit(BitOp.getBit32(dest, 31));
            getCPSR().setZBit(dest == 0);
            getCPSR().setCBit(getAddrMode1Carry(inst));
            //V flag is unaffected
        }

        setReg(rd, dest);
    }

    /**
     * 移動命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeALUMov(Instruction inst, boolean exec) {
        boolean s = inst.getSBit();
        int sbz = inst.getField(16, 4);
        int rd = inst.getRdField();
        int opr = getAddrMode1(inst);
        int right, dest;

        if (sbz != 0x0) {
            System.out.println("Warning: Illegal instruction, " +
                    String.format("mov SBZ[19:16](0x%01x) != 0x0.", sbz));
        }

        right = opr;
        dest = right;

        if (s && rd == 15) {
            setCPSR(getSPSR());
        } else if (s) {
            getCPSR().setNBit(BitOp.getBit32(dest, 31));
            getCPSR().setZBit(dest == 0);
            getCPSR().setCBit(getAddrMode1Carry(inst));
            //V flag is unaffected
        }

        setReg(rd, dest);
    }

    /**
     * ビットクリア命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeALUBic(Instruction inst, boolean exec) {
        boolean s = inst.getSBit();
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int opr = getAddrMode1(inst);
        int left, right, dest;

        left = getReg(rn);
        right = opr;
        dest = left & ~right;

        if (s && rd == 15) {
            setCPSR(getSPSR());
        } else if (s) {
            getCPSR().setNBit(BitOp.getBit32(dest, 31));
            getCPSR().setZBit(dest == 0);
            getCPSR().setCBit(getAddrMode1Carry(inst));
            //V flag is unaffected
        }

        setReg(rd, dest);
    }

    /**
     * 移動否定命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeALUMvn(Instruction inst, boolean exec) {
        boolean s = inst.getSBit();
        int rd = inst.getRdField();
        int opr = getAddrMode1(inst);
        int left, right, dest;

        right = opr;
        dest = ~right;

        if (s && rd == 15) {
            setCPSR(getSPSR());
        } else if (s) {
            getCPSR().setNBit(BitOp.getBit32(dest, 31));
            getCPSR().setZBit(dest == 0);
            getCPSR().setCBit(getAddrMode1Carry(inst));
            //V flag is unaffected
        }

        setReg(rd, dest);
    }

    /**
     * 積和命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeMla(Instruction inst, boolean exec) {
        boolean s = inst.getSBit();
        int rd = inst.getField(16, 4);
        int rn = inst.getField(12, 4);
        int rs = inst.getField(8, 4);
        int rm = inst.getRmField();
        int left, center, right, dest;

        if (!exec) {
            disasmInst(inst,
                    String.format("mla%s%s", inst.getCondFieldName(),
                            (s) ? "s" : ""),
                    String.format("%s, %s, %s, %s",
                            getRegName(rd), getRegName(rm),
                            getRegName(rs), getRegName(rn)));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        left = getReg(rm);
        center = getReg(rs);
        right = getReg(rn);
        dest = left * center + right;

        if (s) {
            getCPSR().setNBit(BitOp.getBit32(dest, 31));
            getCPSR().setZBit(dest == 0);
            //C flag is unaffected
            //V flag is unaffected
        }

        setReg(rd, dest);
    }

    /**
     * 乗算命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeMul(Instruction inst, boolean exec) {
        boolean s = inst.getSBit();
        int rd = inst.getField(16, 4);
        int rs = inst.getField(8, 4);
        int rm = inst.getRmField();
        int left, right, dest;

        if (!exec) {
            disasmInst(inst,
                    String.format("mul%s%s", inst.getCondFieldName(),
                            (s) ? "s" : ""),
                    String.format("%s, %s, %s",
                            getRegName(rd), getRegName(rm),
                            getRegName(rs)));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        left = getReg(rm);
        right = getReg(rs);
        dest = left * right;

        if (s) {
            getCPSR().setNBit(BitOp.getBit32(dest, 31));
            getCPSR().setZBit(dest == 0);
            //C flag is unaffected
            //V flag is unaffected
        }

        setReg(rd, dest);
    }

    /**
     * 符号付き積和ロング命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSmlal(Instruction inst, boolean exec) {
        boolean s = inst.getSBit();
        int rdhi = inst.getField(16, 4);
        int rdlo = inst.getRdField();
        int rs = inst.getField(8, 4);
        int rm = inst.getRmField();
        int left, right, desthi, destlo;
        long dest;

        if (!exec) {
            disasmInst(inst,
                    String.format("smlal%s%s", inst.getCondFieldName(),
                            (s) ? "s" : ""),
                    String.format("%s, %s, %s, %s",
                            getRegName(rdlo), getRegName(rdhi),
                            getRegName(rm), getRegName(rs)));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        left = getReg(rm);
        right = getReg(rs);
        dest = ((long)getReg(rdhi) << 32) + (getReg(rdlo) & 0xffffffffL);
        dest += (long)left * (long)right;
        desthi = (int)(dest >>> 32);
        destlo = (int)dest;

        if (s) {
            getCPSR().setNBit(BitOp.getBit32(desthi, 31));
            getCPSR().setZBit(dest == 0);
            //C flag is unaffected
            //V flag is unaffected
        }

        setReg(rdhi, desthi);
        setReg(rdlo, destlo);
    }

    /**
     * 符号付き乗算ロング命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSmull(Instruction inst, boolean exec) {
        boolean s = inst.getSBit();
        int rdhi = inst.getField(16, 4);
        int rdlo = inst.getRdField();
        int rs = inst.getField(8, 4);
        int rm = inst.getRmField();
        int left, right, desthi, destlo;
        long dest;

        if (!exec) {
            disasmInst(inst,
                    String.format("smull%s%s", inst.getCondFieldName(),
                            (s) ? "s" : ""),
                    String.format("%s, %s, %s, %s",
                            getRegName(rdlo), getRegName(rdhi),
                            getRegName(rm), getRegName(rs)));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        left = getReg(rm);
        right = getReg(rs);
        dest = (long)left * (long)right;
        desthi = (int)(dest >>> 32);
        destlo = (int)dest;

        if (s) {
            getCPSR().setNBit(BitOp.getBit32(desthi, 31));
            getCPSR().setZBit(dest == 0);
            //C flag is unaffected
            //V flag is unaffected
        }

        setReg(rdhi, desthi);
        setReg(rdlo, destlo);
    }

    /**
     * 符号無し積和ロング命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeUmlal(Instruction inst, boolean exec) {
        boolean s = inst.getSBit();
        int rdhi = inst.getField(16, 4);
        int rdlo = inst.getRdField();
        int rs = inst.getField(8, 4);
        int rm = inst.getRmField();
        int left, right, desthi, destlo;
        long dest;

        if (!exec) {
            disasmInst(inst,
                    String.format("umlal%s%s", inst.getCondFieldName(),
                            (s) ? "s" : ""),
                    String.format("%s, %s, %s, %s",
                            getRegName(rdlo), getRegName(rdhi),
                            getRegName(rm), getRegName(rs)));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        left = getReg(rm);
        right = getReg(rs);
        dest = ((long)getReg(rdhi) << 32) + (getReg(rdlo) & 0xffffffffL);
        dest += (left & 0xffffffffL) * (right & 0xffffffffL);
        desthi = (int)(dest >>> 32);
        destlo = (int)dest;

        if (s) {
            getCPSR().setNBit(BitOp.getBit32(desthi, 31));
            getCPSR().setZBit(dest == 0);
            //C flag is unaffected
            //V flag is unaffected
        }

        setReg(rdhi, desthi);
        setReg(rdlo, destlo);
    }

    /**
     * 符号無し乗算ロング命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeUmull(Instruction inst, boolean exec) {
        boolean s = inst.getSBit();
        int rdhi = inst.getField(16, 4);
        int rdlo = inst.getRdField();
        int rs = inst.getField(8, 4);
        int rm = inst.getRmField();
        int left, right, desthi, destlo;
        long dest;

        if (!exec) {
            disasmInst(inst,
                    String.format("umull%s%s", inst.getCondFieldName(),
                            (s) ? "s" : ""),
                    String.format("%s, %s, %s, %s",
                            getRegName(rdlo), getRegName(rdhi),
                            getRegName(rm), getRegName(rs)));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        left = getReg(rm);
        right = getReg(rs);
        dest = (left & 0xffffffffL) * (right & 0xffffffffL);
        desthi = (int)(dest >>> 32);
        destlo = (int)dest;

        if (s) {
            getCPSR().setNBit(BitOp.getBit32(desthi, 31));
            getCPSR().setZBit(dest == 0);
            //C flag is unaffected
            //V flag is unaffected
        }

        setReg(rdhi, desthi);
        setReg(rdlo, destlo);
    }

    /**
     * スワップ命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSwp(Instruction inst, boolean exec) {
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int rm = inst.getRmField();
        int left, right, rot;
        int vaddr, paddr;

        if (!exec) {
            disasmInst(inst,
                    String.format("swp%s", inst.getCondFieldName()),
                    String.format("%s, %s, [%s]",
                            getRegName(rd), getRegName(rm), getRegName(rn)));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        left = getReg(rm);
        rot = getReg(rn) & 0x3;

        vaddr = getReg(rn);
        paddr = getMMU().translate(vaddr, 4, false, getCPSR().isPrivMode(), false);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryRead(paddr) || !tryWrite(paddr)) {
            raiseException(EXCEPT_ABT_DATA,
                    String.format("swp [%08x]", paddr));
            return;
        }
        right = read32(paddr);

        switch (rot) {
        case 0:
            //do nothing
            break;
        case 1:
            right = Integer.rotateRight(right, 8);
            break;
        case 2:
            right = Integer.rotateRight(right, 16);
            break;
        case 3:
            right = Integer.rotateRight(right, 24);
            break;
        default:
            throw new IllegalArgumentException("Illegal address " +
                    String.format("inst:0x%08x, rn:%d, rot:%d.",
                            inst.getInst(), rn, rot));
        }

        write32(paddr, left);
        setReg(rd, right);
    }

    /**
     * 変換付きレジスタロード命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLdrt(Instruction inst, boolean exec) {
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int offset = getAddrMode2(inst);
        int vaddr, paddr, rot, value;

        if (!exec) {
            disasmInst(inst,
                    String.format("ldrt%s", inst.getCondFieldName()),
                    String.format("%s, %s", getRegName(rd),
                            getAddrMode2Name(inst)));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        //P ビットは必ず 0、ポストインデクス
        vaddr = getReg(rn);

        rot = vaddr & 0x3;

        paddr = getMMU().translate(vaddr, 4, false, false, true);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryRead(paddr)) {
            raiseException(EXCEPT_ABT_DATA,
                    String.format("ldrt [%08x]", paddr));
            return;
        }
        value = read32(paddr);

        switch (rot) {
        case 0:
            //do nothing
            break;
        case 1:
            value = Integer.rotateRight(value, 8);
            break;
        case 2:
            value = Integer.rotateRight(value, 16);
            break;
        case 3:
            value = Integer.rotateRight(value, 24);
            break;
        default:
            throw new IllegalArgumentException("Illegal address " +
                    String.format("inst:0x%08x, rot:%d.",
                            inst.getInst(), rot));
        }

        if (rd == 15) {
            setPC(value & 0xfffffffe);
            getCPSR().setTBit(BitOp.getBit32(value, 0));
        } else {
            setReg(rd, value);
        }

        //P ビットは必ず 0、W ビットは必ず 1、ベースレジスタを更新する
        setReg(rn, offset);
    }

    /**
     * 変換付きレジスタバイトロード命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLdrbt(Instruction inst, boolean exec) {
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int offset = getAddrMode2(inst);
        int vaddr, paddr, value;

        if (!exec) {
            disasmInst(inst,
                    String.format("ldrbt%s", inst.getCondFieldName()),
                    String.format("%s, %s", getRegName(rd),
                            getAddrMode2Name(inst)));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        //P ビットは必ず 0、ポストインデクス
        vaddr = getReg(rn);

        paddr = getMMU().translate(vaddr, 1, false, false, true);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryRead(paddr)) {
            raiseException(EXCEPT_ABT_DATA,
                    String.format("ldrbt [%08x]", paddr));
            return;
        }
        value = (int)(read8(paddr)) & 0xff;

        setReg(rd, value);

        //P ビットは必ず 0、W ビットは必ず 1、ベースレジスタを更新する
        setReg(rn, offset);
    }

    /**
     * レジスタバイトロード命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLdrb(Instruction inst, boolean exec) {
        boolean p = inst.getBit(24);
        boolean w = inst.getBit(21);
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int offset = getAddrMode2(inst);
        int vaddr, paddr, value;

        if (!exec) {
            disasmInst(inst,
                    String.format("ldrb%s", inst.getCondFieldName()),
                    String.format("%s, %s", getRegName(rd),
                            getAddrMode2Name(inst)));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        if (p) {
            //プリインデクス
            vaddr = offset;
        } else {
            //ポストインデクス
            vaddr = getReg(rn);
        }

        paddr = getMMU().translate(vaddr, 1, false, getCPSR().isPrivMode(), true);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryRead(paddr)) {
            raiseException(EXCEPT_ABT_DATA,
                    String.format("ldrb [%08x]", paddr));
            return;
        }
        value = (int)(read8(paddr)) & 0xff;

        setReg(rd, value);

        if (!p || w) {
            //ベースレジスタを更新する
            //条件は !(p && !w) と等価、つまり P, W ビットが
            //オフセットアドレス以外の指定なら Rn を書き換える
            setReg(rn, offset);
        }
    }

    /**
     * レジスタロード命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLdr(Instruction inst, boolean exec) {
        boolean p = inst.getBit(24);
        boolean w = inst.getBit(21);
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int offset = getAddrMode2(inst);
        int vaddr, paddr, rot, value;

        if (!exec) {
            disasmInst(inst,
                    String.format("ldr%s", inst.getCondFieldName()),
                    String.format("%s, %s", getRegName(rd),
                            getAddrMode2Name(inst)));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        if (p) {
            //プリインデクス
            vaddr = offset;
        } else {
            //ポストインデクス
            vaddr = getReg(rn);
        }
        rot = vaddr & 0x3;

        paddr = getMMU().translate(vaddr, 4, false, getCPSR().isPrivMode(), true);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryRead(paddr)) {
            raiseException(EXCEPT_ABT_DATA,
                    String.format("ldr [%08x]", paddr));
            return;
        }
        value = read32(paddr);

        switch (rot) {
        case 0:
            //do nothing
            break;
        case 1:
            value = Integer.rotateRight(value, 8);
            break;
        case 2:
            value = Integer.rotateRight(value, 16);
            break;
        case 3:
            value = Integer.rotateRight(value, 24);
            break;
        default:
            throw new IllegalArgumentException("Illegal address " +
                    String.format("inst:0x%08x, rot:%d.",
                            inst.getInst(), rot));
        }

        if (rd == 15) {
            setPC(value & 0xfffffffe);
            getCPSR().setTBit(BitOp.getBit32(value, 0));
        } else {
            setReg(rd, value);
        }

        if (!p || w) {
            //ベースレジスタを更新する
            //条件は !(p && !w) と等価、つまり P, W ビットが
            //オフセットアドレス以外の指定なら Rn を書き換える
            setReg(rn, offset);
        }
    }

    /**
     * レジスタハーフワードロード命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLdrh(Instruction inst, boolean exec) {
        boolean p = inst.getBit(24);
        boolean w = inst.getBit(21);
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int offset = getAddrMode3(inst);
        int vaddr, paddr, value;

        if (!exec) {
            disasmInst(inst,
                    String.format("ldrh%s", inst.getCondFieldName()),
                    String.format("%s, %s", getRegName(rd),
                            getAddrMode3Name(inst)));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        if (p) {
            //プリインデクス
            vaddr = offset;
        } else {
            //ポストインデクス
            vaddr = getReg(rn);
        }

        paddr = getMMU().translate(vaddr, 2, false, getCPSR().isPrivMode(), true);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryRead(paddr)) {
            raiseException(EXCEPT_ABT_DATA,
                    String.format("ldrh [%08x]", paddr));
            return;
        }
        value = read16(paddr) & 0xffff;

        setReg(rd, value);

        if (!p || w) {
            //ベースレジスタを更新する
            //条件は !(p && !w) と等価、つまり P, W ビットが
            //オフセットアドレス以外の指定なら Rn を書き換える
            setReg(rn, offset);
        }
    }

    /**
     * レジスタ符号付きバイトロード命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLdrsb(Instruction inst, boolean exec) {
        boolean p = inst.getBit(24);
        boolean w = inst.getBit(21);
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int offset = getAddrMode3(inst);
        int vaddr, paddr, value;

        if (!exec) {
            disasmInst(inst,
                    String.format("ldrsb%s", inst.getCondFieldName()),
                    String.format("%s, %s", getRegName(rd),
                            getAddrMode3Name(inst)));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        if (p) {
            //プリインデクス
            vaddr = offset;
        } else {
            //ポストインデクス
            vaddr = getReg(rn);
        }

        paddr = getMMU().translate(vaddr, 1, false, getCPSR().isPrivMode(), true);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryRead(paddr)) {
            raiseException(EXCEPT_ABT_DATA,
                    String.format("ldrsb [%08x]", paddr));
            return;
        }
        value = read8(paddr);

        setReg(rd, value);

        if (!p || w) {
            //ベースレジスタを更新する
            //条件は !(p && !w) と等価、つまり P, W ビットが
            //オフセットアドレス以外の指定なら Rn を書き換える
            setReg(rn, offset);
        }
    }

    /**
     * レジスタ符号付きハーフワードロード命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLdrsh(Instruction inst, boolean exec) {
        boolean p = inst.getBit(24);
        boolean w = inst.getBit(21);
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int offset = getAddrMode3(inst);
        int vaddr, paddr, value;

        if (!exec) {
            disasmInst(inst,
                    String.format("ldrsh%s", inst.getCondFieldName()),
                    String.format("%s, %s", getRegName(rd),
                            getAddrMode3Name(inst)));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        if (p) {
            //プリインデクス
            vaddr = offset;
        } else {
            //ポストインデクス
            vaddr = getReg(rn);
        }

        paddr = getMMU().translate(vaddr, 2, false, getCPSR().isPrivMode(), true);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryRead(paddr)) {
            raiseException(EXCEPT_ABT_DATA,
                    String.format("ldrsh [%08x]", paddr));
            return;
        }
        value = read16(paddr);

        setReg(rd, value);

        if (!p || w) {
            //ベースレジスタを更新する
            //条件は !(p && !w) と等価、つまり P, W ビットが
            //オフセットアドレス以外の指定なら Rn を書き換える
            setReg(rn, offset);
        }
    }

    /**
     * レジスタダブルワードロード命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLdrd(Instruction inst, boolean exec) {
        boolean p = inst.getBit(24);
        boolean w = inst.getBit(21);
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int offset = getAddrMode3(inst);
        int vaddr, paddr, value1, value2;

        if (!exec) {
            disasmInst(inst,
                    String.format("ldrd%s", inst.getCondFieldName()),
                    String.format("%s, %s", getRegName(rd),
                            getAddrMode3Name(inst)));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        if (p) {
            //プリインデクス
            vaddr = offset;
        } else {
            //ポストインデクス
            vaddr = getReg(rn);
        }

        paddr = getMMU().translate(vaddr, 4, false, getCPSR().isPrivMode(), true);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryRead(paddr) || !tryRead(paddr + 4)) {
            raiseException(EXCEPT_ABT_DATA,
                    String.format("ldrd [%08x]", paddr));
            return;
        }
        value1 = read32(paddr);
        value2 = read32(paddr + 4);

        setReg(rd, value1);
        setReg(rd + 1, value2);

        if (!p || w) {
            //ベースレジスタを更新する
            //条件は !(p && !w) と等価、つまり P, W ビットが
            //オフセットアドレス以外の指定なら Rn を書き換える
            setReg(rn, offset);
        }
    }

    public void executePld(Instruction inst, boolean exec) {
        boolean r = inst.getBit(22);

        if (!exec) {
            disasmInst(inst,
                    String.format("pld%s", (r) ? "" : "w"),
                    String.format("%s", getAddrMode2Name(inst)));
            return;
        }

        //pld は cond が常に NV のため、条件判定不可です

        //do noting
    }

    /**
     * 変換付きレジスタストア命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeStrt(Instruction inst, boolean exec) {
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int offset = getAddrMode2(inst);
        int vaddr, paddr;

        if (!exec) {
            disasmInst(inst,
                    String.format("strt%s", inst.getCondFieldName()),
                    String.format("%s, %s", getRegName(rd),
                            getAddrMode2Name(inst)));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        //P ビットは必ず 0、ポストインデクス
        vaddr = getReg(rn);

        paddr = getMMU().translate(vaddr, 4, false, false, false);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryWrite(paddr)) {
            raiseException(EXCEPT_ABT_DATA,
                    String.format("strt [%08x]", paddr));
            return;
        }
        write32(paddr, getReg(rd));

        //P ビットは必ず 0、W ビットは必ず 1、ベースレジスタを更新する
        setReg(rn, offset);
    }

    /**
     * 変換付きレジスタバイトストア命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeStrbt(Instruction inst, boolean exec) {
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int offset = getAddrMode2(inst);
        int vaddr, paddr;

        if (!exec) {
            disasmInst(inst,
                    String.format("strbt%s", inst.getCondFieldName()),
                    String.format("%s, %s", getRegName(rd),
                            getAddrMode2Name(inst)));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        //P ビットは必ず 0、ポストインデクス
        vaddr = getReg(rn);

        paddr = getMMU().translate(vaddr, 1, false, false, false);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryWrite(paddr)) {
            raiseException(EXCEPT_ABT_DATA,
                    String.format("strbt [%08x]", paddr));
            return;
        }
        write8(paddr, (byte) getReg(rd));

        //P ビットは必ず 0、W ビットは必ず 1、ベースレジスタを更新する
        setReg(rn, offset);
    }

    /**
     * レジスタバイトストア命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeStrb(Instruction inst, boolean exec) {
        boolean p = inst.getBit(24);
        boolean w = inst.getBit(21);
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int offset = getAddrMode2(inst);
        int vaddr, paddr;

        if (!exec) {
            disasmInst(inst,
                    String.format("strb%s", inst.getCondFieldName()),
                    String.format("%s, %s", getRegName(rd),
                            getAddrMode2Name(inst)));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        if (p) {
            //プリインデクス
            vaddr = offset;
        } else {
            //ポストインデクス
            vaddr = getReg(rn);
        }

        paddr = getMMU().translate(vaddr, 1, false, getCPSR().isPrivMode(), false);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryWrite(paddr)) {
            raiseException(EXCEPT_ABT_DATA,
                    String.format("strb [%08x]", paddr));
            return;
        }
        write8(paddr, (byte) getReg(rd));

        if (!p || w) {
            //ベースレジスタを更新する
            //条件は !(p && !w) と等価、つまり P, W ビットが
            //オフセットアドレス以外の指定なら Rn を書き換える
            setReg(rn, offset);
        }
    }

    /**
     * レジスタストア命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeStr(Instruction inst, boolean exec) {
        boolean p = inst.getBit(24);
        boolean w = inst.getBit(21);
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int offset = getAddrMode2(inst);
        int vaddr, paddr;

        if (!exec) {
            disasmInst(inst,
                    String.format("str%s", inst.getCondFieldName()),
                    String.format("%s, %s", getRegName(rd),
                            getAddrMode2Name(inst)));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        if (p) {
            //プリインデクス
            vaddr = offset;
        } else {
            //ポストインデクス
            vaddr = getReg(rn);
        }

        paddr = getMMU().translate(vaddr, 4, false, getCPSR().isPrivMode(), false);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryWrite(paddr)) {
            raiseException(EXCEPT_ABT_DATA,
                    String.format("str [%08x]", paddr));
            return;
        }
        write32(paddr, getReg(rd));

        if (!p || w) {
            //ベースレジスタを更新する
            //条件は !(p && !w) と等価、つまり P, W ビットが
            //オフセットアドレス以外の指定なら Rn を書き換える
            setReg(rn, offset);
        }
    }

    /**
     * レジスタハーフワードストア命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeStrh(Instruction inst, boolean exec) {
        boolean p = inst.getBit(24);
        boolean w = inst.getBit(21);
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int offset = getAddrMode3(inst);
        int vaddr, paddr;

        if (!exec) {
            disasmInst(inst,
                    String.format("strh%s", inst.getCondFieldName()),
                    String.format("%s, %s", getRegName(rd),
                            getAddrMode3Name(inst)));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        if (p) {
            //プリインデクス
            vaddr = offset;
        } else {
            //ポストインデクス
            vaddr = getReg(rn);
        }

        paddr = getMMU().translate(vaddr, 2, false, getCPSR().isPrivMode(), false);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryWrite(paddr)) {
            raiseException(EXCEPT_ABT_DATA,
                    String.format("strh [%08x]", paddr));
            return;
        }
        write16(paddr, (short) getReg(rd));

        if (!p || w) {
            //ベースレジスタを更新する
            //条件は !(p && !w) と等価、つまり P, W ビットが
            //オフセットアドレス以外の指定なら Rn を書き換える
            setReg(rn, offset);
        }
    }

    /**
     * レジスタダブルワードストア命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeStrd(Instruction inst, boolean exec) {
        boolean p = inst.getBit(24);
        boolean w = inst.getBit(21);
        int rn = inst.getRnField();
        int rd = inst.getRdField();
        int offset = getAddrMode3(inst);
        int vaddr, paddr;

        if (!exec) {
            disasmInst(inst,
                    String.format("strd%s", inst.getCondFieldName()),
                    String.format("%s, %s", getRegName(rd),
                            getAddrMode3Name(inst)));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        if (p) {
            //プリインデクス
            vaddr = offset;
        } else {
            //ポストインデクス
            vaddr = getReg(rn);
        }

        paddr = getMMU().translate(vaddr, 4, false, getCPSR().isPrivMode(), false);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryWrite(paddr) || !tryWrite(paddr + 4)) {
            raiseException(EXCEPT_ABT_DATA,
                    String.format("strd [%08x]", paddr));
            return;
        }
        write32(paddr, getReg(rd));
        write32(paddr + 4, getReg(rd + 1));

        if (!p || w) {
            //ベースレジスタを更新する
            //条件は !(p && !w) と等価、つまり P, W ビットが
            //オフセットアドレス以外の指定なら Rn を書き換える
            setReg(rn, offset);
        }
    }

    /**
     * ロードマルチプル命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLdm1(Instruction inst, boolean exec) {
        boolean w = inst.getBit(21);
        int rn = inst.getRnField();
        int rlist = inst.getRegListField();
        int vaddr, paddr, len;

        if (!exec) {
            disasmInst(inst,
                    String.format("ldm%s%s",
                            inst.getCondFieldName(),
                            inst.getPUFieldName()),
                    String.format("%s%s, {%s}",
                            getRegName(rn), (w) ? "!" : "",
                            inst.getRegListFieldName()));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        //r15 以外
        vaddr = getAddrMode4StartAddress(inst.getPUField(), rn, rlist);
        len = getAddrMode4Length(inst.getPUField(), rlist);
        for (int i = 0; i < 15; i++) {
            if ((rlist & (1 << i)) == 0) {
                continue;
            }

            paddr = getMMU().translate(vaddr, 4, false, getCPSR().isPrivMode(), true);
            if (getMMU().isFault()) {
                getMMU().clearFault();
                return;
            }

            if (!tryRead(paddr)) {
                raiseException(EXCEPT_ABT_DATA,
                        String.format("ldm(1) [%08x]", paddr));
                return;
            }
            setReg(i, read32(paddr));
            vaddr += 4;
        }
        //r15
        if (BitOp.getBit32(rlist, 15)) {
            int v;

            paddr = getMMU().translate(vaddr, 4, false, getCPSR().isPrivMode(), true);
            if (getMMU().isFault()) {
                getMMU().clearFault();
                return;
            }

            if (!tryRead(paddr)) {
                raiseException(EXCEPT_ABT_DATA,
                        String.format("ldm(1) [%08x]", paddr));
                return;
            }
            v = read32(paddr);

            setPC(v & 0xfffffffe);
            getCPSR().setTBit(BitOp.getBit32(v, 0));
            vaddr += 4;
        }

        if (w) {
            setReg(rn, getReg(rn) + len);
        }
    }

    /**
     * ロードマルチプル命令。
     *
     * レジスタリストに r15(pc) を入れることはできません。
     * 現在のモードにかかわらず、ユーザモードのレジスタにロードします。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLdm2(Instruction inst, boolean exec) {
        int rn = inst.getRnField();
        int rlist = inst.getRegListField();
        int vaddr, paddr, v, mod;

        if (!exec) {
            disasmInst(inst,
                    String.format("ldm%s%s",
                            inst.getCondFieldName(),
                            inst.getPUFieldName()),
                    String.format("%s, {%s}^",
                            getRegName(rn),
                            inst.getRegListFieldName()));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        //r15 以外、なお r15 は指定不可のため処理なし
        vaddr = getAddrMode4StartAddress(inst.getPUField(), rn, rlist);
        for (int i = 0; i < 15; i++) {
            if ((rlist & (1 << i)) == 0) {
                continue;
            }

            paddr = getMMU().translate(vaddr, 4, false, getCPSR().isPrivMode(), true);
            if (getMMU().isFault()) {
                getMMU().clearFault();
                return;
            }

            if (!tryRead(paddr)) {
                raiseException(EXCEPT_ABT_DATA,
                        String.format("ldm(2) [%08x]", paddr));
                return;
            }
            //必ずユーザモードのレジスタをロードする
            v = read32(paddr);
            vaddr += 4;

            mod = getCPSR().getMode();
            getCPSR().setMode(PSR.MODE_USR);
            setReg(i, v);
            getCPSR().setMode(mod);
        }
    }

    /**
     * ロードマルチプル命令。
     *
     * レジスタリストに必ず r15(pc) を入れなければなりません。
     * CPSR にカレントモードの SPSR をコピーします。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLdm3(Instruction inst, boolean exec) {
        boolean w = inst.getBit(21);
        int rn = inst.getRnField();
        int rlist = inst.getRegListField();
        int vaddr, paddr, len, v;

        if (!exec) {
            disasmInst(inst,
                    String.format("ldm%s%s",
                            inst.getCondFieldName(),
                            inst.getPUFieldName()),
                    String.format("%s%s, {%s}^",
                            getRegName(rn), (w) ? "!" : "",
                            inst.getRegListFieldName()));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        //r15 以外
        vaddr = getAddrMode4StartAddress(inst.getPUField(), rn, rlist);
        len = getAddrMode4Length(inst.getPUField(), rlist);
        for (int i = 0; i < 15; i++) {
            if ((rlist & (1 << i)) == 0) {
                continue;
            }

            paddr = getMMU().translate(vaddr, 4, false, getCPSR().isPrivMode(), true);
            if (getMMU().isFault()) {
                getMMU().clearFault();
                return;
            }

            if (!tryRead(paddr)) {
                raiseException(EXCEPT_ABT_DATA,
                        String.format("ldm(3) [%08x]", paddr));
                return;
            }
            setReg(i, read32(paddr));
            vaddr += 4;
        }

        //CPSR に SPSR の値を入れる
        setCPSR(getSPSR());

        //r15 は必ずロードする
        paddr = getMMU().translate(vaddr, 4, false, getCPSR().isPrivMode(), true);
        if (getMMU().isFault()) {
            getMMU().clearFault();
            return;
        }

        if (!tryRead(paddr)) {
            raiseException(EXCEPT_ABT_DATA,
                    String.format("ldm(3) [%08x]", paddr));
            return;
        }
        v = read32(paddr);

        setPC(v & 0xfffffffe);
        getCPSR().setTBit(BitOp.getBit32(v, 0));
        vaddr += 4;

        if (w) {
            setReg(rn, getReg(rn) + len);
        }
    }

    /**
     * ストアマルチプル命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeStm1(Instruction inst, boolean exec) {
        int pu = inst.getPUField();
        boolean w = inst.getBit(21);
        int rn = inst.getRnField();
        int rlist = inst.getRegListField();
        int vaddr, paddr, len;

        if (!exec) {
            disasmInst(inst,
                    String.format("stm%s%s",
                            inst.getCondFieldName(),
                            inst.getPUFieldName()),
                    String.format("%s%s, {%s}",
                            getRegName(rn), (w) ? "!" : "",
                            inst.getRegListFieldName()));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        vaddr = getAddrMode4StartAddress(pu, rn, rlist);
        len = getAddrMode4Length(pu, rlist);
        for (int i = 0; i < 16; i++) {
            if ((rlist & (1 << i)) == 0) {
                continue;
            }

            paddr = getMMU().translate(vaddr, 4, false, getCPSR().isPrivMode(), false);
            if (getMMU().isFault()) {
                getMMU().clearFault();
                return;
            }

            if (!tryWrite(paddr)) {
                raiseException(EXCEPT_ABT_DATA,
                        String.format("stm(1) [%08x]", paddr));
                return;
            }
            write32(paddr, getReg(i));
            vaddr += 4;
        }

        if (w) {
            setReg(rn, getReg(rn) + len);
        }
    }

    /**
     * ストアマルチプル命令。
     *
     * 現在のモードにかかわらず、ユーザモードのレジスタをストアします。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeStm2(Instruction inst, boolean exec) {
        int pu = inst.getPUField();
        int rn = inst.getRnField();
        int rlist = inst.getRegListField();
        int vaddr, paddr, v, mod;

        if (!exec) {
            disasmInst(inst,
                    String.format("stm%s%s",
                            inst.getCondFieldName(),
                            inst.getPUFieldName()),
                    String.format("%s, {%s}^",
                            getRegName(rn),
                            inst.getRegListFieldName()));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        vaddr = getAddrMode4StartAddress(pu, rn, rlist);
        for (int i = 0; i < 16; i++) {
            if ((rlist & (1 << i)) == 0) {
                continue;
            }

            paddr = getMMU().translate(vaddr, 4, false, getCPSR().isPrivMode(), false);
            if (getMMU().isFault()) {
                getMMU().clearFault();
                return;
            }

            if (!tryWrite(paddr)) {
                raiseException(EXCEPT_ABT_DATA,
                        String.format("stm(2) [%08x]", paddr));
                return;
            }
            //必ずユーザモードのレジスタをストアする
            mod = getCPSR().getMode();
            getCPSR().setMode(PSR.MODE_USR);
            v = getReg(i);
            getCPSR().setMode(mod);

            write32(paddr, v);
            vaddr += 4;
        }
    }

    /**
     * リンク付き分岐命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeBl(Instruction inst, boolean exec) {
        boolean l = inst.getBit(24);
        int imm24 = inst.getField(0, 24);
        int simm24 = (int) BitOp.signExt64(imm24, 24) << 2;

        if (!exec) {
            disasmInst(inst,
                    String.format("b%s%s",
                            (l) ? "l" : "", inst.getCondFieldName()),
                    String.format("%08x", getPC() + simm24));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        if (l) {
            setReg(14, getPC() - 4);
        }
        jumpRel(simm24);
    }

    /**
     * リンク付き分岐命令。
     *
     * Thumb 命令のサブルーチン呼び出しが可能です。
     *
     * 31  30  29  28 |27  26  25 |24 |23               0|
     * ---------------------------------------------------
     *  1   1   1   1 | 1   0   1 | H | signed_immed_24  |
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeBlx1(Instruction inst, boolean exec) {
        boolean h = inst.getBit(24);
        int vh = BitOp.toInt(h) << 1;
        int imm24 = inst.getField(0, 24);
        int simm24 = (int) BitOp.signExt64(imm24, 24) << 2;

        if (!exec) {
            disasmInst(inst,
                    String.format("blx"),
                    String.format("%08x", getPC() + simm24 + vh));
            return;
        }

        //blx は cond が常に NV のため、条件判定不可です

        setReg(14, getPC() - 4);
        //T ビットをセット
        getCPSR().setTBit(true);
        jumpRel(simm24 + vh);

        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * リンク付き分岐命令。
     *
     * Thumb 命令のサブルーチン呼び出しが可能です。
     *
     * 27  26  25  24 |23  22  21  20 | 7   6   5   4|
     * ----------------------------------------------
     *  0   0   0   1 | 0   0   1   0 | 0   0   1   1|
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeBlx2(Instruction inst, boolean exec) {
        int rm = inst.getRmField();
        int dest;

        if (!exec) {
            disasmInst(inst,
                    String.format("blx"),
                    String.format("%s", getRegName(rm)));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        dest = getReg(rm);

        setReg(14, getPC() - 4);
        //T ビットをセット
        getCPSR().setTBit(BitOp.getBit32(dest, 0));
        setPC(dest & 0xfffffffe);
    }

    /**
     * 分岐交換命令。
     *
     * Thumb 命令のサブルーチン呼び出しが可能です。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeBx(Instruction inst, boolean exec) {
        int rm = inst.getRmField();
        int dest;

        if (!exec) {
            disasmInst(inst,
                    String.format("bx%s", inst.getCondFieldName()),
                    String.format("%s", getRegName(rm)));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        dest = getReg(rm);

        //T ビットを設定する
        getCPSR().setTBit((dest & 0x1) == 1);
        setPC(dest & 0xfffffffe);
    }

    /**
     * 先行ゼロカウント命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeClz(Instruction inst, boolean exec) {
        int rd = inst.getRdField();
        int rm = inst.getRmField();
        int dest;

        if (!exec) {
            disasmInst(inst,
                    String.format("clz%s", inst.getCondFieldName()),
                    String.format("%s, %s",
                            getRegName(rd), getRegName(rm)));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        dest = Integer.numberOfLeadingZeros(getReg(rm));

        setReg(rd, dest);
    }

    /**
     * コプロセッサデータ処理命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeCdp(Instruction inst, boolean exec) {
        int opcode1 = inst.getField(20, 4);
        int crn = inst.getField(16, 4);
        int crd = inst.getField(12, 4);
        int cpnum = inst.getField(8, 4);
        int opcode2 = inst.getField(5, 3);
        int crm = inst.getField(0, 4);
        CoProc cp;
        int crid, crval, rval;

        if (!exec) {
            disasmInst(inst,
                    String.format("cdp%s", inst.getCondFieldName()),
                    String.format("p%d, %d, %s, %s, %s, {%d}",
                            cpnum, opcode1,
                            getCoprocRegName(cpnum, crd),
                            getCoprocRegName(cpnum, crn),
                            getCoprocRegName(cpnum, crm), opcode2));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        cp = getCoproc(cpnum);
        if (cp == null) {
            //TODO: for debug, will be removed
            throw new IllegalArgumentException(String.format(
                    "Unimplemented coprocessor, p%d selected.", cpnum));
            //raiseException(EXCEPT_UND, "Unimplemented coprocessor, " +
            //        String.format("p%d selected.", cpnum));
            //return;
        }

        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * ARM レジスタからコプロセッサへのストア命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeMcr(Instruction inst, boolean exec) {
        int opcode1 = inst.getField(21, 3);
        int crn = inst.getField(16, 4);
        int rd = inst.getRdField();
        int cpnum = inst.getField(8, 4);
        int opcode2 = inst.getField(5, 3);
        int crm = inst.getField(0, 4);
        CoProc cp;
        int crid;

        if (!exec) {
            disasmInst(inst,
                    String.format("mcr%s", inst.getCondFieldName()),
                    String.format("%s, %d, %s, %s, %s, {%d}",
                            getCoproc(cpnum).toString(), opcode1,
                            getRegName(rd), getCoprocRegName(cpnum, crn),
                            getCoprocRegName(cpnum, crm), opcode2));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        cp = getCoproc(cpnum);
        if (cp == null) {
            //TODO: for debug, will be removed
            throw new IllegalArgumentException(String.format(
                    "Unimplemented coprocessor, p%d selected.", cpnum));
            //raiseException(EXCEPT_UND, "Unimplemented coprocessor, " +
            //        String.format("p%d selected.", cpnum));
            //return;
        }

        crid = CoProc.getCRegID(crn, opcode1, crm, opcode2);
        if (!cp.validCRegNumber(crid)) {
            //TODO: for debug, will be removed
            throw new IllegalArgumentException("Unimplemented coprocessor register, " +
                    String.format("p%d id(%08x, crn:%d, opc1:%d, crm:%d, opc2:%d) selected.",
                            cpnum, crid, crn, opcode1, crm, opcode2));
            //raiseException(EXCEPT_UND, "Unimplemented coprocessor register, " +
            //        String.format("p%d id(%08x, crn:%d, opc1:%d, crm:%d, opc2:%d) selected.",
            //                cpnum, crid, crn, opcode1, crm, opcode2));
            //return;
        }

        cp.setCReg(crid, getReg(rd));
    }

    /**
     * コプロセッサから ARM レジスタへのロード命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeMrc(Instruction inst, boolean exec) {
        int opcode1 = inst.getField(21, 3);
        int crn = inst.getField(16, 4);
        int rd = inst.getRdField();
        int cpnum = inst.getField(8, 4);
        int opcode2 = inst.getField(5, 3);
        int crm = inst.getField(0, 4);
        CoProc cp;
        int crid, crval, rval;

        if (!exec) {
            disasmInst(inst,
                    String.format("mrc%s", inst.getCondFieldName()),
                    String.format("p%d, %d, %s, %s, %s, {%d}",
                            cpnum, opcode1,
                            getRegName(rd), getCoprocRegName(cpnum, crn),
                            getCoprocRegName(cpnum, crm), opcode2));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        cp = getCoproc(cpnum);
        if (cp == null) {
            //TODO: for debug, will be removed
            throw new IllegalArgumentException(String.format(
                    "Unimplemented coprocessor, p%d selected.", cpnum));
            //raiseException(EXCEPT_UND, "Unimplemented coprocessor, " +
            //        String.format("p%d selected.", cpnum));
            //return;
        }

        crid = CoProc.getCRegID(crn, opcode1, crm, opcode2);
        if (!cp.validCRegNumber(crid)) {
            //TODO: for debug, will be removed
            throw new IllegalArgumentException("Unimplemented coprocessor register, " +
                    String.format("p%d id(%08x, crn:%d, opc1:%d, crm:%d, opc2:%d) selected.",
                            cpnum, crid, crn, opcode1, crm, opcode2));
            //raiseException(EXCEPT_UND, "Unimplemented coprocessor register, " +
            //        String.format("p%d id(%08x, crn:%d, opc1:%d, crm:%d, opc2:%d) selected.",
            //                cpnum, crid, crn, opcode1, crm, opcode2));
            //return;
        }

        crval = cp.getCReg(crid);
        if (rd == 15) {
            //r15 の場合 r15 を変更せず、APSR の N, Z, C, V ビットを変更する
            rval = getSPSR().getValue();
            rval &= ~0xf0000000;
            rval |= crval & 0xf0000000;
            setAPSR(rval);
        } else {
            setReg(rd, crval);
        }
    }

    public void executeSwi(Instruction inst, boolean exec) {
        int imm24 = inst.getField(0, 24);

        if (!exec) {
            disasmInst(inst,
                    String.format("swi%s", inst.getCondFieldName()),
                    String.format("0x%08x", imm24));
            return;
        }

        if (!inst.satisfiesCond(getCPSR())) {
            return;
        }

        raiseException(EXCEPT_SVC, "swi instruction " +
                String.format("imm24:0x%08x.", imm24));
    }

    /**
     * 未定義命令。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeUnd(Instruction inst, boolean exec) {
        if (!exec) {
            disasmInst(inst,
                    String.format("und%s", inst.getCondFieldName()),
                    "");
            return;
        }

        raiseException(EXCEPT_ABT_INST, "Warning: Undefined instruction " +
                String.format("inst:0x%08x.", inst.getInst()));
    }

    /**
     * 命令を取得します。
     *
     * @return inst ARM 命令
     */
    public Instruction fetch() {
        Instruction inst;
        int v, vaddr, paddr;

        //現在の PC の指すアドレスから命令を取得します
        vaddr = getPC() - 8;

        if (getCPSR().getTBit()) {
            //Thumb モード
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
        } else {
            //ARM モード
            paddr = getMMU().translate(vaddr, 4, true, getCPSR().isPrivMode(), true);
            if (getMMU().isFault()) {
                getMMU().clearFault();
                return null;
            }

            if (!tryRead(paddr)) {
                raiseException(EXCEPT_ABT_INST,
                        String.format("exec [%08x]", paddr));
                return null;
            }
            v = read32(paddr);
            inst = new Instruction(v);

            return inst;
        }
    }

    /**
     * 命令を逆アセンブルします。
     *
     * @param inst ARM 命令
     */
    public void disasm(Instruction inst) {
        executeInst(inst, false);
    }

    /**
     * 逆アセンブルした命令を表示します。
     *
     * @param inst      ARM 命令
     * @param operation 命令の文字列表記
     * @param operand   オペランドの文字列表記
     */
    public void disasmInst(Instruction inst, String operation, String operand) {
        printDisasm(inst, operation, operand);
        printRegs();
    }

    /**
     * 命令を実行します。
     *
     * @param inst ARM 命令
     */
    public void execute(Instruction inst) {
        executeInst(inst, true);
    }

    /**
     * 命令を逆アセンブル、実行します。
     *
     * @param inst ARM 命令
     * @param exec 逆アセンブルと実行なら true、
     *             逆アセンブルのみなら false
     */
    public void executeInst(Instruction inst, boolean exec) {
        int cond = inst.getCondField();
        int subcode = inst.getSubCodeField();

        if (getCPSR().getTBit()) {
            //Thumb モード
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
        } else {
            //ARM モード
            switch (subcode) {
            case Instruction.SUBCODE_USEALU:
                executeSubALU(inst, exec);
                return;
            case Instruction.SUBCODE_LDRSTR:
                executeSubLdrStr(inst, exec);
                return;
            case Instruction.SUBCODE_LDMSTM:
                executeSubLdmStm(inst, exec);
                return;
            case Instruction.SUBCODE_COPSWI:
                executeSubCopSwi(inst, exec);
                return;
            default:
                //do nothing
                break;
            }

            throw new IllegalArgumentException("Unknown Subcode" +
                    String.format("(%d).", subcode));
        }
    }

    /**
     * データ処理命令を実行します。
     *
     * subcode = 0b00
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSubALU(Instruction inst, boolean exec) {
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
                executeSubALUShiftImm(inst, exec);
            } else if (!b7 && b4) {
                //レジスタシフト
                executeSubALUShiftReg(inst, exec);
            } else {
                //算術命令拡張空間、ロードストア命令拡張空間
                int cond = inst.getCondField();
                boolean p = inst.getBit(24);
                int op = inst.getField(5, 2);

                if (cond != Instruction.COND_NV && !p && op == 0) {
                    //算術命令拡張空間
                    executeSubExtALU(inst, exec);
                } else {
                    //ロードストア命令拡張空間
                    executeSubExtLdrStr(inst, exec);
                }
            }
        } else {
            //イミディエート
            executeSubALUImm(inst, exec);
        }
    }

    /**
     * イミディエートシフトオペランドを取るデータ処理命令、
     * または、その他の命令を実行します。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSubALUShiftImm(Instruction inst, boolean exec) {
        int id = inst.getOpcodeSBitShiftID();

        switch (id) {
        case Instruction.OPCODE_S_OTH:
            executeSubALUOther(inst, exec);
            break;
        default:
            executeALU(inst, exec, id);
            break;
        }
    }

    /**
     * レジスタシフトオペランドを取るデータ処理命令、
     * その他の命令を実行します。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSubALUShiftReg(Instruction inst, boolean exec) {
        int id = inst.getOpcodeSBitShiftID();

        switch (id) {
        case Instruction.OPCODE_S_OTH:
            executeSubALUOther(inst, exec);
            break;
        default:
            executeALU(inst, exec, id);
            break;
        }
    }

    /**
     * 算術命令拡張空間（乗算）、
     * を実行します。
     *
     * cond != NV
     * bit[27:24] = 0b0000
     * bit[7:4] = 0b1001
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSubExtALU(Instruction inst, boolean exec) {
        //U, B, W ビット[23:21]
        int ubw = inst.getField(21, 3);

        //算術命令拡張空間
        switch (ubw) {
        case 1:
            //mla
            executeMla(inst, exec);
            break;
        case 0:
            //mul
            executeMul(inst, exec);
            break;
        case 7:
            //smlal
            executeSmlal(inst, exec);
            break;
        case 6:
            //smull
            executeSmull(inst, exec);
            break;
        case 5:
            //umlal
            executeUmlal(inst, exec);
            break;
        case 4:
            //umull
            executeUmull(inst, exec);
            break;
        default:
            //未定義
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
        }
    }

    /**
     * ロードストア命令拡張空間（ハーフワードロード、ストア）、
     * を実行します。
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
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSubExtLdrStr(Instruction inst, boolean exec) {
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
                executeSwp(inst, exec);
                break;
            case 1:
                //swpb
                //TODO: Not implemented
                throw new IllegalArgumentException("Sorry, not implemented.");
                //break;
            default:
                //未定義
                //TODO: Not implemented
                throw new IllegalArgumentException("Sorry, not implemented.");
            }
        } else if (op == 1) {
            if (l) {
                //ldrh
                executeLdrh(inst, exec);
            } else {
                //strh
                executeStrh(inst, exec);
            }
        } else if (op == 2) {
            if (l) {
                //ldrsb
                executeLdrsb(inst, exec);
            } else {
                //ldrd
                executeLdrd(inst, exec);
            }
        } else if (op == 3) {
            if (l) {
                //ldrsh
                executeLdrsh(inst, exec);
            } else {
                //strd
                executeStrd(inst, exec);
            }
        } else {
            //未定義
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
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
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSubALUImm(Instruction inst, boolean exec) {
        int id = inst.getOpcodeSBitImmID();

        switch (id) {
        case Instruction.OPCODE_S_MSR:
            executeMsr(inst, exec);
            break;
        case Instruction.OPCODE_S_UND:
            executeUnd(inst, exec);
            break;
        default:
            executeALU(inst, exec, id);
            break;
        }
    }

    /**
     * その他のデータ処理命令、
     * を実行します。
     *
     * bit[27:23] = 0b00010
     * bit[20] = 0
     *
     * 各ビットと命令の対応は下記の通りです。
     *
     *        | 22  | 21  |  7  |  6  |  5  |  4  |
     * -------+-----+-----+-----+-----+-----+-----+
     * MRS    |  x  |  0  |  0  |  0  |  0  |  0  |
     * MSR    |  x  |  1  |  0  |  0  |  0  |  0  |
     * BX     |  0  |  1  |  0  |  0  |  0  |  1  |
     * CLZ    |  1  |  1  |  0  |  0  |  0  |  1  |
     * BLX(2) |  0  |  1  |  0  |  0  |  1  |  1  |
     * BKPT   |  0  |  1  |  0  |  1  |  1  |  1  |
     * -------+-----+-----+-----+-----+-----+-----+
     *
     * これ以外のパターンは全て未定義命令です。
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSubALUOther(Instruction inst, boolean exec) {
        int cond = inst.getCondField();
        boolean b22 = inst.getBit(22);
        boolean b21 = inst.getBit(21);
        int type = inst.getField(4, 4);

        switch (type) {
        case 0x0:
            if (!b21) {
                //mrs
                executeMrs(inst, exec);
            } else {
                //msr
                executeMsr(inst, exec);
            }
            break;
        case 0x1:
            if (!b22 && b21) {
                //bx
                executeBx(inst, exec);
            } else if (b22 && b21) {
                //clz
                executeClz(inst, exec);
            } else {
                //未定義
                executeUnd(inst, exec);
            }
            break;
        case 0x3:
            if (!b22 && b21) {
                //blx(2)
                executeBlx2(inst, exec);
            } else {
                //未定義
                executeUnd(inst, exec);
            }
            break;
        case 0x7:
            if (cond == Instruction.COND_AL && !b22 && b21) {
                //bkpt
                //TODO: Not implemented
                throw new IllegalArgumentException("Sorry, not implemented.");
            } else {
                //未定義
                executeUnd(inst, exec);
            }
            break;
        default:
            //未定義
            //executeUnd(inst, exec);
            //break;
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
        }
    }

    /**
     * ロード、ストア命令を実行します。
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
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSubLdrStr(Instruction inst, boolean exec) {
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
            executeUnd(inst, exec);
        } else if (l) {
            if (!p && !b && w) {
                //ldrt
                executeLdrt(inst, exec);
            } else if (!p && b && w) {
                //ldrbt
                executeLdrbt(inst, exec);
            } else if (b) {
                if (cond == inst.COND_NV && p && !w && rd == 15) {
                    //pld
                    executePld(inst, exec);
                } else {
                    //ldrb
                    executeLdrb(inst, exec);
                }
            } else if (!b) {
                //ldr
                executeLdr(inst, exec);
            } else {
                throw new IllegalArgumentException("Illegal P,B,W bits " +
                        String.format("p:%b, b:%b, w:%b.", p, b, w));
            }
        } else if (!l) {
            if (!p && !b && w) {
                //strt
                executeStrt(inst, exec);
            } else if (!p && b && w) {
                //strbt
                executeStrbt(inst, exec);
            } else if (b) {
                //strb
                executeStrb(inst, exec);
            } else if (!b) {
                //str
                executeStr(inst, exec);
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
     * ロードマルチプル、ストアマルチプル、分岐命令を実行します。
     *
     * subcode = 0b10
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSubLdmStm(Instruction inst, boolean exec) {
        int cond = inst.getCondField();
        boolean b25 = inst.getBit(25);
        boolean l = inst.getLBit();

        if (!b25) {
            //ロードマルチプル、ストアマルチプル
            if (cond == Instruction.COND_NV) {
                //未定義
                executeUnd(inst, exec);
            } else {
                if (l) {
                    //ldm(1), ldm(2), ldm(3)
                    executeSubLdm(inst, exec);
                } else {
                    //stm(1), stm(2)
                    executeSubStm(inst, exec);
                }
            }
        } else {
            //分岐命令
            if (cond == Instruction.COND_NV) {
                //blx
                executeBlx1(inst, exec);
            } else {
                //b, bl
                executeBl(inst, exec);
            }
        }
    }

    /**
     * ロードマルチプル命令を実行します。
     *
     * subcode = 0b10
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSubLdm(Instruction inst, boolean exec) {
        boolean s = inst.getBit(22);
        boolean b15 = inst.getBit(15);

        if (!s) {
            //ldm(1)
            executeLdm1(inst, exec);
        } else {
            if (!b15) {
                //ldm(2)
                executeLdm2(inst, exec);
            } else {
                //ldm(3)
                executeLdm3(inst, exec);
            }
        }
    }

    /**
     * ストアマルチプル命令を実行します。
     *
     * subcode = 0b10
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSubStm(Instruction inst, boolean exec) {
        boolean s = inst.getBit(22);
        boolean w = inst.getBit(21);

        if (!s) {
            //stm(1)
            executeStm1(inst, exec);
        } else {
            if (!w) {
                //stm(2)
                executeStm2(inst, exec);
            } else {
                //未定義
                executeUnd(inst, exec);
            }
        }
    }

    /**
     * コプロセッサ、ソフトウェア割り込み命令を実行します。
     *
     * subcode = 0b11
     *
     * @param inst ARM 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSubCopSwi(Instruction inst, boolean exec) {
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
            //break;
        case 2:
            if (!b4) {
                //cdp
                executeCdp(inst, exec);
            } else {
                if (!b20) {
                    //mcr
                    executeMcr(inst, exec);
                } else {
                    //mrc
                    executeMrc(inst, exec);
                }
            }
            return;
        case 3:
            if (cond == Instruction.COND_NV) {
                //未定義
                executeUnd(inst, exec);
            } else {
                //swi
                executeSwi(inst, exec);
            }
            return;
        default:
            //do nothing
            break;
        }

        throw new IllegalArgumentException("Illegal b25, b24 bits " +
                String.format("b25b24:%d.", subsub));
    }

    public static final int EXCEPT_RST = 0;
    public static final int EXCEPT_ABT_DATA = 1;
    public static final int EXCEPT_FIQ = 2;
    public static final int EXCEPT_IRQ = 3;
    public static final int EXCEPT_ABT_INST = 4;
    public static final int EXCEPT_UND = 5;
    public static final int EXCEPT_SVC = 6;

    /**
     * 例外を要求します。
     *
     * @param num    例外番号（EXCEPT_xxxx）
     * @param dbgmsg デバッグ用のメッセージ
     */
    public void raiseException(int num, String dbgmsg) {
        if (num < 0 || exceptions.length <= num) {
            throw new IllegalArgumentException("Illegal exception number " + num);
        }

        if (isRaised()) {
            //例外状態がクリアされず残っている
            //一度の命令で二度、例外が起きるのはおそらくバグでしょう
            throw new IllegalStateException("Exception status is not cleared.");
        }

        exceptions[num] = true;
        exceptionReasons[num] = dbgmsg;

        setRaised(true);
    }

    /**
     * 最も優先度の高い例外を 1つだけ発生させます。
     *
     * 優先度の低い例外は後回しにされます。
     */
    public void doImportantException() {
        boolean found = false;
        int i;

        for (i = 0; i < exceptions.length; i++) {
            if (exceptions[i]) {
                exceptions[i] = false;
                found = true;
                break;
            }
        }
        if (!found) {
            return;
        }

        switch (i) {
        case EXCEPT_RST:
            doExceptionReset(exceptionReasons[i]);
            break;
        case EXCEPT_UND:
            doExceptionUndefined(exceptionReasons[i]);
            break;
        case EXCEPT_SVC:
            doExceptionSoftware(exceptionReasons[i]);
            break;
        case EXCEPT_ABT_INST:
            doExceptionPrefetch(exceptionReasons[i]);
            break;
        case EXCEPT_ABT_DATA:
            doExceptionData(exceptionReasons[i]);
            break;
        case EXCEPT_IRQ:
            doExceptionIRQ(exceptionReasons[i]);
            break;
        case EXCEPT_FIQ:
            doExceptionFIQ(exceptionReasons[i]);
            break;
        default:
            throw new IllegalArgumentException("Illegal exception number " + i);
        }
    }

    /**
     * リセット例外を発生させます。
     *
     * @param dbgmsg デバッグ用のメッセージ
     */
    public void doExceptionReset(String dbgmsg) {
        int cpsrORg;

        System.out.printf("Exception: Reset by '%s'.\n",
                dbgmsg);

        //cpsr の値を取っておく
        cpsrORg = getCPSR().getValue();

        //スーパーバイザモード、ARM 状態、高速割り込み禁止、割り込み禁止、
        //へ移行
        getCPSR().setMode(PSR.MODE_SVC);
        getCPSR().setTBit(false);
        getCPSR().setFBit(true);
        getCPSR().setIBit(true);

        //spsr にリセット前の cpsr を保存する
        setSPSR(cpsrORg);

        //リセット例外ベクタへ
        if (isHighVector()) {
            setPC(0xffff0000);
        } else {
            setPC(0x00000000);
        }
    }

    /**
     * 未定義例外を発生させます。
     *
     * この例外はコプロセッサ命令の実行時、
     * 応答するコプロセッサが存在しないときに発生します。
     *
     * @param dbgmsg デバッグ用のメッセージ
     */
    public void doExceptionUndefined(String dbgmsg) {
        int pcOrg, cpsrORg;

        System.out.printf("Exception: Undefined instruction by '%s'.\n",
                dbgmsg);

        //pc, cpsr の値を取っておく
        pcOrg = getPC() - 4;
        cpsrORg = getCPSR().getValue();

        //未定義モード、ARM 状態、割り込み禁止、
        //へ移行
        getCPSR().setMode(PSR.MODE_UND);
        getCPSR().setTBit(false);
        //F flag is not affected
        getCPSR().setIBit(true);

        //lr, spsr に例外前の pc, cpsr を保存する
        setReg(14, pcOrg);
        setSPSR(cpsrORg);

        //未定義例外ベクタへ
        if (isHighVector()) {
            setPC(0xffff0004);
        } else {
            setPC(0x00000004);
        }

        //tentative...
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * ソフトウェア割り込み例外を発生させます。
     *
     * この例外は swi 命令を実行したときに生成されます。
     *
     * @param dbgmsg デバッグ用のメッセージ
     */
    public void doExceptionSoftware(String dbgmsg) {
        int pcOrg, cpsrORg;

        //System.out.printf("Exception: Software interrupt by '%s'.\n",
        //        dbgmsg);

        //pc, cpsr の値を取っておく
        pcOrg = getPC() - 4;
        cpsrORg = getCPSR().getValue();

        //スーパバイザモード、ARM 状態、割り込み禁止、
        //へ移行
        getCPSR().setMode(PSR.MODE_SVC);
        getCPSR().setTBit(false);
        //F flag is not affected
        getCPSR().setIBit(true);

        //lr, spsr に例外前の pc, cpsr を保存する
        setReg(14, pcOrg);
        setSPSR(cpsrORg);

        //ソフトウェア割り込み例外ベクタへ
        if (isHighVector()) {
            setPC(0xffff0008);
        } else {
            setPC(0x00000008);
        }
    }

    /**
     * プリフェッチアボート例外を発生させます。
     *
     * この例外は無効な命令を実行したときに生成されます。
     *
     * @param dbgmsg デバッグ用のメッセージ
     */
    public void doExceptionPrefetch(String dbgmsg) {
        int pcOrg, cpsrORg;

        //System.out.printf("Exception: Prefetch abort by '%s'.\n",
        //        dbgmsg);

        //pc, cpsr の値を取っておく
        pcOrg = getPC() - 4;
        cpsrORg = getCPSR().getValue();

        //アボートモード、ARM 状態、割り込み禁止、
        //へ移行
        getCPSR().setMode(PSR.MODE_ABT);
        getCPSR().setTBit(false);
        //F flag is not affected
        getCPSR().setIBit(true);

        //lr, spsr に例外前の pc, cpsr を保存する
        setReg(14, pcOrg);
        setSPSR(cpsrORg);

        //プリフェッチアボート例外ベクタへ
        if (isHighVector()) {
            setPC(0xffff000c);
        } else {
            setPC(0x0000000c);
        }
    }

    /**
     * データアボート例外を発生させます。
     *
     * この例外は無効なロード、あるいはストア命令を実行したときに生成されます。
     *
     * @param dbgmsg デバッグ用のメッセージ
     */
    public void doExceptionData(String dbgmsg) {
        int pcOrg, cpsrORg;

        //System.out.printf("Exception: Data abort by '%s'.\n",
        //        dbgmsg);

        //pc, cpsr の値を取っておく
        pcOrg = getPC();
        cpsrORg = getCPSR().getValue();

        //アボートモード、ARM 状態、割り込み禁止、
        //へ移行
        getCPSR().setMode(PSR.MODE_ABT);
        getCPSR().setTBit(false);
        //F flag is not affected
        getCPSR().setIBit(true);

        //lr, spsr に例外前の pc, cpsr を保存する
        setReg(14, pcOrg);
        setSPSR(cpsrORg);

        //データアボート例外ベクタへ
        if (isHighVector()) {
            setPC(0xffff0010);
        } else {
            setPC(0x00000010);
        }
    }

    /**
     * 割り込み要求例外を発生させます。
     *
     * この例外はプロセッサの IRQ のアサートにより生成されます。
     *
     * @param dbgmsg デバッグ用のメッセージ
     */
    public void doExceptionIRQ(String dbgmsg) {
        int pcOrg, cpsrORg;

        //System.out.printf("Exception: IRQ by '%s'.\n",
        //        dbgmsg);

        //pc, cpsr の値を取っておく
        pcOrg = getPC() - 4;
        cpsrORg = getCPSR().getValue();

        //IRQ モード、ARM 状態、割り込み禁止、
        //へ移行
        getCPSR().setMode(PSR.MODE_IRQ);
        getCPSR().setTBit(false);
        //F flag is not affected
        getCPSR().setIBit(true);

        //lr, spsr に例外前の pc, cpsr を保存する
        setReg(14, pcOrg);
        setSPSR(cpsrORg);

        //IRQ 例外ベクタへ
        if (isHighVector()) {
            setPC(0xffff0018);
        } else {
            setPC(0x00000018);
        }
    }

    /**
     * 高速割り込み要求例外を発生させます。
     *
     * この例外はプロセッサの FIQ のアサートにより生成されます。
     *
     * @param dbgmsg デバッグ用のメッセージ
     */
    public void doExceptionFIQ(String dbgmsg) {
        int pcOrg, cpsrORg;

        System.out.printf("Exception: FIQ by '%s'.\n",
                dbgmsg);

        //pc, cpsr の値を取っておく
        pcOrg = getPC() - 4;
        cpsrORg = getCPSR().getValue();

        //FIQ モード、ARM 状態、高速割り込み禁止、割り込み禁止、
        //へ移行
        getCPSR().setMode(PSR.MODE_FIQ);
        getCPSR().setTBit(false);
        getCPSR().setFBit(true);
        getCPSR().setIBit(true);

        //lr, spsr に例外前の pc, cpsr を保存する
        setReg(14, pcOrg);
        setSPSR(cpsrORg);

        //FIQ 例外ベクタへ
        if (isHighVector()) {
            setPC(0xffff001c);
        } else {
            setPC(0x0000001c);
        }
    }

    /**
     * 割り込み線に接続するコントローラを取得します。
     *
     * 一度も setINTC() が呼ばれていなければ、
     * NullINTC のインスタンスが返されます。
     *
     * @return 割り込みコントローラ
     */
    public INTC getINTCForIRQ() {
        return intcIRQ;
    }

    /**
     * 割り込み線に接続するコントローラを設定します。
     *
     * CPU の割り込み線に割り込みコントローラを接続することに相当します。
     *
     * @param c 割り込みコントローラ
     */
    public void setINTCForIRQ(INTC c) {
        intcIRQ = c;
    }

    /**
     * 高速割り込み線に接続するコントローラを取得します。
     *
     * 一度も setINTC() が呼ばれていなければ、
     * NullINTC のインスタンスが返されます。
     *
     * @return 割り込みコントローラ
     */
    public INTC getINTCForFIQ() {
        return intcFIQ;
    }

    /**
     * 高速割り込み線に接続するコントローラを設定します。
     *
     * CPU の高速割り込み線に割り込みコントローラを接続することに相当します。
     *
     * @param c 割り込みコントローラ
     */
    public void setINTCForFIQ(INTC c) {
        intcFIQ = c;
    }

    /**
     * いずれかの割り込みコントローラが割り込み線をアサートしていたら、
     * IRQ 例外を要求します。
     */
    public void acceptIRQ() {
        String msg;

        if (getCPSR().getIBit()) {
            //I ビットが 1 の場合は、IRQ 無効を意味する
            return;
        }

        if (!getINTCForIRQ().isAssert()) {
            //割り込み要求がない
            return;
        }

        //割り込み要求の詳細説明を得る
        msg = String.format("accept IRQ from '%s'",
                getINTCForIRQ().getIRQMessage());

        raiseException(EXCEPT_IRQ, msg);
    }

    /**
     * いずれかの割り込みコントローラが割り込み線をアサートしていたら、
     * FIQ 例外を要求します。
     */
    public void acceptFIQ() {
        String msg;

        if (getCPSR().getFBit()) {
            //F ビットが 1 の場合は、FIQ 無効を意味する
            return;
        }

        if (!getINTCForFIQ().isAssert()) {
            //割り込み要求がない
            return;
        }

        //割り込み要求の詳細説明を得る
        msg = String.format("accept FIQ from '%s'",
                getINTCForFIQ().getIRQMessage());

        raiseException(EXCEPT_FIQ, msg);
    }

    /**
     * 最後に行われた命令実行において、
     * CPU が例外を要求したかどうかを取得します。
     *
     * @return CPU が例外を要求した場合 true、要求していない場合 false
     */
    public boolean isRaised() {
        return raised;
    }

    /**
     * CPU が例外を要求したかどうかを設定します。
     *
     * @param m CPU が例外を要求した場合 true、要求していない場合 false
     */
    public void setRaised(boolean m) {
        synchronized(this) {
            raised = m;
        }
    }

    /**
     * CPU が例外を要求したかどうかの状態をクリアします。
     */
    public void clearRaised() {
        setRaised(false);
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
        synchronized(this) {
            jumped = b;
        }
    }

    /**
     * 例外ベクタの位置が、ハイベクタ 0xffff0000～0xffff001c にあるか、
     * 正規ベクタ 0x00000000～0x0000001c にあるかを取得します。
     *
     * @return 例外ベクタの位置、ハイベクタの場合は true、
     * 正規ベクタの場合は false
     */
    public boolean isHighVector() {
        return highVector;
    }

    /**
     * 例外ベクタの位置が、ハイベクタ 0xffff0000～0xffff001c にあるか、
     * 正規ベクタ 0x00000000～0x0000001c にあるかを設定します。
     *
     * @param m 新たな例外ベクタの位置、ハイベクタの場合は true、
     *          正規ベクタの場合は false
     */
    public void setHighVector(boolean m) {
        synchronized(this) {
            highVector = m;
        }
    }

    @Override
    public void step() {
        Instruction inst;

        //for debug
        int target_address1 = 0x0;//0xc036aee8; //<versatile_init_irq>
        int target_address2 = 0x0;//0xc036aee8; //<versatile_init_irq>

        //要求された例外のうち、優先度の高い例外を 1つだけ発生させます
        doImportantException();

        //高速割り込み線がアサートされていれば、FIQ 例外を要求します
        acceptFIQ();
        if (isRaised()) {
            clearRaised();
            return;
        }

        //割り込み線がアサートされていれば、IRQ 例外を要求します
        acceptIRQ();
        if (isRaised()) {
            clearRaised();
            return;
        }

        //命令を取得します
        inst = fetch();
        if (isRaised()) {
            clearRaised();
            return;
        }

        //for debug, 表示調整用
        if (getPC() - 8 == target_address1) {
            setDisasmMode(true);
            setPrintDisasm(true);
        }
        if (getPC() - 8 == target_address2) {
            setPrintRegs(true);
        }

        if (isDisasmMode()) {
            disasm(inst);
        }

        //for debug, ブレーク用
        if (isPrintRegs()) {
            int a = 0;
        }

        //実行して、次の命令へ
        execute(inst);
        if (isRaised()) {
            clearRaised();
            return;
        }
        nextPC();
    }
}
