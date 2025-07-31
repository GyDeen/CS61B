package core;

import java.awt.*;

public class Room extends TerrainInfo{
    private TileType type;
    private int BLOCK_WIDTH1 = 1;
    private int BLOCK_WIDTH2 = 2;
    private boolean isCornered;


    public Room(int height, int width, Point location,  TileType type) {
        super(height, width, location);

        if (type.passable) {
            this.type = type;
        }  else {
            throw new IllegalArgumentException("Invalid tile type");
        }

        isCornered = true;
    }


    public Room(int height, int width, int x, int y, TileType type) {
        super(height, width, x, y);

        if (type.passable) {
            this.type = type;
        }  else {
            throw new IllegalArgumentException("Invalid tile type");
        }
    }

    /** Draw a room with given TileType, the width = inside room + CANT_PASS block numbers. The corner of the room
     * will not have block
     * @param hasCorner randomly determine the room whether it has corner or not,
     *  @param thicknessOfCantPass thickness of the CANT_PASS block. It only determines whether it is width of 2 or 1 */
    public void drawRoomCornerOrNot(int hasCorner, int thicknessOfCantPass) {
        int cantPassBlock;
        // Room without corner is rare
        if (hasCorner % 7 == 0 || hasCorner % 13 == 0 || hasCorner % 4 == 0) {
            isCornered = false;
        }

        // Determine the thickness of the "wall" of the room
        if (getWidth() <= 3) {  // If the width of the room is less than 3, it cant have wall with thickness of 2
            cantPassBlock = 1;
        } else {
            if (thicknessOfCantPass % 2  == 0) cantPassBlock = 2;
            else cantPassBlock = 1;
        }







    }
}
