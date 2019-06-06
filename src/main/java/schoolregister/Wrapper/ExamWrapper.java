package schoolregister.Wrapper;

import schoolregister.DataType.Exam;

public class ExamWrapper {
    private Exam exam;

    public ExamWrapper() {}
    public ExamWrapper(Exam exam){
        this.exam = exam;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }
}
