package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.TileType;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

import static core.Config.IMAGE_SWITCHING_PERIOD;
import static core.Config.PAC_MAN_MOVE_COOLDOWN_DEFAULT;

public class PacMan extends GameObject{
    private final String[] activeImage;
    private final String[] dyingImage;

    private int frameIndex;
    private String currentImage;
    private Direction curDirection;
    private Direction nextIntendedDir = null;

    private long nextSwitchTimeMs = 0;
    private long nextMoveTimeMs = 0;
    private long moveCoolDown = PAC_MAN_MOVE_COOLDOWN_DEFAULT;
    private boolean isWeaponized = false;

    private PacMan(int x, int y, int width, int height) {
        super(x, y, width, height);
        setImagePath("resources/pac man/pac man & life counter & death/");

        activeImage = new String[] {
                getImagePath() + "pac man/pac_man_0.png",
                getImagePath() + "pac man/pac_man_1.png",
                getImagePath() + "pac man/pac_man_2.png",
                getImagePath() + "pac man/pac_man_3.png",
                getImagePath() + "pac man/pac_man_4.png"
        };
        dyingImage = new String[] {
                getImagePath() + "pac man death/spr_pacdeath_0.png",
                getImagePath() + "pac man death/spr_pacdeath_1.png",
                getImagePath() + "pac man death/spr_pacdeath_2.png"
        };

        currentImage = activeImage[0];
        curDirection = Direction.RIGHT;
    }


    public static PacMan generatePacMan(MainRoom initialRoom, Random rand, TETile[][] world) {
        Point p = findSpawnLocation(initialRoom,1, rand, world);
        return new PacMan(p.x, p.y, 1, 1);
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
        frameIndex = 4 - frameIndex;
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


    /* Update direction based on input. Even it is during the cooldown of the movement */
    private Direction getDirectionFromKey(char key) {
        return switch (Character.toLowerCase(key)) {
            case 'w', 'W' -> Direction.UP;
            case 's', 'S' -> Direction.DOWN;
            case 'a', 'A' -> Direction.LEFT;
            case 'd', 'D' -> Direction.RIGHT;
            default -> null;
        };
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
    private void handleInput(TETile[][] world, char nextInput) {
        if (!isActive()) return;
        Direction dir = getDirectionFromKey(nextInput);
        assert dir != null;
        updateBasedInput(dir, world);
    }


    @Override
    /** Draw the image based on current facing. It should be able to switch the image based on the time */
    public void drawImage() {
        double angle = 0d;
        int scaledX = 1, scaledY = 1;

        angle = switch (curDirection) {
            case UP -> 90;
            case DOWN -> -90;
            case LEFT -> 180;
            default -> 0;
        };

        StdDraw.picture(getPosition().x + 0.5, getPosition().y + 0.5, currentImage, 1, 1, angle);
    }


    /** Update the image first (switch image based on world time). Then handle the input to determine whether it could move
     * and adjust its facing. */
    public void update(long worldTime, TETile[][] world, char nextInput) {
        updateImageBasedOnTime(worldTime);

        if (nextInput != '\0') {
            Direction inputDir = getDirectionFromKey(nextInput);
            if (inputDir != null) {
                this.nextIntendedDir = inputDir;
            }
        }

        // Only process input if enough time has passed
        if (worldTime >= nextMoveTimeMs) {
            // If there is an intended direction, change to the direction first
            if (nextIntendedDir != null) {
                this.curDirection = nextIntendedDir;
                updateBasedInput(this.curDirection, world);
                nextIntendedDir = null;
                nextMoveTimeMs = worldTime + moveCoolDown;
            }
        }

        drawImage();
    }


    /** Setter for buff/debuff*/
    public void setMoveCoolDown(long moveCoolDown) {
        this.moveCoolDown = moveCoolDown;
    }


    /** Allow player to destroy Enemies */
    public void setWeaponized(boolean weaponized) {
        isWeaponized = weaponized;
    }
}
