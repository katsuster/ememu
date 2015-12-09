## ememu
'ememu' is EMbedded system EMUlator written by Java.

#### Requires
* Java 7 or later

#### Supported Systems
* ARM Versatile AB/PB
  * CPU: ARM926EJ-S
    * Supported: ARM, Thumb
    * Not yet: EE, VFP, Jazelle
  * Devices: Timer, UART, NAND
  * Not yet: Ethernet, LCD, eMMC, and so on...
  * OS: Linux 4.1.x, Linux 3.18.x, Linux 3.14.x, Linux 3.12.x
    * 3.12.x: Need to enable CONFIG_EABI if you want to use recently cross-compiler.

#### Live DEMO
You can run the ememu (applet version) on my WEB site.

http://www.katsuster.net/contents/java/ememu/ememu_applet.html


## How to use
#### Get the Cross-Compiler
Download the cross-compiler for ARM.

For example: 
* Mentor Graphics(CodeSourcery): http://www.mentor.com/embedded-software/sourcery-tools/sourcery-codebench/overview/
* Linaro: http://www.linaro.org/downloads/
  * WARNING: Linaro's toolchain glibc includes Thumb-2 instructions. The ememu does not support Thmub-2 instructions yet...
* From Scratch: http://www.katsuster.net/contents/java/ememu/ememu.html
  * NOTE: If you want to know how to build these toolchains, please see 'crosstool-builder' repository of my github.
  * https://github.com/katsuster/crosstool-builder

#### Cross-compile the Linux kernel
Set the default config of ARM Versatile.

    $ export ARCH=arm
    $ export CROSS_COMPILE=/path/to/cross-compiler/arm-2013.11/bin/arm-none-linux-gnueabi-
    $ cd linux
    $ make KBUILD_DEFCONFIG=versatile_defconfig defconfig

Change the configs as you need.

    $ make menuconfig

And build.

    $ make -j4

Linux kernel image file is created on the linux/arch/arm/boot/Image directory.

#### Cross-compile the Busybox
Set the build configs to build a static binary.

    $ export ARCH=arm
    $ export CROSS_COMPILE=/path/to/cross-compiler/arm-2013.11/bin/arm-none-linux-gnueabi-
    $ cd busybox
    $ make menuconfig
    Busybox Settings  --->
      Build Options  --->
        [*] Build BusyBox as a static binary (no shared libs)

And build.

    $ make -j4

Busybox executable file is created on the busybox/ directory.

#### Create the InitramFS Image
First, create directories as follows.
NOTE: dev/console, dev/null, dev/ttyXX are the device files.
To create device files, please see the manual of 'mknod' command.

    initramfs
    |-- bin
    |   |-- [ -> busybox
    |   |-- ] -> busybox
    ...
    |-- dev
    |   |-- console
    |   |-- null
    |   |-- tty
    ...
    |-- etc
    |-- init
    |-- lib
    |-- proc
    |-- run
    |-- sbin
    `-- sys

Next, convert and compress these directories to the InitramFS.

    $ cd initramfs
    $ find . | cpio --format=newc -o > ../initramfs.cpio
    $ cat ../initramfs.cpio | gzip  > ../initramfs.gz

#### Build the Emulator
Build the emulator using Apache Ant.

    $ cd ememu
    $ ant

JAR file is created on ememu/emu/ant/dist directory.

#### Run the Emulator
Run the emulator (console version) on the Windows Command Prompt 
or some of Linux terminal as you like.

    $ java -jar emu/ant/dist/armemu.jar linux/arch/arm/boot/Image initramfs.gz
    Exception: Reset by 'Init.'.
    loadFile: linux/arch/arm/boot/Image
    loadFile: 'linux/arch/arm/boot/Image' done, 3912832bytes.
    loadFile: initramfs.gz
    loadFile: 'initramfs.gz' done, 1113270bytes.
    ...

## Known BUGS
* The emulator has crashed when execute the thumb instructions.
For example, glibc of Linaro cross compiler includes thumb instructions.

## FAQ
Sorry, not yet...
