package ch.hslu.cobau.minij.ast.type;

public class InvalidType extends Type {

    @Override
    public boolean equals(Object other) {
        return other != null && other.getClass() == InvalidType.class;
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "invalid";
    }
}
