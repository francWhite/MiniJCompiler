package ch.hslu.cobau.minij;

import ch.hslu.cobau.minij.ast.entity.Declaration;
import ch.hslu.cobau.minij.ast.entity.Program;
import ch.hslu.cobau.minij.ast.entity.RecordStructure;
import ch.hslu.cobau.minij.ast.type.*;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.LinkedList;
import java.util.Stack;

public class MiniJAstBuilder extends MiniJBaseVisitor<Program> {
    private final Stack<Object> stack = new Stack<>();

    @Override
    public Program visitProgram(MiniJParser.ProgramContext ctx) {
        visitChildren(ctx);

        var globals = new LinkedList<Declaration>();
        var records = new LinkedList<RecordStructure>();

        while (!stack.empty()) {
            var child = stack.pop();

            if (child.getClass() == Declaration.class) {
                globals.addFirst((Declaration) child);
            } else if (child.getClass() == RecordStructure.class) {
                records.addFirst((RecordStructure) child);
            }
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
            var type = parseType(param.TYPE());

            declaration = new Declaration(identifier, type);
        } else {
            var identifier = param.IDENTIFIER(1).getText();
            var typeIdentifier = param.IDENTIFIER(0).getText();
            var recordType = new RecordType(typeIdentifier);

            declaration = new Declaration(identifier, recordType);
        }

        stack.push(declaration);
        return null;
    }

    @Override
    public Program visitRecord(MiniJParser.RecordContext ctx) {
        visitChildren(ctx);

        var identifier = ctx.IDENTIFIER().getText();

        var declarations = new LinkedList<Declaration>();
        while (!stack.isEmpty()) {
            var declaration = (Declaration) stack.pop();
            declarations.add(declaration);
        }

        var record = new RecordStructure(identifier, declarations);
        stack.push(record);

        return null;
    }

    Type parseType(TerminalNode node) {
        var typeString = node.getText();
        var isArray = typeString.contains("[]");
        typeString = typeString.replace("[]", "");

        Type baseType = switch (typeString) {
            case "int" -> new IntegerType();
            case "boolean" -> new BooleanType();
            case "string" -> new StringType();
            default -> new RecordType(typeString);
        };

        if (isArray) {
            return new ArrayType(baseType);
        }

        return baseType;
    }
}
