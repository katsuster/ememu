package net.katsuster.semu;

/**
 * ARM MMU
 *
 * @author katsuhiro
 */
public class MMU {
    private boolean enable;

    private CPU cpu;
    private StdCoProc stdCp;
    private int tableBase;

    public MMU(CPU cpu, StdCoProc cp) {
        this.enable = false;

        this.cpu = cpu;
        this.stdCp = cp;
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
    public CPU getCPU() {
        return cpu;
    }

    /**
     * MMU が接続されている標準コプロセッサを取得します。
     *
     * @return MMU が接続されている標準コプロセッサ
     */
    public StdCoProc getStdCoProc() {
        return stdCp;
    }

    /**
     * MMU の状態を更新します。
     *
     * 下記を変更した際に呼び出す必要があります。
     *
     * - TTBR0: 変換テーブルベース（Cp15 レジスタ 2）
     */
    public void update() {
        tableBase = getStdCoProc().getCReg(StdCoProc.CR02_MMU_TTBR0);
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
            //TODO: アボートを通知する
            return 0;
        }
        entryL1 = getCPU().read32(paL1);
        typeL1 = entryL1 & 0x3;

        switch (typeL1) {
        case 0:
            //フォルト
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        case 1:
            //概略ページテーブル
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
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
            throw new IllegalArgumentException("Unknown tblType " +
                    String.format("addr:0x%08x, entry:0x%08x, type:%d.",
                            paL1, entryL1, typeL1));
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
        int tblBase, tblIndex, pa;

        tblBase = (tableBase >> 14) & 0x3ffff;
        tblIndex = (va >> 20) & 0xfff;
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
        int base = (entryL1 >> 10) & 0x3fffff;
        int tblIndex, pa;

        tblIndex = (va >> 12) & 0xff;
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
        int base = (entryL1 >> 14) & 0xfffff;
        int tblIndex, pa;

        tblIndex = (va >> 10) & 0x3ff;
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
        int base = (entryL2 >> 16) & 0xffff;
        int tblIndex, pa;

        tblIndex = va & 0xffff;
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
        int base = (entryL2 >> 12) & 0xfffff;
        int tblIndex, pa;

        tblIndex = va & 0xfff;
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
        int base = (entryL2 >> 10) & 0x3fffff;
        int tblIndex, pa;

        tblIndex = va & 0x3ff;
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
        int base = (entryL1 >> 20) & 0xfff;
        int ap = (entryL1 >> 10) & 0x3;
        int dom = (entryL1 >> 5) & 0xf;
        boolean imp = BitOp.getBit(entryL1, 4);
        boolean c = BitOp.getBit(entryL1, 3);
        boolean b = BitOp.getBit(entryL1, 2);
        int tblIndex, pa;

        //TODO: アクセスチェック
        //if (cond) {
        //TODO: 違反ならデータアボート
        //}

        //物理アドレス
        tblIndex = va & 0xfffff;
        pa = (base << 20) | tblIndex;

        return pa;
    }
}
