package net.katsuster.semu;

/**
 * コプロセッサ 15: 標準コプロセッサ。
 *
 * @author katsuhiro
 */
public class StdCoProc extends CoProc {
    public static final int CREG_MIDR  = 0x00000000;
    public static final int CREG_CTR   = 0x00000001;
    public static final int CREG_TCMTR = 0x00000002;
    public static final int CREG_TLBTR = 0x00000003;
    public static final int CREG_MPIDR = 0x00000005;

    public StdCoProc(int no, CPU p) {
        super(no, p);

        //ID コードレジスタ
        addCReg(CREG_MIDR, "MIDR",   0x00000000);
        addCReg(CREG_CTR, "CTR",     0x00000000);
        addCReg(CREG_TCMTR, "TCMTR", 0x00000000);
        addCReg(CREG_TLBTR, "TLBTR", 0x00000000);
        addCReg(CREG_MPIDR, "MPIDR", 0x00000000);
    }
}
