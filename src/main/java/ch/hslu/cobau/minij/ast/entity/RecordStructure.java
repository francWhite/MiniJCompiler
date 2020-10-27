package ch.hslu.cobau.minij.ast.entity;

import ch.hslu.cobau.minij.ast.AstElement;
import ch.hslu.cobau.minij.ast.AstVisitor;

import java.util.List;
import java.util.Objects;

public class RecordStructure extends AstElement {
    private final String identifier;
    private final List<Declaration> declarations;

    public RecordStructure(String identifier, List<Declaration> declarations) {
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(declarations);

        this.identifier = identifier;
        this.declarations = declarations;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<Declaration> getDeclarations(){
        return declarations;
    }

    public void accept(AstVisitor astVisitor) {
        astVisitor.visit(this);
    }

    @Override
    public void visitChildren(AstVisitor astVisitor) {
        declarations.forEach(declaration -> declaration.accept(astVisitor));
    }
}
