package ch.hslu.cobau.vsl2.code;

import ch.hslu.cobau.vsl2.ast.*;
import java.util.*;

/**
 * Generates valid assembly code for input AST.
 */
public class ProgramGenerator extends BaseVisitor {

    // final generated assembly code.
    private String code;

    // mapping from variable to stack position
    private final Map<String, Integer> localsMap = new HashMap<>();

    // temporary storage for generated assembly code fragments
    private final Stack<String> statementsStack = new Stack<>();

    /**
     * @return Final generated assembly code.
     */
    public String getCode() {
        return code;
    }

    private void addLocal(String identifier) {
        int position = (localsMap.size()  + 1) ;
        if (!localsMap.containsKey(identifier)) {
            localsMap.put(identifier, position);
        }
    }

    @Override
    public void visit(Identifier identifier) {
        identifier.visitChildren(this);

        // Task 1) Überlegen Sie sich, was folgende Zeile macht? Welche Funktion hat addLocal?
        addLocal(identifier.getName());
    }

    public void visit(Assignment assignment) {
        assignment.visitChildren(this);

        String variable = assignment.getAssignee().getName();
        int position = localsMap.get(variable);

        String code = "";
        code += "   mov rax, 1\n";

        // Task 2) Im Register rax erhalten wir das Resultat der Expression
        //         (aktuell 1 als Platzhalter). Wie lautet die Assembleranweisung,
        //         welche das Resultat in die entsprechende VSL2-Variable schreibt?
        code += "; TODO: hier variable speichern\n";

        statementsStack.push(code);
    }

    public void visit(WriteInt writeInt) {
        writeInt.visitChildren(this);

        String variable = writeInt.getValue().getName();
        int position = localsMap.get(variable);

        String code = "";
        // Task 3) Wie lauten die beiden Assembleranweisungen, welche den Inhalt der
        //         VSL2-Variable mittels der eingebauten Funktion writeInt ausgeben?
        //         writeInt übernimmt einen Parameter mit einem Ganzzahlwert
        //         (zwei Zeilen Assemblercode).
        code += " ; hier parameter setzen\n";
        code += " ; hier Funktion aufrufen\n";

        statementsStack.push(code);
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

        // Task 4) Überlegen Sie sich was, folgende vier Zeilen machen. Warum so kompliziert?
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
