package ch.hslu.cobau.minij.semantic;

import ch.hslu.cobau.minij.MiniJCompiler;
import ch.hslu.cobau.minij.ast.*;
import ch.hslu.cobau.minij.ast.constants.*;
import ch.hslu.cobau.minij.ast.entity.*;
import ch.hslu.cobau.minij.ast.expression.*;
import ch.hslu.cobau.minij.ast.statement.*;
import ch.hslu.cobau.minij.ast.type.*;
import ch.hslu.cobau.minij.symbol.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Checks the AST for type and existence errors.
 *
 * Note: This class uses the invalid type to prevent error messages
 *       due to follow-up errors. This makes this class way more
 *       complex than necessary to pass milestone 3!
 */
public class TypeAndExistenceChecker extends BaseAstVisitor {
    private final Stack<Type> stack = new Stack<>();
    private final SymbolTable symbolTable;
    private final MiniJCompiler.EnhancedConsoleErrorListener errorListener;
    private Object currentScope = SymbolTable.GLOBAL_SCOPE;

    public TypeAndExistenceChecker(SymbolTable symbolTable, MiniJCompiler.EnhancedConsoleErrorListener errorListener) {
        this.symbolTable = symbolTable;
        this.errorListener = errorListener;
    }

    public void visit(Procedure procedure) {
        currentScope = procedure;
        procedure.visitChildren(this);
        currentScope = SymbolTable.GLOBAL_SCOPE;
    }

    public void visit(RecordStructure record) {
        currentScope = record;
        record.visitChildren(this);
        currentScope = SymbolTable.GLOBAL_SCOPE;
    }

    @Override
    public void visit(Declaration declaration) {
        Type type = declaration.getType();
        if (type instanceof RecordType) {
            RecordType recordType = (RecordType) type;
            Symbol symbol = symbolTable.findSymbol(SymbolTable.GLOBAL_SCOPE, recordType.getIdentifier(), SymbolKind.RECORD);
            verify(symbol != null, "cannot find record '" + recordType.getIdentifier() + "'");
        }
    }

    @Override
    public void visit(AssignmentStatement assignmentStatement) {
        assignmentStatement.visitChildren(this);
        Type rhs = stack.pop();
        Type lhs = stack.pop();
        verify(lhs.equals(rhs) || isInvalid(lhs, rhs), "Operation performed using different types");
    }

    public void visit(FieldAccess fieldAccess) {
        fieldAccess.visitChildren(this);

        Type type = stack.pop();
        if (type instanceof RecordType) {
            RecordType recordType = (RecordType) type;
            Symbol recordSymbol = symbolTable.findSymbol(SymbolTable.GLOBAL_SCOPE, recordType.getIdentifier(), SymbolKind.RECORD);
            if (recordSymbol != null) {
                Symbol fieldSymbol = symbolTable.findSymbol(recordSymbol.getAstElement(), fieldAccess.getField(), SymbolKind.FIELD);
                if (fieldSymbol != null) {
                    Declaration declaration = (Declaration) fieldSymbol.getAstElement();
                    stack.push(declaration.getType());
                } else {
                    errorAndPushInvalid("cannot find field '" + fieldAccess.getField() + "' in record '" + recordType.getIdentifier() + "'");
                }
            } else {
                errorAndPushInvalid("cannot find record '" + recordType.getIdentifier() + "'");
            }
        } else {
            verify(type instanceof InvalidType, "cannot access fields on non-record type '" + type + "'");
            stack.push(new InvalidType());
        }

    }

    @Override
    public void visit(ArrayAccess arrayAccess) {
        arrayAccess.visitChildren(this);

        // top must be integer
        Type indexType = stack.pop();
        verify(indexType instanceof IntegerType, "array index must be of type integer");

        // followed by an array type
        Type type = stack.pop();
        if (!(type instanceof ArrayType)) {
            errorAndPushInvalid("type is not an array");
        } else {
            stack.push(((ArrayType) type).getType());
        }
    }

    @Override
    public void visit(CallStatement callStatement) {
        callStatement.visitChildren(this);

        // add element to list in reverse order (they are on the stack in reverse order)
        LinkedList<Type> actuals = new LinkedList<>();
        callStatement.getParameters().forEach(actualType -> actuals.addFirst(stack.pop()));

        Symbol symbol = symbolTable.findSymbol(SymbolTable.GLOBAL_SCOPE, callStatement.getIdentifier(), SymbolKind.PROCEDURE);
        if (symbol != null) {
            Procedure procedure = (Procedure) symbol.getAstElement();
            List<Type> formals = procedure.getFormalParameters().stream().map(Declaration::getType).collect(Collectors.toList());

            boolean equal = formals.size() == actuals.size();
            if (equal) {
                Iterator<Type> formalIt = formals.iterator();
                for (Type actual : actuals) {
                    equal &= isInvalid(actual) || formalIt.next().equals(actual);
                }
            }
            verify(equal, "method expects parameters of type " + Arrays.toString(formals.toArray())
                    + ", but was called with parameters of type " + Arrays.toString(actuals.toArray()));
        } else {
            errorListener.semanticError("cannot find procedure '" + callStatement.getIdentifier() + "'");
        }
    }

    @Override
    public void visit(IfStatement ifStatement) {
        ifStatement.visitChildren(this);
        checkConditionalExpression();
    }

    @Override
    public void visit(WhileStatement whileStatement) {
        whileStatement.visitChildren(this);
        checkConditionalExpression();
    }

    private void checkConditionalExpression() {
        Type type = stack.pop();
        verify(type instanceof BooleanType || isInvalid(type),
                "conditional expression must be of type boolean");
    }

    @Override
    public void visit(UnaryExpression expression) {
        expression.visitChildren(this);
        Type type = stack.peek(); // leave stack as is: input type is same as output type
        verify(expression.getUnaryOperator().isSupported(type) || isInvalid(type),
                "operation '" + expression.getUnaryOperator() + "' is not supported by type " + type.toString());
    }

    @Override
    public void visit(BinaryExpression expression) {
        expression.visitChildren(this);

        Type left = stack.pop();
        Type right = stack.pop();

        if (isInvalid(left, right)) {
            stack.push(new InvalidType()); // subsequent error
        } else if (!left.equals(right)) {
            errorAndPushInvalid("operation performed using incompatible types");
        } else if (!expression.getBinaryOperator().isTypeSupported(left)) {
            errorAndPushInvalid("operation '" + expression.getBinaryOperator() + "' is not supported by type " + left.toString());
        } else {
            if (expression.getBinaryOperator().isComparator()) {
                stack.push(new BooleanType());
            } else {
                stack.push(left);
            }
        }
    }

    @Override
    public void visit(VariableAccess variable) {
        variable.visitChildren(this);

        Symbol symbol = symbolTable.findSymbol(currentScope, variable.getIdentifier(), null);
        if (symbol != null && (symbol.getKind() == SymbolKind.VARIABLE || symbol.getKind() == SymbolKind.PARAMETER)) {
            Declaration declaration = (Declaration) symbol.getAstElement();
            stack.push(declaration.getType());
        } else {
            errorAndPushInvalid("cannot find variable '" + variable.getIdentifier() + "'");
        }
    }

    @Override
    public void visit(FalseConstant falseConstant) {
        stack.push(new BooleanType());
    }

    @Override
    public void visit(IntegerConstant integerConstant) {
        stack.push(new IntegerType());
    }

    @Override
    public void visit(StringConstant stringConstant) {
        stack.push(new StringType());
    }

    @Override
    public void visit(TrueConstant trueConstant) {
        stack.push(new BooleanType());
    }

    private void verify(boolean condition, String message) {
        if (!condition) {
            errorListener.semanticError(message);
        }
    }

    private void errorAndPushInvalid(String message) {
        errorListener.semanticError(message);
        stack.push(new InvalidType());
    }

    private boolean isInvalid(Type... types) {
        for(Type type : types) {
            if (type instanceof InvalidType) {
                return true;
            }
        }
        return false;
    }
}
