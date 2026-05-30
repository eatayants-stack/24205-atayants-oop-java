package src;

public class GameBuilder {

    public static GameEngine createDefaultGame() {
        SecretNumber secret = SecretNumber.GenerateSecret();
        return new GameEngine(secret);
    }

    public static GameEngine createGameWithSecret(String secretValue) {
        SecretNumber secret = SecretNumber.of(secretValue);
        return new GameEngine(secret);
    }
}