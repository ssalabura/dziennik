package schoolregister.DataType;
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
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayOfWeek =  c.get(Calendar.DAY_OF_WEEK);
        return dayOfWeekToString(dayOfWeek);
    }

    private String dayOfWeekToString(int dayOfWeek) {
        if(dayOfWeek == 2)
            return "Monday";
        if(dayOfWeek == 3)
            return "Tuesday";
        if(dayOfWeek == 4)
            return "Wednesday";
        if(dayOfWeek == 5)
            return "Thursday";
        return "Friday";
    }

    public int getLessonId() {
        return lessonId;
    }

    public void setLessonId(int lessonId) {
        this.lessonId = lessonId;
    }
}
