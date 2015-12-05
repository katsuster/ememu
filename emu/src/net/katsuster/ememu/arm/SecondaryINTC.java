package net.katsuster.ememu.arm;

import net.katsuster.ememu.generic.*;

/**
 * 2nd 割り込みコントローラ
 *
 * <p>
 * 参考: Versatile Application Baseboard for ARM926EJ-S User Guide
 * ARM DUI0225D
 * </p>
 *
 * @author katsuhiro
 */
public class SecondaryINTC extends Controller32
        implements INTDestination {
    private NormalINTC intc;

    public static final int MAX_INTSRCS = 32;

    public static final int REG_SIC_STATUS     = 0x000;
    public static final int REG_SIC_RAWSTAT    = 0x004;
    public static final int REG_SIC_ENABLE     = 0x008;
    public static final int REG_SIC_ENSET      = 0x008;
    public static final int REG_SIC_ENCLR      = 0x00c;
    public static final int REG_SIC_SOFTINTSET = 0x010;
    public static final int REG_SIC_SOFTINTCLR = 0x014;
    public static final int REG_SIC_PICENABLE  = 0x020;
    public static final int REG_SIC_PICENSET   = 0x020;
    public static final int REG_SIC_PICENCLR   = 0x024;

    public SecondaryINTC() {
        intc = new NormalINTC(MAX_INTSRCS);
        intc.connectINTDestination(this);

        addReg(REG_SIC_ENCLR, "SIC_ENCLR", 0x00000000);
        addReg(REG_SIC_PICENSET, "SIC_PICENSET", 0x00000000);

        //FIXME: Workaround for Linux Versatile Device Tree.
        //  CONFIG_MACH_VERSATILE_DT
        addReg(0x02c, "", 0x00000000);
    }

    /**
     * 割り込みコントローラにコアを接続します。
     *
     * @param n 割り込み線の番号
     * @param c 割り込みを発生させるコア
     */
    public void connectINTSource(int n, INTSource c) {
        intc.connectINTSource(n, c);
    }

    /**
     * 割り込みコントローラからコアを切断します。
     *
     * 切断後はコアからの割り込みを受け付けません。
     *
     * @param n 割り込み線の番号
     */
    public void disconnectINTSource(int n) {
        intc.disconnectINTSource(n);
    }

    @Override
    public int readWord(long addr) {
        int regaddr;
        int result;

        regaddr = (int)(addr & getAddressMask(LEN_WORD_BITS));

        switch (regaddr) {
        case REG_SIC_ENCLR:
            //TODO: not implemented
            System.out.printf("SIC_ENCLR: read 0x%08x\n", 0);
            result = 0x0;
            break;
        case REG_SIC_PICENSET:
            //TODO: not implemented
            System.out.printf("SIC_PICENSET: read 0x%08x\n", 0);
            result = 0x0;
            break;
        default:
            result = super.readWord(regaddr);
            break;
        }

        return result;
    }

    @Override
    public void writeWord(long addr, int data) {
        int regaddr;

        regaddr = (int) (addr & getAddressMask(LEN_WORD_BITS));

        switch (regaddr) {
        case REG_SIC_ENCLR:
            //TODO: not implemented
            System.out.printf("SIC_ENCLR: 0x%08x\n", data);
            break;
        case REG_SIC_PICENSET:
            //TODO: not implemented
            System.out.printf("SIC_PICENSET: 0x%08x\n", data);
            break;
        default:
            super.writeWord(regaddr, data);
            break;
        }
    }

    @Override
    public boolean isRaisedInterrupt() {
        return false;
    }

    @Override
    public void setRaisedInterrupt(boolean m) {
        //FIXME: do nothing
        m = false;
    }

    @Override
    public void run() {
        //do nothing
    }
}
