package ch.hslu.cobau.vsl2.ast;

import ch.hslu.cobau.vsl2.*;

import java.util.LinkedList;
import java.util.Stack;

public class AstBuilder extends Vsl2BaseVisitor<Object> {
    private final Stack<Object> stack = new Stack<>();

    @Override
    public Object visitProgramm(Vsl2Parser.ProgrammContext ctx) {
        visitChildren(ctx);

        LinkedList<Statement> statements = new LinkedList<>();
        while (!stack.empty()) {
            statements.addFirst((Statement) stack.pop());
        }
        return new Program(statements);
    }

    @Override
    public Object visitZuweisung(Vsl2Parser.ZuweisungContext ctx) {
        visitChildren(ctx);

        Assignable assignable;
        if (ctx.BEZEICHNER(1) != null) {
            assignable = new Identifier(ctx.BEZEICHNER(1).getText());
        } else {
            assignable = (Assignable) stack.pop();
        }
        stack.push(new Assignment(new Identifier(ctx.BEZEICHNER(0).getText()), assignable));

        return null;
    }


    @Override
    public Object visitWriteInt(Vsl2Parser.WriteIntContext ctx) {
        visitChildren(ctx);

        stack.push(new WriteInt(new Identifier(ctx.BEZEICHNER().getText())));

        return null;
    }

    @Override
    public Object visitExpression(Vsl2Parser.ExpressionContext ctx) {
        visitChildren(ctx);

        if (ctx.binaryOp != null) {
            Expression right = (Expression) stack.pop();
            Expression left  = (Expression) stack.pop();
            String operator = Vsl2Parser.VOCABULARY.getSymbolicName(ctx.binaryOp.getType());
            stack.push(new BinaryExpression(left, right, operator));
        } else {
            stack.push(new Number(Integer.parseInt(ctx.ZAHL().getText())));
        }

        return null;
    }
}
