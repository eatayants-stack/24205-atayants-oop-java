package engine;

import model.Side;

public class Configuration {
    private final Side playerColor;
    private final boolean vsBotMode;
    private final boolean boardFlipped;

    public Configuration(Side playerColor, boolean vsBotMode, boolean boardFlipped) {
        this.playerColor = playerColor;
        this.vsBotMode = vsBotMode;
        this.boardFlipped = boardFlipped;
    }

    public Side getPlayerColor() { return playerColor; }
    public boolean isVsBotMode() { return vsBotMode; }
    public boolean isBoardFlipped() { return boardFlipped; }
}