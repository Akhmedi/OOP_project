package users;

import patterns.DataStorage;
import enums.DegreeType;
import enums.StudentStatus;
import utils.Department;
import utils.LogEntry;
import enums.ManagerType;
import enums.TeacherTitle;

import java.io.Serializable;
import java.util.List;
import java.util.Scanner;

public class Admin extends User implements Serializable {

    private static final long serialVersionUID = 1L;

    public Admin(int id, String firstName, String lastName, String email, String password, String language) {
        super(id, firstName, lastName, email, password, language);
    }

    public void addUser(List<User> users, User user) {
        users.add(user);
        DataStorage.getInstance().writeLog(new LogEntry(String.valueOf(getId()),
                "Added user: " + user.getFullName()));
        System.out.println("User added: " + user.getFullName());
    }

    public void removeUser(List<User> users, User user) {
        if (user instanceof Employee) {
            Employee emp = (Employee) user;
            if (emp.getDepartment() != null) {
                emp.getDepartment().removeStaff(emp.getEmployeeId());
            }
        }
        users.remove(user);
        DataStorage.getInstance().writeLog(new LogEntry(String.valueOf(getId()),
                "Removed user: " + user.getFullName()));
        System.out.println("User removed: " + user.getFullName());
    }

    public void updateUser(User user, String newFirstName, String newLastName, String newEmail) {
        user.setFirstName(newFirstName);
        user.setLastName(newLastName);
        user.setEmail(newEmail);
        DataStorage.getInstance().writeLog(new LogEntry(String.valueOf(getId()),
                "Updated user: " + user.getId() + " -> " + newFirstName + " " + newLastName + ", Email: " + newEmail));
        System.out.println("User updated: " + user.getFullName() + " | Email: " + user.getEmail());
    }

    public void viewLogs(String filter) {
        System.out.println("=== Log Files ===");
        List<LogEntry> logs = DataStorage.getInstance().readLogs();
        for (LogEntry entry : logs) {
            if (filter == null || entry.toString().contains(filter)) {
                System.out.println(entry);
            }
        }
    }

    public void manageDepartment(List<Department> departments, Department dept, String action) {
        if (action.equals("add")) {
            departments.add(dept);
            System.out.println("Department added: " + dept.getName());
        } else if (action.equals("remove")) {
            departments.remove(dept);
            System.out.println("Department removed: " + dept.getName());
        }
    }

    public boolean handleUserCreation(List<User> allUsers, List<Student> students, List<Teacher> teachers,
                                      List<Manager> managers, List<ResearchAssistant> researchAssistants,
                                      List<research.Researcher> researchers, List<Department> departments,
                                      Scanner scanner) {
        System.out.print("First name: ");
        String firstName = scanner.nextLine();
        System.out.print("Last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.println("Role: 1=Student 2=Teacher 3=Manager 4=Research Assistant");
        String role = scanner.nextLine().trim();
        int newId = allUsers.size() + 100;

        try {
            if (role.equals("1")) {
                Student student = new Student(newId, firstName, lastName, email, password, "EN",
                        "S" + newId, 2025, "CS", 1);
                addUser(allUsers, student);
                students.add(student);
                return true;
            }
            if (role.equals("2")) {
                Department department = selectDepartment(departments, scanner);
                if (department == null) return false;
                TeacherTitle title = selectTeacherTitle(scanner);
                Teacher teacher = new Teacher(newId, firstName, lastName, email, password, "EN",
                        "T" + newId, 50000, department, title);
                addUser(allUsers, teacher);
                teachers.add(teacher);
                department.addStaff(teacher.getEmployeeId());
                return true;
            }
            if (role.equals("3")) {
                ManagerType managerType = selectManagerType(scanner);
                Department department = null;
                if (managerType != ManagerType.RECTOR) {
                    department = selectDepartment(departments, scanner);
                    if (department == null) return false;
                }
                Manager manager = new Manager(newId, firstName, lastName, email, password, "EN",
                        "M" + newId, 80000, department, managerType);
                addUser(allUsers, manager);
                managers.add(manager);
                if (department != null) {
                    department.addStaff(manager.getEmployeeId());
                }
                return true;
            }
            if (role.equals("4")) {
                Department department = selectDepartment(departments, scanner);
                if (department == null) return false;
                ResearchAssistant assistant = new ResearchAssistant(newId, firstName, lastName, email, password, "EN",
                        "RA" + newId, 45000, department);
                addUser(allUsers, assistant);
                researchAssistants.add(assistant);
                researchers.add(assistant.getResearcher());
                department.addStaff(assistant.getEmployeeId());
                return true;
            }
        } catch (Exception e) {
            System.out.println("Invalid input.");
            return false;
        }
        return false;
    }

    public boolean handleUserRemoval(List<User> allUsers, Scanner scanner) {
        System.out.println("Users:");
        for (int i = 0; i < allUsers.size(); i++) {
            System.out.println(i + ". " + allUsers.get(i).getFullName());
        }
        System.out.print("Index to remove: ");
        try {
            int userIndex = Integer.parseInt(scanner.nextLine().trim());
            removeUser(allUsers, allUsers.get(userIndex));
            return true;
        } catch (Exception e) {
            System.out.println("Invalid input.");
            return false;
        }
    }

    public boolean handleUserUpdate(List<User> allUsers, Scanner scanner) {
        System.out.println("Users:");
        for (int i = 0; i < allUsers.size(); i++) {
            System.out.println(i + ". " + allUsers.get(i).getFullName());
        }
        System.out.print("Index to update: ");
        try {
            int userIndex = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("New first name: ");
            String firstName = scanner.nextLine();
            System.out.print("New last name: ");
            String lastName = scanner.nextLine();
            System.out.print("New email: ");
            String email = scanner.nextLine();
            updateUser(allUsers.get(userIndex), firstName, lastName, email);
            return true;
        } catch (Exception e) {
            System.out.println("Invalid input.");
            return false;
        }
    }

    public boolean handleStudentStatusChange(List<Student> students, Scanner scanner) {
        if (students.isEmpty()) {
            System.out.println("No students available.");
            return false;
        }
        System.out.println("Students:");
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            System.out.println(i + ". " + student.getFullName() + " | Current status: " + student.getStatus());
        }
        System.out.print("Student index: ");
        try {
            int studentIndex = Integer.parseInt(scanner.nextLine().trim());
            Student student = students.get(studentIndex);
            StudentStatus[] statuses = StudentStatus.values();
            System.out.println("Available statuses:");
            for (int i = 0; i < statuses.length; i++) {
                System.out.println(i + ". " + statuses[i]);
            }
            System.out.print("Status index: ");
            int statusIndex = Integer.parseInt(scanner.nextLine().trim());
            student.setStatus(statuses[statusIndex]);
            DataStorage.getInstance().writeLog(new LogEntry(String.valueOf(getId()),
                    "Changed student status: " + student.getFullName() + " -> " + student.getStatus()));
            System.out.println("Status updated for " + student.getFullName() + ": " + student.getStatus());
            return true;
        } catch (Exception e) {
            System.out.println("Invalid input.");
            return false;
        }
    }

    public boolean handleBachelorAward(List<Student> students, Scanner scanner) {
        if (students.isEmpty()) {
            System.out.println("No students available.");
            return false;
        }
        System.out.println("Students:");
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            System.out.println(i + ". " + student.getFullName()
                    + " | Year: " + student.getYearOfStudy()
                    + " | Degree: " + student.getDegreeType()
                    + " | Status: " + student.getStatus());
        }
        System.out.print("Student index: ");
        try {
            int studentIndex = Integer.parseInt(scanner.nextLine().trim());
            Student student = students.get(studentIndex);
            student.awardBachelorDegree();
            DataStorage.getInstance().writeLog(new LogEntry(String.valueOf(getId()),
                    "Awarded " + DegreeType.BACHELOR + " degree to " + student.getFullName()));
            return true;
        } catch (Exception e) {
            System.out.println("Invalid input.");
            return false;
        }
    }

    public boolean handleDepartmentManagement(List<Department> departments, List<Manager> managers, Scanner scanner) {
        System.out.println("1. View Departments");
        System.out.println("2. Add Department");
        System.out.println("3. Assign Dean to Department");
        System.out.print("Choice: ");
        String choice = scanner.nextLine().trim();

        if (choice.equals("1")) {
            System.out.println("Departments:");
            for (Department department : departments) {
                System.out.println(department);
            }
            return false;
        }
        if (choice.equals("2")) {
            System.out.print("Dept ID: ");
            String departmentId = scanner.nextLine();
            System.out.print("Dept Name: ");
            String departmentName = scanner.nextLine();
            System.out.print("Description: ");
            String description = scanner.nextLine();
            manageDepartment(departments, new Department(departmentId, departmentName, description), "add");
            return true;
        }
        if (choice.equals("3")) {
            return assignDeanToDepartment(departments, managers, scanner);
        }
        return false;
    }

    public boolean assignDeanToDepartment(List<Department> departments, List<Manager> managers, Scanner scanner) {
        try {
            System.out.println("Departments:");
            for (int i = 0; i < departments.size(); i++) {
                System.out.println(i + ". " + departments.get(i).getName());
            }
            System.out.print("Select department: ");
            int departmentIndex = Integer.parseInt(scanner.nextLine().trim());
            System.out.println("Managers:");
            for (int i = 0; i < managers.size(); i++) {
                System.out.println(i + ". " + managers.get(i).getFullName());
            }
            System.out.print("Select manager to be Dean: ");
            int managerIndex = Integer.parseInt(scanner.nextLine().trim());
            Manager dean = managers.get(managerIndex);
            Department department = departments.get(departmentIndex);
            department.setDeanId(dean.getEmployeeId());
            dean.setManagerType(ManagerType.DEAN);
            System.out.println(dean.getFullName() + " is now Dean of " + department.getName());
            return true;
        } catch (Exception e) {
            System.out.println("Invalid input.");
            return false;
        }
    }

    private Department selectDepartment(List<Department> departments, Scanner scanner) {
        System.out.println("Departments:");
        for (int i = 0; i < departments.size(); i++) {
            System.out.println(i + ". " + departments.get(i).getName());
        }
        System.out.print("Select department index: ");
        return departments.get(Integer.parseInt(scanner.nextLine().trim()));
    }

    private TeacherTitle selectTeacherTitle(Scanner scanner) {
        System.out.println("Select Teacher Title:");
        TeacherTitle[] titles = TeacherTitle.values();
        for (int i = 0; i < titles.length; i++) {
            System.out.println(i + ". " + titles[i]);
        }
        return titles[Integer.parseInt(scanner.nextLine().trim())];
    }

    private ManagerType selectManagerType(Scanner scanner) {
        System.out.println("Select Manager Type:");
        ManagerType[] managerTypes = ManagerType.values();
        for (int i = 0; i < managerTypes.length; i++) {
            System.out.println(i + ". " + managerTypes[i]);
        }
        return managerTypes[Integer.parseInt(scanner.nextLine().trim())];
    }
}
