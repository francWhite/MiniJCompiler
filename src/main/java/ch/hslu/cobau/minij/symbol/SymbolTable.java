package ch.hslu.cobau.minij.symbol;

import ch.hslu.cobau.minij.ast.AstElement;

import java.util.HashMap;
import java.util.Map;

/**
 * This class implements a symbol table with two scope levels suitable for MiniJ.
 */
public class SymbolTable {
    public final static Object GLOBAL_SCOPE = new Object();
    private final Map<Object, Map<String, Symbol>> symbolScopes = new HashMap<>();

    /**
     * Use this method to query the symbol table.
     * @param scope      should point to the scope of the AST that is currently visited:
     *                   - SymbolTable.GLOBAL_SCOPE for the global scope that contains
     *                     global variables, procedure, and records.
     *                   - procedure instance for the scope to the corresponding procedure
     *                     that contains parameter and variable declarations.
     *                   - record instance for the scope of the corresponding record
     *                     that contains the field declarations.
     * @param identifier The name of the symbol that is being looked for.
     * @param symbolKind To restrict the lookup to a specific symbol kind. Use null, if any symbol
     *                   with given identifier should be returned. Which is typically the case
     *                   for the code generator.
     * @return A symbol instance representing the found symbol or null if no symbol is found.
     */
    public Symbol findSymbol(Object scope, String identifier, SymbolKind symbolKind) {
        Symbol symbol = findSymbolScoped(scope, identifier, symbolKind);
        if (symbol == null) {
            symbol = findSymbolScoped(GLOBAL_SCOPE, identifier, symbolKind);
        }
        return symbol;
    }

    private Symbol findSymbolScoped(Object scope, String identifier, SymbolKind symbolKind) {
        Symbol symbol = null;
        Map<String, Symbol> symbols = symbolScopes.get(scope);
        if (symbols != null && symbols.containsKey(identifier)) {
            symbol = symbols.get(identifier);
            if (symbolKind != null && symbol.getKind() != symbolKind) {
                symbol = null;
            }
        }
        return symbol;
    }

    public boolean addSymbol(Object scope, String name, SymbolKind symbolKind, AstElement astElement) {
        Map<String, Symbol> symbols = symbolScopes.get(scope);
        if (symbols == null) {
            symbols = new HashMap<>();
            symbolScopes.put(scope, symbols);
        }
        if (symbols.containsKey(name)) {
            return false;
        } else {
            symbols.put(name, new Symbol(name, symbolKind, astElement));
            return true;
        }
    }
}
