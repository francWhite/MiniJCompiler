package ch.hslu.cobau.minij;

import ch.hslu.cobau.minij.ast.entity.Program;
import ch.hslu.cobau.minij.semanticChecks.symbolTable.SymbolTableVisitor;
import ch.hslu.cobau.minij.semanticChecks.typeCheck.TypeValidationVisitor;
import org.antlr.v4.runtime.*;

import java.io.IOException;

public class MiniJCompiler {

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
        miniJParser.removeErrorListeners();
        miniJParser.addErrorListener(errorListener);

        // start parsing at outermost level
        MiniJParser.UnitContext unitContext = miniJParser.unit();

        // create AST
        MiniJAstBuilder miniJAstBuilder = new MiniJAstBuilder(errorListener);
        Program program = miniJAstBuilder.visit(unitContext);

        // semantic check (milestone 3)
        var symbolTableVisitor = new SymbolTableVisitor(errorListener);
        symbolTableVisitor.visit(program);

        var symbolTables = symbolTableVisitor.getSymbolTables();
        var typeValidationVisitor = new TypeValidationVisitor(errorListener, symbolTables);
        typeValidationVisitor.visit(program);

        // code generation (milestone 4)

        // runtime and system libraries (milestone 5)
       System.exit(errorListener.hasErrors() ? 1 : 0);
    }
}
