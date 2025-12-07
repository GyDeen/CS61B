package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.TileType;

import java.awt.*;
import java.util.Objects;

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

    private long nextSwitchTime = 0;

    public PacMan(int x, int y, int width, int height) {
        super(x, y, width, height);
        setImagePath("resources/pac man/pac man & life counter & death");
    }


    /** Switching image path based when PacMan is alive*/
    public void updateImageBasedOnTime(long worldTimeMs) {
        if (!isActive()) return;

        if (worldTimeMs > nextSwitchTime) {
            if (Objects.equals(currentImage, activeImage[0])) {
                currentImage = activeImage[1];
            } else {
                currentImage = activeImage[0];
            }
        }
    }

    /** Return current facing direction */
    public Direction getDirection() {return curDirection;}


    public void dying() {

    }


    private void move(int x, int y) {
        setPosition(x, y);
    }

    private void validMove(int x, int y, TETile[][] world) {
        if (!TileType.toType(world[x][y]).isPassable())  return;

        move(x, y);
    }


    /* Movement update based on input key */
    private void updateBasedInput(Direction direction, TETile[][] world) {
        if (!isActive()) return;

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


    @Override
    /** Draw the image based on current facing. It should be able switch the image based on the time. */
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
}
