import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

public class Game {
    private static String whereIAm; //Текущее положение в диалоге
    private static HashMap<String, String> choiceHashMap = new HashMap<>(); //Map выбора пункта меню - номер и название
    private static ArrayList<String> users = new ArrayList<>(); //список всех пользователей
    private static ArrayList<String> pets = new ArrayList<>(); //список всех питомцев текущего пользователя
    private static ArrayList<String> eats = new ArrayList<>(); //список всех продуктов питания
    private static ArrayList<String> drinks = new ArrayList<>(); //список всех напитков
    private static ArrayList<String> shopGoods = new ArrayList<>(); //список товаров в магазине
    private static ArrayList<String> shopGood = new ArrayList<>(); //параметры текущего товара в магазине
    private static String currentUser; //Текущий пользователь
    private static String currentPet; //Текущий питомец
    private static String newPetName; //Имя нового питомца
    private static String newPetEat; //Любимая еда нового питомца
    private static String newPetDrink; //Любимый напиток нового питомца
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
                currentUser = choiceHashMap.get(choice);
                if ("0".equals(choice)) login();
                else userMenu();
            }
            case "createPlayer1" -> {
                if ("0".equals(choice)) login();
                else createPlayer2(choice);
            }
            case "userMenu" -> {
                switch (choice) {
                    case "0" -> choosePlayer();
                    case "1" -> choosePet();
                    case "2" -> createPet1();
                    case "3" -> checkBalance();
                    //case "4" -> showInventory();
                    case "5" -> shopMenu();
                }
            }
            case "choosePet" -> {
                currentPet = choiceHashMap.get(choice);
                if ("0".equals(choice)) userMenu();
                //else petMenu();
            }
            case "createPet1" -> {
                newPetName = choice;
                if ("0".equals(choice)) userMenu();
                else createPet2();
            }
            case "createPet2" -> {
                newPetEat = choiceHashMap.get(choice);
                if ("0".equals(choice)) createPet1();
                else createPet3();
            }
            case "createPet3" -> {
                newPetDrink = choiceHashMap.get(choice);
                if ("0".equals(choice)) createPet2();
                else createPet4();
            }
            case "shopMenu" -> {
                if ("0".equals(choice)) userMenu();
                else shopMenu(choiceHashMap.get(choice));
            }
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
    //Метод выбора игрока при авторизации
    private static void choosePlayer() {
        whereIAm = "choosePlayer";
        System.out.println("Выберите Вашего пользователя:");
        try {
            users = DBHandler.getUserList();
        } catch (SQLException e) {
            System.out.println("Что-то пошло не так, возникла ошибка!");
            e.printStackTrace();
        }
        if (!users.isEmpty()) {
            for (int i = 0; i < users.size(); i++) {
                System.out.println(i + 1 + ") " + users.get(i));
                choiceHashMap.put(Integer.toString(i + 1), users.get(i));
            }
            System.out.println("0) Назад");
        } else {
            System.out.println("ОШИБКА! В БД отсутствуют записи о пользователях");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Что-то пошло не так, возникла ошибка!");
                e.printStackTrace();
            }
            login();
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
        try {
            if (DBHandler.createPlayer(newUserName))
                System.out.println("Пользователь " + newUserName + " создан.");
            Thread.sleep(2000);
        } catch (SQLException | InterruptedException e) {
            System.out.println("Что-то пошло не так, возникла ошибка!");
            e.printStackTrace();
        }
        login();
    }
    //Метод вывода меню пользователя
    public static void userMenu() {
        whereIAm = "userMenu";
        System.out.println("Меню пользователя: " + currentUser);
        System.out.println("1) Выбрать питомца");
        System.out.println("2) Создать питомца");
        System.out.println("3) Проверить баланс счета");
        System.out.println("4) Проверить инвентарь");
        System.out.println("5) Сходить в магазин");
        System.out.println("0) Назад");
    }
    //Метод для вывода всех принадлежащих пользователю
    //питомцев для выбора питомца
    private static void choosePet () {
        whereIAm = "choosePet";
        System.out.println("Выберите Вашего питомца, " + currentUser + ":");
        try {
            pets = DBHandler.getPetList(currentUser);
        } catch (SQLException e) {
            System.out.println("Что-то пошло не так, возникла ошибка!");
            e.printStackTrace();
        }
        if (!pets.isEmpty()) {
            for (int i = 0; i < pets.size(); i++) {
                System.out.println(i + 1 + ") " + pets.get(i));
                choiceHashMap.put(Integer.toString(i + 1), pets.get(i));
            }
            System.out.println("0) Назад");
        } else {
            System.out.println("ОШИБКА! В БД отсутствуют записи о питомцах");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Что-то пошло не так, возникла ошибка!");
                e.printStackTrace();
            }
            userMenu();
        }
    }
    //Метод для проверки баланса и его обновления
    private static void checkBalance() {
        whereIAm = "checkBalance";
        try {
            long newBalance = DBHandler.checkBalance(currentUser);
            if (newBalance != -1) System.out.println("Баланс счета пользователя " + currentUser + " - " + newBalance);
            else System.out.println("Ошибка проверки баланса счета");
        } catch (SQLException | ParseException e) {
            System.out.println("Что-то пошло не так, возникла ошибка!");
            e.printStackTrace();
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
        try {
            eats = DBHandler.showEats();
        } catch (SQLException e) {
            System.out.println("Что-то пошло не так, возникла ошибка!");
            e.printStackTrace();
        }
        if (!eats.isEmpty()) {
            for (int i = 0; i < eats.size(); i++) {
                System.out.println(i + 1 + ") " + eats.get(i));
                choiceHashMap.put(Integer.toString(i + 1), eats.get(i));
            }
            System.out.println("0) Назад");
        } else {
            System.out.println("ОШИБКА! В БД отсутствуют записи о еде");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Что-то пошло не так, возникла ошибка!");
                e.printStackTrace();
            }
            createPet1();
        }
    }
    //Метод для выбора любимого напитка нового питомца
    private static void createPet3() {
        whereIAm = "createPet3";
        System.out.println("Выберите любимый напиток нового питомца или \"0\" для возврата:");
        try {
            drinks = DBHandler.showDrinks();
        } catch (SQLException e) {
            System.out.println("Что-то пошло не так, возникла ошибка!");
            e.printStackTrace();
        }
        if (!drinks.isEmpty()) {
            for (int i = 0; i < drinks.size(); i++) {
                System.out.println(i + 1 + ") " + drinks.get(i));
                choiceHashMap.put(Integer.toString(i + 1), drinks.get(i));
            }
            System.out.println("0) Назад");
        } else {
            System.out.println("ОШИБКА! В БД отсутствуют записи о напитках");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Что-то пошло не так, возникла ошибка!");
                e.printStackTrace();
            }
            createPet2();
        }
    }
    //Метод для собственно создания нового питомца
    private static void createPet4() {
        whereIAm = "createPet4";
        try {
            if (DBHandler.createPet(newPetName, newPetEat, newPetDrink, currentUser))
                System.out.println("Питомец " + newPetName + " создан для пользователя " + currentUser + ".");
            Thread.sleep(2000);
        } catch (SQLException | InterruptedException e) {
            System.out.println("Что-то пошло не так, возникла ошибка!");
            e.printStackTrace();
        }
        userMenu();
    }
    //Метод для вывода баланса и всех товаров в магазине
    private static void shopMenu () {
        whereIAm = "shopMenu";
        try {
            System.out.println("Баланс счета " + currentUser + ": " + DBHandler.checkBalance(currentUser));
        } catch (SQLException | ParseException e) {
            System.out.println("Что-то пошло не так, возникла ошибка!");
            e.printStackTrace();
        }
        System.out.println("Список товаров в магазине:");
        try {
            shopGoods = DBHandler.getShopGoods();
        } catch (SQLException e) {
            System.out.println("Что-то пошло не так, возникла ошибка!");
            e.printStackTrace();
        }
        if (!shopGoods.isEmpty()) {
            for (int i = 0; i < shopGoods.size(); i++) {
                try {
                    shopGood = DBHandler.getGoodParams(shopGoods.get(i));
                } catch (SQLException e) {
                    System.out.println("Что-то пошло не так, возникла ошибка!");
                    e.printStackTrace();
                }
                System.out.printf("%-3s %-15s %-10s %-10s %-10s", i + 1 + ") ", shopGoods.get(i), shopGood.get(0), shopGood.get(1), shopGood.get(2));
                System.out.println();
                choiceHashMap.put(Integer.toString(i + 1), shopGoods.get(i));
            }
            System.out.println("0) Назад");
        } else {
            System.out.println("ОШИБКА! В БД отсутствуют записи о товарах");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Что-то пошло не так, возникла ошибка!");
                e.printStackTrace();
            }
            userMenu();
        }
    }
    //Метод для вывода баланса и всех товаров в магазине
    private static void shopMenu (String selectedGood) {
        whereIAm = "shopMenu";
        try {
            if (DBHandler.buyGood(selectedGood, currentUser)) System.out.println(selectedGood + " успешно куплен!");
            else System.out.println(selectedGood + " не куплен!");
        } catch (SQLException e) {
            System.out.println("Что-то пошло не так, возникла ошибка!");
            e.printStackTrace();
        }
        try {
            System.out.println("Баланс счета " + currentUser + ": " + DBHandler.checkBalance(currentUser));
        } catch (SQLException | ParseException e) {
            System.out.println("Что-то пошло не так, возникла ошибка!");
            e.printStackTrace();
        }
        System.out.println("Список товаров в магазине:");
        try {
            shopGoods = DBHandler.getShopGoods();
        } catch (SQLException e) {
            System.out.println("Что-то пошло не так, возникла ошибка!");
            e.printStackTrace();
        }
        if (!shopGoods.isEmpty()) {
            for (int i = 0; i < shopGoods.size(); i++) {
                try {
                    shopGood = DBHandler.getGoodParams(shopGoods.get(i));
                } catch (SQLException e) {
                    System.out.println("Что-то пошло не так, возникла ошибка!");
                    e.printStackTrace();
                }
                System.out.printf("%-3s %-15s %-10s %-10s %-10s", i + 1 + ") ", shopGoods.get(i), shopGood.get(0), shopGood.get(1), shopGood.get(2));
                System.out.println();
                choiceHashMap.put(Integer.toString(i + 1), shopGoods.get(i));
            }
            System.out.println("0) Назад");
        } else {
            System.out.println("ОШИБКА! В БД отсутствуют записи о товарах");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Что-то пошло не так, возникла ошибка!");
                e.printStackTrace();
            }
            userMenu();
        }
    }
}
