package core;

import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static core.Config.*;


public class World {
    private static long seed = 07266262;
    private static Random random = new Random(seed);
    public final TETile[][] world = new TETile[WINDOW_WIDTH][WORLD_HEIGHT];

    private int roomNum;
    private ArrayList<Room> majorRooms = new ArrayList<>();
    private ArrayList<Room> fullFillRooms = new ArrayList<>();
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


    /** Method that generate the room purely randomly, not using cells */
    private void generateRoom() {
        int maxAttempt = 10000, currentAttempt = 0;
        int idealSize = WORLD_HEIGHT * WINDOW_WIDTH / roomNum;

        while (majorRooms.size() < roomNum &&  currentAttempt++ < maxAttempt) {
            // Generate a main room
            MainRoom newRoom = MainRoom.generate(idealSize, random);
            if (Room.validRoom(newRoom, majorRooms, null)) majorRooms.add(newRoom);
        }
    }


    /* Generate hallway */
    private void generateHallway() {
        int attempts = 0;
        carver = new HallwayCarver(world, random);
        ArrayList<Room> connected = new ArrayList<>();
        ArrayList<Room> unconnected = new ArrayList<>(majorRooms);

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


    private boolean isNothing(int x, int y) {
        return world[x][y] == Tileset.NOTHING;
    }



    private void fullFillRooms() {
        int width = world[0].length, height = world.length;
        boolean[][] visited =  new boolean[width][height];
        boolean[][] newAllocated = new boolean[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!isNothing(x, y) || visited[x][y]) continue;

                // start counting the NOTHING area size
                int minX = x, maxX = x, minY = y, maxY = y, area = 0;
                ArrayDeque<Point> dq = new ArrayDeque<>();
                dq.add(new Point(x, y));
                visited[x][y] = true;

                while (!dq.isEmpty()) {
                    Point p = dq.removeFirst();
                    int currentX = p.x, currentY = p.y;
                    area++;

                    // Expand the NOTHING area
                    if (currentX < minX) minX = currentX;
                    if (currentX > maxX) maxX = currentX;
                    if (currentY < minY) minY = currentY;
                    if (currentY > maxY) maxY = currentY;

                    if (currentX > 0 && !visited[currentX - 1][currentY] && isNothing(currentX-1, currentY)) {
                        visited[currentX - 1][currentY] = true;
                        dq.add(new Point(currentX - 1, currentY));
                    }

                    if (currentX + 1 < width &&  !visited[currentX + 1][currentY] && isNothing(currentX + 1, currentY)) {
                        visited[currentX + 1][currentY] = true;
                        dq.add(new Point(currentX + 1, currentY));
                    }

                    if (currentY > 0 && !visited[currentX][currentY - 1] && isNothing(currentX, currentY - 1)) {
                        visited[currentX][currentY - 1] = true;
                        dq.add(new Point(currentX, currentY - 1));
                    }

                    if (currentY + 1 < height && !visited[currentX][currentY + 1] && isNothing(currentX, currentY + 1)) {
                        visited[currentX][currentY + 1] = true;
                        dq.add(new Point(currentX, currentY + 1));
                    }

                    if (area < MIN_VOID_AREA) continue;


                }

            }
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
        for (Room room : majorRooms) {
            int subRoomNum = RandomUtils.uniform(random, MIN_SUB_ROOM_NUM, MAX_SUB_ROOM_NUM);
            int allocateSubroomMaxAttempt = subRoomNum * 50;
            // Try to allocate desire subroom number
            for (int i = 0; i < allocateSubroomMaxAttempt; i++) {
                if (((MainRoom) room).getSubRooms().size() >= subRoomNum) break;

                Direction direction = Direction.values()[RandomUtils.uniform(random, Direction.values().length)];
                SubRoom subRoom = SubRoom.generate(room.getSize() / 4, random, (MainRoom) room, direction);
                if (Room.validRoom(subRoom, majorRooms, (MainRoom) room)) {
                    ((MainRoom) room).attachRoom(subRoom);
                }
            }

        }

        for (Room room : majorRooms) {
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



