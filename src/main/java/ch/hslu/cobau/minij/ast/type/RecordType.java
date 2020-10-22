package ch.hslu.cobau.minij.ast.type;

import java.util.Objects;

public class RecordType extends Type {
    private final String identifier;

    public RecordType(String identifier) {
        Objects.requireNonNull(identifier);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        RecordType that = (RecordType) other;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public String toString() {
        return "record " + identifier;
    }
}
