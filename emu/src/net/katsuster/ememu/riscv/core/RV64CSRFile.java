package net.katsuster.ememu.riscv.core;

import java.util.*;

import net.katsuster.ememu.generic.core.Reg64;
import net.katsuster.ememu.generic.core.Reg64File;
import net.katsuster.ememu.riscv.core.reg.*;

/**
 * RISC-V 64 の CSRs (Control and Status Registers)ファイルです。
 *
 * RISC-V Instruction Set Manual
 *   Volume II: Privileged Architecture
 *   Privileged Architecture Version 1.10
 */
public class RV64CSRFile implements Reg64File {
    private Map<Integer, Reg64> regs_csr;
    //User Trap Setup
    public static final int CSR_USTATUS = 0x000;
    public static final int CSR_UIE = 0x004;
    public static final int CSR_UTVEC = 0x005;

    //User Trap Handling
    public static final int CSR_USCRATCH = 0x040;
    public static final int CSR_UEPC = 0x041;
    public static final int CSR_UCAUSE = 0x042;
    public static final int CSR_UTVAL = 0x043;
    public static final int CSR_UIP = 0x044;

    //User Floating-Pint CSRs
    public static final int CSR_FFLAGS = 0x001;
    public static final int CSR_FRM = 0x002;
    public static final int CSR_FCSR = 0x003;

    //User Counter/Timers
    public static final int CSR_CYCLE = 0xc00;
    public static final int CSR_TIME = 0xc01;
    public static final int CSR_INSTRET = 0xc02;
    public static final int CSR_HPMCOUNTER3 = 0xc03;
    public static final int CSR_HPMCOUNTER4 = 0xc04;
    public static final int CSR_HPMCOUNTER5 = 0xc05;
    public static final int CSR_HPMCOUNTER6 = 0xc06;
    public static final int CSR_HPMCOUNTER7 = 0xc07;
    public static final int CSR_HPMCOUNTER8 = 0xc08;
    public static final int CSR_HPMCOUNTER9 = 0xc09;
    public static final int CSR_HPMCOUNTER10 = 0xc0a;
    public static final int CSR_HPMCOUNTER11 = 0xc0b;
    public static final int CSR_HPMCOUNTER12 = 0xc0c;
    public static final int CSR_HPMCOUNTER13 = 0xc0d;
    public static final int CSR_HPMCOUNTER14 = 0xc0e;
    public static final int CSR_HPMCOUNTER15 = 0xc0f;
    public static final int CSR_HPMCOUNTER16 = 0xc10;
    public static final int CSR_HPMCOUNTER17 = 0xc11;
    public static final int CSR_HPMCOUNTER18 = 0xc12;
    public static final int CSR_HPMCOUNTER19 = 0xc13;
    public static final int CSR_HPMCOUNTER20 = 0xc14;
    public static final int CSR_HPMCOUNTER21 = 0xc15;
    public static final int CSR_HPMCOUNTER22 = 0xc16;
    public static final int CSR_HPMCOUNTER23 = 0xc17;
    public static final int CSR_HPMCOUNTER24 = 0xc18;
    public static final int CSR_HPMCOUNTER25 = 0xc19;
    public static final int CSR_HPMCOUNTER26 = 0xc1a;
    public static final int CSR_HPMCOUNTER27 = 0xc1b;
    public static final int CSR_HPMCOUNTER28 = 0xc1c;
    public static final int CSR_HPMCOUNTER29 = 0xc1d;
    public static final int CSR_HPMCOUNTER30 = 0xc1e;
    public static final int CSR_HPMCOUNTER31 = 0xc1f;
    public static final int CSR_CYCLEH = 0xc80;
    public static final int CSR_TIMEH = 0xc81;
    public static final int CSR_INSTRETH = 0xc82;
    public static final int CSR_HPMCOUNTER3H = 0xc83;
    public static final int CSR_HPMCOUNTER4H = 0xc84;
    public static final int CSR_HPMCOUNTER5H = 0xc85;
    public static final int CSR_HPMCOUNTER6H = 0xc86;
    public static final int CSR_HPMCOUNTER7H = 0xc87;
    public static final int CSR_HPMCOUNTER8H = 0xc88;
    public static final int CSR_HPMCOUNTER9H = 0xc89;
    public static final int CSR_HPMCOUNTER10H = 0xc8a;
    public static final int CSR_HPMCOUNTER11H = 0xc8b;
    public static final int CSR_HPMCOUNTER12H = 0xc8c;
    public static final int CSR_HPMCOUNTER13H = 0xc8d;
    public static final int CSR_HPMCOUNTER14H = 0xc8e;
    public static final int CSR_HPMCOUNTER15H = 0xc8f;
    public static final int CSR_HPMCOUNTER16H = 0xc90;
    public static final int CSR_HPMCOUNTER17H = 0xc91;
    public static final int CSR_HPMCOUNTER18H = 0xc92;
    public static final int CSR_HPMCOUNTER19H = 0xc93;
    public static final int CSR_HPMCOUNTER20H = 0xc94;
    public static final int CSR_HPMCOUNTER21H = 0xc95;
    public static final int CSR_HPMCOUNTER22H = 0xc96;
    public static final int CSR_HPMCOUNTER23H = 0xc97;
    public static final int CSR_HPMCOUNTER24H = 0xc98;
    public static final int CSR_HPMCOUNTER25H = 0xc99;
    public static final int CSR_HPMCOUNTER26H = 0xc9a;
    public static final int CSR_HPMCOUNTER27H = 0xc9b;
    public static final int CSR_HPMCOUNTER28H = 0xc9c;
    public static final int CSR_HPMCOUNTER29H = 0xc9d;
    public static final int CSR_HPMCOUNTER30H = 0xc9e;
    public static final int CSR_HPMCOUNTER31H = 0xc9f;

    //Supervisor Trap Setup
    public static final int CSR_SSTATUS = 0x100;
    public static final int CSR_SEDELEG = 0x102;
    public static final int CSR_SIDELEG = 0x103;
    public static final int CSR_SIE = 0x104;
    public static final int CSR_STVEC = 0x105;
    public static final int CSR_SCOUNTEREN = 0x106;

    //Supervisor Trap Handling
    public static final int CSR_SSCRATCH = 0x140;
    public static final int CSR_SEPC = 0x141;
    public static final int CSR_SCAUSE = 0x142;
    public static final int CSR_STVAL = 0x143;
    public static final int CSR_SIP = 0x144;

    //Supervisor Protection and Translation
    public static final int CSR_SATP = 0x180;

    //Machine Information Registers
    public static final int CSR_MVENDORID = 0xf11;
    public static final int CSR_MARCHID = 0xf12;
    public static final int CSR_MIMPID = 0xf13;
    public static final int CSR_MHARTID = 0xf14;

    //Machine Trap Setup
    public static final int CSR_MSTATUS = 0x300;
    public static final int CSR_MISA = 0x301;
    public static final int CSR_MEDELEG = 0x302;
    public static final int CSR_MIDELEG = 0x303;
    public static final int CSR_MIE = 0x304;
    public static final int CSR_MTVEC = 0x305;
    public static final int CSR_MCOUNTEREN = 0x306;

    //Machine Trap Handling
    public static final int CSR_MSCRATCH = 0x340;
    public static final int CSR_MEPC = 0x341;
    public static final int CSR_MCAUSE = 0x342;
    public static final int CSR_MTVAL = 0x343;
    public static final int CSR_MIP = 0x344;

    //Machine protection and Translation
    public static final int CSR_PMPCFG0 = 0x3a0;
    public static final int CSR_PMPCFG1 = 0x3a1;
    public static final int CSR_PMPCFG2 = 0x3a2;
    public static final int CSR_PMPCFG3 = 0x3a3;
    public static final int CSR_PMPADDR0 = 0x3b0;
    public static final int CSR_PMPADDR1 = 0x3b1;
    public static final int CSR_PMPADDR2 = 0x3b2;
    public static final int CSR_PMPADDR3 = 0x3b3;
    public static final int CSR_PMPADDR4 = 0x3b4;
    public static final int CSR_PMPADDR5 = 0x3b5;
    public static final int CSR_PMPADDR6 = 0x3b6;
    public static final int CSR_PMPADDR7 = 0x3b7;
    public static final int CSR_PMPADDR8 = 0x3b8;
    public static final int CSR_PMPADDR9 = 0x3b9;
    public static final int CSR_PMPADDR10 = 0x3ba;
    public static final int CSR_PMPADDR11 = 0x3bb;
    public static final int CSR_PMPADDR12 = 0x3bc;
    public static final int CSR_PMPADDR13 = 0x3bd;
    public static final int CSR_PMPADDR14 = 0x3be;
    public static final int CSR_PMPADDR15 = 0x3bf;

    //Machine Counter/Timers
    public static final int CSR_MCYCLE = 0xb00;
    public static final int CSR_MINSTRET = 0xb02;
    public static final int CSR_MHPMCOUNTER3 = 0xb03;
    public static final int CSR_MHPMCOUNTER4 = 0xb04;
    public static final int CSR_MHPMCOUNTER5 = 0xb05;
    public static final int CSR_MHPMCOUNTER6 = 0xb06;
    public static final int CSR_MHPMCOUNTER7 = 0xb07;
    public static final int CSR_MHPMCOUNTER8 = 0xb08;
    public static final int CSR_MHPMCOUNTER9 = 0xb09;
    public static final int CSR_MHPMCOUNTER10 = 0xb0a;
    public static final int CSR_MHPMCOUNTER11 = 0xb0b;
    public static final int CSR_MHPMCOUNTER12 = 0xb0c;
    public static final int CSR_MHPMCOUNTER13 = 0xb0d;
    public static final int CSR_MHPMCOUNTER14 = 0xb0e;
    public static final int CSR_MHPMCOUNTER15 = 0xb0f;
    public static final int CSR_MHPMCOUNTER16 = 0xb10;
    public static final int CSR_MHPMCOUNTER17 = 0xb11;
    public static final int CSR_MHPMCOUNTER18 = 0xb12;
    public static final int CSR_MHPMCOUNTER19 = 0xb13;
    public static final int CSR_MHPMCOUNTER20 = 0xb14;
    public static final int CSR_MHPMCOUNTER21 = 0xb15;
    public static final int CSR_MHPMCOUNTER22 = 0xb16;
    public static final int CSR_MHPMCOUNTER23 = 0xb17;
    public static final int CSR_MHPMCOUNTER24 = 0xb18;
    public static final int CSR_MHPMCOUNTER25 = 0xb19;
    public static final int CSR_MHPMCOUNTER26 = 0xb1a;
    public static final int CSR_MHPMCOUNTER27 = 0xb1b;
    public static final int CSR_MHPMCOUNTER28 = 0xb1c;
    public static final int CSR_MHPMCOUNTER29 = 0xb1d;
    public static final int CSR_MHPMCOUNTER30 = 0xb1e;
    public static final int CSR_MHPMCOUNTER31 = 0xb1f;
    public static final int CSR_MCYCLEH = 0xb80;
    public static final int CSR_MINSTRETH = 0xb82;
    public static final int CSR_MHPMCOUNTER3H = 0xb83;
    public static final int CSR_MHPMCOUNTER4H = 0xb84;
    public static final int CSR_MHPMCOUNTER5H = 0xb85;
    public static final int CSR_MHPMCOUNTER6H = 0xb86;
    public static final int CSR_MHPMCOUNTER7H = 0xb87;
    public static final int CSR_MHPMCOUNTER8H = 0xb88;
    public static final int CSR_MHPMCOUNTER9H = 0xb89;
    public static final int CSR_MHPMCOUNTER10H = 0xb8a;
    public static final int CSR_MHPMCOUNTER11H = 0xb8b;
    public static final int CSR_MHPMCOUNTER12H = 0xb8c;
    public static final int CSR_MHPMCOUNTER13H = 0xb8d;
    public static final int CSR_MHPMCOUNTER14H = 0xb8e;
    public static final int CSR_MHPMCOUNTER15H = 0xb8f;
    public static final int CSR_MHPMCOUNTER16H = 0xb90;
    public static final int CSR_MHPMCOUNTER17H = 0xb91;
    public static final int CSR_MHPMCOUNTER18H = 0xb92;
    public static final int CSR_MHPMCOUNTER19H = 0xb93;
    public static final int CSR_MHPMCOUNTER20H = 0xb94;
    public static final int CSR_MHPMCOUNTER21H = 0xb95;
    public static final int CSR_MHPMCOUNTER22H = 0xb96;
    public static final int CSR_MHPMCOUNTER23H = 0xb97;
    public static final int CSR_MHPMCOUNTER24H = 0xb98;
    public static final int CSR_MHPMCOUNTER25H = 0xb99;
    public static final int CSR_MHPMCOUNTER26H = 0xb9a;
    public static final int CSR_MHPMCOUNTER27H = 0xb9b;
    public static final int CSR_MHPMCOUNTER28H = 0xb9c;
    public static final int CSR_MHPMCOUNTER29H = 0xb9d;
    public static final int CSR_MHPMCOUNTER30H = 0xb9e;
    public static final int CSR_MHPMCOUNTER31H = 0xb9f;

    //Machine Counter Setup
    public static final int CSR_MHPMEVENT3 = 0x323;
    public static final int CSR_MHPMEVENT4 = 0x324;
    public static final int CSR_MHPMEVENT5 = 0x325;
    public static final int CSR_MHPMEVENT6 = 0x326;
    public static final int CSR_MHPMEVENT7 = 0x327;
    public static final int CSR_MHPMEVENT8 = 0x328;
    public static final int CSR_MHPMEVENT9 = 0x329;
    public static final int CSR_MHPMEVENT10 = 0x32a;
    public static final int CSR_MHPMEVENT11 = 0x32b;
    public static final int CSR_MHPMEVENT12 = 0x32c;
    public static final int CSR_MHPMEVENT13 = 0x32d;
    public static final int CSR_MHPMEVENT14 = 0x32e;
    public static final int CSR_MHPMEVENT15 = 0x32f;
    public static final int CSR_MHPMEVENT16 = 0x330;
    public static final int CSR_MHPMEVENT17 = 0x331;
    public static final int CSR_MHPMEVENT18 = 0x332;
    public static final int CSR_MHPMEVENT19 = 0x333;
    public static final int CSR_MHPMEVENT20 = 0x334;
    public static final int CSR_MHPMEVENT21 = 0x335;
    public static final int CSR_MHPMEVENT22 = 0x336;
    public static final int CSR_MHPMEVENT23 = 0x337;
    public static final int CSR_MHPMEVENT24 = 0x338;
    public static final int CSR_MHPMEVENT25 = 0x339;
    public static final int CSR_MHPMEVENT26 = 0x33a;
    public static final int CSR_MHPMEVENT27 = 0x33b;
    public static final int CSR_MHPMEVENT28 = 0x33c;
    public static final int CSR_MHPMEVENT29 = 0x33d;
    public static final int CSR_MHPMEVENT30 = 0x33e;
    public static final int CSR_MHPMEVENT31 = 0x33f;

    //Debug/Trace Registers (shared with Debug Mode)
    public static final int CSR_TSELECT = 0x7a0;
    public static final int CSR_TDATA1 = 0x7a1;
    public static final int CSR_TDATA2 = 0x7a2;
    public static final int CSR_TDATA3 = 0x7a3;

    //Debug Mode Registers
    public static final int CSR_DCSR = 0x7b0;
    public static final int CSR_DPC = 0x7b1;
    public static final int CSR_DSCRATCH = 0x7b2;

    //xstatus registers bit field
    public static final int XSTATUS_XIE = 0;
    public static final int XSTATUS_XPIE = 4;

    //xtvec registers bit field
    public static final int XTVEC_MODE_DIRECT = 0;
    public static final int XTVEC_MODE_VECTOR = 1;
    public static final int XTVEC_MODE_RESERVED1 = 2;
    public static final int XTVEC_MODE_RESERVED2 = 3;

    public static final long XTVEC_BASE_MASK = ~3;
    public static final long XTVEC_MODE_MASK = 3;

    //xip registers bit field
    public static final int XIP_XSIP = 0;
    public static final int XIP_XTIP = 4;
    public static final int XIP_XEIP = 8;

    //xie registers bit field
    public static final int XIE_XSIE = 0;
    public static final int XIE_XTIE = 4;
    public static final int XIE_XEIE = 8;

    //xcause registers bit field
    public static final int XCAUSE_CODE = 0;
    public static final int XCAUSE_INTERRUPT = 63;

    public RV64CSRFile(RV64 c) {
        regs_csr = new HashMap<>();

        //User Trap Setup
        regs_csr.put(CSR_USTATUS,        new Reg64("ustatus", 0));
        regs_csr.put(CSR_UIE,            new Reg64("uie", 0));
        regs_csr.put(CSR_UTVEC,          new Reg64("utvec", 0));

        //User Trap Handling
        regs_csr.put(CSR_USCRATCH,       new Reg64("uscratch", 0));
        regs_csr.put(CSR_UEPC,           new Reg64("uepc", 0));
        regs_csr.put(CSR_UCAUSE,         new Reg64("ucause", 0));
        regs_csr.put(CSR_UTVAL,          new Reg64("utval", 0));
        regs_csr.put(CSR_UIP,            new Reg64("uip", 0));

        //User Floating-Pint CSRs
        regs_csr.put(CSR_FFLAGS,         new Reg64("fflags", 0));
        regs_csr.put(CSR_FRM,            new Reg64("frm", 0));
        regs_csr.put(CSR_FCSR,           new Reg64("fcsr", 0));

        //User Counter/Timers
        regs_csr.put(CSR_CYCLE,          new Reg64("cycle", 0));
        regs_csr.put(CSR_TIME,           new Reg64("time", 0));
        regs_csr.put(CSR_INSTRET,        new Reg64("instret", 0));
        regs_csr.put(CSR_HPMCOUNTER3,    new Reg64("hpmcounter3", 0));
        regs_csr.put(CSR_HPMCOUNTER4,    new Reg64("hpmcounter4", 0));
        regs_csr.put(CSR_HPMCOUNTER5,    new Reg64("hpmcounter5", 0));
        regs_csr.put(CSR_HPMCOUNTER6,    new Reg64("hpmcounter6", 0));
        regs_csr.put(CSR_HPMCOUNTER7,    new Reg64("hpmcounter7", 0));
        regs_csr.put(CSR_HPMCOUNTER8,    new Reg64("hpmcounter8", 0));
        regs_csr.put(CSR_HPMCOUNTER9,    new Reg64("hpmcounter9", 0));
        regs_csr.put(CSR_HPMCOUNTER10,   new Reg64("hpmcounter10", 0));
        regs_csr.put(CSR_HPMCOUNTER11,   new Reg64("hpmcounter11", 0));
        regs_csr.put(CSR_HPMCOUNTER12,   new Reg64("hpmcounter12", 0));
        regs_csr.put(CSR_HPMCOUNTER13,   new Reg64("hpmcounter13", 0));
        regs_csr.put(CSR_HPMCOUNTER14,   new Reg64("hpmcounter14", 0));
        regs_csr.put(CSR_HPMCOUNTER15,   new Reg64("hpmcounter15", 0));
        regs_csr.put(CSR_HPMCOUNTER16,   new Reg64("hpmcounter16", 0));
        regs_csr.put(CSR_HPMCOUNTER17,   new Reg64("hpmcounter17", 0));
        regs_csr.put(CSR_HPMCOUNTER18,   new Reg64("hpmcounter18", 0));
        regs_csr.put(CSR_HPMCOUNTER19,   new Reg64("hpmcounter19", 0));
        regs_csr.put(CSR_HPMCOUNTER20,   new Reg64("hpmcounter20", 0));
        regs_csr.put(CSR_HPMCOUNTER21,   new Reg64("hpmcounter21", 0));
        regs_csr.put(CSR_HPMCOUNTER22,   new Reg64("hpmcounter22", 0));
        regs_csr.put(CSR_HPMCOUNTER23,   new Reg64("hpmcounter23", 0));
        regs_csr.put(CSR_HPMCOUNTER24,   new Reg64("hpmcounter24", 0));
        regs_csr.put(CSR_HPMCOUNTER25,   new Reg64("hpmcounter25", 0));
        regs_csr.put(CSR_HPMCOUNTER26,   new Reg64("hpmcounter26", 0));
        regs_csr.put(CSR_HPMCOUNTER27,   new Reg64("hpmcounter27", 0));
        regs_csr.put(CSR_HPMCOUNTER28,   new Reg64("hpmcounter28", 0));
        regs_csr.put(CSR_HPMCOUNTER29,   new Reg64("hpmcounter29", 0));
        regs_csr.put(CSR_HPMCOUNTER30,   new Reg64("hpmcounter30", 0));
        regs_csr.put(CSR_HPMCOUNTER31,   new Reg64("hpmcounter31", 0));
        regs_csr.put(CSR_CYCLEH,         new Reg64("cycleh", 0));
        regs_csr.put(CSR_TIMEH,          new Reg64("timeh", 0));
        regs_csr.put(CSR_INSTRETH,       new Reg64("instreth", 0));
        regs_csr.put(CSR_HPMCOUNTER3H,   new Reg64("hpmcounter3h", 0));
        regs_csr.put(CSR_HPMCOUNTER4H,   new Reg64("hpmcounter4h", 0));
        regs_csr.put(CSR_HPMCOUNTER5H,   new Reg64("hpmcounter5h", 0));
        regs_csr.put(CSR_HPMCOUNTER6H,   new Reg64("hpmcounter6h", 0));
        regs_csr.put(CSR_HPMCOUNTER7H,   new Reg64("hpmcounter7h", 0));
        regs_csr.put(CSR_HPMCOUNTER8H,   new Reg64("hpmcounter8h", 0));
        regs_csr.put(CSR_HPMCOUNTER9H,   new Reg64("hpmcounter9h", 0));
        regs_csr.put(CSR_HPMCOUNTER10H,  new Reg64("hpmcounter10h", 0));
        regs_csr.put(CSR_HPMCOUNTER11H,  new Reg64("hpmcounter11h", 0));
        regs_csr.put(CSR_HPMCOUNTER12H,  new Reg64("hpmcounter12h", 0));
        regs_csr.put(CSR_HPMCOUNTER13H,  new Reg64("hpmcounter13h", 0));
        regs_csr.put(CSR_HPMCOUNTER14H,  new Reg64("hpmcounter14h", 0));
        regs_csr.put(CSR_HPMCOUNTER15H,  new Reg64("hpmcounter15h", 0));
        regs_csr.put(CSR_HPMCOUNTER16H,  new Reg64("hpmcounter16h", 0));
        regs_csr.put(CSR_HPMCOUNTER17H,  new Reg64("hpmcounter17h", 0));
        regs_csr.put(CSR_HPMCOUNTER18H,  new Reg64("hpmcounter18h", 0));
        regs_csr.put(CSR_HPMCOUNTER19H,  new Reg64("hpmcounter19h", 0));
        regs_csr.put(CSR_HPMCOUNTER20H,  new Reg64("hpmcounter20h", 0));
        regs_csr.put(CSR_HPMCOUNTER21H,  new Reg64("hpmcounter21h", 0));
        regs_csr.put(CSR_HPMCOUNTER22H,  new Reg64("hpmcounter22h", 0));
        regs_csr.put(CSR_HPMCOUNTER23H,  new Reg64("hpmcounter23h", 0));
        regs_csr.put(CSR_HPMCOUNTER24H,  new Reg64("hpmcounter24h", 0));
        regs_csr.put(CSR_HPMCOUNTER25H,  new Reg64("hpmcounter25h", 0));
        regs_csr.put(CSR_HPMCOUNTER26H,  new Reg64("hpmcounter26h", 0));
        regs_csr.put(CSR_HPMCOUNTER27H,  new Reg64("hpmcounter27h", 0));
        regs_csr.put(CSR_HPMCOUNTER28H,  new Reg64("hpmcounter28h", 0));
        regs_csr.put(CSR_HPMCOUNTER29H,  new Reg64("hpmcounter29h", 0));
        regs_csr.put(CSR_HPMCOUNTER30H,  new Reg64("hpmcounter30h", 0));
        regs_csr.put(CSR_HPMCOUNTER31H,  new Reg64("hpmcounter31h", 0));

        //Supervisor Trap Setup
        regs_csr.put(CSR_SSTATUS,        new Reg64("sstatus", 0));
        regs_csr.put(CSR_SEDELEG,        new Reg64("sedeleg", 0));
        regs_csr.put(CSR_SIDELEG,        new Reg64("sideleg", 0));
        regs_csr.put(CSR_SIE,            new Reg64("sie", 0));
        regs_csr.put(CSR_STVEC,          new Reg64("stvec", 0));
        regs_csr.put(CSR_SCOUNTEREN,     new Reg64("scounteren", 0));

        //Supervisor Trap Handling
        regs_csr.put(CSR_SSCRATCH,       new Reg64("sscratch", 0));
        regs_csr.put(CSR_SEPC,           new Reg64("sepc", 0));
        regs_csr.put(CSR_SCAUSE,         new Reg64("scause", 0));
        regs_csr.put(CSR_STVAL,          new Reg64("stval", 0));
        regs_csr.put(CSR_SIP,            new Reg64("sip", 0));

        //Supervisor Protection and Translation
        regs_csr.put(CSR_SATP,           new Reg64("satp", 0));

        //Machine Information Registers
        regs_csr.put(CSR_MVENDORID,      new Reg64("mvendorid", 0));
        regs_csr.put(CSR_MARCHID,        new Reg64("marchid", 0));
        regs_csr.put(CSR_MIMPID,         new Reg64("mimpid", 0));
        regs_csr.put(CSR_MHARTID,        new RegHartid64("mhartid", 0, c));

        //Machine Trap Setup
        regs_csr.put(CSR_MSTATUS,        new Reg64("mstatus", 0));
        regs_csr.put(CSR_MISA,           new Reg64("misa", 0));
        regs_csr.put(CSR_MEDELEG,        new Reg64("medeleg", 0));
        regs_csr.put(CSR_MIDELEG,        new Reg64("mideleg", 0));
        regs_csr.put(CSR_MIE,            new Reg64("mie", 0));
        regs_csr.put(CSR_MTVEC,          new Reg64("mtvec", 0));
        regs_csr.put(CSR_MCOUNTEREN,     new Reg64("mcounteren", 0));

        //Machine Trap Handling
        regs_csr.put(CSR_MSCRATCH,       new Reg64("mscratch", 0));
        regs_csr.put(CSR_MEPC,           new Reg64("mepc", 0));
        regs_csr.put(CSR_MCAUSE,         new Reg64("mcause", 0));
        regs_csr.put(CSR_MTVAL,          new Reg64("mtval", 0));
        regs_csr.put(CSR_MIP,            new Reg64("mip", 0));

        //Machine protection and Translation
        regs_csr.put(CSR_PMPCFG0,        new Reg64("pmpcfg0", 0));
        regs_csr.put(CSR_PMPCFG1,        new Reg64("pmpcfg1", 0));
        regs_csr.put(CSR_PMPCFG2,        new Reg64("pmpcfg2", 0));
        regs_csr.put(CSR_PMPCFG3,        new Reg64("pmpcfg3", 0));
        regs_csr.put(CSR_PMPADDR0,       new Reg64("pmpaddr0", 0));
        regs_csr.put(CSR_PMPADDR1,       new Reg64("pmpaddr1", 0));
        regs_csr.put(CSR_PMPADDR2,       new Reg64("pmpaddr2", 0));
        regs_csr.put(CSR_PMPADDR3,       new Reg64("pmpaddr3", 0));
        regs_csr.put(CSR_PMPADDR4,       new Reg64("pmpaddr4", 0));
        regs_csr.put(CSR_PMPADDR5,       new Reg64("pmpaddr5", 0));
        regs_csr.put(CSR_PMPADDR6,       new Reg64("pmpaddr6", 0));
        regs_csr.put(CSR_PMPADDR7,       new Reg64("pmpaddr7", 0));
        regs_csr.put(CSR_PMPADDR8,       new Reg64("pmpaddr8", 0));
        regs_csr.put(CSR_PMPADDR9,       new Reg64("pmpaddr9", 0));
        regs_csr.put(CSR_PMPADDR10,      new Reg64("pmpaddr10", 0));
        regs_csr.put(CSR_PMPADDR11,      new Reg64("pmpaddr11", 0));
        regs_csr.put(CSR_PMPADDR12,      new Reg64("pmpaddr12", 0));
        regs_csr.put(CSR_PMPADDR13,      new Reg64("pmpaddr13", 0));
        regs_csr.put(CSR_PMPADDR14,      new Reg64("pmpaddr14", 0));
        regs_csr.put(CSR_PMPADDR15,      new Reg64("pmpaddr15", 0));

        //Machine Counter/Timers
        regs_csr.put(CSR_MCYCLE,         new Reg64("mcycle", 0));
        regs_csr.put(CSR_MINSTRET,       new Reg64("minstret", 0));
        regs_csr.put(CSR_MHPMCOUNTER3,   new Reg64("mhpmcounter3", 0));
        regs_csr.put(CSR_MHPMCOUNTER4,   new Reg64("mhpmcounter4", 0));
        regs_csr.put(CSR_MHPMCOUNTER5,   new Reg64("mhpmcounter5", 0));
        regs_csr.put(CSR_MHPMCOUNTER6,   new Reg64("mhpmcounter6", 0));
        regs_csr.put(CSR_MHPMCOUNTER7,   new Reg64("mhpmcounter7", 0));
        regs_csr.put(CSR_MHPMCOUNTER8,   new Reg64("mhpmcounter8", 0));
        regs_csr.put(CSR_MHPMCOUNTER9,   new Reg64("mhpmcounter9", 0));
        regs_csr.put(CSR_MHPMCOUNTER10,  new Reg64("mhpmcounter10", 0));
        regs_csr.put(CSR_MHPMCOUNTER11,  new Reg64("mhpmcounter11", 0));
        regs_csr.put(CSR_MHPMCOUNTER12,  new Reg64("mhpmcounter12", 0));
        regs_csr.put(CSR_MHPMCOUNTER13,  new Reg64("mhpmcounter13", 0));
        regs_csr.put(CSR_MHPMCOUNTER14,  new Reg64("mhpmcounter14", 0));
        regs_csr.put(CSR_MHPMCOUNTER15,  new Reg64("mhpmcounter15", 0));
        regs_csr.put(CSR_MHPMCOUNTER16,  new Reg64("mhpmcounter16", 0));
        regs_csr.put(CSR_MHPMCOUNTER17,  new Reg64("mhpmcounter17", 0));
        regs_csr.put(CSR_MHPMCOUNTER18,  new Reg64("mhpmcounter18", 0));
        regs_csr.put(CSR_MHPMCOUNTER19,  new Reg64("mhpmcounter19", 0));
        regs_csr.put(CSR_MHPMCOUNTER20,  new Reg64("mhpmcounter20", 0));
        regs_csr.put(CSR_MHPMCOUNTER21,  new Reg64("mhpmcounter21", 0));
        regs_csr.put(CSR_MHPMCOUNTER22,  new Reg64("mhpmcounter22", 0));
        regs_csr.put(CSR_MHPMCOUNTER23,  new Reg64("mhpmcounter23", 0));
        regs_csr.put(CSR_MHPMCOUNTER24,  new Reg64("mhpmcounter24", 0));
        regs_csr.put(CSR_MHPMCOUNTER25,  new Reg64("mhpmcounter25", 0));
        regs_csr.put(CSR_MHPMCOUNTER26,  new Reg64("mhpmcounter26", 0));
        regs_csr.put(CSR_MHPMCOUNTER27,  new Reg64("mhpmcounter27", 0));
        regs_csr.put(CSR_MHPMCOUNTER28,  new Reg64("mhpmcounter28", 0));
        regs_csr.put(CSR_MHPMCOUNTER29,  new Reg64("mhpmcounter29", 0));
        regs_csr.put(CSR_MHPMCOUNTER30,  new Reg64("mhpmcounter30", 0));
        regs_csr.put(CSR_MHPMCOUNTER31,  new Reg64("mhpmcounter31", 0));
        regs_csr.put(CSR_MCYCLEH,        new Reg64("mcycleh", 0));
        regs_csr.put(CSR_MINSTRETH,      new Reg64("minstreth", 0));
        regs_csr.put(CSR_MHPMCOUNTER3H,  new Reg64("mhpmcounter3h", 0));
        regs_csr.put(CSR_MHPMCOUNTER4H,  new Reg64("mhpmcounter4h", 0));
        regs_csr.put(CSR_MHPMCOUNTER5H,  new Reg64("mhpmcounter5h", 0));
        regs_csr.put(CSR_MHPMCOUNTER6H,  new Reg64("mhpmcounter6h", 0));
        regs_csr.put(CSR_MHPMCOUNTER7H,  new Reg64("mhpmcounter7h", 0));
        regs_csr.put(CSR_MHPMCOUNTER8H,  new Reg64("mhpmcounter8h", 0));
        regs_csr.put(CSR_MHPMCOUNTER9H,  new Reg64("mhpmcounter9h", 0));
        regs_csr.put(CSR_MHPMCOUNTER10H, new Reg64("mhpmcounter10h", 0));
        regs_csr.put(CSR_MHPMCOUNTER11H, new Reg64("mhpmcounter11h", 0));
        regs_csr.put(CSR_MHPMCOUNTER12H, new Reg64("mhpmcounter12h", 0));
        regs_csr.put(CSR_MHPMCOUNTER13H, new Reg64("mhpmcounter13h", 0));
        regs_csr.put(CSR_MHPMCOUNTER14H, new Reg64("mhpmcounter14h", 0));
        regs_csr.put(CSR_MHPMCOUNTER15H, new Reg64("mhpmcounter15h", 0));
        regs_csr.put(CSR_MHPMCOUNTER16H, new Reg64("mhpmcounter16h", 0));
        regs_csr.put(CSR_MHPMCOUNTER17H, new Reg64("mhpmcounter17h", 0));
        regs_csr.put(CSR_MHPMCOUNTER18H, new Reg64("mhpmcounter18h", 0));
        regs_csr.put(CSR_MHPMCOUNTER19H, new Reg64("mhpmcounter19h", 0));
        regs_csr.put(CSR_MHPMCOUNTER20H, new Reg64("mhpmcounter20h", 0));
        regs_csr.put(CSR_MHPMCOUNTER21H, new Reg64("mhpmcounter21h", 0));
        regs_csr.put(CSR_MHPMCOUNTER22H, new Reg64("mhpmcounter22h", 0));
        regs_csr.put(CSR_MHPMCOUNTER23H, new Reg64("mhpmcounter23h", 0));
        regs_csr.put(CSR_MHPMCOUNTER24H, new Reg64("mhpmcounter24h", 0));
        regs_csr.put(CSR_MHPMCOUNTER25H, new Reg64("mhpmcounter25h", 0));
        regs_csr.put(CSR_MHPMCOUNTER26H, new Reg64("mhpmcounter26h", 0));
        regs_csr.put(CSR_MHPMCOUNTER27H, new Reg64("mhpmcounter27h", 0));
        regs_csr.put(CSR_MHPMCOUNTER28H, new Reg64("mhpmcounter28h", 0));
        regs_csr.put(CSR_MHPMCOUNTER29H, new Reg64("mhpmcounter29h", 0));
        regs_csr.put(CSR_MHPMCOUNTER30H, new Reg64("mhpmcounter30h", 0));
        regs_csr.put(CSR_MHPMCOUNTER31H, new Reg64("mhpmcounter31h", 0));

        //Machine Counter Setup
        regs_csr.put(CSR_MHPMEVENT3,     new Reg64("mhpmevent3", 0));
        regs_csr.put(CSR_MHPMEVENT4,     new Reg64("mhpmevent4", 0));
        regs_csr.put(CSR_MHPMEVENT5,     new Reg64("mhpmevent5", 0));
        regs_csr.put(CSR_MHPMEVENT6,     new Reg64("mhpmevent6", 0));
        regs_csr.put(CSR_MHPMEVENT7,     new Reg64("mhpmevent7", 0));
        regs_csr.put(CSR_MHPMEVENT8,     new Reg64("mhpmevent8", 0));
        regs_csr.put(CSR_MHPMEVENT9,     new Reg64("mhpmevent9", 0));
        regs_csr.put(CSR_MHPMEVENT10,    new Reg64("mhpmevent10", 0));
        regs_csr.put(CSR_MHPMEVENT11,    new Reg64("mhpmevent11", 0));
        regs_csr.put(CSR_MHPMEVENT12,    new Reg64("mhpmevent12", 0));
        regs_csr.put(CSR_MHPMEVENT13,    new Reg64("mhpmevent13", 0));
        regs_csr.put(CSR_MHPMEVENT14,    new Reg64("mhpmevent14", 0));
        regs_csr.put(CSR_MHPMEVENT15,    new Reg64("mhpmevent15", 0));
        regs_csr.put(CSR_MHPMEVENT16,    new Reg64("mhpmevent16", 0));
        regs_csr.put(CSR_MHPMEVENT17,    new Reg64("mhpmevent17", 0));
        regs_csr.put(CSR_MHPMEVENT18,    new Reg64("mhpmevent18", 0));
        regs_csr.put(CSR_MHPMEVENT19,    new Reg64("mhpmevent19", 0));
        regs_csr.put(CSR_MHPMEVENT20,    new Reg64("mhpmevent20", 0));
        regs_csr.put(CSR_MHPMEVENT21,    new Reg64("mhpmevent21", 0));
        regs_csr.put(CSR_MHPMEVENT22,    new Reg64("mhpmevent22", 0));
        regs_csr.put(CSR_MHPMEVENT23,    new Reg64("mhpmevent23", 0));
        regs_csr.put(CSR_MHPMEVENT24,    new Reg64("mhpmevent24", 0));
        regs_csr.put(CSR_MHPMEVENT25,    new Reg64("mhpmevent25", 0));
        regs_csr.put(CSR_MHPMEVENT26,    new Reg64("mhpmevent26", 0));
        regs_csr.put(CSR_MHPMEVENT27,    new Reg64("mhpmevent27", 0));
        regs_csr.put(CSR_MHPMEVENT28,    new Reg64("mhpmevent28", 0));
        regs_csr.put(CSR_MHPMEVENT29,    new Reg64("mhpmevent29", 0));
        regs_csr.put(CSR_MHPMEVENT30,    new Reg64("mhpmevent30", 0));
        regs_csr.put(CSR_MHPMEVENT31,    new Reg64("mhpmevent31", 0));

        //Debug/Trace Registers (shared with Debug Mode)
        regs_csr.put(CSR_TSELECT,        new Reg64("tselect", 0));
        regs_csr.put(CSR_TDATA1,         new Reg64("tdata1", 0));
        regs_csr.put(CSR_TDATA2,         new Reg64("tdata2", 0));
        regs_csr.put(CSR_TDATA3,         new Reg64("tdata3", 0));

        //Debug Mode Registers
        regs_csr.put(CSR_DCSR,           new Reg64("dcsr", 0));
        regs_csr.put(CSR_DPC,            new Reg64("dpc", 0));
        regs_csr.put(CSR_DSCRATCH,       new Reg64("dscratch", 0));
    }

    @Override
    public Reg64 getReg(int n) {
        return regs_csr.get(n);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        int[] n = {
                CSR_MSTATUS, CSR_MISA, CSR_MEDELEG, CSR_MIDELEG,
                CSR_MIE, CSR_MTVEC, CSR_MCOUNTEREN, 0xffff,
                CSR_MSCRATCH, CSR_MEPC, CSR_MCAUSE, CSR_MTVAL,
                CSR_MIP, 0xffff, 0xffff, 0xffff,
        };

        for (int i = 0; i < n.length; i++) {
            if (i % 4 == 0)
                b.append("  ");

            if (n[i] != 0xffff)
                b.append(String.format("%3s: %08x, ",
                        getReg(n[i]).getName(), getReg(n[i]).getValue()));

            if (i % 4 == 3)
                b.append("\n");
        }

        return b.toString();
    }
}
