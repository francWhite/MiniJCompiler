package ch.hslu.cobau.minij.ast.expression;

import ch.hslu.cobau.minij.ast.type.*;

public enum UnaryOperator {
    MINUS(new IntegerType()),
    NOT(new BooleanType()),
    PRE_INCREMENT(new IntegerType()),
    PRE_DECREMENT(new IntegerType()),
    POST_INCREMENT(new IntegerType()),
    POST_DECREMENT(new IntegerType());

    private final Type[] supportedTypes;

    UnaryOperator(Type... types) {
        supportedTypes = types;
    }

    public boolean isSupported(Type type) {
        for (Type supportedType : supportedTypes) {
            if (supportedType.equals(type)) {
                return true;
            }
        }
        return false;
    }
}
