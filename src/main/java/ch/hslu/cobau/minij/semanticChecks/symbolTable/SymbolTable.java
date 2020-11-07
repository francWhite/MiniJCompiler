package ch.hslu.cobau.minij.semanticChecks.symbolTable;

import ch.hslu.cobau.minij.ast.AstElement;

import java.util.LinkedList;

public class SymbolTable {
    private final SymbolTable parentSymbolTable;
    private final AstElement astElement;
    private final LinkedList<Symbol> symbols;

    public SymbolTable(SymbolTable parentSymbolTable, AstElement currentScope, LinkedList<Symbol> symbols) {
        this.parentSymbolTable = parentSymbolTable;
        this.astElement = currentScope;
        this.symbols = symbols;
    }

    public LinkedList<Symbol> getSymbols() {
        return symbols;
    }

    public AstElement getAstElement() {
        return astElement;
    }

    public SymbolTable getParentSymbolTable() {
        return parentSymbolTable;
    }
}