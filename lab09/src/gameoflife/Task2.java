package gameoflife;

import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.Random;

public class Task2 {
    private static final int WIDTH = 30;
    private static final int HEIGHT = 20;

    private static void fillWithTrees(TETile[][] world) {
        for (int y = 0; y < world.length; y++) {
            for (int x = 0; x < world[0].length; x++) {
                world[y][x] = Tileset.TREE;
            }
        }
    }

    private static void drawSquare(TETile[][] world, int startX, int startY, int size, TETile tile) {
        for (int x = startX; x < startX + size; x++) {
            for (int y = startY; y > startY - size; y--) {
                try {
                    world[x][y] = tile;
                } catch (IndexOutOfBoundsException e) {
                    // Do nothing just skip the spot that out of bound
                }

            }
        }
    }

    private static void addRandomSquare(TETile[][] world, Random rand) {
        int size = rand.nextInt(5) + 3;
        int startX = rand.nextInt(WIDTH);
        int startY = rand.nextInt(size, 15);
        int tile = rand.nextInt(3);

        switch (tile) {
            case 0: drawSquare(world, startX, startY, size, Tileset.WALL); break;
            case 1: drawSquare(world, startX, startY, size, Tileset.FLOWER); break;
            case 2: drawSquare(world, startX, startY, size, Tileset.WATER); break;
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
        Random rand = new Random(20040726);
        for (int i = 0; i < 5; i++) {addRandomSquare(world, rand);}

        // draws the world to the screen
        ter.renderFrame(world);
    }
}
