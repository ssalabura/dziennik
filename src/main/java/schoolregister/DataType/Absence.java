package schoolregister.DataType;
import schoolregister.utils.Time;

import java.sql.Date;
import java.util.Calendar;


public class Absence {
    private int lessonId;
    private int slot;
    private Date date;
    private String subject;
    public Absence(int lessonId,int slot, Date date, String subject) {
        this.lessonId = lessonId;
        this.slot = slot;
        this.date = date;
        this.subject = subject;
    }

    public int getSlot() {
        return slot;
    }

    public String getDate() {
        return date.toString();
    }

    public String getSubject() {
        return subject;
    }

    public String getDayOfWeek() {
       return Time.dateToDayString(date);
    }

    public int getLessonId() {
        return lessonId;
    }

    public void setLessonId(int lessonId) {
        this.lessonId = lessonId;
    }
}
