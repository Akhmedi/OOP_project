package academic;

import enums.Letter;
import enums.MarkType;
import java.io.Serializable;
import java.util.Date;

public class Mark implements Serializable {

    private static final long serialVersionUID = 1L;

    private double score;
    private Date date;
    private MarkType type; // ATT1, ATT2, FINAL
    private String courseId;

    public Mark(double score, MarkType type, String courseId) {
        this.score = score;
        this.type = type;
        this.courseId = courseId;
        this.date = new Date();
    }

    public String getDetails() {
        return "Score: " + score + " | Type: " + type + " | Course: " + courseId
                + " | Letter: " + getLetter() + " | Date: " + date;
    }

    public double getScore()  { return score; }
    public MarkType getType() { return type; }
    public Date getDate()     { return date; }
    public String getCourseId(){ return courseId; }

    public Letter getLetter() {
        if (score >= 90) return Letter.A;
        else if (score >= 75) return Letter.B;
        else if (score >= 60) return Letter.C;
        else if (score >= 50) return Letter.D;
        else return Letter.F;
    }

    @Override
    public String toString() {
        return "Mark{" + type + ", score=" + score + ", letter=" + getLetter() + "}";
    }
}