package users;

import research.Researcher;
import utils.Department;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public abstract class Employee extends User implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String employeeId;
    protected double salary;
    protected Department department;
    protected Date hireDate;
    protected Researcher researcher;

    public Employee(int id, String firstName, String lastName, String email,
                    String password, String language,
                    String employeeId, double salary, Department department) {
        super(id, firstName, lastName, email, password, language);
        this.employeeId = employeeId;
        this.salary = salary;
        this.department = department;
        this.hireDate = new Date();
    }

    public abstract void work();

    public String getEmployeeId()              { return employeeId; }
    public double getSalary()                  { return salary; }
    public Department getDepartment()          { return department; }
    public Researcher getResearcher()          { return researcher; }
    public void setResearcher(Researcher r)    { this.researcher = r; }
}
