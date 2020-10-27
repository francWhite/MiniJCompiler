package ch.hslu.cobau.lectureExample;

import java.util.Stack;

public class EvalutationVisitor extends BaseVisitor {
    private final Stack<Integer> stack = new Stack<>();

    public int getValue() {
        return stack.peek();
    }

    @Override
    public void visit(AddExpression addExpression) {
        addExpression.visitChildren(this);
        stack.push(stack.pop() + stack.pop());
    }

    @Override
    public void visit(Number number) {
        stack.push(number.getValue());
    }
}
