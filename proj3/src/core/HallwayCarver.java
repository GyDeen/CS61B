package core;

import tileengine.TETile;
import tileengine.TileType;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static core.Config.*;

public class HallwayCarver {
    private static TETile[][] world;
    private static boolean[][] floor;
    private static boolean[][] wall;
    ArrayList<Point> door = new ArrayList<>();
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
        floor[x][y] = true;
        wall[x][y]  = false;
    }


    private void setWall(int x, int y) {
        wall[x][y]  = true;
        floor[x][y] = false;
    }

    private void setDoor(int x, int y) {
        floor[x][y] = true;
        wall[x][y]  = false;
        door.add(new Point(x, y));
    }

    private boolean inBounds(int x, int y) {
        return 0 <= x && x < world.length && 0 <= y && y < world[0].length;
    }

    public TETile[][] getWorld() { return world; }


    /** Connect two room without given Door*/
    public boolean connect(MainRoom a, MainRoom b) {
        return connect(a, null, b, null);
    }

    /** Connect two room with given Door */
    public boolean connect(MainRoom a, Point doorA, MainRoom b, Point doorB) {
        Point drA = (doorA != null) ? doorA : pickDoorOnPerimeter(a, b);
        Point drB = (doorB != null) ? doorB : pickDoorOnPerimeter(b, a);
        Direction direc = null;

        // Door on the horizontal
        if (drA.x == a.getEdgeOn(drA.y, Direction.LEFT)) {
            direc = Direction.LEFT;
        } else if (drA.x == a.getEdgeOn(drA.y, Direction.RIGHT)) {
            direc = Direction.RIGHT;
        }

        // Door on vertical
        if (drA.y == a.getEdgeOn(drA.x, Direction.UP)) {
            direc = Direction.UP;
        } else if (drA.y == a.getEdgeOn(drA.x, Direction.DOWN)) {
            direc = Direction.DOWN;
        }

        // Find how many pivot we need. If it has no alignment for both doors, it needs 2. If it has either x or y align,
        // it needs 1. If both align, it needs 0
        int pivotCount;
        if (drA.x == drB.x && drA.y == drB.y) pivotCount = 0;
        else if (drA.x == drB.x || drA.y == drB.y) pivotCount = 1;
        else pivotCount = 2;

        // Randomly add more pivot for long distance hallway
        if (HallwayCarver.distancePoint(drA.x, drA.y, drB.x, drB.y) > 30 & random.nextBoolean()) pivotCount += 2;

        ArrayList<Point> doors = new ArrayList<>();
        ArrayList<Point> floors = new ArrayList<>();

        Point currentPos = new Point(drA.x, drA.y);
        int attempts = 0;
        while (pivotCount > 0) {
            Point pivot = generatePivot(currentPos, direc, drB, pivotCount);
            boolean stageStartDoor = currentPos.equals(drA);
            // Try to allocate a pivot more than 50 times, this connection failed
            if (!allocateHallway(currentPos, pivot, floors, doors, stageStartDoor)) {
                if (++attempts > MAX_ATTEMPT_PIVOT) return false;
                continue;
            }

            pivotCount--;
            currentPos = pivot;
            direc = nextDirection(direc, currentPos, drB, pivotCount);
        }

        ArrayList<Point> walls = collectWalls(floors, doors);

        // Commit staged changes to world and masks
        for (Point p : floors) {
            world[p.x][p.y] = tileengine.Tileset.FLOOR;
            setFloor(p.x, p.y);
        }
        for (Point p : doors) {
            world[p.x][p.y] = tileengine.Tileset.UNLOCKED_DOOR;
            setDoor(p.x, p.y);
        }

        assert walls != null;
        for (Point p: walls) {
            world[p.x][p.y] = tileengine.Tileset.WALL;
            setWall(p.x, p.y);
        }

        return true;
    }


    /* Generte all the wall tiles that we need to allocate */
    private ArrayList<Point> collectWalls(ArrayList<Point> floors, ArrayList<Point> doors) {
        int[] dx4 = {1,-1,0,0}, dy4 = {0,0,1,-1};
        ArrayList<Point> walls = new ArrayList<>();

        // Treat the final corridor set as passable
        for (Point c : floors) {
            for (int k = 0; k < 4; k++) {
                int wx = c.x + dx4[k], wy = c.y + dy4[k];

                // neighbor already corridor? skip
                if (contains(floors, wx, wy) || contains(doors, wx, wy)) continue;

                // leak guard (should already be prevented; keep as sanity check)
                if (!inBounds(wx, wy)) return null;

                TileType t = typeAt(wx, wy);
                // Never overwrite passables / locked doors
                if (t.isPassable() || t == TileType.LOCKED_DOOR) continue;

                // Fill NOTHING or overwrite existing WALL
                if (!contains(walls, wx, wy)) walls.add(new Point(wx, wy));
            }
        }
        for (Point c : doors) {
            for (int k = 0; k < 4; k++) {
                int wx = c.x + dx4[k], wy = c.y + dy4[k];
                if (contains(floors, wx, wy) || contains(doors, wx, wy)) continue;
                if (!inBounds(wx, wy)) return null;
                TileType t = typeAt(wx, wy);
                if (t.isPassable() || t == TileType.LOCKED_DOOR) continue;
                if (!contains(walls, wx, wy)) walls.add(new Point(wx, wy));
            }
        }
        return walls;
    }

    private Point pickDoorOnPerimeter(MainRoom from, MainRoom to) {
        Point fromLoc = from.getLocation(), toLoc = to.getLocation();

        // Choose relatively closer side for each room as the destination. i.e. If a is at the Left Bottom of b, it
        // pick the door for a on the Top or Right and the door for b on Left or Bottom
        // The door won't at the corner of the room
        boolean fromOnLeft = fromLoc.getX() < toLoc.getX(), fromOnBottom = fromLoc.getY() < toLoc.getY();
        int doorX, doorY;
        if (fromOnLeft && fromOnBottom) { // From on Bottom Left
            boolean onFromTop = random.nextBoolean();
            if (onFromTop) {
                // + - 1 to avoid placing door on corner
                doorX = random.nextInt(from.getLeft() + 1, from.getRight() - 1);
                doorY = from.getEdgeOn(doorX, Direction.UP);
            } else {
                doorY = random.nextInt(from.getBottom() + 1, from.getTop() - 1);
                doorX = from.getEdgeOn(doorY, Direction.RIGHT);
            }
        } else if (!fromOnLeft && fromOnBottom) { // From on Bottom Right
            boolean onFromTop = random.nextBoolean();
            if (onFromTop) {
                doorX = random.nextInt(from.getLeft() + 1, from.getRight() - 1);
                doorY = from.getEdgeOn(doorX, Direction.UP);
            } else {
                doorY = random.nextInt(from.getBottom() + 1, from.getTop() - 1);
                doorX = from.getEdgeOn(doorY, Direction.LEFT);
            }
        } else if (!fromOnLeft && !fromOnBottom) { // From on Top Right
            boolean onFromBottom = random.nextBoolean();
            if (onFromBottom) {
                doorX = random.nextInt(from.getLeft() + 1, from.getRight() - 1);
                doorY = from.getEdgeOn(doorX, Direction.DOWN);
            } else {
                doorY = random.nextInt(from.getBottom() + 1, from.getTop() - 1);
                doorX = from.getEdgeOn(doorY, Direction.LEFT);
            }
        } else { // From on Top Left
            boolean onFromBottom = random.nextBoolean();
            if (onFromBottom) {
                doorX = random.nextInt(from.getLeft() + 1, from.getRight() - 1);
                doorY = from.getEdgeOn(doorX, Direction.DOWN);
            } else {
                doorY = random.nextInt(from.getBottom() + 1, from.getTop() - 1);
                doorX = from.getEdgeOn(doorY, Direction.RIGHT);
            }
        }

        return new Point(doorX, doorY);
    }

    /* Return the distance between given two point */
    private static double distancePoint(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }


    /* It will generate a pivot based on given direction, current position, and destination */
    private Point generatePivot(Point current, Direction direction, Point destination, int pivotCount) {
        if (pivotCount <= 0) throw new IllegalArgumentException("Invalid pivot count");

        final int minX = 1, maxX = world.length - 2;
        final int minY = 1, maxY = world[0].length - 2;

        switch (direction) {
            case UP: {
                // Pivot will be (current.x, py) with py > current.y
                int yStart = Math.min(current.y + DOOR_BUFF, maxY);

                if (pivotCount == 1) {
                    int py = clamp(destination.y, minY, maxY);
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
                    int py = clamp(destination.y, minY, maxY);
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
                    int px = clamp(destination.x, minX, maxX);
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
                    int px = clamp(destination.x, minX, maxX);
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
            // perfectly aligned on x
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


    private static boolean contains(ArrayList<Point> pts, int x, int y) {
        for (Point p : pts) if (p.x == x && p.y == y) return true;
        return false;
    }

    /* Allocate a straight segment currentPos to nextPivot.
     * - If out of bounds, or > MAX_WALL_IN_A_ROW walls in a row: return false
     * - If allocating wall around the hallway will out of bounds: return false
     * - NOTHING: stage floor
     * - PASSABLE: walk over
     * - WALL: stage door, keep counting; if run exceeds limit return false.
     * On success, update floor[][]/wall[][]. */
    private boolean allocateHallway(Point currentPos, Point nextPivot, ArrayList<Point> floors, ArrayList<Point> doors, boolean stageStartDoor) {
        if (!(currentPos.x == nextPivot.x || currentPos.y == nextPivot.y)) {
            throw new IllegalArgumentException("Invalid pivot (must be axis-aligned)");
        }

        int dx = Integer.compare(nextPivot.x, currentPos.x);
        int dy = Integer.compare(nextPivot.y, currentPos.y);

        // Stage change not commit to the world
        ArrayList<Point> stageFloors = new ArrayList<>();
        ArrayList<Point> stageDoors  = new ArrayList<>();

        // Current point is the starting point for current connection
        if (stageStartDoor) {
            TileType tile = TileType.toType(world[currentPos.x][currentPos.y]);
            if (!tile.isPassable() && tile != TileType.NOTHING) {
                stageDoors.add(new Point(currentPos));
            }
        }

        // Already on the pivot point
        if (dx == 0 && dy == 0) {
            // If current hallway floor/door plan will lead to leak, fail fast
            if (collectWalls(floors, doors) == null) return false;
            floors.addAll(stageFloors);
            doors.addAll(stageDoors);
            return true;
        }



        int x = currentPos.x, y = currentPos.y;
        int wallRun = 0;
        while (x != nextPivot.x || y != nextPivot.y) {
            int nx = x + dx, ny = y + dy;
            if (!inBounds(nx, ny)) return false;

            // Treat already-staged cells as passable while planning
            boolean stagedPassable =
                    floors.contains(new Point(nx, ny)) ||
                            doors.contains(new Point(nx, ny))  ||
                            stageFloors.contains(new Point(nx, ny)) ||
                            stageDoors.contains(new Point(nx, ny));

            TileType nextType = stagedPassable ? TileType.FLOOR : TileType.toType(world[nx][ny]);

            if (nextType == TileType.NOTHING) {
                stageFloors.add(new Point(nx, ny));
                wallRun = 0;
            } else if (nextType.isPassable()) {
                wallRun = 0;
            } else {
                stageDoors.add(new Point(nx, ny));
                if (++wallRun > MAX_WALL_IN_A_ROW) return false;
            }

            x = nx; y = ny;
        }

        ArrayList<Point> tmpFloors = new ArrayList<>(floors);
        tmpFloors.addAll(stageFloors);
        ArrayList<Point> tmpDoors  = new ArrayList<>(doors);
        tmpDoors.addAll(stageDoors);

        // This pivot allocation result in leak, fail fast
        if (collectWalls(tmpFloors, tmpDoors) == null) return false;

        // Only add to input floors or doors when it is a valid pivot
        floors.addAll(stageFloors);
        doors.addAll(stageDoors);
        return true;
    }

    /* Get the type of Tile at given position */
    private TileType typeAt(int x, int y) { return TileType.toType(world[x][y]); }
}
