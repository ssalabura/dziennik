package schoolregister.DataType;

import java.util.ArrayList;

public class LessonsOnSlot {
    private Lesson[] lessons = new Lesson[5];

    public Lesson getMon() {
        return lessons[0];
    }

    public void setMon(Lesson mon) {
        lessons[0] = mon;
    }

    public Lesson getTue() {
        return lessons[1];
    }

    public void setTue(Lesson tue) {
        this.lessons[1] = tue;
    }

    public Lesson getWed() {
        return lessons[2];
    }

    public void setWed(Lesson wed) {
        this.lessons[2] = wed;
    }

    public Lesson getThu() {
        return lessons[3];
    }

    public void setThu(Lesson thu) {
        this.lessons[3] = thu;
    }

    public Lesson getFri() {
        return lessons[4];
    }

    public void setFri(Lesson fri) {
        this.lessons[4] = fri;
    }

    public void set(int i, Lesson lesson) {
        this.lessons[i] = lesson;
    }
}
