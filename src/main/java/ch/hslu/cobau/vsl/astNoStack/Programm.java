package ch.hslu.cobau.vsl.astNoStack;

import java.util.LinkedList;
import java.util.List;

public class Programm implements Visitable {
    private final List<Assignment> assignments;

    public Programm(List<Assignment> assignments) {
        this.assignments = new LinkedList<>(assignments);
    }

    public List<Assignment> getAssignment() {
        return assignments;
    }

    @Override
    public Object accept(Visitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Object visitChildren(Visitor visitor) {
        List<Object> list = new LinkedList<>();
        for(Assignment assignment : assignments) {
            list.add(assignment.accept(visitor));
        }
        return list;
    }
}
