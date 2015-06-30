package net.katsuster.ememu.arm.core;

import net.katsuster.ememu.generic.*;

/**
 * ARMv5 の Thumb v2 命令の実行ステージ。
 *
 * 参考: ARM アーキテクチャリファレンスマニュアル Second Edition
 * ARM DDI0100DJ
 *
 * 最新版は、日本語版 ARM DDI0100HJ, 英語版 ARM DDI0100I
 *
 * @author katsuhiro
 */
public class Thumbv2ExecStage extends ExecStage {
    /**
     * ARMv5 CPU コア c の実行ステージを生成します。
     *
     * @param c 実行ステージの持ち主となる ARMv5 CPU コア
     */
    public Thumbv2ExecStage(ARMv5 c) {
        super(c);
    }

    /**
     * 実行ステージの持ち主となる ARMv5 CPU コアを取得します。
     *
     * @return 実行ステージの持ち主となる ARMv5 CPU コア
     */
    @Override
    public ARMv5 getCore() {
        return (ARMv5)super.getCore();
    }

    /**
     * CPSR（カレントプログラムステートレジスタ）の値を取得します。
     *
     * @return CPSR
     */
    public PSR getCPSR() {
        return getCore().getCPSR();
    }

    /**
     * APSR（アプリケーションプログラムステートレジスタ）の値を取得します。
     *
     * N, Z, C, V, Q, GE のみ取得され、他の値は 0 でマスクされます。
     *
     * @return APSR の値
     */
    public APSR getAPSR() {
        return getCore().getAPSR();
    }

    /**
     * SPSR（保存されたプログラムステートレジスタ）の値を取得します。
     *
     * @return SPSR の値
     */
    public PSR getSPSR() {
        return getCore().getSPSR();
    }

    /**
     * コプロセッサ Pn を取得します。
     *
     * @param cpnum コプロセッサ番号
     * @return コプロセッサ
     */
    public CoProc getCoproc(int cpnum) {
        return getCore().getCoproc(cpnum);
    }

    /**
     * コプロセッサレジスタ CRn の名前を取得します。
     *
     * @param cpnum コプロセッサ番号
     * @param n     コプロセッサレジスタ番号（0 ～ 7）
     * @return コプロセッサレジスタの名前
     */
    public String getCoprocRegName(int cpnum, int n) {
        return getCore().getCoprocRegName(cpnum, n);
    }

    /**
     * MMU を取得します。
     *
     * @return MMU
     */
    public MMUv5 getMMU() {
        return getCore().getMMU();
    }

    /**
     * 例外を要求します。
     *
     * @param num    例外番号（EXCEPT_xxxx）
     * @param dbgmsg デバッグ用のメッセージ
     */
    public void raiseException(int num, String dbgmsg) {
        getCore().raiseException(num, dbgmsg);
    }

    /**
     * PC への加算命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAdd5(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * SP への加算（8ビットイミディエート）命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAdd6(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * SP への加算（7ビットイミディエート）命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAdd7(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * SP への減算（7ビットイミディエート）命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSub4(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * プッシュ命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executePush(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * ポップ命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executePop(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }

    /**
     * ブレークポイント命令。
     *
     * @param inst Thumb 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeBkpt(InstructionThumb inst, boolean exec) {
        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }
}
