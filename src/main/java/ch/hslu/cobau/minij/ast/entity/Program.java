package ch.hslu.cobau.minij.ast.entity;

import ch.hslu.cobau.minij.ast.AstElement;
import ch.hslu.cobau.minij.ast.AstVisitor;

import java.util.List;
import java.util.Objects;

public class Program extends AstElement {
    private final List<Declaration> globals;
    private final List<Procedure> procedures;
    private final List<RecordStructure> recordStructures;

    public Program(List<Declaration> globals, List<Procedure> procedures, List<RecordStructure> recordStructures) {
        Objects.requireNonNull(globals);
        Objects.requireNonNull(procedures);
        Objects.requireNonNull(recordStructures);

        this.globals = globals;
        this.procedures = procedures;
        this.recordStructures = recordStructures;
    }

    public List<Declaration> getGlobals() {
        return globals;
    }

    public List<Procedure> getProcedures() {
        return procedures;
    }

    public List<RecordStructure> getRecords() {
        return recordStructures;
    }

    @Override
    public void accept(AstVisitor astVisitor) {
        astVisitor.visit(this);
    }

    @Override
    public void visitChildren(AstVisitor astVisitor) {
        globals.forEach(global -> global.accept(astVisitor));
        procedures.forEach(procedure -> procedure.accept(astVisitor));
        recordStructures.forEach(recordStructure -> recordStructure.accept(astVisitor));
    }
}
