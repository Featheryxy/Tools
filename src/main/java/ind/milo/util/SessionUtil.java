package ind.milo.util;

import java.sql.*;

public class SessionUtil {
//    private static final String TCP_JDBC_URL = "jdbc:h2:tcp://127.0.0.1/F:\\GitHubNote\\tools\\database\\work";
//    private static final String TCP_JDBC_URL = "jdbc:h2:F:\\GitHubNote\\tools\\database\\work";
    private static final String TCP_JDBC_URL = "jdbc:h2:F:\\JavaFX\\tools\\database\\work";
    private static final String USER = "sa";
    private static final String PASSWORD = "213213";
    private static final String DRIVER_CLASS = "org.h2.Driver";



    public static Connection getConnection() {
        Connection connection = null;

        // todo 后续改成单例模式
        try {
            connection = DriverManager.getConnection(TCP_JDBC_URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Connection conn = DriverManager.getConnection(TCP_JDBC_URL, USER, PASSWORD);
        Statement statement = conn.createStatement();

        ResultSet resultSet = statement.executeQuery("select * from tbwork");
        while (resultSet.next()) {
            System.out.println(resultSet.getString("mdf_no"));
        }
        statement.close();
        conn.close();

    }
}

