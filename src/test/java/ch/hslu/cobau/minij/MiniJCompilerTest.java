package ch.hslu.cobau.minij;


import org.junit.Test;

import java.io.IOException;

public class MiniJCompilerTest {
    @Test
    public void someTest() throws IOException {
        MiniJCompiler.main(new String[]{"D:\\Temp\\Test.txt"});
    }
}
