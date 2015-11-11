package net.katsuster.ememu.arm.core;

/**
 * ARM コプロセッサ 14: デバッグコプロセッサ
 *
 * 参考: ARM アーキテクチャリファレンスマニュアル
 * ARM DDI0100HJ
 *
 * @author katsuhiro
 */
public class CoProcDebugv1 extends CoProc {
    //----------------------------------------------------------------------
    //crn00: デバッグレジスタ
    //----------------------------------------------------------------------
    public static final int OP_DIDR = 0x00000000;
    public static final int OP_DSCR = 0x00000010;
    public static final int OP_DTR = 0x00000050;
    public static final int OP_WFAR = 0x00000060;
    public static final int OP_VCR = 0x00000070;
    public static final int OP_BVR00 = 0x0000004;
    public static final int OP_BVR01 = 0x0000014;
    public static final int OP_BCR00 = 0x0000005;
    public static final int OP_BCR01 = 0x0000015;
    public static final int OP_WVR00 = 0x0000006;
    public static final int OP_WVR01 = 0x0000016;
    public static final int OP_WCR00 = 0x0000007;
    public static final int OP_WCR01 = 0x0000017;

    //----------------------------------------------------------------------
    //crn01: デバッグレジスタ (ARMv7 のみ)
    //----------------------------------------------------------------------
    public static final int OP_DRAR = 0x00001000;

    //----------------------------------------------------------------------
    //crn02: デバッグレジスタ (ARMv7 のみ)
    //----------------------------------------------------------------------
    public static final int OP_DSAR = 0x00002000;

    public CoProcDebugv1(int no, ARMv5 p) {
        super(no, p);

        //[31:28]: WRP 数           : 0x1 (2個)
        //[27:24]: BRP 数           : 0x1 (2個)
        //[23:20]: BRP コンテキスト ID 比較 : 0x1 (2個)
        //[19:16]: バージョン       : 0x1 (ARMv6)
        //[ 7: 4]: バリエーション   : 0x0
        //[ 3: 0]: リビジョン       : 0x0
        addCReg(OP_DIDR, "DIDR",   0x11110000);

        //[31:12]: ADDR   : 0x00000
        //[ 1: 0]: 有効   : 0x0 (無効)
        addCReg(OP_DRAR, "DRAR",   0x00000000);

        //[31:12]: ADDR   : 0x00000
        //[ 1: 0]: 有効   : 0x0 (無効)
        addCReg(OP_DSAR, "DSAR",   0x00000000);
    }

    @Override
    public void setCReg(int cn, int val) {
        switch (cn) {
        case OP_DIDR:
            //read only, ignored
            break;
        case OP_DRAR:
            //read only, ignored
            break;
        case OP_DSAR:
            //read only, ignored
            break;
        default:
            super.setCReg(cn, val);
            break;
        }
    }
}
