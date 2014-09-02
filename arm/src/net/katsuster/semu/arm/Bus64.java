package net.katsuster.semu.arm;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 64 ビットアドレスバス。
 *
 * バスのマスターは 1つに限られます。
 *
 * @author katsuhiro
 */
public class Bus64 {
    private MasterCore64 master;
    private List<SlaveCoreAddress> slaveMap;
    private SlaveCoreAddress cachedSlave;

    public Bus64() {
        this.slaveMap = new LinkedList<SlaveCoreAddress>();
    }

    /**
     * バスのマスターコアを取得します。
     *
     * マスターコアはバスに読み取り、書き込みのリクエストができます。
     *
     * @return マスターコア、設定されていなければ null
     */
    public MasterCore64 getMasterCore() {
        return master;
    }

    /**
     * バスのマスターコアを設定します。
     *
     * マスターコアはバスに読み取り、書き込みのリクエストができます。
     *
     * @param core マスターコア
     */
    public void setMasterCore(MasterCore64 core) {
        master = core;
    }

    /**
     * 指定されたアドレスからの読み取りが可能かどうかを判定します。
     *
     * @param addr アドレス
     * @param len  読み取るデータのサイズ
     * @return 読み取りが可能な場合は true、不可能な場合は false
     */
    public boolean tryRead(long addr, int len) {
        SlaveCoreAddress sca;
        long offSt;

        sca = findSlaveCore(addr, addr + len - 1);
        if (sca == null) {
            //TODO: for debug, will be removed
            throw new IllegalArgumentException(String.format(
                    "Illegal address 0x%08x.", addr));
            //return false;
        }

        offSt = addr - sca.start;
        return sca.slave.tryRead(offSt);
    }

    /**
     * 指定されたアドレスから 8 ビットのデータを読み取ります。
     *
     * @param addr アドレス
     * @return データ
     */
    public byte read8(long addr) {
        SlaveCoreAddress sca;
        long offSt;

        sca = findSlaveCore(addr, addr);
        if (sca == null) {
            throw new IllegalArgumentException("Read from invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.start;
        return sca.slave.read8(offSt);
    }

    /**
     * 指定されたアドレスから 16 ビットのデータを読み取ります。
     *
     * @param addr アドレス
     * @return データ
     */
    public short read16(long addr) {
        SlaveCoreAddress sca;
        long offSt;

        sca = findSlaveCore(addr, addr + 1);
        if (sca == null) {
            throw new IllegalArgumentException("Read from invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.start;
        return sca.slave.read16(offSt);
    }

    /**
     * 指定されたアドレスから 32 ビットのデータを読み取ります。
     *
     * @param addr アドレス
     * @return データ
     */
    public int read32(long addr) {
        SlaveCoreAddress sca;
        long offSt;

        sca = findSlaveCore(addr, addr + 3);
        if (sca == null) {
            throw new IllegalArgumentException("Read from invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.start;
        return sca.slave.read32(offSt);
    }

    /**
     * 指定されたアドレスから 64 ビットのデータを読み取ります。
     *
     * @param addr アドレス
     * @return データ
     */
    public long read64(long addr) {
        SlaveCoreAddress sca;
        long offSt;

        sca = findSlaveCore(addr, addr + 7);
        if (sca == null) {
            throw new IllegalArgumentException("Read from invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.start;
        return sca.slave.read64(offSt);
    }

    /**
     * 指定されたアドレスへの書き込みが可能かどうかを判定します。
     *
     * @param addr アドレス
     * @param len  書き込むデータのサイズ
     * @return 書き込みが可能な場合は true、不可能な場合は false
     */
    public boolean tryWrite(long addr, int len) {
        SlaveCoreAddress sca;
        long offSt, offEd;

        sca = findSlaveCore(addr, addr + len - 1);
        if (sca == null) {
            //TODO: for debug, will be removed
            throw new IllegalArgumentException(String.format(
                    "Illegal address 0x%08x.", addr));
            //return false;
        }

        offSt = addr - sca.start;
        offEd = offSt + len - 1;
        return sca.slave.tryWrite(offSt);
    }

    /**
     * 指定したアドレスへ 8 ビットのデータを書き込みます。
     *
     * @param addr アドレス
     * @param data データ
     */
    public void write8(long addr, byte data) {
        SlaveCoreAddress sca;
        long offSt;

        sca = findSlaveCore(addr, addr);
        if (sca == null) {
            throw new IllegalArgumentException("Write to invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.start;
        sca.slave.write8(offSt, data);
    }

    /**
     * 指定したアドレスへ 16 ビットのデータを書き込みます。
     *
     * @param addr アドレス
     * @param data データ
     */
    public void write16(long addr, short data) {
        SlaveCoreAddress sca;
        long offSt;

        sca = findSlaveCore(addr, addr + 1);
        if (sca == null) {
            throw new IllegalArgumentException("Write to invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.start;
        sca.slave.write16(offSt, data);
    }

    /**
     * 指定したアドレスへ 32 ビットのデータを書き込みます。
     *
     * @param addr アドレス
     * @param data データ
     */
    public void write32(long addr, int data) {
        SlaveCoreAddress sca;
        long offSt;

        sca = findSlaveCore(addr, addr + 3);
        if (sca == null) {
            throw new IllegalArgumentException("Write to invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.start;
        sca.slave.write32(offSt, data);
    }

    /**
     * 指定したアドレスへ 64 ビットのデータを書き込みます。
     *
     * @param addr アドレス
     * @param data データ
     */
    public void write64(long addr, long data) {
        SlaveCoreAddress sca;
        long offSt;

        sca = findSlaveCore(addr, addr + 7);
        if (sca == null) {
            throw new IllegalArgumentException("Write to invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.start;
        sca.slave.write64(offSt, data);
    }

    /**
     * バスにスレーブコアを追加し、指定したアドレスに割り当てます。
     *
     * スレーブコアへの読み取り、書き込みを行うときに渡されるアドレスは、
     * 指定したアドレスからのオフセットとなります。
     *
     * 例えばバスのアドレス 0x1000 にスレーブコア A を割り当て、
     * バスのアドレス 0x1004 から読み取りを行うと、
     * スレーブコア A のアドレス 0x0004 の読み取りが行われます。
     *
     * @param c スレーブコア
     * @param start 開始アドレス
     * @param end   終了アドレス
     */
    public void addSlaveCore(SlaveCore64 c, long start, long end) {
        SlaveCoreAddress sca;

        sca = findSlaveCore(start, end);
        if (sca != null) {
            throw new IllegalArgumentException("Already exists on " +
                    String.format("0x%08x - 0x%08x.", sca.start, sca.end));
        }

        c.setMasterBus(this);
        slaveMap.add(new SlaveCoreAddress(c, start, end));
    }

    /**
     * バスの指定したアドレスに割り当てられているスレーブコアを検索します。
     *
     * @param start 開始アドレス
     * @param end   終了アドレス
     * @return 指定したアドレスに割り当てられているスレーブコア、
     * 何も割り当てられていなければ null
     */
    protected SlaveCoreAddress findSlaveCore(long start, long end) {
        Iterator<SlaveCoreAddress> it;
        SlaveCoreAddress s;

        if (cachedSlave != null && cachedSlave.contains(start, end)) {
            return cachedSlave;
        }

        for (it = slaveMap.iterator(); it.hasNext(); ) {
            s = it.next();

            if (s.contains(start, end)) {
                cachedSlave = s;
                return s;
            }
        }

        return null;
    }

    public class SlaveCoreAddress {
        public SlaveCore64 slave;
        public long start;
        public long end;

        public SlaveCoreAddress(SlaveCore64 slave, long st, long ed) {
            if (st > ed) {
                throw new IllegalArgumentException("Invalid address" +
                        String.format("st(0x%08x) > ed(0x%08x).", st, ed));
            }

            this.slave = slave;
            this.start = st;
            this.end = ed;
        }

        public long length() {
            return end - start + 1;
        }

        public boolean contains(long st, long ed) {
            if (st > ed) {
                throw new IllegalArgumentException("st(" + st + ") is " +
                        "larger than ed(" + ed + ").");
            }

            return start <= st && ed <= end;
        }
    }
}
