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

    public MMU(CPU cpu) {
        this.enable = false;

        this.cpu = cpu;
        this.stdCp = cpu.getStdCoProc();
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
     * MMU の状態を更新します。
     *
     * 下記を変更した際に呼び出す必要があります。
     *
     * - TTBR0: 変換テーブルベース（Cp15 レジスタ 2）
     */
    public void update() {
        tableBase = stdCp.getCReg(StdCoProc.CR02_MMU_TTBR0);
    }

    /**
     * アドレス変換を行います。
     *
     * @param va 仮想アドレス（VA）
     * @return 物理アドレス（PA）
     */
    public int translate(int va) {
        int tblEntry, tblType, pa;

        if (!isEnable()) {
            return va;
        }

        tblEntry = getTableEntry(va);
        tblType = tblEntry & 0x3;

        switch (tblType) {
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
            pa = translateSection(va, tblEntry);
            break;
        case 3:
            //詳細ページテーブル
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        default:
            throw new IllegalArgumentException("Unknown tblType " +
                    String.format("entry:0x%08x, type:%d.",
                            tblEntry, tblType));
        }

        return pa;
    }

    /**
     * 第 1 レベル記述子を取得します。
     *
     * @param va 仮想アドレス
     * @return 第 1 レベル記述子
     */
    protected int getTableEntry(int va) {
        int tblBase, tblIndex, tblAddr, tblEntry;

        tblBase = tableBase & 0xffffc000;
        tblIndex = (va >> 20) & 0xfff;
        tblAddr = tblBase | (tblIndex << 2);
        tblEntry = cpu.read32(tblAddr);

        return tblEntry;
    }

    protected int translateSection(int va, int entry) {
        int baseSec = (entry >> 20) & 0xfff;
        int ap = (entry >> 10) & 0x3;
        int dom = (entry >> 5) & 0xf;
        boolean imp = BitOp.getBit(entry, 4);
        boolean c = BitOp.getBit(entry, 3);
        boolean b = BitOp.getBit(entry, 2);
        int vaIndex, pa;

        //TODO: アクセスチェック
        //if (cond) {
            //違反ならデータアボート
        //}

        //物理アドレス
        vaIndex = va & 0xfffff;
        pa = (baseSec << 20) | vaIndex;

        return pa;
    }
}
