import org.sqlite.JDBC;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
public final class DBHandler {
    private static final String CON_STR = "jdbc:sqlite:db.db"; // Константа, в которой хранится адрес подключения
    private static Statement statement;
    private static ResultSet resultSet;
    private static Connection connection; // Объект, в котором будет храниться соединение с БД
    public static void init() throws SQLException {
        DriverManager.registerDriver(new JDBC());  // Регистрируем драйвер, с которым будем работать (Sqlite)
        connection = DriverManager.getConnection(CON_STR); // Выполняем подключение к базе данных
        statement = connection.createStatement();
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
    public static HashMap<String,String> getDBConfig () throws SQLException {
        HashMap<String, String> configMap = new HashMap<>();
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
    //Метод возвращает список всех пользователей из таблицы users
    public static ArrayList<String> getUserList()  throws SQLException {
        ArrayList<String> userList = new ArrayList<>();
        resultSet = statement.executeQuery("SELECT name FROM users");
        while (resultSet.next()) {
            userList.add(resultSet.getString("name"));
        }
        return userList;
    }
    //Метод для запроса баланса и его изменения
    public static long checkBalance(String userName) throws SQLException, ParseException {
        long balance, newBalance;
        Date dateNow = new Date();
        Date lastBalanceIncrTime;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        resultSet = statement.executeQuery("SELECT balance,lastBalanceIncrTime FROM users WHERE name=\"" + userName + "\"");
        resultSet.next();
        balance = resultSet.getInt("balance");
        lastBalanceIncrTime = sdf.parse(resultSet.getString("lastBalanceIncrTime"));
        long dateDiff = Math.abs(dateNow.getTime() - lastBalanceIncrTime.getTime()) / 1000;
        newBalance = balance + dateDiff/Config.getBalanceIncrSpeed();
        if (statement.executeUpdate("UPDATE users SET balance=" + newBalance + ", lastBalanceIncrTime=datetime('now','localtime') WHERE name=\"" + userName + "\"") != 1) return  -1;
        return newBalance;
    }
    //Метод создает нового пользователя
    public static boolean createPlayer (String newUserName) throws SQLException {
        return statement.executeUpdate("INSERT INTO users (name) VALUES (\"" + newUserName + "\")") == 1;
    }
    //Метод возвращает список всех питомцев из таблицы pets, принадлежащих выбранному пользователю
    public static ArrayList<String> getPetList(String userName)  throws SQLException {
        ArrayList<String> petList = new ArrayList<>();
        resultSet = statement.executeQuery("SELECT name FROM pets WHERE userName = '" + userName + "'");
        while (resultSet.next()) {
            petList.add(resultSet.getString("name"));
        }
        return petList;
    }
    //Метод возвращает список всех продуктов питания из таблицы shop
    public static ArrayList<String> showEats()  throws SQLException {
        ArrayList<String> eatList = new ArrayList<>();
        resultSet = statement.executeQuery("SELECT object FROM shop WHERE type=\"eat\"");
        while (resultSet.next()) {
            eatList.add(resultSet.getString("object"));
        }
        return eatList;
    }
    //Метод возвращает список всех напитков из таблицы shop
    public static ArrayList<String> showDrinks()  throws SQLException {
        ArrayList<String> drinkList = new ArrayList<>();
        resultSet = statement.executeQuery("SELECT object FROM shop WHERE type=\"drink\"");
        while (resultSet.next()) {
            drinkList.add(resultSet.getString("object"));
        }
        return drinkList;
    }
    //Метод создает нового питомца
    public static boolean createPet (String newPetName, String newPetEat, String newPetDrink, String userName) throws SQLException {
        return statement.executeUpdate("INSERT INTO pets (name, favEat, favDrink, userName) VALUES (\"" + newPetName + "\", \"" + newPetEat + "\", \"" + newPetDrink + "\", \"" + userName + "\")") == 1;
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