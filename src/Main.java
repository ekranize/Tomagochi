import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Инициализация игры... Пожалуйста, подождите");
        Config.init();
        System.out.println("Инициализация игры... успешно");
        Scanner scanner = new Scanner(System.in);
        Game.login();
        while (true) {
            String answer = scanner.nextLine();
            Game.dialog(answer);
        }
    }
}