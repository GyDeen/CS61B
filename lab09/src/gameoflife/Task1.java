package gameoflife;

import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

public class Task1 {
    private static final int WIDTH = 30;
    private static final int HEIGHT = 20;

    public static void fillWithTrees(TETile[][] world) {
        for (int y = 0; y < world.length; y++) {
            for (int x = 0; x < world[0].length; x++) {
                world[y][x] = Tileset.TREE;
            }
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][15];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < 15; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        fillWithTrees(world);

        // draws the world to the screen
        ter.renderFrame(world);
    }
}
