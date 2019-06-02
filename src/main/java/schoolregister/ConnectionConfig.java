package schoolregister;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class ConnectionConfig {
    private static final String dbConnectionFile = "dbconnection.json";

    private JSONObject object;
    public ConnectionConfig() throws IOException {
        try {
            Object o = new JSONParser().parse(new FileReader(dbConnectionFile));
            object = (JSONObject) o;
        }
        catch (ParseException e){
            throw new IOException("JSON parse exception: "+e.getMessage());
        }
    }
    public String getDBname() {
        return get("database");
    }
    public String getUser() {
        return get("user");
    }
    public String getPassword() {
        return get("password");
    }

    private String get(String key) {
        return (String) object.get(key);
    }



}
