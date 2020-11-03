package ch.hslu.cobau.minij.ast.entity;

import ch.hslu.cobau.minij.ast.AstVisitor;
import ch.hslu.cobau.minij.ast.statement.Block;
import ch.hslu.cobau.minij.ast.statement.Statement;
import org.antlr.v4.codegen.model.decl.Decl;

import java.util.List;
import java.util.Objects;

public class Procedure extends Block {
    private final String identifier;
    private final List<Parameter> formalParameters;
    private final List<Declaration> declarations;

    public Procedure(String identifier, List<Parameter> formalParameters, List<Declaration> declarations, List<Statement> statements) {
        super(statements);
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(declarations);
        Objects.requireNonNull(formalParameters);

        this.identifier = identifier;
        this.declarations = declarations;
        this.formalParameters = formalParameters;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<Parameter> getFormalParameters() {
        return formalParameters;
    }

    public List<Declaration> getDeclarations(){
        return declarations;
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
