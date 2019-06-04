package schoolregister.DataType;

public class Absence {
    private int lessonId;
    private int slot;
    private String date;
    private String subject;
    public Absence(int lessonId,int slot, String date, String subject) {
        this.lessonId = lessonId;
        this.slot = slot;
        this.date = date;
        this.subject = subject;
    }

    public int getLessonId() {
        return lessonId;
    }

    public void setLessonId(int lessonId) {
        this.lessonId = lessonId;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
