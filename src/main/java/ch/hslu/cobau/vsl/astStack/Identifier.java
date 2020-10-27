package ch.hslu.cobau.vsl.astStack;

public class Identifier implements Assignable, Visitable {
    private String name;

    public Identifier(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void visitChildren(Visitor visitor) {
    }
}
