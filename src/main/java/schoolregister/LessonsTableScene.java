package schoolregister;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import schoolregister.DataType.Lesson;

public class LessonsTableScene {
    private static int id = -1;
    private static boolean isStudent;

    public static void setToStudent(int id) {
        isStudent = true;
        LessonsTableScene.id = id;
    }

    public static void setToTeacher(int id) {
        isStudent = false;
        LessonsTableScene.id = id;
    }

    public static Scene newScene() {
        GridPane grid = new GridPane();

        Lesson[][] lessons = Database.getInstance().getLessonsAssignedTo(id,isStudent);
        for(int i=0;i<lessons.length;i++) {
            for(int j=0;j<lessons[i].length;j++) {
                if(lessons[i][j] != null) {
                    grid.add(new Label(lessons[i][j].toString()), i, j, 1, 1);
                }
            }
        }
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(40);
        grid.setVgap(30);

        Button btn = new Button("Back");
        grid.add(btn, 6, 1);

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Main.window.setScene(Main.studentScene);
            }
        });

        return new Scene(grid, 1280, 720);
    }
}