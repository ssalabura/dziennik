package schoolregister.Factory;

import javafx.scene.Scene;
import schoolregister.Scenes.*;

public class SceneFactory {
    private static SceneFactory factory;

    private SceneFactory() {}

    public static SceneFactory getInstance(){
        if(factory == null)
            factory = new SceneFactory();
        return factory;
    }

    public Scene createLoginScene(){
        return LoginScene.newScene();
    }

    public Scene createMainScene(){
        return MainScene.newScene();
    }

    public Scene createLessonTableSceneForStudent(int studentId) {
        LessonsTableScene.setToStudent(studentId);
        return LessonsTableScene.newScene();
    }

    public Scene createLessonTableSceneForTeacher(int teacherId) {
        LessonsTableScene.setToTeacher(teacherId);
        return LessonsTableScene.newScene();
    }

    public Scene createStudentScene(int studentId, boolean forGuardian){
        return StudentScene.newScene(studentId, forGuardian);
    }

    public Scene createTeacherScene(int teacherId){
       return TeacherAndGuardianScene.newTeacherScene(teacherId);
    }

    public Scene createGuardianScene(int guardianId) {
        return TeacherAndGuardianScene.newGuardianScene(guardianId);
    }
}