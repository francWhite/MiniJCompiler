package ch.hslu.cobau.minij.ast.statement;

import ch.hslu.cobau.minij.ast.AstVisitor;

public class ReturnStatement extends Statement {

    @Override
    public void accept(AstVisitor astVisitor) {
        astVisitor.visit(this);
    }
}
