package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

import static core.Config.*;
import static core.FinalBox.*;
import static core.MainRoom.fullFillRooms;
import static core.PacMan.*;
import static core.UI.drawTimer;


public class World {
    private enum PlayState { RUNNING, PAUSED}
    private PlayState playState = PlayState.RUNNING;
    private boolean escHeld = false;
    private boolean mouseHeld = false;
    private TERenderer ter = new TERenderer();
    private int gameTime = GAME_TIME_IN_SEC;
    private long gameStartTimeMs;
    private long elapsedTimeMs = 0;
    private int remainTime = gameTime;
    private int gameResult = LOSE;

    private static long seed = 654326789;
    private static Random random = new Random(seed);
    private static final double difficulty = DEFAULT_DIFFICULTY;
    public final TETile[][] world = new TETile[WINDOW_WIDTH][WORLD_HEIGHT];

    private UI.Setting setting = new UI.Setting();
    int currentRoomID = 0;
    private int roomNum;
    private ArrayList<MainRoom> majorRooms = new ArrayList<>();
    private ArrayList<MysteryBox>  mysteryBoxes = new ArrayList<>();
    private ArrayList<Gold> golds = new ArrayList<>();
    private HallwayCarver carver;
    private PacMan player;
    private FinalBox finalBox;
    private int money = 0;
    private int destroyedEnemies = 0;



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


    /* Place final box and player into the most distance room pair */
    private void initialPlayerAndFinalBox() {
        MainRoom[] playerSpawnAndFinalBoxRoom;
        playerSpawnAndFinalBoxRoom = findMostDistanceRoom();

        for (MainRoom room : playerSpawnAndFinalBoxRoom) {
            System.out.println("Best room pair @" + room.getLocation());
        }

        player = generatePacMan(playerSpawnAndFinalBoxRoom[0], random, world);
        finalBox = generateFinalBox(playerSpawnAndFinalBoxRoom[1], random, world);
    }


    /* Method that generate the room purely randomly, not using cells */
    private void generateRoom() {
        int maxAttempt = 10000, currentAttempt = 0;
        int idealSize = WORLD_HEIGHT * WINDOW_WIDTH / roomNum;

        while (majorRooms.size() < roomNum &&  currentAttempt++ < maxAttempt) {
            // Generate a main room
            MainRoom newRoom = MainRoom.generate(idealSize, random, currentRoomID++);
            if (Room.validRoom(newRoom, majorRooms, null)) majorRooms.add(newRoom);
        }
    }


    /* Generate hallway */
    private void generateHallway() {
        int attempts = 0;
        carver = new HallwayCarver(world, random, finalBox.getRoom());
        ArrayList<MainRoom> connected = new ArrayList<>();
        ArrayList<MainRoom> unconnected = new ArrayList<>(majorRooms);

        connected.add(unconnected.removeFirst());

        while (!unconnected.isEmpty()) {
            MainRoom u = unconnected.getFirst();
            boolean linked = false;

            for (MainRoom vR : connected) {
                // If we cannot connect with more turn, we will just connect two room using straight forward
                if (attempts >= ALLOCATE_FAIL_CAP) {
                    if (carver.connectSimpleL(vR, u, false)) {
                        connected.add(u);
                        u.addDirectlyConnected(vR);
                        vR.addDirectlyConnected(u);
                        // Remove the room u from unconnected since it successfully connect with room vR
                        unconnected.removeFirst();
                        linked = true;
                        attempts = 0;
                        break;
                    } else {
                        attempts = 0;
                    }
                }
                if (carver.connect(vR, u, false)) {
                    connected.add(u);
                    u.addDirectlyConnected(vR);
                    vR.addDirectlyConnected(u);
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

            if (!carver.connect(a, b, false)) {
                carver.connectSimpleL(a, b, true);
            }
        }

        System.out.println("FinalBox position: " + finalBox.getPosition());
        // Finally, connect the final room with the major rooms graph
        carver.connectFinalRoom(finalBox.getRoom(), majorRooms);
    }


    /* Finding the most distance room pair. Remove them from those ArrayList to avoid initial connection */
    private MainRoom[] findMostDistanceRoom() {
        MainRoom[] bestPair = new MainRoom[2] ;
        double greatestDistance = -1.0;

        ArrayList<MainRoom> rooms = new ArrayList<>(majorRooms);

        int n = rooms.size();
        for (int i = 0; i < n; i++) {
            MainRoom a = rooms.get(i);
            for (int j = i + 1; j < n; j++) {
                MainRoom b = rooms.get(j);
                double curDistance = MainRoom.distanceBetween(a, b);
                if (curDistance > greatestDistance) {
                    bestPair[0] = a;
                    bestPair[1] = b;
                    greatestDistance = curDistance;
                }
            }
        }


        // Remove the best pair from the major rooms list to avoid connection at first
        majorRooms.remove(bestPair[1]);
        System.out.println("Remove room@" + bestPair[1].getLocation());

        return bestPair;
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


    private void generateMysteryBoxes() {
        int mysteryBoxNum = majorRooms.size() * 4 / 5;

        while (mysteryBoxes.size() < mysteryBoxNum) {
            MysteryBox box = MysteryBox.generateMysteryBox(majorRooms, world, random);
            if (box == null) continue;
            mysteryBoxes.add(box);
        }
    }

    private void generateGold() {
        int goldNum = majorRooms.size() * 3 / 2;
        while (golds.size() < goldNum) {
            Gold coin = Gold.goldGenerator(100, majorRooms, random, world);
            if (coin == null) continue;
            golds.add(coin);
        }
    }


    private boolean nextToGameObject(GameObject object) {
        Point pPos = player.getPosition();
        Point cPos = object.getPosition();

        if (object.getImageWidth() > 1) { // 2x2 case
            boolean xNearby = pPos.x >= cPos.x - 1 && pPos.x <= cPos.x + 2;
            boolean yNearby = pPos.y >= cPos.y - 2 && pPos.y <= cPos.y + 1;

            return xNearby && yNearby;
        } else { // 1x1 case
            return Math.abs(pPos.x - cPos.x) <= 1 && Math.abs(pPos.y - cPos.y) <= 1;
        }
    }


    private void updateTimer() {
        long currentTime = System.currentTimeMillis();
        elapsedTimeMs = currentTime - gameStartTimeMs;
        int elapsedSeconds = (int) (elapsedTimeMs / 1000);
        remainTime = gameTime - elapsedSeconds;
        if (remainTime < 0) remainTime = 0;
        drawTimer(remainTime);

        ter.renderFrameNoShow(world);
        ter.resetFont();
    }


    public void removeCollected(GameObject obj) {
        TETile tile = obj.getRoom().getFloorType().toTETile();
        Point p = obj.getPosition();
        if (obj.getImageWidth() > 1) {
            world[p.x][p.y] = tile;
            world[p.x + 1][p.y] = tile;
            world[p.x][p.y - 1] = tile;
            world[p.x + 1][p.y - 1] = tile;
        } else {
            world[p.x][p.y] = tile;
        }
    }


    /** The game loop of the game */
    public int gameLoop() {
        while (playState == PlayState.RUNNING) {

            if (gameResult == WIN) return WIN;
            if (!player.isActive() || gameTime - elapsedTimeMs / 1000 <= 0) return LOSE;

            if (pauseRequest()) {
                playState = PlayState.PAUSED;
                return PAUSE;
            }

            char key = '\0';
            if (StdDraw.hasNextKeyTyped()) {
                key = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (key == 'f') {
                    for (MysteryBox box : mysteryBoxes) {
                        if (box.isActive() && box.canInteract(player)) {
                            box.startFading();
                        }
                    }
                }
            }


            player.update(elapsedTimeMs, world, key);
            int finalBoxStatus = finalBox.update(this);
            if (finalBoxStatus == FINISHED) { playerWin(); }
            if (nextToGameObject(finalBox)) {
                if (nextToGameObject(finalBox) && key == 'f') {
                    finalBox.startOpening();
                }
            }
            for (int i = 0; i < mysteryBoxes.size(); i++) {
                MysteryBox box = mysteryBoxes.get(i);
                int status = box.update(this);

                // Finished fading
                if (status == FINISHED) {
                    removeCollected(box);
                    box.destroy();
                    mysteryBoxes.remove(i);
                    i--;
                }
            }

            for (int i = 0; i < golds.size(); i++) {
                Gold gold = golds.get(i);

                if (nextToGameObject(gold)) {
                    this.money += gold.getWorth();

                    removeCollected(gold);
                    gold.destroy();

                    golds.remove(i);
                    // Adjust index because the list shifted left
                    i--;
                    continue;
                }

                gold.drawImage();
            }

            UI.drawMoney(money);
            setting.drawSetting();
            updateTimer();
            UI.drawUIBackground();
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
        fullFillRooms(world, majorRooms, majorRooms, random, currentRoomID++);
        for (MainRoom room : majorRooms) {
            attachSubRoom(room, MIN_SUB_ROOM_FOR_MAIN_WIDTH, MAX_SUB_ROOM_FOR_MAIN_WIDTH,
                    MIN_SUB_ROOM_FOR_MAIN_HEIGHT, MAX_SUB_ROOM_FOR_MAIN_HEIGHT);
        }

        for (Room room : majorRooms) {
            room.allocateRoom(world);
        }

        initialPlayerAndFinalBox();

        generateHallway();

        TETile[][] carved = carver.getWorld();
        for (int x = 0; x < world.length; x++) {
            System.arraycopy(carved[x], 0, world[x], 0, world[0].length);
        }

        generateMysteryBoxes();
        generateGold();

        ter.renderFrame(world);

        // start game timer
        gameStartTimeMs = System.currentTimeMillis();
    }



    /** Set game result to win */
    public void playerWin() {gameResult = WIN;}


    /** Return current game player */
    public PacMan getPlayer() {return player;}


    /** Return the elapsed time for the game */
    public int getRemainTime() {return remainTime;}


    public long getElapsedTimeMs() {return elapsedTimeMs;}


    /** Return current money count */
    public int getMoney() {return money;}

    /** Return how many enemies player destroyed*/
    public int destroyedEnemies() {return destroyedEnemies;}


    /** Return current game difficulty */
    public double getDifficulty() {return difficulty;}


    /** Add money */
    public void addMoney(int amount) {
        if (money + amount > 0) {
            money += amount;
        } else {
            money = 0;
        }
    }


    /** Get random */
    public Random getRandom() {return random;}
}



