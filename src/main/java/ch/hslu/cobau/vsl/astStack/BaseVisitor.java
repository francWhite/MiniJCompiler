package ch.hslu.cobau.vsl.astStack;

public class BaseVisitor implements Visitor {

    @Override
    public void visit(Programm programm) {
        programm.visitChildren(this);
    }

    @Override
    public void visit(BinaryExpression expression) {
        expression.visitChildren(this);
    }

    @Override
    public void visit(Assignment assignment) {
        assignment.visitChildren(this);
    }

    @Override
    public void visit(Text text) {  }

    @Override
    public void visit(Number number) {  }

    @Override
    public void visit(Identifier symbol) {  }
}
