package ch.hslu.cobau.vsl2.ast;

public class WriteInt extends Statement {
    private final Identifier value;

    public WriteInt(Identifier value) {
        this.value = value;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void visitChildren(Visitor visitor) {
        value.accept(visitor);
    }

    public Identifier getValue() {
        return value;
    }
}
