package ch.hslu.cobau.minij;

import org.junit.Test;

import java.io.IOException;

public class MiniJCompilerTest {

    @Test
    public void debug() throws IOException {
        MiniJCompiler.main(new String[]{"D:\\Temp\\MiniJ.txt"});
    }
}
