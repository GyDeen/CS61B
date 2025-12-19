package core;

public enum Direction {
    LEFT, RIGHT, UP, DOWN;
    public String toString() {
        switch (this) {
            case LEFT:
                return "Left";
            case RIGHT:
                return "Right";
            case UP:
                return "Up";
            case DOWN:
                return "Down";
        }
        return "";
    }
}
