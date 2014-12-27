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
    public NullINTSource() {
        //do nothing
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
