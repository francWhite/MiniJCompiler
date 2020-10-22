package ch.hslu.cobau.minij.ast.statement;

import ch.hslu.cobau.minij.ast.AstVisitor;
import ch.hslu.cobau.minij.ast.expression.Expression;

import java.util.List;
import java.util.Objects;

public class IfStatement extends Block {
    private final Expression expression;
    private final Block elseBlock;

    public IfStatement(Expression expression, List<Statement> statements, Block elseBlock) {
        super(statements);
        Objects.requireNonNull(expression);

        this.expression = expression;
        this.elseBlock = elseBlock;
    }

    public Expression getExpression() {
        return expression;
    }

    public Block getElseBlock() {
        return elseBlock;
    }

    @Override
    public void accept(AstVisitor astVisitor) {
        astVisitor.visit(this);
    }

    @Override
    public void visitChildren(AstVisitor astVisitor) {
        expression.accept(astVisitor); // expression
        super.visitChildren(astVisitor); // statements in "if"-branch
        if (elseBlock != null) {
            elseBlock.accept(astVisitor);    // statements in "else"-branch
        }
    }
}
