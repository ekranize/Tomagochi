import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Инициализация игры... Пожалуйста, подождите");
        Config config = new Config();
        System.out.println("Инициализация игры... успешно");
        Scanner scanner = new Scanner(System.in);
        Game.login();
        while (true) {
            int answer = scanner.nextInt();
            Game.dialog(answer);
        }
    }
}