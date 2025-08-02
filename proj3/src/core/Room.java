package core;

import tileengine.TETile;

import java.awt.*;
import java.util.ArrayList;

public class Room extends TerrainInfo{
    private TileType floorType;
    private TileType wallType;
    private boolean isCornered;
    private int thicknessOfWall;

    private static int BUFFER = 2;


    public Room(int height, int width, Point location, int thicknessOfWall, boolean isCornered, TileType floorType, TileType wallType) {
        super(height, width, location);

        this.thicknessOfWall = thicknessOfWall;
        this.floorType = floorType;
        this.wallType = wallType;
        this.isCornered = isCornered;
    }



    public Room(int height, int width, int x, int y, int thicknessOfWall, boolean isCornered, TileType floorType, TileType wallType) {
        super(height, width, x, y);

        this.thicknessOfWall = thicknessOfWall;
        this.floorType = floorType;
        this.wallType = wallType;
        this.isCornered = isCornered;
    }



    /** Render the room based on the information stored. The out-most floor will always be FLOOR type, while the other
     * floor will be the floor type it stored
     * @param world the world that the room will be presented */
    public void renderRoom(TETile[][] world) {
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
            if (boundingBoxesOverlap(room, r, BUFFER)) {
                return false;
            }
        }

        return true;
    }



    /* Check whether it will have potential interaction with a room */
    private static boolean boundingBoxesOverlap(Room a, Room b, int buffer) {
        Rectangle boxA = new Rectangle(
                a.getLocation().x - buffer,
                a.getLocation().y - buffer,
                a.getWidth() + 2 * buffer,
                a.getHeight() + 2 * buffer
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
}
