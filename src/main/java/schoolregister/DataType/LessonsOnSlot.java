package schoolregister.DataType;

public class LessonsOnSlot {
    private Lesson[] lessons = new Lesson[5];
    private String timeInterval;
    private int slotId;

    public LessonsOnSlot(int slotId, String start_time, String end_time) {
        this.slotId = slotId;
        this.timeInterval = start_time + " - " + end_time;
    }


    public Lesson getMon() {
        return lessons[0];
    }

    public Lesson getTue() {
        return lessons[1];
    }

    public Lesson getWed() {
        return lessons[2];
    }

    public Lesson getThu() {
        return lessons[3];
    }

    public Lesson getFri() {
        return lessons[4];
    }


    public void set(int i, Lesson lesson) {
        this.lessons[i] = lesson;
    }

    public void setSlotInterval(String timeInterval) {
        this.timeInterval = timeInterval;
    }

    public String getTimeInterval() {
        return timeInterval;
    }

    public int getSlotId() {
        return slotId;
    }

    public Lesson get(int day_id) {
        return lessons[day_id];
    }
}
