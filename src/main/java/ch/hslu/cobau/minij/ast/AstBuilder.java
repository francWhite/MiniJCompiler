package ch.hslu.cobau.minij.ast;

import ch.hslu.cobau.minij.*;
import ch.hslu.cobau.minij.ast.constants.*;
import ch.hslu.cobau.minij.ast.entity.*;
import ch.hslu.cobau.minij.ast.expression.*;
import ch.hslu.cobau.minij.ast.statement.*;
import ch.hslu.cobau.minij.ast.type.*;

import java.util.LinkedList;
import java.util.Stack;

/**
 * Builds the abstract syntax tree (AST) for MiniJ using a stack based approach.
 * After building the tree, fetch the generated AST using getProgram().
 */
public class AstBuilder extends MiniJBaseVisitor<Object> {
    private final MiniJCompiler.EnhancedConsoleErrorListener errorListener;
    private final Stack<Declaration> declarationStack = new Stack<>();
    private final Stack<Procedure> procedureStack = new Stack<>();
    private final Stack<RecordStructure> recordStack = new Stack<>();
    private final Stack<Block> blockStack = new Stack<>();
    private final Stack<Statement> statementsStack = new Stack<>();
    private final Stack<Expression> expressionStack = new Stack<>();
    private final Stack<Type> typeStack = new Stack<>();
    private Program program;

    public AstBuilder(MiniJCompiler.EnhancedConsoleErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    /**
     * @return The root of the generated MiniJ AST.
     */
    public Program getProgram() {
        return program;
    }

    @Override
    public Object visitUnit(MiniJParser.UnitContext ctx) {
        super.visitChildren(ctx);
        program = new Program(getElements(declarationStack, 0), getElements(procedureStack, 0), getElements(recordStack, 0));
        return null;
    }

    @Override
    public Object visitProcedure(MiniJParser.ProcedureContext ctx) {
        int declarationsCount = declarationStack.size();
        int statementsCount = statementsStack.size();
        super.visitChildren(ctx);
        procedureStack.add(new Procedure(ctx.identifier().ID().getText(), getElements(declarationStack, declarationsCount), getElements(statementsStack, statementsCount)));
        return null;
    }

    @Override
    public Object visitParameter(MiniJParser.ParameterContext ctx) {
        super.visitChildren(ctx);
        declarationStack.push(new Declaration(ctx.identifier().ID().getText(), typeStack.pop(), ctx.REF() != null));
        return null;
    }

    @Override
    public Object visitRecord(MiniJParser.RecordContext ctx) {
        int declarationsCount = declarationStack.size();
        super.visitChildren(ctx);
        recordStack.push(new RecordStructure(ctx.identifier().ID().getText(), getElements(declarationStack, declarationsCount)));
        return null;
    }

    @Override
    public Object visitDeclarationStatement(MiniJParser.DeclarationStatementContext ctx) {
        super.visitChildren(ctx);
        statementsStack.push(new DeclarationStatement(declarationStack.pop()));
        return null;
    }

    @Override
    public Object visitCall(MiniJParser.CallContext ctx) {
        int expressionCount = expressionStack.size();
        super.visitChildren(ctx);
        statementsStack.push(new CallStatement(ctx.identifier().ID().getText(), getElements(expressionStack, expressionCount)));
        return null;
    }

    @Override
    public Object visitDeclaration(MiniJParser.DeclarationContext ctx) {
        super.visitChildren(ctx);
        declarationStack.push(new Declaration(ctx.identifier().ID().getText(), typeStack.pop(), false));
        return null;
    }


    @Override
    public Object visitWhileStatement(MiniJParser.WhileStatementContext ctx) {
        int statementsCount = statementsStack.size();
        super.visitChildren(ctx);
        statementsStack.push(new WhileStatement(expressionStack.pop(), getElements(statementsStack, statementsCount)));
        return null;
    }

    @Override
    public Object visitIfStatement(MiniJParser.IfStatementContext ctx) {
        int statementsCount = statementsStack.size();
        int blockCount = blockStack.size();
        super.visitChildren(ctx);

        Block elseBlock = null;
        while (blockStack.size() > blockCount) {
            if (elseBlock == null) {
                elseBlock = blockStack.pop();
            } else {
                IfStatement ifStatement = (IfStatement) blockStack.pop();
                // create new IfStatement to set new parent and elseBlock
                elseBlock = new IfStatement(ifStatement.getExpression(), ifStatement.getStatements(), elseBlock);
            }
        }

        statementsStack.push(new IfStatement(expressionStack.pop(), getElements(statementsStack, statementsCount), elseBlock));
        return null;
    }

    @Override
    public Object visitElsifClause(MiniJParser.ElsifClauseContext ctx) {
        int statementsCount = statementsStack.size();
        super.visitChildren(ctx);
        blockStack.push(new IfStatement(expressionStack.pop(), getElements(statementsStack, statementsCount), null));
        return null;
    }

    @Override
    public Object visitElseClause(MiniJParser.ElseClauseContext ctx) {
        int statementsCount = statementsStack.size();
        super.visitChildren(ctx);
        blockStack.push(new Block(getElements(statementsStack, statementsCount)));
        return null;
    }

    @Override
    public Object visitReturnStatement(MiniJParser.ReturnStatementContext ctx) {
        super.visitChildren(ctx);
        statementsStack.push(new ReturnStatement());
        return null;
    }

    @Override
    public Object visitAssignment(MiniJParser.AssignmentContext ctx) {
        super.visitChildren(ctx);
        Expression rhs = expressionStack.pop();
        statementsStack.push(new AssignmentStatement(expressionStack.pop(), rhs));
        return null;
    }

    public Object visitExpression(MiniJParser.ExpressionContext ctx) {
        super.visitChildren(ctx);
        if (ctx.binaryOp != null) {
            Expression rhs = expressionStack.pop();
            expressionStack.push(new BinaryExpression(expressionStack.pop(), rhs, BinaryOperator.valueOf(MiniJParser.VOCABULARY.getSymbolicName(ctx.binaryOp.getType()))));
        } else if (ctx.INCREMENT() != null) {
            expressionStack.push(new UnaryExpression(expressionStack.pop(), UnaryOperator.POST_INCREMENT));
        } else if (ctx.DECREMENT() != null) {
            expressionStack.push(new UnaryExpression(expressionStack.pop(), UnaryOperator.POST_DECREMENT));
        }
        return null;
    }

    @Override
    public Object visitUnaryExpression(MiniJParser.UnaryExpressionContext ctx) {
        super.visitChildren(ctx);

        String operator = MiniJParser.VOCABULARY.getSymbolicName(ctx.unaryOp.getType());
        if (operator.equals("INCREMENT") || operator.equals("DECREMENT")) {
            operator = "PRE_" + operator;
        }
        expressionStack.push(new UnaryExpression(expressionStack.pop(), UnaryOperator.valueOf(operator)));
        return null;
    }

    @Override
    public Object visitTrueConstant(MiniJParser.TrueConstantContext ctx) {
        expressionStack.push(new TrueConstant());
        return null;
    }

    @Override
    public Object visitFalseConstant(MiniJParser.FalseConstantContext ctx) {
        expressionStack.push(new FalseConstant());
        return null;
    }

    @Override
    public Object visitIntegerConstant(MiniJParser.IntegerConstantContext ctx) {
        long value;
        String inputValue = ctx.INTEGER().getText();
        try {
            value = Long.parseLong(inputValue);
        } catch (NumberFormatException e) {
            errorListener.semanticError("value '" + inputValue + "' is not a valid 64-bit integer");
            value = 0;
        }
        expressionStack.push(new IntegerConstant(value));

        return null;
    }

    @Override
    public Object visitStringConstant(MiniJParser.StringConstantContext ctx) {
        expressionStack.push(new StringConstant(ctx.STRINGCONSTANT().getText()));
        return null;
    }

    @Override
    public Object visitMemoryAccess(MiniJParser.MemoryAccessContext ctx) {
        super.visitChildren(ctx);

        if (ctx.DOT() != null) {
            expressionStack.push(new FieldAccess(expressionStack.pop(), ctx.ID().getText()));
        } else if (ctx.LBRACKET() != null) {
            Expression index = expressionStack.pop();
            expressionStack.push(new ArrayAccess((MemoryAccess) expressionStack.pop(), index));
        } else {
            expressionStack.push(new VariableAccess(ctx.ID().getText()));
        }
        return null;
    }

    @Override
    public Object visitIntegerType(MiniJParser.IntegerTypeContext ctx) {
        typeStack.push(new IntegerType());
        return null;
    }

    @Override
    public Object visitBooleanType(MiniJParser.BooleanTypeContext ctx) {
        typeStack.push(new BooleanType());
        return null;
    }

    @Override
    public Object visitStringType(MiniJParser.StringTypeContext ctx) {
        typeStack.push(new StringType());
        return null;
    }

    @Override
    public Object visitRecordType(MiniJParser.RecordTypeContext ctx) {
        typeStack.push(new RecordType(ctx.identifier().ID().getText()));
        return null;
    }

    @Override
    public Object visitType(MiniJParser.TypeContext ctx) {
        super.visitChildren(ctx);
        if (ctx.LBRACKET() != null) {
            typeStack.push(new ArrayType(typeStack.pop()));
        }
        return null;
    }

    private static <V> LinkedList<V> getElements(Stack<V> stack, int count) {
        LinkedList<V> list = new LinkedList<>();
        while (stack.size() > count) {
            list.addFirst(stack.pop());
        }
        return list;
    }
}
