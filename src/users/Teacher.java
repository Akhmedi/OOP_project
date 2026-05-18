package users;

import academic.Course;
import academic.Mark;
import academic.Transcript;
import enums.TeacherTitle;
import enums.ResearcherType;
import patterns.ResearcherFactory;
import research.Researcher;
import utils.Department;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Teacher extends Employee implements Comparable<Teacher>, Serializable {

    private static final long serialVersionUID = 1L;

    private TeacherTitle title;
    private List<Course> courses;
    private double rating;
    private int ratingCount;
    public Teacher(int id, String firstName, String lastName, String email,
                   String password, String language,
                   String employeeId, double salary, Department department, TeacherTitle title) {
        super(id, firstName, lastName, email, password, language, employeeId, salary, department);
        this.title = title;
        this.courses = new ArrayList<>();
        this.rating = 0.0;
        this.ratingCount = 0;

        // Professors are always researchers
        if (title == TeacherTitle.PROFESSOR) {
            this.researcher = new Researcher(employeeId + "_res", this, ResearcherType.TEACHER);
        }
    }

    public void putMark(Student student, Course course, Mark mark) {
        Transcript transcript = student.getTranscriptObj();
        transcript.addMark(mark);
        System.out.println(employeeId + " assigned mark " + mark.getDetails()
                + " to " + student.getFullName() + " in " + course.getCourseName());
    }

    public List<Student> viewStudents() {
        List<Student> allStudents = new ArrayList<>();
        for (Course c : courses) {
            for (Student s : c.getEnrolledStudents()) {
                if (!allStudents.contains(s)) {
                    allStudents.add(s);
                }
            }
        }
        return allStudents;
    }

    public void viewStudentInfo(Student student) {
        System.out.println("=== Student Info ===");
        System.out.println(student);
        System.out.println("GPA: " + student.getTranscriptObj().computeGPA());
        System.out.println("Credits: " + student.getCredits());
        System.out.println("Status: " + student.getStatus());
    }

    public void manageCourse(Course course) {
        if (!courses.contains(course)) {
            courses.add(course);
            course.addInstructor(this);
        }
        System.out.println(employeeId + " is managing course: " + course.getCourseName());
    }

    public Researcher becomeResearcher() {
        if (researcher == null) {
            researcher = ResearcherFactory.createForEmployee(this);
            System.out.println(getFullName() + " is now a researcher.");
        }
        return researcher;
    }

    public boolean isProfessor() {
        return title == TeacherTitle.PROFESSOR;
    }

    public List<Course> viewCourses() {
        System.out.println("=== Courses of " + getFullName() + " ===");
        for (Course c : courses) {
            System.out.println(c);
        }
        return new ArrayList<>(courses);
    }

    public void addRating(int score) {
        ratingCount++;
        rating = ((rating * (ratingCount - 1)) + score) / ratingCount;
        System.out.println("Rating updated: " + String.format("%.2f", rating));
    }

    // Report: marks summary for all students
    public void generateMarksReport() {
        System.out.println("=== Marks Report by " + getFullName() + " ===");
        for (Course c : courses) {
            System.out.println("Course: " + c.getCourseName());
            for (Student s : c.getEnrolledStudents()) {
                Transcript t = s.getTranscriptObj();
                System.out.println("  " + s.getFullName() + " | GPA: " +
                        String.format("%.2f", t.computeGPA()) + " | Failed: " + t.getFailedCount());
            }
        }
    }

    @Override
    public void work() {
        System.out.println("Teacher " + employeeId + " is teaching...");
    }

    @Override
    public int compareTo(Teacher other) {
        return Double.compare(other.rating, this.rating); // sorted by rating descending
    }

    public TeacherTitle getTitle()           { return title; }
    public List<Course> getCourses()         { return courses; }
    public double getRating()                { return rating; }
}
