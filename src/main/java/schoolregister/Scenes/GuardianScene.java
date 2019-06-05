package schoolregister.Scenes;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import schoolregister.Database;
import schoolregister.Factory.SceneFactory;
import schoolregister.Factory.ViewFactory;

import java.util.ArrayList;

import static schoolregister.Main.*;

public class GuardianScene {
    private static ViewFactory viewFactory = ViewFactory.getInstance();

    public static Scene newGuardianScene(int guardianId) {
        currentIndex = 0;
        guardianKids = new ArrayList<>(Database.getInstance().getGuardianKids(guardianId));
        studentsScenes = new ArrayList<>();
        GridPane grid = viewFactory.createGrid();

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
}
