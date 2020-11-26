package ch.hslu.cobau.minij.ast.expression;

import ch.hslu.cobau.minij.ast.type.*;

public enum BinaryOperator {
    PLUS(false, false, new IntegerType(), new StringType()),
    MINUS(false, false, new IntegerType()),
    TIMES(false, false, new IntegerType()),
    DIV(false, false, new IntegerType()),
    MOD(false, false, new IntegerType()),
    EQUAL(true, false, new IntegerType(), new BooleanType(), new StringType()),
    UNEQUAL(true, false, new IntegerType(), new BooleanType(), new StringType()),
    LESSER(true, false, new IntegerType(), new StringType()),
    LESSER_EQ(true, false, new IntegerType(), new StringType()),
    GREATER(true, false, new IntegerType(), new StringType()),
    GREATER_EQ(true, false, new IntegerType(), new StringType()),
    AND(false, true, new BooleanType(), new BooleanType()),
    OR(false, true, new BooleanType(), new BooleanType());

    private final Type[] supportedTypes;
    private final boolean isComparator;
    private final boolean isBooleanOperation;

    BinaryOperator(boolean isComparator, boolean isBooleanOperation, Type... types) {
        this.isComparator = isComparator;
        this.isBooleanOperation = isBooleanOperation;
        supportedTypes = types;
    }

    public boolean isComparator() {
        return isComparator;
    }

    public boolean isBooleanOperation() {
        return isBooleanOperation;
    }

    public boolean isTypeSupported(Type type) {
        for (Type supportedType : supportedTypes) {
            if (supportedType.equals(type)) {
                return true;
            }
        }
        return false;
    }
}
