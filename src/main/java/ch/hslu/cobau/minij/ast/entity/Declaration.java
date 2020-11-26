package ch.hslu.cobau.minij.ast.entity;

import ch.hslu.cobau.minij.ast.AstElement;
import ch.hslu.cobau.minij.ast.AstVisitor;
import ch.hslu.cobau.minij.ast.type.Type;

import java.util.Objects;

public class Declaration extends AstElement {
    private final String identifier;
    private final Type type;
    private final boolean isReference;

    public Declaration(String identifier, Type type, boolean isReference) {
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(type);

        this.identifier = identifier;
        this.type = type;
        this.isReference = isReference;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Type getType() {
        return type;
    }

    public boolean isReference() {
        return isReference;
    }

    @Override
    public void accept(AstVisitor astVisitor) {
        astVisitor.visit(this);
    }
}
