package ch.hslu.cobau.vsl2.ast;

public interface Visitor {
    void visit(Program program);
    void visit(BinaryExpression expression);
    void visit(Number number);
    void visit(Identifier symbol);
    void visit(Assignment assignment);
    void visit(WriteInt writeInt);
    void visit(Text text);
}
