package ch.hslu.cobau.minij.symbol;

import ch.hslu.cobau.minij.ast.AstElement;

/**
 * Represents a symbol and maps it to an element of the AST.
 * Name and kind define the name of the symbol and the kind (see class SymbolKind).
 * astElement links to the corresponding element in the AST.
 * E.g. a symbol with name == "bla" and kind == SymbolKind.RECORD have its astElement
 * set to the corresponding element Record in the AST.
 */
public class Symbol {
    private final String name;
    private final SymbolKind kind;
    private final AstElement astElement;

    public Symbol(String name, SymbolKind kind, AstElement astElement) {
        this.name = name;
        this.kind = kind;
        this.astElement = astElement;
    }

    public String getName() {
        return name;
    }

    public SymbolKind getKind() {
        return kind;
    }

    public AstElement getAstElement() {
        return astElement;
    }
}
