package ch.hslu.cobau.lectureExample;

public class Main {
    public static void main(String[] args) {
        Expression expr = new AddExpression(new Number(34), new AddExpression(new Number(12), new Number(45)));
        EvalutationVisitor evalutationVisitor = new EvalutationVisitor();
        expr.accept(evalutationVisitor);
        System.out.println("sum: " + evalutationVisitor.getValue());

        CountVisitor countVisitor = new CountVisitor();
        expr.accept(countVisitor);
        System.out.println("count: " + countVisitor.getCount());
    }
}
