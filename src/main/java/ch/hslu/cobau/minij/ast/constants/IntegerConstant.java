package ch.hslu.cobau.minij.ast.constants;

import ch.hslu.cobau.minij.ast.AstVisitor;

public class IntegerConstant extends Constant {
    private final long value;

    public IntegerConstant(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public void accept(AstVisitor astVisitor) {
        astVisitor.visit(this);
    }
}
