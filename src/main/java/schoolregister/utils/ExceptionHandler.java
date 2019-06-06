package schoolregister.utils;

import schoolregister.Scenes.TeacherScene;

/**
 * currentType
 * 0 - grades
 * 1 - absences
 * 2 - topics
 * 3 - exams
 */

public class ExceptionHandler {

    public static void onFailUpdate(Exception e, int type) {
        System.out.println("querying failed");
        System.out.println(e);
        switch (type){
            case 0:
                TeacherScene.fillGrades();
            case 1:
                TeacherScene.fillAbsences();
            case 2:
                TeacherScene.fillLessons();
            case 3:
                TeacherScene.fillExams();
        }
        TeacherScene.fillGrades();
    }

    public static void crash(Exception e) {
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        System.exit(0);
    }
}
