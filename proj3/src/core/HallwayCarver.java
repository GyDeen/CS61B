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
        world[x][y] = Tileset.FLOOR;
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
        Point drb = (doorB != null) ? doorB : pickDoorOnPerimeter(b, a);
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
                doorX = random.nextInt(from.getLeft(), from.getRight());
                doorY = from.getTop();
            } else {
                doorY = random.nextInt(from.getBottom(), from.getBottom());
                doorX = from.getRight();
            }
        } else if (!fromOnLeft && fromOnBottom) { // From on Bottom Right
            boolean onFromTop = random.nextBoolean();
            if (onFromTop) {
                doorX = random.nextInt(from.getLeft(), from.getRight());
                doorY = from.getTop();
            } else {
                doorY = random.nextInt(from.getBottom(), from.getBottom());
                doorX = from.getLeft();
            }
        } else if (!fromOnLeft && !fromOnBottom) { // From on Top Right
            boolean onFromBottom = random.nextBoolean();
            if (onFromBottom) {
                doorX = random.nextInt(from.getLeft(), from.getRight());
                doorY = from.getBottom();
            } else {
                doorY = random.nextInt(from.getBottom(), from.getTop());
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


}
