package net.katsuster.ememu.test;

import net.katsuster.ememu.riscv.core.DecodeStageRVI;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        BitOpTest.class,
        IntegerExtTest.class,
        RAMTest.class,
        Bus64Test.class,
        DecodeStageRVITest.class,
})
public class AllTest {
    protected AllTest() {
        //do nothing
    }

    public static void main(String[] args) {
        JUnitCore.main(AllTest.class.getName());
    }
}
