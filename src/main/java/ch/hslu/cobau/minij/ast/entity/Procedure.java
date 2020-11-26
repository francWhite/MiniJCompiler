package ch.hslu.cobau.minij.ast.entity;

import ch.hslu.cobau.minij.ast.AstVisitor;
import ch.hslu.cobau.minij.ast.statement.Block;
import ch.hslu.cobau.minij.ast.statement.Statement;

import java.util.List;
import java.util.Objects;

public class Procedure extends Block {
    private final String identifier;
    private final List<Declaration> formalParameters;

    public Procedure(String identifier, List<Declaration> formalParameters, List<Statement> statements) {
        super(statements);
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(formalParameters);

        this.identifier = identifier;
        this.formalParameters = formalParameters;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<Declaration> getFormalParameters() {
        return formalParameters;
    }

    public void accept(AstVisitor astVisitor) {
        astVisitor.visit(this);
    }

    @Override
    public void visitChildren(AstVisitor astVisitor) {
        formalParameters.forEach(parameter -> parameter.accept(astVisitor));
        super.visitChildren(astVisitor); // statements
    }
}
