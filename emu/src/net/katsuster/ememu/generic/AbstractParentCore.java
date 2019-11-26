package net.katsuster.ememu.generic;

abstract public class AbstractParentCore implements ParentCore {
    private String name;
    private MasterCore64 master;
    private SlaveCore64 slave;

    public AbstractParentCore(String n) {
        name = n;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public MasterCore64 getMasterCore() {
        return master;
    }

    /**
     * バスマスターとなるコアを設定します。
     *
     * @param c バスマスターコア
     */
    public void setMasterCore(MasterCore64 c) {
        master = c;
    }

    @Override
    public SlaveCore64 getSlaveCore() {
        return slave;
    }

    /**
     * バススレーブとなるコアを設定します。
     *
     * @param c バススレーブコア
     */
    public void setSlaveCore(SlaveCore64 c) {
        slave = c;
    }
}
