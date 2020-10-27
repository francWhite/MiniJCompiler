package ch.hslu.cobau.vsl.astNoStack;

import java.util.List;

public class BaseVisitor implements Visitor {

    @Override
    public Object visit(Programm programm) {
        return programm.visitChildren(this);
    }

    @Override
    public Object visit(BinaryExpression expression) {
        return expression.visitChildren(this);
    }

    @Override
    public Object visit(Assignment assignment) {
        return assignment.visitChildren(this);
    }

    @Override
    public Object visit(Text text) { return null; }

    @Override
    public Object visit(Number number) { return null; }

    @Override
    public Object visit(Identifier symbol) { return null; }
}
