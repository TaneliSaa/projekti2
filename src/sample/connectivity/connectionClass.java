package sample.connectivity;

import java.sql.Connection;
import java.sql.DriverManager;


public class connectionClass {

    public Connection connection;

    public Connection getConnection() {

        String dbName = "vn";
        String userName = "root";
        String password = "";
        String url = "jdbc:mysql://localhost/" + dbName;

        try {
            Class.forName("com.mysql.jdbc.Driver");

            connection = DriverManager.getConnection(url, userName, password);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }
}
