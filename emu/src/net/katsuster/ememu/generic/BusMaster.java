package net.katsuster.ememu.generic;

/**
 * 64bit バスマスターのインタフェース
 *
 * 自身のタイミングで動作し、バスに Read/Write 要求を行うコア、
 * マスターコアを保持していることを表します。
 *
 * @author katsuhiro
 */
public interface BusMaster {
    /**
     * バスマスターとなるコアを取得します。
     *
     * @return バスマスターコア
     */
    public abstract MasterCore getMasterCore();
}
