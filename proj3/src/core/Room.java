package core;

import java.awt.*;

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
}
