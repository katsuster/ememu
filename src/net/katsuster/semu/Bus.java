package net.katsuster.semu;

import java.util.*;

/**
 * バス。
 *
 * @author katsuhiro
 */
public class Bus<T extends ByteSeq> {
    private MasterCore<T> master;
    private List<SlaveCoreAddress<T>> slaveMap;

    public Bus() {
        this.slaveMap = new LinkedList<SlaveCoreAddress<T>>();
    }

    public MasterCore getMasterCore() {
        return master;
    }

    public void setMasterCore(MasterCore<T> core) {
        master = core;
    }

    public T read(long addr, int len) {
        SlaveCoreAddress<T> sca;
        long offSt, offEd;

        sca = findSlaveCore(addr, addr + len - 1);
        if (sca == null) {
            throw new IllegalArgumentException("Read from invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.start;
        offEd = offSt + len - 1;
        return sca.slave.read(offSt);
    }

    public void write(long addr, T data) {
        SlaveCoreAddress<T> sca;
        long offSt, offEd;

        sca = findSlaveCore(addr, addr + data.length() - 1);
        if (sca == null) {
            throw new IllegalArgumentException("Write to invalid address" +
                    String.format("(0x%08x).", addr));
        }

        offSt = addr - sca.start;
        offEd = offSt + data.length() - 1;
        sca.slave.write(offSt, data);
    }

    public void addSlaveCore(SlaveCore<T> c, long start, long end) {
        SlaveCoreAddress sca;

        sca = findSlaveCore(start, end);
        if (sca != null) {
            throw new IllegalArgumentException("Already exists on " +
                    String.format("0x%08x - 0x%08x.", sca.start, sca.end));
        }

        c.setMasterBus(this);
        slaveMap.add(new SlaveCoreAddress<T>(c, start, end));
    }

    protected SlaveCoreAddress<T> findSlaveCore(long start, long end) {
        Iterator<SlaveCoreAddress<T>> it;
        SlaveCoreAddress<T> s;

        for (it = slaveMap.iterator(); it.hasNext(); ) {
            s = it.next();

            if (s.contains(start, end)) {
                return s;
            }
        }

        return null;
    }

    public class SlaveCoreAddress<U extends ByteSeq> {
        public SlaveCore<U> slave;
        public long start;
        public long end;

        public SlaveCoreAddress(SlaveCore<U> slave, long st, long ed) {
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
