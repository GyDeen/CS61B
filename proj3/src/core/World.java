package core;

import org.knowm.xchart.internal.chartpart.Axis;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static core.Config.*;


public class World {
    private static long seed = 2024726;
    private static Random random = new Random(seed);
    public final TETile[][] world = new TETile[WINDOW_WIDTH][WORLD_HEIGHT];

    private int roomNum;
    private ArrayList<Room> rooms = new ArrayList<>();


    /** Using the default seed to generate the world */
    public World() {
        roomNum = RandomUtils.uniform(random, MIN_MAIN_ROOM_NUM + 2, 9);
    }


    /** Generate a world that based on input seed
     * @param seed seed for random generator
     */
    public World(long seed) {
        World.seed = seed;
        random = new Random(seed);
        roomNum = RandomUtils.uniform(random, MIN_MAIN_ROOM_NUM + 2, 9);
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

        int roomsToPlace = RandomUtils.uniform(random, MIN_MAIN_ROOM_NUM, cells.size() + 1);
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
        while (rooms.size() < MIN_MAIN_ROOM_NUM && retries++ < retryLimit) {
            Collections.shuffle(cells, random);
            for (Point cell : cells) {
                if (rooms.size() >= MIN_MAIN_ROOM_NUM) break;
                generateRoomInCell(cell.x, cell.y, cellWidth, cellHeight);
            }
        }
    }


    /** Method that generate the room purely randomly, not using cells */
    public void generateRoom() {
        int roomNum = RandomUtils.uniform(random, MIN_MAIN_ROOM_NUM, MAX_MAIN_ROOM_NUM + 1);
        int maxAttempt = 1000, currentAttempt = 0;
        int idealSize = WORLD_HEIGHT * WINDOW_WIDTH / roomNum;

        while (rooms.size() < roomNum &&  currentAttempt++ < maxAttempt) {
            // Generate a main room
            Room newRoom = Room.generateRoom(idealSize, random, null, 0);
            if (Room.validRoom(newRoom, rooms)) rooms.add(newRoom);
        }
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

            boolean isCornered = random.nextInt(100) % 4 != 0;
            int wallThickness = (random.nextDouble(0,1) < WALL_THICKNESS_1_PROBABILITY) ? BLOCK_WIDTH1 : BLOCK_WIDTH2;

            Room newRoom = new Room(height, width, x, y, wallThickness, isCornered);
            newRoom.getRandomPassable(random);
            newRoom.getRandomImpassable(random);

            rooms.add(newRoom);
            return true;
        }

        // Cannot generate a valid room
        return false;
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


        generateRoom();
        // Generate subroom for each main room
        for (Room room : rooms) {
            int direction = RandomUtils.uniform(random, 0, 5);
            Room subRoom = Room.generateRoom(room.getSize() / 4, random, room, direction);

            // Check whether it will overlap with other main room
            if (Room.validRoom(subRoom, rooms)) {
                subRoom.attachRoom(room);
            }
        }

        for (Room room : rooms) {
            room.allocateRooms(world);
        }

        ter.renderFrame(world);

    }
}



