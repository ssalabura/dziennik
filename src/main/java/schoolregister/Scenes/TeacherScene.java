package schoolregister.Scenes;

import javafx.collections.FXCollections;
import javafx.scene.Node;
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
import schoolregister.Dialogs.AddExamDialog;
import schoolregister.Dialogs.AddGradeDialog;
import schoolregister.Dialogs.AddTopicDialog;
import schoolregister.Factory.SceneFactory;
import schoolregister.Factory.ViewFactory;
import schoolregister.Wrapper.*;
import schoolregister.utils.ExceptionHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static schoolregister.Main.*;

/**
 * currentType
 * 0 - grades
 * 1 - absences
 * 2 - topics
 * 3 - exams
 */

public class TeacherScene {
    private static ViewFactory viewFactory = ViewFactory.getInstance();

    public static TableView<Group> groups;
    public static TableView<Grade> grades;
    public static TableView<Person> students;
    public static TableView<Absence> absences;
    public static TableView<Lesson> lessons;
    public static TableView<Exam> exams;

    public static IntegerWrapper currentType;
    public static GroupWrapper currentGroup;
    public static PersonWrapper currentStudent;
    public static LessonWrapper currentLesson;
    public static ExamWrapper currentExam;

    private static Button addRowButton;
    private static Button deleteRowButton;
    private static Button absencesCheckButton;
    private static Button addExamButton;

    public static Scene newTeacherScene(int teacherId) {
        groups = viewFactory.getGroupsFor(teacherId);
        grades = viewFactory.getGrades();
        grades.setEditable(true);
        students = viewFactory.getStudents();
        absences = viewFactory.getAbsences();
        lessons = viewFactory.getLessonsForTeacher();
        exams = viewFactory.getExamsForTeacher();

        currentGroup = new GroupWrapper();
        currentStudent = new PersonWrapper();
        currentExam = new ExamWrapper();
        currentType = new IntegerWrapper(0);
        currentLesson = new LessonWrapper();

        GridPane grid = viewFactory.createGrid();
        Button backButton = new Button("Back");
        Button lessonsButton = new Button("Plan");
        Button gradesButton = new Button("Grades");
        Button absencesButton = new Button("Absences");
        Button lessonTopicButton = new Button("Topics");
        Button examsButton = new Button("Exams");
        addRowButton = new Button("Add");
        deleteRowButton = new Button("Remove");
        absencesCheckButton = new Button("Check absences");
        addExamButton = new Button("Add Exam");


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
        Label examsLabel = new Label("Exams");
        examsLabel.setFont(new Font("Arial", 20));

        backButton.setMinSize(120,30);
        lessonsButton.setMinSize(120,30);
        lessonTopicButton.setMinSize(120,30);
        gradesButton.setMinSize(120,30);
        absencesButton.setMinSize(120,30);
        examsButton.setMinSize(120, 30);

        addRowButton.setMinSize(120,30);
        deleteRowButton.setMinSize(120,30);
        absencesCheckButton.setMinSize(120,30);
        addExamButton.setMinSize(120, 30);

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
                    ExceptionHandler.onFailUpdate(x, currentType.getValue());
                }
            }
            else if(lessons.isVisible() && currentGroup.getGroup() != null) {
                AddTopicDialog.showAndWait(teacherId);
                if(AddTopicDialog.date != null && AddTopicDialog.topic != null) {
                    try {
                        Database.getInstance().addLesson(currentGroup.getGroup().getId(), currentGroup.getGroup().getSubjectId(), AddTopicDialog.date,AddTopicDialog.slot,AddTopicDialog.topic);
                        fillLessons();
                    } catch (SQLException | NumberFormatException x) {
                        ExceptionHandler.onFailUpdate(x, currentType.getValue());
                    }
                }
            }
        });

        addExamButton.setOnAction(e -> {
            if(currentLesson.getLesson() != null){
                String description = AddExamDialog.showAndWait();
                if(description == null)
                    return;
                try{
                    Database.getInstance().addExam(currentLesson.getLesson().getLessonId(), description);
                    fillExams();
                }
                catch (SQLException x){
                    ExceptionHandler.onFailUpdate(x, currentType.getValue());
                }
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
                else if(exams.isVisible()){
                    if(currentExam.getExam() == null)
                        return;
                    Database.getInstance().removeExam(currentExam.getExam().getExamId());
                    exams.getItems().remove(currentExam.getExam());
                }
            }
            catch(SQLException x) {
                ExceptionHandler.onFailUpdate(x, currentType.getValue());
            }
        });

        absencesCheckButton.setOnAction(e -> {
            if(lessons.isVisible() && currentGroup.getGroup() != null && currentLesson.getLesson() != null ) {
                synchronized (AbsencesDialog.class) {
                    AbsencesDialog.showAndWait(currentLesson.getLesson(), currentGroup.getGroup().getId());
                    if (AbsencesDialog.before != null && AbsencesDialog.after != null) {
                        try {
                            List<StudentsAndAbsences> toAdd = new ArrayList<>();
                            List<StudentsAndAbsences> toRemove = new ArrayList<>();
                            for(int i=0;i<AbsencesDialog.after.size();i++) {
                                boolean wasBefore = !AbsencesDialog.before.get(i).getAbsence().isSelected();
                                boolean isNow =     !AbsencesDialog.after.get(i).getAbsence().isSelected();
                                if(wasBefore && !isNow){
                                    toRemove.add(AbsencesDialog.after.get(i));
                                }
                                else if(!wasBefore && isNow) {
                                    toAdd.add(AbsencesDialog.after.get(i));
                                }
                            }
                            Database.getInstance().updateAbsences(currentLesson.getLesson().getLessonId(), toAdd, toRemove);
                        }
                        catch (SQLException x) {
                            ExceptionHandler.onFailUpdate(x, currentType.getValue());
                        }
                    }
                    AbsencesDialog.before = null;
                    AbsencesDialog.after = null;
                }

            }
        });


        grid.add(backButton, 0, 4);
        grid.add(lessonsButton, 1, 4);
        grid.add(lessonTopicButton, 1, 5);
        grid.add(examsButton, 1, 6);
        grid.add(gradesButton, 2, 4);
        grid.add(absencesButton, 2, 5);
        grid.add(addRowButton,3,4);
        grid.add(deleteRowButton,4,4);
        grid.add(absencesCheckButton,5,4);
        grid.add(addExamButton, 4, 4);


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

        grid.add(examsLabel, 1, 0);
        grid.add(exams, 1, 1, 4, 1);
        exams.setMinSize(900, 400);



        groups.getSelectionModel().selectedItemProperty().addListener((observableValue, group, t1) -> {
            currentGroup.setGroup(t1);
            if(t1 != null) {
                if(currentType.getValue() == 0 || currentType.getValue() == 1)
                    fillStudents();
                else if(currentType.getValue() == 2)
                    fillLessons();
                else if(currentType.getValue() == 3)
                    fillExams();
            }
            else{
                if(currentType.getValue() == 0 || currentType.getValue() == 1)
                    students.getItems().clear();
                else if(currentType.getValue() == 2)
                    lessons.getItems().clear();
                else if(currentType.getValue() == 3)
                    exams.getItems().clear();
            }
        });

        exams.getSelectionModel().selectedItemProperty().addListener((observableValue, exam, t1) -> {
            currentExam.setExam(t1);
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
            addExamButton.setVisible(t1 != null);
            currentLesson.setLesson(t1);
        });

        setAbsencesVisible(absencesLabel, false);

        setLessonsVisible(lessonTopicLabel, false);

        setExamsVisible(examsLabel, false);

        addExamButton.setVisible(false);

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

            setExamsVisible(examsLabel, false);

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

            setExamsVisible(examsLabel, false);

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

            setExamsVisible(examsLabel, false);

            if(currentGroup.getGroup() != null){
                fillLessons();
            }

            setLessonsVisible(lessonTopicLabel, true);

            currentType.setValue(2);
        });

        examsButton.setOnAction(actionEvent -> {
            if(currentType.getValue() == 3)
                return;
            currentStudent.setPerson(null);

            setGradesVisible(gradesLabel, false);

            setAbsencesVisible(absencesLabel, false);

            setStudentsVisible(studentsLabel, false);

            setLessonsVisible(lessonTopicLabel, false);

            if(currentGroup.getGroup() != null){
                fillExams();
            }

            setExamsVisible(examsLabel, true);

            currentType.setValue(3);
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

    public static TableView<Exam> fillExams(){
        exams.setItems(FXCollections.observableArrayList(Database.getInstance().getExamsForGroupSubject(currentGroup.getGroup().getId(), currentGroup.getGroup().getSubjectId())));
        return exams;
    }

    public static void setLessonsVisible(Label label, boolean isVisible){
        setVisible(isVisible, label, absencesCheckButton, lessons);
        setVisible(!isVisible, deleteRowButton);
        if(isVisible){
            addRowButton.setText("Add Topic");
        }
        else{
            addRowButton.setText("Add");
        }
    }

    public static void setStudentsVisible(Label label, boolean isVisible){
        setVisible(isVisible, label, students);
    }

    public static void setAbsencesVisible(Label label, boolean isVisible){
        setVisible(isVisible, label, absences);
        setVisible(!isVisible, addRowButton);
    }

    public static void setGradesVisible(Label label, boolean isVisible){
        setVisible(isVisible, label, grades);
        if(isVisible){
            addRowButton.setText("Add Grade");
        }
        else{
            addRowButton.setText("Add");
        }
    }

    public static void setExamsVisible(Label label, boolean isVisible){
        setVisible(isVisible, label, exams);
        setVisible(!isVisible, addRowButton);
        if(isVisible){
            addRowButton.setText("Add Exam");
        }
        else{
            addRowButton.setText("Add");
        }
    }

    private static void setVisible(boolean isVisible, Node... args){
        for(Node node : args){
            node.setVisible(isVisible);
        }
    }
}
