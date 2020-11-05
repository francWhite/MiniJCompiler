package ch.hslu.cobau.minij.semanticChecks.symbolTable;

import ch.hslu.cobau.minij.ast.AstElement;
import ch.hslu.cobau.minij.ast.type.Type;

public class Symbol {
    private final String identifier;
    private final AstElement entity;
    private final Type type;

    public Symbol(String identifier, AstElement entity, Type type) {
        this.identifier = identifier;
        this.entity = entity;
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public AstElement getEntity() {
        return entity;
    }
}
