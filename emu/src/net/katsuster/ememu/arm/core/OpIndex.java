package net.katsuster.ememu.arm.core;

/**
 * 命令の詳細な種類。
 *
 * @author katsuhiro
 */
public enum OpIndex {
    INS_ARM_MRS,
    INS_ARM_MSR,
    INS_ARM_ALUAND,
    INS_ARM_ALUEOR,
    INS_ARM_ALUSUB,
    INS_ARM_ALURSB,
    INS_ARM_ALUADD,
    INS_ARM_ALUADC,
    INS_ARM_ALUSBC,
    INS_ARM_ALURSC,
    INS_ARM_ALUTST,
    INS_ARM_ALUTEQ,
    INS_ARM_ALUCMP,
    INS_ARM_ALUCMN,
    INS_ARM_ALUORR,
    INS_ARM_ALUMOV,
    INS_ARM_ALUBIC,
    INS_ARM_ALUMVN,
    INS_ARM_MLA,
    INS_ARM_MUL,
    INS_ARM_SMLAL,
    INS_ARM_SMULL,
    INS_ARM_UMLAL,
    INS_ARM_UMULL,
    INS_ARM_SMLALXY,
    INS_ARM_SMLAXY,
    INS_ARM_SMLAWY,
    INS_ARM_SMULXY,
    INS_ARM_SMULWY,
    INS_ARM_QDSUB,
    INS_ARM_QDADD,
    INS_ARM_QSUB,
    INS_ARM_QADD,
    INS_ARM_BKPT,
    INS_ARM_SWP,
    INS_ARM_SWPB,
    INS_ARM_LDRT,
    INS_ARM_LDRBT,
    INS_ARM_LDRB,
    INS_ARM_LDR,
    INS_ARM_LDRH,
    INS_ARM_LDRSB,
    INS_ARM_LDRSH,
    INS_ARM_LDRD,
    INS_ARM_PLD,
    INS_ARM_STRT,
    INS_ARM_STRBT,
    INS_ARM_STRB,
    INS_ARM_STR,
    INS_ARM_STRH,
    INS_ARM_STRD,
    INS_ARM_LDM1,
    INS_ARM_LDM2,
    INS_ARM_LDM3,
    INS_ARM_STM1,
    INS_ARM_STM2,
    INS_ARM_BL,
    INS_ARM_BLX1,
    INS_ARM_BLX2,
    INS_ARM_BX,
    INS_ARM_CLZ,
    INS_ARM_CDP,
    INS_ARM_MCR,
    INS_ARM_MRC,
    INS_ARM_SWI,
    INS_ARM_UND,

    INS_THUMB_AND,
    INS_THUMB_EOR,
    INS_THUMB_LSL2,
    INS_THUMB_LSR2,
    INS_THUMB_ASR2,
    INS_THUMB_ADC,
    INS_THUMB_SBC,
    INS_THUMB_ROR,
    INS_THUMB_TST,
    INS_THUMB_NEG,
    INS_THUMB_CMP2,
    INS_THUMB_CMN,
    INS_THUMB_ORR,
    INS_THUMB_MUL,
    INS_THUMB_BIC,
    INS_THUMB_MVN,
    INS_THUMB_ADD1,
    INS_THUMB_ADD2,
    INS_THUMB_ADD3,
    INS_THUMB_ADD4,
    INS_THUMB_ADD5,
    INS_THUMB_ADD6,
    INS_THUMB_ADD7,
    INS_THUMB_SUB1,
    INS_THUMB_SUB2,
    INS_THUMB_SUB3,
    INS_THUMB_SUB4,
    INS_THUMB_CMP1,
    INS_THUMB_MOV1,
    INS_THUMB_MOV3,
    INS_THUMB_LSL1,
    INS_THUMB_LSR1,
    INS_THUMB_ASR1,
    INS_THUMB_LDR1,
    INS_THUMB_LDR2,
    INS_THUMB_LDR3,
    INS_THUMB_LDR4,
    INS_THUMB_LDRB1,
    INS_THUMB_LDRB2,
    INS_THUMB_LDRH1,
    INS_THUMB_LDRH2,
    INS_THUMB_LDRSB,
    INS_THUMB_LDRSH,
    INS_THUMB_STR1,
    INS_THUMB_STR2,
    INS_THUMB_STR3,
    INS_THUMB_STRB1,
    INS_THUMB_STRB2,
    INS_THUMB_STRH1,
    INS_THUMB_STRH2,
    INS_THUMB_PUSH,
    INS_THUMB_POP,
    INS_THUMB_LDMIA,
    INS_THUMB_STMIA,
    INS_THUMB_BKPT,
    INS_THUMB_UND,
    INS_THUMB_SWI,
    INS_THUMB_B1,
    INS_THUMB_B2,
    INS_THUMB_BLX2,
    INS_THUMB_BX,

    INS_THUMB2_B,
    INS_THUMB2_BL,
    INS_THUMB2_BLX,
    INS_THUMB2_SMC,
    INS_THUMB2_UND,

    INS_UNKNOWN,
}
