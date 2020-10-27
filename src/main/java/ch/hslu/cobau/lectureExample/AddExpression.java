package ch.hslu.cobau.lectureExample;

public class AddExpression extends Expression {
    private final Expression left;
    private final Expression right;

    public AddExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void visitChildren(Visitor visitor) {
        left.accept(visitor);
        right.accept(visitor);
    }
}
