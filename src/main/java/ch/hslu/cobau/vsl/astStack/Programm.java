package ch.hslu.cobau.vsl.astStack;

import java.util.LinkedList;
import java.util.List;

public class Programm implements Visitable {
    private List<Assignment> assignments;

    public Programm(List<Assignment> assignments) {
        this.assignments = new LinkedList<>(assignments);
    }

    public List<Assignment> getAssignment() {
        return assignments;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void visitChildren(Visitor visitor) {
        for(Assignment assignment : assignments) {
            assignment.accept(visitor);
        }
    }
}
