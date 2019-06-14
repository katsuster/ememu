package net.katsuster.ememu.riscv.core;

/**
 * 命令の種類。
 */
public enum OpType {
    INS_TYPE_RV32I,
    INS_TYPE_RV64I,
    INS_TYPE_RV128I,
    INS_TYPE_RVM,
    INS_TYPE_RVA,
    INS_TYPE_RVF,
    INS_TYPE_RVD,
    INS_TYPE_RVQ,
    INS_TYPE_RVL,
    INS_TYPE_RVC,
    INS_TYPE_RVB,
    INS_TYPE_RVJ,
    INS_TYPE_RVT,
    INS_TYPE_RVP,
    INS_TYPE_RVV,
    INS_TYPE_RVN,
    INS_TYPE_UNKNOWN,
}
