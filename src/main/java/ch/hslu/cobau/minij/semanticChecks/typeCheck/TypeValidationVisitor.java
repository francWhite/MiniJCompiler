package ch.hslu.cobau.minij.semanticChecks.typeCheck;

import ch.hslu.cobau.minij.EnhancedConsoleErrorListener;
import ch.hslu.cobau.minij.ast.BaseAstVisitor;
import ch.hslu.cobau.minij.ast.constants.FalseConstant;
import ch.hslu.cobau.minij.ast.constants.IntegerConstant;
import ch.hslu.cobau.minij.ast.constants.StringConstant;
import ch.hslu.cobau.minij.ast.constants.TrueConstant;
import ch.hslu.cobau.minij.ast.entity.Declaration;
import ch.hslu.cobau.minij.ast.entity.Procedure;
import ch.hslu.cobau.minij.ast.expression.FieldAccess;
import ch.hslu.cobau.minij.ast.expression.VariableAccess;
import ch.hslu.cobau.minij.ast.statement.AssignmentStatement;
import ch.hslu.cobau.minij.ast.statement.CallStatement;
import ch.hslu.cobau.minij.ast.type.*;
import ch.hslu.cobau.minij.semanticChecks.symbolTable.Symbol;
import ch.hslu.cobau.minij.semanticChecks.symbolTable.SymbolTable;

import java.util.*;

public class TypeValidationVisitor extends BaseAstVisitor {
    private final EnhancedConsoleErrorListener errorListener;
    private final Stack<Object> stack = new Stack<>();

    private final LinkedList<SymbolTable> symbolTables;
    private SymbolTable currentScopeSymoblTable;

    private final Set<String> builtInMethods = new HashSet<>(Arrays.asList("writeInt", "readInt", "writeChar", "readChar"));

    public TypeValidationVisitor(EnhancedConsoleErrorListener errorListener, LinkedList<SymbolTable> symbolTables) {
        this.errorListener = errorListener;
        this.symbolTables = symbolTables;
    }

    @Override
    public void visit(Procedure procedure) {
        currentScopeSymoblTable = symbolTables
                .stream()
                .filter(st -> st.getAstElement().equals(procedure))
                .findFirst()
                .get();

        if (procedure.getIdentifier().equals("main") && !procedure.getFormalParameters().isEmpty()) {
            errorListener.semanticError("Main mustn't contain parameters");
        }

        super.visit(procedure);
    }

    @Override
    public void visit(Declaration declaration) {
        if (declaration.getType() instanceof RecordType) {
            var recordType = (RecordType) declaration.getType();
            var symbol = getSymbolOfParent(recordType.getIdentifier());
            if (symbol.isEmpty()) {
                errorListener.semanticError("Identifier '" + declaration.getIdentifier() + "' doesnt exists");
            }
        }
    }

    @Override
    public void visit(AssignmentStatement assignment) {
        assignment.visitChildren(this);

        var rightType = (Type) stack.pop();
        var leftType = (Type) stack.pop();

        if (rightType.getClass() != leftType.getClass()) {
            errorListener.semanticError("Assignment: types '" + leftType.getClass() + "' and '" + rightType.getClass() + "' doesnt match");
        }
    }


    @Override
    public void visit(CallStatement callStatement) {
        callStatement.visitChildren(this);

        var callParameters = callStatement.getParameters();
        if (builtInMethods.contains(callStatement.getIdentifier())) {
            if (callParameters.size() != 1) {
                errorListener.semanticError("Method '" + callStatement.getIdentifier() + "' takes only 1 parameter, but " + callParameters.size() + " are passed");
            }
            return;
        }

        var procedureSymbol = getSymbolOfParent(callStatement.getIdentifier());
        if (procedureSymbol.isEmpty()) {
            errorListener.semanticError("Method '" + callStatement.getIdentifier() + "' doesnt exist");
            return;
        }

        var actualProcedure = (Procedure) procedureSymbol.get().getEntity();
        if (callParameters.size() != actualProcedure.getFormalParameters().size()) {
            errorListener.semanticError("Method '" + actualProcedure.getIdentifier() + "' takes only " + actualProcedure.getFormalParameters().size() + " parameters, but " + callParameters.size() + " are passed");
        }
    }


    @Override
    public void visit(FieldAccess fieldAccess) {
        fieldAccess.visitChildren(this);
    }


    @Override
    public void visit(VariableAccess variableAccess) {
        var symbol = getSymbol(variableAccess.getIdentifier());
        if (symbol.isEmpty()) {
            errorListener.semanticError("Identifier '" + variableAccess.getIdentifier() + "' doesnt exists");
            stack.push(new InvalidType());
        } else {
            stack.push(symbol.get().getType());
        }
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
    public void visit(FalseConstant falseConstant) {
        stack.push(new BooleanType());
    }

    @Override
    public void visit(TrueConstant trueConstant) {
        stack.push(new BooleanType());
    }

    private Optional<Symbol> getSymbol(String identifier) {
        var currentScopeSymbol = currentScopeSymoblTable
                .getSymbols()
                .stream()
                .filter(s -> s.getIdentifier().equals(identifier))
                .findFirst();

        if (currentScopeSymbol.isEmpty()) {
            return getSymbolOfParent(identifier);
        } else {
            return currentScopeSymbol;
        }
    }

    private Optional<Symbol> getSymbolOfParent(String identifier) {
        return currentScopeSymoblTable
                .getParentSymbolTable()
                .getSymbols()
                .stream()
                .filter(s -> s.getIdentifier().equals(identifier))
                .findFirst();
    }
}
