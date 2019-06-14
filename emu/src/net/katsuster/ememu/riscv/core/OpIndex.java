package net.katsuster.ememu.riscv.core;

/**
 * 命令の詳細な種類。
 */
public enum OpIndex {
    INS_RV32I_LUI,
    INS_RV32I_AUIPC,

    INS_RV32I_JAL,
    INS_RV32I_JALR,

    INS_RV32I_BEQ,
    INS_RV32I_BNE,
    INS_RV32I_BLT,
    INS_RV32I_BGE,
    INS_RV32I_BLTU,
    INS_RV32I_BGEU,

    INS_RV32I_LB,
    INS_RV32I_LH,
    INS_RV32I_LW,
    INS_RV32I_LBU,
    INS_RV32I_LHU,

    INS_UNKNOWN,
}