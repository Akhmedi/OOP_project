package academic;

import users.Student;
import users.Teacher;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    private String courseId;
    private String courseName;
    private int credits;
    private String description;
    public String major;
    private String targetMajor;
    private int targetYear;
    private List<Teacher> instructors;
    private List<Student> students;
    private List<Lesson> schedule;

    public Course(String courseId, String courseName, int credits, String description, String major) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.credits = credits;
        this.description = description;
        this.major = major;
        this.instructors = new ArrayList<>();
        this.students = new ArrayList<>();
        this.schedule = new ArrayList<>();
    }

    public List<Student> getEnrolledStudents() {
        return new ArrayList<>(students);
    }

    public List<Teacher> getInstructors() {
        return new ArrayList<>(instructors);
    }

    public void addInstructor(Teacher teacher) {
        if (!instructors.contains(teacher)) {
            instructors.add(teacher);
        }
    }

    public List<Student> getStudents() {
        return students;
    }

    public void addSchedule(Lesson lesson) {
        schedule.add(lesson);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Course)) return false;
        Course c = (Course) obj;
        return Objects.equals(courseId, c.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId);
    }

    @Override
    public String toString() {
        return "Course{" + courseId + ", name='" + courseName + "', credits=" + credits
                + ", major='" + major + "', students=" + students.size() + "}";
    }

    public String getCourseId()             { return courseId; }
    public String getCourseName()           { return courseName; }
    public int getCredits()                 { return credits; }
    public String getTargetMajor()          { return targetMajor; }
    public void setTargetMajor(String m)    { this.targetMajor = m; }
    public int getTargetYear()              { return targetYear; }
    public void setTargetYear(int y)        { this.targetYear = y; }
}
