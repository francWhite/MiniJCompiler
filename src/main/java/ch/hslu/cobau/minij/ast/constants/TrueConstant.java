package ch.hslu.cobau.minij.ast.constants;

import ch.hslu.cobau.minij.ast.AstVisitor;

public class TrueConstant extends Constant {
    @Override
    public void accept(AstVisitor astVisitor) {
        astVisitor.visit(this);
    }
}
