package ch.hslu.cobau.vsl.astStack;

import ch.hslu.cobau.vsl.VslBaseVisitor;
import ch.hslu.cobau.vsl.VslParser;

import java.util.LinkedList;
import java.util.Stack;

public class AstBuilder extends VslBaseVisitor<Object> {
    private final Stack<Object> stack = new Stack<>();

    @Override
    public Object visitProgramm(VslParser.ProgrammContext ctx) {
        visitChildren(ctx);

        LinkedList<Assignment> assignments = new LinkedList<>();
        while (!stack.empty()) {
            assignments.addFirst((Assignment) stack.pop());
        }
        return new Programm(assignments);
    }

    @Override
    public Object visitZuweisung(VslParser.ZuweisungContext ctx) {
        visitChildren(ctx);

        Assignable assignable;
        if (ctx.BEZEICHNER(1) != null) {
            assignable = new Identifier(ctx.BEZEICHNER(1).getText());
        } else if (ctx.STRING() != null) {
            assignable = new Text(ctx.STRING().getText());
        } else {
            assignable = (Assignable) stack.pop();
        }
        stack.push(new Assignment(new Identifier(ctx.BEZEICHNER(0).getText()), assignable));

        return null;
    }

    @Override
    public Object visitExpression(VslParser.ExpressionContext ctx) {
        visitChildren(ctx);

        if (ctx.binaryOp != null) {
            Expression right = (Expression) stack.pop();
            Expression left  = (Expression) stack.pop();
            String operator = VslParser.VOCABULARY.getSymbolicName(ctx.binaryOp.getType());
            stack.push(new BinaryExpression(left, right, operator));
        } else {
            stack.push(new Number(Integer.parseInt(ctx.ZAHL().getText())));
        }

        return null;
    }
}
