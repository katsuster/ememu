package net.katsuster.ememu.generic.core;

/**
 * 64bit スレーブコアを所持するインタフェース
 *
 * 自身のタイミングで動作するほか、
 * バスからの Read/Write 要求に応えるコア、
 * スレーブコアを保持していることを表します。
 */
public interface ParentCore {
    /**
     * コアの名前を取得します。
     *
     * @return コアの名前
     */
    public abstract String getName();

    /**
     * バスマスターとなるコアを取得します。
     *
     * @return バスマスターコア
     */
    public abstract MasterCore64 getMasterCore();

    /**
     * バススレーブとなるコアを取得します。
     *
     * @return バススレーブコア
     */
    public abstract SlaveCore64 getSlaveCore();
}
