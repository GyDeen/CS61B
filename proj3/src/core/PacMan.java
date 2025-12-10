package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.TileType;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

import static core.Config.IMAGE_SWITCHING_PERIOD;

public class PacMan extends GameObject{
    private final String[] activeImage = {
            getImagePath() + "pac man/pac_man_1.png",
            getImagePath() + "pac man/pac_man_2.png"
    };

    private final String[] dyingImage = {
            getImagePath() + "pac man death/spr_pacdeath_0.png",
            getImagePath() + "pac man death/spr_pacdeath_1.png",
            getImagePath() + "pac man death/spr_pacdeath_2.png",
    };

    private int frameIndex;
    private String currentImage;
    private Direction curDirection;

    private long nextSwitchTimeMs = 0;

    private PacMan(int x, int y, int width, int height) {
        super(x, y, width, height);
        setImagePath("resources/pac man/pac man & life counter & death");
    }


    public PacMan generatePacMan(MainRoom initialRoom, Random rand, TETile[][] world) {
        int maxAttempt = Config.MAX_ATTEMPT_PIVOT;

        for (int i = 0; i < maxAttempt; i++) {
            int minX = initialRoom.getLeft();
            int minY = initialRoom.getBottom();
            int maxX = initialRoom.getRight();
            int maxY = initialRoom.getTop();

            int x = rand.nextInt(minX, maxX);
            int y = rand.nextInt(minY, maxY);

            if (initialRoom.isInRoom(x, y) && TileType.toType(world[x][y]).isPassable()) return new PacMan(x, y, 1, 1);
        }

        int x = initialRoom.getLocation().x;
        int y = initialRoom.getLocation().y;
        int[] directions = {1, -1};

        // Starting from the center of the initial room to different direction to find the first available spot
        while (initialRoom.isInRoom(x, y) && !TileType.toType(world[x][y]).isPassable()) {
            for (Integer direction : directions) {
                if (TileType.toType(world[x + direction][y]).isPassable()) {
                    return new PacMan(x + direction, y, 1, 1);
                }

                if (TileType.toType(world[x ][y + direction]).isPassable()) {
                    return new PacMan(x, y + direction, 1, 1);
                }
            }
        }

        return new PacMan(x, y, 1, 1);
    }


    /** Switching image path based when PacMan is alive*/
    public void updateImageBasedOnTime(long worldTimeMs) {
        if (!isActive()) return;

        // first time: initialise nextSwitchTime
        if (nextSwitchTimeMs == 0) {
            nextSwitchTimeMs = worldTimeMs + IMAGE_SWITCHING_PERIOD;
            return;
        }

        if (worldTimeMs < nextSwitchTimeMs) return;


        // time to toggle frame
        frameIndex = 1 - frameIndex;
        currentImage = activeImage[frameIndex];

        // Compute the next switch time
        nextSwitchTimeMs = worldTimeMs + IMAGE_SWITCHING_PERIOD;
    }

    /** Return current facing direction */
    public Direction getDirection() {return curDirection;}


    public void dying() {

    }


    private void move(int x, int y) {
        setPosition(x, y);
    }


    // Check whether PacMan can move to the desire position
    private void validMove(int x, int y, TETile[][] world) {
        if (!TileType.toType(world[x][y]).isPassable())  return;

        move(x, y);
    }


    /* Movement update based on input key */
    private void updateBasedInput(Direction direction, TETile[][] world) {
        int newX = getPosition().x, newY = getPosition().y;
        Point curPosition = getPosition();
        switch (direction) {
            case UP:
                newX = curPosition.x; newY = curPosition.y + 1;
                curDirection = Direction.UP;
                break;
            case DOWN:
                newX = curPosition.x; newY = curPosition.y - 1;
                curDirection = Direction.DOWN;
                break;
            case LEFT:
                newX = curPosition.x - 1; newY = curPosition.y;
                curDirection = Direction.LEFT;
                break;
            case RIGHT:
                newX = curPosition.x + 1; newY = curPosition.y;
                curDirection = Direction.RIGHT;
                break;
        }

        validMove(newX, newY, world);
    }


    /* Based on the input update the PacMan position and direction */
    private void handleInput(TETile[][] world) {
        if (!isActive()) return;

        if (!StdDraw.hasNextKeyTyped()) return;


        char key = StdDraw.nextKeyTyped();
        Direction dir = null;

        switch (Character.toLowerCase(key)) {
            case 'w', 'W' -> dir = Direction.UP;
            case 's', 'S' -> dir = Direction.DOWN;
            case 'a', 'A' -> dir = Direction.LEFT;
            case 'd', 'D' -> dir = Direction.RIGHT;
            default -> {return;}
        }

        updateBasedInput(dir, world);
    }


    @Override
    /** Draw the image based on current facing. It should be able to switch the image based on the time */
    public void drawImage() {
        double angle = 0d;
        int scaledX = 1, scaledY = 1;

        switch (curDirection) {
            case UP:
                angle = 90;
                break;
            case DOWN:
                angle = -90;
                break;
            case LEFT:
                angle = 0;
                scaledX = -1;
                break;
            default:
                angle = 0;
                break;
        }

        StdDraw.picture(getPosition().x, getPosition().y, currentImage, scaledX, scaledY, angle);
    }


    /** Update the image first (switch image based on world time). Then handle the input to determine whether it could move
     * and adjust its facing. */
    public void update(long worldTime, TETile[][] world) {
        updateImageBasedOnTime(worldTime);
        handleInput(world);
        drawImage();
    }
}
