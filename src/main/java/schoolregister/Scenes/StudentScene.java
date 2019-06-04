package schoolregister.Scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import schoolregister.DataType.Absence;
import schoolregister.DataType.Grade;
import schoolregister.DataType.Person;
import schoolregister.Database;
import schoolregister.Factory.SceneFactory;
import schoolregister.Factory.TableViewFactory;

import static schoolregister.Main.*;

public class StudentScene {
    private static TableViewFactory tableFactory = TableViewFactory.getInstance();
    public static Scene newScene(int studentId, boolean forGuardian) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.BASELINE_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));


        Person student = Database.getInstance().getPerson(studentId, Person.Type.student);

        Text studentInfo = new Text(student.getName() + " " + student.getSurname());
        studentInfo.setStyle("-fx-font-size: 24");
        studentInfo.setFill(Color.BLACK);
        grid.add(studentInfo, 10, 0);
        if(!forGuardian)
            studentInfo.setVisible(false);

        Label gradesLabel = new Label("Grades");
        gradesLabel.setFont(new Font("Arial", 20));
        TableView<Grade> grades = tableFactory.getGradesFor(studentId);
        grid.add(gradesLabel, 0, 5);
        grid.add(grades, 0 ,6);

        Label absencesLabel = new Label("Absences");
        absencesLabel.setFont(new Font("Arial", 20));
        TableView<Absence> absences = tableFactory.getAbsencesFor(studentId);
        grid.add(absencesLabel, 10, 5);
        grid.add(absences, 10 ,6);


        Button lessonsButton = new Button("Lessons");
        Button backButton = new Button("Back");
        Button nextButton = new Button("next");
        Button prevButton = new Button("prev");
        grid.add(backButton, 1, 21);
        grid.add(lessonsButton, 1, 20);
        grid.add(nextButton, 5, 20);
        grid.add(prevButton, 4, 20);


        if(!forGuardian) {
            nextButton.setVisible(false);
            prevButton.setVisible(false);
        }
        if(forGuardian && (guardianKids.size() <= 1 || currentIndex == guardianKids.size() - 1))
            nextButton.setVisible(false);

        if(forGuardian && currentIndex == 0)
            prevButton.setVisible(false);

        nextButton.setOnAction(actionEvent -> {
            currentIndex++;
            studentScene = studentsScenes.get(currentIndex);
            window.setScene(studentsScenes.get(currentIndex));
        });

        prevButton.setOnAction(actionEvent -> {
            currentIndex--;
            studentScene = studentsScenes.get(currentIndex);
            window.setScene(studentsScenes.get(currentIndex));
        });

        backButton.setOnAction(actionEvent -> window.setScene(mainScene));

        lessonsButton.setOnAction(e -> {
            lessonsTableScene = SceneFactory.getInstance().createLessonTableSceneForStudent(studentId);
            window.setScene(lessonsTableScene);
        });

        return new Scene(grid, 1280, 720);
    }
}
