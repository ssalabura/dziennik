package schoolregister.Scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import schoolregister.Database;
import schoolregister.Factory.SceneFactory;

import java.util.ArrayList;

import static schoolregister.Main.*;

public class TeacherAndGuardianScene {
    public static Scene newTeacherScene(int teacherId) {
        GridPane grid = createGrid();
        Button backButton = new Button("Back");
        Button lessonsButton = new Button("Lessons");

        grid.add(backButton, 1, 21);
        grid.add(lessonsButton, 1, 20);

        lessonsButton.setOnAction(e -> {
            lessonsTableScene = SceneFactory.getInstance().createLessonTableSceneForTeacher(teacherId);
            window.setScene(lessonsTableScene);
        });

        backButton.setOnAction(actionEvent -> window.setScene(mainScene));
        return new Scene(grid, 1280, 720);
    }


    public static Scene newGuardianScene(int guardianId) {
        currentIndex = 0;
        guardianKids = new ArrayList<>(Database.getInstance().getGuardianKids(guardianId));
        studentsScenes = new ArrayList<>();
        GridPane grid = createGrid();

        Button backButton = new Button("Back");
            grid.add(backButton, 1, 21);

            backButton.setOnAction(actionEvent -> window.setScene(mainScene));

            if(guardianKids.isEmpty()) {
            studentsScenes.add(new Scene(grid, 1280, 720));
        }

            for(Integer i : guardianKids){
            studentsScenes.add(SceneFactory.getInstance().createStudentScene(i, true));
            currentIndex++;
        }
        currentIndex = 0;
        studentScene = studentsScenes.get(currentIndex);
        return studentScene;
    }

    private static GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.BASELINE_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        return grid;
    }
}
