package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

import static core.Config.*;
import static core.MainRoom.fullFillRooms;
import static core.UI.*;


public class World {
    private class Setting {
        private int settingX = SETTING_WIDTH / 2;
        private int settingY = WINDOW_HEIGHT - SETTING_HEIGHT / 2;
        private String settingImage = "src/resources/Icon/icons8-settings-50.png";

        private boolean onSetting(double x, double y) {
            return x >= 0 && x < SETTING_WIDTH
                    && y >= settingY - (double) SETTING_HEIGHT / 2 && y < settingY + SETTING_HEIGHT;
        }


        private void drawSetting() {
            java.io.File f = new java.io.File("src/resources/Icon/icons8-settings-50.png");
            System.out.println("settings.png exists = " + f.getAbsolutePath() + " -> " + f.exists());
            StdDraw.picture(settingX, settingY, settingImage);
            StdDraw.show();
        }
    }

    private enum PlayState { RUNNING, PAUSED }
    private PlayState playState = PlayState.RUNNING;
    private boolean escHeld = false;
    private boolean mouseHeld = false;
    private TERenderer ter = new TERenderer();
    private int gameTime = GAME_TIME_IN_SEC;
    private long gameStartTimeMs;
    private long elapsedTimeMs = 0;
    private int gameResult = LOSE;

    private static long seed = 654326789;
    private static Random random = new Random(seed);
    public final TETile[][] world = new TETile[WINDOW_WIDTH][WORLD_HEIGHT];

    private Setting setting = new Setting();
    private int roomNum;
    private ArrayList<MainRoom> majorRooms = new ArrayList<>();
    private ArrayList<MainRoom> fullFillRooms = new ArrayList<>();
    private MainRoom[] playerSpawnAndFinalBoxRoom = new MainRoom[2];
    private HallwayCarver carver;
    private PacMan player;


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


    private void playerSpawn() {

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


    /* Generate hallway */
    private void generateHallway() {
        int attempts = 0;
        carver = new HallwayCarver(world, random);
        ArrayList<MainRoom> connected = new ArrayList<>();
        ArrayList<MainRoom> unconnected = new ArrayList<>(majorRooms);

        connected.add(unconnected.removeFirst());

        while (!unconnected.isEmpty()) {
            MainRoom u = unconnected.getFirst();
            boolean linked = false;

            for (MainRoom vR : connected) {
                // If we cannot connect with more turn, we will just connect two room using straight forward
                if (attempts >= ALLOCATE_FAIL_CAP) {
                    if (carver.connectSimpleL(vR, u)) {
                        connected.add(u);
                        // Remove the room u from unconnected since it successfully connect with room vR
                        unconnected.removeFirst();
                        linked = true;
                        attempts = 0;
                        break;
                    } else {
                        attempts = 0;
                    }
                }
                if (carver.connect(vR, u)) {
                    connected.add(u);
                    // Remove the room u from unconnected since it successfully connect with room vR
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
            MainRoom a = majorRooms.get(random.nextInt(majorRooms.size()));
            MainRoom b = majorRooms.get(random.nextInt(majorRooms.size()));
            if (a == b) {
                i--;
                continue;
            }

            if (!carver.connect(a, b)) {
                carver.connectSimpleL(a, b);
            }
        }
    }


    /* Finding the most distance room pair. Remove them from those ArrayList to avoid initial connection */
    private void findMostDistanceRoom() {
        MainRoom bestA = null;
        MainRoom bestB = null;
        double greatestDistance = -1.0;

        ArrayList<MainRoom> rooms = new ArrayList<>(majorRooms);
        rooms.addAll(fullFillRooms);

        int n = rooms.size();
        for (int i = 0; i < n; i++) {
            MainRoom a = rooms.get(i);
            for (int j = i + 1; j < n; j++) {
                MainRoom b = rooms.get(j);
                double curDistance = MainRoom.distanceBetween(a, b);
                if (curDistance > greatestDistance) {
                    bestA = a;
                    bestB = b;
                    greatestDistance = curDistance;
                }
            }
        }

        playerSpawnAndFinalBoxRoom[0] = bestA;
        playerSpawnAndFinalBoxRoom[1] = bestB;

        // Remove from major rooms to avoid initial connection
        for (MainRoom room : majorRooms) {
            if (room.equals(bestA)) majorRooms.remove(room);
            if (room.equals(bestB)) majorRooms.remove(room);
        }


        // Remove from fullFillRooms to avoid initial connection
        for (MainRoom room : fullFillRooms) {
            if (room.equals(bestA)) fullFillRooms.remove(room);
            if (room.equals(bestB)) fullFillRooms.remove(room);
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
            if (setting.onSetting(x, y)) {
                System.out.println("Currently on setting icon");
                mouseHeld = true;
                return true;
            }
        }

        mouseHeld = clicking;
        return false;
    }


    private void drawTimer(int remainingSeconds) {
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new java.awt.Font("Monospaced", java.awt.Font.BOLD, 18));
        String text = "Time: " + remainingSeconds + "s";
        StdDraw.textRight(WINDOW_WIDTH, WINDOW_HEIGHT - 1, text);
        StdDraw.show();
    }


    private void updateTimer() {
        long currentTime = System.currentTimeMillis();
        elapsedTimeMs = currentTime - gameStartTimeMs;
        int elapsedSeconds = (int) (elapsedTimeMs / 1000);
        int remainingSeconds = gameTime - elapsedSeconds;
        if (remainingSeconds < 0) remainingSeconds = 0;
        drawTimer(remainingSeconds);

        ter.renderFrameNoShow(world);
        ter.resetFont();
        setting.drawSetting();
    }


    /** The game loop of the game */
    public int gameLoop() {
        while (playState == PlayState.RUNNING) {

            if (gameResult == WIN) return WIN;
            if (!player.isActive()) return LOSE;

            if (pauseRequest()) {
                playState = PlayState.PAUSED;
                return PAUSE;
            }

            updateTimer();
        }

        return PAUSE;
    }


    /** Return to gaming */
    public void continueGame() {
        playState = PlayState.RUNNING;
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



    /** Set game result to win */
    public void playerWin() {gameResult = WIN;}


    /** Return the elapsed time for the game */
    public long getElapsedTimeMs() {return elapsedTimeMs;}
}



