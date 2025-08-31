package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static core.Config.*;
import static tileengine.Tileset.FLOOR;

public class HallwayCarver {
    private static TETile[][] world;
    private static boolean[][] floor;
    private static boolean[][] wall;
    ArrayList<Point> door;
    Random random;

    public HallwayCarver(TETile[][] world, Random rand) {
        random = rand;

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
        world[x][y] = FLOOR;
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


    /** Connect two room without given Door*/
    public boolean connect(Room a, Room b, boolean placeDoors) {
        return connect(a, null, b, null, placeDoors);
    }

    /** Connect two room with given Door */
    public boolean connect(Room a, Point doorA, Room b, Point doorB, boolean placeDoors) {
        Point drA = (doorA != null) ? doorA : pickDoorOnPerimeter(a, b);
        Point drB = (doorB != null) ? doorB : pickDoorOnPerimeter(b, a);
        Direction direc = null;

        // Door on the horizontal
        if (drA.x == a.getLeft()) {
            direc = Direction.LEFT;
        } else if (drA.x == a.getRight()) {
            direc = Direction.RIGHT;
        }

        // Door on vertical
        if (drA.y == a.getTop()) {
            direc = Direction.UP;
        } else  if (drA.y == a.getBottom()) {
            direc = Direction.DOWN;
        }

        // Find how many pivot we need. If it has no alignment for both doors, it needs 2. If it has either x or y align,
        // it needs 1. If both align, it needs 0
        int pivotCount = 0;
        if (drA.x == drB.x && drA.y == drB.y) {
            pivotCount = 2;
        } else if (drA.x == drB.x || drA.y == drB.y) {
            pivotCount = 1;
        }

        // Generate pivot position
        Point[] pivots = new Point[pivotCount + 2];
        if (HallwayCarver.distancePoint(drA.x, drA.y, drB.x, drB.y) > 30 & random.nextBoolean()) {
            pivotCount += 2;
        }

        int i = 0;
        while (pivotCount > 0) {
            pivots[i] = pickDoorOnPerimeter(a, b);
            pivotCount--;
        }



        return true;
    }


    private Point pickDoorOnPerimeter(Room from, Room to) {
        Point fromLoc = from.getLocation(), toLoc = to.getLocation();

        // Choose relatively closer side for each room as the destination. i.e. If a is at the Left Bottom of b, it
        // pick the door for a on the Top or Right and the door for b on Left or Bottom
        boolean fromOnLeft = fromLoc.getX() < toLoc.getX(), fromOnBottom = fromLoc.getY() < toLoc.getY();
        int doorX, doorY;
        if (fromOnLeft && fromOnBottom) { // From on Bottom Left
            boolean onFromTop = random.nextBoolean();
            if (onFromTop) {
                doorX = random.nextInt(from.getLeft() + 1, from.getRight() - 1);
                doorY = from.getTop();
            } else {
                doorY = random.nextInt(from.getBottom() + 1, from.getBottom() - 1);
                doorX = from.getRight();
            }
        } else if (!fromOnLeft && fromOnBottom) { // From on Bottom Right
            boolean onFromTop = random.nextBoolean();
            if (onFromTop) {
                doorX = random.nextInt(from.getLeft() + 1, from.getRight() - 1);
                doorY = from.getTop();
            } else {
                doorY = random.nextInt(from.getBottom() + 1, from.getBottom() - 1);
                doorX = from.getLeft();
            }
        } else if (!fromOnLeft && !fromOnBottom) { // From on Top Right
            boolean onFromBottom = random.nextBoolean();
            if (onFromBottom) {
                doorX = random.nextInt(from.getLeft() + 1, from.getRight() - 1);
                doorY = from.getBottom();
            } else {
                doorY = random.nextInt(from.getBottom() + 1, from.getTop() - 1);
                doorX = from.getLeft();
            }
        } else { // From on Top Left
            boolean onFromBottom = random.nextBoolean();
            if (onFromBottom) {
                doorX = random.nextInt(from.getLeft(), from.getRight());
                doorY = from.getBottom();
            } else {
                doorY = random.nextInt(from.getBottom(), from.getBottom());
                doorX = from.getRight();
            }
        }

        return new Point(doorX, doorY);
    }

    /* Return the distance between given two point */
    private static double distancePoint(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private Point generatePivot(Point current, Direction direction, Point destination, int pivotCount) {
        if (pivotCount <= 0) throw new IllegalArgumentException("Invalid pivot count");

        final int minX = 1, maxX = world.length - 2;
        final int minY = 1, maxY = world[0].length - 2;

        switch (direction) {
            case UP: {
                // Pivot will be (current.x, py) with py > current.y
                int yStart = Math.min(current.y + DOOR_BUFF, maxY);

                if (pivotCount == 1) {
                    if (destination.y <= current.y)
                        throw new IllegalStateException("Final UP pivot impossible: dest.y is not ahead");
                    int py = Math.min(destination.y, maxY);
                    return new Point(current.x, py);
                } else {
                    int yEnd;
                    if (destination.y > current.y) {
                        // head toward dest, but don't go past
                        int target = destination.y - FUTURE_BUFFER + MAX_OVERSHOOT;
                        yEnd = Math.min(target, maxY);
                    } else {
                        // dest is behind or equal in y
                        yEnd = Math.min(current.y + SMALL_ADVANCE, maxY);
                    }
                    if (yStart > yEnd) return new Point(current.x, Math.min(current.y + 1, maxY));
                    int py = (yStart == yEnd) ? yStart : random.nextInt(yStart, yEnd + 1);
                    return new Point(current.x, py);
                }
            }

            case DOWN: {
                // Pivot will be (current.x, py) with py < current.y
                int yStart = Math.max(current.y - DOOR_BUFF, minY);

                if (pivotCount == 1) {
                    if (destination.y >= current.y)
                        throw new IllegalStateException("Final DOWN pivot impossible: dest.y is not reachable");
                    int py = Math.max(destination.y, minY);
                    return new Point(current.x, py);
                } else {
                    int yEnd;
                    if (destination.y < current.y) {
                        int target = destination.y + FUTURE_BUFFER - MAX_OVERSHOOT;
                        yEnd = Math.max(target, minY);
                    } else {
                        yEnd = Math.max(current.y - SMALL_ADVANCE, minY);
                    }
                    // yEnd <= yStart for DOWN
                    if (yEnd > yStart) return new Point(current.x, Math.max(current.y - 1, minY));
                    int py = (yEnd == yStart) ? yStart : random.nextInt(yEnd, yStart + 1);
                    if (py >= current.y) py = current.y - 1;    // ensure we actually move down
                    return new Point(current.x, Math.max(py, minY));
                }
            }

            case RIGHT: {
                // Pivot will be (px, current.y) with px > current.x
                int xStart = Math.min(current.x + DOOR_BUFF, maxX);

                if (pivotCount == 1) {
                    if (destination.x <= current.x)
                        throw new IllegalStateException("Final RIGHT pivot impossible: dest.x is not reachable");
                    int px = Math.min(destination.x, maxX);
                    return new Point(px, current.y);
                } else {
                    int xEnd;
                    if (destination.x > current.x) {
                        int target = destination.x - FUTURE_BUFFER + MAX_OVERSHOOT;
                        xEnd = Math.min(target, maxX);
                    } else {
                        xEnd = Math.min(current.x + SMALL_ADVANCE, maxX);
                    }
                    if (xStart > xEnd) return new Point(Math.min(current.x + 1, maxX), current.y);
                    int px = (xStart == xEnd) ? xStart : random.nextInt(xStart, xEnd + 1);
                    return new Point(px, current.y);
                }
            }

            case LEFT: {
                // Pivot will be (px, current.y) with px < current.x
                int xStart = Math.max(current.x - DOOR_BUFF, minX);

                if (pivotCount == 1) {
                    if (destination.x >= current.x)
                        throw new IllegalStateException("Final LEFT pivot impossible: dest.x is not reachable");
                    int px = Math.max(destination.x, minX);
                    return new Point(px, current.y);
                } else {
                    int xEnd;
                    if (destination.x < current.x) {
                        int target = destination.x + FUTURE_BUFFER - MAX_OVERSHOOT;
                        xEnd = Math.max(target, minX);
                    } else {
                        xEnd = Math.max(current.x - SMALL_ADVANCE, minX);
                    }
                    // xEnd <= xStart for LEFT;
                    if (xEnd > xStart) return new Point(Math.max(current.x - 1, minX), current.y);
                    int px = (xEnd == xStart) ? xStart : random.nextInt(xEnd, xStart + 1);
                    if (px >= current.x) px = current.x - 1;
                    return new Point(Math.max(px, minX), current.y);
                }
            }

            default:
                throw new IllegalStateException("Unknown direction: " + direction);
        }
    }


    /* Fit v into range of lo and hi */
    private static int clamp(int v, int lo, int hi) {
        if (lo > hi) { int t = lo; lo = hi; hi = t; }
        return (v < lo) ? lo : Math.min(v, hi);
    }

    /* Return true if given direction on Horizontal */
    private boolean isHorizontal(Direction d) {
        return d == Direction.LEFT || d == Direction.RIGHT;
    }


    /* Find next Direction for current carver */
    private Direction nextDirection(Direction currentDir, Point atPivot, Point dest, int remainingPivots) {
        if (!isHorizontal(currentDir)) { // was UP/DOWN, go horizontal now
            if (dest.x > atPivot.x) return Direction.RIGHT;
            if (dest.x < atPivot.x) return Direction.LEFT;
            // dest.x == pivot.x: perfectly aligned on x
            if (remainingPivots > 0) {
                // pick the side with more space
                int rightSpace = (world.length - 2) - atPivot.x;
                int leftSpace  = atPivot.x - 1;
                if (rightSpace == leftSpace) {
                    return random.nextBoolean() ? Direction.RIGHT : Direction.LEFT;
                }
                return (rightSpace > leftSpace) ? Direction.RIGHT : Direction.LEFT;
            } else {
                // final leg will be vertical anyway
                return Direction.RIGHT;
            }
        } else { // was LEFT/RIGHT, go vertical now
            if (dest.y > atPivot.y) return Direction.UP;
            if (dest.y < atPivot.y) return Direction.DOWN;
            if (remainingPivots > 0) {
                int upSpace   = (world[0].length - 2) - atPivot.y;
                int downSpace = atPivot.y - 1;
                if (upSpace == downSpace) {
                    return random.nextBoolean() ? Direction.UP : Direction.DOWN;
                }
                return (upSpace > downSpace) ? Direction.UP : Direction.DOWN;
            } else {
                return Direction.UP;
            }
        }
    }

}
