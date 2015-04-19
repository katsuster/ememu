package net.katsuster.ememu.arm.core;

import net.katsuster.ememu.generic.BitOp;

/**
 * ARM コプロセッサ 15: 標準コプロセッサ。
 *
 * 参考: ARM アーキテクチャリファレンスマニュアル Second Edition
 * ARM DDI0100DJ
 *
 * @author katsuhiro
 */
public class CoProcStdv5 extends CoProc {
    //----------------------------------------------------------------------
    //crn00: ID コード、キャッシュタイプ、読み取り専用
    //----------------------------------------------------------------------
    public static final int CR00_MIDR = 0x00000000;
    public static final int CR00_CTR = 0x00000001;
    public static final int CR00_TCMTR = 0x00000002;
    public static final int CR00_TLBTR = 0x00000003;
    public static final int CR00_MPIDR = 0x00000005;

    //----------------------------------------------------------------------
    //crn01: 制御ビット
    //----------------------------------------------------------------------
    //制御ビット
    public static final int CR01_MMU_SCTLR = 0x00001000;

    //----------------------------------------------------------------------
    //crn02: メモリの保護と制御、MMU 変換テーブル
    //----------------------------------------------------------------------
    //変換ベーステーブル
    public static final int CR02_MMU_TTBR0 = 0x00002000;

    //----------------------------------------------------------------------
    //crn03: メモリの保護と制御、MMU ドメインアクセス制御
    //----------------------------------------------------------------------
    //ドメインアクセス制御
    public static final int CR03_MMU_DACR = 0x00003000;

    //----------------------------------------------------------------------
    //crn04: メモリの保護と制御、MMU 予約済み
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    //crn05: メモリの保護と制御、MMU フォルトステータス
    //----------------------------------------------------------------------
    //フォルトステータス
    public static final int CR05_MMU_FSR = 0x00005000;

    //----------------------------------------------------------------------
    //crn06: メモリの保護と制御、MMU フォルトアドレス
    //----------------------------------------------------------------------
    //フォルトアドレス
    public static final int CR06_MMU_FAR = 0x00006000;

    //----------------------------------------------------------------------
    //crn07: キャッシュとライトバッファ
    //  レジスタ名は規定無しのため、独自
    //----------------------------------------------------------------------
    //割り込み待ち(ARM926EJ-S ダイナミックパワーマネジメント機能)
    public static final int CR07_INTWAIT = 0x00007004;
    //命令キャッシュ全体の無効化
    public static final int CR07_ICH_INVALL = 0x00007050;
    //命令キャッシュラインの無効化（仮想アドレス）
    public static final int CR07_ICH_INVV = 0x00007051;
    //命令キャッシュラインの無効化（セット/インデクス）
    public static final int CR07_ICH_INVS = 0x00007052;
    //プリフェッチバッファのフラッシュ
    public static final int CR07_PRE_FLU = 0x00007054;
    //分岐ターゲットキャッシュ全体のフラッシュ
    public static final int CR07_BTC_FLU = 0x00007056;
    //分岐ターゲットキャッシュ全体のフラッシュ（実装定義）
    public static final int CR07_BTC_FLU2 = 0x00007057;
    //データキャッシュ全体の無効化
    public static final int CR07_DCH_INVALL = 0x00007060;
    //データキャッシュラインの無効化（仮想アドレス）
    public static final int CR07_DCH_INVV = 0x00007061;
    //データキャッシュラインの無効化（セット/インデクス）
    public static final int CR07_DCH_INVS = 0x00007062;
    //統合キャッシュ全体、または命令、データキャッシュ双方の無効化
    public static final int CR07_UCH_INVALL = 0x00007070;
    //統合キャッシュラインの無効化（仮想アドレス）
    public static final int CR07_UCH_INVV = 0x00007071;
    //統合キャッシュラインの無効化（セット/インデクス）
    public static final int CR07_UCH_INVS = 0x00007072;
    //割り込み待ち（非推奨）
    //public static final int CR07_ = 0x00007082;
    //データキャッシュラインのクリーン（仮想アドレス）
    public static final int CR07_DCH_CLNV = 0x000070a1;
    //データキャッシュラインのクリーン（セット/インデクス）
    public static final int CR07_DCH_CLNS = 0x000070a2;
    //データキャッシュラインのテスト、クリーン
    public static final int CR07_DCH_TSTCLN = 0x000070a3;
    //ライトバッファの排出
    public static final int CR07_WB_PUR = 0x000070a4;
    //統合キャッシュラインのクリーン（仮想アドレス）
    public static final int CR07_UCH_CLNV = 0x000070b1;
    //統合キャッシュラインのクリーン（セット/インデクス）
    public static final int CR07_UCH_CLNS = 0x000070b2;
    //命令キャッシュラインのプリフェッチ（仮想アドレス）
    public static final int CR07_ICH_PREV = 0x000070d1;
    //データキャッシュラインのクリーン/無効化（仮想アドレス）
    public static final int CR07_DCH_CLNINVV = 0x000070e1;
    //データキャッシュラインのクリーン/無効化（セット/インデクス）
    public static final int CR07_DCH_CLNINVS = 0x000070e2;
    //データキャッシュラインのテスト、クリーン、無効化
    public static final int CR07_DCH_TSTCLNINV = 0x000070e3;
    //統合キャッシュラインのクリーン/無効化（仮想アドレス）
    public static final int CR07_UCH_CLNINVV = 0x000070f1;
    //統合キャッシュラインのクリーン/無効化（セット/インデクス）
    public static final int CR07_UCH_CLNINVS = 0x000070f2;

    //----------------------------------------------------------------------
    //crn08: メモリの保護と制御、MMU TLB 制御
    //  レジスタ名は規定無しのため、独自
    //----------------------------------------------------------------------
    //統合 TLB 全体を無効化する
    public static final int CR08_UTLB_INVALL = 0x00008070;
    //統合シングルエントリを無効化する（仮想アドレス）
    public static final int CR08_UTLB_INVV = 0x00008071;
    //命令 TLB 全体を無効化する
    public static final int CR08_ITLB_INVALL = 0x00008050;
    //命令 TLB のシングルエントリを無効化する（仮想アドレス）
    public static final int CR08_ITLB_INVV = 0x00008051;
    //データ TLB 全体を無効化する
    public static final int CR08_DTLB_INVALL = 0x00008060;
    //データ TLB のシングルエントリを無効化する（仮想アドレス）
    public static final int CR08_DTLB_INVV = 0x00008061;

    //----------------------------------------------------------------------
    //crn09: キャッシュとライトバッファ、ロックダウン
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    //crn10: メモリの保護と制御、MMU TLB ロックダウン
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    //crn11: 予約済み
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    //crn12: 予約済み
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    //crn13: プロセス ID
    //----------------------------------------------------------------------
    //FCSE（高速コンテキストスイッチ拡張機能）のサポート予定はなし

    //----------------------------------------------------------------------
    //crn14: 予約済み
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    //crn15: 実装ごとに定義
    //----------------------------------------------------------------------

    public CoProcStdv5(int no, ARMv5 p) {
        super(no, p);

        //------------------------------------------------------------
        //crn00: ID コードレジスタ（読み取り専用）
        //------------------------------------------------------------
        //  implementer: 0x41(ARM), variant: 0x0(nothing),
        //  arch: 0x6(ARMv5TEJ), part: 0x926(ARM926), revision: 0x0
        addCReg(CR00_MIDR, "MIDR", 0x41069260);
        addCReg(CR00_CTR, "CTR", 0x00000000);
        addCReg(CR00_TCMTR, "TCMTR", 0x00000000);
        addCReg(CR00_TLBTR, "TLBTR", 0x00000000);
        addCReg(CR00_MPIDR, "MPIDR", 0x00000000);

        //------------------------------------------------------------
        //crn01: 制御ビット
        //------------------------------------------------------------
        addCReg(CR01_MMU_SCTLR, "MMU_SCTLR", 0x00000000);

        //------------------------------------------------------------
        //crn02: メモリの保護と制御、MMU 変換テーブル
        //------------------------------------------------------------
        addCReg(CR02_MMU_TTBR0, "TTBR0", 0x00000000);

        //------------------------------------------------------------
        //crn03: メモリの保護と制御、MMU ドメインアクセス制御
        //------------------------------------------------------------
        addCReg(CR03_MMU_DACR, "DACR", 0x00000000);

        //------------------------------------------------------------
        //crn05: メモリの保護と制御、MMU フォルトステータス
        //------------------------------------------------------------
        //フォルトステータス
        addCReg(CR05_MMU_FSR, "FSR", 0x00000000);

        //------------------------------------------------------------
        //crn06: メモリの保護と制御、MMU フォルトアドレス
        //------------------------------------------------------------
        //フォルトアドレス
        addCReg(CR06_MMU_FAR, "FAR", 0x00000000);

        //------------------------------------------------------------
        //crn07: キャッシュとライトバッファ（書き込み専用なので初期値 0）
        //------------------------------------------------------------
        addCReg(CR07_INTWAIT, "INTWAIT", 0x00000000);
        addCReg(CR07_ICH_INVALL, "ICH_INVALL", 0x00000000);
        addCReg(CR07_ICH_INVV, "ICH_INVV", 0x00000000);
        addCReg(CR07_ICH_INVS, "ICH_INVS", 0x00000000);
        addCReg(CR07_PRE_FLU, "PRE_FLU", 0x00000000);
        addCReg(CR07_BTC_FLU, "BTC_FLU", 0x00000000);
        addCReg(CR07_BTC_FLU2, "BTC_FLU2", 0x00000000);
        addCReg(CR07_DCH_INVALL, "DCH_INVALL", 0x00000000);
        addCReg(CR07_DCH_INVV, "DCH_INVV", 0x00000000);
        addCReg(CR07_DCH_INVS, "DCH_INVS", 0x00000000);
        addCReg(CR07_UCH_INVALL, "UCH_INVALL", 0x00000000);
        addCReg(CR07_UCH_INVV, "UCH_INVV", 0x00000000);
        addCReg(CR07_UCH_INVS, "UCH_INVS", 0x00000000);
        addCReg(CR07_DCH_CLNV, "DCH_CLNV", 0x00000000);
        addCReg(CR07_DCH_CLNS, "DCH_CLNS", 0x00000000);
        //必ず Z ビットをセット（データキャッシュは全てクリーン）
        addCReg(CR07_DCH_TSTCLN, "DCH_TSTCLN", 0x40000000);
        addCReg(CR07_WB_PUR, "WB_PUR", 0x00000000);
        addCReg(CR07_UCH_CLNV, "UCH_CLNV", 0x00000000);
        addCReg(CR07_UCH_CLNS, "UCH_CLNS", 0x00000000);
        addCReg(CR07_ICH_PREV, "ICH_PREV", 0x00000000);
        addCReg(CR07_DCH_CLNINVV, "DCH_CLNINVV", 0x00000000);
        addCReg(CR07_DCH_CLNINVS, "DCH_CLNINVS", 0x00000000);
        //必ず Z ビットをセット（データキャッシュは全てクリーン）
        addCReg(CR07_DCH_TSTCLNINV, "DCH_TSTCLNINV", 0x40000000);
        addCReg(CR07_UCH_CLNINVV, "UCH_CLNINVV", 0x00000000);
        addCReg(CR07_UCH_CLNINVS, "UCH_CLNINVS", 0x00000000);

        //------------------------------------------------------------
        //crn08: メモリの保護と制御、MMU TLB 制御（書き込み専用なので初期値 0）
        //------------------------------------------------------------
        addCReg(CR08_UTLB_INVALL, "UTLB_INVALL", 0x00000000);
        addCReg(CR08_UTLB_INVV, "UTLB_INVV", 0x00000000);
        addCReg(CR08_ITLB_INVALL, "ITLB_INVALL", 0x00000000);
        addCReg(CR08_ITLB_INVV, "ITLB_INVV", 0x00000000);
        addCReg(CR08_DTLB_INVALL, "DTLB_INVALL", 0x00000000);
        addCReg(CR08_DTLB_INVV, "DTLB_INVV", 0x00000000);
    }

    @Override
    public void setCReg(int cn, int val) {
        switch (cn) {
        case CR00_MIDR:
        case CR00_CTR:
        case CR00_TCMTR:
        case CR00_TLBTR:
        case CR00_MPIDR:
            //read only, ignored
            break;
        case CR01_MMU_SCTLR:
            setSCTLR(val);
            break;
        case CR02_MMU_TTBR0:
            setTTBR0(val);
            break;
        case CR03_MMU_DACR:
            setDACR(val);
            break;
        case CR07_INTWAIT:
            waitInt(val);
            break;
        case CR07_UCH_INVALL:
            //System.out.printf("I&D-cache: all invalidated.\n");
            break;
        case CR07_UCH_INVV:
            System.out.printf("I&D-cache: invalidated 0x%08x.\n", val);
            break;
        case CR07_ICH_INVALL:
            //System.out.printf("I-cache  : all invalidated.\n");
            break;
        case CR07_ICH_INVV:
            //System.out.printf("I-cache  : invalidated 0x%08x.\n", val);
            break;
        case CR07_DCH_INVALL:
            //System.out.printf("D-cache  : all invalidated.\n");
            break;
        case CR07_DCH_INVV:
            //System.out.printf("D-cache  : invalidated 0x%08x.\n", val);
            break;
        case CR07_DCH_TSTCLN:
            System.out.printf("D-cache  : test & clean.\n");
            break;
        case CR07_DCH_TSTCLNINV:
            System.out.printf("D-cache  : test & clean & invalidated.\n");
            break;
        case CR07_WB_PUR:
            //System.out.printf("W-buffer : all purged.\n");
            break;
        case CR08_UTLB_INVALL:
            //System.out.printf("I&D-TLB  : all invalidated.\n");
            break;
        case CR08_UTLB_INVV:
            System.out.printf("i&D-TLB  : invalidated 0x%08x.\n", val);
            break;
        case CR08_ITLB_INVALL:
            //System.out.printf("I-TLB    : all invalidated.\n");
            break;
        case CR08_ITLB_INVV:
            //System.out.printf("I-TLB    : invalidated 0x%08x.\n", val);
            break;
        case CR08_DTLB_INVALL:
            //System.out.printf("D-TLB    : all invalidated.\n");
            break;
        case CR08_DTLB_INVV:
            //System.out.printf("D-TLB    : invalidated 0x%08x.\n", val);
            break;
        default:
            super.setCReg(cn, val);
            break;
        }
    }

    /**
     * crn01: 制御ビット
     *
     * @param val 新たなレジスタの値
     */
    public void setSCTLR(int val) {
        boolean v = BitOp.getBit32(val, 13);
        boolean r = BitOp.getBit32(val, 9);
        boolean s = BitOp.getBit32(val, 8);
        boolean a = BitOp.getBit32(val, 1);
        boolean m = BitOp.getBit32(val, 0);

        //System.out.printf("SCTLR    : 0x%x.\n", val);
        //System.out.printf("  V      : %b.\n", v);
        //System.out.printf("  R      : %b.\n", r);
        //System.out.printf("  S      : %b.\n", s);
        //System.out.printf("  A      : %b.\n", a);
        //System.out.printf("  M      : %b.\n", m);

        //v: ハイベクタ、0: 無効、1: 有効
        getCPU().setHighVector(v);
        //r: ROM 保護ビット
        getCPU().getMMU().setROMProtect(r);
        //s: システム保護ビット
        getCPU().getMMU().setSystemProtect(s);
        //a: アドレスアラインメントチェック、0: 無効、1: 有効
        getCPU().getMMU().setAlignmentCheck(a);
        //m: MMU イネーブルビット、0: 無効、1: 有効
        getCPU().getMMU().setEnable(m);

        super.setCReg(CR01_MMU_SCTLR, val);
    }

    /**
     * crn02: メモリの保護と制御、MMU 変換テーブル
     *
     * @param val 新たなレジスタの値
     */
    public void setTTBR0(int val) {
        //int base = (val >> 14) & 0x3ffff;

        //System.out.printf("TTBR0    : 0x%x.\n", val);
        //System.out.printf("  base   : 0x%x.\n", base);

        //MMU の状態を更新する
        getCPU().getMMU().setTableBase(val);

        super.setCReg(CR02_MMU_TTBR0, val);
    }

    /**
     * crn03: メモリの保護と制御、MMU ドメインアクセス制御
     *
     * @param val 新たなレジスタの値
     */
    public void setDACR(int val) {
        for (int i = 0; i < 16; i++) {
            getCPU().getMMU().setDomainAccess(i, (val >> (i * 2)) & 0x3);
        }

        super.setCReg(CR03_MMU_DACR, val);
    }

    /**
     * 割り込み待ち。
     *
     * @param val 新たなレジスタの値
     */
    public void waitInt(int val) {
        ARMv5 cpu = getCPU();

        synchronized (cpu) {
            while (!cpu.isRaisedInterrupt()) {
                try {
                    cpu.wait(100000);
                } catch (InterruptedException ex) {
                    //do nothing
                }
            }
        }
    }
}
