package schoolregister.DataType;

import java.util.ArrayList;
import java.util.Arrays;

public class GradeList {
    private ArrayList<Grade> grades = new ArrayList<>();
    private Subject subject;

    public GradeList(Subject s) {
        this.subject = s;
    }

    public void addGrades(Grade... gradeList) {
        grades.addAll(Arrays.asList(gradeList));
    }

    public String getGrades() {
        StringBuilder builder = new StringBuilder();
        for(Grade g : grades) {
            builder.append(g);
            builder.append(", ");
        }
        if(builder.length() >= 2) {
            builder.deleteCharAt(builder.length() - 1);
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    public String getAverage() {
        double avg = 0;
        int w = 0;
        for(Grade g : grades) {
            avg += (g.getFloatValue()*g.getWeight());
            w += g.getWeight();
        }
        if(w == 0)
            return "";
        return String.format("%.2f", avg/w);
    }


    public String getSubject() {
        return subject.toString();
    }

    public int getSubjectId() {
        return subject.getSubjectId();
    }
}
