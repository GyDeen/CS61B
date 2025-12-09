package core;

public class Config {
    // Game time
    public static final int GAME_TIME_IN_SEC = 300;
    // Could self define teh ratio of Floor type of room
    public static final double ROOM_FLOOR_POSS = 0.5;
    public static final int BLOCK_WIDTH1 = 1;
    public static final int BLOCK_WIDTH2 = 2;
    // Could self define the ratio of thickness of wall
    public static final double WALL_THICKNESS_1_PROBABILITY = 1;

    // Window setting
    public static final int WINDOW_HEIGHT = 50;
    public static final int WINDOW_WIDTH = 90;
    public static final int WORLD_HEIGHT = WINDOW_HEIGHT - UI.BOTTOM_UI - UI.TOP_UI;

    // Main room setting
    public static final int MIN_MAIN_ROOM_WIDTH = 14;
    public static final int MAX_MAIN_ROOM_WIDTH = 20;
    public static final int MIN_MAIN_ROOM_HEIGHT = 12;
    public static final int MAX_MAIN_ROOM_HEIGHT = 24;
    public static final int MIN_MAIN_ROOM_NUM = 6;
    public static final int MAX_MAIN_ROOM_NUM = 10;
    public static final int MIN_GAP_BETWEEN_MAIN_ROOM = 2;

    // Subroom setting
    public static final int MIN_SUB_ROOM_FOR_MAIN_WIDTH = 5;
    public static final int MAX_SUB_ROOM_FOR_MAIN_WIDTH = 12;
    public static final int MIN_SUB_ROOM_FOR_MAIN_HEIGHT = 5;
    public static final int MAX_SUB_ROOM_FOR_MAIN_HEIGHT = 12;
    public static final int MIN_SUB_ROOM_NUM = 1;
    public static final int MAX_SUB_ROOM_NUM = 5;

    // Full fill room setting
    public static final int MIN_VOID_AREA = 35;
    public static final int MIN_FILL_ROOM_WIDTH  = 6;
    public static final int MAX_FILL_ROOM_WIDTH  = 20;
    public static final int MIN_FILL_ROOM_HEIGHT = 6;
    public static final int MAX_FILL_ROOM_HEIGHT = 20;

    public static final int MIN_SUB_ROOM_WIDTH_FOR_FILL = 3;
    public static final int MIN_SUB_ROOM_HEIGHT_FOR_FILL = 3;

    // Render world setting
    public static final int DOOR_BUFF = 1;
    public static final int MAX_OVERSHOOT = 5;
    public static final int FUTURE_BUFFER = 1;
    public static final int SMALL_ADVANCE = 1;
    public static final int MAX_WALL_IN_A_ROW = 2;
    public static final int MAX_ATTEMPT_PIVOT = 50;
    public static final int ALLOCATE_FAIL_CAP = 200;
    public static final int PAUSE = 0;
    public static final int WIN = 1;
    public static final int LOSE = -1;

    // Init page setting
    public static final int BUFFER = 4;
    public static final int TITLE_SIZE = 150;
    public static final int PROMPT_SIZE = 70;
    public static final int TILE_SIZE = 16;


    // PacMan config
    public static final int IMAGE_SWITCHING_PERIOD = 500;


    // Loot Box type
    public static final long FADE_INTERVAL_MS = 300;
    public static final int NOT_FADING = 0;
    public static final int FADING = 1;
    public static final int FINISHED = 2;
}