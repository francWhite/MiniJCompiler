package ch.hslu.cobau.minij;

import ch.hslu.cobau.minij.ast.entity.Program;
import ch.hslu.cobau.minij.semanticChecks.symbolTable.SemanticErrorListener;
import ch.hslu.cobau.minij.semanticChecks.symbolTable.SymbolTableVisitor;
import ch.hslu.cobau.minij.semanticChecks.typeCheck.TypeValidationVisitor;
import org.antlr.v4.runtime.*;

import java.io.IOException;

public class MiniJCompiler {
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

        MiniJLexer miniJLexer = new MiniJLexer(charStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(miniJLexer);
        MiniJParser miniJParser = new MiniJParser(commonTokenStream);

        EnhancedConsoleErrorListener errorListener = new EnhancedConsoleErrorListener();
        var semanticErrorListener = new SemanticErrorListener();
        miniJParser.removeErrorListeners();
        miniJParser.addErrorListener(errorListener);

        // start parsing at outermost level
        MiniJParser.UnitContext unitContext = miniJParser.unit();

        // create AST
        MiniJAstBuilder miniJAstBuilder = new MiniJAstBuilder(semanticErrorListener);
        Program program = miniJAstBuilder.visit(unitContext);

        // semantic check (milestone 3)
        var symbolTableVisitor = new SymbolTableVisitor(semanticErrorListener);
        symbolTableVisitor.visit(program);

        var symbolTables = symbolTableVisitor.getSymbolTables();
        var typeValidationVisitor = new TypeValidationVisitor(semanticErrorListener, symbolTables);
        typeValidationVisitor.visit(program);

        // code generation (milestone 4)


        // runtime and system libraries (milestone 5)
        if (errorListener.hasErrors()){
            System.exit(1);
        }

        if (semanticErrorListener.hasErrors()){
            for(var error : semanticErrorListener.getErrors()){
                System.out.println("Error: " + error.getErrorMessage());
            }
            System.exit(1);
        }

        System.exit(0);
    }
}
