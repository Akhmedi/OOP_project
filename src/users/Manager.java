package users;

import academic.Course;
import enums.ManagerType;
import exceptions.InvalidCourseRegistrationException;
import exceptions.MaxCreditsException;
import patterns.Observable;
import patterns.Observer;
import research.Researcher;
import utils.Department;
import utils.News;
import academic.Lesson;
import academic.Schedule;
import enums.LessonType;
import exceptions.RoomOccupiedException;
import users.User;
import java.util.Scanner;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Manager extends Employee implements Observable, Serializable {

    private static final long serialVersionUID = 1L;

    private ManagerType managerType;
    private List<News> newsList;
    private List<Course> availableCourses;
    private List<Observer> observers;

    public Manager(int id, String firstName, String lastName, String email,
                   String password, String language,
                   String employeeId, double salary, Department department, ManagerType managerType) {
        super(id, firstName, lastName, email, password, language, employeeId, salary, department);
        this.managerType = managerType;
        this.newsList = new ArrayList<>();
        this.availableCourses = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    public boolean isDean() {
        return managerType == ManagerType.DEAN;
    }

    public boolean isORManager() {
        return managerType == ManagerType.OR_MANAGER;
    }

    public boolean isRector() {
        return managerType == ManagerType.RECTOR;
    }

    public void approveRegistration(Student student, Course course) {
        if (!isORManager()) {
            System.out.println("Access Denied: Only OR Manager can approve registration.");
            return;
        }
        try {
            student.registerCourse(course);
            System.out.println("Manager " + employeeId + " approved registration of "
                    + student.getFullName() + " for " + course.getCourseName());
        } catch (MaxCreditsException | InvalidCourseRegistrationException e) {
            System.out.println("REGISTRATION ERROR: " + e.getMessage());
        }
    }

    public void assignCourse(Teacher teacher, Course course) {
        if (!isORManager()) {
            System.out.println("Access Denied: Only OR Manager can assign courses.");
            return;
        }
        teacher.manageCourse(course);
        System.out.println("Manager " + employeeId + " assigned course "
                + course.getCourseName() + " to " + teacher.getFullName());
    }

    // Statistical report
    public String createReport(List<Student> students) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Academic Performance Report ===\n");
        double totalGpa = 0;
        int count = 0;
        for (Student s : students) {
            double gpa = s.getTranscriptObj().computeGPA();
            sb.append(s.getFullName()).append(" | GPA: ").append(String.format("%.2f", gpa)).append("\n");
            totalGpa += gpa;
            count++;
        }
        if (count > 0) {
            sb.append("Average GPA: ").append(String.format("%.2f", totalGpa / count)).append("\n");
        }
        System.out.println(sb);
        return sb.toString();
    }

    // Observer pattern - publish news
    public News publishNews(String title, String content) {
        News news = new News("news_" + System.currentTimeMillis(), title, content);
        newsList.add(news);
        notifyObservers("[NEWS] " + title + ": " + content);
        System.out.println("News published: " + title);
        return news;
    }

    // View students sorted by GPA
    public List<Student> viewStudentsByGpa(List<Student> students) {
        List<Student> sorted = new ArrayList<>(students);
        Collections.sort(sorted); // uses Comparable
        System.out.println("=== Students by GPA ===");
        for (Student s : sorted) {
            System.out.println(s.getFullName() + " | GPA: " + String.format("%.2f", s.getGpa()));
        }
        return sorted;
    }

    // View students alphabetically
    public List<Student> viewStudentsAlphabetically(List<Student> students) {
        List<Student> sorted = new ArrayList<>(students);
        sorted.sort((a, b) -> a.getFullName().compareTo(b.getFullName()));
        System.out.println("=== Students Alphabetically ===");
        for (Student s : sorted) {
            System.out.println(s.getFullName());
        }
        return sorted;
    }

    // View teachers sorted by rating
    public List<Teacher> viewTeachersByRating(List<Teacher> teachers) {
        List<Teacher> sorted = new ArrayList<>(teachers);
        Collections.sort(sorted);
        System.out.println("=== Teachers by Rating ===");
        for (Teacher t : sorted) {
            System.out.println(t.getFullName() + " | Rating: " + String.format("%.2f", t.getRating()));
        }
        return sorted;
    }

    public void manageDepartment(Department dept, Employee employee, String action) {
        if (action.equals("add")) {
            dept.addStaff(employee.getEmployeeId());
            System.out.println(employee.getFullName() + " added to " + dept.getName());
        } else if (action.equals("remove")) {
            dept.removeStaff(employee.getEmployeeId());
            System.out.println(employee.getFullName() + " removed from " + dept.getName());
        }
    }

    public void handleRegistrationApproval(List<Student> students, List<Course> courses, Scanner scanner) {
        System.out.println("Students:");
        for (int i = 0; i < students.size(); i++) {
            System.out.println(i + ". " + students.get(i).getFullName());
        }
        System.out.print("Student index: ");
        try {
            int studentIndex = Integer.parseInt(scanner.nextLine().trim());
            System.out.println("Courses:");
            for (int i = 0; i < courses.size(); i++) {
                System.out.println(i + ". " + courses.get(i).getCourseName());
            }
            System.out.print("Course index: ");
            int courseIndex = Integer.parseInt(scanner.nextLine().trim());
            approveRegistration(students.get(studentIndex), courses.get(courseIndex));
        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    public void handleTeacherCourseAssignment(List<Teacher> teachers, List<Course> courses, Scanner scanner) {
        System.out.println("Teachers:");
        for (int i = 0; i < teachers.size(); i++) {
            System.out.println(i + ". " + teachers.get(i).getFullName());
        }
        System.out.print("Teacher index: ");
        try {
            int teacherIndex = Integer.parseInt(scanner.nextLine().trim());
            System.out.println("Courses:");
            for (int i = 0; i < courses.size(); i++) {
                System.out.println(i + ". " + courses.get(i).getCourseName());
            }
            System.out.print("Course index: ");
            int courseIndex = Integer.parseInt(scanner.nextLine().trim());
            assignCourse(teachers.get(teacherIndex), courses.get(courseIndex));
        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    public void handleCourseRegistrationSetup(List<Course> courses, Scanner scanner) {
        System.out.println("Courses:");
        for (int i = 0; i < courses.size(); i++) {
            System.out.println(i + ". " + courses.get(i).getCourseName());
        }
        System.out.print("Course index: ");
        try {
            int courseIndex = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Target major: ");
            String major = scanner.nextLine();
            System.out.print("Target year: ");
            int year = Integer.parseInt(scanner.nextLine().trim());
            Course course = courses.get(courseIndex);
            course.setTargetMajor(major);
            course.setTargetYear(year);
            if (!availableCourses.contains(course)) {
                availableCourses.add(course);
            }
            System.out.println("Course added for registration.");
        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    public boolean handleDeanAssignment(List<Department> departments, List<Manager> managers, Scanner scanner) {
        if (!isRector()) return false;
        System.out.println("Departments:");
        for (int i = 0; i < departments.size(); i++) {
            System.out.println(i + ". " + departments.get(i).getName());
        }
        System.out.print("Select department: ");
        try {
            int departmentIndex = Integer.parseInt(scanner.nextLine().trim());
            System.out.println("Managers:");
            for (int i = 0; i < managers.size(); i++) {
                System.out.println(i + ". " + managers.get(i).getFullName());
            }
            System.out.print("Select manager to be Dean: ");
            int managerIndex = Integer.parseInt(scanner.nextLine().trim());
            Manager deanCandidate = managers.get(managerIndex);
            Department department = departments.get(departmentIndex);
            department.setDeanId(deanCandidate.getEmployeeId());
            deanCandidate.setManagerType(ManagerType.DEAN);
            System.out.println(deanCandidate.getFullName() + " is now Dean of " + department.getName());
            return true;
        } catch (Exception e) {
            System.out.println("Invalid input.");
            return false;
        }
    }

    public boolean handleLessonScheduling(List<Course> courses, List<Teacher> teachers,
                                          Schedule schedule, Scanner scanner) {
        if (!isORManager()) return false;
        try {
            System.out.println("Courses:");
            for (int i = 0; i < courses.size(); i++) {
                System.out.println(i + ". " + courses.get(i).getCourseName());
            }
            System.out.print("Select course index: ");
            int courseIndex = Integer.parseInt(scanner.nextLine().trim());

            System.out.println("Teachers:");
            for (int i = 0; i < teachers.size(); i++) {
                System.out.println(i + ". " + teachers.get(i).getFullName());
            }
            System.out.print("Select teacher index: ");
            int teacherIndex = Integer.parseInt(scanner.nextLine().trim());

            System.out.println("Type: 1=LECTURE, 2=PRACTICE");
            int typeIndex = Integer.parseInt(scanner.nextLine().trim());
            LessonType lessonType = typeIndex == 1 ? LessonType.LECTURE : LessonType.PRACTICE;

            Lesson lesson = schedule.scheduleLesson(lessonType, courses.get(courseIndex), teachers.get(teacherIndex), 90);
            courses.get(courseIndex).addSchedule(lesson);
            return true;
        } catch (RoomOccupiedException e) {
            System.out.println("SCHEDULING ERROR: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
        return false;
    }

    public void viewDepartmentStaff(List<Department> departments, List<User> allUsers, Scanner scanner) {
        Department departmentToView = null;
        if (isRector()) {
            if (departments.isEmpty()) {
                System.out.println("No departments exist in the system.");
                return;
            }
            System.out.println("Select Department to view staff:");
            for (int i = 0; i < departments.size(); i++) {
                System.out.println(i + ". " + departments.get(i).getName() + " (ID: " + departments.get(i).getDepartmentId() + ")");
            }
            System.out.print("Choice: ");
            try {
                departmentToView = departments.get(Integer.parseInt(scanner.nextLine().trim()));
            } catch (Exception e) {
                System.out.println("Invalid input.");
                return;
            }
        } else {
            departmentToView = getDepartment();
        }

        if (departmentToView == null) {
            System.out.println("No department selected or you are not assigned to one.");
            return;
        }

        System.out.println("\n=== STAFF LIST: " + departmentToView.getName() + " ===");
        int count = 0;
        for (User user : allUsers) {
            if (user instanceof Employee) {
                Employee employee = (Employee) user;
                if (employee.getDepartment() != null
                        && employee.getDepartment().getDepartmentId().equals(departmentToView.getDepartmentId())) {
                    System.out.println(++count + ". " + employee.getFullName() + " [" + employee.getClass().getSimpleName() + "]");
                }
            }
        }
        if (count == 0) {
            System.out.println("No staff members found assigned to this department.");
        }
    }

    public News handleNewsPublishing(List<News> allNews, Scanner scanner) {
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Content: ");
        String content = scanner.nextLine();
        News news = publishNews(title, content);
        allNews.add(news);
        return news;
    }

    // Observer pattern
    @Override
    public void addObserver(Observer o)    { observers.add(o); }
    @Override
    public void removeObserver(Observer o) { observers.remove(o); }
    @Override
    public void notifyObservers(String message) {
        for (Observer o : observers) {
            o.update(message);
        }
    }

    @Override
    public void work() {
        System.out.println("Manager " + employeeId + " is managing...");
    }

    public ManagerType getManagerType()          { return managerType; }
    public void setManagerType(ManagerType type) { this.managerType = type; }
    public List<Course> getAvailableCourses()    { return availableCourses; }
    public List<News> getNewsList()              { return newsList; }
}
