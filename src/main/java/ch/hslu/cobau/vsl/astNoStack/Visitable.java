package ch.hslu.cobau.vsl.astNoStack;

public interface Visitable {
    Object accept(Visitor visitor);
    Object visitChildren(Visitor visitor);
}
