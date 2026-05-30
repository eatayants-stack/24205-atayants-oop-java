package src;

public class Main {
    public static void main(String[] args) {
        GameEngine engine = GameBuilder.createDefaultGame();
        ConsoleUI ui = new ConsoleUI(engine);
        ui.start();
    }
}