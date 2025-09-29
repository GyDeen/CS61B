package core;

import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.TileType;
import utils.RandomUtils;

import java.awt.*;
import java.util.Random;

import static core.Config.*;
import static java.lang.Math.clamp;

public class SubRoom extends Room {
    private MainRoom mainRoom;
    private Direction direction;


    public SubRoom(int height, int width, int x, int y, int thicknessOfWall, boolean isCornered, MainRoom baseRoom, Direction direction) {
        super(height, width, x, y, thicknessOfWall, isCornered);
        mainRoom = baseRoom;
        this.direction = direction;
    }

    /** Generate a subroom attached to a base main room
     * direction: 0=left, 1=right, 2=bottom, 3=top
     */
    public static SubRoom generate(int idealSize, Random random, MainRoom baseRoom, Direction direction) {
        double aspectRatio = random.nextDouble(0.8, 1.3);
        int width  = (int) Math.sqrt(idealSize * aspectRatio);
        int height = (int) (idealSize / (double) width);

        width += RandomUtils.uniform(random, -2, 3);
        height += RandomUtils.uniform(random, -2, 3);

        width = clamp(width,  MIN_SUB_ROOM_WIDTH,  MAX_SUB_ROOM_WIDTH);
        height = clamp(height, MIN_SUB_ROOM_HEIGHT, MAX_SUB_ROOM_HEIGHT);

        int minOverlapX = baseRoom.getThicknessOfWall() + baseRoom.getThicknessOfWall();
        int minOverlapY = baseRoom.getThicknessOfWall() + baseRoom.getThicknessOfWall();

        // At least wall tiles overlap with the main room's out-most floor. At most half of the subroom overlap with main room
        int overlap;

        int x, y;
        switch (direction) {
            case LEFT: // left
                overlap = RandomUtils.uniform(random, minOverlapX, width / 2);
                x = baseRoom.getLeft() + overlap - width / 2;
                y = RandomUtils.uniform(random, baseRoom.getBottom(), baseRoom.getTop() - height + 1);
                break;
            case RIGHT: // right
                overlap = RandomUtils.uniform(random, minOverlapX, width / 2);
                x = baseRoom.getRight() - overlap + width / 2;
                y = RandomUtils.uniform(random, baseRoom.getBottom(), baseRoom.getTop() - height + 1);
                break;
            case DOWN: // bottom
                overlap = RandomUtils.uniform(random, minOverlapY, height / 2);
                y = baseRoom.getBottom() + overlap - height / 2;
                x = RandomUtils.uniform(random, baseRoom.getLeft(), baseRoom.getRight() - width + 1);
                break;
            case UP: // top
            default:
                overlap = RandomUtils.uniform(random, minOverlapY, height / 2);
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
        int mainRoomWidth  = mainRoom.getWidth();
        int mainRoomHeight = mainRoom.getHeight();

        int startX = getLeft();
        int startY = getBottom();
        int endX = getRight();
        int endY = getTop();
        int t = getThicknessOfWall();

        for (int i = startX; i < endX; i++) {
            for (int j = startY; j < endY; j++) {

                // Belong to subroom wall
                boolean subWall  = (i < startX + t) || (i >= endX - t) || (j < startY + t) || (j >= endY - t);
                boolean subFloor = !subWall;

                // Main room membership
                boolean inMainBounds = belongMainRoom(i, j, mainRoomPosition, mainRoomWidth, mainRoomHeight);
                boolean onMainFloor = (i >= mainRoom.getLeft() + t && i < mainRoom.getRight() - t) &&
                        (j >= mainRoom.getBottom() + t && j < mainRoom.getTop() - t);
                boolean onMainWall = inMainBounds && !onMainFloor;

                // Doorway only along the shared edge for this subroom's direction
                boolean onSharedEdgeBand =
                        switch (direction) {
                            case LEFT -> (i >= endX - t);
                            case RIGHT -> (i >= startX && i < startX + t);
                            case DOWN -> (j >= endY - t);
                            case UP -> (j >= startY && j < startY + t);
                        };

                boolean againstMainWallBand =
                        switch (direction) {
                            case LEFT -> (i >= mainRoom.getLeft() && i < mainRoom.getLeft() + t) && j >= Math.max(startY,mainRoom.getBottom()) && j < Math.min(endY,mainRoom.getTop());
                            case RIGHT -> (i >= mainRoom.getRight() - t && i < mainRoom.getRight()) && j >= Math.max(startY,mainRoom.getBottom()) && j < Math.min(endY,mainRoom.getTop());
                            case DOWN -> (j >= mainRoom.getBottom() && j < mainRoom.getBottom() + t) && i >= Math.max(startX,mainRoom.getLeft()) && i < Math.min(endX,mainRoom.getRight());
                            case UP -> (j >= mainRoom.getTop() - t && j < mainRoom.getTop()) && i >= Math.max(startX, mainRoom.getLeft()) && i < Math.min(endX,mainRoom.getRight());
                        };

                boolean onSharedDoorway = onSharedEdgeBand && againstMainWallBand;
                boolean placeFloor;
                if (onMainFloor) {
                    placeFloor = true;
                } else if (onSharedDoorway) {
                    placeFloor = true;
                } else if (onMainWall) {
                    placeFloor = false;
                } else {
                    placeFloor = subFloor && !wouldLeakToNothing(world, i, j, mainRoom, this);
                }

                world[i][j] = (placeFloor ? getFloorType() : getWallType()).toTETile();
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


    /** Get direction of this subroom attached */
    public Direction getDirection() {return direction;}


    private boolean wouldLeakToNothing(TETile[][] world, int x, int y,
                                       MainRoom mainRoom, SubRoom subRoom) {
        int[] dx = {1, -1, 0, 0}, dy = {0, 0, 1, -1};
        for (int k = 0; k < 4; k++) {
            int nx = x + dx[k], ny = y + dy[k];
            if (nx < 0 || ny < 0 || nx >= world.length || ny >= world[0].length) return true;

            boolean inMain = belongMainRoom(nx, ny, mainRoom.getLocation(), mainRoom.getWidth(), mainRoom.getHeight());
            boolean inSub  = nx >= subRoom.getLeft() && nx < subRoom.getRight()
                    && ny >= subRoom.getBottom() && ny < subRoom.getTop();

            if (!inMain && !inSub && TileType.toType(world[nx][ny]) == TileType.NOTHING) {
                return true;
            }
        }
        return false;
    }

}
