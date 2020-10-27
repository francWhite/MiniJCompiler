package ch.hslu.cobau.vsl.astStack;

public class Text extends Expression {
    private final String value;

    public Text(String value) {
        this.value = value;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void visitChildren(Visitor visitor) {
    }
}
