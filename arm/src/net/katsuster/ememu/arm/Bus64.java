package net.katsuster.ememu.arm;

import java.util.*;

/**
 * 64 ビットアドレスバス。
 *
 * バスのマスターは 1つに限られます。
 *
 * @author katsuhiro
 */
public class Bus64 {
    private MasterCore64 master;
    //32bit アドレス内のスレーブコアに高速にアクセスするためのテーブル
    private SlaveCoreAddress[] slaves;
    //全スレーブコアを管理するリスト
    private List<SlaveCoreAddress> slaveList;
    //直前にアクセスしたスレーブコアのキャッシュ
    private SlaveCoreAddress cachedSlave;

    public Bus64() {
        //4KB ごとにスレーブコアを記録するため、
        //2^32 / 2^12 = 2^20 の要素が必要となる
        this.slaves = new SlaveCoreAddress[1024 * 1024];
        this.slaveList = new ArrayList<SlaveCoreAddress>();
        this.cachedSlave = new SlaveCoreAddress(null, 0, 0);
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
     * バスに接続されている全てのマスターコアを起動します。
     */
    public void startAllMasterCores() {
        master.setName(master.getClass().getName());
        master.start();
    }

    /**
     * バスに接続されている全てのマスターコアに対し、
     * コアの停止を要求します。
     */
    public void haltAllMasterCores() {
        master.halt();
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

        sca = findSlaveCoreAddress(addr, addr + len - 1);
        if (sca == null) {
            //TODO: for debug, will be removed
            throw new IllegalArgumentException(String.format(
                    "Illegal address 0x%08x.", addr));
            //return false;
        }

        offSt = addr - sca.getStartAddress();
        //offEd = offSt + len - 1;
        return sca.getCore().tryRead(offSt, len);
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

        sca = findSlaveCoreAddress(addr, addr);
        if (sca == null) {
            throw new IllegalArgumentException("Read from invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.getStartAddress();
        return sca.getCore().read8(offSt);
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

        sca = findSlaveCoreAddress(addr, addr + 1);
        if (sca == null) {
            throw new IllegalArgumentException("Read from invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.getStartAddress();
        return sca.getCore().read16(offSt);
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

        sca = findSlaveCoreAddress(addr, addr + 3);
        if (sca == null) {
            throw new IllegalArgumentException("Read from invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.getStartAddress();
        return sca.getCore().read32(offSt);
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

        sca = findSlaveCoreAddress(addr, addr + 7);
        if (sca == null) {
            throw new IllegalArgumentException("Read from invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.getStartAddress();
        return sca.getCore().read64(offSt);
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
        long offSt;

        sca = findSlaveCoreAddress(addr, addr + len - 1);
        if (sca == null) {
            //TODO: for debug, will be removed
            throw new IllegalArgumentException(String.format(
                    "Illegal address 0x%08x.", addr));
            //return false;
        }

        offSt = addr - sca.getStartAddress();
        //offEd = offSt + len - 1;
        return sca.getCore().tryWrite(offSt, len);
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

        sca = findSlaveCoreAddress(addr, addr);
        if (sca == null) {
            throw new IllegalArgumentException("Write to invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.getStartAddress();
        sca.getCore().write8(offSt, data);
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

        sca = findSlaveCoreAddress(addr, addr + 1);
        if (sca == null) {
            throw new IllegalArgumentException("Write to invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.getStartAddress();
        sca.getCore().write16(offSt, data);
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

        sca = findSlaveCoreAddress(addr, addr + 3);
        if (sca == null) {
            throw new IllegalArgumentException("Write to invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.getStartAddress();
        sca.getCore().write32(offSt, data);
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

        sca = findSlaveCoreAddress(addr, addr + 7);
        if (sca == null) {
            throw new IllegalArgumentException("Write to invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.getStartAddress();
        sca.getCore().write64(offSt, data);
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

        sca = findSlaveCoreAddress(start, end);
        if (sca != null) {
            throw new IllegalArgumentException("Already exists on " +
                    String.format("0x%08x - 0x%08x.",
                            sca.getStartAddress(), sca.getEndAddress()));
        }

        //32bit アドレス範囲内のスレーブコアならばテーブルにも記録する
        for (long i = start; i <= end; i += 4096) {
            int ind = (int) (i >>> 12);
            if (ind > 0xfffff) {
                //32bit アドレスの範囲外
                break;
            }
            slaves[ind] = new SlaveCoreAddress(c, start, end);
        }

        //リストにスレーブコアを記録する
        slaveList.add(new SlaveCoreAddress(c, start, end));
        c.setMasterBus(this);
    }

    /**
     * バスの指定したアドレスに割り当てられているスレーブコアを取得します。
     *
     * @param start 開始アドレス
     * @param end   終了アドレス
     * @return 指定したアドレスに割り当てられているスレーブコア、
     * 何も割り当てられていなければ null
     */
    public SlaveCore64 getSlaveCore(long start, long end) {
        SlaveCoreAddress sca;

        sca = findSlaveCoreAddress(start, end);
        if (sca != null) {
            return sca.getCore();
        } else {
            return null;
        }
    }

    /**
     * バスから指定したスレーブコアを削除します。
     *
     * @param c スレーブコア
     * @return バスから指定したスレーブコアを削除できた場合は true、
     * そうでなければ false
     */
    public boolean removeSlaveCore(SlaveCore64 c) {
        //32bit アドレス範囲内のスレーブコアならばテーブルから消去する
        for (long i = 0; i <= 0xffffffff; i += 4096) {
            int ind = (int) (i >>> 12);

            if (slaves[ind].getCore().equals(c)) {
                slaves[ind] = null;
            }
        }

        //リストからスレーブコアを消去する
        for (SlaveCoreAddress sca : slaveList) {
            SlaveCore64 sc = sca.getCore();

            if (sc.equals(c)) {
                slaveList.remove(sc);
                sc.setMasterBus(null);
                return true;
            }
        }

        return false;
    }

    /**
     * バスの指定したアドレスに割り当てられている、
     * スレーブコアアドレスを検索します。
     *
     * @param start 開始アドレス
     * @param end   終了アドレス
     * @return 指定したアドレスに割り当てられているスレーブコアアドレス、
     * 何も割り当てられていなければ null
     */
    protected SlaveCoreAddress findSlaveCoreAddress(long start, long end) {
        if (cachedSlave.contains(start, end)) {
            return cachedSlave;
        }

        //テーブルから探索する
        int ind = (int) (start >>> 12);
        if (ind <= 0xfffff) {
            return slaves[ind];
        }

        //リストから線形探索する
        for (SlaveCoreAddress sca : slaveList) {
            if (sca.contains(start, end)) {
                cachedSlave = sca;
                return sca;
            }
        }

        return null;
    }

    /**
     * バスに接続されている全てのスレーブコアを起動します。
     */
    public void startAllSlaveCores() {
        for (SlaveCoreAddress sca : slaveList) {
            SlaveCore64 sc = sca.getCore();

            sc.setName(sc.getClass().getName());
            sc.start();
        }
    }

    /**
     * バスに接続されている全てのスレーブコアに対し、
     * コアの停止を要求します。
     */
    public void haltAllSlaveCores() {
        for (SlaveCoreAddress sca : slaveList) {
            sca.getCore().halt();
        }
    }

    /**
     * スレーブコアとスレーブコアが占めるアドレスを表すクラスです。
     */
    private class SlaveCoreAddress {
        private SlaveCore64 slave;
        private long start;
        private long end;

        /**
         * 指定したアドレスの範囲にスレーブコアを割り当てます。
         *
         * @param slave スレーブコア
         * @param st    開始アドレス
         * @param ed    終了アドレス
         */
        public SlaveCoreAddress(SlaveCore64 slave, long st, long ed) {
            if (st > ed) {
                throw new IllegalArgumentException("Invalid address" +
                        String.format("st(0x%08x) > ed(0x%08x).", st, ed));
            }

            this.slave = slave;
            this.start = st;
            this.end = ed;
        }

        /**
         * スレーブコアを取得します。
         *
         * @return スレーブコア
         */
        public SlaveCore64 getCore() {
            return slave;
        }

        /**
         * スレーブコアの開始アドレスを取得します。
         *
         * @return 開始アドレス
         */
        public long getStartAddress() {
            return start;
        }

        /**
         * スレーブコアの終了アドレスを取得します。
         *
         * @return 終了アドレス
         */
        public long getEndAddress() {
            return end;
        }

        /**
         * このスレーブコアが占めるアドレスの長さを取得します。
         *
         * @return コアが占めるアドレスの長さ
         */
        public long length() {
            return end - start + 1;
        }

        /**
         * このスレーブコアが指定したアドレスの範囲を含むかどうかを判定します。
         *
         * @param st 開始アドレス
         * @param ed 終了アドレス
         * @return 指定したアドレスを含むなら true、含まないなら false
         */
        public boolean contains(long st, long ed) {
            if (st > ed) {
                throw new IllegalArgumentException("st(" + st + ") is " +
                        "larger than ed(" + ed + ").");
            }

            return start <= st && ed <= end;
        }
    }
}
