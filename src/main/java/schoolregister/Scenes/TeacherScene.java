package schoolregister.Scenes;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.Pair;
import schoolregister.DataType.*;
import schoolregister.Database;
import schoolregister.Dialogs.AbsencesDialog;
import schoolregister.Dialogs.AddGradeDialog;
import schoolregister.Factory.SceneFactory;
import schoolregister.Factory.ViewFactory;
import schoolregister.Wrapper.GroupWrapper;
import schoolregister.Wrapper.IntegerWrapper;
import schoolregister.Wrapper.LessonWrapper;
import schoolregister.Wrapper.PersonWrapper;
import schoolregister.utils.ExceptionHandler;

import java.sql.SQLException;

import static schoolregister.Main.*;

public class TeacherScene {
    private static ViewFactory viewFactory = ViewFactory.getInstance();

    public static TableView<Group> groups;
    public static TableView<Grade> grades;
    public static TableView<Person> students;
    public static TableView<Absence> absences;
    public static TableView<Lesson> lessons;

    public static GroupWrapper currentGroup;
    public static PersonWrapper currentStudent;
    public static LessonWrapper currentLesson;
    private static Button addRowButton;
    private static Button deleteRowButton;

    public static Scene newTeacherScene(int teacherId) {
        groups = viewFactory.getGroupsFor(teacherId);
        grades = viewFactory.getGrades();
        grades.setEditable(true);
        students = viewFactory.getStudents();
        absences = viewFactory.getAbsences();
        lessons = viewFactory.getLessons();

        currentGroup = new GroupWrapper();
        currentStudent = new PersonWrapper();
        IntegerWrapper currentType = new IntegerWrapper(0);
        currentLesson = new LessonWrapper();

        GridPane grid = viewFactory.createGrid();
        Button backButton = new Button("Back");
        Button lessonsButton = new Button("Lessons");
        Button gradesButton = new Button("Grades");
        Button absencesButton = new Button("Absences");
        Button lessonTopicButton = new Button("Topics");
        addRowButton = new Button("Add");
        deleteRowButton = new Button("Remove");


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

        addRowButton.setMinSize(120,30);
        deleteRowButton.setMinSize(120,30);


        addRowButton.setOnAction(e -> {
            if(grades.isVisible() && currentStudent.getPerson() != null) {
                Pair<String, String> p = AddGradeDialog.showAndWait();
                if (p == null)
                    return;
                try {
                    int weight = Integer.parseInt(p.getValue());
                    Database.getInstance().addGrade(p.getKey(), weight, currentStudent.getPerson().getId(), currentGroup.getGroup().getSubjectId(), teacherId);
                    fillGrades();
                } catch (SQLException | NumberFormatException x) {
                    ExceptionHandler.onFailUpdate(x);
                }
            }
            if(lessons.isVisible() && currentGroup.getGroup() != null && currentLesson.getLesson() != null ) {
                AbsencesDialog.showAndWait(currentGroup.getGroup().getId());
                for(StudentsAndAbsences s : AbsencesDialog.res)
                    System.out.print(s.getAbsence().isSelected()+" ");
                System.out.println();
            }
        });

        deleteRowButton.setOnAction(e -> {
            try {
                if(grades.isVisible()) {
                    Grade selectedItem = grades.getSelectionModel().getSelectedItem();
                    if (grades.getSelectionModel() == null || grades.getSelectionModel().getSelectedItem() == null)
                        return;
                    Database.getInstance().removeGrade(grades.getSelectionModel().getSelectedItem().getId());
                    grades.getItems().remove(selectedItem);
                }
                else if (absences.isVisible()) {
                    Absence selectedItem = absences.getSelectionModel().getSelectedItem();
                    if (absences.getSelectionModel() == null || absences.getSelectionModel().getSelectedItem() == null || currentStudent.getPerson() == null)
                        return;
                    Database.getInstance().removeAbsence(currentStudent.getPerson().getId(), absences.getSelectionModel().getSelectedItem().getLessonId());
                    absences.getItems().remove(selectedItem);
                }
            }
            catch(SQLException x) {
                ExceptionHandler.onFailUpdate(x);
            }
        });


        grid.add(backButton, 0, 4);
        grid.add(lessonsButton, 1, 4);
        grid.add(lessonTopicButton, 1, 5);
        grid.add(gradesButton, 2, 4);
        grid.add(absencesButton, 2, 5);
        grid.add(addRowButton,3,4);
        grid.add(deleteRowButton,4,4);


        grid.add(groupsLabel, 0, 0);
        grid.add(groups, 0, 1);
        groups.setMinSize(300,400);

        grid.add(studentsLabel, 1, 0);
        grid.add(students, 1, 1,2,1);
        students.setMinSize(300,400);

        grid.add(gradesLabel, 3, 0);
        grid.add(grades, 3, 1,2,1);
        grades.setMinSize(400,400);

        grid.add(absencesLabel, 3, 0);
        grid.add(absences, 3, 1,2,1);
        absences.setMinSize(400,400);

        grid.add(lessonTopicLabel, 1, 0);
        grid.add(lessons, 1, 1,4,1);
        lessons.setMinSize(900,400);



        groups.getSelectionModel().selectedItemProperty().addListener((observableValue, group, t1) -> {
            currentGroup.setGroup(t1);
            if(t1 != null) {
                if(currentType.getValue() != 2)
                    fillStudents();
                else
                    fillLessons();
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
                        fillGrades();
                        break;
                    case 1:
                        fillAbsences();
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

        lessons.getSelectionModel().selectedItemProperty().addListener((observableValue, lesson, t1) -> {
            currentLesson.setLesson(t1);
        });

        setAbsencesVisible(absencesLabel, false);

        setLessonsVisible(lessonTopicLabel, false);

        lessonsButton.setOnAction(actionEvent -> {
            lessonsTableScene = SceneFactory.getInstance().createLessonTableSceneForTeacher(teacherId);
            window.setScene(lessonsTableScene);
        });

        backButton.setOnAction(actionEvent -> window.setScene(mainScene));
        gradesButton.setOnAction(actionEvent -> {
            if(currentType.getValue() == 0)
                return;

            setAbsencesVisible(absencesLabel, false);

            setLessonsVisible(lessonTopicLabel, false);

            if(currentType.getValue() == 2 && currentGroup.getGroup() != null){
                fillStudents();
            }

            if(currentStudent.getPerson() != null && currentGroup.getGroup() != null){
                fillGrades();
            }

            setStudentsVisible(studentsLabel, true);

            setGradesVisible(gradesLabel, true);

            currentType.setValue(0);
            currentLesson.setLesson(null);
        });

        absencesButton.setOnAction(actionEvent -> {
            if(currentType.getValue() == 1)
                return;

            setGradesVisible(gradesLabel, false);

            setLessonsVisible(lessonTopicLabel, false);

            if(currentType.getValue() == 2 && currentGroup.getGroup() != null){
                fillStudents();
            }

            if(currentStudent.getPerson() != null && currentGroup.getGroup() != null){
                fillAbsences();
            }

            setStudentsVisible(studentsLabel, true);

            setAbsencesVisible(absencesLabel, true);

            currentType.setValue(1);
            currentLesson.setLesson(null);
        });

        lessonTopicButton.setOnAction(actionEvent -> {
            if(currentType.getValue() == 2)
                return;
            currentStudent.setPerson(null);

            setGradesVisible(gradesLabel, false);

            setAbsencesVisible(absencesLabel, false);

            setStudentsVisible(studentsLabel, false);

            if(currentGroup.getGroup() != null){
                fillLessons();
            }

            setLessonsVisible(lessonTopicLabel, true);

            currentType.setValue(2);
        });
        return new Scene(grid, 1280, 720);
    }

    public static TableView<Lesson> fillLessons(){
        lessons.setItems(FXCollections.observableArrayList(Database.getInstance().getLessons(currentGroup.getGroup().getId(), currentGroup.getGroup().getSubjectId())));
        return lessons;
    }

    public static TableView<Person> fillStudents(){
        students.setItems(FXCollections.observableArrayList(Database.getInstance().getStudentsFor(currentGroup.getGroup().getId())));
        return students;
    }

    public static TableView<Absence> fillAbsences(){
        absences.setItems(FXCollections.observableArrayList(Database.getInstance().getAbsences(currentStudent.getPerson().getId(), currentGroup.getGroup().getId(), currentGroup.getGroup().getSubjectId())));
        return absences;
    }

    public static TableView<Grade> fillGrades(){
        grades.setItems(FXCollections.observableArrayList(Database.getInstance().getGrades(currentStudent.getPerson().getId(), currentGroup.getGroup().getSubjectId())));
        return grades;
    }

    public static void setLessonsVisible(Label label, boolean isVisible){
        label.setVisible(isVisible);
        lessons.setVisible(isVisible);
    }

    public static void setStudentsVisible(Label label, boolean isVisible){
        label.setVisible(isVisible);
        students.setVisible(isVisible);
    }

    public static void setAbsencesVisible(Label label, boolean isVisible){
        label.setVisible(isVisible);
        absences.setVisible(isVisible);
        addRowButton.setVisible(!isVisible);
    }

    public static void setGradesVisible(Label label, boolean isVisible){
        label.setVisible(isVisible);
        grades.setVisible(isVisible);
    }

}
