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
    private TileType floorType;
    private TileType wallType;
    private boolean isCornered;
    private int thicknessOfWall;


    public Room(int height, int width, Point location, int thicknessOfWall, boolean isCornered) {
        this.height = height;
        this.width = width;
        this.location = new Point(location);
        this.thicknessOfWall = thicknessOfWall;
        this.isCornered = isCornered;
    }

    public Room(int height, int width, int x, int y, int thicknessOfWall, boolean isCornered) {
        this(height, width, new Point(x, y), thicknessOfWall, isCornered);
    }



    // Getter for often used property
    public int getHeight() { return height; }
    public int getWidth()  { return width; }
    public int getThicknessOfWall() { return thicknessOfWall; }
    public int getSize() {return getWidth() * getHeight();}
    public boolean isCornered() { return isCornered; }
    public Point getLocation() { return new Point(location); }

    public int getLeft()   { return getLocation().x - getWidth() / 2; }
    public int getRight()  { return getLocation().x + getWidth() / 2; }
    public int getTop()    { return getLocation().y + getHeight() / 2; }
    public int getBottom() { return getLocation().y - getHeight() / 2; }

    public TileType getFloorType() { return floorType; }
    public TileType getWallType() { return wallType; }


    // Setter for often used property
    public void setFloorType(TileType floorType) { this.floorType = floorType; }
    public void setWallType(TileType wallType) { this.wallType = wallType; }





    /** Check whether generated terrain will not exceed the window */
    public static boolean withinBounds(int x, int y, int width, int height) {
        return x - width / 2 > 0
                && y - height / 2 > 0
                && x + width / 2 < WINDOW_WIDTH
                && y + height / 2 < WORLD_HEIGHT;
    }


    /** Assigning the world with current information stored */
    public void allocateRoom(TETile[][] world) {
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
                // Inner floor
                else {
                    world[i][j] = floorType.toTETile();
                }
            }
        }

        if(this instanceof MainRoom mainRoom) {
            for (SubRoom subRoom : mainRoom.getSubRooms()) {
                subRoom.allocateRoom(world);
             }
        }
    }


    /** Return true if the given position is a corner */
    public boolean isCornerArea(int x, int y, int startX, int startY, int endX, int endY, int t) {
        boolean inLeft = x < startX + t;
        boolean inRight = x >= endX - t;
        boolean inBottom = y < startY + t;
        boolean inTop = y >= endY - t;
        return (inLeft && inBottom) || (inRight && inBottom) || (inLeft && inTop) || (inRight && inTop);
    }



    /** Testing whether input room is a valid room (no overlaps and within bounds) */
    public static boolean validRoom(Room room, ArrayList<Room> rooms, MainRoom allowedOverlap) {
        if (!withinBounds(room.getLocation().x, room.getLocation().y, room.getWidth(), room.getHeight())) {
            return false;
        }

        for (Room r : rooms) {

            // Skip the overlap checking against subroom's baseRoom
            if (r == allowedOverlap) {
                // allow overlap with the base main room itself,
                // but forbid overlap with its existing subrooms
                if (allowedOverlap.getSubRooms() != null) {
                    for (Room s : allowedOverlap.getSubRooms()) {
                        if (boundingBoxesOverlap(room, s)) return false;
                    }
                }
                continue;
            }


            if (boundingBoxesOverlap(room, r)) return false;

            if (r instanceof MainRoom main) {
                if (!main.getSubRooms().isEmpty()) {
                    for (Room subRoom : main.getSubRooms()) {
                        if (boundingBoxesOverlap(room, subRoom)) return false;
                    }
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

            if (r instanceof MainRoom main) {
                if (!main.getSubRooms().isEmpty()) {
                    for (Room subRoom : main.getSubRooms()) {
                        if (boundingBoxesOverlap(x, y, width, height, subRoom)) return false;
                    }
                }
            }
        }
        return true;
    }



    /* Checking whether given information will have collision with existing room */
    private static boolean boundingBoxesOverlap(Room a, Room b) {
        int aLeft = a.getLeft(), aBottom = a.getBottom(), aRight = a.getRight(),  aTop = a.getTop();
        int bLeft = b.getLeft(), bBottom = b.getBottom(), bRight = b.getRight(),  bTop = b.getTop();
        return aLeft < bRight && aRight > bLeft && aBottom < bTop && aTop > bBottom;
    }


    /* Checking whether given information will have collision with existing room */
    private static boolean boundingBoxesOverlap(int x, int y, int width, int height, Room b) {
        Rectangle boxA = new Rectangle(x, y, width, height);
        Rectangle boxB = new Rectangle(b.getLocation().x, b.getLocation().y, b.getWidth(), b.getHeight());
        return boxA.intersects(boxB);
    }



    /** Random wall type biased by floor type
     * @param random the random object used for randomize */
    public void getRandomImpassable(Random random) {
        boolean isNatural = floorType == TileType.FLOWER
                || floorType == TileType.TREE
                || floorType == TileType.GRASS;
        double mountainChance = isNatural ? 0.6 : 0.3;
        wallType = (random.nextDouble() < mountainChance) ? TileType.MOUNTAIN : TileType.WALL;
    }



    /** Random passable floor type
     * @param random the random object used for randomize */
    public void getRandomPassable(Random random) {
        if (random.nextDouble() < ROOM_FLOOR_POSS) {
            floorType = TileType.FLOOR;
        }

        ArrayList<TileType> others = new ArrayList<>();
        for (TileType t : TileType.values()) {
            if (t.isPassable() && t != TileType.FLOOR) {
                others.add(t);
            }
        }
        floorType = others.get(random.nextInt(others.size()));
    }
}
