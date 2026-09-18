import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static final Properties props = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream("db.properties")) {
            props.load(fis);
        } catch (IOException e) {
            System.err.println("Notice: db.properties not found! Utilizing fallback defaults.");
        }
    }

    @FunctionalInterface
    public interface SQLConsumer {
        void accept(Connection con) throws SQLException;
    }

    @FunctionalInterface 
    public interface SQLFunction<T> {
        T apply(Connection con) throws SQLException;
    }

    public static Connection getConnection() throws SQLException {
        String host = props.getProperty("db.host", "loacalhost");
        String port = props.getProperty("db.port", "3306");
        String dbName = props.getProperty("db.name", "pharmacy");
        String user = props.getProperty("db.user", "root");
        String pass = props.getProperty("db.password", "madhvesh12");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?useSSL=false&allowPublicKeyRetrieval=true";
        return DriverManager.getConnection(url, user, pass);
    }

    public static void execute(SQLConsumer action) {
        try (Connection con = getConnection()) {
            action.accept(con);
        } catch (SQLException e) {
            System.err.println("Database Execution Error: " + e.getMessage());
        }
    }

    public static <T> T executeQuery(SQLFunction<T> action, T defaultValue) {
        try (Connection con =getConnection()) {
            return action.apply(con);
        } catch (SQLException e) {
            System.err.println("Database Query Error: " + e.getMessage());
            return defaultValue;
        }
    }
}