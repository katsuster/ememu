package net.katsuster.ememu.generic;

import java.io.*;

public abstract class AbstractBoard implements Board {
    @Override
    public abstract InputStream getUARTInputStream(int index);

    @Override
    public abstract void setUARTInputStream(int index, InputStream is);

    @Override
    public abstract OutputStream getUARTOutputStream(int index);

    @Override
    public abstract void setUARTOutputStream(int index, OutputStream os);

    @Override
    public void setup() {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
