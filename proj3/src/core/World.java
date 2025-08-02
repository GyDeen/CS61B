package core;

import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;



public class World {
    public static final int WINDOW_HEIGHT = 50;
    public static final int WINDOW_WIDTH = 70;
    public static final int WORLD_HEIGHT = WINDOW_HEIGHT - UI.BOTTOM_UI - UI.TOP_UI;

    private static final int BLOCK_WIDTH1 = 1;
    private static final int BLOCK_WIDTH2 = 2;
    // Could self define the ratio of thickness of wall
    private static final double WALL_THICKNESS_1_PROBABILITY = 0.9;

    private static final int MIN_ROOM_WIDTH = 13;

    // Could self define teh ratio of Floor type of room
    private static final double ROOM_FLOOR_POSS = 0.5;


    private static final int MIN_ROOM_NUM = 5;

    private static long seed = 2024726;
    private static Random random = new Random(seed);
    public final TETile[][] world = new TETile[WINDOW_WIDTH][WORLD_HEIGHT];

    private int roomNum;
    private ArrayList<Room> rooms = new ArrayList<>();


    /** Using the default seed to generate the world */
    public World() {
        roomNum = RandomUtils.uniform(random, MIN_ROOM_NUM + 2, 9);
    }


    /** Generate a world that based on input seed
     * @param seed seed for random generator
     */
    public World(long seed) {
        World.seed = seed;
        random = new Random(seed);
        roomNum = RandomUtils.uniform(random, MIN_ROOM_NUM + 2, 9);
    }


    /** Generate a random room for the world. Randomly generate information such as size, location,
     * the thickness of the "wall" of the room, whether it has corner. It will stop generate when it has more than
     * minimum number of room and fails to many times to generate new rooms
     */
    public void generateRooms() {
        int rows = 3;
        int cols = 3;
        int cellWidth = WINDOW_WIDTH / cols;
        int cellHeight = WORLD_HEIGHT / rows;

        // Make a list of all cells
        ArrayList<Point> cells = new ArrayList<>();
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                cells.add(new Point(i * cellWidth, j * cellHeight));
            }
        }

        // Shuffle to ensure random order of selection
        Collections.shuffle(cells, random);

        int roomsToPlace = RandomUtils.uniform(random, MIN_ROOM_NUM, cells.size() + 1);
        int placed = 0;

        for (Point cell : cells) {
            if (placed >= roomsToPlace) break;
            if (generateRoomInCell(cell.x, cell.y, cellWidth, cellHeight)) {
                placed++;
            }
        }

        // Retry with new random cells if under MIN_ROOM_NUM
        int retryLimit = 50;
        int retries = 0;
        while (rooms.size() < MIN_ROOM_NUM && retries++ < retryLimit) {
            Collections.shuffle(cells, random);
            for (Point cell : cells) {
                if (rooms.size() >= MIN_ROOM_NUM) break;
                generateRoomInCell(cell.x, cell.y, cellWidth, cellHeight);
            }
        }
    }


    /* Generate random room, return true if generate a valid room, false otherwise */
    private boolean generateRoom() {
        int width = RandomUtils.uniform(random, MIN_ROOM_WIDTH, 20);
        int height = RandomUtils.uniform(random, MIN_ROOM_WIDTH, 19);
        int x = RandomUtils.uniform(random, 1, WINDOW_WIDTH - width - 1);
        int y = RandomUtils.uniform(random, 1, WORLD_HEIGHT - height - 1);
        int wallThickness = (random.nextDouble() < WALL_THICKNESS_1_PROBABILITY) ? BLOCK_WIDTH1 : BLOCK_WIDTH2;

        if (!Room.validRoom(x, y, width, height, rooms)) {
            return false;
        }

        boolean isCornered = true;
        int cornerRoll = random.nextInt(100);
        if (cornerRoll % 7 == 0 || cornerRoll % 13 == 0 || cornerRoll % 4 == 0) {
            isCornered = false;
        }

        TileType floorType = getRandomPassable();
        TileType wallType = getRandomImpassable(floorType);

        rooms.add(new Room(height, width, x, y, wallThickness, isCornered, floorType, wallType));
        return true;
    }



    /* Generate location of cell(bucket) to make the distribution of room more uniform */
    private boolean generateRoomInCell(int cellX, int cellY, int cellWidth, int cellHeight) {
        int maxAttempts = 10;

        for (int i = 0; i < maxAttempts; i++) {

            // Make the room be able to be square or not square rectangle
            int width, height;
            do {
                width = RandomUtils.uniform(random, 14, 24);
                height = RandomUtils.uniform(random, 14, 24);
            } while (random.nextDouble(0,1) < 0.7 && Math.abs(width - height) < 3);

            // Make the room not exactly the central of the cell
            int maxX = cellX + cellWidth - width - 1;
            int maxY = cellY + cellHeight - height - 1;

            int x = RandomUtils.uniform(random, cellX + 1, maxX);
            int y = RandomUtils.uniform(random, cellY + 1, maxY);

            if (!Room.validRoom(x, y, width, height, rooms)) continue;

            // Randomize the room's floor type, wall type and wall thickness
            TileType floorType = getRandomPassable(), wallType = getRandomImpassable(floorType);
            boolean isCornered = random.nextInt(100) % 4 != 0;
            int wallThickness = (random.nextDouble(0,1) < WALL_THICKNESS_1_PROBABILITY) ? BLOCK_WIDTH1 : BLOCK_WIDTH2;

            rooms.add(new Room(height, width, x, y, wallThickness, isCornered, floorType, wallType));
            return true;
        }

        // Cannot generate a valid room
        return false;
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

        generateRooms();
        for (Room room : rooms) {
            room.allocateRoom(world);
        }

        ter.renderFrame(world);

    }
}



