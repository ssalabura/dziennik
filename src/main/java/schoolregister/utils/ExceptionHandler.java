package schoolregister.utils;

import schoolregister.Scenes.TeacherScene;
import java.sql.SQLException;


public class ExceptionHandler {

    public static void onFailUpdate(SQLException e) {
        System.out.println("querying failed");
        System.out.println(e);
        TeacherScene.fillGrades();
    }

    public static void crash(Exception e) {
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        System.exit(0);
    }
}
