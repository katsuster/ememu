package net.katsuster.ememu.riscv.core;

/**
 * 命令の詳細な種類。
 */
public enum OpIndex {
    //LUI
    INS_RV32I_LUI,

    //AUIPC
    INS_RV32I_AUIPC,

    //JAL
    INS_RV32I_JAL,

    //JALR
    INS_RV32I_JALR,

    //BRANCH
    INS_RV32I_BEQ,
    INS_RV32I_BNE,
    INS_RV32I_BLT,
    INS_RV32I_BGE,
    INS_RV32I_BLTU,
    INS_RV32I_BGEU,

    //LOAD
    INS_RV32I_LB,
    INS_RV32I_LH,
    INS_RV32I_LW,
    INS_RV32I_LBU,
    INS_RV32I_LHU,

    //STORE
    INS_RV32I_SB,
    INS_RV32I_SH,
    INS_RV32I_SW,

    //OP-IMM
    INS_RV32I_ADDI,
    INS_RV32I_SLTI,
    INS_RV32I_SLTIU,
    INS_RV32I_XORI,
    INS_RV32I_ORI,
    INS_RV32I_ANDI,
    INS_RV32I_SLLI,
    INS_RV32I_SRLI,
    INS_RV32I_SRAI,

    //OP
    INS_RV32I_ADD,
    INS_RV32I_SUB,
    INS_RV32I_SLL,
    INS_RV32I_SLT,
    INS_RV32I_SLTU,
    INS_RV32I_XOR,
    INS_RV32I_SRL,
    INS_RV32I_SRA,
    INS_RV32I_OR,
    INS_RV32I_AND,

    //MISC-MEM
    INS_RV32I_FENCE,
    INS_RV32I_FENCE_I,

    //SYSTEM
    INS_RV32I_ECALL,
    INS_RV32I_EBREAK,
    INS_RV32I_CSRRW,
    INS_RV32I_CSRRS,
    INS_RV32I_CSRRC,
    INS_RV32I_CSRRWI,
    INS_RV32I_CSRRSI,
    INS_RV32I_CSRRCI,

    //LOAD
    INS_RV64I_LWU,
    INS_RV64I_LD,

    //STORE
    INS_RV64I_SD,

    //OP-IMM
    INS_RV64I_SLLI,
    INS_RV64I_SRLI,
    INS_RV64I_SRAI,

    //OP-IMM32
    INS_RV64I_ADDIW,
    INS_RV64I_SLLIW,
    INS_RV64I_SRLIW,
    INS_RV64I_SRAIW,

    //OP32
    INS_RV64I_ADDW,
    INS_RV64I_SUBW,
    INS_RV64I_SLLW,
    INS_RV64I_SRLW,
    INS_RV64I_SRAW,

    //OP
    INS_RV32M_MUL,
    INS_RV32M_MULH,
    INS_RV32M_MULHSU,
    INS_RV32M_MULHU,
    INS_RV32M_DIV,
    INS_RV32M_DIVU,
    INS_RV32M_REM,
    INS_RV32M_REMU,

    //OP-32
    INS_RV64M_MULW,
    INS_RV64M_DIVW,
    INS_RV64M_DIVUW,
    INS_RV64M_REMW,
    INS_RV64M_REMUW,

    //AMO
    INS_RV32A_LR_W,
    INS_RV32A_SC_W,
    INS_RV32A_AMOSWAP_W,
    INS_RV32A_AMOADD_W,
    INS_RV32A_AMOXOR_W,
    INS_RV32A_AMOAND_W,
    INS_RV32A_AMOOR_W,
    INS_RV32A_AMOMIN_W,
    INS_RV32A_AMOMAX_W,
    INS_RV32A_AMOMINU_W,
    INS_RV32A_AMOMAXU_W,

    //AMO
    INS_RV64A_LR_D,
    INS_RV64A_SC_D,
    INS_RV64A_AMOSWAP_D,
    INS_RV64A_AMOADD_D,
    INS_RV64A_AMOXOR_D,
    INS_RV64A_AMOAND_D,
    INS_RV64A_AMOOR_D,
    INS_RV64A_AMOMIN_D,
    INS_RV64A_AMOMAX_D,
    INS_RV64A_AMOMINU_D,
    INS_RV64A_AMOMAXU_D,

    //LOAD-FP
    INS_RV32F_FLW,

    //STORE-FP
    INS_RV32F_FSW,

    //MADD
    INS_RV32F_FMADD_S,

    //MSUB
    INS_RV32F_FMSUB_S,

    //NMSUB
    INS_RV32F_FNMSUB_S,

    //NMADD
    INS_RV32F_FNMADD_S,

    //OP-FP
    INS_RV32F_FADD_S,
    INS_RV32F_FSUB_S,
    INS_RV32F_FMUL_S,
    INS_RV32F_FDIV_S,
    INS_RV32F_FSQRT_S,
    INS_RV32F_FSGNJ_S,
    INS_RV32F_FSGNJN_S,
    INS_RV32F_FSGNJX_S,
    INS_RV32F_FMIN_S,
    INS_RV32F_FMAX_S,
    INS_RV32F_FCVT_W_S,
    INS_RV32F_FCVT_WU_S,
    INS_RV32F_FMV_X_W,
    INS_RV32F_FEQ_S,
    INS_RV32F_FLT_S,
    INS_RV32F_FLE_S,
    INS_RV32F_FCLASS_S,
    INS_RV32F_FCVT_S_W,
    INS_RV32F_FCVT_S_WU,
    INS_RV32F_FMV_W_X,

    //OP-FP
    INS_RV64F_FCVT_L_S,
    INS_RV64F_FCVT_LU_S,
    INS_RV64F_FCVT_S_L,
    INS_RV64F_FCVT_S_LU,

    //LOAD-FP
    INS_RV32D_FLD,

    //STORE-FP
    INS_RV32D_FSD,

    //MADD
    INS_RV32D_FMADD_D,

    //MSUB
    INS_RV32D_FMSUB_D,

    //NMSUB
    INS_RV32D_FNMSUB_D,

    //NMADD
    INS_RV32D_FNMADD_D,

    //OP-FP
    INS_RV32D_FADD_D,
    INS_RV32D_FSUB_D,
    INS_RV32D_FMUL_D,
    INS_RV32D_FDIV_D,
    INS_RV32D_FSQRT_D,
    INS_RV32D_FSGNJ_D,
    INS_RV32D_FSGNJN_D,
    INS_RV32D_FSGNJX_D,
    INS_RV32D_FMIN_D,
    INS_RV32D_FMAX_D,
    INS_RV32D_FCVT_S_D,
    INS_RV32D_FCVT_D_S,
    INS_RV32D_FMV_X_W,
    INS_RV32D_FEQ_D,
    INS_RV32D_FLT_D,
    INS_RV32D_FLE_D,
    INS_RV32D_FCLASS_D,
    INS_RV32D_FCVT_W_D,
    INS_RV32D_FCVT_WU_D,
    INS_RV32D_FMV_D_W,
    INS_RV32D_FMV_D_WU,

    //OP-FP
    INS_RV64D_FCVT_L_D,
    INS_RV64D_FCVT_LU_D,
    INS_RV64D_FMV_X_D,
    INS_RV64D_FCVT_D_L,
    INS_RV64D_FCVT_D_LU,
    INS_RV64D_FMV_D_X,

    INS_RVC_ADDISPN,
    INS_RVC_FLD,
    INS_RVC_LQ,
    INS_RVC_LW,
    INS_RVC_FLW,
    INS_RVC_LD,
    INS_RVC_FSD,
    INS_RVC_SQ,
    INS_RVC_SW,
    INS_RVC_FSW,
    INS_RVC_SD,

    INS_RVC_NOP,
    INS_RVC_JAL,
    INS_RVC_ADDIW,
    INS_RVC_LI,
    INS_RVC_ADDI16SP,
    INS_RVC_LUI,
    INS_RVC_SRLI,
    INS_RVC_SRLI64,
    INS_RVC_SRAI,
    INS_RVC_SRAI64,
    INS_RVC_ANDI,
    INS_RVC_SUB,
    INS_RVC_XOR,
    INS_RVC_OR,
    INS_RVC_AND,
    INS_RVC_SUBW,
    INS_RVC_ADDW,
    INS_RVC_J,
    INS_RVC_BEQZ,
    INS_RVC_BNEZ,

    INS_RVC_SLLI,
    INS_RVC_SLLI64,
    INS_RVC_FLDSP,
    INS_RVC_LQSP,
    INS_RVC_LWSP,
    INS_RVC_FLWSP,
    INS_RVC_LDSP,
    INS_RVC_JR,
    INS_RVC_MV,
    INS_RVC_EBREAK,
    INS_RVC_JALR,
    INS_RVC_ADD,
    INS_RVC_FSDSP,
    INS_RVC_SQSP,
    INS_RVC_SWSP,
    INS_RVC_FSWSP,
    INS_RVC_SDSP,

    INS_UNKNOWN,
}