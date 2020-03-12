import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DAO {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/project3-nudb";
    static final String USER = "root";
    static final String PASS = "rootroot";
    Connection conn = null;
    Statement stmt = null;
    public DAO() throws SQLException {

        conn = DriverManager.getConnection(DB_URL, USER, PASS);

    }

    public void checkStudentCredentials() throws SQLException {
        stmt = conn.createStatement();


    }
}
