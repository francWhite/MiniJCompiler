package ch.hslu.cobau.vsl.astNoStack;

import java.util.List;

public class Number extends Expression {
    private final int value;

    public Number(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
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
