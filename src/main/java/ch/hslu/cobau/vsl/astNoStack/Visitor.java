package ch.hslu.cobau.vsl.astNoStack;

public interface Visitor {
    Object visit(Programm programm);
    Object visit(BinaryExpression expression);
    Object visit(Number number);
    Object visit(Identifier symbol);
    Object visit(Assignment assignment);
    Object visit(Text text);
}
