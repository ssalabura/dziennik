package schoolregister.Scenes;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import schoolregister.DataType.*;
import schoolregister.Database;
import schoolregister.Factory.SceneFactory;
import schoolregister.Factory.ViewFactory;
import schoolregister.Wrapper.IntegerWrapper;

import static schoolregister.Main.*;

/**
 * currentType
 * 0 - absences
 * 1 - exams
 * 2 - topics
 */

public class StudentScene {
    private static ViewFactory tableFactory = ViewFactory.getInstance();
    public static Scene newScene(int studentId, boolean forGuardian) {
        IntegerWrapper currentType = new IntegerWrapper(0);
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.BASELINE_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));


        Person student = Database.getInstance().getPerson(studentId, Person.Type.student);

        Text studentInfo = new Text(student.getName() + " " + student.getSurname());
        studentInfo.setStyle("-fx-font-size: 24");
        studentInfo.setFill(Color.BLACK);
        grid.add(studentInfo, 0, 3,2,1);
        if(!forGuardian)
            studentInfo.setVisible(false);

        Label gradesLabel = new Label("Grades");
        gradesLabel.setFont(new Font("Arial", 20));
        TableView<GradeList> grades = tableFactory.getGradesFor(studentId);
        grid.add(gradesLabel, 0, 0,3,1);
        grid.add(grades, 0 ,1,3,1);

        Label absencesLabel = new Label("Absences");
        absencesLabel.setFont(new Font("Arial", 20));
        TableView<Absence> absences = tableFactory.getAbsencesFor(studentId);
        grid.add(absencesLabel, 3, 0,2,1);
        grid.add(absences, 3 ,1,2,1);

        Label examsLabel = new Label("Exams");
        examsLabel.setFont(new Font("Arial", 20));
        TableView<Exam> exams = tableFactory.getExamsForStudent(studentId);
        exams.setMinSize(700, 300);
        grid.add(examsLabel, 3, 0, 2, 1);
        grid.add(exams, 3, 1, 2, 1);

        Label topicsLabel = new Label("Topics");
        topicsLabel.setFont(new Font("Arial", 20));
        TableView<Lesson> topics = tableFactory.getLessonsForStudent(studentId);
        topics.setMinSize(700, 300);
        grid.add(topicsLabel, 3, 0, 2, 1);
        grid.add(topics, 3, 1, 2, 1);


        Button lessonsButton = new Button("Plan");
        Button absencesButton = new Button("Absences");
        Button examsButton = new Button("Exams");
        Button topicsButton = new Button("Topics");
        Button backButton = new Button("Back");
        Button nextButton = new Button("Next");
        Button prevButton = new Button("Prev");


        backButton.setMinSize(80,30);
        lessonsButton.setMinSize(80,30);
        nextButton.setMinSize(80,30);
        prevButton.setMinSize(80,30);
        examsButton.setMinSize(80, 30);
        topicsButton.setMinSize(80,30);
        absencesButton.setMinSize(80, 30);


        grid.add(backButton, 0, 4);
        grid.add(lessonsButton, 1, 4);
        grid.add(examsButton, 1, 5);
        grid.add(topicsButton, 2, 5);
        grid.add(absencesButton, 2, 4);
        grid.add(prevButton, 3, 4,1,1);
        grid.add(nextButton, 4, 4,1,1);
        GridPane.setHalignment(prevButton, HPos.RIGHT);
        GridPane.setHalignment(nextButton,HPos.RIGHT);

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

        absencesButton.setOnAction(actionEvent -> {
            if(currentType.getValue() == 0)
                return;
            setVisible(false, examsLabel, exams, topicsLabel, topics);

            setVisible(true, absencesLabel, absences);

            currentType.setValue(0);
        });

        examsButton.setOnAction(actionEvent -> {
            if(currentType.getValue() == 1)
                return;
            setVisible(false, absencesLabel, absences, topicsLabel, topics);

            setVisible(true, examsLabel, exams);

            currentType.setValue(1);
        });

        topicsButton.setOnAction(actionEvent -> {
            if(currentType.getValue() == 2)
                return;
            setVisible(false, absencesLabel, absences, examsLabel, exams);

            setVisible(true, topicsLabel, topics);

            currentType.setValue(2);
        });


        setVisible(false, examsLabel, exams);
        setVisible(false, topicsLabel, topics);

        return new Scene(grid, 1280, 720);
    }

    private static void setVisible(boolean isVisible, Node... args){
        for(Node node : args){
            node.setVisible(isVisible);
        }
    }

}
