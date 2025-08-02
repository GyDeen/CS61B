package core;

import java.awt.*;
import java.util.ArrayList;

public class Room extends TerrainInfo{
    private TileType floorType;
    private TileType wallType;
    private boolean isCornered;
    private static int BUFFER = 2;


    public Room(int height, int width, Point location, boolean isCornered, TileType floorType, TileType wallType) {
        super(height, width, location);

        this.floorType = floorType;
        this.wallType = wallType;
        this.isCornered = isCornered;
    }



    public Room(int height, int width, int x, int y, boolean isCornered, TileType floorType, TileType wallType) {
        super(height, width, x, y);

        this.floorType = floorType;
        this.wallType = wallType;
        this.isCornered = isCornered;
    }



    /** Render the room */
    public void renderRoom() {

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
