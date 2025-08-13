package core;

public class Config {
    // Could self define teh ratio of Floor type of room
    public static final double ROOM_FLOOR_POSS = 0.5;
    public static final int BLOCK_WIDTH1 = 1;
    public static final int BLOCK_WIDTH2 = 2;
    // Could self define the ratio of thickness of wall
    public static final double WALL_THICKNESS_1_PROBABILITY = 0.9;

    public static final int BUFFER = 2;

    public static final int WINDOW_HEIGHT = 50;
    public static final int WINDOW_WIDTH = 90;
    public static final int WORLD_HEIGHT = WINDOW_HEIGHT - UI.BOTTOM_UI - UI.TOP_UI;

    public static final int MIN_MAIN_ROOM_WIDTH = 17;
    public static final int MAX_MAIN_ROOM_WIDTH = 25;
    public static final int MIN_MAIN_ROOM_HEIGHT = 14;
    public static final int MAX_MAIN_ROOM_HEIGHT = 20;
    public static final int MIN_MAIN_ROOM_NUM = 3;
    public static final int MAX_MAIN_ROOM_NUM = 5;

    public static final int MIN_SUB_ROOM_WIDTH = 8;
    public static final int MAX_SUB_ROOM_WIDTH = 10;
    public static final int MIN_SUB_ROOM_HEIGHT = 6;
    public static final int MAX_SUB_ROOM_HEIGHT = 10;
    public static final int MIN_SUB_ROOM_NUM = 2;
    public static final int MAX_SUB_ROOM_NUM = 7;

}
