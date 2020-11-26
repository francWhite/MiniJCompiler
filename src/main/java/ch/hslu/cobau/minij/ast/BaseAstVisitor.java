package ch.hslu.cobau.minij.ast;

import ch.hslu.cobau.minij.ast.constants.*;
import ch.hslu.cobau.minij.ast.entity.*;
import ch.hslu.cobau.minij.ast.expression.*;
import ch.hslu.cobau.minij.ast.statement.*;

public class BaseAstVisitor implements AstVisitor {
    @Override
    public void visit(Program program) {
        program.visitChildren(this);
    }

    @Override
    public void visit(Procedure procedure) {
        procedure.visitChildren(this);
    }

    @Override
    public void visit(RecordStructure recordStructure) {
        recordStructure.visitChildren(this);
    }

    @Override
    public void visit(Declaration declaration) {
        declaration.visitChildren(this);
    }

    @Override
    public void visit(ReturnStatement returnStatement) {
        returnStatement.visitChildren(this);
    }

    @Override
    public void visit(AssignmentStatement assignment) {
        assignment.visitChildren(this);
    }

    @Override
    public void visit(DeclarationStatement declarationStatement) {
        declarationStatement.visitChildren(this);
    }

    @Override
    public void visit(CallStatement callStatement) {
        callStatement.visitChildren(this);
    }

    @Override
    public void visit(IfStatement ifStatement) {
        ifStatement.visitChildren(this);
    }

    @Override
    public void visit(WhileStatement whileStatement) {
        whileStatement.visitChildren(this);
    }

    @Override
    public void visit(Block block) {
        block.visitChildren(this);
    }

    @Override
    public void visit(UnaryExpression unaryExpression) {
        unaryExpression.visitChildren(this);
    }

    @Override
    public void visit(BinaryExpression binaryExpression) {
        binaryExpression.visitChildren(this);
    }

    @Override
    public void visit(VariableAccess variable) {
        variable.visitChildren(this);
    }

    @Override
    public void visit(ArrayAccess arrayAccess) {
        arrayAccess.visitChildren(this);
    }

    @Override
    public void visit(FieldAccess fieldAccess) {
        fieldAccess.visitChildren(this);
    }

    @Override
    public void visit(FalseConstant falseConstant) {
        falseConstant.visitChildren(this);
    }

    @Override
    public void visit(IntegerConstant integerConstant) {
        integerConstant.visitChildren(this);
    }

    @Override
    public void visit(StringConstant stringConstant) {
        stringConstant.visitChildren(this);
    }

    @Override
    public void visit(TrueConstant trueConstant) {
        trueConstant.visitChildren(this);
    }
}
