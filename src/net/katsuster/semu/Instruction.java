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

    /**
     * ARM 命令のバイナリデータを取得します。
     *
     * @return ARM 命令のバイナリデータ
     */
    public int getInst() {
        return rawInst;
    }

    /**
     * ARM 命令のバイナリデータの指定された 1ビットを取得します。
     *
     * @param bit ビット位置
     * @return 指定されたビットがセットされていれば 1、そうでなければ 0
     */
    public int getBit(int bit) {
        return BitOp.getBit(rawInst, bit);
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
     * ARM 命令セットの cond フィールド（ビット [31:28]）が示す、
     * 条件の名前を取得します。
     *
     * AL の場合は空の文字列を返します。
     *
     * @return cond フィールドの条件の名前
     */
    public String getCondFieldName() {
        return getCondFieldName(getCondField());
    }

    /**
     * ARM 命令セットの cond フィールド（ビット [31:28]）が示す、
     * 条件の名前を取得します。
     *
     * AL の場合は空の文字列を返します。
     *
     * @param cond ARM 命令の cond フィールド
     * @return cond フィールドの条件の名前
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
                    String.format("%d.", cond));
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
        int n = BitOp.getBit(psr, CPU.PSR_BIT_N);
        int z = BitOp.getBit(psr, CPU.PSR_BIT_Z);
        int c = BitOp.getBit(psr, CPU.PSR_BIT_C);
        int v = BitOp.getBit(psr, CPU.PSR_BIT_V);

        switch (cond) {
        case Instruction.COND_EQ:
            return z == 1;
        case Instruction.COND_NE:
            return z == 0;
        case Instruction.COND_CS:
            return c == 1;
        case Instruction.COND_CC:
            return c == 0;
        case Instruction.COND_MI:
            return n == 1;
        case Instruction.COND_PL:
            return n == 0;
        case Instruction.COND_VS:
            return v == 1;
        case Instruction.COND_VC:
            return v == 0;
        case Instruction.COND_HI:
            return (c == 1) && (z == 0);
        case Instruction.COND_LS:
            return (c == 0) || (z == 1);
        case Instruction.COND_GE:
            return n == v;
        case Instruction.COND_LT:
            return n != v;
        case Instruction.COND_GT:
            return (z == 0) && (n == v);
        case Instruction.COND_LE:
            return (z == 1) || (n != v);
        case Instruction.COND_AL:
        case Instruction.COND_NV:
            return true;
        default:
            throw new IllegalArgumentException(String.format(
                    "Unknown cond %d, psr 0x%08x.", cond, psr));
        }
    }

    public static final int SUBCODE_USEALU = 0;
    public static final int SUBCODE_LDRSTR = 1;
    public static final int SUBCODE_LDMSTM = 2;
    public static final int SUBCODE_COPSWI = 3;

    /**
     * ARM 命令セットのサブコードのフィールド（ビット [27:26]）を取得します。
     *
     * 注: ARM の仕様書にはサブコードのフィールドという定義はありません。
     * このアプリケーション独自の定義です。
     *
     * @return サブコード
     */
    public int getSubCodeField() {
        return getSubCodeField(rawInst);
    }

    /**
     * ARM 命令セットのサブコードフィールド（ビット [27:26]）を取得します。
     *
     * 注: ARM の仕様書にはサブコードのフィールドという定義はありません。
     * このアプリケーション独自の定義です。
     *
     * @param inst ARM 命令
     * @return サブコード
     */
    public static int getSubCodeField(int inst) {
        return (inst >> 26) & 0x3;
    }

    /**
     * ARM 命令の I ビット（ビット 25）を取得します。
     *
     * データ処理命令に存在し、
     * オペランドがイミディエートかどうかを示します。
     *
     * @return I ビット
     */
    public int getIBit() {
        return getIBit(rawInst);
    }

    /**
     * ARM 命令の I ビット（ビット 25）を取得します。
     *
     * データ処理命令に存在し、
     * オペランドがイミディエートかどうかを示します。
     *
     * @param inst ARM 命令
     * @return I ビット
     */
    public static int getIBit(int inst) {
        return BitOp.getBit(inst, 25);
    }

    public static final int OPCODE_AND = 0;
    public static final int OPCODE_EOR = 1;
    public static final int OPCODE_SUB = 2;
    public static final int OPCODE_RSB = 3;
    public static final int OPCODE_ADD = 4;
    public static final int OPCODE_ADC = 5;
    public static final int OPCODE_SBC = 6;
    public static final int OPCODE_RSC = 7;
    public static final int OPCODE_TST = 8;
    public static final int OPCODE_TEQ = 9;
    public static final int OPCODE_CMP = 10;
    public static final int OPCODE_CMN = 11;
    public static final int OPCODE_ORR = 12;
    public static final int OPCODE_MOV = 13;
    public static final int OPCODE_BIC = 14;
    public static final int OPCODE_MVN = 15;

    /**
     * ARM 命令セットのオペコードフィールド（ビット [24:21]）を取得します。
     *
     * このフィールドが存在するのは、データ命令処理のみです。
     *
     * @return オペコード
     */
    public int getOpcodeField() {
        return getOpcodeField(rawInst);
    }

    /**
     * ARM 命令セットのオペコードフィールド（ビット [24:21]）を取得します。
     *
     * このフィールドが存在するのは、データ命令処理のみです。
     *
     * @param inst ARM 命令
     * @return オペコード
     */
    public static int getOpcodeField(int inst) {
        return (inst >> 21) & 0xf;
    }

    /**
     * ARM 命令セットの opcode フィールド（ビット [24:21]）が示す、
     * 演算の名前を取得します。
     *
     * @return opcode フィールドが示す演算の名前
     */
    public String getOpcodeFieldName() {
        return getOpcodeFieldName(getOpcodeField());
    }

    /**
     * ARM 命令セットの opcode フィールド（ビット [24:21]）が示す、
     * 演算の名前を取得します。
     *
     * @param opcode ARM 命令のオペコードフィールド
     * @return opcode フィールドが示す演算の名前
     */
    public static String getOpcodeFieldName(int opcode) {
        final String[] names = {
                "and", "eor", "sub", "rsb",
                "add", "adc", "sbc", "rsc",
                "tst", "teq", "cmp", "cmn",
                "orr", "mov", "bic", "mvn",
        };

        if (0 <= opcode && opcode <= 15) {
            return names[opcode];
        } else {
            throw new IllegalArgumentException("Invalid opcode " +
                    opcode + ".");
        }
    }

    public static final int OPCODE_S_AND = 0;
    public static final int OPCODE_S_EOR = 2;
    public static final int OPCODE_S_SUB = 4;
    public static final int OPCODE_S_RSB = 6;
    public static final int OPCODE_S_ADD = 8;
    public static final int OPCODE_S_ADC = 10;
    public static final int OPCODE_S_SBC = 12;
    public static final int OPCODE_S_RSC = 14;
    public static final int OPCODE_S_OTH = 16;
    public static final int OPCODE_S_TST = 17;
    public static final int OPCODE_S_MSR = 18;
    public static final int OPCODE_S_TEQ = 19;
    //16 と同じ //public static final int OPCODE_S_MRS = 20;
    public static final int OPCODE_S_CMP = 21;
    //18 と同じ //public static final int OPCODE_S_MSR = 22;
    public static final int OPCODE_S_CMN = 23;
    public static final int OPCODE_S_ORR = 24;
    public static final int OPCODE_S_MOV = 26;
    public static final int OPCODE_S_BIC = 28;
    public static final int OPCODE_S_MVN = 30;
    public static final int OPCODE_S_UND = 32;

    public static final int[] opcodeSBitShiftTable = {
            //0
            OPCODE_S_AND, OPCODE_S_AND,
            OPCODE_S_EOR, OPCODE_S_EOR,
            OPCODE_S_SUB, OPCODE_S_SUB,
            OPCODE_S_RSB, OPCODE_S_RSB,
            OPCODE_S_ADD, OPCODE_S_ADD,
            OPCODE_S_ADC, OPCODE_S_ADC,
            OPCODE_S_SBC, OPCODE_S_SBC,
            OPCODE_S_RSC, OPCODE_S_RSC,
            //16
            OPCODE_S_OTH, OPCODE_S_TST,
            OPCODE_S_OTH, OPCODE_S_TEQ,
            OPCODE_S_OTH, OPCODE_S_CMP,
            OPCODE_S_OTH, OPCODE_S_CMN,
            OPCODE_S_ORR, OPCODE_S_ORR,
            OPCODE_S_MOV, OPCODE_S_MOV,
            OPCODE_S_BIC, OPCODE_S_BIC,
            OPCODE_S_MVN, OPCODE_S_MVN,
    };

    public static final int[] opcodeSBitImmTable = {
            //0
            OPCODE_S_AND, OPCODE_S_AND,
            OPCODE_S_EOR, OPCODE_S_EOR,
            OPCODE_S_SUB, OPCODE_S_SUB,
            OPCODE_S_RSB, OPCODE_S_RSB,
            OPCODE_S_ADD, OPCODE_S_ADD,
            OPCODE_S_ADC, OPCODE_S_ADC,
            OPCODE_S_SBC, OPCODE_S_SBC,
            OPCODE_S_RSC, OPCODE_S_RSC,
            //16
            OPCODE_S_UND, OPCODE_S_TST,
            OPCODE_S_MSR, OPCODE_S_TEQ,
            OPCODE_S_UND, OPCODE_S_CMP,
            OPCODE_S_MSR, OPCODE_S_CMN,
            OPCODE_S_ORR, OPCODE_S_ORR,
            OPCODE_S_MOV, OPCODE_S_MOV,
            OPCODE_S_BIC, OPCODE_S_BIC,
            OPCODE_S_MVN, OPCODE_S_MVN,
    };

    /**
     * ARM 命令セットのデータ処理シフト命令における、
     * オペコードフィールド（ビット [24:21]）と、
     * S ビット（ビット 20）が示す演算の ID を取得します。
     *
     * このフィールドが存在するのは、データ命令処理のみです。
     *
     * @return 演算を示す ID
     */
    public int getOpcodeSBitShiftID() {
        return getOpcodeSBitShiftID(rawInst);
    }

    /**
     * ARM 命令セットのデータ処理シフト命令における、
     * オペコードフィールド（ビット [24:21]）と、
     * S ビット（ビット 20）が示す演算の ID を取得します。
     *
     * このフィールドが存在するのは、データ命令処理のみです。
     *
     * @param inst ARM 命令
     * @return 演算を示す ID
     */
    public static int getOpcodeSBitShiftID(int inst) {
        return opcodeSBitShiftTable[(inst >> 20) & 0x1f];
    }

    /**
     * ARM 命令セットのデータ処理イミディエート命令における、
     * オペコードフィールド（ビット [24:21]）と、
     * S ビット（ビット 20）が示す演算の ID を取得します。
     *
     * このフィールドが存在するのは、データ命令処理のみです。
     *
     * @return 演算を示す ID
     */
    public int getOpcodeSBitImmID() {
        return getOpcodeSBitImmID(rawInst);
    }

    /**
     * ARM 命令セットのデータ処理イミディエート命令における、
     * オペコードフィールド（ビット [24:21]）と、
     * S ビット（ビット 20）が示す演算の ID を取得します。
     *
     * このフィールドが存在するのは、データ命令処理のみです。
     *
     * @param inst ARM 命令
     * @return 演算を示す ID
     */
    public static int getOpcodeSBitImmID(int inst) {
        return opcodeSBitImmTable[(inst >> 20) & 0x1f];
    }

    public static final int PU_ADDR4_IA = 1;
    public static final int PU_ADDR4_IB = 3;
    public static final int PU_ADDR4_DA = 0;
    public static final int PU_ADDR4_DB = 2;

    /**
     * ARM 命令の P, U ビット（ビット [24:23]）を取得します。
     *
     * ロード、ストアマルチプル命令に存在し、
     * アドレシングモード 4 の 4つのモード（IA, IB, DA, DB）のうち、
     * どのモードを使用するか指定するフィールドです。
     *
     * @return P, U ビット
     */
    public int getPUField() {
        return getPUField(rawInst);
    }

    /**
     * ARM 命令の P, U ビット（ビット [24:23]）を取得します。
     *
     * ロード、ストアマルチプル命令に存在し、
     * アドレシングモード 4 の 4つのモード（IA, IB, DA, DB）のうち、
     * どのモードを使用するか指定するフィールドです。
     *
     * @param inst ARM 命令
     * @return P, U ビット
     */
    public static int getPUField(int inst) {
        return (inst >> 23) & 0x3;
    }

    /**
     * ARM 命令の P, U ビット（ビット [24:23]）が示す、
     * アドレシングモードの名前を取得します。
     *
     * @return P, U ビットが指すアドレシングモードの名前
     */
    public String getPUFieldName() {
        return getPUFieldName(getPUField());
    }

    /**
     * ARM 命令の P, U ビット（ビット [24:23]）が示す、
     * アドレシングモードの名前を取得します。
     *
     * @param pu ARM 命令の P, U ビット
     * @return P, U ビットが指すアドレシングモードの名前
     */
    public static String getPUFieldName(int pu) {
        final String[] names = {
                "da", "ia", "db", "ib",
        };

        if (0 <= pu && pu <= 3) {
            return names[pu];
        } else {
            throw new IllegalArgumentException("Invalid p, u bits " +
                    pu + ".");
        }
    }

    /**
     * ARM 命令の S ビット（ビット 20）を取得します。
     *
     * データ処理命令に存在し、
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
     * データ処理命令に存在し、
     * このビットが 1 の場合、PSR の状態ビット
     * （N, Z, C, V ビット）を更新します。
     *
     * @param inst ARM 命令
     * @return S ビット
     */
    public static int getSBit(int inst) {
        return BitOp.getBit(inst, 20);
    }

    /**
     * ARM 命令の L ビット（ビット 20）を取得します。
     *
     * ロード、ストア、ロードストアマルチプル処理命令に存在し、
     * このビットが 1 の場合、ロード命令であることを示します。
     *
     * @return L ビット
     */
    public int getLBit() {
        return getLBit(rawInst);
    }

    /**
     * ARM 命令の L ビット（ビット 20）を取得します。
     *
     * ロード、ストア、ロードストアマルチプル処理命令に存在し、
     * このビットが 1 の場合、ロード命令であることを示します。
     *
     * @param inst ARM 命令
     * @return L ビット
     */
    public static int getLBit(int inst) {
        return BitOp.getBit(inst, 20);
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
     * ARM 命令のレジスタリストフィールド（ビット [15:0]）を取得します。
     *
     * @return レジスタリストフィールド
     */
    public int getRegListField() {
        return getRegListField(rawInst);
    }

    /**
     * ARM 命令のレジスタリストフィールド（ビット [15:0]）を取得します。
     *
     * @param inst ARM 命令
     * @return レジスタリストフィールド
     */
    public static int getRegListField(int inst) {
        return inst & 0xffff;
    }

    /**
     * ARM 命令のレジスタリストフィールドの名前を取得します。
     *
     * @return レジスタリストに含まれるレジスタの名前一覧
     */
    public String getRegListFieldName() {
        return getRegListFieldName(getRegListField());
    }

    /**
     * ARM 命令のレジスタリストフィールドの名前を取得します。
     *
     * @param rlist レジスタリストフィールド
     * @return レジスタリストに含まれるレジスタの名前一覧
     */
    public static String getRegListFieldName(int rlist) {
        StringBuilder sb = new StringBuilder();
        int i, cnt;

        cnt = 0;
        for (i = 0; i < 16; i++) {
            if ((rlist & (1 << i)) != 0) {
                if (cnt != 0) {
                    sb.append(", ");
                }
                sb.append(String.format("r%d", i));
                cnt += 1;
            }
        }

        return sb.toString();
    }
}
