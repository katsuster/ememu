package net.katsuster.ememu.arm;

/**
 * 64bit バスマスターのインタフェース
 *
 * 自身のタイミングで動作し、バスに Read/Write 要求を行うコア、
 * マスターコアを保持していることを表します。
 *
 * @author katsuhiro
 */
public interface BusMaster64 {
    public MasterCore64 getMasterCore();
}
