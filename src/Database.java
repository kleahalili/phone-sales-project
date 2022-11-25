import java.sql.*;

public class Database {
    private Connection con;

    public Database() {
        try {
            String URL = "jdbc:mysql://localhost:3306/test?useSSL=false";
            String username = "root";
            String password = "";
            this.con = DriverManager.getConnection(URL, username, password);
            if (this.con != null) System.out.println("Connected to database");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getCon() {
        return con;
    }

    public ResultSet executeQuery(String query) throws SQLException {
        Statement stmt = this.con.createStatement();
        return stmt.executeQuery(query);
    }

    public int executeUpdate(String query) throws SQLException {
        Statement stmt = this.con.createStatement();
        return stmt.executeUpdate(query);
    }
}