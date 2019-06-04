package schoolregister.Scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import schoolregister.Factory.SceneFactory;

import static schoolregister.Main.*;
import static schoolregister.Main.studentsScenes;

public class MainScene {
    public static Scene newScene() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.BASELINE_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Button studentButton = new Button("Student");
        Button teacherButton = new Button("Teacher");
        Button guardianButton = new Button("Guardian");
        grid.add(studentButton, 1, 20);
        grid.add(teacherButton, 1, 21);
        grid.add(guardianButton, 1, 22);

        if((userMask & studentMask) == 0)
            studentButton.setVisible(false);
        if((userMask & teacherMask) == 0)
            teacherButton.setVisible(false);
        if((userMask & guardianMask) == 0)
            guardianButton.setVisible(false);

        studentButton.setOnAction(actionEvent -> {
            if(studentScene == null)
                studentScene = SceneFactory.getInstance().createStudentScene(userIDs[studentMask], false);
            window.setScene(studentScene);
        });

        teacherButton.setOnAction(actionEvent -> {
            if(teacherScene == null)
                teacherScene = SceneFactory.getInstance().createTeacherScene(userIDs[teacherMask]);
            window.setScene(teacherScene);
        });

        guardianButton.setOnAction(actionEvent -> {
            SceneFactory.getInstance().createGuardianScene(userIDs[guardianMask]);
            if(studentsScenes != null)
                window.setScene(studentsScenes.get(0));
        });

        return new Scene(grid, 1280, 720);
    }
}
