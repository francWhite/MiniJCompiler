package ch.hslu.cobau.vsl.astStack;

public interface Visitable {
    void accept(Visitor visitor);
    void visitChildren(Visitor visitor);
}
