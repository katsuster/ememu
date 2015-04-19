package net.katsuster.ememu.arm.core;

/**
 * ARM コプロセッサ 10: ベクタ浮動小数点プロセッサ（VFP）
 *
 * 参考: ARM アーキテクチャリファレンスマニュアル Second Edition
 * ARM DDI0100DJ
 *
 * @author katsuhiro
 */
public class CoProcVFPv2 extends CoProc {
    //----------------------------------------------------------------------
    //opcode07: システムレジスタ転送（FMXR, FMRX）
    //----------------------------------------------------------------------
    public static final int OP_FMR_FPSID = 0x00000700;
    public static final int OP_FMR_FPSCR = 0x00001700;
    public static final int OP_FMR_FPEXC = 0x00008700;

    public CoProcVFPv2(int no, ARMv5 p) {
        super(no, p);

        //[31:24]: 実装者          : 0x41 (ARM Ltd.)
        //[23   ]: SW              : 1 (Software)
        //[22:21]: 形式            : 0x0 (FSTMX, FLDMX 標準形式 1)
        //[20   ]: SNG             : 1 (double and single)
        //[19:16]: アーキテクチャ  : 1 (VFPv2)
        //[15: 8]: 部品番号        : 0
        //[ 7: 4]: バリアント      : 0
        //[ 3: 0]: Revision        : 0
        addCReg(OP_FMR_FPSID, "FMR_FPSID",   0x41910000);
        addCReg(OP_FMR_FPSCR, "FMR_FPSCR",   0x00000000);
        addCReg(OP_FMR_FPEXC, "FMR_FPEXC",   0x00000000);
    }

    @Override
    public void setCReg(int cn, int val) {
        switch (cn) {
        case OP_FMR_FPSID:
            //read only, ignored
            break;
        case OP_FMR_FPSCR:
            //TODO: not implemented
            System.out.printf("FMR_FPSCR: 0x%08x\n", val);
            break;
        case OP_FMR_FPEXC:
            //TODO: not implemented
            System.out.printf("FMR_FPEXC: 0x%08x\n", val);
            break;
        default:
            super.setCReg(cn, val);
            break;
        }
    }
}
