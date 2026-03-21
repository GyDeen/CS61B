package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.TileType;

import java.awt.*;
import java.util.Random;

import static core.Config.DEFAULT_GHOST_CHASE_DISTANCE;
import static core.Config.GHOST_MOVE_COOLDOWN_DEFAULT;
import static tileengine.Tileset.GHOST;
import static tileengine.Tileset.MOUNTAIN;

public abstract class Ghost extends GameObject{
    private boolean chasePlayer = false;
    private Direction currentDir = null;
    private long nextMoveTime = 0;
    private long moveCoolDown = GHOST_MOVE_COOLDOWN_DEFAULT;

    protected Ghost(int x, int y, int width, int height, Random rand) {
        super(x, y, width, height);
        setImagePath("resources/pac man/ghost");
    }

    protected Ghost(int x, int y, int width, int height, MainRoom belongsTo, Random rand) {
        super(x, y, width, height,  belongsTo);
        setImagePath("resources/pac man/ghost");
    }


    /* Chasing player */
    private void chase(PacMan player) {
        if (!chasePlayer) return;
        Point playerPosition = player.getPosition();

        // For Ghost has assigned room:
        // If player not at the same room with Ghost, or the distance between them is too large, Ghost will not chase it
        if (!getRoom().isInRoom(playerPosition.x, playerPosition.y) || distance(playerPosition) > DEFAULT_GHOST_CHASE_DISTANCE) {
            chasePlayer = false;
            return;
        }

        int leftOfPlayer = getPosition().x - playerPosition.x, topOfPlayer = getPosition().y - playerPosition.y;

        if (leftOfPlayer < 0) currentDir = Direction.RIGHT;
        else currentDir = Direction.LEFT;
    }


    public void setChase(boolean chase) {chasePlayer = chase;}

    private double distance(Point p1) {
        return Math.abs(Math.sqrt(Math.pow(p1.x - getPosition().x, 2) + Math.pow(p1.y - getPosition().y, 2)));
    }

    private boolean isHorizontal() {
        return currentDir == Direction.LEFT || currentDir == Direction.RIGHT;
    }


    /** Moving only determine by the dx and dy between the p1 and current position not by the direction */
    public boolean moveToward(Point p1, TETile[][] world, long worldTime) {
        if (worldTime < nextMoveTime) return false;

        int dx = Integer.compare(p1.x, getPosition().x);
        int dy = Integer.compare(p1.y, getPosition().y);
        int currentX = getPosition().x;
        int currentY = getPosition().y;

        boolean moved = false;

        // try move directly first
        if (canGhostEnter(currentX + dx, currentY + dy, world)) {
            setPosition(currentX + dx, currentY + dy);
            moved = true;
        }
        // if being blocked, try move horizontally first
        else if (dx != 0 && canGhostEnter(currentX + dx, currentY, world)) {
            setPosition(currentX + dx, currentY);
            moved = true;
        }
        // try move vertically
        else if (dy != 0 && canGhostEnter(currentX, currentY + dy, world)) {
            setPosition(currentX, currentY + dy);
            moved = true;
        }

        // Only update cooldown and facing direction if we actually moved
        if (moved) {
            nextMoveTime = worldTime + moveCoolDown;
            if (dx != 0) {
                this.currentDir = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
            }
            return true;
        }

        return false;
    }

    private boolean canGhostEnter(int x, int y, TETile[][] world) {
        if (x < 0 || y < 0 || x >= world.length || y >= world[0].length) return false;
        TileType targetTile = TileType.toType(world[x][y]);

        // Ghost can step anywhere EXCEPT walls and the void
        return !targetTile.isWallType() && targetTile != TileType.NOTHING;
    }


    /** Update without given destination point for set route ghost*/
    public void update(TETile[][] world) {

    }


    /** Update with given destination point */
    public void update(Point destination) {}


    /** Update when set to chase player */
    public void update (PacMan player) {}


    /** Draw Ghost image on current position */
    public void draw() {
        StdDraw.picture(getPosition().x + 0.5, getPosition().y + 0.5, getImagePath(), 1, 1);
    }


    public Direction getDirection() {return currentDir;}

}
