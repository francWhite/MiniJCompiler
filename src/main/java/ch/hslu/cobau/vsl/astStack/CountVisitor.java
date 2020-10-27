package ch.hslu.cobau.vsl.astStack;

public class CountVisitor extends BaseVisitor {
    private int count;

    public int getCount() {
        return count;
    }

    @Override
    public void visit(Number number) {
        count++;
    }
}
