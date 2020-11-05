package ch.hslu.cobau.minij.semanticChecks.symbolTable;

public class Error {
    private final String errorMessage;

    public Error(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
