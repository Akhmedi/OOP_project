package utils;

import java.util.Date;

public class LogEntry {
    private String userId;
    private String action;
    private Date timestamp;

    public LogEntry(String userId, String action) {
        this.userId = userId;
        this.action = action;
        this.timestamp = new Date();
    }

    public String getUserId()   { return userId; }
    public String getAction()   { return action; }
    public Date getTimestamp()  { return timestamp; }

    @Override
    public String toString() {
        return "[" + timestamp + "] User(" + userId + "): " + action;
    }
}