package net.katsuster.ememu.generic;

/**
 * 何も割り込みを要求しないコア。
 *
 * <p>
 * CPU に割り込みを発生させるコアが接続されるまでは、
 * このコアが接続されます。
 * </p>
 *
 * @author katsuhiro
 */
public class NullINTSource implements INTSource {
    private INTDestination intDst = new NullINTDestination();

    public NullINTSource() {
        //do nothing
    }

    @Override
    public INTDestination getINTDestination() {
        return intDst;
    }

    @Override
    public void connectINTDestination(INTDestination c) {
        intDst = c;
    }

    @Override
    public void disconnectINTDestination() {
        intDst = new NullINTDestination();
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
