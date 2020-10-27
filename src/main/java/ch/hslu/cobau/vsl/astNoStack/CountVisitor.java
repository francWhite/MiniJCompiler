package ch.hslu.cobau.vsl.astNoStack;

public class CountVisitor extends BaseVisitor {
    private int count;

    public int getCount() {
        return count;
    }

    @Override
    public Object visit(Number number) {
        count++;
        return super.visit(number);
    }
}
