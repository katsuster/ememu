package net.katsuster.ememu.arm;

/**
 * 割り込みを要求しないコアです。
 *
 * CPU に割り込みを発生させるコアが接続されるまでは、
 * このコアが接続されます。
 *
 * @author katsuhiro
 */
public class NullINTSource implements INTSource {
    private INTC parentIntc = new INTC();

    public NullINTSource() {
        //do nothing
    }

    @Override
    public INTC getINTC() {
        return parentIntc;
    }

    @Override
    public void setINTC(INTC ctr) {
        parentIntc = ctr;
    }

    @Override
    public boolean isAssert() {
        return false;
    }

    @Override
    public String getIRQMessage() {
        return "";
    }
}
