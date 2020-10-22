package ch.hslu.cobau.minij.ast.type;

public class BooleanType extends Type {
    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == BooleanType.class;
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "boolean";
    }
}
