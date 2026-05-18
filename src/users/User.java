package users;

import patterns.DataStorage;
import patterns.Observer;
import utils.LogEntry;
import java.io.Serializable;
import java.util.Objects;

public abstract class User implements Observer, Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String language;

    public User(int id, String firstName, String lastName, String email, String password, String language) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = hashPassword(password);
        this.language = language;
    }

    public boolean login(String email, String password) {
        boolean success = this.email.equals(email) && this.password.equals(hashPassword(password));
        DataStorage.getInstance().writeLog(new LogEntry(String.valueOf(id),
                success ? "login SUCCESS" : "login FAILED"));
        return success;
    }

    public void logout() {
        DataStorage.getInstance().writeLog(new LogEntry(String.valueOf(id), "logout"));
        System.out.println(getFullName() + " logged out.");
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    protected String hashPassword(String password) {
        return "hashed_" + password;
    }

    // Observer pattern - receives news notifications
    @Override
    public void update(String notification) {
        System.out.println("[NOTIFICATION] " + getFullName() + ": " + notification);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User other = (User) obj;
        return this.id == other.id && this.email.equals(other.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + ", name='" + getFullName() + "', email='" + email + "'}";
    }

    public int getId()              { return id; }
    public String getEmail()        { return email; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName)   { this.lastName = lastName; }
    public void setEmail(String email)         { this.email = email; }
}
