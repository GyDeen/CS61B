package core;

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
    private static long seed = 7076;
    private static Random random = new Random(seed);
    public final TETile[][] world = new TETile[WINDOW_WIDTH][WORLD_HEIGHT];

    private int roomNum;
    private ArrayList<Room> rooms = new ArrayList<>();
    private HallwayCarver carver;


    /** Using the default seed to generate the world */
    public World() {
        roomNum = RandomUtils.uniform(random, MIN_MAIN_ROOM_NUM, MAX_MAIN_ROOM_NUM);
    }


    /** Generate a world that based on input seed
     * @param seed seed for random generator
     */
    public World(long seed) {
        World.seed = seed;
        random = new Random(seed);
        roomNum = RandomUtils.uniform(random, MIN_MAIN_ROOM_NUM, MAX_MAIN_ROOM_NUM);
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
        int maxAttempt = 1000, currentAttempt = 0;
        int idealSize = WORLD_HEIGHT * WINDOW_WIDTH / roomNum;

        while (rooms.size() < roomNum &&  currentAttempt++ < maxAttempt) {
            // Generate a main room
            MainRoom newRoom = MainRoom.generate(idealSize, random);
            if (Room.validRoom(newRoom, rooms, null)) rooms.add(newRoom);
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

            MainRoom newRoom = new MainRoom(height, width, x, y, wallThickness, isCornered);
            newRoom.getRandomPassable(random);
            newRoom.getRandomImpassable(random);

            rooms.add(newRoom);
            return true;
        }

        // Cannot generate a valid room
        return false;
    }


    /* Generate hallway */
    private void generateHallway() {
        int attempts = 0;
        carver = new HallwayCarver(world, random);
        ArrayList<Room> connected = new ArrayList<>();
        ArrayList<Room> unconnected = new ArrayList<>(rooms);

        connected.add(unconnected.removeFirst());

        while (!unconnected.isEmpty()) {
            MainRoom u = (MainRoom) unconnected.getFirst();
            boolean linked = false;

            for (Room vR : connected) {
                if (attempts >= ALLOCATE_FAIL_CAP) {
                    if (carver.connectSimpleL((MainRoom) vR, u)) {
                        connected.add(u);
                        unconnected.removeFirst();
                        linked = true;
                        attempts = 0;
                        break;
                    } else {
                        attempts = 0;
                    }
                }
                if (carver.connect((MainRoom) vR, u)) {
                    connected.add(u);
                    unconnected.removeFirst();
                    linked = true;
                    attempts = 0;
                    break;
                }

                attempts++;
            }

            if (!linked) unconnected.add(unconnected.removeFirst());
        }
    }

    /* Generate hallway with given rooms */
    private void generateHallway(ArrayList<Room> givenRooms) {
        int attempts = 0;
        carver = new HallwayCarver(world, random);
        ArrayList<Room> connected = new ArrayList<>();
        ArrayList<Room> unconnected = new ArrayList<>(givenRooms.size());

        connected.add(unconnected.removeFirst());

        while (!unconnected.isEmpty()) {
            MainRoom u = (MainRoom) unconnected.getFirst();
            boolean linked = false;

            for (Room vR : connected) {
                if (attempts >= ALLOCATE_FAIL_CAP) {
                    if (carver.connectSimpleL((MainRoom) vR, u)) {
                        connected.add(u);
                        unconnected.removeFirst();
                        linked = true;
                        attempts = 0;
                        break;
                    } else {
                        attempts = 0;
                    }
                }
                if (carver.connect((MainRoom) vR, u)) {
                    connected.add(u);
                    unconnected.removeFirst();
                    linked = true;
                    attempts = 0;
                    break;
                }

                attempts++;
            }

            if (!linked) unconnected.add(unconnected.removeFirst());
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


        generateRoom();
        // Generate subroom for each main room
        for (Room room : rooms) {
                    int subRoomNum = RandomUtils.uniform(random, MIN_SUB_ROOM_NUM, MAX_SUB_ROOM_NUM);
                    int allocateSubroomMaxAttempt = subRoomNum * 50;
                    // Try to allocate desire subroom number
                    for (int i = 0; i < allocateSubroomMaxAttempt; i++) {
                        if (((MainRoom) room).getSubRooms().size() >= subRoomNum) break;

                        Direction direction = Direction.values()[RandomUtils.uniform(random, Direction.values().length)];
                        SubRoom subRoom = SubRoom.generate(room.getSize() / 4, random, (MainRoom) room, direction);
                        if (Room.validRoom(subRoom, rooms, (MainRoom) room)) {
                            ((MainRoom) room).attachRoom(subRoom);
                        }
                    }

        }

        for (Room room : rooms) {
            room.allocateRoom(world);
        }

        generateHallway();

        TETile[][] carved = carver.getWorld();
        for (int x = 0; x < world.length; x++) {
            System.arraycopy(carved[x], 0, world[x], 0, world[0].length);
        }

        ter.renderFrame(world);
    }
}



