package ch.hslu.cobau.minij.symbol;

import ch.hslu.cobau.minij.MiniJCompiler;
import ch.hslu.cobau.minij.ast.*;
import ch.hslu.cobau.minij.ast.entity.*;
import ch.hslu.cobau.minij.ast.statement.*;
import ch.hslu.cobau.minij.ast.type.*;

import java.util.LinkedList;
import java.util.List;

public class SymbolTableBuilder extends BaseAstVisitor {
    private final MiniJCompiler.EnhancedConsoleErrorListener errorListener;
    private final SymbolTable symbolTable = new SymbolTable();
    private Object currentScope = SymbolTable.GLOBAL_SCOPE;

    /* stores the current declaration kind */
    private SymbolKind currentSymbolKind = SymbolKind.VARIABLE; //  declarations on global scope are variables

    public SymbolTableBuilder(MiniJCompiler.EnhancedConsoleErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    @Override
    public void visit(Program program) {
        addDefaultSymbols(program);
        program.visitChildren(this);
    }

    @Override
    public void visit(Procedure procedure) {
        addSymbol(procedure.getIdentifier(), SymbolKind.PROCEDURE, procedure);
        currentSymbolKind = SymbolKind.PARAMETER;
        currentScope = procedure;
        procedure.visitChildren(this);
        currentScope = SymbolTable.GLOBAL_SCOPE;
        currentSymbolKind = null;
    }

    @Override
    public void visit(RecordStructure record) {
        addSymbol(record.getIdentifier(), SymbolKind.RECORD, record);
        SymbolKind previousSymbolKind = currentSymbolKind;
        currentSymbolKind = SymbolKind.FIELD;
        currentScope = record;
        record.visitChildren(this);
        currentScope = SymbolTable.GLOBAL_SCOPE;
        currentSymbolKind = previousSymbolKind;
    }

    @Override
    public void visit(DeclarationStatement declarationStatement) {
        SymbolKind previousSymbolKind = currentSymbolKind;
        currentSymbolKind = SymbolKind.VARIABLE;
        declarationStatement.visitChildren(this);
        currentSymbolKind = previousSymbolKind;
    }

    @Override
    public void visit(Declaration declaration) {
        addSymbol(declaration.getIdentifier(), currentSymbolKind, declaration);
    }

    private void addSymbol(String identifier, SymbolKind symbolKind, AstElement astElement) {
        boolean success = symbolTable.addSymbol(currentScope, identifier, symbolKind, astElement);
        if (!success) {
            errorListener.semanticError("error: symbol '" + identifier + "' already defined");
        }
    }

    private void addDefaultSymbols(Program program) {
        addProcedure(program, "writeInt", false);
        addProcedure(program, "readInt", true);
        addProcedure(program, "writeChar", false);
        addProcedure(program, "readChar", true);
    }

    private void addProcedure(Program program, String procedureName, boolean isReference) {
        List<Declaration> declarations = new LinkedList<>();
        declarations.add(new Declaration("value", new IntegerType(), isReference));
        program.getProcedures().add(new Procedure(procedureName, declarations, new LinkedList<>()));
    }
}
