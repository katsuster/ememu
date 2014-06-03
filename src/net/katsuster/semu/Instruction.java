package net.katsuster.semu;

/**
 * 命令。
 *
 * @author katsuhiro
 */
public class Instruction {
    private int rawInst;

    public Instruction(int inst) {
        this.rawInst = inst;
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
     * ARM 命令のバイナリデータを取得します。
     *
     * @return ARM 命令のバイナリデータ
     */
    public int getInst() {
        return rawInst;
    }

    /**
     * ARM 命令の cond フィールド（ビット [31:28]）を取得します。
     *
     * @return cond フィールド
     */
    public int getCondField() {
        return getCondField(rawInst);
    }

    /**
     * ARM 命令セットの cond フィールド（ビット [31:28]）を取得します。
     *
     * @param inst ARM 命令
     * @return cond フィールド
     */
    public static int getCondField(int inst) {
        return (inst >> 28) & 0xf;
    }

    /**
     * ARM 命令セットの cond フィールドの名前を取得します。
     *
     * AL の場合は空の文字列を返します。
     *
     * @return cond フィールドの名前
     */
    public String getCondFieldName() {
        return getCondFieldName(getCondField());
    }

    /**
     * ARM 命令セットの cond フィールドの名前を取得します。
     *
     * AL の場合は空の文字列を返します。
     *
     * @param cond ARM 命令の cond フィールド
     * @return cond フィールドの名前
     */
    public static String getCondFieldName(int cond) {
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
     * ステータスレジスタの値が、
     * 条件フィールドで指定された条件を満たしているかどうか判定します。
     *
     * cond フィールドが NV の場合は常に true を返し、条件の判定は行いません。
     * 各命令ごとに適切な判定を行って下さい。
     *
     * @param psr プログラムステータスレジスタの値
     * @return 条件を満たしていれば true、満たしていなければ false
     */
    public boolean satisfiesCond(int psr) {
        return satisfiesCond(getCondField(), psr);
    }

    /**
     * ステータスレジスタの値が、
     * cond フィールドで指定された条件を満たしているかどうか判定します。
     *
     * cond フィールドが NV の場合は常に true を返し、条件の判定は行いません。
     * 各命令ごとに適切な判定を行って下さい。
     *
     * @param cond  ARM 命令の cond フィールド
     * @param psr プログラムステータスレジスタの値
     * @return 条件を満たしていれば true、満たしていなければ false
     */
    public static boolean satisfiesCond(int cond, int psr) {
        switch (cond) {
        case Instruction.COND_EQ:
            return CPU.getPSR_Z(psr) == 1;
        case Instruction.COND_NE:
            return CPU.getPSR_Z(psr) == 0;
        case Instruction.COND_CS:
            return CPU.getPSR_C(psr) == 1;
        case Instruction.COND_CC:
            return CPU.getPSR_C(psr) == 0;
        case Instruction.COND_MI:
            return CPU.getPSR_N(psr) == 1;
        case Instruction.COND_PL:
            return CPU.getPSR_N(psr) == 0;
        case Instruction.COND_VS:
            return CPU.getPSR_V(psr) == 1;
        case Instruction.COND_VC:
            return CPU.getPSR_V(psr) == 0;
        case Instruction.COND_HI:
            return (CPU.getPSR_C(psr) == 1) && (CPU.getPSR_Z(psr) == 0);
        case Instruction.COND_LS:
            return (CPU.getPSR_C(psr) == 0) || (CPU.getPSR_Z(psr) == 1);
        case Instruction.COND_GE:
            return CPU.getPSR_N(psr) == CPU.getPSR_V(psr);
        case Instruction.COND_LT:
            return CPU.getPSR_N(psr) != CPU.getPSR_V(psr);
        case Instruction.COND_GT:
            return (CPU.getPSR_Z(psr) == 0) &&
                    (CPU.getPSR_N(psr) == CPU.getPSR_V(psr));
        case Instruction.COND_LE:
            return (CPU.getPSR_Z(psr) == 1) ||
                    (CPU.getPSR_N(psr) != CPU.getPSR_V(psr));
        case Instruction.COND_AL:
        case Instruction.COND_NV:
            return true;
        default:
            throw new IllegalArgumentException(String.format(
                    "Unknown cond %d, psr 0x%08x.", cond, psr));
        }
    }

    public static final int SUB_ADDSFT = 0;
    public static final int SUB_MRSREG = 1;
    public static final int SUB_MSRREG = 2;
    public static final int SUB_ANDIMM = 100;
    public static final int SUB_EORIMM = 101;
    public static final int SUB_SUBIMM = 102;
    public static final int SUB_RSBIMM = 103;
    public static final int SUB_ADDIMM = 104;
    public static final int SUB_ADCIMM = 105;
    public static final int SUB_SBCIMM = 106;
    public static final int SUB_RSCIMM = 107;
    public static final int SUB_TSTIMM = 108;
    public static final int SUB_TEQIMM = 109;
    public static final int SUB_CMPIMM = 110;
    public static final int SUB_CMNIMM = 111;
    public static final int SUB_ORRIMM = 112;
    public static final int SUB_MOVIMM = 113;
    public static final int SUB_BICIMM = 114;
    public static final int SUB_MVNIMM = 115;
    public static final int SUB_UNDIMM = 116;
    public static final int SUB_MSRIMM = 117;
    public static final int SUB_LDRIMM = 6;
    public static final int SUB_LDRREG = 7;
    public static final int SUB_LDMSTM = 8;
    public static final int SUB_BL_BLX = 10;
    public static final int SUB_LDCSTC = 12;
    public static final int SUB_CDPMCR = 14;
    public static final int SUB_CDPMRC = 15;
    public static final int SUB_SWIIMM = 16;

    private static final int[] subinsts = {
            //0b000_00000: データ処理
            //  0b000_10x00: mrs ステータスレジスタへレジスタ転送
            //               16, 20
            //  0b000_10x10: msr ステータスレジスタへレジスタ転送
            //               18, 22
            SUB_ADDSFT, SUB_ADDSFT, SUB_ADDSFT, SUB_ADDSFT,
            SUB_ADDSFT, SUB_ADDSFT, SUB_ADDSFT, SUB_ADDSFT,
            SUB_ADDSFT, SUB_ADDSFT, SUB_ADDSFT, SUB_ADDSFT,
            SUB_ADDSFT, SUB_ADDSFT, SUB_ADDSFT, SUB_ADDSFT,

            SUB_MRSREG, SUB_ADDSFT, SUB_MSRREG, SUB_ADDSFT,
            SUB_MRSREG, SUB_ADDSFT, SUB_MSRREG, SUB_ADDSFT,
            SUB_ADDSFT, SUB_ADDSFT, SUB_ADDSFT, SUB_ADDSFT,
            SUB_ADDSFT, SUB_ADDSFT, SUB_ADDSFT, SUB_ADDSFT,

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
            SUB_ANDIMM, SUB_ANDIMM, SUB_EORIMM, SUB_EORIMM,
            SUB_SUBIMM, SUB_SUBIMM, SUB_RSBIMM, SUB_RSBIMM,
            SUB_ADDIMM, SUB_ADDIMM, SUB_ADCIMM, SUB_ADCIMM,
            SUB_SBCIMM, SUB_SBCIMM, SUB_RSCIMM, SUB_RSCIMM,

            SUB_UNDIMM, SUB_TSTIMM, SUB_MSRIMM, SUB_TEQIMM,
            SUB_UNDIMM, SUB_CMPIMM, SUB_MSRIMM, SUB_CMNIMM,
            SUB_ORRIMM, SUB_ORRIMM, SUB_MOVIMM, SUB_MOVIMM,
            SUB_BICIMM, SUB_BICIMM, SUB_MVNIMM, SUB_MVNIMM,

            //0b010_00000
            SUB_LDRIMM, SUB_LDRIMM, SUB_LDRIMM, SUB_LDRIMM,
            SUB_LDRIMM, SUB_LDRIMM, SUB_LDRIMM, SUB_LDRIMM,
            SUB_LDRIMM, SUB_LDRIMM, SUB_LDRIMM, SUB_LDRIMM,
            SUB_LDRIMM, SUB_LDRIMM, SUB_LDRIMM, SUB_LDRIMM,

            SUB_LDRIMM, SUB_LDRIMM, SUB_LDRIMM, SUB_LDRIMM,
            SUB_LDRIMM, SUB_LDRIMM, SUB_LDRIMM, SUB_LDRIMM,
            SUB_LDRIMM, SUB_LDRIMM, SUB_LDRIMM, SUB_LDRIMM,
            SUB_LDRIMM, SUB_LDRIMM, SUB_LDRIMM, SUB_LDRIMM,

            //0b011_00000
            SUB_LDRREG, SUB_LDRREG, SUB_LDRREG, SUB_LDRREG,
            SUB_LDRREG, SUB_LDRREG, SUB_LDRREG, SUB_LDRREG,
            SUB_LDRREG, SUB_LDRREG, SUB_LDRREG, SUB_LDRREG,
            SUB_LDRREG, SUB_LDRREG, SUB_LDRREG, SUB_LDRREG,

            SUB_LDRREG, SUB_LDRREG, SUB_LDRREG, SUB_LDRREG,
            SUB_LDRREG, SUB_LDRREG, SUB_LDRREG, SUB_LDRREG,
            SUB_LDRREG, SUB_LDRREG, SUB_LDRREG, SUB_LDRREG,
            SUB_LDRREG, SUB_LDRREG, SUB_LDRREG, SUB_LDRREG,

            //0b100_00000
            SUB_LDMSTM, SUB_LDMSTM, SUB_LDMSTM, SUB_LDMSTM,
            SUB_LDMSTM, SUB_LDMSTM, SUB_LDMSTM, SUB_LDMSTM,
            SUB_LDMSTM, SUB_LDMSTM, SUB_LDMSTM, SUB_LDMSTM,
            SUB_LDMSTM, SUB_LDMSTM, SUB_LDMSTM, SUB_LDMSTM,

            SUB_LDMSTM, SUB_LDMSTM, SUB_LDMSTM, SUB_LDMSTM,
            SUB_LDMSTM, SUB_LDMSTM, SUB_LDMSTM, SUB_LDMSTM,
            SUB_LDMSTM, SUB_LDMSTM, SUB_LDMSTM, SUB_LDMSTM,
            SUB_LDMSTM, SUB_LDMSTM, SUB_LDMSTM, SUB_LDMSTM,

            //0b101_00000
            SUB_BL_BLX, SUB_BL_BLX, SUB_BL_BLX, SUB_BL_BLX,
            SUB_BL_BLX, SUB_BL_BLX, SUB_BL_BLX, SUB_BL_BLX,
            SUB_BL_BLX, SUB_BL_BLX, SUB_BL_BLX, SUB_BL_BLX,
            SUB_BL_BLX, SUB_BL_BLX, SUB_BL_BLX, SUB_BL_BLX,

            SUB_BL_BLX, SUB_BL_BLX, SUB_BL_BLX, SUB_BL_BLX,
            SUB_BL_BLX, SUB_BL_BLX, SUB_BL_BLX, SUB_BL_BLX,
            SUB_BL_BLX, SUB_BL_BLX, SUB_BL_BLX, SUB_BL_BLX,
            SUB_BL_BLX, SUB_BL_BLX, SUB_BL_BLX, SUB_BL_BLX,

            //0b110_00000
            SUB_LDCSTC, SUB_LDCSTC, SUB_LDCSTC, SUB_LDCSTC,
            SUB_LDCSTC, SUB_LDCSTC, SUB_LDCSTC, SUB_LDCSTC,
            SUB_LDCSTC, SUB_LDCSTC, SUB_LDCSTC, SUB_LDCSTC,
            SUB_LDCSTC, SUB_LDCSTC, SUB_LDCSTC, SUB_LDCSTC,

            SUB_LDCSTC, SUB_LDCSTC, SUB_LDCSTC, SUB_LDCSTC,
            SUB_LDCSTC, SUB_LDCSTC, SUB_LDCSTC, SUB_LDCSTC,
            SUB_LDCSTC, SUB_LDCSTC, SUB_LDCSTC, SUB_LDCSTC,
            SUB_LDCSTC, SUB_LDCSTC, SUB_LDCSTC, SUB_LDCSTC,

            //0b111_00000
            //  0b1110xxx0: cdp コプロセッサデータ処理
            //              mrc コプロセッサから ARM レジスタへ転送
            //              224, 226, 228, 230, 232, 234, 236, 238
            //  0b1110xxx1: cdp コプロセッサデータ処理
            //              mrc コプロセッサから ARM レジスタへ転送
            //              225, 227, 229, 231, 233, 235, 237, 239
            //  0b1111xxxx: swi ソフトウェア割り込み
            //              240, ..., 255
            SUB_CDPMCR, SUB_CDPMRC, SUB_CDPMCR, SUB_CDPMRC,
            SUB_CDPMCR, SUB_CDPMRC, SUB_CDPMCR, SUB_CDPMRC,
            SUB_CDPMCR, SUB_CDPMRC, SUB_CDPMCR, SUB_CDPMRC,
            SUB_CDPMCR, SUB_CDPMRC, SUB_CDPMCR, SUB_CDPMRC,

            SUB_SWIIMM, SUB_SWIIMM, SUB_SWIIMM, SUB_SWIIMM,
            SUB_SWIIMM, SUB_SWIIMM, SUB_SWIIMM, SUB_SWIIMM,
            SUB_SWIIMM, SUB_SWIIMM, SUB_SWIIMM, SUB_SWIIMM,
            SUB_SWIIMM, SUB_SWIIMM, SUB_SWIIMM, SUB_SWIIMM,
    };

    /**
     * ARM 命令セットのオペコードのフィールド（ビット [27:20]）から、
     * 命令のタイプを表す ID を取得します。
     *
     * @return 命令のタイプを表す ID
     */
    public int getSubcodeId() {
        return getSubcodeId(rawInst);
    }

    /**
     * ARM 命令セットのオペコードのフィールド（ビット [27:20]）から、
     * 命令のタイプを表す ID を取得します。
     *
     * @param inst ARM 命令
     * @return 命令のタイプを表す ID
     */
    public static int getSubcodeId(int inst) {
        return subinsts[(inst >> 20) & 0xff];
    }

    /**
     * ARM 命令の S ビット（ビット 20）を取得します。
     *
     * このビットが 1 の場合、PSR の状態ビット
     * （N, Z, C, V ビット）を更新します。
     *
     * @return S ビット
     */
    public int getSBit() {
        return getSBit(rawInst);
    }

    /**
     * ARM 命令の S ビット（ビット 20）を取得します。
     *
     * このビットが 1 の場合、PSR の状態ビット
     * （N, Z, C, V ビット）を更新します。
     *
     * @param inst ARM 命令
     * @return S ビット
     */
    public static int getSBit(int inst) {
        return (inst >> 20) & 0x1;
    }

    /**
     * ARM 命令の Rn フィールド（ビット [19:16]）を取得します。
     *
     * @return Rn フィールド
     */
    public int getRnField() {
        return getRnField(rawInst);
    }

    /**
     * ARM 命令の Rn フィールド（ビット [19:16]）を取得します。
     *
     * @param inst ARM 命令
     * @return Rn フィールド
     */
    public static int getRnField(int inst) {
        return (inst >> 16) & 0xf;
    }

    /**
     * ARM 命令の Rd フィールド（ビット [15:12]）を取得します。
     *
     * @return Rd フィールド
     */
    public int getRdField() {
        return getRdField(rawInst);
    }
    /**
     * ARM 命令の Rd フィールド（ビット [15:12]）を取得します。
     *
     * @param inst ARM 命令
     * @return Rd フィールド
     */
    public static int getRdField(int inst) {
        return (inst >> 12) & 0xf;
    }

    /**
     * データ処理オペランドの 32ビットイミディエートを取得します。
     *
     * rotate_imm: ビット[11:8]
     * immed_8: ビット[7:0]
     * とすると、イミディエート imm32 は下記のように求められます。
     *
     * imm32 = rotateRight(immed_8, rotate_imm * 2)
     *
     * @return イミディエート
     */
    public int getImm32Operand() {
        return getImm32Operand(rawInst);
    }

    /**
     * データ処理オペランドの 32ビットイミディエートを取得します。
     *
     * rotate_imm: ビット[11:8]
     * immed_8: ビット[7:0]
     * とすると、イミディエート imm32 は下記のように求められます。
     *
     * imm32 = rotateRight(immed_8, rotate_imm * 2)
     *
     * @param inst 命令コード
     * @return イミディエート
     */
    public static int getImm32Operand(int inst) {
        int rotR = (inst >> 8) & 0xf;
        int imm8 = inst & 0xff;

        return Integer.rotateRight(imm8, rotR * 2);
    }
}
