package utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Department implements Serializable {
    private static final long serialVersionUID = 1L;
    private String departmentId;
    private String name;
    private String description;
    private String deanId;
    private List<String> staffIds;

    public Department(String departmentId, String name, String description) {
        this.departmentId = departmentId;
        this.name = name;
        this.description = description;
        this.staffIds = new ArrayList<>();
    }

    public void addStaff(String employeeId)    { staffIds.add(employeeId); }
    public void removeStaff(String employeeId) { staffIds.remove(employeeId); }
    public List<String> getStaffIds()          { return staffIds; }

    public String getDepartmentId()            { return departmentId; }
    public String getName()                    { return name; }
    public String getDescription()             { return description; }
    public String getDeanId()                  { return deanId; }
    public void setDeanId(String deanId)       { this.deanId = deanId; }

    @Override
    public String toString() {
        return "Department{" + name + ", dean=" + deanId + ", staff=" + staffIds.size() + "}";
    }
}
