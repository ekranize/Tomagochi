import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Game {
    private static String whereIAm; //Текущее положение в диалоге
    private static HashMap<String, String> choiceHashMap = new HashMap<>(); //Map выбора пункта меню - номер и название
    private static ArrayList<String> users = new ArrayList<>(); //список всех пользователей
    private static ArrayList<String> pets = new ArrayList<>(); //список всех питомцев текущего пользователя
    private static String currentUser; //Текущий пользователь
    private static String currentPet; //Текущий питомец
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
                switch (choice) {
                    case "0" -> login();
                    default -> userMenu();
                }
            }
            case "createPlayer1" -> {
                switch (choice) {
                    case "0" -> login();
                    default -> createPlayer2(choice);
                }
            }
            case "userMenu" -> {
                switch (choice) {
                    case "0" -> choosePlayer();
                    case "1" -> choosePet();
                    //case "2" -> createPet();
                    case "3" -> checkBalance();
                    //case "4" -> showInventory();
                    //case "5" -> shopMenu();
                }
            }
            case "choosePet" -> {
                currentPet = choiceHashMap.get(choice);
                switch (choice) {
                    case "0" -> userMenu();
                    //default -> petMenu();
                }
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
}
