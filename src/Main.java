import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static String whereIAm; //Текущее положение в диалоге
    private static int balanceIncrSpeed; //скорость увеличения баланса, сек./+1
    private static int startBalance; //стартовый баланс для нового игрока
    private static int eatDecrSpeed; //скорость уменьшения "сытости", сек./-1
    private static int drinkDecrSpeed; //скорость уменьшения "воды", сек./-1
    private static int washDecrSpeed; //скорость уменьшения "чистоты", сек./-1
    private static int playDecrSpeed; //скорость уменьшения "настроения", сек./-1
    private static int washingIncrCount; //количество "чистоты", приобретаемое мытьем
    private static int playingIncrCount; //количество "настроения", приобретаемое игрой
    private static int maximumBalance; //максимальный баланс для игрока
    private static HashMap<Integer, User> users = new HashMap<>(); //Список пользователей
    private static HashMap<Integer, Good> eats = new HashMap<>(); //Список товаров в магазине
    private static HashMap<Integer, Good> drinks = new HashMap<>(); //Список товаров в магазине
    private static HashMap<Integer, Good> washes = new HashMap<>(); //Список товаров в магазине
    private static HashMap<Integer, Good> plays = new HashMap<>(); //Список товаров в магазине
    private static HashMap<String, String> newPetParams = new HashMap<>();
    //Параметры нового питомца
    private static User currentUser;
    public static void main(String[] args) {
        System.out.println("Инициализация игры... Пожалуйста, подождите");
        HashMap<String, String> configMap = null;
        System.out.print("Подключение к БД... ");
        DBHandler.init();
        System.out.println("успешно");
        System.out.print("Загрузка конфигурации из БД... ");
        configMap = DBHandler.getDBConfig();
        System.out.println("успешно");
        System.out.print("Инициализация настроек... ");
        balanceIncrSpeed = Integer.parseInt(configMap.get("balanceIncrSpeed"));
        startBalance = Integer.parseInt(configMap.get("startBalance"));
        eatDecrSpeed = Integer.parseInt(configMap.get("eatDecrSpeed"));
        drinkDecrSpeed = Integer.parseInt(configMap.get("drinkDecrSpeed"));
        washDecrSpeed = Integer.parseInt(configMap.get("washDecrSpeed"));
        playDecrSpeed = Integer.parseInt(configMap.get("playDecrSpeed"));
        washingIncrCount = Integer.parseInt(configMap.get("washingIncrCount"));
        playingIncrCount = Integer.parseInt(configMap.get("playingIncrCount"));
        maximumBalance = Integer.parseInt(configMap.get("maximumBalance"));
        System.out.println("успешно");
        System.out.print("Загрузка из БД и инициализация пользователей... ");
        users = DBHandler.getUserList();
        System.out.println("успешно");
        System.out.print("Загрузка из БД и инициализация питомцев... ");
        for (Integer key:users.keySet()) {
            currentUser = users.get(key);
            currentUser.setPets(DBHandler.getPetList(currentUser.getName()));
        }
        System.out.println("успешно");
        System.out.print("Загрузка из БД и инициализация инвентаря... ");
        for (Integer key:users.keySet()) {
            currentUser = users.get(key);
            currentUser.setInventoryGoods(DBHandler.getInventoryGoodsList(currentUser.getName()));
        }
        System.out.println("успешно");
        System.out.print("Загрузка из БД и инициализация товаров... ");
        eats = DBHandler.getGoodList("еда");
        drinks = DBHandler.getGoodList("напиток");
        washes = DBHandler.getGoodList("мытье");
        plays = DBHandler.getGoodList("игра");
        System.out.println("успешно");
        Scanner scanner = new Scanner(System.in);
        login();
        while (true) {
            String answer = scanner.nextLine();
            dialog(answer);
        }
    }
    //Метод первичного запуска авторизации
    public static void login() {
        whereIAm = "login";
        System.out.println("Авторизация пользователя:");
        System.out.println("1) Продолжить игру");
        System.out.println("2) Создать нового пользователя");
        System.out.println("3) Выйти из игры");
    }
    //Метод обрабатывает очередной выбор в диалоге
    public static void dialog(String choice) {
        switch (whereIAm) {
            case "login" -> {
                switch (choice) {
                    case "1" -> choosePlayer();
                    case "2" -> createPlayer1();
                    case "3" -> System.exit(0);
                }
            }
            case "choosePlayer" -> {
                currentUser = users.get(Integer.parseInt(choice));
                if (choice.equals("0")) login();
                else userMenu();
            }
            case "createPlayer1" -> {
                if (choice.equals("0")) login();
                else createPlayer2(choice);
            }
            case "userMenu" -> {
                switch (choice) {
                    case "0" -> choosePlayer();
                    case "1" -> choosePet();
                    case "2" -> createPet1();
                    case "3" -> checkBalance();
                    case "4" -> showInventory();
                    case "5" -> shopMenu(0);
                }
            }
            case "choosePet" -> {
                currentUser.setCurrentPet(currentUser.getPets().get(Integer.parseInt(choice)));
                if (choice.equals("0")) userMenu();
                else petMenu();
            }
            case "createPet1" -> {
                newPetParams.put("name", choice);
                if (choice.equals("0")) userMenu();
                else createPet2();
            }
            case "createPet2" -> {
                newPetParams.put("eat", eats.get(Integer.parseInt(choice)).getObject());
                if (choice.equals("0")) createPet1();
                else  createPet3();
            }
            case "createPet3" -> {
                newPetParams.put("drink", drinks.get(Integer.parseInt(choice)).getObject());
                if (choice.equals("0")) createPet2();
                else createPet4();
            }
            case "shopMenu" -> {
                if (choice.equals("0")) userMenu();
                else shopMenu(Integer.parseInt(choice));
            }
            case "showInventory" -> {
                if (choice.equals("0")) userMenu();
            }
            case "petMenu" -> {
                switch (choice) {
                    case "0" -> choosePet();
                    //case "1" -> eatPet();
                    //case "2" -> drinkPet();
                    //case "3" -> washPet();
                    //case "4" -> playPet();
                    //case "5" -> useInventory(0);
                }
            }
        }
    }
    //Метод выбора игрока при авторизации
    private static void choosePlayer() {
        whereIAm = "choosePlayer";
        System.out.println("Выберите Вашего пользователя:");
        if (users != null && !users.isEmpty()) {
            for (Integer key:users.keySet()) {
                System.out.println(key + ") " + users.get(key).getName());
            }
            System.out.println("0) Назад");
        } else {
            System.out.println("Отсутствуют записи о пользователях");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("ОШИБКА Main.choosePlayer");
            }
            login();
        }
    }
    //Метод вывода меню пользователя
    public static void userMenu() {
        whereIAm = "userMenu";
        System.out.println("Меню пользователя: " + currentUser.getName());
        System.out.println("1) Выбрать питомца");
        System.out.println("2) Создать питомца");
        System.out.println("3) Проверить баланс счета");
        System.out.println("4) Проверить инвентарь");
        System.out.println("5) Сходить в магазин");
        System.out.println("0) Назад");
    }
    //Метод для проверки баланса и его обновления
    public static void checkBalance() {
        whereIAm = "checkBalance";
        long newBalance = DBHandler.checkBalance(currentUser.getName());
        if (newBalance != -1) System.out.println("Баланс счета пользователя " + currentUser.getName() + " - " + newBalance + " руб.");
        else System.out.println("Ошибка проверки баланса счета");
        userMenu();
    }
    //Метод для вывода всех принадлежащих пользователю
    //питомцев для выбора питомца
    public static void choosePet () {
        whereIAm = "choosePet";
        if (currentUser.getPets() != null && !currentUser.getPets().isEmpty()) {
            System.out.println("Выберите Вашего питомца, " + currentUser.getName() + ":");
            for (Integer key:currentUser.getPets().keySet()) {
                System.out.println(key + ") " + currentUser.getPets().get(key).getName());
            }
            System.out.println("0) Назад");
        } else {
            System.out.println("Отсутствуют записи о питомцах");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("ОШИБКА Main.choosePet");
            }
            userMenu();
        }
    }
    //Метод для запроса имени нового пользователя
    private static void createPlayer1() {
        whereIAm = "createPlayer1";
        System.out.println("Введите имя нового пользователя или \"0\" для возврата:");
    }
    //Метод для собственно создания нового пользователя
    private static void createPlayer2(String newUserName) {
        whereIAm = "createPlayer2";
        currentUser = DBHandler.createPlayer(newUserName);
        if (currentUser != null) {
            users.put(currentUser.getId(), currentUser);
            System.out.println("Пользователь " + currentUser.getName() + " создан.");
        } else System.out.println("ОШИБКА Main.createPlayer2. Ошибка создания пользователя");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("ОШИБКА Main.createPlayer2");
        }
        userMenu();
    }
    //Метод для запроса имени нового питомца
    private static void createPet1() {
        whereIAm = "createPet1";
        System.out.println("Введите имя нового питомца или \"0\" для возврата:");
    }
    //Метод для выбора любимой еды нового питомца
    private static void createPet2() {
        whereIAm = "createPet2";
        System.out.println("Выберите любимую еду нового питомца или \"0\" для возврата:");
        if (eats != null && !eats.isEmpty()) {
            for (Integer key:eats.keySet()) {
                System.out.println(key + ") " + eats.get(key).getObject());
            }
            System.out.println("0) Назад");
        } else {
            System.out.println("Отсутствуют записи о еде");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("ОШИБКА Main.createPet2");
            }
            createPet1();
        }
    }
    //Метод для выбора любимого напитка нового питомца
    private static void createPet3() {
        whereIAm = "createPet3";
        System.out.println("Выберите любимый напиток нового питомца или \"0\" для возврата:");
        if (drinks != null && !drinks.isEmpty()) {
            for (Integer key:drinks.keySet()) {
                System.out.println(key + ") " + drinks.get(key).getObject());
            }
            System.out.println("0) Назад");
        } else {
            System.out.println("Отсутствуют записи о напитках");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("ОШИБКА Main.createPet3");
            }
            createPet1();
        }
    }
    //Метод для собственно создания нового питомца
    private static void createPet4() {
        whereIAm = "createPet4";
        Pet newPet;
        newPet = DBHandler.createPet(newPetParams.get("name"), newPetParams.get("eat"), newPetParams.get("drink"), currentUser.getName());
        if (newPet != null) {
            currentUser.addPet(newPet.getId(),newPet);
            System.out.println("Питомец " + newPetParams.get("name") + " создан для пользователя " + currentUser.getName() + ".");
            newPetParams.clear();
        } else System.out.println("ОШИБКА Main.createPet4. Ошибка создания питомца");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
                System.out.println("ОШИБКА Main.createPet4");
        }
        currentUser.setCurrentPet(newPet);
        petMenu();
    }
    //Метод для вывода баланса и всех товаров в магазине
    private static void shopMenu (int buyNum) {
        whereIAm = "shopMenu";
        if (buyNum > 0) {
            Good newGood = DBHandler.buyGood(buyNum, currentUser.getName());
            int newCount = DBHandler.getNewGoodCount(currentUser.getName(), newGood.getObject());
            currentUser.addGood(newGood, newCount);
            System.out.println(newGood.getObject() + " успешно куплен!");
        }
        System.out.println("Баланс счета " + currentUser.getName() + ": " + DBHandler.checkBalance(currentUser.getName()) + " руб.");
        System.out.println("Список товаров в магазине:");
        System.out.println("Еда:");
        if (eats != null && !eats.isEmpty()) {
            for (Integer key:eats.keySet()) {
                System.out.printf("%-3s %-15s %-10s %-10s", key + ") ", eats.get(key).getObject(), "+" + eats.get(key).getIncrCount(), eats.get(key).getPrice() + "руб.");
                System.out.println();
            }
        }
        System.out.println("Напитки:");
        if (drinks != null && !drinks.isEmpty()) {
            for (Integer key:drinks.keySet()) {
                System.out.printf("%-3s %-15s %-10s %-10s", key + ") ", drinks.get(key).getObject(), "+" + drinks.get(key).getIncrCount(), drinks.get(key).getPrice() + "руб.");
                System.out.println();
            }
        }
        System.out.println("Для мытья:");
        if (washes != null && !washes.isEmpty()) {
            for (Integer key:washes.keySet()) {
                System.out.printf("%-3s %-15s %-10s %-10s", key + ") ", washes.get(key).getObject(), "+" + washes.get(key).getIncrCount(), washes.get(key).getPrice() + "руб.");
                System.out.println();
            }
        }
        System.out.println("Игры:");
        if (plays != null && !plays.isEmpty()) {
            for (Integer key:plays.keySet()) {
                System.out.printf("%-3s %-15s %-10s %-10s", key + ") ", plays.get(key).getObject(), "+" + plays.get(key).getIncrCount(), plays.get(key).getPrice() + "руб.");
                System.out.println();
            }
        }
        System.out.println("0) Назад");
    }
    //Метод для вывода баланса и всех товаров в магазине
    private static void showInventory () {
        whereIAm = "showInventory";
        if (currentUser.getGoods() != null && !currentUser.getGoods().isEmpty()) {
            System.out.println("Список товаров в инвентаре пользователя " + currentUser.getName() + ":");
            for (Good good:currentUser.getGoods().keySet()) {
                System.out.printf("%-15s %-10s %-10s %-5s %-5s", good.getObject(), good.getType(), "+" + good.getIncrCount(), good.getPrice() + " руб.", currentUser.getGoods().get(good) + " шт.");
                System.out.println();
            }
            System.out.println("0) Назад");
        } else {
            System.out.println("Отсутствуют записи об инвентаре пользователя");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("ОШИБКА Main.createPet3");
            }
            userMenu();
        }
    }
    //Метод вывода меню питомца
    public static void petMenu() {
        whereIAm = "petMenu";
        System.out.println("Меню питомца: " + currentUser.getCurrentPet().getName() + " пользователя " + currentUser.getName());
        System.out.println("Уровень сытости: " + DBHandler.getLevel(currentUser.getName(), currentUser.getCurrentPet().getName(), "Eat") + "%");
        System.out.println("Уровень воды: " + DBHandler.getLevel(currentUser.getName(), currentUser.getCurrentPet().getName(), "Drink") + "%");
        System.out.println("Уровень чистоты: " + DBHandler.getLevel(currentUser.getName(), currentUser.getCurrentPet().getName(), "Wash") + "%");
        System.out.println("Уровень настроения: " + DBHandler.getLevel(currentUser.getName(), currentUser.getCurrentPet().getName(), "Play") + "%");
        System.out.println("1) Покормить питомца");
        System.out.println("2) Напоить питомца");
        System.out.println("3) Помыть питомца");
        System.out.println("4) Поиграть с питомцем");
        System.out.println("5) Применить предмет из инвентаря");
        System.out.println("0) Назад");
    }
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