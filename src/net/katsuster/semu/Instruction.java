package net.katsuster.semu;

/**
 * 命令。
 *
 * @author katsuhiro
 */
public class Instruction {
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
     * ARM 命令セットの cond フィールド（ビット [31:28]）を取得します。
     *
     * @param op ARM 命令
     * @return cond フィールド
     */
    public static int getCondField(int op) {
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
     * @param op  ARM 命令
     * @param psr プログラムステータスレジスタの値
     * @return 条件を満たしていれば true、満たしていなければ false
     */
    public static boolean satisfiesCond(int op, int psr) {
        int cond = getCondField(op);

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

    private static final int[] optable = {
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
     * ARM 命令セットのオペコードのフィールド（ビット [27:20]）から、
     * 命令のタイプを表す ID を取得します。
     *
     * @param op ARM 命令
     * @return 命令のタイプを表す ID
     */
    public static int getSubcodeId(int op) {
        return optable[(op >> 20) & 0xff];
    }

    /**
     * ARM 命令の S ビット（ビット 20）を取得します。
     *
     * このビットが 1 の場合、PSR の状態ビット
     * （N, Z, C, V ビット）を更新します。
     *
     * @param op ARM 命令
     * @return S ビット
     */
    public static int getSBit(int op) {
        return (op >> 20) & 0x1;
    }

    /**
     * ARM 命令の Rn フィールド（ビット [19:16]）を取得します。
     *
     * @param op ARM 命令
     * @return Rn フィールド
     */
    public static int getRnField(int op) {
        return (op >> 16) & 0xf;
    }

    /**
     * ARM 命令の Rd フィールド（ビット [15:12]）を取得します。
     *
     * @param op ARM 命令
     * @return Rd フィールド
     */
    public static int getRdField(int op) {
        return (op >> 12) & 0xf;
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
     * @param op 命令コード
     * @return イミディエート
     */
    public static int getImm32Operand(int op) {
        int rotR = (op >> 8) & 0xf;
        int imm8 = op & 0xff;

        return Integer.rotateRight(imm8, rotR * 2);
    }
}
