package schoolregister;

import org.mindrot.jbcrypt.BCrypt;
import schoolregister.DataType.*;

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
            p.setHash(rs.getString("password"));
        }
        catch (Exception e){
            crash(e);
        }
        return p;
    }

    public Person getPerson(int id, Person.Type type){
        try{
            String tableName = (type == Person.Type.guardian) ? "legal_guardians" : type + "s";
            String query = "SELECT * FROM " + tableName + " WHERE " + type + "_id = " + id;
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if(rs.next()){
                return createPerson(rs, type);
            }
            statement.close();
            rs.close();
        }
        catch(Exception e){
            crash(e);
        }

        return null;
    }

    public List<Person> getPeople(Person.Type type){
        List<Person> list = new ArrayList<>();
        try{
            String source = (type == Person.Type.guardian) ? "legal_guardians" : (type + "s");
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM " + source);
            while(rs.next()){
                list.add(createPerson(rs, type));
            }
            rs.close();
            statement.close();
        }
        catch (Exception e){
            crash(e);
        }
        return list;
    }

    public List<LessonsOnSlot> getLessonsAssignedTo(int id, boolean isStudent) {
        Lesson[][] lessons = new Lesson[5][10];
        List<LessonsOnSlot> res = new ArrayList<>();
        int maxiSlotNr = 0;
        try {
            Statement statement = connection.createStatement();
            ResultSet rs;
            if(isStudent)
                rs = statement.executeQuery("SELECT * FROM students JOIN groups_students USING(student_id) JOIN groups_subjects_plan USING(group_id) WHERE student_id = "+id);
            else
                rs = statement.executeQuery("SELECT * FROM groups_subjects_plan JOIN teachers_groups_subjects USING(group_id,day_id,slot,subject_id) WHERE teacher_id = "+id);

            while (rs.next()) {
                Lesson lesson = new Lesson();
                lesson.dayId = rs.getInt("day_id");
                lesson.slot = rs.getInt("slot");
                lesson.groupId = rs.getInt("group_id");
                lesson.groupName = rs.getString("group_name");
                lesson.subjectId = rs.getInt("subject_id");
                lesson.subjectName = rs.getString("subject_name");
                lessons[lesson.dayId-1][lesson.slot-1] = lesson;
                maxiSlotNr = Math.max(maxiSlotNr,lesson.slot);
            }
            rs.close();
            statement.close();

            statement = connection.createStatement();
            rs = statement.executeQuery("SELECT slot,to_char(start_time,'HH24:MI') AS start_time,to_char(start_time+'45 minutes','HH24:MI') AS end_time FROM slots ORDER BY start_time");
            while (rs.next()) {
                res.add(new LessonsOnSlot(rs.getInt("slot"),rs.getString("start_time"),rs.getString("end_time")));
            }

        }
        catch (SQLException e) {
            crash(e);
        }

        for(int i=0;i<lessons.length;i++){
            for(int j=0;j<maxiSlotNr;j++) {
                res.get(j).set(i,lessons[i][j]);
            }
        }
        return res;
    }


    public List<Grade> getGrades(int studentID){
        List<Grade> list = new ArrayList<>();
        try{
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT subject_id,name, value,value::NUMERIC(3,2) as floatValue, weight FROM grades g JOIN subjects USING(subject_id) WHERE student_id = " + studentID + " ORDER BY subject_id, weight DESC");
            while(rs.next()){
                list.add(new Grade(rs.getInt("subject_id"),rs.getString("name"),rs.getString("value"),rs.getFloat("floatValue"), rs.getInt("weight")));
            }
            statement.close();
            rs.close();
        }
        catch (Exception e){
            crash(e);
        }
        return list;
    }

    public List<Absence> getAbsences(int studentId) {
        List<Absence> list = new ArrayList<>();
        try{
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(
                    "SELECT lesson_id,slot,date,s.name FROM absences JOIN lessons USING(lesson_id) JOIN subjects s USING(subject_id) WHERE student_id = "+studentId);
            while(rs.next()){
                list.add(new Absence(rs.getInt("lesson_id"),rs.getInt("slot"),rs.getDate("date"),rs.getString("name")));
            }
            statement.close();
            rs.close();
        }
        catch (Exception e){
            crash(e);
        }
        return list;
    }

    public List<Subject> getAllSubjects(int studentId) {
        List<Subject> list = new ArrayList<>();
        try{
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(
                    "SELECT subject_id,name FROM students_subjects JOIN subjects USING(subject_id) WHERE student_id = "+studentId);
            while(rs.next()){
                list.add(new Subject(rs.getInt("subject_id"),rs.getString("name")));
            }
            statement.close();
            rs.close();
        }
        catch (Exception e){
            crash(e);
        }
        return list;
    }

    public short logUser(String email, String password){
        short mask = 0;
        if((Main.userIDs[Main.studentMask] = logUser(email, password, Person.Type.student)) != 0)
            mask |= Main.studentMask;
        if((Main.userIDs[Main.guardianMask] = logUser(email, password, Person.Type.guardian)) != 0)
            mask |= Main.guardianMask;
        if((Main.userIDs[Main.teacherMask] = logUser(email, password, Person.Type.teacher)) != 0)
            mask |= Main.teacherMask;
        return mask;
    }

    private int logUser(String email, String password, Person.Type type){
        try{
            String tableName = (type == Person.Type.guardian) ? "legal_guardians" : type + "s";
            String columnName = type + "_id, password";
            String query = "SELECT "+ columnName + " FROM " + tableName + " WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                if(BCrypt.checkpw(password, rs.getString(2))) {
                    return rs.getInt(1);
                }
            }
            statement.close();
            rs.close();
        }
        catch (Exception e){
            crash(e);
        }
        return 0;
    }

    public List<Integer> getGuardianKids(int guardianID){
        List<Integer> list = new ArrayList<>();
        try{
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT student_id FROM guardians_students WHERE guardian_id = " + guardianID);
            while(rs.next()){
                list.add(rs.getInt(1));
            }
            statement.close();
            rs.close();
        }
        catch (Exception e){
            crash(e);
        }
        return list;
    }

    private static void crash(Exception e) {
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        System.exit(0);
    }

    public void testLogAll(boolean onlyErrors){
        testLog(getPeople(Person.Type.teacher), onlyErrors, "TEACHERS");
        testLog(getPeople(Person.Type.student), onlyErrors, "STUDENTS");
        testLog(getPeople(Person.Type.guardian), onlyErrors, "GUARDIANS");
    }

    private void testLog(List<Person> users, boolean onlyErrors, String type){
        System.out.println("---------------------------------" + type);
        for(Person p : users){
            StringBuilder password = new StringBuilder(p.getEmail()).reverse();
            boolean goodPassword = BCrypt.checkpw(password.toString(), p.getHash());
            if(onlyErrors){
                if(!goodPassword)
                    System.out.println(p.getId() + " ERROR");
            }
            else{
                System.out.println(p.getId() + (goodPassword ? " OK" : " ERROR"));
            }
        }
        System.out.println("-----------------------------------------------");
    }
}