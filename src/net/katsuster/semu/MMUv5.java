package net.katsuster.semu;

/**
 * ARMv5 VMSA
 *
 * VMSA: 仮想メモリシステムアーキテクチャ
 *
 * 参考: ARM アーキテクチャリファレンスマニュアル Second Edition
 * ARM DDI0100DJ
 *
 * @author katsuhiro
 */
public class MMUv5 {
    private boolean enable;

    private ARMv5 cpu;
    private CoProcStdv5 cpStd;
    private int tableBase;

    public MMUv5(ARMv5 cpu, CoProcStdv5 cp) {
        this.enable = false;

        this.cpu = cpu;
        this.cpStd = cp;
        this.tableBase = 0;
    }

    /**
     * MMU が有効か否かを取得します。
     *
     * @return MMU が有効ならば true、無効ならば false
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * MMU が有効か否かを設定します。
     *
     * @param e MMU が有効ならば true、無効ならば false
     */
    public void setEnable(boolean e) {
        enable = e;
    }

    /**
     * MMU が接続されている CPU を取得します。
     *
     * @return MMU が接続されている CPU
     */
    public ARMv5 getCPU() {
        return cpu;
    }

    /**
     * MMU が接続されている標準コプロセッサを取得します。
     *
     * @return MMU が接続されている標準コプロセッサ
     */
    public CoProcStdv5 getCoProcStd() {
        return cpStd;
    }

    /**
     * MMU の状態を更新します。
     *
     * 下記を変更した際に呼び出す必要があります。
     *
     * - TTBR0: 変換テーブルベース（Cp15 レジスタ 2）
     */
    public void update() {
        tableBase = getCoProcStd().getCReg(CoProcStdv5.CR02_MMU_TTBR0);
    }

    public static final int FS_TERM = 0x2;
    public static final int FS_VECT = 0x0;
    public static final int FS_ALIGN1 = 0x1;
    public static final int FS_ALIGN2 = 0x3;
    public static final int FS_TRANS_L1 = 0xc;
    public static final int FS_TRANS_L2 = 0xe;
    public static final int FS_TRANS_SEC = 0x5;
    public static final int FS_TRANS_PAGE = 0x7;
    public static final int FS_DOM_SEC = 0x9;
    public static final int FS_DOM_PAGE = 0xb;
    public static final int FS_PERM_SEC = 0xd;
    public static final int FS_PERM_PAGE = 0xf;
    public static final int FS_LINE_SEC = 0x4;
    public static final int FS_LINE_PAGE = 0x6;
    public static final int FS_ABORT_SEC = 0x8;
    public static final int FS_ABORT_PAGE = 0xa;

    /**
     * MMU フォルトを発生させます。
     *
     * CPU に対しては、プリフェッチアボート例外、
     * またはデータアボート例外を発生させます。
     *
     * @param fs   フォルトステータス
     * @param dom  ドメイン、存在しない場合は 0
     * @param va   仮想アドレス（VA）
     * @param inst 命令の場合は true、データの場合は false
     * @param dbgmsg デバッグ用のメッセージ
     */
    public void faultMMU(int fs, int dom, int va, boolean inst, String dbgmsg) {
        int val, num;

        //フォルトステータス
        val = cpStd.getCReg(5);
        BitOp.setField32(val, 4, 3, dom);
        BitOp.setField32(val, 0, 3, fs);
        cpStd.setCReg(CoProcStdv5.CR05_MMU_FSR, val);

        //フォルトアドレス
        cpStd.setCReg(CoProcStdv5.CR06_MMU_FAR, va);

        //例外を発生させる
        if (inst) {
            //プリフェッチアボート例外
            num = ARMv5.EXCEPT_ABT_INST;
        } else {
            //データアボート例外
            num = ARMv5.EXCEPT_ABT_DATA;
        }
        getCPU().raiseException(num, dbgmsg);
    }

    /**
     * アドレス変換を行います。
     *
     * 変換時にエラーが発生した場合はアボートが発生します。
     * 仮想アドレスが指すデータの種類により、
     * 命令の場合はプリフェッチアボートが発生します。
     * データの場合はデータアボートが発生します。
     *
     * @param va   仮想アドレス（VA）
     * @param inst 仮想アドレスが指すデータの種類、
     *             命令の場合は true、データの場合は false
     * @return 物理アドレス（PA）
     */
    public int translate(int va, boolean inst) {
        int paL1, entryL1, typeL1, pa;

        if (!isEnable()) {
            return va;
        }

        paL1 = getL1Address(va);
        if (!getCPU().tryRead(paL1)) {
            faultMMU(FS_TRANS_L1, 0, va, inst,
                    String.format("MMU read L1 [%08x]", paL1));
            return 0;
        }
        entryL1 = getCPU().read32(paL1);
        typeL1 = BitOp.getField32(entryL1, 0, 2);

        switch (typeL1) {
        case 0:
            //フォルト
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        case 1:
            //概略ページテーブル
            pa = translateCoarse(va, entryL1);
            break;
        case 2:
            //セクション
            pa = translateSection(va, entryL1);
            break;
        case 3:
            //詳細ページテーブル
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        default:
            throw new IllegalArgumentException("Unknown L1 table " +
                    String.format("va:0x%08x, paL1:0x%08x, entryL1:0x%08x, typeL1:%d.",
                            va, paL1, entryL1, typeL1));
        }

        return pa;
    }

    /**
     * 変換テーブルの第 1 レベル記述子のアドレスを取得します。
     *
     * TTBR の上位 18 ビットの「変換ベース」と、
     * 仮想アドレスの上位 12 ビットの「テーブルインデクス」から、
     * 第 1 レベル記述子の位置（物理アドレス）が決まります。
     *
     * 記述子のサイズは 4 バイトのため、アドレスの下位 2 ビットは 0 です。
     *
     * @param va 仮想アドレス
     * @return 第 1 レベル記述子
     */
    protected int getL1Address(int va) {
        int tblBase = BitOp.getField32(tableBase, 14, 18);
        int tblIndex = BitOp.getField32(va, 20, 12);
        int pa;

        pa = (tblBase << 14) | (tblIndex << 2);

        return pa;
    }

    /**
     * 概略ページテーブルの第 2 レベル記述子のアドレスを取得します。
     *
     * 第 1 レベル記述子の 31～10 ビットの「ページテーブルベースアドレス」と、
     * 仮想アドレスの 19～14 ビットの「テーブルインデクス」から、
     * 第 2 レベル記述子の位置（物理アドレス）が決まります。
     *
     * 記述子のサイズは 4 バイトのため、アドレスの下位 2 ビットは 0 です。
     *
     * @param va 仮想アドレス
     * @param entryL1 概略ページテーブルの第 1 レベル記述子
     * @return 概略ページテーブルの第 2 レベル記述子のアドレス
     */
    protected int getL2AddressCoarse(int va, int entryL1) {
        int base = BitOp.getField32(entryL1, 10, 22);
        int tblIndex = BitOp.getField32(va, 12, 8);
        int pa;

        pa = (base << 10) | (tblIndex << 2);

        return pa;
    }

    /**
     * 詳細ページテーブルの第 2 レベル記述子のアドレスを取得します。
     *
     * 第 1 レベル記述子の 31～14 ビットの「ページテーブルベースアドレス」と、
     * 仮想アドレスの 19～10 ビットの「テーブルインデクス」から、
     * 第 2 レベル記述子の位置（物理アドレス）が決まります。
     *
     * 記述子のサイズは 4 バイトのため、アドレスの下位 2 ビットは 0 です。
     *
     * @param va 仮想アドレス
     * @param entryL1 詳細ページテーブルの第 1 レベル記述子
     * @return 詳細ページテーブルの第 2 レベル記述子のアドレス
     */
    protected int getL2AddressFine(int va, int entryL1) {
        int base = BitOp.getField32(entryL1, 14, 20);
        int tblIndex = BitOp.getField32(va, 10, 10);
        int pa;

        pa = (base << 12) | (tblIndex << 2);

        return pa;
    }

    /**
     * 大ページ（64KB）のアドレスを取得します。
     *
     * @param va 仮想アドレス
     * @param entryL2 第 2 レベル記述子
     * @return 大ページの物理アドレス
     */
    protected int getPageAddressLarge(int va, int entryL2) {
        int base = BitOp.getField32(entryL2, 16, 16);
        int tblIndex = BitOp.getField32(va, 0, 16);
        int pa;

        pa = (base << 16) | tblIndex;

        return pa;
    }

    /**
     * 小ページ（4KB）のアドレスを取得します。
     *
     * @param va 仮想アドレス
     * @param entryL2 第 2 レベル記述子
     * @return 小ページの物理アドレス
     */
    protected int getPageAddressSmall(int va, int entryL2) {
        int base = BitOp.getField32(entryL2, 12, 20);
        int tblIndex, pa;

        tblIndex = BitOp.getField32(va, 0, 12);
        pa = (base << 12) | tblIndex;

        return pa;
    }

    /**
     * 極小ページ（1KB）のアドレスを取得します。
     *
     * @param va 仮想アドレス
     * @param entryL2 第 2 レベル記述子
     * @return 極小ページの物理アドレス
     */
    protected int getPageAddressTiny(int va, int entryL2) {
        int base = BitOp.getField32(entryL2, 10, 22);
        int tblIndex = BitOp.getField32(va, 0, 10);
        int pa;

        pa = (base << 10) | tblIndex;

        return pa;
    }

    /**
     * セクション（1MB のメモリブロック）のアドレスを変換します。
     *
     * 第 2 レベル記述子は存在しません。
     *
     * @param va      仮想アドレス
     * @param entryL1 第 1 レベル記述子
     * @return 物理アドレス
     */
    protected int translateSection(int va, int entryL1) {
        int base = BitOp.getField32(entryL1, 20, 12);
        int ap = BitOp.getField32(entryL1, 10, 2);
        int dom = BitOp.getField32(entryL1, 5, 4);
        boolean imp = BitOp.getBit32(entryL1, 4);
        boolean c = BitOp.getBit32(entryL1, 3);
        boolean b = BitOp.getBit32(entryL1, 2);
        int tblIndex = BitOp.getField32(va, 0, 20);
        int pa;

        //TODO: アクセスチェック
        //if (cond) {
        //TODO: 違反ならデータアボート
        //}

        //物理アドレス
        pa = (base << 20) | tblIndex;

        return pa;
    }

    /**
     * 概略ページテーブルのアドレスを変換します。
     *
     * 第 2 レベル記述子は、大ページ（64KB）または、
     * 小ページ（4KB）が存在します。
     *
     * @param va      仮想アドレス
     * @param entryL1 第 1 レベル記述子
     * @return 物理アドレス
     */
    protected int translateCoarse(int va, int entryL1) {
        int paL2, entryL2, typeL2;
        int pa;

        //TODO: アクセスチェック
        //if (cond) {
        //TODO: 違反ならデータアボート
        //}

        paL2 = getL2AddressCoarse(va, entryL1);

        entryL2 = getCPU().read32(paL2);
        typeL2 = BitOp.getField32(entryL2, 0, 2);

        switch (typeL2) {
        case 0:
            //フォルト
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        case 1:
            //大ページ
            pa = getPageAddressLarge(va, entryL2);
            break;
        case 2:
            //小ページ
            pa = getPageAddressSmall(va, entryL2);
            break;
        case 3:
            //極小ページ（使用禁止）
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        default:
            throw new IllegalArgumentException("Unknown L2 table " +
                    String.format("va:0x%08x, L1:0x%08x, paL2:0x%08x, L2:0x%08x typeL2:%d.",
                            va, entryL1, paL2, entryL2, typeL2));
        }

        return pa;
    }
}
