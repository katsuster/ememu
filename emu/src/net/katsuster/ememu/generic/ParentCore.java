package net.katsuster.ememu.generic;

/**
 * 64bit スレーブコアを所持するインタフェース
 *
 * 自身のタイミングで動作するほか、
 * バスからの Read/Write 要求に応えるコア、
 * スレーブコアを保持していることを表します。
 */
public interface ParentCore {
    /**
     * バススレーブとなるコアを取得します。
     *
     * @return バススレーブコア
     */
    public abstract SlaveCore64 getSlaveCore();
}
