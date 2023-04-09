import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Game {
    private static String whereIAm; //Текущее положение в диалоге
    private static HashMap<Integer, String> choiceHashMap = new HashMap<>(); //Map выбора пункта меню - номер и название
    private static ArrayList<String> users = new ArrayList<>(); //список всех пользователей
    private static ArrayList<String> pets = new ArrayList<>(); //список всех питомцев
    //Метод обрабатывает очередной выбор в диалоге
    public static void dialog(int choice) {
        switch (whereIAm) {
            case "login" -> {
                switch (choice) {
                    case 1 -> choosePlayer();
                    //case 2 -> createPlayer();
                    case 3 -> System.exit(0);
                }
            }
            case "choosePlayer" -> {
                switch (choice) {
                    case 0 -> login();
                    default -> choosePet(choiceHashMap.get(choice));
                }
            }
            case "choosePet" -> {
                switch (choice) {
                    case 0 -> choosePlayer();
                    //default -> choosePet(choiceHashMap.get(choice));
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
                choiceHashMap.put(i + 1, users.get(i));
            }
        } else {
            System.out.println("В БД отсутствуют записи о пользователях");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println("Что-то пошло не так, возникла ошибка!");
                e.printStackTrace();
            }
            login();
        }
    }
    private static void choosePet (String userName) {
        whereIAm = "choosePet";
        System.out.println("Выберите Вашего питомца:");
        try {
            pets = DBHandler.getPetList(userName);
        } catch (SQLException e) {
            System.out.println("Что-то пошло не так, возникла ошибка!");
            e.printStackTrace();
        }
        if (!pets.isEmpty()) {
            for (int i = 0; i < pets.size(); i++) {
                System.out.println(i + 1 + ") " + pets.get(i));
                choiceHashMap.put(i + 1, pets.get(i));
            }
        } else {
            System.out.println("В БД отсутствуют записи о питомцах");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println("Что-то пошло не так, возникла ошибка!");
                e.printStackTrace();
            }
            choosePlayer();
        }
    }
}
