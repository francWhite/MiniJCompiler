package ch.hslu.cobau.vsl2.ast;

import java.util.LinkedList;
import java.util.List;

public class Program implements Visitable {
    private List<Statement> statements;

    public Program(List<Statement> statements) {
        this.statements = new LinkedList<>(statements);
    }

    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void visitChildren(Visitor visitor) {
        for(Statement statement : statements) {
            statement.accept(visitor);
        }
    }
}
