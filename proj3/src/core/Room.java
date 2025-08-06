package core;

import tileengine.TETile;
import utils.RandomUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import static core.Config.*;
import static java.lang.Math.clamp;

public class Room extends TerrainInfo{
    private TileType floorType;
    private TileType wallType;
    private boolean isCornered;
    private boolean isSubroom;
    private int thicknessOfWall;



    private ArrayList<Room> subRoom;


    public Room(int height, int width, Point location, int thicknessOfWall, boolean isCornered) {
        super(height, width, location);

        this.thicknessOfWall = thicknessOfWall;
        this.isCornered = isCornered;
    }



    public Room(int height, int width, int x, int y, int thicknessOfWall, boolean isCornered) {
        super(height, width, x, y);

        this.thicknessOfWall = thicknessOfWall;
        this.isCornered = isCornered;
    }



    /** Allocate the room based on the information stored. The out-most floor will always be FLOOR type, while the other
     * floor will be the floor type it stored
     * @param world the world that the room will be presented */
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
                if (i < startX + thicknessOfWall || i >= endX - thicknessOfWall || j < startY + thicknessOfWall || j >= endY - thicknessOfWall) {
                    world[i][j] = wallType.toTETile();
                }
                // Outermost floor layer inside the wall
                else if (i < startX + thicknessOfWall + 1 || i >= endX - thicknessOfWall - 1 || j < startY + thicknessOfWall + 1 || j >= endY - thicknessOfWall - 1) {
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

        return (inLeft && inBottom)  ||
                (inRight && inBottom) ||
                (inLeft && inTop)     ||
                (inRight && inTop);
    }



    /** Testing whether input room is a valid room. It couldn't have overlap with other rooms. It cannot make the floor
     * adjacent with the edge of the world, that is, the "floor" of the room need to be surrounded by "wall"
     * @param room the room that need validation
     * @param rooms the room that already exist */
    public static boolean validRoom(Room room, ArrayList<Room> rooms) {
        if (!TerrainInfo.withinBounds(room.getLocation().x, room.getLocation().y, room.getWidth(), room.getHeight())) {
            return false;
        }

        for (Room r : rooms) {
            if (boundingBoxesOverlap(room, r)) {
                return false;
            }
        }

        return true;
    }



    /* Check whether it will have overlap with a room */
    private static boolean boundingBoxesOverlap(Room a, Room b) {
        Rectangle boxA = new Rectangle(
                a.getLocation().x,
                a.getLocation().y,
                a.getWidth(),
                a.getHeight()
        );

        Rectangle boxB = new Rectangle(
                b.getLocation().x,
                b.getLocation().y,
                b.getWidth(),
                b.getHeight()
        );

        return boxA.intersects(boxB);
    }



    /** Testing whether input room is a valid room. It couldn't have overlap with other rooms. It cannot make the floor
     * adjacent with the edge of the world, that is, the "floor" of the room need to be surrounded by "wall"
     * @param x the room needs validation's x-axis
     * @param y the room needs validation's y-axis
     * @param width the room needs validation's width
     * @param height the room needs validations' height
     * @param rooms the room that already exist */
    public static boolean validRoom(int x, int y, int width, int height, ArrayList<Room> rooms) {
        if (!TerrainInfo.withinBounds(x, y, width, height)) {
            return false;
        }

        for (Room r : rooms) {
            if (boundingBoxesOverlap(x, y, width, height, r, BUFFER)) {
                return false;
            }
        }
        return true;
    }



    /* Check whether it will have potential interaction with a room */
    private static boolean boundingBoxesOverlap(int x, int y, int width, int height, Room b, int buffer) {
        Rectangle boxA = new Rectangle(
                x - buffer,
                y - buffer,
                width + 2 * buffer,
                height + 2 * buffer
        );

        Rectangle boxB = new Rectangle(
                b.getLocation().x,
                b.getLocation().y,
                b.getWidth(),
                b.getHeight()
        );
        return boxA.intersects(boxB);
    }



    /** Return a room based on input ideal size. If it is attaching on a main room, return null.
     * @param idealSize The ideal size of the room
     * @param random The random that used to generate the room
     * @param baseRoom whether it is a subroom. If it is a subroom, information of its main room
     * @param direction which direction the subroom will be generated */
    public static Room generateRoom(int idealSize, Random random, Room baseRoom, int direction) {
        double aspectRatio = random.nextDouble(0.8, 1.3);
        int width = (int) Math.sqrt(idealSize * aspectRatio);
        int height = (int) (idealSize / (double) width);

        // Apply some randomness to make the shape of room has more diversity
        width += RandomUtils.uniform(random, -2, 3);
        height += RandomUtils.uniform(random, -2, 3);

        if (baseRoom == null) {
            width = clamp(width, MIN_MAIN_ROOM_WIDTH, MAX_MAIN_ROOM_WIDTH);
            height = clamp(height, MIN_MAIN_ROOM_HEIGHT, MAX_MAIN_ROOM_HEIGHT);
        } else {
            width = clamp(width, MIN_SUB_ROOM_WIDTH, MAX_SUB_ROOM_WIDTH);
            height = clamp(height, MIN_SUB_ROOM_HEIGHT, MAX_SUB_ROOM_HEIGHT);
        }


        int x, y;
        if (baseRoom == null) {
            // Main room: place anywhere
            x = RandomUtils.uniform(random, 1, WINDOW_WIDTH - width / 2 - 1);
            y = RandomUtils.uniform(random, 1, WORLD_HEIGHT - height / 2 - 1);
        } else {
            // It is generating a subroom
            switch (direction) {
                case 0: // left
                    x = baseRoom.getLeft() - width - 1;
                    y = RandomUtils.uniform(random, baseRoom.getBottom(), baseRoom.getTop() - height + 1);
                    break;
                case 1: // right
                    x = baseRoom.getRight() + 1;
                    y = RandomUtils.uniform(random, baseRoom.getBottom(), baseRoom.getTop() - height + 1);
                    break;
                case 2: // bottom
                    y = baseRoom.getBottom() - height - 1;
                    x = RandomUtils.uniform(random, baseRoom.getLeft(), baseRoom.getRight() - width + 1);
                    break;
                case 3: // top
                    y = baseRoom.getTop() + 1;
                    x = RandomUtils.uniform(random, baseRoom.getLeft(), baseRoom.getRight() - width + 1);
                    break;
                default:
                    // fallback if invalid direction
                    x = baseRoom.getRight() + 1;
                    y = baseRoom.getBottom();
                    break;
            }
        }

        int wallThickness;
        boolean isCornered;
        Room newRoom;
        if (baseRoom == null) {
            wallThickness = (random.nextDouble(1) < WALL_THICKNESS_1_PROBABILITY) ? BLOCK_WIDTH1 : BLOCK_WIDTH2;
            isCornered = random.nextInt(100) % 4 != 0;
            newRoom = new Room(height, width, x, y, wallThickness, isCornered);
            newRoom.getRandomPassable(random);
            newRoom.getRandomImpassable(random);
            newRoom.isSubroom = false;

        } else {
            wallThickness = baseRoom.thicknessOfWall;
            isCornered = baseRoom.isCornered;
            newRoom = new Room(height, width, x, y, wallThickness, isCornered);
            newRoom.isSubroom = true;
        }

        return newRoom;
    }


    /* Return the Left edge (the x value) of the room */
    private int getLeft() {
        return getLocation().x - getWidth() / 2;
    }

    /* Return the Right edge (the x value) of the room */
    private int getRight() {
        return getLocation().x + getWidth() / 2;
    }

    /* Return the Top edge (the y value) of the room */
    private int getTop() {
        return getLocation().y - getHeight() / 2;
    }

    /* Return the Bottom edge (the y value) of the room */
    private int getBottom() {
        return getLocation().y + getHeight() / 2;
    }


    /** Return random picked tile type for room wall. WALL type has higher probability. If the
     * Floor type is natural, mountain has higher chance */
    public void getRandomImpassable(Random random) {
        // Natural floors boost MOUNTAIN chance
        boolean isNatural = floorType == TileType.FLOWER
                || floorType == TileType.TREE
                || floorType == TileType.GRASS;

        double mountainChance = isNatural ? 0.6 : 0.3;
        double roll = random.nextDouble();

        if (roll < mountainChance) {
            wallType = TileType.MOUNTAIN;
        } else {
            wallType = TileType.WALL;
        }
    }


    /** Return random picked tile type for room floor. FLOOR type is more likely to present */
    public void getRandomPassable(Random random) {
        if (random.nextDouble() < ROOM_FLOOR_POSS) {
            floorType = TileType.FLOOR;
        }

        // Else choose from other passable types
        TileType[] values = TileType.values();
        ArrayList<TileType> others = new ArrayList<>();
        for (TileType t : values) {
            if (t.passable && t != TileType.FLOOR) {
                others.add(t);
            }
        }

        floorType = others.get(random.nextInt(others.size()));
    }


    /** Return the size of the room */
    public int getSize() {
        return getWidth() * getHeight();
    }


    /** Attach a room to the given main room
     * @param mainRoom the room that being attached */
    public void attachRoom(Room mainRoom) {
        mainRoom.subRoom.add(this);
    }

}
