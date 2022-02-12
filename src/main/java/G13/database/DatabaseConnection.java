package G13.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public Connection databaseLink;

    /**
     * Get the databaseUser, databasePassword and the url and try to connect to the database successfully
     * @return databaseLink
     * @throws Exception
     * @author Ercan
     */
    public Connection getConnection() throws Exception {
        String databaseUser = "timeemef";
        String databasePassword = "12345678";
        String url = "jdbc:mysql://db4free.net:3306/timeemef?serverTimezone=UTC";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url,databaseUser, databasePassword);
        }catch (SQLException e) {
            e.printStackTrace();
            e.getCause();
        }
        return databaseLink;
    }
}
