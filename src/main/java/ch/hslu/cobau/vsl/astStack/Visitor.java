package ch.hslu.cobau.vsl.astStack;

public interface Visitor {
    void visit(Programm programm);
    void visit(BinaryExpression expression);
    void visit(Number number);
    void visit(Identifier symbol);
    void visit(Assignment assignment);
    void visit(Text text);
}
