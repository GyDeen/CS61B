package core;

import tileengine.TETile;

import java.awt.*;
import java.util.Random;

import static core.Config.DEFAULT_GHOST_CHASE_DISTANCE;

public abstract class Ghost extends GameObject{
    private boolean chasePlayer = false;
    private Direction currentDir = null;

    private Ghost(int x, int y, int width, int height, Random rand) {
        super(x, y, width, height);
        setImagePath("resources/pac man/ghost");
    }

    private Ghost(int x, int y, int width, int height, MainRoom belongsTo, Random rand) {
        super(x, y, width, height,  belongsTo);
        setImagePath("resources/pac man/ghost");
    }


    /** Chasing player */
    public void chase(PacMan player) {
        if (!chasePlayer) return;
        Point playerPosition = player.getPosition();

        // For Ghost has assigned room:
        // If player not at the same room with Ghost, or the distance between them is too large, Ghost will not chase it
        if (!getRoom().isInRoom(playerPosition.x, playerPosition.y) || distance(playerPosition) > DEFAULT_GHOST_CHASE_DISTANCE) {
            chasePlayer = false;
            return;
        }

        int leftOfPlayer = getPosition().x - playerPosition.x, topOfPlayer = getPosition().y - playerPosition.y;
        boolean horizontalFirst = Math.abs(leftOfPlayer) > Math.abs(topOfPlayer);

        if (horizontalFirst) {
            if (leftOfPlayer < 0) currentDir = Direction.RIGHT;
            else currentDir = Direction.LEFT;
        } else {
            if (topOfPlayer < 0) currentDir = Direction.UP;
            else currentDir = Direction.DOWN;
        }
    }


    public void setChase(boolean chase) {chasePlayer = chase;}

    private double distance(Point p1) {
        return Math.abs(Math.sqrt(Math.pow(p1.x - getPosition().x, 2) + Math.pow(p1.y - getPosition().y, 2)));
    }

    private boolean isHorizontal() {
        return currentDir == Direction.LEFT || currentDir == Direction.RIGHT;
    }

    private void moveToward(Point p1) {
        if (getPosition().x != p1.x) {
            if (isHorizontal()) {
                if (currentDir == Direction.LEFT) {
                    setPosition(getPosition().x - 1, getPosition().y);
                } else {
                    setPosition(getPosition().x + 1, getPosition().y);
                }
            } else {
                if (currentDir == Direction.UP) {
                    setPosition(getPosition().x, getPosition().y + 1);
                } else {
                    setPosition(getPosition().x, getPosition().y - 1);
                }
            }

            return;
        }

        if (getPosition().y - p1.y > 0) {
            currentDir = Direction.DOWN;
            setPosition(getPosition().x, getPosition().y - 1);
        } else {
            currentDir = Direction.UP;
            setPosition(getPosition().x, getPosition().y +1);
        }
    }

}
