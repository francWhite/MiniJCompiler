package ch.hslu.cobau.vsl.astNoStack;

import java.util.List;

public class Identifier implements Visitable, Assignable {
    private final String name;

    public Identifier(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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
