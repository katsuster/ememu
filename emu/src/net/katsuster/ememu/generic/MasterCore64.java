package net.katsuster.ememu.generic;

import java.util.concurrent.locks.*;

/**
 * 64 ビットアドレスバスのマスターコア。
 *
 * <p>
 * 自身のタイミングで動作します。
 * スレーブバスを持ち、バスに read/write 要求を出します。
 * </p>
 *
 * <p>
 * バスへのアクセス時に用いるアドレスは 64 ビット幅です。
 * </p>
 */
public abstract class MasterCore64 extends AbstractCore
        implements BusMaster64 {
    private Bus64 slaveBus;

    @Override
    public Bus64 getSlaveBus() {
        return slaveBus;
    }

    @Override
    public void setSlaveBus(Bus64 bus) {
        slaveBus = bus;
    }

    @Override
    public Lock getReadLock() {
        return slaveBus.getReadLock();
    }

    @Override
    public Lock getWriteLock() {
        return slaveBus.getWriteLock();
    }

    @Override
    public boolean tryRead(long addr, int len) {
        return slaveBus.tryRead(this, addr, len);
    }

    @Override
    public byte read8(long addr) {
        return slaveBus.read8(this, addr);
    }

    @Override
    public short read16(long addr) {
        return slaveBus.read16(this, addr);
    }

    @Override
    public int read32(long addr) {
        return slaveBus.read32(this, addr);
    }

    @Override
    public long read64(long addr) {
        return slaveBus.read64(this, addr);
    }

    @Override
    public short read_ua16(long addr) {
        return slaveBus.read_ua16(this, addr);
    }

    @Override
    public int read_ua32(long addr) {
        return slaveBus.read_ua32(this, addr);
    }

    @Override
    public long read_ua64(long addr) {
        return slaveBus.read_ua64(this, addr);
    }

    @Override
    public boolean tryWrite(long addr, int len) {
        return slaveBus.tryWrite(this, addr, len);
    }

    @Override
    public void write8(long addr, byte data) {
        slaveBus.write8(this, addr, data);
    }

    @Override
    public void write16(long addr, short data) {
        slaveBus.write16(this, addr, data);
    }

    @Override
    public void write32(long addr, int data) {
        slaveBus.write32(this, addr, data);
    }

    @Override
    public void write64(long addr, long data) {
        slaveBus.write64(this, addr, data);
    }

    @Override
    public void write_ua16(long addr, short data) {
        slaveBus.write_ua16(this, addr, data);
    }

    @Override
    public void write_ua32(long addr, int data) {
        slaveBus.write_ua32(this, addr, data);
    }

    @Override
    public void write_ua64(long addr, long data) {
        slaveBus.write_ua64(this, addr, data);
    }
}
