package ch.hslu.cobau.vsl.astStack;

public class BinaryExpression extends Expression {
    private final Expression left;
    private final Expression right;
    private final String operator;

    public BinaryExpression(Expression left, Expression right, String operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
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
