package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static tileengine.Tileset.FLOOR;

public class HallwayCarver {
    private static TETile[][] world;
    private static boolean[][] floor;
    private static boolean[][] wall;
    ArrayList<Point> door;
    Random random;

    public HallwayCarver(TETile[][] world, Random rand) {
        random = rand;

        HallwayCarver.world = new TETile[world.length][];
        for (int i = 0; i < world.length; i++) {
            HallwayCarver.world[i] = Arrays.copyOf(world[i], world[i].length);
        }

        floor = new boolean[world.length][world[0].length];
        wall = new boolean[world.length][world[0].length];

        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                TileType worldTile= TileType.toType(world[x][y]);
                if (worldTile.isPassable()) {
                    floor[x][y] = true;
                } else if (worldTile == TileType.NOTHING) {
                    floor[x][y] = false;
                    wall[x][y] = false;
                } else {
                    wall[x][y] = true;
                }
            }
        }
    }

    private void setFloor(int x, int y) {
        world[x][y] = FLOOR;
        floor[x][y] = true;
        wall[x][y]  = false;
    }

    private void setWall(int x, int y) {
        world[x][y] = Tileset.WALL;
        wall[x][y]  = true;
        floor[x][y] = false;
    }

    private boolean inBounds(int x, int y) {
        return 0 <= x && x < world.length && 0 <= y && y < world[0].length;
    }

    public TETile[][] getWorld() { return world; }


    /** Connect two room without given Door*/
    public boolean connect(Room a, Room b, boolean placeDoors) {
        return connect(a, null, b, null, placeDoors);
    }

    /** Connect two room with given Door */
    public boolean connect(Room a, Point doorA, Room b, Point doorB, boolean placeDoors) {
        Point drA = (doorA != null) ? doorA : pickDoorOnPerimeter(a, b);
        Point drB = (doorB != null) ? doorB : pickDoorOnPerimeter(b, a);
        Direction direc = null;

        // Door on the horizontal
        if (drA.x == a.getLeft()) {
            direc = Direction.LEFT;
        } else if (drA.x == a.getRight()) {
            direc = Direction.RIGHT;
        }

        // Door on vertical
        if (drA.y == a.getTop()) {
            direc = Direction.UP;
        } else  if (drA.y == a.getBottom()) {
            direc = Direction.DOWN;
        }

        // Find how many pivot we need. If it has no alignment for both doors, it needs 2. If it has either x or y align,
        // it needs 1. If both align, it needs 0
        int pivotCount = 0;
        if (drA.x == drB.x && drA.y == drB.y) {
            pivotCount = 2;
        } else if (drA.x == drB.x || drA.y == drB.y) {
            pivotCount = 1;
        }

        // Generate pivot position
        Point[] pivots = new Point[pivotCount + 2];
        if (HallwayCarver.distancePoint(drA.x, drA.y, drB.x, drB.y) > 30 & random.nextBoolean()) {
            pivotCount += 2;
        }

        while (pivotCount > 0) {

        }



        return true;
    }


    private Point pickDoorOnPerimeter(Room from, Room to) {
        Point fromLoc = from.getLocation(), toLoc = to.getLocation();

        // Choose relatively closer side for each room as the destination. i.e. If a is at the Left Bottom of b, it
        // pick the door for a on the Top or Right and the door for b on Left or Bottom
        boolean fromOnLeft = fromLoc.getX() < toLoc.getX(), fromOnBottom = fromLoc.getY() < toLoc.getY();
        int doorX, doorY;
        if (fromOnLeft && fromOnBottom) { // From on Bottom Left
            boolean onFromTop = random.nextBoolean();
            if (onFromTop) {
                doorX = random.nextInt(from.getLeft() + 1, from.getRight() - 1);
                doorY = from.getTop();
            } else {
                doorY = random.nextInt(from.getBottom() + 1, from.getBottom() - 1);
                doorX = from.getRight();
            }
        } else if (!fromOnLeft && fromOnBottom) { // From on Bottom Right
            boolean onFromTop = random.nextBoolean();
            if (onFromTop) {
                doorX = random.nextInt(from.getLeft() + 1, from.getRight() - 1);
                doorY = from.getTop();
            } else {
                doorY = random.nextInt(from.getBottom() + 1, from.getBottom() - 1);
                doorX = from.getLeft();
            }
        } else if (!fromOnLeft && !fromOnBottom) { // From on Top Right
            boolean onFromBottom = random.nextBoolean();
            if (onFromBottom) {
                doorX = random.nextInt(from.getLeft() + 1, from.getRight() - 1);
                doorY = from.getBottom();
            } else {
                doorY = random.nextInt(from.getBottom() + 1, from.getTop() - 1);
                doorX = from.getLeft();
            }
        } else { // From on Top Left
            boolean onFromBottom = random.nextBoolean();
            if (onFromBottom) {
                doorX = random.nextInt(from.getLeft(), from.getRight());
                doorY = from.getBottom();
            } else {
                doorY = random.nextInt(from.getBottom(), from.getBottom());
                doorX = from.getRight();
            }
        }

        return new Point(doorX, doorY);
    }

    /* Return the distance between given two point */
    private static double distancePoint(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }


    /* It will generate a pivot based on current position, direction, and destination position */
    private Point generatePivot(Point current, Direction direction, Point destination, int pivotCount) {
        if (pivotCount == 0) throw new RuntimeException("Invalid pivot count");

        final int minX = 1, maxX = world.length - 2;
        final int minY = 1, maxY = world[0].length - 2;

        switch (direction) {
            case UP:
                int yStart = clamp(current.y + Config.DOOR_BUFF, minY, maxY);
        }

    }


    private static int clamp(int v, int lo, int hi) {
        if (lo > hi) { int t = lo; lo = hi; hi = t; }
        return (v < lo) ? lo : Math.min(v, hi);
    }

}
