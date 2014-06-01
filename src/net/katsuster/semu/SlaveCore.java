package net.katsuster.semu;

/**
 * バスのスレーブコア。
 *
 * @author katsuhiro
 */
public interface SlaveCore<T extends ByteSeq> {
    public Bus<T> getMasterBus();
    public void setMasterBus(Bus<T> bus);

    public T read(long addr);
    public void write(long addr, T data);
}
