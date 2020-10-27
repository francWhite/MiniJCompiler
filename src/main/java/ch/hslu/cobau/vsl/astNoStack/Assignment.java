package ch.hslu.cobau.vsl.astNoStack;

import java.util.Arrays;

public class Assignment implements Visitable {
    private final Identifier assignee;
    private final Assignable value;

    public Assignment(Identifier assignee, Assignable value) {
        this.assignee = assignee;
        this.value = value;
    }

    @Override
    public Object accept(Visitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Object visitChildren(Visitor visitor) {
        return Arrays.asList(assignee.accept(visitor), value.accept(visitor));
    }
}
