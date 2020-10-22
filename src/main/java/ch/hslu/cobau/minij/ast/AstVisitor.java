package ch.hslu.cobau.minij.ast;

import ch.hslu.cobau.minij.ast.constants.*;
import ch.hslu.cobau.minij.ast.entity.*;
import ch.hslu.cobau.minij.ast.expression.*;
import ch.hslu.cobau.minij.ast.statement.*;

public interface AstVisitor {
    void visit(Program program);
    void visit(Procedure procedure);
    void visit(Declaration declaration);
    void visit(RecordStructure record);

    void visit(IfStatement ifStatement);
    void visit(WhileStatement whileStatement);
    void visit(ReturnStatement returnStatement);
    void visit(AssignmentStatement assignment);
    void visit(DeclarationStatement declarationStatement);
    void visit(CallStatement callStatement);
    void visit(Block block);

    void visit(UnaryExpression operator);
    void visit(BinaryExpression operator);

    void visit(VariableAccess variable);
    void visit(ArrayAccess arrayAccess);
    void visit(FieldAccess fieldAccess);

    void visit(FalseConstant falseConstant);
    void visit(IntegerConstant integerConstant);
    void visit(StringConstant stringConstant);
    void visit(TrueConstant trueConstant);
}
