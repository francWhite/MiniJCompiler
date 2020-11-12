package ch.hslu.cobau.vsl2.ast;

public class BaseVisitor implements Visitor {

    @Override
    public void visit(Program program) {
        program.visitChildren(this);
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
    public void visit(WriteInt writeInt) {
        writeInt.visitChildren(this);
    }

    @Override
    public void visit(Text text) {  }

    @Override
    public void visit(Number number) {  }

    @Override
    public void visit(Identifier symbol) {  }
}
