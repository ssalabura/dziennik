package schoolregister.Wrapper;

import schoolregister.DataType.Lesson;

public class LessonWrapper {
    private Lesson lesson;

    public LessonWrapper() {}
    public LessonWrapper(Lesson lesson){
        this.lesson = lesson;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }
}
