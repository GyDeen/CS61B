package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static core.Config.*;
import static core.MainRoom.fullFillRooms;
import static core.UI.*;


public class World {
    private TERenderer ter = new TERenderer();
    private int gameTime = GAME_TIME_IN_SEC;
    private long gameStartTimeMs;

    private static long seed = 654326789;
    private static Random random = new Random(seed);
    public final TETile[][] world = new TETile[WINDOW_WIDTH][WORLD_HEIGHT];

    private int roomNum;
    private ArrayList<Room> majorRooms = new ArrayList<>();
    private ArrayList<Room> fullFillRooms = new ArrayList<>();
    private HallwayCarver carver;

    private enum PlayState { RUNNING, PAUSED }
    private PlayState playState = PlayState.RUNNING;
    private boolean escHeld = false;
    private boolean mouseHeld = false;

    private int settingX = SETTING_WIDTH / 2;
    private int settingY = WINDOW_HEIGHT - SETTING_HEIGHT / 2;
    private String settingImage = "src/resources/Icon/icons8-settings-50.png";


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


    /* Method that generate the room purely randomly, not using cells */
    private void generateRoom() {
        int maxAttempt = 10000, currentAttempt = 0;
        int idealSize = WORLD_HEIGHT * WINDOW_WIDTH / roomNum;

        while (majorRooms.size() < roomNum &&  currentAttempt++ < maxAttempt) {
            // Generate a main room
            MainRoom newRoom = MainRoom.generate(idealSize, random);
            if (Room.validRoom(newRoom, majorRooms, null)) majorRooms.add(newRoom);
        }
    }


    /* Find the closest room for u to connect */
    private MainRoom nearestOf(MainRoom u, List<Room> connected) {
        MainRoom best = null; int bestD2 = Integer.MAX_VALUE;
        for (Room r : connected) {
            MainRoom v = (MainRoom) r;
            int distanceX = v.getLocation().x - u.getLocation().x;
            int distanceY = v.getLocation().y - u.getLocation().y;
            int absoluteDistance = distanceX*distanceX + distanceY*distanceY;
            if (absoluteDistance < bestD2) { bestD2 = absoluteDistance; best = v; }
        }
        return best;
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

        int extraEdges = Math.max(1, majorRooms.size() / random.nextInt(1, 3));

        for (int i = 0; i < extraEdges; i++) {
            if (majorRooms.size() < 2) break;

            // pick two distinct random main rooms
            MainRoom a = (MainRoom) majorRooms.get(random.nextInt(majorRooms.size()));
            MainRoom b = (MainRoom) majorRooms.get(random.nextInt(majorRooms.size()));
            if (a == b) {
                i--;
                continue;
            }

            if (!carver.connect(a, b)) {
                carver.connectSimpleL(a, b);
            }
        }
    }


    // Generate subroom for each room
    private void attachSubRoom(MainRoom room, int minW, int maxW, int minH, int maxH) {
        int subRoomNum = RandomUtils.uniform(random, MIN_SUB_ROOM_NUM, MAX_SUB_ROOM_NUM);
        int allocateSubroomMaxAttempt = subRoomNum * 50;
        // Try to allocate desire subroom number
        for (int i = 0; i < allocateSubroomMaxAttempt; i++) {
            if (room.getSubRooms().size() >= subRoomNum) break;

            Direction direction = Direction.values()[RandomUtils.uniform(random, Direction.values().length)];
            SubRoom subRoom = SubRoom.generate(room.getSize() / 4, random, room, direction, minW, maxW, minH, maxH);
            if (subRoom == null) continue;
            if (Room.validRoom(subRoom, majorRooms, room)) {
                room.attachRoom(subRoom);
            }
        }
    }


    /** Draw the world and generate the whole picture of the world */
    public void renderWorld() {
        ter.initialize(WINDOW_WIDTH, WINDOW_HEIGHT);
        for (int x = 0; x < WINDOW_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }


        generateRoom();
        for (Room room : majorRooms) {
            attachSubRoom((MainRoom) room, MIN_SUB_ROOM_FOR_MAIN_WIDTH, MAX_SUB_ROOM_FOR_MAIN_WIDTH,
                    MIN_SUB_ROOM_FOR_MAIN_HEIGHT, MAX_SUB_ROOM_FOR_MAIN_HEIGHT);
        }

        for (Room room : majorRooms) {
            room.allocateRoom(world);
        }

        fullFillRooms(world, fullFillRooms, majorRooms, random);
        for (Room room : fullFillRooms) {
            attachSubRoom((MainRoom) room, MIN_SUB_ROOM_WIDTH_FOR_FILL, room.getWidth(),
                    MIN_SUB_ROOM_HEIGHT_FOR_FILL, room.getHeight());
        }

        for (Room room : fullFillRooms) {
            room.allocateRoom(world);
        }

        majorRooms.addAll(fullFillRooms);
        generateHallway();

        TETile[][] carved = carver.getWorld();
        for (int x = 0; x < world.length; x++) {
            System.arraycopy(carved[x], 0, world[x], 0, world[0].length);
        }

        ter.renderFrame(world);

        // start game timer
        gameStartTimeMs = System.currentTimeMillis();
    }



    private boolean pauseRequest() {
        boolean escNow = StdDraw.isKeyPressed(KeyEvent.VK_ESCAPE);
        if (escNow && !escHeld) {
            escHeld = true;
            return true;
        }

        if (!escNow) escHeld = false;

        boolean clicking = StdDraw.isMousePressed();
        if (clicking && !mouseHeld) {
            double x = StdDraw.mouseX(), y = StdDraw.mouseY();
            // If the mouse is clicking on the setting icon, return true
            if (onSetting(x, y)) {
                System.out.println("Currently on setting icon");
                mouseHeld = true;
                return true;
            }
        }

        mouseHeld = clicking;
        return false;
    }


    private boolean onSetting(double x, double y) {
        return x >= 0 && x < SETTING_WIDTH
                && y >= settingY - (double) SETTING_HEIGHT / 2 && y < settingY + SETTING_HEIGHT;
    }


    private void drawSetting() {
//        java.io.File f = new java.io.File("src/resources/Icon/icons8-settings-50.png");
//        System.out.println("settings.png exists = " + f.getAbsolutePath() + " -> " + f.exists());
        StdDraw.picture(settingX, settingY, settingImage);
        StdDraw.show();
    }

    private void drawTimer(int remainingSeconds) {
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new java.awt.Font("Monospaced", java.awt.Font.BOLD, 18));
        String text = "Time: " + remainingSeconds + "s";
        StdDraw.textRight(WINDOW_WIDTH, WINDOW_HEIGHT - 1, text);
        StdDraw.show();
    }

    private void update() {
        long currentTime = System.currentTimeMillis();
        int elapsedSeconds = (int) ((currentTime - gameStartTimeMs) / 1000);
        int remainingSeconds = gameTime - elapsedSeconds;
        if (remainingSeconds < 0) remainingSeconds = 0;
        drawTimer(remainingSeconds);

        ter.renderFrameNoShow(world);
        ter.resetFont();
        drawSetting();

    }




    /** The game loop of the game */
    public boolean gameLoop() {
        while (playState == PlayState.RUNNING) {

            if (pauseRequest()) {
                playState = PlayState.PAUSED;
                return true;
            }

            update();
        }

        return true;
    }


    /** Return to gaming */
    public void continueGame() {
        playState = PlayState.RUNNING;
    }
}



