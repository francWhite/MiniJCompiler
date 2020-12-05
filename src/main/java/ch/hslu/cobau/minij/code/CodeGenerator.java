package ch.hslu.cobau.minij.code;

import ch.hslu.cobau.minij.ast.BaseAstVisitor;
import ch.hslu.cobau.minij.ast.constants.IntegerConstant;
import ch.hslu.cobau.minij.ast.entity.Procedure;
import ch.hslu.cobau.minij.ast.entity.Program;
import ch.hslu.cobau.minij.ast.statement.CallStatement;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class CodeGenerator extends BaseAstVisitor {
    private String code;
    private final Map<String, Integer> localsMap = new HashMap<>();
    private final Stack<String> statementsStack = new Stack<>();

    public String getCode() {
        return code;
    }

    @Override
    public void visit(Program program) {
        program.visitChildren(this);

        String statements = "";
        while (!statementsStack.isEmpty()) {
            statements = statementsStack.pop() + statements;
        }

        code += statements;
    }

    @Override
    public void visit(Procedure procedure) {
        if (procedure.getIdentifier().equals("main")) {
            BuildMainProcedure(procedure);
            return;
        }

        BuildProcedure(procedure);
    }


    private void BuildMainProcedure(Procedure procedure) {
        procedure.visitChildren(this);

        int stackSize = localsMap.size() * 8;
        stackSize += stackSize % 16; // align to 16 bytes

        code = "DEFAULT REL\n" +
                "extern writeInt\n" +
                "extern writeChar\n" +
                "extern _exit\n" +
                "global _start\n" +
                "section .text\n" +
                "_start:" +
                "   push rbp\n" +
                "   mov  rbp, rsp\n" +
                "   sub  rsp, " + stackSize + "\n";

        String statements = "";
        while (!statementsStack.isEmpty()) {
            statements = statementsStack.pop() + statements;
        }

        code += statements;
        code += "   mov  rdi, 0\n" +
                "   call _exit\n" +
                "   mov  rsp, rbp\n" +
                "   pop  rbp\n";
    }

    private void BuildProcedure(Procedure procedure) {
        procedure.visitChildren(this);

        //epilog
        var procedureCode = procedure.getIdentifier() + ":\n" +
                "   push rbp\n" +
                "   mov rbp, rsp\n";

        //save parameters
        //TODO

        //actual procedure code
        String statements = "";
        while (!statementsStack.isEmpty()) {
            statements = statementsStack.pop() + statements;
        }
        procedureCode += statements;

        //epilog
        procedureCode += "   mov rsp, rbp\n" +
                "   pop rbp\n" +
                "   ret\n";

        statementsStack.push(procedureCode);
    }

    @Override
    public void visit(CallStatement callStatement) {
        callStatement.visitChildren(this);

        //TODO build all Parameters correctly, currently only 1 param is supported
        var parameterValue = statementsStack.pop();
        var procedureCode = "   mov rdi, " + parameterValue + "\n" +
                "   call " + callStatement.getIdentifier() + "\n";

        statementsStack.push(procedureCode);
    }

    @Override
    public void visit(IntegerConstant integerConstant) {
        statementsStack.push(Long.toString(integerConstant.getValue()));
    }
}
