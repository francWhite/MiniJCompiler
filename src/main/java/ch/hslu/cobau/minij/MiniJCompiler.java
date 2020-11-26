package ch.hslu.cobau.minij;

import ch.hslu.cobau.minij.ast.*;
import ch.hslu.cobau.minij.ast.entity.*;
import ch.hslu.cobau.minij.semantic.*;
import ch.hslu.cobau.minij.symbol.*;
import org.antlr.v4.runtime.*;

import java.io.IOException;

public class MiniJCompiler {
    public static class EnhancedConsoleErrorListener extends ConsoleErrorListener {
        private boolean hasErrors;

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
            hasErrors = true;
        }

        public void semanticError(String message) {
            System.err.println(message);
            hasErrors = true;
        }

        public boolean hasErrors() {
            return hasErrors;
        }
    }

    public static void main(String[] args) throws IOException {
        // initialize lexer and parser
        CharStream charStream;
        if (args.length > 0) {
            charStream = CharStreams.fromFileName(args[0]);
        } else {
            charStream = CharStreams.fromStream(System.in);
        }

        MiniJLexer miniJLexer = new MiniJLexer(charStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(miniJLexer);
        MiniJParser miniJParser = new MiniJParser(commonTokenStream);

        EnhancedConsoleErrorListener errorListener = new EnhancedConsoleErrorListener();
        miniJParser.removeErrorListeners();
        miniJParser.addErrorListener(errorListener);

        // start parsing at outermost level (milestone 2)
        MiniJParser.UnitContext unitContext = miniJParser.unit();

        // build abstract syntax tree (performs value checks for milestone 3)
        AstBuilder astBuilder = new AstBuilder(errorListener);
        unitContext.accept(astBuilder);
        Program program = astBuilder.getProgram();

        // build symbol table (performs duplicate checks for milestone 3)
        SymbolTableBuilder symbolTableBuilder = new SymbolTableBuilder(errorListener);
        program.accept(symbolTableBuilder);
        SymbolTable symbolTable = symbolTableBuilder.getSymbolTable();

        // checks for symbol existence and correct types (milestone 3)
        TypeAndExistenceChecker typeAndExistenceChecker = new TypeAndExistenceChecker(symbolTable, errorListener);
        typeAndExistenceChecker.visit(astBuilder.getProgram());

        // check main procedure (mile stone 3)
        MainChecker mainChecker = new MainChecker(errorListener);
        mainChecker.visit(astBuilder.getProgram());

        if (!errorListener.hasErrors()) {
            // TODO: Milestone 4: generate program code
        } else {
            System.exit(1);
        }
    }
}
