package schoolregister.utils;

import schoolregister.Scenes.TeacherScene;

public class ExceptionHandler {

    public static void onFailUpdate(Exception e) {
        System.out.println("querying failed");
        System.out.println(e);
        TeacherScene.fillGrades();
    }

    public static void onExamUpdateFail(Exception e){
        System.out.println("querying failed");
        System.out.println(e);
        TeacherScene.fillExams();
    }

    public static void crash(Exception e) {
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        System.exit(0);
    }
}
