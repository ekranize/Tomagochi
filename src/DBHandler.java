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
    private static Statement statement2;
    private static ResultSet resultSet;
    private static ResultSet resultSet2;
    public static void init() {
        try {
            DriverManager.registerDriver(new JDBC());  // Регистрируем драйвер, с которым будем работать (Sqlite)
            // Объект, в котором будет храниться соединение с БД
            Connection connection = DriverManager.getConnection(CON_STR); // Выполняем подключение к базе данных
            statement = connection.createStatement();
            statement2 = connection.createStatement();
        } catch (SQLException e) {
            System.out.println("ОШИБКА DBHandler.init");
        }
    }
    //Метод возвращает все настройки конфигурации из БД
    public static HashMap<String,String> getDBConfig () {
        HashMap<String, String> configMap = new HashMap<>();
        try {
            resultSet = statement.executeQuery("SELECT *, COUNT(*) AS recordCount FROM config");
            ResultSetMetaData meta = resultSet.getMetaData();
            if (resultSet.getInt("recordCount") > 0) {
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    String key = meta.getColumnName(i);
                    String value = resultSet.getString(key);
                    if (!key.equals("recordCount")) configMap.put(key, value);
                }
                return configMap;
            }
            else {
                System.out.println("ОШИБКА DBHandler.getDBConfig");
                System.out.println("Запрос к БД вернул 0 строк");
            }
        } catch (SQLException e) {
            System.out.println("ОШИБКА DBHandler.getDBConfig");
        }
        return configMap;
    }
    //Метод возвращает список всех пользователей из таблицы users
    public static HashMap<Integer, User> getUserList() {
        HashMap<Integer, User> userList = new HashMap<>();
        try {
            resultSet = statement.executeQuery("SELECT * FROM users");
            while (resultSet.next()) {
                userList.put(resultSet.getInt("id"), new User(resultSet.getInt("id"), resultSet.getString("name")));
            }
        } catch (SQLException e) {
            System.out.println("ОШИБКА DBHandler.getUserList");
        }
        return userList;
    }
    //Метод возвращает список всех питомцев из таблицы pets, принадлежащих выбранному пользователю
    public static HashMap<Integer, Pet> getPetList(String userName) {
        HashMap<Integer, Pet> petList = new HashMap<>();
        try {
            resultSet = statement.executeQuery("SELECT * FROM pets WHERE userName=\"" + userName + "\"");
            while (resultSet.next()) {
                petList.put(resultSet.getInt("id"),
                        new Pet(resultSet.getInt("id"),
                                resultSet.getString("name"),
                                resultSet.getString("favEat"),
                                resultSet.getString("favDrink")));
            }
        } catch (SQLException e) {
            System.out.println("ОШИБКА DBHandler.getUserList");
        }
        return petList;
    }
    //Метод возвращает список всех товаров пользователя из таблицы inventory
    public static HashMap<Good, Integer> getInventoryGoodsList(String userName) {
        HashMap<Good, Integer> goodsList = new HashMap<>();
        try {
            resultSet = statement.executeQuery("SELECT * FROM inventory WHERE userName=\"" + userName + "\"");
            while (resultSet.next()) {
                resultSet2 = statement2.executeQuery("SELECT * FROM shop WHERE object=\"" + resultSet.getString("object") + "\"");
                goodsList.put(new Good(resultSet.getInt("id"),
                                resultSet.getString("object"),
                                resultSet2.getInt("price"),
                                resultSet2.getInt("incrCount"),
                                resultSet2.getString("type")),
                        resultSet.getInt("count"));
            }
        } catch (SQLException e) {
            System.out.println("ОШИБКА DBHandler.getInventoryGoodsList");
        }
        return goodsList;
    }
    //Метод возвращает список всех товаров из таблицы shop
    public static HashMap<Integer, Good> getGoodList(String type) {
        HashMap<Integer, Good> goodList = new HashMap<>();
        try {
            resultSet = statement.executeQuery("SELECT * FROM shop WHERE type=\"" + type + "\"");
            while (resultSet.next()) {
                goodList.put(resultSet.getInt("id"),
                        new Good(resultSet.getInt("id"),
                                resultSet.getString("object"),
                                resultSet.getInt("price"),
                                resultSet.getInt("incrCount"),
                                resultSet.getString("type")));
            }
        } catch (SQLException e) {
            System.out.println("ОШИБКА DBHandler.getGoodList");
            e.printStackTrace();
        }
        return goodList;
    }
    //Метод для запроса баланса и его изменения
    public static long checkBalance(String userName) {
        long balance, newBalance = 0L;
        Date dateNow = new Date();
        Date lastBalanceIncrTime;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            resultSet = statement.executeQuery("SELECT balance,lastBalanceIncrTime FROM users WHERE name=\"" + userName + "\"");
            resultSet.next();
            balance = resultSet.getInt("balance");
            lastBalanceIncrTime = sdf.parse(resultSet.getString("lastBalanceIncrTime"));
            long dateDiff = Math.abs(dateNow.getTime() - lastBalanceIncrTime.getTime()) / 1000;
            newBalance = balance + dateDiff / Main.getBalanceIncrSpeed();
            if (newBalance > Main.getMaximumBalance()) newBalance = Main.getMaximumBalance();
            if (statement.executeUpdate("UPDATE users SET balance=" + newBalance + ", lastBalanceIncrTime=datetime('now','localtime') WHERE name=\"" + userName + "\"") != 1)
                return -1;
        } catch (SQLException | ParseException e) {
            System.out.println("ОШИБКА DBHandler.checkBalance");
        }
        return newBalance;
    }
    //Метод создает нового пользователя
    public static User createPlayer (String newUserName) {
        int id=0;
        try {
            if (statement.executeUpdate("INSERT INTO users (name, balance) VALUES (\"" + newUserName + "\", " + Main.getStartBalance() + ")") == 1) {
                resultSet = statement2.executeQuery("SELECT id FROM users WHERE name=\"" + newUserName + "\"");
                resultSet.next();
                id = resultSet.getInt("id");
                return new User(id, newUserName);
            }
        } catch (SQLException e) {
            System.out.println("ОШИБКА DBHandler.createPlayer");
        }
        return null;
    }
    //Метод создает нового питомца
    public static Pet createPet (String newPetName, String newPetEat, String newPetDrink, String userName) {
        int id=0;
        try {
            if (statement.executeUpdate("INSERT INTO pets (name, favEat, favDrink, userName) VALUES (\"" + newPetName + "\", \"" + newPetEat + "\", \"" + newPetDrink + "\", \"" + userName + "\")") == 1) {
                resultSet = statement2.executeQuery("SELECT id FROM pets WHERE name=\"" + newPetName + "\"");
                resultSet.next();
                id = resultSet.getInt("id");
                return new Pet(id, newPetName, newPetEat, newPetDrink);
            }
        } catch (SQLException e) {
            System.out.println("ОШИБКА DBHandler.createPet");
        }
        return null;
    }
    //Метод обрабатывает покупку товара
    public static Good buyGood (int goodId, String userName) {
        String object = null;
        int price = 0;
        String type = null;
        int incrCount = 0;
        try {
            resultSet2 = statement2.executeQuery("SELECT * FROM shop WHERE id=\"" + goodId + "\"");
            resultSet2.next();
            object = resultSet2.getString("object");
            price = resultSet2.getInt("price");
            type = resultSet2.getString("type");
            incrCount = resultSet2.getInt("incrCount");
            resultSet = statement.executeQuery("SELECT count FROM inventory WHERE object=\"" + object + "\" AND userName=\"" + userName + "\"");
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                count++;
                statement.executeUpdate("UPDATE inventory SET count=\"" + count + "\" WHERE object=\"" + object + "\" AND userName=\"" + userName + "\"");
                statement.executeUpdate("UPDATE users SET balance=balance-" + price + " WHERE name=\"" + userName + "\"");
            } else {
                statement.executeUpdate("INSERT INTO inventory (userName, object) VALUES (\"" + userName + "\", \"" + object + "\")");
                statement.executeUpdate("UPDATE users SET balance=balance-" + price + " WHERE name=\"" + userName + "\"");
            }
        } catch (SQLException e) {
            System.out.println("ОШИБКА DBHandler.buyGood");
        }
        return new Good(goodId, object, price, incrCount, type);
    }
    //Метод возвращает список всех товаров из таблицы shop
    public static int getNewGoodCount(String userName, String object) {
        int newCount = 0;
        try {
            resultSet = statement.executeQuery("SELECT count FROM inventory WHERE userName=\"" + userName + "\" AND object=\"" + object + "\"");
            newCount = resultSet.getInt("count");
        } catch (SQLException e) {
            System.out.println("ОШИБКА DBHandler.getNewGoodCount");
            e.printStackTrace();
        }
        return newCount;
    }
}