package ch.hslu.cobau.minij.semanticChecks.symbolTable;

import ch.hslu.cobau.minij.ast.AstElement;

import java.util.LinkedList;

public class SymbolTable {
    private final SymbolTable parentSymbolTable;
    private final AstElement currentScope;
    private final LinkedList<Symbol> symbols;

    public SymbolTable(SymbolTable parentSymbolTable, AstElement currentScope, LinkedList<Symbol> symbols) {
        this.parentSymbolTable = parentSymbolTable;
        this.currentScope = currentScope;
        this.symbols = symbols;
    }

    public LinkedList<Symbol> getSymbols() {
        return symbols;
    }
}