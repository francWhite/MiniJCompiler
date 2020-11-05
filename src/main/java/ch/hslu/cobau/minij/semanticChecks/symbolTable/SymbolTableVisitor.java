package ch.hslu.cobau.minij.semanticChecks.symbolTable;

import ch.hslu.cobau.minij.ast.BaseAstVisitor;
import ch.hslu.cobau.minij.ast.entity.Procedure;
import ch.hslu.cobau.minij.ast.entity.Program;

import java.util.LinkedList;

public class SymbolTableVisitor extends BaseAstVisitor {
    private final LinkedList<SymbolTable> symbolTables = new LinkedList<>();
    private final SemanticErrorListener errorListener;

    public SymbolTableVisitor(SemanticErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public LinkedList<SymbolTable> getSymbolTables() {
        return symbolTables;
    }

    @Override
    public void visit(Program program) {
        var symbols = new LinkedList<Symbol>();
        for (var declaration : program.getGlobals()) {
            var symbol = new Symbol(declaration.getIdentifier(), declaration, declaration.getType());

            if (validateSymbol(symbol, symbols, new LinkedList<>())) {
                symbols.add(symbol);
            }
        }

        var symbolTable = new SymbolTable(null, program, symbols);
        symbolTables.add(symbolTable);

        program.visitChildren(this);
    }

    @Override
    public void visit(Procedure procedure) {
        var parentSymbolTable = symbolTables.getFirst();
        var symbols = new LinkedList<Symbol>();

        //Parameter Symbols
        for (var parameter : procedure.getFormalParameters()) {
            var symbol = new Symbol(parameter.getIdentifier(), parameter, parameter.getType());

            if (validateSymbol(symbol, symbols, parentSymbolTable.getSymbols())) {
                symbols.add(symbol);
            }
        }

        //Declaration Symbols
        for (var declaration : procedure.getDeclarations()) {
            var symbol = new Symbol(declaration.getIdentifier(), declaration, declaration.getType());

            if (validateSymbol(symbol, symbols, parentSymbolTable.getSymbols())) {
                symbols.add(symbol);
            }
        }

        //Procedure Symbol
        var procedureSymbol = new Symbol(procedure.getIdentifier(), procedure, null);
        if (validateSymbol(procedureSymbol, parentSymbolTable.getSymbols(), new LinkedList<>())){
            parentSymbolTable.getSymbols().add(procedureSymbol);
        }

        var symbolTable = new SymbolTable(parentSymbolTable, procedure, symbols);

        symbolTables.add(symbolTable);
    }

    private boolean validateSymbol(Symbol symbol, LinkedList<Symbol> existingSymbols, LinkedList<Symbol> existingSymbolsParent){
        var alreadyExitsInCurrentScope = existingSymbols
                .stream()
                .anyMatch(s -> s.getIdentifier().equals(symbol.getIdentifier()));

        var alreadyExitsInParentScope = existingSymbolsParent
                .stream()
                .anyMatch(s -> s.getIdentifier().equals(symbol.getIdentifier()));

        if (alreadyExitsInCurrentScope || alreadyExitsInParentScope) {
            errorListener.reportError("Symbol '" + symbol.getIdentifier() + "' of type '"+symbol.getEntity().getClass().getSimpleName()+"' already exists");
            return false;
        }

        return true;
    }
}
