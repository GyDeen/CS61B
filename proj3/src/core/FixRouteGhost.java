package core;

import tileengine.TETile;

import java.awt.*;
import java.util.Random;

public class FixRouteGhost extends Ghost{
    private static int GAURD = 1;
    private static int ROUGUE = 2;


    private Point[] route;
    private int currentIdx = 0;

    private FixRouteGhost(int x, int y, int width, int height, Random rand) {
        super(x, y, width, height, rand);
        setImagePath("resources/pac man/ghost/pink ghost");
    }

    public static FixRouteGhost generateFixRouteGhost(Random rand, TETile[][] world, GameObject closeTo) {
        Point p = findSpawnLocation(rand.nextInt(3, 5), 1, rand, world, closeTo);
        if (p == null) return null;

    }


    public Point[] generateRoute(Random rand, TETile[][] world) {
        if (rand.nextInt(1, 3) == GAURD) {
            return generateGuardRoute();
        }
    }


    private Point[] generateGuardRoute() {

    }
}
