package core;

import tileengine.TETile;
import utils.RandomUtils;

import java.awt.*;
import java.util.Random;

import static core.Config.*;
import static java.lang.Math.clamp;

public class SubRoom extends Room {
    MainRoom mainRoom;


    public SubRoom(int height, int width, int x, int y, int thicknessOfWall, boolean isCornered, MainRoom baseRoom) {
        super(height, width, x, y, thicknessOfWall, isCornered);
        mainRoom = baseRoom;
    }

    /** Generate a subroom attached to a base main room
     * direction: 0=left, 1=right, 2=bottom, 3=top
     */
    public static SubRoom generate(int idealSize, Random random, MainRoom baseRoom, int direction) {
        double aspectRatio = random.nextDouble(0.8, 1.3);
        int width  = (int) Math.sqrt(idealSize * aspectRatio);
        int height = (int) (idealSize / (double) width);

        width  += RandomUtils.uniform(random, -2, 3);
        height += RandomUtils.uniform(random, -2, 3);

        width  = clamp(width,  MIN_SUB_ROOM_WIDTH,  MAX_SUB_ROOM_WIDTH);
        height = clamp(height, MIN_SUB_ROOM_HEIGHT, MAX_SUB_ROOM_HEIGHT);

        int x, y;
        switch (direction) {
            case 0: // left
                x = baseRoom.getLeft() - width - 1;
                y = RandomUtils.uniform(random, baseRoom.getTop(), baseRoom.getBottom() - height + 1);
                break;
            case 1: // right
                x = baseRoom.getRight() + 1;
                y = RandomUtils.uniform(random, baseRoom.getTop(), baseRoom.getBottom() - height + 1);
                break;
            case 2: // bottom
                y = baseRoom.getBottom() - height - 1;
                x = RandomUtils.uniform(random, baseRoom.getLeft(), baseRoom.getRight() - width + 1);
                break;
            case 3: // top
            default:
                y = baseRoom.getTop() + 1;
                x = RandomUtils.uniform(random, baseRoom.getLeft(), baseRoom.getRight() - width + 1);
                break;
        }

        SubRoom sub = new SubRoom(height, width, x, y, baseRoom.getThicknessOfWall(), baseRoom.isCornered(), baseRoom);
        sub.setFloorType(baseRoom.getFloorType());
        sub.setWallType(baseRoom.getWallType());
        return sub;
    }

    /** Allocation of subroom needs to connect the subroom floor and override the wall and out most FLOOR with its
     * floor type */
    @Override
    public void allocateRoom(TETile[][] world) {
        Point mainRoomPosition = mainRoom.getLocation();
        int mainRoomWidth = mainRoom.getWidth();
        int mainRoomHeight = mainRoom.getHeight();

    }


    /* Return true if given position belongs to its main room */
    private boolean belongMainRoom(int x, int y, Point mainRoomPosition, int mainRoomWidth, int mainRoomHeight) {
        return (mainRoomPosition.x - mainRoomWidth / 2 <= x && x <= mainRoomPosition.x + mainRoomWidth / 2) &&
                (mainRoomPosition.y - mainRoomHeight / 2 <= y && y <= mainRoomPosition.y + mainRoomHeight / 2);
    }
}
