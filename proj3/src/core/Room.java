package core;

import tileengine.TETile;
import utils.RandomUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import static core.Config.*;

public abstract class Room {
    // Merged-from-TerrainInfo
    private final int height;
    private final int width;
    // central point
    private final Point location;

    // Existing Room state
    protected TileType floorType;
    protected TileType wallType;
    protected boolean isCornered;
    protected int thicknessOfWall;

    // Only initialise when there is a subroom attach to this room
    protected ArrayList<Room> subRoom;


    protected Room(int height, int width, Point location, int thicknessOfWall, boolean isCornered) {
        this.height = height;
        this.width = width;
        this.location = new Point(location);
        this.thicknessOfWall = thicknessOfWall;
        this.isCornered = isCornered;
    }

    protected Room(int height, int width, int x, int y, int thicknessOfWall, boolean isCornered) {
        this(height, width, new Point(x, y), thicknessOfWall, isCornered);
    }



    // Getter for often used property
    public int getHeight() { return height; }
    public int getWidth()  { return width; }
    public Point getLocation() { return new Point(location); }
    public int getSize() {return getWidth() * getHeight();}

    public int getLeft()   { return getLocation().x - getWidth() / 2; }
    public int getRight()  { return getLocation().x + getWidth() / 2; }
    public int getTop()    { return getLocation().y - getHeight() / 2; }
    public int getBottom() { return getLocation().y + getHeight() / 2; }



    /** Check whether generated terrain will not exceed the window */
    public static boolean withinBounds(int x, int y, int width, int height) {
        return x - width / 2 > 0
                && y - height / 2 > 0
                && x + width / 2 < WINDOW_WIDTH
                && y + height / 2 < WORLD_HEIGHT;
    }


    /** Allocate this room and all subrooms into world */
    public void allocateRooms(TETile[][] world) {
        allocateRoom(world);
        if (subRoom != null) {
            for (Room room : subRoom) {
                room.allocateRooms(world);
            }
        }
    }



    /* The method that actually assign tiles to the input world */
    private void allocateRoom(TETile[][] world) {
        int startX = getLocation().x - getWidth() / 2;
        int startY = getLocation().y - getHeight() / 2;
        int endX = startX + getWidth();
        int endY = startY + getHeight();

        for (int i = startX; i < endX; i++) {
            for (int j = startY; j < endY; j++) {
                // If it don't have corner, skip the corner tiles
                if (!isCornered && isCornerArea(i, j, startX, startY, endX, endY, thicknessOfWall)) continue;

                // Wall zone
                if (i < startX + thicknessOfWall || i >= endX - thicknessOfWall ||
                        j < startY + thicknessOfWall || j >= endY - thicknessOfWall) {
                    world[i][j] = wallType.toTETile();
                }
                // Outermost floor layer inside the wall
                else if (i < startX + thicknessOfWall + 1 || i >= endX - thicknessOfWall - 1 ||
                        j < startY + thicknessOfWall + 1 || j >= endY - thicknessOfWall - 1) {
                    world[i][j] = TileType.FLOOR.toTETile();
                }
                // Inner floor
                else {
                    world[i][j] = floorType.toTETile();
                }
            }
        }
    }


    /* Return true if the given position is a corner */
    private boolean isCornerArea(int x, int y, int startX, int startY, int endX, int endY, int t) {
        boolean inLeft = x < startX + t;
        boolean inRight = x >= endX - t;
        boolean inBottom = y < startY + t;
        boolean inTop = y >= endY - t;
        return (inLeft && inBottom) || (inRight && inBottom) || (inLeft && inTop) || (inRight && inTop);
    }


    /** Testing whether input room is a valid room (no overlaps and within bounds) */
    public static boolean validRoom(Room room, ArrayList<Room> rooms) {
        if (!withinBounds(room.getLocation().x, room.getLocation().y, room.getWidth(), room.getHeight())) {
            return false;
        }
        for (Room r : rooms) {
            if (boundingBoxesOverlap(room, r)) return false;

            if (r.subRoom != null) {
                for (Room s : r.subRoom) {
                    if (boundingBoxesOverlap(room, s)) return false;
                }
            }
        }
        return true;
    }


    /** Validate by raw x/y/width/height */
    public static boolean validRoom(int x, int y, int width, int height, ArrayList<Room> rooms) {
        if (!withinBounds(x, y, width, height)) return false;

        for (Room r : rooms) {
            if (boundingBoxesOverlap(x, y, width, height, r)) return false;

            if (r.subRoom != null) {
                for (Room s : r.subRoom) {
                    if (boundingBoxesOverlap(x, y, width, height, s)) return false;
                }
            }
        }
        return true;
    }

    private static boolean boundingBoxesOverlap(Room a, Room b) {
        Rectangle boxA = new Rectangle(a.getLocation().x, a.getLocation().y, a.getWidth(), a.getHeight());
        Rectangle boxB = new Rectangle(b.getLocation().x, b.getLocation().y, b.getWidth(), b.getHeight());
        return boxA.intersects(boxB);
    }

    private static boolean boundingBoxesOverlap(int x, int y, int width, int height, Room b) {
        Rectangle boxA = new Rectangle(x, y, width, height);
        Rectangle boxB = new Rectangle(b.getLocation().x, b.getLocation().y, b.getWidth(), b.getHeight());
        return boxA.intersects(boxB);
    }



    public void attachRoom(Room mainRoom) {
        if (mainRoom.subRoom == null) mainRoom.subRoom = new ArrayList<>();
        mainRoom.subRoom.add(this);
    }



    /** Random wall type biased by floor type */
    public void getRandomImpassable(Random random) {
        boolean isNatural = floorType == TileType.FLOWER
                || floorType == TileType.TREE
                || floorType == TileType.GRASS;
        double mountainChance = isNatural ? 0.6 : 0.3;
        wallType = (random.nextDouble() < mountainChance) ? TileType.MOUNTAIN : TileType.WALL;
    }



    /** Random passable floor type */
    public void getRandomPassable(Random random) {
        if (random.nextDouble() < ROOM_FLOOR_POSS) {
            floorType = TileType.FLOOR;
        }
        ArrayList<TileType> others = new ArrayList<>();
        for (TileType t : TileType.values()) {
            if (t.passable && t != TileType.FLOOR) {
                others.add(t);
            }
        }
        floorType = others.get(random.nextInt(others.size()));
    }
}
