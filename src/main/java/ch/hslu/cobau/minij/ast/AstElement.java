package ch.hslu.cobau.minij.ast;

public abstract class AstElement {
    public abstract void accept(AstVisitor astVisitor);
    public void visitChildren(AstVisitor astVisitor) { }
}
