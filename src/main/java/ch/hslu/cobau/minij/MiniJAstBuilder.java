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

        Declaration declaration;
        var identifier = ctx.identifier().ID().getText();

        // if basicType is not set, then it is a array-type
        if (ctx.type().basicType() == null) {
            var baseType = ctx.type().type().basicType().getText();
            var type = parseType(baseType);
            var arrayType = new ArrayType(type);

            declaration = new Declaration(identifier, arrayType);
        } else {
            var baseType = ctx.type().basicType().getText();
            var type = parseType(baseType);

            declaration = new Declaration(identifier, type);
        }

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

    Type parseType(String typeString) {
        return switch (typeString) {
            case "int" -> new IntegerType();
            case "boolean" -> new BooleanType();
            case "string" -> new StringType();
            default -> new RecordType(typeString);
        };
    }
}
