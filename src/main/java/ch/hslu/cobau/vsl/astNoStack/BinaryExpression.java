package ch.hslu.cobau.vsl.astNoStack;

import java.util.Arrays;
import java.util.List;

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
    public Object accept(Visitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Object visitChildren(Visitor visitor) {
        return Arrays.asList(left.accept(visitor), right.accept(visitor));
    }
}
