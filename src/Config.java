import java.sql.SQLException;
import java.util.HashMap;

//конфигуратор игры
public final class Config {
    private final int balanceIncrSpeed; //скорость увеличения баланса, сек./+1
    private final int startBalance; //стартовый баланс для нового игрока
    private final int eatDecrSpeed; //скорость уменьшения "сытости", сек./-1
    private final int drinkDecrSpeed; //скорость уменьшения "воды", сек./-1
    private final int washDecrSpeed; //скорость уменьшения "чистоты", сек./-1
    private final int playDecrSpeed; //скорость уменьшения "настроения", сек./-1
    private final int washingIncrCount; //количество "чистоты", приобретаемое мытьем
    private final int playingIncrCount; //количество "настроения", приобретаемое игрой
    private final int maximumBalance; //максимальный баланс для игрока

    //Конструктор конфигуратора
    public Config () {
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
        this.balanceIncrSpeed = Integer.parseInt(configMap.get("balanceIncrSpeed"));
        this.startBalance = Integer.parseInt(configMap.get("startBalance"));
        this.eatDecrSpeed = Integer.parseInt(configMap.get("eatDecrSpeed"));
        this.drinkDecrSpeed = Integer.parseInt(configMap.get("drinkDecrSpeed"));
        this.washDecrSpeed = Integer.parseInt(configMap.get("washDecrSpeed"));
        this.playDecrSpeed = Integer.parseInt(configMap.get("playDecrSpeed"));
        this.washingIncrCount = Integer.parseInt(configMap.get("washingIncrCount"));
        this.playingIncrCount = Integer.parseInt(configMap.get("playingIncrCount"));
        this.maximumBalance = Integer.parseInt(configMap.get("maximumBalance"));
    }

    //Геттеры
    public int getBalanceIncrSpeed() {
        return balanceIncrSpeed;
    }
    public int getStartBalance() {
        return startBalance;
    }
    public int getEatDecrSpeed() {
        return eatDecrSpeed;
    }
    public int getDrinkDecrSpeed() {
        return drinkDecrSpeed;
    }
    public int getWashDecrSpeed() {
        return washDecrSpeed;
    }
    public int getPlayDecrSpeed() {
        return playDecrSpeed;
    }
    public int getWashingIncrCount() {
        return washingIncrCount;
    }
    public int getPlayingIncrCount() {
        return playingIncrCount;
    }
    public int getMaximumBalance() {
        return maximumBalance;
    }
}
