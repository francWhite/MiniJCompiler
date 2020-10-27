package ch.hslu.cobau.lectureExample;

public interface Visitor {
    void visit(AddExpression addExpression);
    void visit(Number number);
}
