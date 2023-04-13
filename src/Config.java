import java.sql.SQLException;
import java.util.HashMap;

//конфигуратор игры
public class Config {
    private static int balanceIncrSpeed; //скорость увеличения баланса, сек./+1
    private static int startBalance; //стартовый баланс для нового игрока
    private static int eatDecrSpeed; //скорость уменьшения "сытости", сек./-1
    private static int drinkDecrSpeed; //скорость уменьшения "воды", сек./-1
    private static int washDecrSpeed; //скорость уменьшения "чистоты", сек./-1
    private static int playDecrSpeed; //скорость уменьшения "настроения", сек./-1
    private static int washingIncrCount; //количество "чистоты", приобретаемое мытьем
    private static int playingIncrCount; //количество "настроения", приобретаемое игрой
    private static int maximumBalance; //максимальный баланс для игрока

    //Инициализация конфигуратора
    public static void init () {
        HashMap<String, String> configMap = null;
        try {
            DBHandler.init();
            System.out.println("Подключение к БД... успешно");
            configMap = DBHandler.getDBConfig();
            System.out.println("Загрузка конфигурации из БД... успешно");
        } catch (SQLException e) {
            System.out.println("Что-то пошло не так, возникла ошибка!");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        balanceIncrSpeed = Integer.parseInt(configMap.get("balanceIncrSpeed"));
        startBalance = Integer.parseInt(configMap.get("startBalance"));
        eatDecrSpeed = Integer.parseInt(configMap.get("eatDecrSpeed"));
        drinkDecrSpeed = Integer.parseInt(configMap.get("drinkDecrSpeed"));
        washDecrSpeed = Integer.parseInt(configMap.get("washDecrSpeed"));
        playDecrSpeed = Integer.parseInt(configMap.get("playDecrSpeed"));
        washingIncrCount = Integer.parseInt(configMap.get("washingIncrCount"));
        playingIncrCount = Integer.parseInt(configMap.get("playingIncrCount"));
        maximumBalance = Integer.parseInt(configMap.get("maximumBalance"));
    }

    //Геттеры
    public static int getBalanceIncrSpeed() {
        return balanceIncrSpeed;
    }
    public static int getStartBalance() {
        return startBalance;
    }
    public static int getEatDecrSpeed() {
        return eatDecrSpeed;
    }
    public static int getDrinkDecrSpeed() {
        return drinkDecrSpeed;
    }
    public static int getWashDecrSpeed() {
        return washDecrSpeed;
    }
    public static int getPlayDecrSpeed() {
        return playDecrSpeed;
    }
    public static int getWashingIncrCount() {
        return washingIncrCount;
    }
    public static int getPlayingIncrCount() {
        return playingIncrCount;
    }
    public static int getMaximumBalance() {
        return maximumBalance;
    }
}
