package ch.hslu.cobau.minij;

import ch.hslu.cobau.minij.ast.entity.Declaration;
import ch.hslu.cobau.minij.ast.entity.Program;
import ch.hslu.cobau.minij.ast.entity.RecordStructure;
import ch.hslu.cobau.minij.ast.type.*;

import java.util.LinkedList;
import java.util.Stack;

public class MiniJAstBuilder extends MiniJBaseVisitor<Program> {
    // Main Stack
    private final Stack<Object> stack = new Stack<>();

    // Declarations
    private final Stack<Declaration> globalDeclarations = new Stack<>();

    // Records
    private final Stack<RecordStructure> recordStructures = new Stack<>();
    private final Stack<Declaration> recordDeclarations = new Stack<>();

    @Override
    public Program visitUnit(MiniJParser.UnitContext ctx) {
        visitChildren(ctx);

        var globals = new LinkedList<Declaration>();
        var records = new LinkedList<RecordStructure>();

        while (!recordStructures.empty()) {
            records.addFirst(recordStructures.pop());
        }

        while (!globalDeclarations.isEmpty()) {
            globals.addFirst(globalDeclarations.pop());
        }

        return new Program(globals, new LinkedList<>(), records);
    }

    @Override
    public Program visitDeclaration(MiniJParser.DeclarationContext ctx) {
        visitChildren(ctx);

        var identifier = (String)stack.pop();
        var type = (Type)stack.pop();
        var declaration = new Declaration(identifier, type);

        if (ctx.parent instanceof MiniJParser.MemberContext) {
            globalDeclarations.push(declaration);
        } else if (ctx.parent instanceof MiniJParser.RecordContext) {
            recordDeclarations.push(declaration);
        }

        return null;
    }

    @Override
    public Program visitRecord(MiniJParser.RecordContext ctx) {
        visitChildren(ctx);

        var declarations = new LinkedList<Declaration>();
        while (!recordDeclarations.isEmpty()) {
            declarations.addFirst(recordDeclarations.pop());
        }

        var identifier = ctx.identifier().ID().getText();
        var record = new RecordStructure(identifier, declarations);
        recordStructures.push(record);

        return null;
    }

    @Override
    public Program visitType(MiniJParser.TypeContext ctx) {
        visitChildren(ctx);

        var type = (Type)stack.pop();
        if (ctx.RBRACKET() != null){
           type = new ArrayType(type);
        }

        stack.push(type);
        return null;
    }

    @Override
    public Program visitIdentifier(MiniJParser.IdentifierContext ctx) {
        stack.push(ctx.ID().getText());
        return null;
    }

    @Override
    public Program visitIntegerType(MiniJParser.IntegerTypeContext ctx) {
        stack.push(new IntegerType());
        return null;
    }

    @Override
    public Program visitBooleanType(MiniJParser.BooleanTypeContext ctx) {
        stack.push(new BooleanType());
        return null;
    }

    @Override
    public Program visitStringType(MiniJParser.StringTypeContext ctx) {
        stack.push(new StringType());
        return null;
    }

    @Override
    public Program visitRecordType(MiniJParser.RecordTypeContext ctx) {
        visitChildren(ctx);

        var identifier = ctx.identifier().ID().getText();
        stack.push(new RecordType(identifier));
        return null;
    }
}
