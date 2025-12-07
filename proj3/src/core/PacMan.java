package core;

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

    public PacMan(int x, int y, int width, int height) {
        super(x, y, width, height);
        setImagePath("resources/pac man/pac man & life counter & death");
    }


    /* Switching image path based when PacMan is alive*/
    private void changeImageAlive() {
        if (Objects.equals(currentImage, activeImage[0])) {
            currentImage = activeImage[1];
        } else {
            currentImage = activeImage[0];
        }
    }


    public void dying() {

    }


    private void move(int x, int y) {
        setPosition(x, y);
    }

    /* Movement update based on input key */
    private void updateBasedInput(Direction direction, TETile[][] world) {
        Point curPosition = getPosition();
        switch (direction) {
            case UP:
                if (!TileType.toType(world[curPosition.x][curPosition.y + 1]).isPassable()) return;
                move(curPosition.x, curPosition.y + 1);
                curDirection = Direction.UP;
                break;
            case DOWN:
                if (!TileType.toType(world[curPosition.x][curPosition.y - 1]).isPassable()) return;
                move(curPosition.x, curPosition.y - 1);
                curDirection = Direction.DOWN;
                break;
            case LEFT:
                if (!TileType.toType(world[curPosition.x - 1][curPosition.y]).isPassable()) return;
                move(curPosition.x - 1, curPosition.y);
                curDirection = Direction.LEFT;
                break;
            case RIGHT:
                if (!TileType.toType(world[curPosition.x + 1][curPosition.y]).isPassable()) return;
                move(curPosition.x + 1, curPosition.y);
                curDirection = Direction.RIGHT;
                break;
        }
    }


    /** Return current facing direction */
    public Direction getDirection() {return curDirection;}
}
