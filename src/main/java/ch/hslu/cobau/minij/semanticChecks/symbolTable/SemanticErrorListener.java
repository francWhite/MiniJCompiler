package ch.hslu.cobau.minij.semanticChecks.symbolTable;

import java.util.LinkedList;

public class SemanticErrorListener {
    private final LinkedList<Error> errors = new LinkedList<>();

    public void reportError(String errorMessage){
        var error = new Error(errorMessage);
        errors.add(error);
    }

    public LinkedList<Error> getErrors(){
        return errors;
    }

    public boolean hasErrors(){
        return !errors.isEmpty();
    }
}

