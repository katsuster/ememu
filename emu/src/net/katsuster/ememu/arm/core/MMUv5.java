package net.katsuster.ememu.arm.core;

import net.katsuster.ememu.generic.BitOp;

/**
 * ARMv5 VMSA
 *
 * <p>
 * VMSA: 仮想メモリシステムアーキテクチャ
 * </p>
 * <p>
 * 参考: ARM アーキテクチャリファレンスマニュアル Second Edition
 * ARM DDI0100DJ
 * </p>
 *
 * @author katsuhiro
 */
public class MMUv5 {
    private boolean alignCheck;
    private boolean enable;
    private boolean fault;
    private int tableBase;
    private int[] domAcc;
    private boolean systemProtect;
    private boolean romProtect;

    private ARMv5 cpu;
    private CoProcStdv5 cpStd;

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

    public static final int DOMACC_INVALID = 0x0;
    public static final int DOMACC_CLIENT = 0x1;
    public static final int DOMACC_RESERVED = 0x2;
    public static final int DOMACC_MANAGER = 0x3;

    public MMUv5(ARMv5 cpu, CoProcStdv5 cp) {
        this.alignCheck = false;
        this.enable = false;
        this.fault = false;
        this.tableBase = 0;
        this.domAcc = new int[16];
        this.systemProtect = false;
        this.romProtect = false;

        this.cpu = cpu;
        this.cpStd = cp;
    }

    /**
     * アドレスアラインメントのチェックをするかどうかを取得します。
     *
     * @return チェックを行う場合は true、行わない場合は false
     */
    public boolean isAlignmentCheck() {
        return alignCheck;
    }

    /**
     * アドレスアラインメントのチェックをするかどうかを設定します。
     *
     * @param m チェックを行う場合は true、行わない場合は false
     */
    public void setAlignmentCheck(boolean m) {
        alignCheck = m;
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
     * 最後に行われたアドレス変換において、
     * MMU がフォルトを起こしたかどうかを取得します。
     *
     * @return MMU がフォルトを起こした場合 true、起こしていない場合 false
     */
    public boolean isFault() {
        return fault;
    }

    /**
     * MMU がフォルトを起こしたかどうかを設定します。
     *
     * @param m MMU がフォルトを起こした場合 true、起こしていない場合 false
     */
    public void setFault(boolean m) {
        fault = m;
    }

    /**
     * MMU がフォルトを起こしたかどうかの状態をクリアします。
     */
    public void clearFault() {
        setFault(false);
    }

    /**
     * MMU フォルトを発生させます。
     *
     * CPU に対しては、プリフェッチアボート例外、
     * またはデータアボート例外を発生させます。
     *
     * @param fs   フォルトステータス
     * @param dom  ドメイン、存在しない場合は 0
     * @param va   仮想アドレス（VA）
     * @param inst 仮想アドレスが指すデータの種類、
     *             命令の場合は true、データの場合は false
     * @param priv 特権アクセスならば true、非特権アクセスならば false
     * @param read 読み取りアクセスならば true、書き込みアクセスならば false
     * @param dbgmsg デバッグ用のメッセージ
     */
    public void faultMMU(int fs, int dom, int va, boolean inst, boolean priv, boolean read, String dbgmsg) {
        int val, num;

        //フォルトを起こしたことを覚えておく
        setFault(true);

        //フォルトステータス
        val = getCoProcStd().getCReg(5);
        val = BitOp.setField32(val, 4, 4, dom);
        val = BitOp.setField32(val, 0, 4, fs);
        getCoProcStd().setCReg(CoProcStdv5.CR05_MMU_FSR, val);

        //フォルトアドレス
        getCoProcStd().setCReg(CoProcStdv5.CR06_MMU_FAR, va);

        //例外を発生させる
        if (inst) {
            //プリフェッチアボート例外
            num = ARMv5.EXCEPT_ABT_INST;
        } else {
            //データアボート例外
            num = ARMv5.EXCEPT_ABT_DATA;
        }
        getCPU().raiseException(num, String.format("%s, va:0x%08x, dom:%d, inst:%s, priv:%s, read:%s.",
                dbgmsg, va, dom,
                inst ? "ins" : "dat",
                priv ? "priv" : "user",
                read ? "rd" : "wr"));
    }

    /**
     * MMU の変換テーブルベースを取得します。
     *
     * @return 変換テーブルベース
     */
    public int getTableBase() {
        return tableBase;
    }

    /**
     * MMU の変換テーブルベースを設定します。
     *
     * 下記を変更した際に呼び出す必要があります。
     *
     * - TTBR0: 変換テーブルベース（Cp15 レジスタ 2）
     *
     * @param base 変換テーブルベース
     */
    public void setTableBase(int base) {
        tableBase = base;
    }

    /**
     * 指定されたドメインのアクセス値を取得します。
     *
     * @param n ドメイン番号
     * @return 指定されたドメインのアクセス値
     */
    public int getDomainAccess(int n) {
        if (n < 0 || 16 <= n) {
            throw new IllegalArgumentException(String.format(
                    "Illegal domain %d.", n));
        }

        return domAcc[n];
    }

    /**
     * 指定されたドメインのアクセス値を設定します。
     *
     * @param n   ドメイン番号
     * @param acc ドメインのアクセス値
     */
    public void setDomainAccess(int n, int acc) {
        if (n < 0 || 16 <= n) {
            throw new IllegalArgumentException(String.format(
                    "Illegal domain %d.", n));
        }
        if (acc < 0 || 4 <= acc) {
            throw new IllegalArgumentException(String.format(
                    "Illegal access type %d.", acc));
        }

        domAcc[n] = acc;
    }

    /**
     * MMU のシステム保護ビットを取得します。
     *
     * セクション変換エントリ、ページ変換エントリの、
     * AP ビットと共に参照されます。
     *
     * @return システム保護ビット
     */
    public boolean isSystemProtect() {
        return systemProtect;
    }

    /**
     * MMU のシステム保護ビットを設定します。
     *
     * セクション変換エントリ、ページ変換エントリの、
     * AP ビットと共に参照されます。
     *
     * @param b システム保護ビット
     */
    public void setSystemProtect(boolean b) {
        systemProtect = b;
    }

    /**
     * MMU の ROM 保護ビットを取得します。
     *
     * セクション変換エントリ、ページ変換エントリの、
     * AP ビットと共に参照されます。
     *
     * @return ROM 保護ビット
     */
    public boolean isROMProtect() {
        return romProtect;
    }

    /**
     * MMU の ROM 保護ビットを設定します。
     *
     * セクション変換エントリ、ページ変換エントリの、
     * AP ビットと共に参照されます。
     *
     * @param b ROM 保護ビット
     */
    public void setROMProtect(boolean b) {
        romProtect = b;
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
     * アドレス変換を行います。
     *
     * 変換時にエラーが発生した場合はアボートが発生します。
     * 仮想アドレスが指すデータの種類により、
     * 命令の場合はプリフェッチアボートが発生します。
     * データの場合はデータアボートが発生します。
     *
     * @param va   仮想アドレス（VA）
     * @param size アクセスするサイズ
     * @param inst 仮想アドレスが指すデータの種類、
     *             命令の場合は true、データの場合は false
     * @param priv 特権アクセスならば true、非特権アクセスならば false
     * @param read 読み取りアクセスならば true、書き込みアクセスならば false
     * @return 物理アドレス（PA）
     */
    public int translate(int va, int size, boolean inst, boolean priv, boolean read) {
        int paL1, entryL1, typeL1, pa;
        boolean validAlign;

        if (isFault()) {
            //フォルト状態がクリアされず残っている
            throw new IllegalStateException("Fault status not cleared.");
        }

        //TODO: アドレス以外の条件がある？
        /*if (!inst && 0x00 <= va && va <= 0x1f) {
            //ベクタ例外
            faultMMU(FS_VECT, 0, va, inst, priv, read,
                    "MMU vector");
            return 0;
        }*/

        if (!isEnable()) {
            //MMU 無効なので変換しない
            return va;
        }

        validAlign = (va & (size - 1)) == 0;
        if (isAlignmentCheck() && !validAlign) {
            //アラインメントフォルト
            faultMMU(FS_ALIGN1, 0, va, inst, priv, read,
                    String.format("MMU align, size:%d", size));
            return 0;
        }

        paL1 = getL1Address(va);
        if (!getCPU().tryRead_a32(paL1, 4)) {
            //変換時の外部アボート、第1レベル
            faultMMU(FS_TRANS_L1, 0, va, inst, priv, read,
                    String.format("MMU trans L1, paL1:0x%08x", paL1));
            return 0;
        }
        entryL1 = getCPU().read32_a32(paL1);
        typeL1 = BitOp.getField32(entryL1, 0, 2);

        switch (typeL1) {
        case 0:
            //フォルト
            //変換フォルト、セクション
            faultMMU(FS_TRANS_SEC, 0, va, inst, priv, read,
                    String.format("MMU trans sec, paL1:0x%08x, entryL1:0x%08x",
                            paL1, entryL1));
            return 0;
        case 1:
            //概略ページテーブル
            pa = translateCoarse(va, inst, priv, read, entryL1);
            break;
        case 2:
            //セクション
            pa = translateSection(va, inst, priv, read, entryL1);
            break;
        case 3:
            //詳細ページテーブル
            pa = translateFine(va, inst, priv, read, entryL1);
            break;
        default:
            throw new IllegalArgumentException("Unknown L1 table " +
                    String.format("paL1:0x%08x, entryL1:0x%08x, typeL1:%d.",
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
        int tblBase = BitOp.getField32(getTableBase(), 14, 18);
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
     * @param va  仮想アドレス
     * @param inst 仮想アドレスが指すデータの種類、
     *             命令の場合は true、データの場合は false
     * @param priv 特権アクセスならば true、非特権アクセスならば false
     * @param read 読み取りアクセスならば true、書き込みアクセスならば false
     * @param entryL1 第 1 レベル記述子
     * @param entryL2 第 2 レベル記述子
     * @return 大ページの物理アドレス
     */
    protected int getPageAddressLarge(int va, boolean inst, boolean priv, boolean read, int entryL1, int entryL2) {
        int dom = BitOp.getField32(entryL1, 5, 4);
        int base = BitOp.getField32(entryL2, 16, 16);
        //int ap3 = BitOp.getField32(entryL2, 10, 2);
        //int ap2 = BitOp.getField32(entryL2, 8, 2);
        //int ap1 = BitOp.getField32(entryL2, 6, 2);
        //int ap0 = BitOp.getField32(entryL2, 4, 2);
        int sub = BitOp.getField32(va, 14, 2);
        int tblIndex = BitOp.getField32(va, 0, 16);
        int acc, apsub, pa;

        switch (sub) {
        case 0: case 1: case 2: case 3:
            apsub = BitOp.getField32(entryL2, 4 + sub * 2, 2);
            break;
        default:
            throw new IllegalArgumentException("Unknown sub page (large), " +
                    String.format("sub:%d, va:0x%08x, entryL1:0x%08x, entryL2:%d.",
                            sub, va, entryL1, entryL2));
        }

        acc = getDomainAccess(dom);
        switch (acc) {
        case DOMACC_INVALID:
        case DOMACC_RESERVED:
            //ドメインフォルト、ページ
            faultMMU(FS_DOM_PAGE, dom, va, inst, priv, read,
                    String.format("Domain page (large), dom list:0x%08x, dom acc:%d, entryL1:0x%08x",
                            getCoProcStd().getCReg(CoProcStdv5.CR03_MMU_DACR),
                            acc, entryL1));
            return 0;
        case DOMACC_CLIENT:
            if (isPermitted(priv, read, apsub)) {
                //アクセス許可がある
                break;
            }

            //許可フォルト、ページ
            faultMMU(FS_PERM_PAGE, dom, va, inst, priv, read,
                    String.format("Permission page (large), dom acc:%d, sub:%d, apsub:%d, entryL1:0x%08x, entryL2:0x%08x",
                            acc, sub, apsub, entryL1, entryL2));
            return 0;
        case DOMACC_MANAGER:
            //アクセス許可がある
            break;
        default:
            throw new IllegalArgumentException("Unknown domain access (large), " +
                    String.format("dom acc:%d, va:0x%08x, entryL1:0x%08x, entryL2:%d.",
                            acc, va, entryL1, entryL2));
        }

        pa = (base << 16) | tblIndex;

        return pa;
    }

    /**
     * 小ページ（4KB）のアドレスを取得します。
     *
     * @param va   仮想アドレス
     * @param inst 仮想アドレスが指すデータの種類、
     *             命令の場合は true、データの場合は false
     * @param priv 特権アクセスならば true、非特権アクセスならば false
     * @param read 読み取りアクセスならば true、書き込みアクセスならば false
     * @param entryL1 第 1 レベル記述子
     * @param entryL2 第 2 レベル記述子
     * @return 小ページの物理アドレス
     */
    protected int getPageAddressSmall(int va, boolean inst, boolean priv, boolean read, int entryL1, int entryL2) {
        int dom = BitOp.getField32(entryL1, 5, 4);
        int base = BitOp.getField32(entryL2, 12, 20);
        //int ap3 = BitOp.getField32(entryL2, 10, 2);
        //int ap2 = BitOp.getField32(entryL2, 8, 2);
        //int ap1 = BitOp.getField32(entryL2, 6, 2);
        //int ap0 = BitOp.getField32(entryL2, 4, 2);
        int sub = BitOp.getField32(va, 10, 2);
        int tblIndex = BitOp.getField32(va, 0, 12);
        int acc, apsub, pa;

        switch (sub) {
        case 0: case 1: case 2: case 3:
            apsub = BitOp.getField32(entryL2, 4 + sub * 2, 2);
            break;
        default:
            throw new IllegalArgumentException("Unknown sub page (small), " +
                    String.format("sub:%d, va:0x%08x, entryL1:0x%08x, entryL2:%d.",
                            sub, va, entryL1, entryL2));
        }

        acc = getDomainAccess(dom);
        switch (acc) {
        case DOMACC_INVALID:
        case DOMACC_RESERVED:
            //ドメインフォルト、ページ
            faultMMU(FS_DOM_PAGE, dom, va, inst, priv, read,
                    String.format("Domain page (small), dom list:0x%08x, dom acc:%d, entryL1:0x%08x",
                            getCoProcStd().getCReg(CoProcStdv5.CR03_MMU_DACR),
                            acc, entryL1));
            return 0;
        case DOMACC_CLIENT:
            if (isPermitted(priv, read, apsub)) {
                //アクセス許可がある
                break;
            }

            //許可フォルト、ページ
            faultMMU(FS_PERM_PAGE, dom, va, inst, priv, read,
                    String.format("Permission page (small), dom acc:%d, sub:%d, apsub:%d, entryL1:0x%08x, entryL2:0x%08x",
                            acc, sub, apsub, entryL1, entryL2));
            return 0;
        case DOMACC_MANAGER:
            //アクセス許可がある
            break;
        default:
            throw new IllegalArgumentException("Unknown domain access (small), " +
                    String.format("dom acc:%d, va:0x%08x, entryL1:0x%08x, entryL2:%d.",
                            acc, va, entryL1, entryL2));
        }

        pa = (base << 12) | tblIndex;

        return pa;
    }

    /**
     * 極小ページ（1KB）のアドレスを取得します。
     *
     * @param va  仮想アドレス
     * @param inst 仮想アドレスが指すデータの種類、
     *             命令の場合は true、データの場合は false
     * @param priv 特権アクセスならば true、非特権アクセスならば false
     * @param read 読み取りアクセスならば true、書き込みアクセスならば false
     * @param entryL1 第 1 レベル記述子
     * @param entryL2 第 2 レベル記述子
     * @return 極小ページの物理アドレス
     */
    protected int getPageAddressTiny(int va, boolean inst, boolean priv, boolean read, int entryL1, int entryL2) {
        int dom = BitOp.getField32(entryL1, 5, 4);
        int base = BitOp.getField32(entryL2, 10, 22);
        int ap = BitOp.getField32(entryL2, 4, 2);
        int tblIndex = BitOp.getField32(va, 0, 10);
        int acc, pa;

        acc = getDomainAccess(dom);
        switch (acc) {
        case DOMACC_INVALID:
        case DOMACC_RESERVED:
            //ドメインフォルト、ページ
            faultMMU(FS_DOM_PAGE, dom, va, inst, priv, read,
                    String.format("Domain page (tiny), dom list:0x%08x, dom acc:%d, entryL1:0x%08x",
                            getCoProcStd().getCReg(CoProcStdv5.CR03_MMU_DACR),
                            acc, entryL1));
            return 0;
        case DOMACC_CLIENT:
            if (isPermitted(priv, read, ap)) {
                //アクセス許可がある
                break;
            }

            //許可フォルト、ページ
            faultMMU(FS_PERM_PAGE, dom, va, inst, priv, read,
                    String.format("Permission page (tiny), dom acc:%d, ap:%d, entryL1:0x%08x, entryL2:0x%08x",
                            acc, ap, entryL1, entryL2));
            return 0;
        case DOMACC_MANAGER:
            //アクセス許可がある
            break;
        default:
            throw new IllegalArgumentException("Unknown domain access (tiny), " +
                    String.format("dom acc:%d, va:0x%08x, entryL1:0x%08x, entryL2:%d.",
                            acc, va, entryL1, entryL2));
        }

        pa = (base << 10) | tblIndex;

        return pa;
    }

    /**
     * アクセスが許可されるかどうかを取得します。
     *
     * @param priv 特権アクセスならば true、非特権アクセスならば false
     * @param read 読み取りアクセスならば true、書き込みアクセスならば false
     * @param ap   アクセスタイプ
     * @return アクセスが許可される場合は true、許可されない場合は false
     */
    protected boolean isPermitted(boolean priv, boolean read, int ap) {
        boolean result;

        switch (ap) {
        case 0:
            if (!isSystemProtect() && !isROMProtect()) {
                result = false;
            } else if (isSystemProtect() && !isROMProtect()) {
                result = priv && read;
            } else if (!isSystemProtect() && isROMProtect()) {
                result = read;
            } else {
                //unpredictable
                result = false;
            }
            break;
        case 1:
            result = priv;
            break;
        case 2:
            result = priv || read;
            break;
        case 3:
            result = true;
            break;
        default:
            throw new IllegalArgumentException(String.format(
                    "Unknown AP field %d.", ap));
        }

        return result;
    }

    /**
     * セクション（1MB のメモリブロック）のアドレスを変換します。
     *
     * 第 2 レベル記述子は存在しません。
     *
     * @param va      仮想アドレス
     * @param inst 仮想アドレスが指すデータの種類、
     *             命令の場合は true、データの場合は false
     * @param priv 特権アクセスならば true、非特権アクセスならば false
     * @param read 読み取りアクセスならば true、書き込みアクセスならば false
     * @param entryL1 第 1 レベル記述子
     * @return 物理アドレス
     */
    protected int translateSection(int va, boolean inst, boolean priv, boolean read, int entryL1) {
        int base = BitOp.getField32(entryL1, 20, 12);
        int ap = BitOp.getField32(entryL1, 10, 2);
        int dom = BitOp.getField32(entryL1, 5, 4);
        //boolean imp = BitOp.getBit32(entryL1, 4);
        //boolean c = BitOp.getBit32(entryL1, 3);
        //boolean b = BitOp.getBit32(entryL1, 2);
        int tblIndex = BitOp.getField32(va, 0, 20);
        int acc, pa;

        acc = getDomainAccess(dom);
        switch (acc) {
        case DOMACC_INVALID:
        case DOMACC_RESERVED:
            //ドメインフォルト、セクション
            faultMMU(FS_DOM_SEC, dom, va, inst, priv, read,
                    String.format("Domain sec, dom list:0x%08x, dom acc:%d, entryL1:0x%08x",
                            getCoProcStd().getCReg(CoProcStdv5.CR03_MMU_DACR),
                            acc, entryL1));
            return 0;
        case DOMACC_CLIENT:
            if (isPermitted(priv, read, ap)) {
                //アクセス許可がある
                break;
            }

            //許可フォルト、セクション
            faultMMU(FS_PERM_SEC, dom, va, inst, priv, read,
                    String.format("Permission sec, dom acc:%d, ap:%d, entryL1:0x%08x",
                            acc, ap, entryL1));
            return 0;
        case DOMACC_MANAGER:
            //アクセス許可がある
            break;
        default:
            throw new IllegalArgumentException("Unknown domain access (large), " +
                    String.format("dom acc:%d, va:0x%08x, entryL1:0x%08x.",
                            acc, va, entryL1));
        }

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
     * @param inst 仮想アドレスが指すデータの種類、
     *             命令の場合は true、データの場合は false
     * @param priv 特権アクセスならば true、非特権アクセスならば false
     * @param read 読み取りアクセスならば true、書き込みアクセスならば false
     * @param entryL1 第 1 レベル記述子
     * @return 物理アドレス
     */
    protected int translateCoarse(int va, boolean inst, boolean priv, boolean read, int entryL1) {
        //int dom = BitOp.getField32(entryL1, 5, 4);
        //int imp = BitOp.getField32(entryL1, 2, 3);
        int paL2, entryL2, typeL2;
        int pa;

        paL2 = getL2AddressCoarse(va, entryL1);
        if (!getCPU().tryRead_a32(paL2, 4)) {
            //変換時の外部アボート、第2レベル
            faultMMU(FS_TRANS_L2, 0, va, inst, priv, read,
                    String.format("MMU trans L2 coarse, entryL1:0x%08x, paL2:0x%08x",
                            entryL1, paL2));
            return 0;
        }
        entryL2 = getCPU().read32_a32(paL2);
        typeL2 = BitOp.getField32(entryL2, 0, 2);

        switch (typeL2) {
        case 0:
            //フォルト
            //変換フォルト、ページ
            faultMMU(FS_TRANS_PAGE, 0, va, inst, priv, read,
                    String.format("MMU trans coarse page, paL2:0x%08x, entryL2:0x%08x",
                            paL2, entryL2));
            return 0;
        case 1:
            //大ページ
            pa = getPageAddressLarge(va, inst, priv, read, entryL1, entryL2);
            break;
        case 2:
            //小ページ
            pa = getPageAddressSmall(va, inst, priv, read, entryL1, entryL2);
            break;
        case 3:
            //極小ページ（使用禁止）
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        default:
            throw new IllegalArgumentException("Unknown L2 table coarse " +
                    String.format("va:0x%08x, entryL1:0x%08x, paL2:0x%08x, entryL2:0x%08x typeL2:%d.",
                            va, entryL1, paL2, entryL2, typeL2));
        }

        return pa;
    }

    /**
     * 詳細ページテーブルのアドレスを変換します。
     *
     * 第 2 レベル記述子は、大ページ（64KB）、小ページ（4KB）または、
     * 極小ページ（1KB）が存在します。
     *
     * @param va      仮想アドレス
     * @param inst 仮想アドレスが指すデータの種類、
     *             命令の場合は true、データの場合は false
     * @param priv 特権アクセスならば true、非特権アクセスならば false
     * @param read 読み取りアクセスならば true、書き込みアクセスならば false
     * @param entryL1 第 1 レベル記述子
     * @return 物理アドレス
     */
    protected int translateFine(int va, boolean inst, boolean priv, boolean read, int entryL1) {
        //int dom = BitOp.getField32(entryL1, 5, 4);
        //int imp = BitOp.getField32(entryL1, 2, 3);
        int paL2, entryL2, typeL2;
        int pa;

        paL2 = getL2AddressFine(va, entryL1);
        if (!getCPU().tryRead_a32(paL2, 4)) {
            //変換時の外部アボート、第2レベル
            faultMMU(FS_TRANS_L2, 0, va, inst, priv, read,
                    String.format("MMU trans L2 fine, entryL1:0x%08x, paL2:0x%08x",
                            entryL1, paL2));
            return 0;
        }
        entryL2 = getCPU().read32_a32(paL2);
        typeL2 = BitOp.getField32(entryL2, 0, 2);

        switch (typeL2) {
        case 0:
            //フォルト
            //変換フォルト、ページ
            faultMMU(FS_TRANS_PAGE, 0, va, inst, priv, read,
                    String.format("MMU trans L2 fine fault page, paL2:0x%08x, entryL2:0x%08x",
                            paL2, entryL2));
            return 0;
        case 1:
            //大ページ
            pa = getPageAddressLarge(va, inst, priv, read, entryL1, entryL2);
            break;
        case 2:
            //小ページ
            pa = getPageAddressSmall(va, inst, priv, read, entryL1, entryL2);
            break;
        case 3:
            //極小ページ（使用禁止）
            pa = getPageAddressTiny(va, inst, priv, read, entryL1, entryL2);
            break;
        default:
            throw new IllegalArgumentException("Unknown L2 table fine " +
                    String.format("va:0x%08x, entryL1:0x%08x, paL2:0x%08x, entryL2:0x%08x typeL2:%d.",
                            va, entryL1, paL2, entryL2, typeL2));
        }

        return pa;
    }
}
