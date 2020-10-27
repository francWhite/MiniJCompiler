package ch.hslu.cobau.vsl;

import ch.hslu.cobau.vsl.astStack.AstBuilder;
import ch.hslu.cobau.vsl.astStack.CountVisitor;
import ch.hslu.cobau.vsl.astStack.Programm;
import org.antlr.v4.runtime.*;

import java.io.IOException;

public class VslCompiler {
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

    public static void main(String[] args) throws IOException {    
        // initialize compiler
        CharStream charStream;
        if (args.length > 0) {
            charStream = CharStreams.fromFileName(args[0]);
        } else {
            charStream = CharStreams.fromStream(System.in);
        }
        
        VslLexer vslLexer = new VslLexer(charStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(vslLexer);
        VslParser vslParser = new VslParser(commonTokenStream);
        
        EnhancedConsoleErrorListener errorListener = new EnhancedConsoleErrorListener();
        vslParser.removeErrorListeners();
        vslParser.addErrorListener(errorListener);

        // start parsing at outermost level
        VslParser.ProgrammContext programContext = vslParser.programm();

        AstBuilder astBuilder = new AstBuilder();
        Programm programm = (Programm) astBuilder.visit(programContext);

        CountVisitor countVisitor = new CountVisitor();
        programm.accept(countVisitor);
        System.out.println(countVisitor.getCount());

        System.exit(errorListener.hasErrors() ? 1 : 0);
    }
}
