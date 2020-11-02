package ch.hslu.cobau.minij;

import ch.hslu.cobau.minij.ast.entity.Declaration;
import ch.hslu.cobau.minij.ast.entity.Procedure;
import ch.hslu.cobau.minij.ast.entity.Program;
import ch.hslu.cobau.minij.ast.entity.RecordStructure;
import ch.hslu.cobau.minij.ast.type.*;

import java.util.LinkedList;
import java.util.Stack;

public class MiniJAstBuilder extends MiniJBaseVisitor<Program> {
    // Main Stack
    private final Stack<Object> stack = new Stack<>();

    @Override
    public Program visitUnit(MiniJParser.UnitContext ctx) {
        visitChildren(ctx);

        var globals = new LinkedList<Declaration>();
        var records = new LinkedList<RecordStructure>();
        var procedures = new LinkedList<Procedure>();

        while (!stack.empty()) {
            var member = stack.pop();

            if (member instanceof Declaration) {
                globals.addFirst((Declaration) member);
            } else if (member instanceof RecordStructure) {
                records.addFirst((RecordStructure) member);
            } else if (member instanceof Procedure) {
                procedures.addFirst((Procedure) member);
            }
        }

        return new Program(globals, procedures, records);
    }

    @Override
    public Program visitDeclaration(MiniJParser.DeclarationContext ctx) {
        visitChildren(ctx);

        var identifier = (String)stack.pop();
        var type = (Type)stack.pop();
        var declaration = new Declaration(identifier, type);

        stack.push(declaration);
        return null;
    }

    @Override
    public Program visitRecord(MiniJParser.RecordContext ctx) {
        var parentStackCount = stack.size();
        visitChildren(ctx);

        var declarations = new LinkedList<Declaration>();
        while (stack.size() - 1 > parentStackCount) {            //stack.size() - 1 because the identifier is also pushed to the stack
            declarations.addFirst((Declaration)stack.pop());
        }

        var identifier = (String)stack.pop();
        var record = new RecordStructure(identifier, declarations);
        stack.push(record);

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

        var identifier = (String)stack.pop();
        stack.push(new RecordType(identifier));
        return null;
    }
}
