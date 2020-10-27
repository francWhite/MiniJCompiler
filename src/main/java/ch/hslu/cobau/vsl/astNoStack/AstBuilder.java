package ch.hslu.cobau.vsl.astNoStack;

import ch.hslu.cobau.vsl.VslBaseVisitor;
import ch.hslu.cobau.vsl.VslParser;

import java.util.LinkedList;
import java.util.List;

public class AstBuilder extends VslBaseVisitor<Object> {

    @Override
    public Object visitProgramm(VslParser.ProgrammContext ctx) {
        List<Assignment> assignments = new LinkedList<>();
        for(Object object : ((List<Object>) visitChildren(ctx))) {
            assignments.add((Assignment) object);
        }
        return new Programm(assignments);
    }

    @Override
    public Object visitZuweisung(VslParser.ZuweisungContext ctx) {
        Assignable assignable;
        if (ctx.BEZEICHNER(1) != null) {
            assignable = new Identifier(ctx.BEZEICHNER(1).getText());
        } else if (ctx.STRING() != null) {
            assignable = new Text(ctx.STRING().getText());
        } else {
            assignable = (Assignable) ((List<Object>) visitChildren(ctx)).get(0);
        }
        return new Assignment(new Identifier(ctx.BEZEICHNER(0).getText()), assignable);
    }

    @Override
    public Object visitExpression(VslParser.ExpressionContext ctx) {
        if (ctx.binaryOp != null) {
            List<Object> expressions = (List<Object>) visitChildren(ctx);
            String operator = VslParser.VOCABULARY.getSymbolicName(ctx.binaryOp.getType());
            return new BinaryExpression((Expression) expressions.get(0), (Expression) expressions.get(1), operator);
        } else {
            return new Number(Integer.parseInt(ctx.ZAHL().getText()));
        }
    }

    @Override
    protected Object aggregateResult(Object aggregate, Object nextResult) {
        // aggregate results into a list
        if (aggregate == null) {
            aggregate = new LinkedList<>();
        }
        if (nextResult != null) {
            ((List<Object>)aggregate).add(nextResult);
        }
        return aggregate;
    }
}
