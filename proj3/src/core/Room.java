package core;

import java.awt.*;
import java.util.ArrayList;

public class Room extends TerrainInfo{
    private TileType floorType;
    private TileType wallType;
    private boolean isCornered;


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

    /** Draw a room with given TileType, the width = inside room + CANT_PASS block numbers.
     */
    public void drawRoomCornerOrNot() {

    }


    /** Testing whether input room is a valid room. It couldn't have overlap with other rooms. It cannot make the floor
     * adjacent with the edge of the world, that is, the "floor" of the room need to be surrounded by "wall"
     * @param room the room that need validation
     * @param rooms the room that already exist */
    public static boolean validRoom(Room room, ArrayList<Room> rooms) {}



    /** Testing whether input room is a valid room. It couldn't have overlap with other rooms. It cannot make the floor
     * adjacent with the edge of the world, that is, the "floor" of the room need to be surrounded by "wall"
     * @param x the room needs validation's x-axis
     * @param y the room needs validation's y-axis
     * @param width the room needs validation's width
     * @param height the room needs validations' height
     * @param thickness the thickness of the room's wall
     * @param rooms the room that already exist */
    public static boolean validRoom(int x, int y, int width, int height, int thickness, ArrayList<Room> rooms) {

    }


    /** Testing whether input room is a valid room. It couldn't have overlap with other rooms. It cannot make the floor
     * adjacent with the edge of the world, that is, the "floor" of the room need to be surrounded by "wall"
     * @param location the location of the room
     * @param width the room needs validation's width
     * @param height the room needs validations' height
     * @param thickness the thickness of the room's wall
     * @param rooms the room that already exist */
    public static boolean validRoom(Point location, int width, int height, int thickness, ArrayList<Room> rooms) {

    }
}
