package schoolregister;

import org.mindrot.jbcrypt.BCrypt;
import schoolregister.DataType.*;
import schoolregister.utils.ExceptionHandler;

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

    static void close() {
        if(instance != null) {
            try {
                instance.connection.close();
                instance.connection = null;
                instance = null;
            }
            catch (SQLException e) {
                ExceptionHandler.crash(e);
            }
        }
    }

    private void connect() {
        connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager
                    .getConnection("jdbc:postgresql://" + ConnectionConfig.host + ":" + ConnectionConfig.port+"/" + ConnectionConfig.database,
                            ConnectionConfig.username, ConnectionConfig.password);
        }
        catch (Exception e){
            ExceptionHandler.crash(e);
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
            ExceptionHandler.crash(e);
        }
        return p;
    }

    private Group createGroup(ResultSet rs){
        Group g = new Group();
        try{
            g.setId(rs.getInt("group_id"));
            g.setSubjectId(rs.getInt("subject_id"));
            g.setName(rs.getString("g_name"));
            g.setSubject(rs.getString("s_name"));
        }
        catch (Exception e){
            ExceptionHandler.crash(e);
        }
        return g;
    }

    private Lesson createLesson(ResultSet rs){
        Lesson l = new Lesson();
        try{
            l.setId(rs.getInt("lesson_id"));
            l.setDate(rs.getDate("date"));
            l.setTopic(rs.getString("topic"));
            l.setSlot(rs.getInt("slot"));
        }
        catch (Exception e){
            ExceptionHandler.crash(e);
        }
        return l;
    }

    public List<Person> getStudentsFor(int groupId){
        List<Person> list = new ArrayList<>();

        String query = "select students.* from students join students_in_groups using(student_id) where group_id = " + groupId;

        try(Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(query)){
            while(rs.next()){
                list.add(createPerson(rs, Person.Type.student));
            }
        }
        catch (Exception e){
            ExceptionHandler.crash(e);
        }
        return list;
    }

    public Person getPerson(int id, Person.Type type){
        String tableName = (type == Person.Type.guardian) ? "legal_guardians" : type + "s";
        String query = "SELECT * FROM " + tableName + " WHERE " + type + "_id = " + id;

        try(Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query)){
            if(rs.next()){
                return createPerson(rs, type);
            }
        }
        catch(Exception e){
            ExceptionHandler.crash(e);
        }

        return null;
    }

    public List<Person> getPeople(Person.Type type){
        List<Person> list = new ArrayList<>();

        String source = (type == Person.Type.guardian) ? "legal_guardians" : (type + "s");
        String query = "SELECT * FROM " + source;

        try(Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query)){
            while(rs.next()){
                list.add(createPerson(rs, type));
            }
        }
        catch (Exception e){
            ExceptionHandler.crash(e);
        }
        return list;
    }

    public List<Group> getGroupsFor(int teacherId){
        List<Group> list = new ArrayList<>();

        String query = "select teacher_id, group_id, subject_id, subjects.name\"s_name\", groups.name\"g_name\" from teachers_groups_subjects join groups using(group_id) join subjects using(subject_id) where teacher_id = " + teacherId + " group by 1, 2, 3, 4, 5";

        try(Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query)){
            while(rs.next()){
                list.add(createGroup(rs));
            }
        }
        catch (Exception e){
            ExceptionHandler.crash(e);
        }
        return list;
    }

    public List<LessonsOnSlot> getLessonsAssignedTo(int id, boolean isStudent) {
        Lesson[][] lessons = new Lesson[5][10];
        List<LessonsOnSlot> res = new ArrayList<>();

        String query;

        String query2 = "SELECT slot,to_char(start_time,'HH24:MI') AS start_time,to_char(start_time+'45 minutes','HH24:MI') AS end_time FROM slots ORDER BY start_time";

        if(isStudent)
            query = "SELECT * FROM students JOIN groups_students USING(student_id) JOIN groups_subjects_plan USING(group_id) WHERE student_id = "+id;
        else
            query = "SELECT * FROM groups_subjects_plan JOIN teachers_groups_subjects USING(group_id,day_id,slot,subject_id) WHERE teacher_id = "+id;

        int maxiSlotNr = 0;

        try (Statement statement = connection.createStatement();
              Statement statement2 = connection.createStatement();
              ResultSet rs = statement.executeQuery(query);
              ResultSet rs2 = statement2.executeQuery(query2)){
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

            while (rs2.next()) {
                res.add(new LessonsOnSlot(rs2.getInt("slot"),rs2.getString("start_time"),rs2.getString("end_time")));
            }

        }
        catch (SQLException e) {
            ExceptionHandler.crash(e);
        }

        for(int i=0;i<lessons.length;i++){
            for(int j=0;j<maxiSlotNr;j++) {
                res.get(j).set(i,lessons[i][j]);
            }
        }
        return res;
    }

    public List<Lesson> getLessons(int groupId, int subject_id){
        List<Lesson> list = new ArrayList<>();

        String query = "select * from lessons where group_id = " + groupId + " and subject_id = " + subject_id;

        try(Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query)){
            while(rs.next()){
                list.add(createLesson(rs));
            }
        }
        catch (Exception e){
            ExceptionHandler.crash(e);
        }
        return list;
    }



    public List<Grade> getGrades(int studentID){
        return getGrades(studentID,-1);
    }

    public List<Grade> getGrades(int studentId, int subject_id){
        List<Grade> list = new ArrayList<>();

        String query = "SELECT grade_id,subject_id,name, value,value::NUMERIC(3,2) as floatValue, weight FROM grades g JOIN subjects USING(subject_id) WHERE student_id =" + studentId;

        if(subject_id != -1)
            query +=  " AND subject_id = " + subject_id;
        query += " ORDER BY subject_id, weight DESC";

        try(Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query)){
            while(rs.next()){
                list.add(new Grade(rs.getInt("grade_id"), rs.getInt("subject_id"),rs.getString("name"),rs.getString("value"),rs.getFloat("floatValue"), rs.getInt("weight")));
            }
        }
        catch (Exception e){
            ExceptionHandler.crash(e);
        }
        return list;
    }

    public List<Absence> getAbsences(int studentId) {
        List<Absence> list = new ArrayList<>();

        String query = "SELECT lesson_id,slot,date,s.name FROM absences JOIN lessons USING(lesson_id) JOIN subjects s USING(subject_id) WHERE student_id = "+studentId;

        try(Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query)){
            while(rs.next()){
                list.add(new Absence(rs.getInt("lesson_id"),rs.getInt("slot"),rs.getDate("date"),rs.getString("name")));
            }
        }
        catch (Exception e){
            ExceptionHandler.crash(e);
        }
        return list;
    }

    public List<Absence> getAbsences(int studentId, int groupId, int subjectId){
        List<Absence> list = new ArrayList<>();

        String query = "SELECT lesson_id,slot,date,s.name FROM absences JOIN lessons USING(lesson_id) JOIN subjects s USING(subject_id) WHERE student_id = "+studentId + " and subject_id = " + subjectId + " and group_id = " + groupId;

        try(Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query)){
            while(rs.next()){
                list.add(new Absence(rs.getInt("lesson_id"),rs.getInt("slot"),rs.getDate("date"),rs.getString("name")));
            }
        }
        catch (Exception e){
            ExceptionHandler.crash(e);
        }
        return list;
    }

    public List<Subject> getAllSubjects(int studentId) {
        List<Subject> list = new ArrayList<>();

        String query = "SELECT subject_id,name FROM students_subjects JOIN subjects USING(subject_id) WHERE student_id = "+studentId;

        try(Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query)){
            while(rs.next()){
                list.add(new Subject(rs.getInt("subject_id"),rs.getString("name")));
            }
        }
        catch (Exception e){
            ExceptionHandler.crash(e);
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
        String tableName = (type == Person.Type.guardian) ? "legal_guardians" : type + "s";
        String columnName = type + "_id, password";
        String query = "SELECT "+ columnName + " FROM " + tableName + " WHERE email = ?";

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, email);
            try(ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    if (BCrypt.checkpw(password, rs.getString(2))) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        catch (Exception e){
            ExceptionHandler.crash(e);
        }
        return 0;
    }

    public List<Integer> getGuardianKids(int guardianID){
        List<Integer> list = new ArrayList<>();

        String query = "SELECT student_id FROM guardians_students WHERE guardian_id = " + guardianID;

        try(Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query)){
            while(rs.next()){
                list.add(rs.getInt(1));
            }
        }
        catch (Exception e){
            ExceptionHandler.crash(e);
        }
        return list;
    }

    public void updateGrade(Grade grade, String newValue, int weight) throws SQLException{
        String query = "UPDATE grades SET weight = ? , value = ?::GRADE WHERE grade_id = ?";

        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, weight);
            statement.setString(2, newValue);
            statement.setLong(3, grade.getId());
            statement.execute();
        }
    }

    public void removeGrade(long id) throws SQLException {
        String query = "DELETE FROM grades WHERE grade_id = ?";

        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            statement.execute();
        }
    }

    public void addGrade(String value, int weight, int student_id, int subject_id, int teacher_id) throws SQLException {
        String query = "INSERT INTO grades (value, weight, student_id, subject_id, teacher_id) VALUES (?::GRADE,?,?,?,?)";
        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, value);
            statement.setInt(2, weight);
            statement.setInt(3, student_id);
            statement.setInt(4, subject_id);
            statement.setInt(5, teacher_id);
            statement.execute();
        }
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