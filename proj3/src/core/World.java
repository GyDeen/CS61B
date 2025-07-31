package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.Random;



public class World {
    private static final int WINDOW_HEIGHT = 1000;
    private static final int WINDOW_WIDTH = 1000;
    private static final int WORLD_HEIGHT = WINDOW_HEIGHT - UI.BOTTOM_UI - UI.TOP_UI;

    private static long seed = 726;
    private static final Random RANDOM = new Random(seed);
    public final TETile[][] world = new TETile[WINDOW_WIDTH][WORLD_HEIGHT];


    public World() {}


    /** Generate a world that based on input seed
     * @param seed seed for random generator
     */
    public World(long seed) {
        this.seed = seed;
    }


    /** Generate random room for the world
     * @param world The world that the room will be in
     */
    public void generateRoom(TETile[][] world) {

    }


    /** */
    public void renderWorld() {
        TERenderer ter = new TERenderer();
        ter.initialize(WINDOW_WIDTH, WINDOW_HEIGHT);

        for (int x = 0; x < WINDOW_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }

    }
}



