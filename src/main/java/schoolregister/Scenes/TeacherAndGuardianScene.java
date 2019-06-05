package schoolregister.Scenes;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import schoolregister.DataType.*;
import schoolregister.Database;
import schoolregister.Factory.SceneFactory;
import schoolregister.Factory.ViewFactory;
import schoolregister.Wrapper.GroupWrapper;
import schoolregister.Wrapper.IntegerWrapper;
import schoolregister.Wrapper.LessonWrapper;
import schoolregister.Wrapper.PersonWrapper;

import java.util.ArrayList;

import static schoolregister.Main.*;

public class TeacherAndGuardianScene {
    private static ViewFactory viewFactory = ViewFactory.getInstance();
    public static Scene newTeacherScene(int teacherId) {
        TableView<Group> groups = viewFactory.getGroupsFor(teacherId);
        TableView<Grade> grades = viewFactory.getGrades();
        grades.setEditable(true);
        TableView<Person> students = viewFactory.getStudents();
        TableView<Absence> absences = viewFactory.getAbsences();
        TableView<Lesson> lessons = viewFactory.getLessons();

        GroupWrapper currentGroup = new GroupWrapper();
        PersonWrapper currentStudent = new PersonWrapper();
        IntegerWrapper currentType = new IntegerWrapper(0);
        LessonWrapper currentLesson = new LessonWrapper();

        GridPane grid = createGrid();
        Button backButton = new Button("Back");
        Button lessonsButton = new Button("Lessons");
        Button gradesButton = new Button("Grades");
        Button absencesButton = new Button("Absences");
        Button lessonTopicButton = new Button("Topics");

        Label groupsLabel = new Label("Groups");
        groupsLabel.setFont(new Font("Arial", 20));
        Label studentsLabel = new Label("Students");
        studentsLabel.setFont(new Font("Arial", 20));
        Label gradesLabel = new Label("Grades");
        gradesLabel.setFont(new Font("Arial", 20));
        Label absencesLabel = new Label("Absences");
        absencesLabel.setFont(new Font("Arial", 20));
        Label lessonTopicLabel = new Label("Topics");
        lessonTopicLabel.setFont(new Font("Arial", 20));

        backButton.setMinSize(120,30);
        lessonsButton.setMinSize(120,30);
        lessonTopicButton.setMinSize(120,30);
        gradesButton.setMinSize(120,30);
        absencesButton.setMinSize(120,30);

        grid.add(backButton, 0, 4);
        grid.add(lessonsButton, 1, 4);
        grid.add(lessonTopicButton, 1, 5);
        grid.add(gradesButton, 2, 4);
        grid.add(absencesButton, 2, 5);


        grid.add(groupsLabel, 0, 0);
        grid.add(groups, 0, 1);
        groups.setMinSize(300,400);

        grid.add(studentsLabel, 1, 0);
        grid.add(students, 1, 1);
        students.setMinSize(300,400);

        grid.add(gradesLabel, 2, 0);
        grid.add(grades, 2, 1);
        grades.setMinSize(400,400);

        grid.add(absencesLabel, 2, 0);
        grid.add(absences, 2, 1);
        absences.setMinSize(400,400);

        grid.add(lessonTopicLabel, 1, 0);
        grid.add(lessons, 1, 1,2,1);
        lessons.setMinSize(900,400);

        lessonsButton.setOnAction(e -> {
            lessonsTableScene = SceneFactory.getInstance().createLessonTableSceneForTeacher(teacherId);
            window.setScene(lessonsTableScene);
        });

        groups.getSelectionModel().selectedItemProperty().addListener((observableValue, group, t1) -> {
            currentGroup.setGroup(t1);
            if(t1 != null) {
                if(currentType.getValue() != 2)
                    students.setItems(FXCollections.observableArrayList(Database.getInstance().getStudentsFor(t1.getId())));
                else
                    lessons.setItems(FXCollections.observableArrayList(Database.getInstance().getLessons(currentGroup.getGroup().getId(), currentGroup.getGroup().getSubjectId())));
            }
            else{
                if(currentType.getValue() != 2)
                    students.getItems().clear();
                else
                    lessons.getItems().clear();
            }
        });

        students.getSelectionModel().selectedItemProperty().addListener((observableValue, person, t1) -> {
            currentStudent.setPerson(t1);
            if(t1 != null){
                switch (currentType.getValue()){
                    case 0:
                        grades.setItems(FXCollections.observableArrayList(Database.getInstance().getGrades(t1.getId(), currentGroup.getGroup().getId())));
                        break;
                    case 1:
                        absences.setItems(FXCollections.observableArrayList(Database.getInstance().getAbsences(t1.getId(), currentGroup.getGroup().getId(), currentGroup.getGroup().getSubjectId())));
                        break;
                }
            }
            else{
                switch(currentType.getValue()){
                    case 0:
                        grades.getItems().clear();
                        break;
                    case 1:
                        absences.getItems().clear();
                        break;
                }
            }
        });

        absencesLabel.setVisible(false);
        absences.setVisible(false);

        lessonTopicLabel.setVisible(false);
        lessons.setVisible(false);

        backButton.setOnAction(actionEvent -> window.setScene(mainScene));
        gradesButton.setOnAction(actionEvent -> {
            if(currentType.getValue() == 0)
                return;

            absencesLabel.setVisible(false);
            absences.setVisible(false);

            lessonTopicLabel.setVisible(false);
            lessons.setVisible(false);

            if(currentType.getValue() == 2 && currentGroup.getGroup() != null){
                students.setItems(FXCollections.observableArrayList(Database.getInstance().getStudentsFor(currentGroup.getGroup().getId())));
            }

            if(currentStudent.getPerson() != null && currentGroup.getGroup() != null){
                grades.setItems(FXCollections.observableArrayList(Database.getInstance().getGrades(currentStudent.getPerson().getId(), currentGroup.getGroup().getId())));
            }

            studentsLabel.setVisible(true);
            students.setVisible(true);

            gradesLabel.setVisible(true);
            grades.setVisible(true);

            currentType.setValue(0);
        });

        absencesButton.setOnAction(actionEvent -> {
            if(currentType.getValue() == 1)
                return;

            gradesLabel.setVisible(false);
            grades.setVisible(false);

            lessonTopicLabel.setVisible(false);
            lessons.setVisible(false);

            if(currentType.getValue() == 2 && currentGroup.getGroup() != null){
                students.setItems(FXCollections.observableArrayList(Database.getInstance().getStudentsFor(currentGroup.getGroup().getId())));
            }

            if(currentStudent.getPerson() != null && currentGroup.getGroup() != null){
                absences.setItems(FXCollections.observableArrayList(Database.getInstance().getAbsences(currentStudent.getPerson().getId(), currentGroup.getGroup().getId(), currentGroup.getGroup().getSubjectId())));
            }

            studentsLabel.setVisible(true);
            students.setVisible(true);

            absencesLabel.setVisible(true);
            absences.setVisible(true);

            currentType.setValue(1);
        });

        lessonTopicButton.setOnAction(actionEvent -> {
            if(currentType.getValue() == 2)
                return;
            currentStudent.setPerson(null);

            gradesLabel.setVisible(false);
            grades.setVisible(false);

            absencesLabel.setVisible(false);
            absences.setVisible(false);

            studentsLabel.setVisible(false);
            students.setVisible(false);

            if(currentGroup.getGroup() != null){
                lessons.setItems(FXCollections.observableArrayList(Database.getInstance().getLessons(currentGroup.getGroup().getId(), currentGroup.getGroup().getSubjectId())));
            }

            lessonTopicLabel.setVisible(true);
            lessons.setVisible(true);

            currentType.setValue(2);

        });
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
