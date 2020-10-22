package ch.hslu.cobau.minij.ast.type;

public class StringType extends Type {

    @Override
    public boolean equals(Object other) {
        return other != null && other.getClass() == StringType.class;
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "string";
    }
}
