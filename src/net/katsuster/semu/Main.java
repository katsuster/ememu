package net.katsuster.semu;

import java.io.*;

public class Main {
    public static Word64[] loadImage(String filename) {
        int lenWords;
        Word64[] ramWords;

        try {
            File f = new File(filename);
            DataInputStream s = new DataInputStream(
                    new BufferedInputStream(new FileInputStream(f)));

            lenWords = (int)(f.length() / 8);
            ramWords = new Word64[lenWords];
            for (int i = 0; i < ramWords.length; i++) {
                ramWords[i] = new Word64(Long.reverseBytes(s.readLong()));
            }
        } catch (FileNotFoundException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }

        return ramWords;
    }

    public static void main(String[] args) {
        String filename = "C:\\Users\\katsuhiro\\Desktop\\Image";

        CPU cpu = new CPU();
        RAM<Word64> ram = new RAM<Word64>(loadImage(filename));
        Bus<Word64> bus = new Bus<Word64>();

        cpu.setSlaveBus(bus);
        bus.addSlaveCore(ram, 0xc0008000L, 0xc0800000L);

        cpu.setDisasmMode(1);
        cpu.exceptionReset("Init.");
        //tentative
        cpu.setPC(0xc0008000 + 8);
        cpu.setJumped(false);
        cpu.run();
    }
}
