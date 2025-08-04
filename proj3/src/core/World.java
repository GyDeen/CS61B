package core;

import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static java.lang.Math.clamp;


public class World {
    public static final int WINDOW_HEIGHT = 50;
    public static final int WINDOW_WIDTH = 90;
    public static final int WORLD_HEIGHT = WINDOW_HEIGHT - UI.BOTTOM_UI - UI.TOP_UI;

    private static final int BLOCK_WIDTH1 = 1;
    private static final int BLOCK_WIDTH2 = 2;
    // Could self define the ratio of thickness of wall
    private static final double WALL_THICKNESS_1_PROBABILITY = 0.9;

    private static final int MIN_ROOM_WIDTH = 13;

    // Could self define teh ratio of Floor type of room
    private static final double ROOM_FLOOR_POSS = 0.5;


    private static final int MIN_ROOM_NUM = 3;
    private static final int MAX_ROOM_NUM = 5;

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
    public void generateRoomsUsingCells() {
        int rows = 3;
        int cols = 3;
        int cellWidth = WINDOW_WIDTH / cols;
        int cellHeight = WORLD_HEIGHT / rows;

        // Make a list of all cells
        ArrayList<Point> cells = new ArrayList<>();
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                int centerX = i * cellWidth + cellWidth / 2;
                int centerY = j * cellHeight + cellHeight / 2;
                cells.add(new Point(centerX, centerY));
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


    /** Method that generate the room purely randomly, not using cells */
    public void generateWithoutCell() {
        int roomNum = RandomUtils.uniform(random, MIN_ROOM_NUM, MAX_ROOM_NUM + 1);
        int maxAttempt = 100, currentAttempt = 0;
        while (currentAttempt < maxAttempt && rooms.size() < roomNum) {
            if (generateRoom(roomNum)) {
                continue;
            } else {
                currentAttempt++;
                System.out.println("Fail to generate room " + currentAttempt + "th");
            }


            while (rooms.size() < MIN_ROOM_NUM) generateRoom(roomNum);
        }
    }


    /* Generate random room, return true if generate a valid room, false otherwise. */
    private boolean generateRoom(int roomCount) {
        double targetArea = WINDOW_WIDTH * WORLD_HEIGHT * 0.5;
        int idealRoomArea = (int) (targetArea / roomCount);

        // Randomly favor rectangular rooms (not perfect square)
        int width, height;
        for (int i = 0; i < 50; i++) {
            // Randomize width/height ratio slightly
            double aspectRatio = random.nextDouble(0.8, 1.3);
            width = (int) Math.sqrt(idealRoomArea * aspectRatio);
            height = (int) (idealRoomArea / (double) width);

            // Apply some randomness to make the shape of room has more diversity
            width += RandomUtils.uniform(random, -2, 3);
            height += RandomUtils.uniform(random, -2, 3);

            // Make the width and height of the room fit in the requirement
            width = clamp(width, MIN_ROOM_WIDTH, WINDOW_WIDTH - 4);
            height = clamp(height, MIN_ROOM_WIDTH, WORLD_HEIGHT - 4);

            int x = RandomUtils.uniform(random, 1, WINDOW_WIDTH - width - 1);
            int y = RandomUtils.uniform(random, 1, WORLD_HEIGHT - height - 1);

            int wallThickness = (random.nextDouble() < WALL_THICKNESS_1_PROBABILITY) ? BLOCK_WIDTH1 : BLOCK_WIDTH2;

            if (!Room.validRoom(x, y, width, height, rooms)) continue;

            boolean isCornered = random.nextInt(100) % 4 != 0;
            TileType floorType = getRandomPassable();
            TileType wallType = getRandomImpassable(floorType);

            rooms.add(new Room(height, width, x, y, wallThickness, isCornered, floorType, wallType));
            return true;
        }

        return false;
    }




    /* Generate location of cell(bucket) to make the distribution of room more uniform. Return true if successfully
    *  generate a room else false */
    private boolean generateRoomInCell(int cellX, int cellY, int cellWidth, int cellHeight) {
        int maxAttempts = 10;

        for (int i = 0; i < maxAttempts; i++) {
            int maxRoomHeight = cellHeight - 1;

            if (maxRoomHeight < 10) return false;


            // Make the room be able to be square or not square rectangle
            int width = RandomUtils.uniform(random, 25, 35);
            int height = RandomUtils.uniform(random, 10, maxRoomHeight + 1);

            // Make the room not exactly the central of the cell
            int maxX = cellX + cellWidth / 2 - width / 2 - 1;
            int maxY = cellY + cellHeight / 2 - height / 2 - 1;

            int x = RandomUtils.uniform(random, cellX + 1, maxX);
            int y = RandomUtils.uniform(random, cellY + 1,  maxY);

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

        generateWithoutCell();
        System.out.println("Current room number is: " + rooms.size());
        for (Room room : rooms) {
            room.allocateRoom(world);
        }

        ter.renderFrame(world);

    }
}



