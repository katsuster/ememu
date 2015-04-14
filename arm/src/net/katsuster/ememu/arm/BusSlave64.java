package net.katsuster.ememu.arm;

/**
 * 64bit バススレーブのインタフェース
 *
 * 自身のタイミングで動作するほか、
 * バスからの Read/Write 要求に応えるコア、
 * スレーブコアを保持していることを表します。
 *
 * @author katsuhiro
 */
public interface BusSlave64 {
    public SlaveCore64 getSlaveCore();
}
