package net.katsuster.semu;

/**
 * コプロセッサ 15: 標準コプロセッサ。
 *
 * @author katsuhiro
 */
public class StdCoProc extends CoProc {
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

    //----------------------------------------------------------------------
    //crn06: メモリの保護と制御、MMU フォルトアドレス
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    //crn07: キャッシュとライトバッファ
    //  レジスタ名は規定無しのため、独自
    //----------------------------------------------------------------------
    //割り込み待ち
    //public static final int CR07_ = 0x00007004;
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

    //----------------------------------------------------------------------
    //crn14: 予約済み
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    //crn15: 実装ごとに定義
    //----------------------------------------------------------------------

    public StdCoProc(int no, CPU p) {
        super(no, p);

        //------------------------------------------------------------
        //crn00: ID コードレジスタ（読み取り専用）
        //------------------------------------------------------------
        //  implementer: 0x41(ARM), variant: 0x0(nothing),
        //  arch: 0x6(ARMv5TEJ), part: 0x926(ARM926), revision: 0x0
        addCReg(CR00_MIDR, "MIDR",   0x41069260);
        addCReg(CR00_CTR, "CTR",     0x00000000);
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
        //crn07: キャッシュとライトバッファ（書き込み専用なので初期値 0）
        //------------------------------------------------------------
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
        addCReg(CR07_WB_PUR, "WB_PUR", 0x00000000);
        addCReg(CR07_UCH_CLNV, "UCH_CLNV", 0x00000000);
        addCReg(CR07_UCH_CLNS, "UCH_CLNS", 0x00000000);
        addCReg(CR07_ICH_PREV, "ICH_PREV", 0x00000000);
        addCReg(CR07_DCH_CLNINVV, "DCH_CLNINVV", 0x00000000);
        addCReg(CR07_DCH_CLNINVS, "DCH_CLNINVS", 0x00000000);
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
        case CR01_MMU_SCTLR:
            System.out.printf("SCTLR    : 0x%x.\n", val);
            break;
        case CR02_MMU_TTBR0:
            System.out.printf("TTBR0    : 0x%x.\n", val);
            break;
        case CR03_MMU_DACR:
            System.out.printf("DACR     : 0x%x.\n", val);
            break;
        case CR07_UCH_INVALL:
            System.out.printf("I&D-cache: all invalidated.\n");
            break;
        case CR07_ICH_INVALL:
            System.out.println("I-cache  : all invalidated.\n");
            break;
        case CR07_DCH_INVALL:
            System.out.printf("D-cache  : all invalidated.\n");
            break;
        case CR07_WB_PUR:
            System.out.printf("W-buffer : all purged.\n");
            break;
        case CR08_UTLB_INVALL:
            System.out.printf("I&D-TLB  : all invalidated.\n");
            break;
        case CR08_ITLB_INVALL:
            System.out.printf("I-TLB    : all invalidated.\n");
            break;
        case CR08_DTLB_INVALL:
            System.out.printf("D-TLB    : all invalidated.\n");
            break;
        default:
            super.setCReg(cn, val);
            break;
        }
    }
}
