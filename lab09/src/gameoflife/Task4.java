package gameoflife;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.io.BufferedReader;


public class Task4 {
    private static final int WIDTH = 30;
    private static final int HEIGHT = 20;

    private static long seed = 726;
    private Random rand = new Random(seed);

    private static int savedIndex = 0;

    private LinkedList<SquareInfo> squareInfos = new LinkedList<>();
    private LinkedList<Character> inputChars = new LinkedList<>();

    private class SquareInfo {
        private int startingX;
        private int startingY;
        private int size;
        private boolean removed;

        public SquareInfo(int startX, int startY, int squareSize) {
            startingX = startX;
            startingY = startY;
            size = squareSize;
            removed = false;
        }

    }

    private static void fillWithTrees(TETile[][] world) {
        for (int y = 0; y < world.length; y++) {
            for (int x = 0; x < world[0].length; x++) {
                world[y][x] = Tileset.TREE;
            }
        }
    }

    private void drawSquare(TETile[][] world, int startX, int startY, int size, TETile tile) {
        squareInfos.add(new SquareInfo(startX, startY, size));
        inputChars.add('n');
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

    private void deleteLastSquare(TETile[][] world) {
        while (!squareInfos.isEmpty()) {
            SquareInfo sq = squareInfos.removeLast();
            if (!sq.removed) {
                sq.removed = true;
                inputChars.add('d');
                for (int x = sq.startingX; x < sq.startingX + sq.size; x++) {
                    for (int y = sq.startingY; y > sq.startingY - sq.size; y--) {
                        try {
                            world[x][y] = Tileset.TREE;
                        } catch (IndexOutOfBoundsException e) {
                            // Skip out-of-bounds
                        }
                    }
                }
                break;
            }
        }
    }


    private void addRandomSquare(TETile[][] world, Random rand) {
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

    public void load(TETile[][] world) {
        int currentIdx = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("save.txt"))) {
            long seed = Long.parseLong(reader.readLine());
            rand = new Random(seed);

            String[] commands = reader.readLine().split("\t");
            for (String ch : commands) {

                switch (ch) {
                    case "n" -> addRandomSquare(world, rand);
                    case "d" -> deleteLastSquare(world);
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading from file: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        Task4 task = new Task4();

        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][15];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < 15; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        fillWithTrees(world);

        ter.renderFrame(world);

        int squareNum = 0;
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.textLeft(1, 17, "Number of Squares: " + squareNum);
        StdDraw.show();

        Queue<Character> queue = new LinkedList<>();
        boolean done = false;
        while (!done) {
            while (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                queue.add(Character.toLowerCase(c));

                switch (c) {
                    case 'n', 'N':
                        task.addRandomSquare(world, task.rand);
                        squareNum++;
                        break;
                    case 'q', 'Q':
                        System.exit(0);
                        break;
                    case 'd', 'D':
                        task.deleteLastSquare(world);
                        if (squareNum > 0) squareNum--;
                        break;
                    case's', 'S': // Save the rand seed and input for loading
                        try {
                            savedIndex = task.inputChars.size();
                            FileWriter fw = new FileWriter("save.txt");
                            fw.write(seed + "\n");
                            for (char ch : task.inputChars) {
                                fw.write(ch + "\t");
                            }

                            fw.close();
                        } catch (IOException e) {
                            System.out.println("No such file");
                            throw new RuntimeException(e);
                        }

                        break;
                    case 'l', 'L':
                        task.load(world);
                    default:
                        break;
                }

                ter.renderFrame(world);
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.textLeft(1, 17, "Number of Squares: " + squareNum);
                StdDraw.show();
            }
        }
    }
}
