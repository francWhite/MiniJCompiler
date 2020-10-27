package ch.hslu.cobau.lectureExample;

public interface Visitable {
    void accept(Visitor visitor);
    void visitChildren(Visitor visitor);
}
