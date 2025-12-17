package core;

import tileengine.TETile;
import tileengine.TileType;
import tileengine.Tileset;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static core.Config.*;

public class HallwayCarver {

    private static final class DoorPair {
        private final Point drA, drB;
        private final Direction outA, outB;
        DoorPair(Point a, Point b, Direction da, Direction db) {
            drA = a; drB = b; outA = da; outB = db;
        }
    }

    private class WallPoint extends Point {
        private TileType tile;
        private WallPoint(int x, int y, TileType tile) {
            super(x, y);
            this.tile = tile;
        }
    }


    /* Store the information for allocating two rooms connection */
    private static final class ConnectionPlan {
        private final MainRoom a, b;
        private final DoorPair dp;
        private final ArrayList<Point> floors = new ArrayList<>();
        private final ArrayList<Point> doors  = new ArrayList<>();
        private ArrayList<WallPoint> walls;

        private ConnectionPlan(MainRoom a, MainRoom b, DoorPair doorP) {
            this.a = a; this.b = b;
            dp = doorP;
        }
    }


    private static TETile[][] world;
    Random random;
    private static MainRoom finalBoxRoom;

    public HallwayCarver(TETile[][] world, Random rand, MainRoom finalBoxRoom) {
        random = rand;

        HallwayCarver.world = new TETile[world.length][];
        for (int i = 0; i < world.length; i++) {
            HallwayCarver.world[i] = Arrays.copyOf(world[i], world[i].length);
        }

        this.finalBoxRoom = finalBoxRoom;
    }


    private boolean inBounds(int x, int y) {
        return 0 <= x && x < world.length && 0 <= y && y < world[0].length;
    }

    public TETile[][] getWorld() { return world; }



    /** Connecting given rooms with given type. If isLocked is true, it will place a locked door at the entry of room b */
    public boolean connect(MainRoom a, MainRoom b, boolean isLocked) {
        ConnectionPlan connectPlan = planConnection(a, b, isLocked);
        if (connectPlan == null) return false;


//        System.out.println("CONNECT " + a.getID() + "->" + b.getID()
//                + " floors=" + connectPlan.floors.size()
//                + " doors=" + connectPlan.doors.size());
        for (Point p : connectPlan.floors) { world[p.x][p.y] = Tileset.FLOOR;}
        for (Point p: connectPlan.walls)  {
            if (TileType.toType(world[p.x][p.y]).isPassable()) continue;
            world[p.x][p.y] = Tileset.WALL;
        }
        for (Point p : connectPlan.doors) {
            // If it is not final room's door or starting room's door, it is a door on pathway room, which should not
            // be blocked
            if (!p.equals(connectPlan.dp.drB)) {
                world[p.x][p.y] = Tileset.FLOOR;
                continue;
            }

            if (isLocked) world[p.x][p.y] = Tileset.LOCKED_DOOR;
            else world[p.x][p.y] = Tileset.UNLOCKED_DOOR;
        }

        return true;
    }

    /* Connect two room without given Door*/
    private ConnectionPlan planConnection(MainRoom a, MainRoom b, boolean isFinalRoomConnection) {
        return planConnection(a, null, b, null, isFinalRoomConnection);
    }

    /* Connect two room with given Door */
    private ConnectionPlan planConnection(MainRoom a, Point doorA, MainRoom b, Point doorB, boolean isFinalRoomConnection) {
        Direction direc = null;
        Point drA, drB;
        if (doorA == null && doorB == null) {
            DoorPair dp = pickDoorPairByEdges(a, b);
            drA = dp.drA; drB = dp.drB;
            direc = dp.outA;
        } else {
            drA = doorA == null ? pickDoorOnPerimeter(a, b) : doorA;
            drB = doorB == null ? pickDoorOnPerimeter(b, a) : doorB;
        }

        int t = a.getThicknessOfWall();
        int off = Math.max(0, t - 1);

        // Horizontal sides (x equals left/right edge, shifted inward by off)
        if (drA.x == a.getEdgeOn(drA.y, Direction.LEFT) + off)  direc = Direction.LEFT;
        else if (drA.x == a.getEdgeOn(drA.y, Direction.RIGHT) - off) direc = Direction.RIGHT;

        // Vertical sides (y equals up/down edge, shifted inward by off)
        else if (drA.y == a.getEdgeOn(drA.x, Direction.UP) - off) direc = Direction.UP;
        else if (drA.y == a.getEdgeOn(drA.x, Direction.DOWN) + off) direc = Direction.DOWN;

        // Find how many pivot we need. If it has no alignment for both doors, it needs 2. If it has either x or y align,
        // it needs 1. If both align, it needs 0
        int pivotCount;
        if (drA.x == drB.x && drA.y == drB.y) pivotCount = 0;
        else if (drA.x == drB.x || drA.y == drB.y) pivotCount = 1;
        else pivotCount = 2;


        ConnectionPlan plan = new ConnectionPlan(a, b, pickDoorPairByEdges(a, b));

        // Randomly add more pivot for long distance hallway
         if (Math.pow(drA.x, 2) + Math.pow(drA.y, 2) - (Math.pow(drB.x, 2) + Math.pow(drB.y, 2)) > 30 && random.nextBoolean()) pivotCount += 2;

//        System.out.println("drA: " + drA.toString());
//        System.out.println("drB: " + drB.toString());

        Point currentPos = new Point(drA.x, drA.y);
        int attempts = 0;
        while (pivotCount > 0) {
            Point pivot = generatePivot(currentPos, direc, drB, pivotCount, b);

            // Current position is the only possible allocation for next pivot and it is the last pivot
            // Just turn towards the door
            if (pivotCount == 1 && pivot.equals(currentPos)) {
                pivotCount--;
                direc = nextDirection(direc, currentPos, drB, pivotCount);
                continue;
            }

            // Not the last pivot and has no valid pivot allocation, fail fast
            if (pivot.equals(currentPos)) {
                if (++attempts > MAX_ATTEMPT_PIVOT) return null;
                continue;
            }


            boolean stageStartDoor = currentPos.equals(drA);
            // Try to allocate a pivot more than 50 times, this connection failed
            if (!allocateHallway(currentPos, pivot, plan.floors, plan.doors, stageStartDoor, b, isFinalRoomConnection)) {
                if (++attempts > MAX_ATTEMPT_PIVOT) return null;
                continue;
            }

            pivotCount--;
            currentPos = pivot;
            direc = nextDirection(direc, currentPos, drB, pivotCount);
        }

        if (!allocateHallway(currentPos, drB, plan.floors, plan.doors, false, b, isFinalRoomConnection)) return null;

        ArrayList<WallPoint> walls = collectWalls(plan.floors, plan.doors);
        if (walls == null) return null;

        plan.walls = walls;
        return plan;
    }


    /* Generate all the wall tiles that we need to allocate */
    private ArrayList<WallPoint> collectWalls(ArrayList<Point> floors, ArrayList<Point> doors) {
        int[] dx4 = {1,-1,0,0}, dy4 = {0,0,1,-1};
        ArrayList<WallPoint> walls = new ArrayList<>();

        // Treat the final corridor set as passable
        for (Point c : floors) {
            for (int k = 0; k < 4; k++) {
                int wx = c.x + dx4[k], wy = c.y + dy4[k];

                // neighbor already corridor? skip
                if (contains(floors, wx, wy) || contains(doors, wx, wy)) continue;

                // leak guard
                if (!inBounds(wx, wy)) return null;

                TileType t = typeAt(wx, wy);
                // Fill NOTHING with WALL; if it is not passable, remain the same type
                if (!t.isPassable() && !contains(walls, wx, wy)) {
                    if (t == TileType.NOTHING) walls.add(new WallPoint(wx, wy, TileType.WALL));
                    else walls.add(new WallPoint(wx, wy, t));
                }

                // Never overwrite passable / locked doors
                if (t.isPassable() || t == TileType.LOCKED_DOOR) continue;
            }
        }

        for (Point c : doors) {
            for (int k = 0; k < 4; k++) {
                int wx = c.x + dx4[k], wy = c.y + dy4[k];
                if (contains(floors, wx, wy) || contains(doors, wx, wy)) continue;
                if (!inBounds(wx, wy)) return null;
                TileType t = typeAt(wx, wy);
                if (t.isPassable() || t == TileType.LOCKED_DOOR) continue;
                if (!contains(walls, wx, wy)) {
                    if (t == TileType.NOTHING) walls.add(new WallPoint(wx, wy, TileType.WALL));
                    else walls.add(new WallPoint(wx, wy, t));
                }
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
                if (from.getThicknessOfWall() == 2) doorY = from.getEdgeOn(doorX, Direction.UP) - 1;
                else doorY = from.getEdgeOn(doorX, Direction.UP);
            } else {
                doorY = random.nextInt(from.getBottom() + 1, from.getTop() - 1);
                if (from.getThicknessOfWall() == 2) doorX = from.getEdgeOn(doorY, Direction.RIGHT) - 1;
                else doorX = from.getEdgeOn(doorY, Direction.RIGHT);
            }
        } else if (!fromOnLeft && fromOnBottom) { // From on Bottom Right
            boolean onFromTop = random.nextBoolean();
            if (onFromTop) {
                doorX = random.nextInt(from.getLeft() + 1, from.getRight() - 1);
                if (from.getThicknessOfWall() == 2) doorY = from.getEdgeOn(doorX, Direction.UP) - 1;
                else doorY = from.getEdgeOn(doorX, Direction.UP);
            } else {
                doorY = random.nextInt(from.getBottom() + 1, from.getTop() - 1);
                if (from.getThicknessOfWall() == 2) doorX = from.getEdgeOn(doorY, Direction.LEFT) + 1;
                else doorX = from.getEdgeOn(doorY, Direction.LEFT);
            }
        } else if (!fromOnLeft && !fromOnBottom) { // From on Top Right
            boolean onFromBottom = random.nextBoolean();
            if (onFromBottom) {
                doorX = random.nextInt(from.getLeft() + 1, from.getRight() - 1);
                if (from.getThicknessOfWall() == 2) doorY = from.getEdgeOn(doorX, Direction.DOWN) + 1;
                else doorY = from.getEdgeOn(doorX, Direction.DOWN);
            } else {
                doorY = random.nextInt(from.getBottom() + 1, from.getTop() - 1);
                if (from.getThicknessOfWall() == 2) doorX = from.getEdgeOn(doorY, Direction.LEFT) + 1;
                else doorX = from.getEdgeOn(doorY, Direction.LEFT);
            }
        } else { // From on Top Left
            boolean onFromBottom = random.nextBoolean();
            if (onFromBottom) {
                doorX = random.nextInt(from.getLeft() + 1, from.getRight() - 1);
                if (from.getThicknessOfWall() == 2) doorY = from.getEdgeOn(doorX, Direction.DOWN) + 1;
                else doorY = from.getEdgeOn(doorX, Direction.DOWN);
            } else {
                doorY = random.nextInt(from.getBottom() + 1, from.getTop() - 1);
                if (from.getThicknessOfWall() == 2) doorX = from.getEdgeOn(doorY, Direction.RIGHT) - 1;
                else doorX = from.getEdgeOn(doorY, Direction.RIGHT);
            }
        }

        return new Point(doorX, doorY);
    }


    /** Pick doors for A and B as a *pair* using only edges
     *  - Uses the axis with the smaller rectangle gap
     *  - If one axis has gap==0 (walls touching), use the another axis to avoid punch-through
     *  - Doors face each other; Y (for horizontal) or X (for vertical) is chosen from the overlap band
     */
    private DoorPair pickDoorPairByEdges(MainRoom A, MainRoom B) {
        // legal (non-corner) bands for door coordinates
        int aXL = A.getLeft()+1, aXR = A.getRight()-1;
        int aYB = A.getBottom()+1, aYT = A.getTop()-1;
        int bXL = B.getLeft()+1, bXR = B.getRight()-1;
        int bYB = B.getBottom()+1, bYT = B.getTop()-1;

        int offA = Math.max(0, A.getThicknessOfWall() - 1);
        int offB = Math.max(0, B.getThicknessOfWall() - 1);

        int gx = gapX(A, B);
        int gy = gapY(A, B);

        boolean horizontal;
        if (gx == 0 && gy > 0) horizontal = false;
        else if (gy == 0 && gx > 0) horizontal = true;
        else if (gx != gy) horizontal = (gx < gy);
        else horizontal = (Math.abs(A.getLocation().x - B.getLocation().x) >= Math.abs(A.getLocation().y - B.getLocation().y));

        if (horizontal) {
            // pick independent rows near the middle, clamped to each room
            int yMid = (A.getLocation().y + B.getLocation().y) / 2;
            int yA = clamp(yMid, aYB + A.getThicknessOfWall(), aYT - A.getThicknessOfWall());
            int yB = clamp(yMid, bYB + A.getThicknessOfWall(), bYT - A.getThicknessOfWall());

            // edge intervals at those rows
            int aLx = A.getEdgeOn(yA, Direction.LEFT),  aRx = A.getEdgeOn(yA, Direction.RIGHT);
            int bLx = B.getEdgeOn(yB, Direction.LEFT),  bRx = B.getEdgeOn(yB, Direction.RIGHT);

            Direction outA, outB;
            if (aRx <= bLx) { outA = Direction.RIGHT; outB = Direction.LEFT; }
            else if (bRx <= aLx) { outA = Direction.LEFT; outB = Direction.RIGHT; }
            else { // projections overlap at these rows (possible with notches) → tie-break by centers
                outA = (A.getLocation().x <= B.getLocation().x) ? Direction.RIGHT : Direction.LEFT;
                outB = (outA == Direction.RIGHT) ? Direction.LEFT : Direction.RIGHT;
            }

            int ax = (outA == Direction.RIGHT)
                    ? A.getEdgeOn(yA, Direction.RIGHT) - offA
                    : A.getEdgeOn(yA, Direction.LEFT) + offA;
            int bx = (outB == Direction.RIGHT)
                    ? B.getEdgeOn(yB, Direction.RIGHT) - offB
                    : B.getEdgeOn(yB, Direction.LEFT) + offB;

            return new DoorPair(new Point(ax, yA), new Point(bx, yB), outA, outB);

        } else {
            // vertical: pick independent columns near the middle, clamped to each room
            int xMid = (A.getLocation().x + B.getLocation().x) / 2;
            int xA = clamp(xMid, aXL + A.getThicknessOfWall(), aXR - A.getThicknessOfWall());
            int xB = clamp(xMid, bXL + A.getThicknessOfWall(), bXR - A.getThicknessOfWall());

            // edge intervals at those columns
            int aBy = A.getEdgeOn(xA, Direction.DOWN), aUy = A.getEdgeOn(xA, Direction.UP);
            int bBy = B.getEdgeOn(xB, Direction.DOWN), bUy = B.getEdgeOn(xB, Direction.UP);

            Direction outA, outB;
            if (aUy <= bBy) { outA = Direction.UP; outB = Direction.DOWN; }
            else if (bUy <= aBy) { outA = Direction.DOWN; outB = Direction.UP; }
            else {
                outA = (A.getLocation().y <= B.getLocation().y) ? Direction.UP : Direction.DOWN;
                outB = (outA == Direction.UP) ? Direction.DOWN : Direction.UP;
            }

            int ay = (outA == Direction.UP)
                    ? A.getEdgeOn(xA, Direction.UP)   - offA
                    : A.getEdgeOn(xA, Direction.DOWN) + offA;
            int by = (outB == Direction.UP)
                    ? B.getEdgeOn(xB, Direction.UP)   - offB
                    : B.getEdgeOn(xB, Direction.DOWN) + offB;

            return new DoorPair(new Point(xA, ay), new Point(xB, by), outA, outB);
        }
    }


    /* Return true if given point doesn't belong to destination room*/
    private boolean isValidPivot(Point p, MainRoom destinationRoom) {
        return !destinationRoom.isInRoom(p.x, p.y);
    }


    /* It will generate a pivot based on given direction, current position, and destination. The pivot point cannot
    * inside the destination room since it will open another door accidentally. */
    private Point generatePivot(Point current, Direction direction, Point destination, int pivotCount, MainRoom destinationRoom) {
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
                    Point p = new Point(current.x, py);
                    // If current generated pivot is inside the destination point, it means it will go inwards the destination
                    // room, which will open another door except the given door
                    if (!isValidPivot(p, destinationRoom)) {
                        int safeY = Math.min(current.y + 1, maxY);
                        return new Point(current.x, safeY);
                    }
                    return p;
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
                    Point p = new Point(current.x, Math.max(py, minY));
                    if (!isValidPivot(p, destinationRoom)) {
                        int safeY = Math.max(current.y - 1, minY);
                    }
                    return p;
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
                    Point p = new Point(px, current.y);
                    if (!isValidPivot(p, destinationRoom)) {
                        int safeX = Math.min(current.x + 1, maxX);
                        return new Point(safeX, current.y);
                    }
                    return p;
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
                    Point p = new Point(Math.max(px, minX), current.y);
                    if (!isValidPivot(p, destinationRoom)) {
                        int safeX = Math.max(current.x - 1, minX);
                        return new Point(safeX, current.y);
                    }
                    return p;
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
        // Choose the straight direction toward dest
        if (remainingPivots == 0) {
            if (atPivot.x != dest.x) return (dest.x > atPivot.x) ? Direction.RIGHT : Direction.LEFT;
            if (atPivot.y != dest.y) return (dest.y > atPivot.y) ? Direction.UP : Direction.DOWN;
            return currentDir;
        }

        boolean wasHorizontal = (currentDir == Direction.LEFT || currentDir == Direction.RIGHT);

        if (!wasHorizontal) { // came vertically; now go horizontal if possible
            if (dest.x > atPivot.x) return Direction.RIGHT;
            if (dest.x < atPivot.x) return Direction.LEFT;
            // tie-breaker
            int rightSpace = (world.length - 2) - atPivot.x;
            int leftSpace = atPivot.x - 1;
            if (rightSpace == leftSpace) return (random.nextBoolean() ? Direction.RIGHT : Direction.LEFT);
            return (rightSpace > leftSpace) ? Direction.RIGHT : Direction.LEFT;
        } else { // came horizontally; now go vertical if possible
            if (dest.y > atPivot.y) return Direction.UP;
            if (dest.y < atPivot.y) return Direction.DOWN;
            int upSpace   = (world[0].length - 2) - atPivot.y;
            int downSpace = atPivot.y - 1;
            if (upSpace == downSpace) return (random.nextBoolean() ? Direction.UP : Direction.DOWN);
            return (upSpace > downSpace) ? Direction.UP : Direction.DOWN;
        }
    }


    private static boolean contains(java.util.List<? extends Point> pts, int x, int y) {
        for (Point p : pts) if (p.x == x && p.y == y) return true;
        return false;
    }

    /* Allocate a straight segment currentPos to nextPivot.
     * - If out of bounds, or > MAX_WALL_IN_A_ROW walls in a row: return false
     * - If allocating wall around the hallway will out of bounds: return false
     * - NOTHING: stage floor
     * - PASSABLE: walk over
     * - WALL: stage door, keep counting; if run exceeds limit return false.
     */
    private boolean allocateHallway(Point currentPos, Point nextPivot, ArrayList<Point> floors, ArrayList<Point> doors, boolean stageStartDoor, MainRoom destination, boolean connectFinalRoom) {
        if (!(currentPos.x == nextPivot.x || currentPos.y == nextPivot.y)) {
            throw new IllegalArgumentException("Invalid pivot: " + nextPivot.toString() + "with current Position: " + currentPos.toString());
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
        if (dx == 0 && dy == 0) return false;

        int x = currentPos.x, y = currentPos.y;
        int wallRun = 0;
        while (x != nextPivot.x || y != nextPivot.y) {
            int nx = x + dx, ny = y + dy;
            if (!inBounds(nx, ny)) return false;
            if (!connectFinalRoom) if (finalBoxRoom.isInRoom(nx, ny)) return false;

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
                stageFloors.add(new Point(nx, ny));
                wallRun = 0;
            } else {
                if (destination.isInRoom(nx, ny)) return false;
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

    /* Get the distance between A and B horizontally */
    private int gapX(MainRoom A, MainRoom B) {
        if (A.getRight() < B.getLeft())  return B.getLeft() - A.getRight();
        if (B.getRight() < A.getLeft())  return A.getLeft() - B.getRight();
        return 0;
    }

    /* Get the distance between A and B vertically */
    private int gapY(MainRoom A, MainRoom B) {
        if (A.getTop() < B.getBottom())  return B.getBottom() - A.getTop();
        if (B.getTop() < A.getBottom())  return A.getBottom() - B.getTop();
        return 0;
    }


    /** Simple connection for a constant failing connection */
    public boolean connectSimpleL(MainRoom a, MainRoom b, boolean connectFinalRoom) {
        DoorPair dp = pickDoorPairByEdges(a, b);
        Point drA = dp.drA, drB = dp.drB;

        ArrayList<Point> floors = new ArrayList<>(), doors = new ArrayList<>();

        // straight case
        if (drA.x == drB.x || drA.y == drB.y) {
            if (!allocateHallway(drA, drB, floors, doors, true, b, connectFinalRoom)) return false;
        } else {
            Point p1 = new Point(drB.x, drA.y);
            if (!(allocateHallway(drA, p1, floors, doors, true, b, connectFinalRoom) && allocateHallway(p1, drB, floors, doors, false, b, connectFinalRoom))) {
                floors.clear(); doors.clear();
                Point p2 = new Point(drA.x, drB.y);
                if (!(allocateHallway(drA, p2, floors, doors, true, b, connectFinalRoom) && allocateHallway(p2, drB, floors, doors, false, b, connectFinalRoom))) {
                    return false;
                }
            }
        }

        // guarantee endpoint opens if last hop stopped adjacent
        if (!TileType.toType(world[drB.x][drB.y]).isPassable()
                && !contains(doors, drB.x, drB.y) && !contains(floors, drB.x, drB.y)) {
            doors.add(new Point(drB));
        }

        ArrayList<WallPoint> walls = collectWalls(floors, doors);
        if (walls == null) return false;
        walls.removeIf(p -> contains(floors,p.x,p.y) || contains(doors,p.x,p.y));

        for (Point f : floors) world[f.x][f.y] = tileengine.Tileset.FLOOR;
        for (WallPoint w : walls)  world[w.x][w.y] = w.tile.toTETile();
        for (Point d : doors)  world[d.x][d.y] = tileengine.Tileset.FLOOR;
        return true;
    }


    public boolean connectFinalRoom(MainRoom finalRoom, ArrayList<MainRoom> majorRooms) {
        for (int i = FINAL_ROOM_HALLWAY_NUM; i > 0;) {
            if (connect(majorRooms.get(random.nextInt(majorRooms.size())), finalRoom,true)) i--;
        }

        return true;
    }
}
