package users;

import academic.Course;
import academic.Mark;
import academic.Transcript;
import enums.DegreeType;
import enums.StudentStatus;
import exceptions.InvalidCourseRegistrationException;
import exceptions.LowHIndexException;
import exceptions.MaxCreditsException;
import exceptions.NotResearcherException;
import exceptions.SupervisorAssignmentException;
import patterns.ResearcherFactory;
import research.Researcher;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Student extends User implements Comparable<Student>, Serializable {

    private static final long serialVersionUID = 1L;

    private String studentId;
    private double gpa;
    private int credits;
    private int failCount;
    private int enrollYear;
    private String major;
    private int yearOfStudy;
    private DegreeType degreeType;
    private List<Course> courses;
    private Transcript transcript;
    private static final int MAXCREDITS = 21;
    private static final int MAXFAILS = 3;
    public StudentStatus status;
    public Teacher advisor;      // research supervisor for 4th year
    public Researcher researcher;

    public Student(int id, String firstName, String lastName, String email,
                   String password, String language,
                   String studentId, int enrollYear, String major, int yearOfStudy) {
        super(id, firstName, lastName, email, password, language);
        this.studentId = studentId;
        this.enrollYear = enrollYear;
        this.major = major;
        this.yearOfStudy = yearOfStudy;
        this.degreeType = DegreeType.BACHELOR;
        this.gpa = 0.0;
        this.credits = 0;
        this.failCount = 0;
        this.courses = new ArrayList<>();
        this.transcript = new Transcript(this);
        this.status = StudentStatus.ACTIVE;
    }

    public List<Course> viewCourses() {
        System.out.println("=== Courses of " + getFullName() + " ===");
        for (Course c : courses) {
            System.out.println(c);
        }
        return new ArrayList<>(courses);
    }

    public void registerCourse(Course course) throws MaxCreditsException, InvalidCourseRegistrationException {
        if (degreeType != DegreeType.BACHELOR) {
            throw new InvalidCourseRegistrationException("Only bachelor students can register for courses.");
        }
        if (courses.contains(course)) {
            throw new InvalidCourseRegistrationException("Already registered for " + course.getCourseName() + ".");
        }
        if (!canRegisterForCourse()) {
            throw new InvalidCourseRegistrationException("Cannot register: check credits, fail count or status.");
        }
        if (course.getTargetMajor() != null && !course.getTargetMajor().isEmpty()
                && !course.getTargetMajor().equalsIgnoreCase(major)) {
            throw new InvalidCourseRegistrationException(
                    "Course " + course.getCourseName() + " is intended for major " + course.getTargetMajor());
        }
        if (course.getTargetYear() > 0 && course.getTargetYear() != yearOfStudy) {
            throw new InvalidCourseRegistrationException(
                    "Course " + course.getCourseName() + " is intended for year " + course.getTargetYear());
        }
        if (credits + course.getCredits() > MAXCREDITS) {
            throw new MaxCreditsException("Max credits exceeded! Current: " + credits + ", Course: " + course.getCredits());
        }
        courses.add(course);
        course.getStudents().add(this);
        credits += course.getCredits();
        System.out.println(getFullName() + " registered for " + course.getCourseName());
    }

    public List<Mark> viewGrades() {
        System.out.println("=== Grades of " + getFullName() + " ===");
        List<Mark> marks = transcript.getMarks();
        for (Mark m : marks) {
            System.out.println(m.getDetails());
        }
        return marks;
    }

    public String getTranscript() {
        return transcript.toString();
    }

    public Transcript getTranscriptObj() {
        return transcript;
    }

    public void viewTeacherInfo(Course course) {
        System.out.println("=== Instructors of " + course.getCourseName() + " ===");
        for (Teacher t : course.getInstructors()) {
            System.out.println(t.getFullName() + " | Title: " + t.getTitle()
                    + " | Rating: " + String.format("%.2f", t.getRating()));
        }
    }

    public void rateTeacher(Teacher t, int score) {
        if (score < 1 || score > 5) {
            System.out.println("Score must be between 1 and 5.");
            return;
        }
        t.addRating(score);
        System.out.println(getFullName() + " rated " + t.getFullName() + ": " + score);
    }

    public boolean canRegisterForCourse() {
        return failCount < MAXFAILS && status == StudentStatus.ACTIVE;
    }

    public void setResearchSupervisor(Teacher supervisor)
            throws SupervisorAssignmentException, LowHIndexException, NotResearcherException {
        if (yearOfStudy != 4) {
            throw new SupervisorAssignmentException("Only 4th year bachelor students can have a research supervisor.");
        }
        if (supervisor.getResearcher() == null) {
            throw new NotResearcherException("Supervisor must be a researcher.");
        }
        if (supervisor.getResearcher().getHIndex() < 3) {
            throw new LowHIndexException("Supervisor h-index must be >= 3. Current: "
                    + supervisor.getResearcher().getHIndex());
        }
        this.advisor = supervisor;
        System.out.println("Supervisor " + supervisor.getFullName() + " assigned to " + getFullName());
    }

    public Researcher becomeResearcher() {
        if (researcher == null) {
            researcher = ResearcherFactory.createForStudent(this);
            System.out.println(getFullName() + " is now a student researcher.");
        }
        return researcher;
    }

    public void awardBachelorDegree() {
        this.degreeType = DegreeType.BACHELOR;
        this.status = StudentStatus.GRADUATED;
        System.out.println(getFullName() + " has been awarded the Bachelor degree.");
    }

    // Advanced search by regex
    public static List<Student> searchByName(List<Student> students, String regex) {
        List<Student> result = new ArrayList<>();
        for (Student s : students) {
            if (s.getFullName().matches(".*" + regex + ".*")) {
                result.add(s);
            }
        }
        return result;
    }

    @Override
    public int compareTo(Student other) {
        return Double.compare(other.getGpa(), this.getGpa()); // sorted by GPA descending
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Student)) return false;
        Student s = (Student) obj;
        return Objects.equals(studentId, s.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }

    @Override
    public String toString() {
        return "Student{" + getFullName() + ", id=" + studentId + ", major=" + major
                + ", year=" + yearOfStudy + ", degree=" + degreeType
                + ", gpa=" + String.format("%.2f", getGpa()) + ", status=" + status + "}";
    }

    public String getStudentId()            { return studentId; }
    public double getGpa()                  { return transcript.computeGPA(); }
    public void setGpa(double gpa)          { this.gpa = gpa; }
    public int getCredits()                 { return credits; }
    public int getFailCount()               { return failCount; }
    public void incrementFailCount()        { this.failCount++; }
    public int getEnrollYear()              { return enrollYear; }
    public String getMajor()                { return major; }
    public int getYearOfStudy()             { return yearOfStudy; }
    public DegreeType getDegreeType()       { return degreeType; }
    public void setDegreeType(DegreeType degreeType) { this.degreeType = degreeType; }
    public Teacher getAdvisor()             { return advisor; }
    public StudentStatus getStatus()        { return status; }
    public void setStatus(StudentStatus s)  { this.status = s; }
    public List<Course> getCourses()        { return courses; }
    public Researcher getResearcher()       { return researcher; }
}
