package net.katsuster.ememu.generic;

/**
 * 64bit バススレーブのインタフェース
 *
 * 自身のタイミングで動作するほか、
 * バスからの Read/Write 要求に応えるコア、
 * スレーブコアを保持していることを表します。
 *
 * @author katsuhiro
 */
public interface BusSlave {
    /**
     * バススレーブとなるコアを取得します。
     *
     * @return バススレーブコア
     */
    public abstract SlaveCore getSlaveCore();
}
