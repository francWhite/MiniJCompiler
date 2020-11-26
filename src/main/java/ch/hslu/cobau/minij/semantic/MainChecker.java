package ch.hslu.cobau.minij.semantic;

import ch.hslu.cobau.minij.MiniJCompiler;
import ch.hslu.cobau.minij.ast.BaseAstVisitor;
import ch.hslu.cobau.minij.ast.entity.Procedure;

/**
 * Check whether main class has zero arguments.
 */
public class MainChecker extends BaseAstVisitor {
    private static final String MAIN_PROCEDURE_NAME = "main";

    private final MiniJCompiler.EnhancedConsoleErrorListener errorListener;

    public MainChecker(MiniJCompiler.EnhancedConsoleErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    @Override
    public void visit(Procedure procedure) {
        if (MAIN_PROCEDURE_NAME.equals(procedure.getIdentifier()) && procedure.getFormalParameters().size() != 0) {
            errorListener.semanticError("main procedure must not have any arguments");
        }
    }
}
