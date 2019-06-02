package schoolregister;

import java.io.IOException;
import java.sql.*;


public class Database {
    private Connection connection;
    private ConnectionConfig config;
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


    public int getStudentsCount() {
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT count(*) FROM students");
            int count = 0;
            while (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            statement.close();
            return count;
        }
        catch (SQLException e) {
            crash(e);
            return 0;
        }
    }

    private void connect() {
        connection = null;
        try {
            config = new ConnectionConfig();
            Class.forName("org.postgresql.Driver");
            connection = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/"+config.getDBname(),
                            config.getUser(), config.getPassword());
        } catch (IOException e) {
            System.out.println("Connection with database failed, check if dbconnection config is correct");
            crash(e);
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