package net.katsuster.ememu.generic;

import java.io.*;

import net.katsuster.ememu.generic.bus.Bus64;
import net.katsuster.ememu.generic.core.CPU;

public interface Board {
    /**
     * メイン CPU を取得します。
     *
     * @return メイン CPU
     */
    public abstract CPU getMainCPU();

    /**
     * メインバスを取得します。
     *
     * @return メインバス
     */
    public abstract Bus64 getMainBus();

    /**
     * メイン RAM を取得します。
     *
     * @return メイン RAM
     */
    public abstract RAM getMainRAM();

    /**
     * UART コアの入力に繋がるストリームを取得します。
     *
     * @param index UART コアの番号
     * @return 入力ストリーム
     */
    public abstract InputStream getUARTInputStream(int index);

    /**
     * UART コアの入力に繋がるストリームを設定します。
     *
     * @param index UART コアの番号
     * @param is 入力ストリーム
     */
    public abstract void setUARTInputStream(int index, InputStream is);

    /**
     * UART コアの出力に繋がるストリームを取得します。
     *
     * @param index UART コアの番号
     * @return 出力ストリーム
     */
    public abstract OutputStream getUARTOutputStream(int index);

    /**
     * UART コアの出力に繋がるストリームを設定します。
     *
     * @param index UART コアの番号
     * @param os 出力ストリーム
     */
    public abstract void setUARTOutputStream(int index, OutputStream os);

    /**
     * ボードにコア、バスを配置して初期化します。
     */
    public abstract void setup();

    /**
     * ボードを起動します。
     */
    public abstract void start();

    /**
     * ボードを停止します。
     */
    public abstract void stop();
}
