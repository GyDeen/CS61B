package core;

import tileengine.TERenderer;
import tileengine.TETile;
import utils.RandomUtils;

import java.awt.*;
import java.util.Random;

import static core.Config.*;
import static java.lang.Math.clamp;

public class SubRoom extends Room {
    private MainRoom mainRoom;
    // 0: LEFT   1: RIGHT   2: BOTTOM   3: TOP
    private int direction;


    public SubRoom(int height, int width, int x, int y, int thicknessOfWall, boolean isCornered, MainRoom baseRoom, int direction) {
        super(height, width, x, y, thicknessOfWall, isCornered);
        mainRoom = baseRoom;
        this.direction = direction;
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

        // At least wall tiles overlap with the main room. At most hal of the subroom overlap with main room
        int overlap;

        int x, y;
        switch (direction) {
            case 0: // left
                overlap = RandomUtils.uniform(random, baseRoom.getThicknessOfWall(), width / 2);
                x = baseRoom.getLeft() + overlap - width / 2;
                y = RandomUtils.uniform(random, baseRoom.getBottom(), baseRoom.getTop() - height + 1);
                break;
            case 1: // right
                overlap = RandomUtils.uniform(random, baseRoom.getThicknessOfWall(), width / 2);
                x = baseRoom.getRight() - overlap + width / 2;
                y = RandomUtils.uniform(random, baseRoom.getBottom(), baseRoom.getTop() - height + 1);
                break;
            case 2: // bottom
                overlap = RandomUtils.uniform(random, baseRoom.getThicknessOfWall(), height / 2);
                y = baseRoom.getBottom() + overlap - height / 2;
                x = RandomUtils.uniform(random, baseRoom.getLeft(), baseRoom.getRight() - width + 1);
                break;
            case 3: // top
            default:
                overlap = RandomUtils.uniform(random, baseRoom.getThicknessOfWall(), height / 2);
                y = baseRoom.getTop() - overlap + height / 2;
                x = RandomUtils.uniform(random, baseRoom.getLeft(), baseRoom.getRight() - width + 1);
                break;
        }

        SubRoom sub = new SubRoom(height, width, x, y, baseRoom.getThicknessOfWall(), baseRoom.isCornered(), baseRoom, direction);
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

        int startX = getLeft();
        int startY = getBottom();
        int endX = getRight();
        int endY = getTop();

        for (int i = startX; i < endX; i++) {
            for (int j = startY; j < endY; j++) {
                // If it don't have corner, skip the corner tiles
                if (!isCornered() && isCornerArea(i, j, startX, startY, endX, endY, getThicknessOfWall())) continue;

                // If it belongs to Main room, override it with floorType it should be
                if (belongMainRoom(i, j, mainRoomPosition, mainRoomWidth, mainRoomHeight)) {
                    world[i][j] = getFloorType().toTETile();
                    continue;
                }

                // Wall zone
                if (i < startX + getThicknessOfWall() || i >= endX - getThicknessOfWall() ||
                        j < startY + getThicknessOfWall() || j >= endY - getThicknessOfWall()) {
                    world[i][j] = getWallType().toTETile();
                }
                // Inner floor
                else {
                    world[i][j] = getFloorType().toTETile();
                }
            }
        }

    }


    /* Return true if given position belongs to its main room */
    private boolean belongMainRoom(int x, int y, Point mainRoomPosition, int mainRoomWidth, int mainRoomHeight) {
        int left = mainRoomPosition.x - mainRoomWidth / 2;
        int bottom  = mainRoomPosition.y - mainRoomHeight / 2;
        return x >= left && x < left + mainRoomWidth
                && y >= bottom  && y < bottom  + mainRoomHeight;
    }
}
