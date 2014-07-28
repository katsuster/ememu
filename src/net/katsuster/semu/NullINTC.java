package net.katsuster.semu;

/**
 * 割り込みを要求しない割り込みコントローラ
 *
 * CPU に割り込みコントローラが接続されるまでは、
 * このコントローラが接続されます。
 *
 * @author katsuhiro
 */
public class NullINTC implements INTC {
    public NullINTC() {
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
