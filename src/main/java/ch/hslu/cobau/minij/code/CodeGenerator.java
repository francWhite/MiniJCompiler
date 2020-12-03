package ch.hslu.cobau.minij.code;

import ch.hslu.cobau.minij.ast.BaseAstVisitor;
import ch.hslu.cobau.minij.ast.entity.Program;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class CodeGenerator extends BaseAstVisitor {
    private String code;
    private final Map<String, Integer> localsMap = new HashMap<>();
    private final Stack<String> statementsStack = new Stack<>();

    public String getCode(){
        return code;
    }

    @Override
    public void visit(Program program) {
        program.visitChildren(this);

        int stackSize = localsMap.size() * 8;
        stackSize += stackSize % 16; // align to 16 bytes

        code = "DEFAULT REL\n" +
                "extern writeInt\n" +
                "extern _exit\n" +
                "global _start\n" +
                "section .text\n" +
                "_start:" +
                "   push rbp\n" +
                "   mov  rbp, rsp\n" +
                "   sub  rsp, " + stackSize + "\n";

        String statements = "";
        while(!statementsStack.isEmpty()) {
            statements = statementsStack.pop() + statements;
        }

        code += statements;
        code += "   mov  rdi, 0\n" +
                "   call _exit\n" +
                "   mov  rsp, rbp\n" +
                "   pop  rbp\n";
    }
}
