package schoolregister;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;


public class Database {
    private Connection connection;
    private static Database instance;

    public static Database getInstance() {
        if(instance == null) {
            instance = new Database();
            instance.connect();
        }
        return instance;
    }
    public static void close() {
        if(instance != null) {
            try {
                instance.connection.close();
                instance.connection = null;
                instance = null;
            }
            catch (SQLException e) {
                crash(e);
            }
        }
    }

    public Lesson[][] getLessonsAssignedTo(int studentId) {
       Lesson[][] lessons = new Lesson[5][10];
        try {
            Statement statement = connection.createStatement();
            //TODO SQL injection
            ResultSet rs = statement.executeQuery("SELECT * FROM students JOIN groups_students using(student_id) join groups_subjects_plan using(group_id) where student_id = "+studentId+";");
            while (rs.next()) {
                Lesson lesson = new Lesson();
                lesson.dayId = rs.getInt("day_id");
                lesson.slot = rs.getInt("slot");
                lesson.groupId = rs.getInt("group_id");
                lesson.groupName = rs.getString("group_name");
                lesson.subjectId = rs.getInt("subject_id");
                lesson.subjectName = rs.getString("subject_name");
                lessons[lesson.dayId-1][lesson.slot-1] = lesson;
            }
            rs.close();
            statement.close();
        }
        catch (SQLException e) {
            crash(e);
        }
        return lessons;
    }

    private void connect() {
        connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager
                    .getConnection("jdbc:postgresql://localhost:"+ConnectionConfig.port+"/" + ConnectionConfig.database,
                            ConnectionConfig.username, ConnectionConfig.password);
        }
        catch (Exception e){
            crash(e);
        }
    }

    private static void crash(Exception e) {
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        System.exit(0);
    }

}