package academic;

import enums.Letter;
import enums.MarkType;
import users.Student;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Transcript implements Serializable {

    private static final long serialVersionUID = 1L;

    private Student student;
    private List<Mark> marks;
    private double gpa;

    public Transcript(Student student) {
        this.student = student;
        this.marks = new ArrayList<>();
        this.gpa = 0.0;
    }

    public double computeGPA() {
        if (marks.isEmpty()) return 0.0;
        double total = 0.0;
        for (Mark mark : marks) {
            total += mark.getScore();
        }
        gpa = total / marks.size();
        student.setGpa(gpa);
        return gpa;
    }

    public void addMark(Mark m) {
        marks.add(m);
        if (m.getLetter() == Letter.F) {
            student.incrementFailCount();
        }
    }

    public int getFailedCount() {
        int count = 0;
        for (Mark mark : marks) {
            if (mark.getLetter() == Letter.F) {
                count++;
            }
        }
        return count;
    }

    public List<Mark> getMarks() {
        return new ArrayList<>(marks);
    }

    @Override
    public String toString() {
        return "Transcript{student=" + student.getFullName()
                + ", gpa=" + String.format("%.2f", computeGPA())
                + ", totalMarks=" + marks.size()
                + ", failed=" + getFailedCount() + "}";
    }
}
