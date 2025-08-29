package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static tileengine.Tileset.FLOOR;

public class HallwayCarver {
    private static TETile[][] world;
    private static boolean[][] floor;
    private static boolean[][] wall;
    ArrayList<Point> door;

    public HallwayCarver(TETile[][] world) {
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


    public boolean connect(Room a, Room b, boolean placeDoors) {
        return connect(a, null, b, null, placeDoors);
    }

    public boolean connect(Room a, Point doorA, Room b, Point doorB, boolean placeDoors) {
        Point drA = (doorA != null) ? doorA : pickDoorOnPerimeter(a, roomCenter(b));
        Point drb = (doorB != null) ? doorB : pickDoorOnPerimeter(b, roomCenter(a));
    }


}
