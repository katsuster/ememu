package net.katsuster.ememu.arm.core;

import net.katsuster.ememu.generic.*;

/**
 * Thumb-2 命令の実行ステージ。
 *
 * 参考: ARM アーキテクチャリファレンスマニュアル ARMv7-A および ARMv7-R
 * ARM DDI0406BJ
 *
 * 最新版は、日本語版 ARM DDI0406BJ, 英語版 ARM DDI0406C
 *
 * @author katsuhiro
 */
public class ExecStageThumb2 extends Stage {
    /**
     * CPU コア c の実行ステージを生成します。
     *
     * @param c 実行ステージの持ち主となる CPU コア
     */
    public ExecStageThumb2(ARMv5 c) {
        super(c);
    }

}
