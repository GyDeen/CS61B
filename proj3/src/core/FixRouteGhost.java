package core;

import tileengine.TETile;

import java.awt.*;
import java.util.Random;

public class FixRouteGhost extends Ghost{
    private static final int INF_SIGN = 0;
    private static final int SQUARE = 1;
    private static final int CIRCLE = 2;
    private static final int STAR = 3;



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


    private void generateRoute(Random rand, TETile[][] world, GameObject closeTo, Point currentPos) {
        int shape = rand.nextInt(4);
        int xDistance = currentPos.x - closeTo.getPosition().x, yDistance = currentPos.y - closeTo.getPosition().y;

        switch (shape) {
            case INF_SIGN:
                route = new Point[4];
                route[0] = currentPos;

                // Diagonal Point
                route[1] = new Point(closeTo.getPosition().x - xDistance, closeTo.getPosition().y - yDistance);
                // Symmetry About the y-Axis
                route[2] = new Point(closeTo.getPosition().x - xDistance, currentPos.y);
                // Symmetry About the x-Axis
                route[3] = new Point(currentPos.x, closeTo.getPosition().y - yDistance);
                break;
            case SQUARE:
                route = new Point[4];
                route[0] = currentPos;
                boolean horizontalFirst = rand.nextBoolean();
                if (!horizontalFirst) {
                    route[1] = new Point(currentPos.x, closeTo.getPosition().y - yDistance);
                    route[2] = new Point(closeTo.getPosition().x - xDistance, closeTo.getPosition().y - yDistance);
                    route[3] = new Point(closeTo.getPosition().x - xDistance, currentPos.y);
                } else {
                    route[1] = new Point(closeTo.getPosition().x - xDistance, currentPos.y);
                    route[2] = new Point(closeTo.getPosition().x - xDistance, closeTo.getPosition().y - yDistance);
                    route[3] = new Point(currentPos.x, closeTo.getPosition().y - yDistance);
                }
                break;
            case CIRCLE:
                   int radius = (int) Math.sqrt(Math.pow(currentPos.x - closeTo.getPosition().x ,2) + Math.pow(currentPos.y - closeTo.getPosition().y, 2));
                for (int i = 0; i < 8; i++) {
                    double angle = 2 * Math.PI * i / 8;
                    int x = (int) Math.round(closeTo.getPosition().x + radius * Math.cos(angle));
                    int y = (int) Math.round(closeTo.getPosition().y + radius * Math.sin(angle));

                    // Ensure the vertex is passable and within room bounds
                    if (validPos(x, y, 1, world)) {
                        route[i] = new Point(x, y);
                    } else {
                        // Fallback: use anchor if a vertex is inside a wall
                        route[i] = closeTo.getPosition();
                    }
                }
        }
    }


}
