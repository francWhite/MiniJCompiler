package ch.hslu.cobau.minij;

import ch.hslu.cobau.minij.ast.constants.FalseConstant;
import ch.hslu.cobau.minij.ast.constants.IntegerConstant;
import ch.hslu.cobau.minij.ast.constants.StringConstant;
import ch.hslu.cobau.minij.ast.constants.TrueConstant;
import ch.hslu.cobau.minij.ast.entity.*;
import ch.hslu.cobau.minij.ast.expression.*;
import ch.hslu.cobau.minij.ast.statement.*;
import ch.hslu.cobau.minij.ast.type.*;
import ch.hslu.cobau.minij.semanticChecks.symbolTable.SemanticErrorListener;
import org.antlr.v4.runtime.Token;

import java.util.LinkedList;
import java.util.Stack;

public class MiniJAstBuilder extends MiniJBaseVisitor<Program> {
    // Main Stack
    private final Stack<Object> stack = new Stack<>();
    private final SemanticErrorListener semanticErrorListener;

    public MiniJAstBuilder(SemanticErrorListener semanticErrorListener) {
        this.semanticErrorListener = semanticErrorListener;
    }

    @Override
    public Program visitUnit(MiniJParser.UnitContext ctx) {
        visitChildren(ctx);

        var globals = new LinkedList<Declaration>();
        var records = new LinkedList<RecordStructure>();
        var procedures = new LinkedList<Procedure>();

        while (!stack.empty()) {
            var member = stack.pop();

            if (member.getClass() == Declaration.class) {
                globals.addFirst((Declaration) member);
            } else if (member.getClass() == RecordStructure.class) {
                records.addFirst((RecordStructure) member);
            } else if (member.getClass() == Procedure.class) {
                procedures.addFirst((Procedure) member);
            }
        }

        return new Program(globals, procedures, records);
    }

    @Override
    public Program visitDeclaration(MiniJParser.DeclarationContext ctx) {
        visitChildren(ctx);

        var identifier = (String) stack.pop();
        var type = (Type) stack.pop();
        var declaration = new Declaration(identifier, type);

        stack.push(declaration);
        return null;
    }

    @Override
    public Program visitRecord(MiniJParser.RecordContext ctx) {
        var parentStackCount = stack.size();
        visitChildren(ctx);

        var declarations = new LinkedList<Declaration>();
        while (stack.size() - 1 > parentStackCount) {            //stack.size() - 1 because the identifier is also pushed to the stack
            declarations.addFirst((Declaration) stack.pop());
        }

        var identifier = (String) stack.pop();
        var record = new RecordStructure(identifier, declarations);
        stack.push(record);

        return null;
    }

    @Override
    public Program visitProcedure(MiniJParser.ProcedureContext ctx) {
        var parentStackCount = stack.size();
        visitChildren(ctx);

        var block = (Block) stack.pop();

        var declarations = new LinkedList<Declaration>();
        while (stack.size() > parentStackCount && stack.peek().getClass() == Declaration.class) {
            declarations.addFirst((Declaration) stack.pop());
        }

        var parameters = new LinkedList<Parameter>();
        while (stack.size() > parentStackCount && stack.peek().getClass() == Parameter.class) {
            parameters.addFirst((Parameter) stack.pop());
        }

        var identifier = (String) stack.pop();

        var procedure = new Procedure(identifier, parameters, declarations, block.getStatements());
        stack.push(procedure);
        return null;
    }

    @Override
    public Program visitBlock(MiniJParser.BlockContext ctx) {
        var parentStackCount = stack.size();
        visitChildren(ctx);

        var statements = new LinkedList<Statement>();
        while (stack.size() > parentStackCount) {
            statements.addFirst((Statement) stack.pop());
        }

        var block = new Block(statements);
        stack.push(block);
        return null;
    }

    @Override
    public Program visitParameter(MiniJParser.ParameterContext ctx) {
        visitChildren(ctx);

        var identifier = (String) stack.pop();
        var type = (Type) stack.pop();
        var isByReference = ctx.REF() != null;

        var parameter = new Parameter(identifier, type, isByReference);
        stack.push(parameter);
        return null;
    }

    //Statements-----------------------------------------------------
    @Override
    public Program visitReturnStatement(MiniJParser.ReturnStatementContext ctx) {
        stack.push(new ReturnStatement());
        return null;
    }

    @Override
    public Program visitAssignment(MiniJParser.AssignmentContext ctx) {
        visitChildren(ctx);

        var right = (Expression) stack.pop();
        var left = (Expression) stack.pop();

        var assignement = new AssignmentStatement(left, right);
        stack.push(assignement);
        return null;
    }

    @Override
    public Program visitCallStatement(MiniJParser.CallStatementContext ctx) {
        var parentStackCount = stack.size();
        visitChildren(ctx);

        var parameters = new LinkedList<Expression>();
        while (stack.size() > parentStackCount && stack.peek() instanceof Expression) {
            parameters.addFirst((Expression) stack.pop());
        }

        var identifier = (String) stack.pop();
        var callStatement = new CallStatement(identifier, parameters);

        stack.push(callStatement);
        return null;
    }

    @Override
    public Program visitWhileStatement(MiniJParser.WhileStatementContext ctx) {
        visitChildren(ctx);

        var block = (Block) stack.pop();
        var expression = (Expression) stack.pop();
        var whileStatement = new WhileStatement(expression, block.getStatements());

        stack.push(whileStatement);
        return null;
    }

    @Override
    public Program visitIfStatement(MiniJParser.IfStatementContext ctx) {
        var parentStackCount = stack.size();
        visitChildren(ctx);

        Block elseBlock = null;
        if ((stack.size() - parentStackCount) % 2 != 0) {
            elseBlock = (Block) stack.pop();
        }

        IfStatement lastElseIfStatement = null;
        while (stack.size() >= parentStackCount + 4) {
            var elseIfBlock = (Block) stack.pop();
            var expression = (Expression) stack.pop();

            if (lastElseIfStatement == null) {
                lastElseIfStatement = new IfStatement(expression, elseIfBlock.getStatements(), elseBlock);
            } else {
                lastElseIfStatement = new IfStatement(expression, elseIfBlock.getStatements(), lastElseIfStatement);
            }
        }

        var ifBlock = (Block) stack.pop();
        var expression = (Expression) stack.pop();
        var ifStatement = new IfStatement(expression, ifBlock.getStatements(), lastElseIfStatement);

        stack.push(ifStatement);
        return null;
    }

    //Expressions-----------------------------------------------------
    @Override
    public Program visitExpression(MiniJParser.ExpressionContext ctx) {
        visitChildren(ctx);

        if (stack.peek() instanceof MemoryAccess && ctx.INCREMENT() != null || ctx.DECREMENT() != null) {
            var memoryAccess = (MemoryAccess) stack.pop();

            UnaryOperator unaryOperand;
            if (ctx.INCREMENT() != null) {
                unaryOperand = UnaryOperator.POST_INCREMENT;
            } else {
                unaryOperand = UnaryOperator.POST_DECREMENT;
            }

            var unaryExpression = new UnaryExpression(memoryAccess, unaryOperand);
            stack.push(unaryExpression);
        }

        if (ctx.binaryOp != null) {
            var right = (Expression) stack.pop();
            var left = (Expression) stack.pop();
            var operator = parseBinaryOperator(ctx.binaryOp);

            var binaryExpression = new BinaryExpression(left, right, operator);
            stack.push(binaryExpression);
        }

        return null;
    }

    @Override
    public Program visitUnaryExpression(MiniJParser.UnaryExpressionContext ctx) {
        visitChildren(ctx);

        var expression = (Expression) stack.pop();
        var unaryOperator = parseUnaryOperator(ctx.unaryOp);
        var unaryExpression = new UnaryExpression(expression, unaryOperator);

        stack.push(unaryExpression);
        return null;
    }

    @Override
    public Program visitMemoryAccess(MiniJParser.MemoryAccessContext ctx) {
        var parentStackCount = stack.size();
        visitChildren(ctx);

        MemoryAccess memoryAccess;

        //if no childern were added, it must be an VariableAccess
        if (stack.size() == parentStackCount) {
            memoryAccess = new VariableAccess(ctx.ID().getText());
        }
        //if the last child is a MemoryAccess, it must be an FieldAccess
        else if (stack.peek() instanceof MemoryAccess) {
            var base = (MemoryAccess) stack.pop();
            memoryAccess = new FieldAccess(base, ctx.ID().getText());
        }
        //else it must be an ArrayAccess
        else {
            var indexExpression = (Expression) stack.pop();
            var base = (MemoryAccess) stack.pop();
            memoryAccess = new ArrayAccess(base, indexExpression);
        }

        stack.push(memoryAccess);
        return null;
    }

    //Constants-------------------------------------------------------
    @Override
    public Program visitTrueConstant(MiniJParser.TrueConstantContext ctx) {
        stack.push(new TrueConstant());
        return null;
    }

    @Override
    public Program visitFalseConstant(MiniJParser.FalseConstantContext ctx) {
        stack.push(new FalseConstant());
        return null;
    }

    @Override
    public Program visitIntegerConstant(MiniJParser.IntegerConstantContext ctx) {
        var stringValue = ctx.INTEGER().getText();
        try{
            var numericValue = Long.parseLong(stringValue);
            stack.push(new IntegerConstant(numericValue));
        }catch (NumberFormatException e){
            semanticErrorListener.reportError("Integer: number is out of 64-Bit range");
            stack.push(new IntegerConstant(-1));
        }
        return null;
    }

    @Override
    public Program visitStringConstant(MiniJParser.StringConstantContext ctx) {
        var value = ctx.STRINGCONSTANT().getText();
        value = value.substring(1, value.length() - 1); //remove leading and trailing "

        stack.push(new StringConstant(value));
        return null;
    }

    //Types and identifier--------------------------------------------
    @Override
    public Program visitType(MiniJParser.TypeContext ctx) {
        visitChildren(ctx);

        var type = (Type) stack.pop();
        if (ctx.RBRACKET() != null) {
            type = new ArrayType(type);
        }

        stack.push(type);
        return null;
    }

    @Override
    public Program visitIdentifier(MiniJParser.IdentifierContext ctx) {
        stack.push(ctx.ID().getText());
        return null;
    }

    @Override
    public Program visitIntegerType(MiniJParser.IntegerTypeContext ctx) {
        stack.push(new IntegerType());
        return null;
    }

    @Override
    public Program visitBooleanType(MiniJParser.BooleanTypeContext ctx) {
        stack.push(new BooleanType());
        return null;
    }

    @Override
    public Program visitStringType(MiniJParser.StringTypeContext ctx) {
        stack.push(new StringType());
        return null;
    }

    @Override
    public Program visitRecordType(MiniJParser.RecordTypeContext ctx) {
        visitChildren(ctx);

        var identifier = (String) stack.pop();
        stack.push(new RecordType(identifier));
        return null;
    }

    private UnaryOperator parseUnaryOperator(Token operator) {
        var symbolicName = MiniJParser.VOCABULARY.getSymbolicName(operator.getType());

        return switch (symbolicName) {
            case "NOT" -> UnaryOperator.NOT;
            case "MINUS" -> UnaryOperator.MINUS;
            case "INCREMENT" -> UnaryOperator.PRE_INCREMENT;
            case "DECREMENT" -> UnaryOperator.PRE_DECREMENT;
            default -> null;
        };
    }

    private BinaryOperator parseBinaryOperator(Token operator) {
        var symbolicName = MiniJParser.VOCABULARY.getSymbolicName(operator.getType());
        return BinaryOperator.valueOf(symbolicName);
    }
}
