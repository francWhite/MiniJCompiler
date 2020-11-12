package ch.hslu.cobau.vsl2.ast;

public interface Visitable {
    void accept(Visitor visitor);
    void visitChildren(Visitor visitor);
}
