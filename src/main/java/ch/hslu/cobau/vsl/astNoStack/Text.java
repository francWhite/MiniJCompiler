package ch.hslu.cobau.vsl.astNoStack;

import java.util.List;

public class Text extends Expression {
    private final String value;

    public Text(String value) {
        this.value = value;
    }

    @Override
    public Object accept(Visitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Object visitChildren(Visitor visitor) {
        return List.of();
    }
}
