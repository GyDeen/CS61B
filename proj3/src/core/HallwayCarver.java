package core;

import tileengine.TETile;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class HallwayCarver {
    private static TETile[][] world;
    private static final boolean[][] floor;
    private static final boolean[][] wall;
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
                if (world[x][y].isPassable())
            }
        }
    }
}
