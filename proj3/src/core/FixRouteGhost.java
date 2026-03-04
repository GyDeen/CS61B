package core;

import edu.princeton.cs.algs4.StdDraw;
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
        setImagePath("resources/pac man/ghost/pink ghost/pink_ghost_");
    }

    public static FixRouteGhost generateFixRouteGhost(Random rand, TETile[][] world, GameObject closeTo) {
        Point p = findSpawnLocation(rand.nextInt(3, 5), rand, world, closeTo);
        if (p == null) return null;

        FixRouteGhost currentGhost = new FixRouteGhost(p.x, p.y, 1, 1, rand);
        currentGhost.generateRoute(rand, world, closeTo, p);
        return currentGhost;
    }


    private void generateRoute(Random rand, TETile[][] world, GameObject closeTo, Point currentPos) {
        int shape = rand.nextInt(4);
        int xDistance = currentPos.x - closeTo.getPosition().x, yDistance = currentPos.y - closeTo.getPosition().y;
        int radius = (int) Math.sqrt(Math.pow(currentPos.x - closeTo.getPosition().x, 2) +
                Math.pow(currentPos.y - closeTo.getPosition().y, 2));

        switch (shape) {
            case INF_SIGN: {
                route = new Point[4];
                route[0] = currentPos;

                // Diagonal Point
                route[1] = new Point(closeTo.getPosition().x - xDistance, closeTo.getPosition().y - yDistance);
                // Symmetry About the y-Axis
                route[2] = new Point(closeTo.getPosition().x - xDistance, currentPos.y);
                // Symmetry About the x-Axis
                route[3] = new Point(currentPos.x, closeTo.getPosition().y - yDistance);
                break;
            }
            case SQUARE: {
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
            }
            case CIRCLE: {
                route = new Point[8];
                for (int i = 0; i < 8; i++) {
                    double angle = 2 * Math.PI * i / 8;
                    int x = (int) Math.round(closeTo.getPosition().x + radius * Math.cos(angle));
                    int y = (int) Math.round(closeTo.getPosition().y + radius * Math.sin(angle));

                    // Ensure the vertex is passable and within room bounds
                    if (validPos(x, y, 1, 1, world)) {
                        route[i] = new Point(x, y);
                    } else {
                        // Fallback: use anchor if a vertex is inside a wall
                        route[i] = closeTo.getPosition();
                    }
                }
                break;
            }

            case STAR: {
                route = new Point[5];
                Point[] vertices = new Point[5];

                for (int i = 0; i < 5; i++) {
                    double angle = (Math.PI / 2) + (2 * Math.PI * i / 5);
                    int x = (int) Math.round(closeTo.getPosition().x + radius * Math.cos(angle));
                    int y = (int) Math.round(closeTo.getPosition().y + radius * Math.sin(angle));
                    vertices[i] = new Point(x, y);
                }

                route[0] = vertices[0];
                route[1] = vertices[2];
                route[2] = vertices[4];
                route[3] = vertices[1];
                route[4] = vertices[3];

                break;
            }

            default:
                throw new RuntimeException("Invalid shape " + shape);
        }
    }

    @Override
    /** Only change facing between LEFT and RIGHT*/
    public void draw() {
        if (getDirection() == Direction.LEFT) {
            StdDraw.picture(getPosition().x + 0.5, getPosition().y + 0.5, getImagePath() + "left.png", 1, 1);
        } else {
            StdDraw.picture(getPosition().x + 0.5, getPosition().y + 0.5, getImagePath() + "right.png", 1, 1);
        }
    }


    public void update(TETile[][] world, long worldTime) {
        if (moveToward(route[currentIdx], world, worldTime)) currentIdx = (currentIdx + 1) % route.length;

        draw();
    }


}
