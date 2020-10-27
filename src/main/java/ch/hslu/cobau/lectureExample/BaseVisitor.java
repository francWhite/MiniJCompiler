package ch.hslu.cobau.lectureExample;

public class BaseVisitor implements Visitor {
    @Override
    public void visit(AddExpression addExpression) {
        addExpression.visitChildren(this);
    }

    @Override
    public void visit(Number number) {
        number.visitChildren(this);
    }
}
