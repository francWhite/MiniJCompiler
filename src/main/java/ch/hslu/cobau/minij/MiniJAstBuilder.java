package ch.hslu.cobau.minij;

import ch.hslu.cobau.minij.ast.entity.Declaration;
import ch.hslu.cobau.minij.ast.entity.Program;
import ch.hslu.cobau.minij.ast.entity.RecordStructure;
import ch.hslu.cobau.minij.ast.type.*;
import org.antlr.v4.runtime.tree.TerminalNode;

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
    public Program visitProgram(MiniJParser.ProgramContext ctx) {
        visitChildren(ctx);

        var globals = new LinkedList<Declaration>();
        var records = new LinkedList<RecordStructure>();

        while (!recordStructures.empty()) {
            records.addFirst(recordStructures.pop());
        }

        while(!globalDeclarations.isEmpty()){
            globals.addFirst(globalDeclarations.pop());
        }

        return new Program(globals, new LinkedList<>(), records);
    }

    @Override
    public Program visitDeclaration(MiniJParser.DeclarationContext ctx) {
        visitChildren(ctx);

        var param = ctx.param();
        Declaration declaration;

        if (param.IDENTIFIER().size() == 1) {
            var identifier = param.IDENTIFIER(0).getText();
            var isArrayType = !param.INDEXBEGIN().isEmpty();
            var type = parseType(param.TYPE().getText(), isArrayType);

            declaration = new Declaration(identifier, type);
        } else {
            var identifier = param.IDENTIFIER(1).getText();
            var typeIdentifier = param.IDENTIFIER(0).getText();
            var recordType = new RecordType(typeIdentifier);

            declaration = new Declaration(identifier, recordType);
        }

        if (ctx.parent instanceof MiniJParser.ProgramContext){
            globalDeclarations.push(declaration);
        }
        else if(ctx.parent instanceof MiniJParser.RecordContext){
            recordDeclarations.push(declaration);
        }

        return null;
    }

    @Override
    public Program visitRecord(MiniJParser.RecordContext ctx) {
        visitChildren(ctx);

        var identifier = ctx.IDENTIFIER().getText();

        var declarations = new LinkedList<Declaration>();
        while (!recordDeclarations.isEmpty()) {
            declarations.addFirst(recordDeclarations.pop());
        }

        var record = new RecordStructure(identifier, declarations);
        recordStructures.push(record);

        return null;
    }

    Type parseType(String typeString, boolean isArraytype) {
        Type baseType = switch (typeString) {
            case "int" -> new IntegerType();
            case "boolean" -> new BooleanType();
            case "string" -> new StringType();
            default -> new RecordType(typeString);
        };

        if (isArraytype) {
            return new ArrayType(baseType);
        }

        return baseType;
    }
}
