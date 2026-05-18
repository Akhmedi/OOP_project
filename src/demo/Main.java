package demo;

import academic.*;
import enums.*;
import exceptions.*;
import patterns.DataStorage;
import research.*;
import users.*;
import utils.*;
import java.util.*;

public class Main {

    // System data
    static List<User> allUsers = new ArrayList<>();
    static List<Student> students = new ArrayList<>();
    static List<Teacher> teachers = new ArrayList<>();
    static List<Manager> managers = new ArrayList<>();
    static List<ResearchAssistant> researchAssistants = new ArrayList<>();
    static List<Admin> admins = new ArrayList<>();
    static List<Course> courses = new ArrayList<>();
    static List<Researcher> researchers = new ArrayList<>();
    static List<Department> departments = new ArrayList<>();
    static List<Room> rooms = new ArrayList<>();
    static List<News> allNews = new ArrayList<>();
    static List<ResearchProject> allResearchProjects = new ArrayList<>();
    static Schedule schedule;
    static Scanner scanner = new Scanner(System.in);
    static DataStorage dataStorage = DataStorage.getInstance();

    public static void main(String[] args) {
        DataStorage.DataWrapper wrapper = new DataStorage.DataWrapper();
        if (!dataStorage.loadSystemData(allUsers, students, teachers, managers, admins, researchers,
                                        courses, rooms, departments, allNews, allResearchProjects, wrapper)) {
            initData();
            saveData();
        } else {
            schedule = wrapper.schedule;
        }
        
        System.out.println("=== University Information System ===");

        boolean running = true;
        while (running) {
            System.out.println("\n--- LOGIN ---");
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine().trim();

            User user;
            try {
                user = dataStorage.authenticate(allUsers, email, password);
            } catch (AuthenticationException e) {
                System.out.println("Login failed: " + e.getMessage());
                continue;
            }

            System.out.println("Welcome, " + user.getFullName() + "!");
            running = showMenu(user);
            saveData(); // Save after each session or change
        }
        saveData();
        System.out.println("Goodbye!");
    }

    static void saveData() {
        dataStorage.saveSystemData(allUsers, courses, rooms, departments, schedule, allNews,
                allResearchProjects);
    }

    static Comparator<ResearchPaper> resolvePaperComparator(String choice) {
        if ("2".equals(choice)) return Researcher.byDate();
        if ("3".equals(choice)) return Researcher.byPages();
        return Researcher.byCitations();
    }

    static void printResearchProjects() {
        System.out.println("--- Research Projects ---");
        for (int i = 0; i < allResearchProjects.size(); i++) {
            ResearchProject project = allResearchProjects.get(i);
            System.out.println(i + ". " + project.getName() + " (Topic: " + project.getTopic() + ")");
        }
    }

    static void addResearcherIfAbsent(Researcher researcher) {
        if (researcher != null && !researchers.contains(researcher)) {
            researchers.add(researcher);
        }
    }

    // ===== RESEARCH MENU =====
    static void researchMenu(Researcher researcher) {
        while (true) {
            System.out.println("\n=== RESEARCH MENU: " + researcher.getOwner().getFullName() + " ===");
            System.out.println("H-Index: " + researcher.getHIndex());
            System.out.println("1. View My Papers");
            System.out.println("2. Publish New Paper");
            System.out.println("3. View All Research Projects");
            System.out.println("4. Create New Research Project");
            System.out.println("5. Join a Research Project");
            System.out.println("6. Cite a Paper (Boost author's H-index)");
            System.out.println("7. View & Approve/Reject Join Requests");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("0")) break;

            switch (choice) {
                case "1":
                    System.out.println("Sort by: 1=Citations 2=Date 3=Pages");
                    researcher.printPapers(resolvePaperComparator(scanner.nextLine().trim()));
                    break;
                case "2":
                    System.out.print("Paper Title: ");
                    String title = scanner.nextLine();
                    System.out.print("Journal: ");
                    String journal = scanner.nextLine();
                    List<String> authors = new ArrayList<>();
                    authors.add(researcher.getOwner().getFullName());
                    ResearchPaper paper = new ResearchPaper("P" + System.currentTimeMillis(), 
                            title, authors, new Date(), journal, 0, 0, "DOI", "Tags");
                    researcher.publishPaper(paper);
                    saveData();
                    break;
                case "3":
                    printResearchProjects();
                    break;
                case "4":
                    System.out.print("Project Name: ");
                    String pName = scanner.nextLine();
                    System.out.print("Topic: ");
                    String pTopic = scanner.nextLine();
                    try {
                        ResearchProject newProj = new ResearchProject("PR" + System.currentTimeMillis(),
                                pTopic, pName, researcher);
                        allResearchProjects.add(newProj);
                        System.out.println("Project created.");
                        saveData();
                    } catch (LowHIndexException e) {
                        System.out.println("Failed to create project: " + e.getMessage());
                    }                    break;
                case "5":
                    System.out.println("Select project index to join:");
                    for (int i = 0; i < allResearchProjects.size(); i++) {
                        System.out.println(i + ". " + allResearchProjects.get(i).getName());
                    }
                    try {
                        int pi = Integer.parseInt(scanner.nextLine().trim());
                        researcher.requestToJoin(allResearchProjects.get(pi));
                        saveData();
                    } catch (Exception e) {
                        System.out.println("Error.");
                    }
                    break;
                case "6":
                    // Simple citation logic
                    System.out.println("All Researchers:");
                    for (int i = 0; i < researchers.size(); i++) {
                        System.out.println(i + ". " + researchers.get(i).getOwner().getFullName());
                    }
                    try {
                        System.out.print("Select researcher index to cite their work: ");
                        int ri = Integer.parseInt(scanner.nextLine().trim());
                        Researcher target = researchers.get(ri);
                        if (target.getPapers().isEmpty()) {
                            System.out.println("This researcher has no papers to cite.");
                        } else {
                            System.out.println("Papers:");
                            List<ResearchPaper> papers = target.getPapers();
                            for (int j = 0; j < papers.size(); j++) {
                                System.out.println(j + ". " + papers.get(j).getTitle());
                            }
                            System.out.print("Select paper index: ");
                            int pj = Integer.parseInt(scanner.nextLine().trim());
                            papers.get(pj).addCitation();
                            System.out.println("Paper cited! " + target.getOwner().getFullName() + "'s H-index might increase.");
                            saveData();
                        }
                    } catch (Exception e) {
                        System.out.println("Error.");
                    }
                    break;

                case "7":
                    if (researcher.getProjects().isEmpty()) {
                        System.out.println("You have no projects.");
                        break;
                    }
                    for (ResearchProject p : researcher.getProjects()) {
                        p.viewPendingRequests();
                        List<JoinRequest> pending = p.getPendingRequests();
                        if (!pending.isEmpty()) {
                            System.out.println("1. Approve  2. Reject  0. Skip");
                            System.out.print("Choice: ");
                            String rc = scanner.nextLine().trim();
                            if (rc.equals("1") || rc.equals("2")) {
                                System.out.print("Enter request number: ");
                                try {
                                    int ri = Integer.parseInt(scanner.nextLine().trim());
                                    if (ri < 0 || ri >= pending.size()) {
                                        System.out.println("Invalid request number.");
                                        continue;
                                    }
                                    JoinRequest req = pending.get(ri);
                                    if (rc.equals("1")) {
                                        p.approveRequest(req, req.getResearcher());
                                    } else {
                                        p.rejectRequest(req, req.getResearcher());
                                    }
                                    saveData();
                                } catch (Exception e) {
                                    System.out.println("Invalid input.");
                                }
                            }
                        }
                    }
                    break;
            }
        }
    }


    static boolean showMenu(User user) {
        if (user instanceof Admin) return adminMenu((Admin) user);
        if (user instanceof Manager) return managerMenu((Manager) user);
        if (user instanceof Teacher) return teacherMenu((Teacher) user);
        if (user instanceof Student) return studentMenu((Student) user);
        if (user instanceof ResearchAssistant) return researchAssistantMenu((ResearchAssistant) user);
        return true;
    }

    // ===== RESEARCH ASSISTANT MENU =====
    static boolean researchAssistantMenu(ResearchAssistant assistant) {
        while (true) {
            System.out.println("\n=== Research Assistant Menu: " + assistant.getFullName() + " ===");
            System.out.println("1. Work");
            System.out.println("2. View News");
            System.out.println("3. Research Menu");
            System.out.println("4. Logout");
            System.out.println("0. Exit");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    assistant.work();
                    break;
                case "2":
                    News.printAll(allNews);
                    break;
                case "3":
                    researchMenu(assistant.getResearcher());
                    break;
                case "4":
                    assistant.logout();
                    return true;
                case "0":
                    return false;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // ===== STUDENT MENU =====
    static boolean studentMenu(Student student) {
        while (true) {
            System.out.println("\n=== Student Menu: " + student.getFullName() + " ===");
            System.out.println("1. View My Courses");
            System.out.println("2. Register for Course");
            System.out.println("3. View Grades");
            System.out.println("4. View Transcript");
            System.out.println("5. Rate Teacher");
            System.out.println("6. View Teacher Info");
            System.out.println("7. Check Registration Eligibility");
            System.out.println("8. View News");
            if (student.getYearOfStudy() == 4) System.out.println("12. Assign Research Supervisor");
            if (student.getResearcher() == null) System.out.println("13. Become Researcher");
            else System.out.println("11. Research Menu");
            System.out.println("10. Logout");
            System.out.println("0. Exit");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    student.viewCourses();
                    break;
                case "2":
                    System.out.println("Available courses:");
                    for (int i = 0; i < courses.size(); i++) {
                        System.out.println(i + ". " + courses.get(i));
                    }
                    System.out.print("Select course index: ");
                    try {
                        int idx = Integer.parseInt(scanner.nextLine().trim());
                        if (idx >= 0 && idx < courses.size()) {
                            student.registerCourse(courses.get(idx));
                        }
                    } catch (MaxCreditsException e) {
                        System.out.println("ERROR: " + e.getMessage());
                    } catch (InvalidCourseRegistrationException e) {
                        System.out.println("REGISTRATION ERROR: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("Invalid input.");
                    }
                    break;
                case "3":
                    student.viewGrades();
                    break;
                case "4":
                    System.out.println(student.getTranscript());
                    break;
                case "5":
                    System.out.println("Teachers:");
                    for (int i = 0; i < teachers.size(); i++) {
                        System.out.println(i + ". " + teachers.get(i).getFullName()
                                + " | Rating: " + String.format("%.2f", teachers.get(i).getRating()));
                    }
                    System.out.print("Select teacher index: ");
                    try {
                        int idx = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Score (1-5): ");
                        int score = Integer.parseInt(scanner.nextLine().trim());
                        student.rateTeacher(teachers.get(idx), score);
                    } catch (Exception e) {
                        System.out.println("Invalid input.");
                    }
                    break;
                case "6":
                    System.out.println("Your courses:");
                    List<Course> myCourses = student.getCourses();
                    for (int i = 0; i < myCourses.size(); i++) {
                        System.out.println(i + ". " + myCourses.get(i).getCourseName());
                    }
                    System.out.print("Select course index: ");
                    try {
                        int idx = Integer.parseInt(scanner.nextLine().trim());
                        student.viewTeacherInfo(myCourses.get(idx));
                    } catch (Exception e) {
                        System.out.println("Invalid input.");
                    }
                    break;
                case "7":
                    System.out.println("Can register: " + student.canRegisterForCourse());
                    System.out.println("Credits used: " + student.getCredits() + "/21");
                    System.out.println("Fail count: " + student.getFailCount() + "/3");
                    break;
                case "8":
                    News.printAll(allNews);
                    break;
                case "11":
                    if (student.getResearcher() != null) researchMenu(student.getResearcher());
                    break;
                case "12":
                    System.out.println("Professors / researchers:");
                    List<Teacher> supervisorCandidates = new ArrayList<>();
                    for (Teacher t : teachers) {
                        if (t.getResearcher() != null) {
                            supervisorCandidates.add(t);
                            System.out.println((supervisorCandidates.size() - 1) + ". " + t.getFullName()
                                    + " | H-Index: " + t.getResearcher().getHIndex());
                        }
                    }
                    if (supervisorCandidates.isEmpty()) {
                        System.out.println("No eligible supervisors.");
                        break;
                    }
                    System.out.print("Supervisor index: ");
                    try {
                        int idx = Integer.parseInt(scanner.nextLine().trim());
                        student.setResearchSupervisor(supervisorCandidates.get(idx));
                        saveData();
                    } catch (SupervisorAssignmentException | LowHIndexException | NotResearcherException e) {
                        System.out.println("ERROR: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("Invalid input.");
                    }
                    break;
                case "13":
                    student.becomeResearcher();
                    addResearcherIfAbsent(student.getResearcher());
                    saveData();
                    break;
                case "10":
                    student.logout();
                    return true;
                case "0":
                    return false;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // ===== TEACHER MENU =====
    static boolean teacherMenu(Teacher teacher) {
        while (true) {
            System.out.println("\n=== Teacher Menu: " + teacher.getFullName() + " ===");
            System.out.println("1. View My Courses");
            System.out.println("2. View Students");
            System.out.println("3. Put Mark");
            System.out.println("4. Generate Marks Report");
            System.out.println("5. View News");
            if (teacher.getResearcher() == null && !teacher.isProfessor())
                System.out.println("6. Become Researcher");
            if (teacher.getResearcher() != null) System.out.println("7. Research Menu");
            System.out.println("8. Logout");
            System.out.println("0. Exit");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    teacher.viewCourses();
                    break;
                case "2":
                    List<Student> myStudents = teacher.viewStudents();
                    for (Student s : myStudents) {
                        teacher.viewStudentInfo(s);
                    }
                    break;
                case "3":
                    List<Course> teacherCourses = teacher.getCourses();
                    if (teacherCourses.isEmpty()) {
                        System.out.println("No assigned courses.");
                        break;
                    }
                    System.out.println("Your courses:");
                    for (int i = 0; i < teacherCourses.size(); i++) {
                        System.out.println(i + ". " + teacherCourses.get(i).getCourseName());
                    }
                    System.out.print("Course index: ");
                    try {
                        int courseIndex = Integer.parseInt(scanner.nextLine().trim());
                        Course selectedCourse = teacherCourses.get(courseIndex);
                        List<Student> enrolledStudents = selectedCourse.getEnrolledStudents();
                        if (enrolledStudents.isEmpty()) {
                            System.out.println("No students in this course.");
                            break;
                        }
                        for (int i = 0; i < enrolledStudents.size(); i++) {
                            System.out.println(i + ". " + enrolledStudents.get(i).getFullName());
                        }
                        System.out.print("Student index: ");
                        int studentIndex = Integer.parseInt(scanner.nextLine().trim());
                        Student selectedStudent = enrolledStudents.get(studentIndex);
                        System.out.print("Score (0-100): ");
                        double score = Double.parseDouble(scanner.nextLine().trim());
                        System.out.println("Mark type: 1=ATT1, 2=ATT2, 3=FINAL");
                        int mt = Integer.parseInt(scanner.nextLine().trim());
                        MarkType mtype = mt == 1 ? MarkType.ATT1 : mt == 2 ? MarkType.ATT2 : MarkType.FINAL;
                        Mark mark = new Mark(score, mtype, selectedCourse.getCourseId());
                        teacher.putMark(selectedStudent, selectedCourse, mark);
                    } catch (Exception e) {
                        System.out.println("Invalid input.");
                    }
                    break;
                case "4":
                    teacher.generateMarksReport();
                    break;
                case "5":
                    News.printAll(allNews);
                    break;
                case "6":
                    if (!teacher.isProfessor()) {
                        teacher.becomeResearcher();
                        addResearcherIfAbsent(teacher.getResearcher());
                        saveData();
                    }
                    break;
                case "7":
                    if (teacher.getResearcher() != null) researchMenu(teacher.getResearcher());
                    break;
                case "8":
                    teacher.logout();
                    return true;
                case "0":
                    return false;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // ===== MANAGER MENU =====
    static boolean managerMenu(Manager manager) {
        while (true) {
            System.out.println("\n=== Manager Menu (" + manager.getManagerType() + "): " + manager.getFullName() + " ===");
            
            List<String> actions = new ArrayList<>();
            if (manager.isORManager()) {
                actions.add("Approve Student Registration");
                actions.add("Assign Course to Teacher");
                actions.add("Add Course for Registration");
                actions.add("Schedule New Lesson");
                actions.add("View Schedule");
            }
            
            if (manager.isRector()) {
                actions.add("Assign Dean to Department");
                actions.add("View All Departments");
                actions.add("View Schedule");
            }
            
            actions.add("View Department Staff");
            actions.add("Create Statistical Report");
            actions.add("Publish News");
            actions.add("View News");
            actions.add("View Students by GPA");
            actions.add("View Students Alphabetically");
            actions.add("View Teachers by Rating");
            actions.add("Logout");

            // Display sequential menu
            for (int i = 0; i < actions.size(); i++) {
                System.out.println((i + 1) + ". " + actions.get(i));
            }
            System.out.println("0. Exit");
            
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();
            if (choice.equals("0")) return false;
            
            int actionIdx;
            try {
                actionIdx = Integer.parseInt(choice) - 1;
                if (actionIdx < 0 || actionIdx >= actions.size()) {
                    System.out.println("Invalid choice.");
                    continue;
                }
            } catch (Exception e) {
                System.out.println("Invalid input.");
                continue;
            }

            String selectedAction = actions.get(actionIdx);

            switch (selectedAction) {
                case "Approve Student Registration":
                    manager.handleRegistrationApproval(students, courses, scanner);
                    break;
                case "Assign Course to Teacher":
                    manager.handleTeacherCourseAssignment(teachers, courses, scanner);
                    break;
                case "Add Course for Registration":
                    manager.handleCourseRegistrationSetup(courses, scanner);
                    break;
                case "Assign Dean to Department":
                    if (manager.handleDeanAssignment(departments, managers, scanner)) {
                        saveData();
                    }
                    break;
                case "View All Departments":
                    System.out.println("University Departments:");
                    for (Department dpt : departments) {
                        System.out.println(dpt);
                    }
                    break;
                case "Schedule New Lesson":
                    if (manager.handleLessonScheduling(courses, teachers, schedule, scanner)) {
                        saveData();
                    }
                    break;
                case "View Schedule":
                    schedule.printSchedule();
                    break;
                case "View Department Staff":
                    manager.viewDepartmentStaff(departments, allUsers, scanner);
                    break;
                case "Create Statistical Report":
                    manager.createReport(students);
                    break;
                case "Publish News":
                    manager.handleNewsPublishing(allNews, scanner);
                    saveData();
                    break;
                case "View News":
                    News.printAll(allNews);
                    break;
                case "View Students by GPA":
                    manager.viewStudentsByGpa(students);
                    break;
                case "View Students Alphabetically":
                    manager.viewStudentsAlphabetically(students);
                    break;
                case "View Teachers by Rating":
                    manager.viewTeachersByRating(teachers);
                    break;
                case "Logout":
                    manager.logout();
                    return true;
                default:
                    System.out.println("Action not implemented.");
            }
        }
    }

    // ===== ADMIN MENU =====
    static boolean adminMenu(Admin admin) {
        while (true) {
            System.out.println("\n=== Admin Menu: " + admin.getFullName() + " ===");
            System.out.println("1. Add User");
            System.out.println("2. Remove User");
            System.out.println("3. Update User");
            System.out.println("4. View Logs");
            System.out.println("5. Manage Departments");
            System.out.println("6. Search User by Name (regex)");
            System.out.println("7. Change Student Status");
            System.out.println("8. Award Bachelor Degree");
            System.out.println("9. View News");
            System.out.println("10. Logout");
            System.out.println("11. View University Research");
            System.out.println("0. Exit");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    if (admin.handleUserCreation(allUsers, students, teachers, managers,
                            researchAssistants, researchers, departments, scanner)) {
                        saveData();
                    }
                    break;
                case "2":
                    if (admin.handleUserRemoval(allUsers, scanner)) {
                        saveData();
                    }
                    break;
                case "3":
                    if (admin.handleUserUpdate(allUsers, scanner)) {
                        saveData();
                    }
                    break;
                case "4":
                    System.out.print("Filter (or press Enter for all): ");
                    String filter = scanner.nextLine().trim();
                    admin.viewLogs(filter.isEmpty() ? null : filter);
                    break;
                case "5":
                    if (admin.handleDepartmentManagement(departments, managers, scanner)) {
                        saveData();
                    }
                    break;
                case "6":
                    System.out.print("Enter regex (e.g. 'Ali.*'): ");
                    String regex = scanner.nextLine().trim();
                    List<Student> found = Student.searchByName(students, regex);
                    System.out.println("Found " + found.size() + " students:");
                    for (Student s : found) {
                        System.out.println(s);
                    }
                    break;
                case "7":
                    if (admin.handleStudentStatusChange(students, scanner)) {
                        saveData();
                    }
                    break;
                case "8":
                    if (admin.handleBachelorAward(students, scanner)) {
                        saveData();
                    }
                    break;
                case "9":
                    News.printAll(allNews);
                    break;
                case "10":
                    admin.logout();
                    return true;
                case "11":
                    Researcher.viewUniversityResearchStatistics(scanner, researchers, dataStorage);
                    break;
                case "0":
                    return false;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // ===== INIT SAMPLE DATA =====
    static void initData() {
        // Rooms
        rooms.add(new Room("R1", "Hall-101", 200, RoomType.LECTURE_HALL));
        rooms.add(new Room("R2", "Lab-201", 30, RoomType.LAB));
        rooms.add(new Room("R3", "Sem-301", 40, RoomType.SEMINAR_ROOM));
        schedule = new Schedule(rooms);

        // Department
        Department dept = new Department("D1", "Computer Science", "CS Department");
        departments.add(dept);

        // Admin
        Admin admin = new Admin(1, "Admin", "System", "admin@kbtu.kz", "admin123", "EN");
        admins.add(admin);
        allUsers.add(admin);

        // Manager
        Manager manager1 = new Manager(2, "Azamat", "Imanbayev", "a_imanbayev@kbtu.kz", "azamat123", "EN",
                "M001", 80000, dept, ManagerType.DEAN);
        dept.setDeanId(manager1.getEmployeeId());
        dept.addStaff(manager1.getEmployeeId());
        
        Manager manager2 = new Manager(3, "Guldana", "Kassymbayeva", "g_kassymbayeva@kbtu.kz", "guldana123", "EN",
                "M002", 80000, dept, ManagerType.OR_MANAGER);
        dept.addStaff(manager2.getEmployeeId());
        
        managers.add(manager1);
        allUsers.add(manager1);
        managers.add(manager2);
        allUsers.add(manager2);

        // Rector
        Manager rector = new Manager(10, "Maratbek", "Gabdullin", "m_gabdullin@kbtu.kz", "rector123", "EN",
                "R001", 150000, dept, ManagerType.RECTOR);
        managers.add(rector);
        allUsers.add(rector);

        // Teachers
        Teacher teacher1 = new Teacher(4, "Dmitrii", "Tuchashvili", "d_tuchashvili@kbtu.kz", "dmitrii123", "EN",
                "T001", 60000, dept, TeacherTitle.PROFESSOR);
        Teacher teacher2 = new Teacher(5, "Miras", "Assubay", "m_assubay@kbtu.kz", "miras123", "EN",
                "T002", 55000, dept, TeacherTitle.LECTURER);
        dept.addStaff(teacher1.getEmployeeId());
        dept.addStaff(teacher2.getEmployeeId());
        teachers.add(teacher1);
        teachers.add(teacher2);
        allUsers.add(teacher1);
        allUsers.add(teacher2);

        // Research Assistant (employee, not teacher or student)
        ResearchAssistant researchAssistant = new ResearchAssistant(11, "Arman", "Nurlanov",
                "a_nurlanov@kbtu.kz", "arman123", "EN",
                "RA001", 45000, dept);
        dept.addStaff(researchAssistant.getEmployeeId());
        researchAssistants.add(researchAssistant);
        researchers.add(researchAssistant.getResearcher());
        allUsers.add(researchAssistant);

        // Students subscribe to manager news
        manager2.addObserver(teacher1);
        manager2.addObserver(teacher2);

        // Researchers
        if (teacher1.getResearcher() != null) {
            // Add some papers to professor
            List<String> authors = new ArrayList<>();
            authors.add("Dmitrii Tuchashvili");
            ResearchPaper paper1 = new ResearchPaper("P001", "Deep Learning in Education",
                    authors, new Date(), "IEEE", 45, 12, "10.1109/xxx", "AI, Education");
            ResearchPaper paper2 = new ResearchPaper("P002", "OOP Patterns Analysis",
                    authors, new Date(), "ACM", 30, 8, "10.1145/xxx", "OOP, Design");
            teacher1.getResearcher().publishPaper(paper1);
            teacher1.getResearcher().publishPaper(paper2);
            researchers.add(teacher1.getResearcher());
        }

        // Students
        Student student1 = new Student(6, "Dizhan", "Mashirov", "d_mashirov@kbtu.kz", "dizhan123", "KZ",
                "ST001", 2023, "SE", 2);
        Student student2 = new Student(7, "Akhmedi", "Orynbassar", "a_orynbassar@kbtu.kz", "akhmedi123", "KZ",
                "ST002", 2022, "CS", 3);
        Student student3 = new Student(8, "Bexultan", "Turar", "b_turar@kbtu.kz", "bexultan123", "KZ",
                "ST003", 2021, "CS", 4);
        Student student4 = new Student(9, "Akezhan", "Sarkyt", "a_sarkyt@kbtu.kz", "akezhan123", "KZ",
                "ST004", 2021, "CS", 1);
        student1.setDegreeType(DegreeType.BACHELOR);
        student2.setDegreeType(DegreeType.BACHELOR);
        student3.setDegreeType(DegreeType.BACHELOR);
        student4.setDegreeType(DegreeType.BACHELOR);
        students.add(student1);
        students.add(student2);
        students.add(student3);
        students.add(student4);
        allUsers.add(student1);
        allUsers.add(student2);
        allUsers.add(student3);
        allUsers.add(student4);

        // Subscribe students to news
        manager2.addObserver(student1);
        manager2.addObserver(student2);

        // 4th year student researcher + supervisor
        try {
            student3.becomeResearcher();
            researchers.add(student3.getResearcher());
            student3.setResearchSupervisor(teacher1);
        } catch (SupervisorAssignmentException | LowHIndexException | NotResearcherException e) {
            System.out.println("Init supervisor note: " + e.getMessage());
        }

        // Courses
        Course course1 = new Course("CS101", "Introduction to Programming", 3,
                "Basics of programming", "CS");
        Course course2 = new Course("CS201", "Object-Oriented Programming", 3,
                "OOP concepts", "CS");
        Course course3 = new Course("CS301", "Algorithms", 3,
                "Data structures and algorithms", "CS");
        courses.add(course1);
        courses.add(course2);
        courses.add(course3);
        course1.setTargetMajor("CS");
        course1.setTargetYear(1);
        course2.setTargetMajor("CS");
        course2.setTargetYear(2);
        course3.setTargetMajor("CS");
        course3.setTargetYear(3);

        // Assign courses to teacher
        teacher1.manageCourse(course1);
        teacher1.manageCourse(course2);
        teacher2.manageCourse(course3);

        // Register students for courses
        try {
            student1.registerCourse(course1);
            student2.registerCourse(course2);
        } catch (MaxCreditsException | InvalidCourseRegistrationException e) {
            System.out.println("Init error: " + e.getMessage());
        }

        // Add some marks
        Mark m1 = new Mark(85, MarkType.ATT1, "CS101");
        Mark m2 = new Mark(90, MarkType.ATT2, "CS101");
        teacher1.putMark(student1, course1, m1);
        teacher1.putMark(student1, course1, m2);

        // Schedule lessons
        try {
            Lesson l1 = schedule.scheduleLesson(LessonType.LECTURE, course1, teacher1, 90);
            Lesson l2 = schedule.scheduleLesson(LessonType.PRACTICE, course2, teacher2, 90);
            if (l1 != null) course1.addSchedule(l1);
            if (l2 != null) course2.addSchedule(l2);
        } catch (RoomOccupiedException e) {
            System.out.println("Init Schedule Error: " + e.getMessage());
        }
    }
}
