import org.sqlite.JDBC;
import java.sql.*;
import java.util.HashMap;

public final class DBHandler {
    private static final String CON_STR = "jdbc:sqlite:db.db"; // Константа, в которой хранится адрес подключения
    private Statement statement = null;
    private ResultSet resultSet = null;
    private final Connection connection; // Объект, в котором будет храниться соединение с БД
    public DBHandler() throws SQLException {
        DriverManager.registerDriver(new JDBC());  // Регистрируем драйвер, с которым будем работать (Sqlite)
        this.connection = DriverManager.getConnection(CON_STR); // Выполняем подключение к базе данных
    }
    /*public void connectionClose () throws SQLException {
        if (connection != null && !connection.isClosed()) {
            Statement statement = null;
            try {
                statement = connection.createStatement();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            connection.close();
        }
    }*/
    //Метод возвращает все настройки конфигурации из БД
    public HashMap<String,String> getDBConfig () throws SQLException {
        HashMap<String, String> configMap = new HashMap<String, String>();
        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT *, COUNT(*) AS recordCount FROM config");
        ResultSetMetaData meta = resultSet.getMetaData();
        if (resultSet.getInt("recordCount") > 0) {
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                String key = meta.getColumnName(i);
                String value = resultSet.getString(key);
                if (!key.equals("recordCount")) configMap.put(key,value);
            }
            return configMap;
        } else throw new SQLException("Запрос к БД вернул 0 строк");
    }
    /*public boolean checkAuth (String userName, String passwordHash) throws SQLException {
        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT COUNT(*) AS recordCount FROM users WHERE userName = '" + userName + "' AND password = '" + passwordHash + "'");
        return resultSet.getInt("recordCount") == 1;
    }
    public boolean passwordChange (String userName, String passwordHash) throws SQLException {
        statement = connection.createStatement();
        return statement.executeUpdate("UPDATE users SET password = '" + passwordHash + "' WHERE userName = '" + userName + "'") == 1;
    }
    public boolean moneyTransfer (String userName, String toUserName, int count) throws SQLException {
        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT balance FROM users WHERE userName = '" + userName + "'");
        if (resultSet.getInt("balance") >= count) {
            int result1, result2;
            result1 = statement.executeUpdate("UPDATE users SET balance = balance - " + count + " WHERE userName = '" + userName + "'");
            result2 = statement.executeUpdate("UPDATE users SET balance = balance + " + count + " WHERE userName = '" + toUserName + "'");
            return (result1 + result2) == 2;
        } return false;
    }
    public int balanceCheck (String userName) throws SQLException {
        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT balance FROM users WHERE userName = '" + userName + "'");
        return resultSet.getInt("balance");
    }*/
}