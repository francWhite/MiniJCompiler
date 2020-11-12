package ch.hslu.cobau.vsl2;

import ch.hslu.cobau.vsl2.ast.AstBuilder;
import ch.hslu.cobau.vsl2.ast.Program;
import ch.hslu.cobau.vsl2.code.ProgramGenerator;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.io.IOException;

public class Vsl2Compiler {
    private static class EnhancedConsoleErrorListener extends ConsoleErrorListener {
        private boolean errors;

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
            errors = true;
        }

        public boolean hasErrors() {
            return errors;
        }
    }

    public static void main(String[] args) {
        // here comes the input for the vsl2 compiler
        String input =
            "PROGRAM Bla\n" +
            "BEGIN\n" +
            "  x := 1;\n" +
            "  y := 1 + 2;\n" +
            "  z := 1 + 2 * 3;\n" +
            "  writeInt z;\n" +
            "END.\n";

        // initialize compiler
        CharStream charStream = CharStreams.fromString(input);
        Vsl2Lexer vsl2Lexer = new Vsl2Lexer(charStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(vsl2Lexer);
        Vsl2Parser vsl2Parser = new Vsl2Parser(commonTokenStream);

        // add enhanced error listener (that sets flag upon first error)
        EnhancedConsoleErrorListener errorListener = new EnhancedConsoleErrorListener();
        vsl2Parser.removeErrorListeners();
        vsl2Parser.addErrorListener(errorListener);

        // start parsing at outermost level
        Vsl2Parser.ProgrammContext programContext = vsl2Parser.programm();

        // build the ast
        AstBuilder astBuilder = new AstBuilder();
        Program program = (Program) astBuilder.visit(programContext);

        // generate the programm code
        ProgramGenerator programGenerator = new ProgramGenerator();
        program.accept(programGenerator);

        // output only if there are no errors, otherwise exit with status "1"
        if (!errorListener.hasErrors()) {
            System.out.println(programGenerator.getCode());
        } else {
            System.exit(1);
        }
    }
}
