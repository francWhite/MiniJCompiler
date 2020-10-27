package ch.hslu.cobau.vsl.astStack;

public class Assignment implements Visitable {
    private final Identifier assignee;
    private final Assignable value;

    public Assignment(Identifier assignee, Assignable value) {
        this.assignee = assignee;
        this.value = value;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void visitChildren(Visitor visitor) {
        assignee.accept(visitor);
        value.accept(visitor);
    }
}
