package ch.hslu.cobau.minij.ast.entity;

import ch.hslu.cobau.minij.ast.AstVisitor;
import ch.hslu.cobau.minij.ast.type.Type;

public class Parameter extends Declaration {
    private final boolean byReference;

    public Parameter(String identifier, Type type, boolean byReference) {
        super(identifier, type);

        this.byReference = byReference;
    }

    public boolean isByReference() {
        return byReference;
    }

    @Override
    public void accept(AstVisitor astVisitor) {
        astVisitor.visit(this);
    }
}
