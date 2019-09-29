package net.katsuster.ememu.generic;

import java.util.*;
import java.util.concurrent.locks.*;

/**
 * 64 ビットアドレスバス。
 */
public class Bus64 implements BusSlave64 {
    //ロック
    private ReentrantReadWriteLock rwlock;
    //全マスターコアを管理するリスト
    private List<MasterCore64> masterList;
    //全スレーブコアを管理するリスト
    private List<SlaveCoreAddress> slaveList;
    //32bit アドレス内のスレーブコアに高速にアクセスするためのテーブル
    private SlaveCoreAddress[] slaves;
    //直前にアクセスしたスレーブコアのキャッシュ
    private SlaveCoreAddress cachedSlave;
    private long cacheHit;
    private long cacheMiss;

    public Bus64() {
        rwlock = new ReentrantReadWriteLock();
        masterList = new ArrayList<MasterCore64>();
        slaveList = new ArrayList<SlaveCoreAddress>();
        //4KB ごとにスレーブコアを記録するため、
        //2^32 / 2^12 = 2^20 の要素が必要となる
        slaves = new SlaveCoreAddress[1024 * 1024];
        cachedSlave = new InvalidSlaveCoreAddress();
    }

    /**
     * バスのマスターコアを追加します。
     *
     * マスターコアはバスに読み取り、書き込みのリクエストができます。
     *
     * @param c 追加するマスターコア
     */
    public void addMasterCore(MasterCore64 c) {
        masterList.add(c);
        c.setSlaveBus(this);
    }

    /**
     * バスのマスターコアを削除します。
     *
     * マスターコアはバスに読み取り、書き込みのリクエストができます。
     *
     * @param core 削除するマスターコア
     */
    public void removeMasterCore(MasterCore64 core) {
        masterList.remove(core);
    }

    /**
     * バスに接続されている全てのマスターコアを起動します。
     */
    public void startAllMasterCores() {
        for (MasterCore64 mc : masterList) {
            mc.setName(mc.getClass().getName());
            mc.start();
        }
    }

    /**
     * バスに接続されている全てのマスターコアに対し、
     * コアの停止を要求します。
     */
    public void haltAllMasterCores() {
        for (MasterCore64 mc : masterList) {
            mc.halt();
        }
    }

    /**
     * バスの読み込みロックを取得します。
     *
     * @return 読み込みロック
     */
    public Lock getReadLock() {
        return rwlock.readLock();
    }

    /**
     * バスの書き込みロックを取得します。
     *
     * @return 書き込みロック
     */
    public Lock getWriteLock() {
        return rwlock.writeLock();
    }

    @Override
    public boolean tryRead(BusMaster64 m, long addr, int len) {
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
        return sca.getCore().tryRead(m, offSt, len);
    }

    @Override
    public byte read8(BusMaster64 m, long addr) {
        SlaveCoreAddress sca;
        long offSt;

        sca = findSlaveCoreAddress(addr, addr);
        if (sca == null) {
            throw new IllegalArgumentException("Read from invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.getStartAddress();

        rwlock.readLock().lock();
        try {
            return sca.getCore().read8(m, offSt);
        } finally {
            rwlock.readLock().unlock();
        }
    }

    @Override
    public short read16(BusMaster64 m, long addr) {
        SlaveCoreAddress sca;
        long offSt;

        sca = findSlaveCoreAddress(addr, addr + 1);
        if (sca == null) {
            throw new IllegalArgumentException("Read from invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.getStartAddress();

        rwlock.readLock().lock();
        try {
            return sca.getCore().read16(m, offSt);
        } finally {
            rwlock.readLock().unlock();
        }
    }

    @Override
    public int read32(BusMaster64 m, long addr) {
        SlaveCoreAddress sca;
        long offSt;

        sca = findSlaveCoreAddress(addr, addr + 3);
        if (sca == null) {
            throw new IllegalArgumentException("Read from invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.getStartAddress();

        rwlock.readLock().lock();
        try {
            return sca.getCore().read32(m, offSt);
        } finally {
            rwlock.readLock().unlock();
        }
    }

    @Override
    public long read64(BusMaster64 m, long addr) {
        SlaveCoreAddress sca;
        long offSt;

        sca = findSlaveCoreAddress(addr, addr + 7);
        if (sca == null) {
            throw new IllegalArgumentException("Read from invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.getStartAddress();

        rwlock.readLock().lock();
        try {
            return sca.getCore().read64(m, offSt);
        } finally {
            rwlock.readLock().unlock();
        }
    }

    @Override
    public short read_ua16(BusMaster64 m, long addr) {
        SlaveCoreAddress sca;
        long offSt;

        sca = findSlaveCoreAddress(addr, addr + 1);
        if (sca == null) {
            throw new IllegalArgumentException("Read from invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.getStartAddress();

        rwlock.readLock().lock();
        try {
            return sca.getCore().read_ua16(m, offSt);
        } finally {
            rwlock.readLock().unlock();
        }
    }

    @Override
    public int read_ua32(BusMaster64 m, long addr) {
        SlaveCoreAddress sca;
        long offSt;

        sca = findSlaveCoreAddress(addr, addr + 3);
        if (sca == null) {
            throw new IllegalArgumentException("Read from invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.getStartAddress();

        rwlock.readLock().lock();
        try {
            return sca.getCore().read_ua32(m, offSt);
        } finally {
            rwlock.readLock().unlock();
        }
    }

    @Override
    public long read_ua64(BusMaster64 m, long addr) {
        SlaveCoreAddress sca;
        long offSt;

        sca = findSlaveCoreAddress(addr, addr + 7);
        if (sca == null) {
            throw new IllegalArgumentException("Read from invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.getStartAddress();

        rwlock.readLock().lock();
        try {
            return sca.getCore().read_ua64(m, offSt);
        } finally {
            rwlock.readLock().unlock();
        }
    }

    @Override
    public boolean tryWrite(BusMaster64 m, long addr, int len) {
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
        return sca.getCore().tryWrite(m, offSt, len);
    }

    @Override
    public void write8(BusMaster64 m, long addr, byte data) {
        SlaveCoreAddress sca;
        long offSt;

        sca = findSlaveCoreAddress(addr, addr);
        if (sca == null) {
            throw new IllegalArgumentException("Write to invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.getStartAddress();

        rwlock.writeLock().lock();
        try {
            sca.getCore().write8(m, offSt, data);
        } finally {
            rwlock.writeLock().unlock();
        }
    }

    @Override
    public void write16(BusMaster64 m, long addr, short data) {
        SlaveCoreAddress sca;
        long offSt;

        sca = findSlaveCoreAddress(addr, addr + 1);
        if (sca == null) {
            throw new IllegalArgumentException("Write to invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.getStartAddress();

        rwlock.writeLock().lock();
        try {
            sca.getCore().write16(m, offSt, data);
        } finally {
            rwlock.writeLock().unlock();
        }
    }

    @Override
    public void write32(BusMaster64 m, long addr, int data) {
        SlaveCoreAddress sca;
        long offSt;

        sca = findSlaveCoreAddress(addr, addr + 3);
        if (sca == null) {
            throw new IllegalArgumentException("Write to invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.getStartAddress();

        rwlock.writeLock().lock();
        try {
            sca.getCore().write32(m, offSt, data);
        } finally {
            rwlock.writeLock().unlock();
        }
    }

    @Override
    public void write64(BusMaster64 m, long addr, long data) {
        SlaveCoreAddress sca;
        long offSt;

        sca = findSlaveCoreAddress(addr, addr + 7);
        if (sca == null) {
            throw new IllegalArgumentException("Write to invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.getStartAddress();

        rwlock.writeLock().lock();
        try {
            sca.getCore().write64(m, offSt, data);
        } finally {
            rwlock.writeLock().unlock();
        }
    }

    @Override
    public void write_ua16(BusMaster64 m, long addr, short data) {
        SlaveCoreAddress sca;
        long offSt;

        sca = findSlaveCoreAddress(addr, addr + 1);
        if (sca == null) {
            throw new IllegalArgumentException("Write to invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.getStartAddress();

        rwlock.writeLock().lock();
        try {
            sca.getCore().write_ua16(m, offSt, data);
        } finally {
            rwlock.writeLock().unlock();
        }
    }

    @Override
    public void write_ua32(BusMaster64 m, long addr, int data) {
        SlaveCoreAddress sca;
        long offSt;

        sca = findSlaveCoreAddress(addr, addr + 3);
        if (sca == null) {
            throw new IllegalArgumentException("Write to invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.getStartAddress();

        rwlock.writeLock().lock();
        try {
            sca.getCore().write_ua32(m, offSt, data);
        } finally {
            rwlock.writeLock().unlock();
        }
    }

    @Override
    public void write_ua64(BusMaster64 m, long addr, long data) {
        SlaveCoreAddress sca;
        long offSt;

        sca = findSlaveCoreAddress(addr, addr + 7);
        if (sca == null) {
            throw new IllegalArgumentException("Write to invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.getStartAddress();

        rwlock.writeLock().lock();
        try {
            sca.getCore().write_ua64(m, offSt, data);
        } finally {
            rwlock.writeLock().unlock();
        }
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
        sca = new SlaveCoreAddress(c, start, end);
        for (long i = start; i <= end; i += 4096) {
            int ind = (int) (i >>> 12);
            if (ind > 0xfffff) {
                //32bit アドレスの範囲外
                break;
            }
            slaves[ind] = sca;
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
     * 2つ以上の領域に同一のスレーブコアが登録されている場合、
     * 全て削除します。
     *
     * @param c スレーブコア
     * @return バスから指定したスレーブコアを削除できた場合は true、
     * そうでなければ false
     */
    public boolean removeSlaveCore(SlaveCore64 c) {
        boolean result = false;

        //32bit アドレス範囲内のスレーブコアならばテーブルから消去する
        for (long i = 0; i <= 0xffffffffL; i += 4096) {
            int ind = (int) (i >>> 12);

            if (slaves[ind] == null || slaves[ind].getCore() == null) {
                continue;
            }

            if (slaves[ind].getCore().equals(c)) {
                slaves[ind] = null;
            }
        }

        //リストからスレーブコアを消去する
        out: while (true) {
            boolean found = false;

            for (SlaveCoreAddress sca : slaveList) {
                SlaveCore64 sc = sca.getCore();

                if (sc.equals(c)) {
                    slaveList.remove(sca);
                    sc.setMasterBus(null);
                    found = true;
                    result = true;

                    break;
                }
            }
            if (!found) {
                break out;
            }
        }

        return result;
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
    synchronized protected SlaveCoreAddress findSlaveCoreAddress(long start, long end) {
        if (cachedSlave.contains(start, end)) {
            cacheHit++;
            return cachedSlave;
        }
        cacheMiss++;

        //テーブルから探索する
        int ind = (int) (start >>> 12);
        if (ind <= 0xfffff) {
            if (slaves[ind] != null) {
                cachedSlave = slaves[ind];
            }
            if (cachedSlave.contains(start, end)) {
                return slaves[ind];
            }
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

    /**
     * 無効なスレーブコアが占めるアドレスを表すクラスです。
     */
    private class InvalidSlaveCoreAddress extends SlaveCoreAddress {
        /**
         * 無効なアドレスを生成します。
         */
        public InvalidSlaveCoreAddress() {
            super(null, 0, 0);
        }

        /**
         * スレーブコアを返します。
         * 常に IllegalStateException 例外をスローします。
         *
         * @return 値を返しません
         */
        @Override
        public SlaveCore64 getCore() {
            throw new IllegalStateException("Invalid slave core.");
        }

        /**
         * このスレーブコアが指定したアドレスの範囲を含むかどうかを判定します。
         * 常に false を返します。
         *
         * @param st 開始アドレス
         * @param ed 終了アドレス
         * @return 常に false
         */
        @Override
        public boolean contains(long st, long ed) {
            return false;
        }
    }
}
