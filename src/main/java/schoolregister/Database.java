package schoolregister;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


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

    private Person createPerson(ResultSet rs, Person.Type type){
        Person p = new Person(type);
        try{
            p.setId(rs.getInt(type + "_id"));
            p.setPesel(rs.getString("pesel"));
            p.setName(rs.getString("name"));
            p.setSurname(rs.getString("surname"));
            p.setCity(rs.getString("city"));
            p.setStreet(rs.getString("street"));
            p.setPostalCode(rs.getString("postalcode"));
            p.setEmail(rs.getString("email"));
            p.setPhone(rs.getString("phone"));
        }
        catch (Exception e){
            crash(e);
        }
        return p;
    }

    public List<Person> getPeople(Person.Type type){
        List<Person> list = new ArrayList<>();
        try{
            String source = (type == Person.Type.guardian) ? "legal_guardians" : (type + "s");
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM " + source);
            while(rs.next()){
                Person p = createPerson(rs, type);
                list.add(p);
            }
            rs.close();
            statement.close();
        }
        catch (Exception e){
            crash(e);
        }
        return list;
    }

    public short logUser(String email, String password){
        short mask = 0;
        if(logUser(email, password, Person.Type.student))
            mask |= Main.studentMash;
        if(logUser(email, password, Person.Type.teacher))
            mask |= Main.teacherMask;
        if(logUser(email, password, Person.Type.guardian))
            mask |= Main.guardianMask;
        return mask;
    }

    private boolean logUser(String email, String password, Person.Type type){
        boolean result = false;
        try{
            String tableName = (type == Person.Type.guardian) ? "legal_guardians" : type + "s";
            String columnName = type + "_id, password";
            String query = "SELECT "+ columnName + " FROM " + tableName + " WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                if(BCrypt.checkpw(password, rs.getString(2))) {
                    result = true;
                    Main.userType = type;
                    Main.userID = rs.getInt(1);
                }
            }
            statement.close();
            rs.close();
        }
        catch (Exception e){
            crash(e);
        }
        return result;
    }

    private static void crash(Exception e) {
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        System.exit(0);
    }
}