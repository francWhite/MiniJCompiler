package ch.hslu.cobau.minij.ast.statement;

import ch.hslu.cobau.minij.ast.AstVisitor;
import ch.hslu.cobau.minij.ast.expression.Expression;

import java.util.List;
import java.util.Objects;

public class CallStatement extends Statement {
    private final String identifier;
    private final List<Expression> actualParameters;

    public CallStatement(String identifier, List<Expression> actualParameters) {
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(actualParameters);

        this.identifier = identifier;
        this.actualParameters = actualParameters;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<Expression> getParameters() {
        return actualParameters;
    }

    @Override
    public void accept(AstVisitor astVisitor) {
        astVisitor.visit(this);
    }

    @Override
    public void visitChildren(AstVisitor astVisitor) {
        actualParameters.forEach(actualParameter -> actualParameter.accept(astVisitor));
    }
}
