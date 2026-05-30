package model;

public record Position(int x, int y) {
    public boolean isInside() {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    public Position add(int dx, int dy) {
        return new Position(x + dx, y + dy);
    }
}