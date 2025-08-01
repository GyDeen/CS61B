package core;

import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;

import java.util.ArrayList;
import java.util.Random;



public class World {
    public static final int WINDOW_HEIGHT = 60;
    public static final int WINDOW_WIDTH = 40;
    public static final int WORLD_HEIGHT = WINDOW_HEIGHT - UI.BOTTOM_UI - UI.TOP_UI;

    private static final int BLOCK_WIDTH1 = 1;
    private static final int BLOCK_WIDTH2 = 2;
    // Could self define the ratio of thickness of wall
    private static final double WALL_THICKNESS_1_PROBABILITY = 0.75;

    private static final int MIN_ROOM_WIDTH = 4;

    // Could self define teh ratio of Floor type of room
    private static final double ROOM_FLOOR_POSS = 0.5;


    private static final int MIN_ROOM_NUM = 6;

    private static long seed = 726;
    private static Random random = new Random(seed);
    public final TETile[][] world = new TETile[WINDOW_WIDTH][WORLD_HEIGHT];

    private int roomNum;
    private ArrayList<Room> rooms = new ArrayList<>();


    /** Using the default seed to generate the world */
    public World() {
        roomNum = RandomUtils.uniform(random, MIN_ROOM_NUM + 2, 20);
    }


    /** Generate a world that based on input seed
     * @param seed seed for random generator
     */
    public World(long seed) {
        World.seed = seed;
        random = new Random(seed);
        roomNum = RandomUtils.uniform(random, MIN_ROOM_NUM + 2, 17);
    }


    /** Generate a random room for the world. Randomly generate information such as size, location,
     * the thickness of the "wall" of the room, whether it has corner. It will stop generate when it has more than
     * minimum number of room and fails to many times to generate new rooms
     */
    public void generateRooms() {
        int failAttempt = 0, failLimit = 400;
        while (rooms.size() < roomNum && (failAttempt < failLimit && rooms.size() < MIN_ROOM_NUM)) {
            int width = RandomUtils.uniform(random, MIN_ROOM_WIDTH, 10);
            int height = RandomUtils.uniform(random, MIN_ROOM_WIDTH, 8);
            int x = RandomUtils.uniform(random, 1, WINDOW_WIDTH - width - 1);
            int y = RandomUtils.uniform(random, 1, WORLD_HEIGHT - height - 1);
            int wallThickness = (random.nextDouble() < WALL_THICKNESS_1_PROBABILITY) ? BLOCK_WIDTH1 : BLOCK_WIDTH2;

            // Check validation of the room
            if (!Room.validRoom(x, y, width, height, wallThickness, rooms)) {
                failAttempt++;
                continue;
            }

            // room without corner is rare
            boolean isCornered = true;
            int cornerRoll = random.nextInt(100);
            if (cornerRoll % 7 == 0 || cornerRoll % 13 == 0 || cornerRoll % 4 == 0) {
                isCornered = false;
            }

            // Generate the floor type and wall type
            TileType floorType = getRandomPassable(), wallType = getRandomImpassable(floorType);

        }
    }

    /* Helper function that return random picked tile type for room floor. FLOOR type is more likely to present */
    private static TileType getRandomPassable() {
        if (random.nextDouble() < ROOM_FLOOR_POSS) {
            return TileType.FLOOR;
        }

        // Else choose from other passable types (excluding FLOOR)
        TileType[] values = TileType.values();
        ArrayList<TileType> others = new ArrayList<>();
        for (TileType t : values) {
            if (t.passable && t != TileType.FLOOR) {
                others.add(t);
            }
        }

        return others.get(random.nextInt(others.size()));
    }

    /* Helper function that return random picked tile type for room wall. WALL type has higher probability. If the
    * Floor type is natural, mountain has higher chance */
    private static TileType getRandomImpassable(TileType floorType) {
        // Natural floors boost MOUNTAIN chance
        boolean isNatural = floorType == TileType.FLOWER
                || floorType == TileType.TREE
                || floorType == TileType.GRASS;

        double mountainChance = isNatural ? 0.6 : 0.3;
        double roll = random.nextDouble();

        if (roll < mountainChance) {
            return TileType.MOUNTAIN;
        } else {
            return TileType.WALL;
        }
    }


    /** Draw the world and generate the whole picture of the world */
    public void renderWorld() {
        TERenderer ter = new TERenderer();
        ter.initialize(WINDOW_WIDTH, WINDOW_HEIGHT);

        for (int x = 0; x < WINDOW_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }



    }
}



