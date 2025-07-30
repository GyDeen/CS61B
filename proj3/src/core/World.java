package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class World {
    private static int WINDOW_HEIGHT = 1000;
    private static int WINDOW_WIDTH = 1000;
    private static long seed = 726;
    private static final Random RANDOM = new Random(seed);

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
}



