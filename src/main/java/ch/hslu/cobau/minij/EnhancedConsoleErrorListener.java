package ch.hslu.cobau.minij;

import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class EnhancedConsoleErrorListener extends ConsoleErrorListener {
    private boolean errors;

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
        errors = true;
    }

    public void semanticError(String errorMessage){
        System.out.println(errorMessage);
        errors = true;
    }

    public boolean hasErrors() {
        return errors;
    }
}
