package schoolregister.Factory;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import schoolregister.*;
import schoolregister.DataType.Absence;
import schoolregister.DataType.Grade;
import schoolregister.Scenes.LessonsTableScene;
import schoolregister.Scenes.LoginScene;

import java.util.ArrayList;

import static schoolregister.Main.*;

public class SceneFactory {
    private static SceneFactory factory;
    private TableViewFactory tableFactory = TableViewFactory.getInstance();

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

        studentButton.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(studentScene == null)
                    studentScene = createStudentScene(userIDs[studentMask], false);
                window.setScene(studentScene);
            }
        });

        teacherButton.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(teacherScene == null)
                    teacherScene = createTeacherScene(userIDs[teacherMask]);
                window.setScene(teacherScene);
            }
        });

        guardianButton.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                createGuardianScene(userIDs[guardianMask]);
                window.setScene(studentsScenes.get(0));
            }
        });

        return new Scene(grid, 1280, 720);
    }

    public Scene createLessonTableSceneForStudent(int studentId) {
        LessonsTableScene.setToStudent(studentId);
        return LessonsTableScene.newScene();
    }

    public Scene createLessonTableSceneForTeacher(int teacherId) {
        LessonsTableScene.setToStudent(teacherId);
        return LessonsTableScene.newScene();
    }

    public Scene createStudentScene(int studentId, boolean forGuardian){
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.BASELINE_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));


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

        nextButton.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                currentIndex++;
                studentScene = studentsScenes.get(currentIndex);
                window.setScene(studentsScenes.get(currentIndex));
            }
        });

        prevButton.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                currentIndex--;
                studentScene = studentsScenes.get(currentIndex);
                window.setScene(studentsScenes.get(currentIndex));
            }
        });

        backButton.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                window.setScene(mainScene);
            }
        });

        lessonsButton.setOnAction(new EventHandler<>() {

            @Override
            public void handle(ActionEvent e) {
                lessonsTableScene = createLessonTableSceneForStudent(studentId);
                window.setScene(lessonsTableScene);
            }
        });

        return new Scene(grid, 1280, 720);
    }

    public Scene createTeacherScene(int teacherId){
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.BASELINE_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Button backButton = new Button("Back");
        grid.add(backButton, 1, 21);

        backButton.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                window.setScene(mainScene);
            }
        });

        return new Scene(grid, 1280, 720);
    }

    public void createGuardianScene(int guardianId){
        currentIndex = 0;
        guardianKids = new ArrayList<>(Database.getInstance().getGuardianKids(guardianId));
        studentsScenes = new ArrayList<>();
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.BASELINE_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Button backButton = new Button("Back");
        grid.add(backButton, 1, 21);

        backButton.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                window.setScene(mainScene);
            }
        });

        if(guardianKids.isEmpty()) {
            studentsScenes.add(new Scene(grid, 1280, 720));
        }

        for(Integer i : guardianKids){
            studentsScenes.add(createStudentScene(i, true));
            currentIndex++;
        }
        currentIndex = 0;
        studentScene = studentsScenes.get(currentIndex);
    }
}