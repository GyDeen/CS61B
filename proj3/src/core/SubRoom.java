package core;

import tileengine.TETile;
import tileengine.TileType;
import utils.RandomUtils;

import java.awt.*;
import java.util.Random;

import static core.Config.*;
import static java.lang.Math.clamp;

public class SubRoom extends Room {
    private MainRoom mainRoom;
    private Direction directionOnMain;


    public SubRoom(int height, int width, int x, int y, int thicknessOfWall, boolean isCornered, MainRoom baseRoom, Direction direction) {
        super(height, width, x, y, thicknessOfWall, isCornered);
        mainRoom = baseRoom;
        this.directionOnMain = direction;
    }

    /** Generate a subroom attached to a base main room
     * direction: 0=left, 1=right, 2=bottom, 3=top
     */
    public static SubRoom generate(int idealSize, Random random, MainRoom baseRoom, Direction direction) {
        double aspectRatio = random.nextDouble(0.8, 1.3);
        int width  = (int) Math.sqrt(idealSize * aspectRatio);
        int height = (int) (idealSize / (double) width);
        int t = baseRoom.getThicknessOfWall();

        width += RandomUtils.uniform(random, -2, 3);
        height += RandomUtils.uniform(random, -2, 3);

        width = clamp(width,  MIN_SUB_ROOM_WIDTH,  MAX_SUB_ROOM_WIDTH);
        height = clamp(height, MIN_SUB_ROOM_HEIGHT, MAX_SUB_ROOM_HEIGHT);
        int x, y;
        /* + - t to make sure it will always have at least the outmost floor connected */
        switch (direction) {
            case LEFT -> {
                x = baseRoom.getLeft() - (width / 2) + RandomUtils.uniform(random, 0, width / 3 + 1) + t;
                y = RandomUtils.uniform(random, baseRoom.getBottom(), baseRoom.getTop() - height);
            }
            case RIGHT -> {
                // - 1 due to allocation is open range
                x = baseRoom.getRight() + (width / 2) - RandomUtils.uniform(random, 0, width / 3 + 1) - 1 - t;
                y = RandomUtils.uniform(random, baseRoom.getBottom(), baseRoom.getTop() - height);
            }
            case DOWN -> {
                y = baseRoom.getBottom() - (height / 2) + RandomUtils.uniform(random, 0, height / 3 + 1) + t;
                x = RandomUtils.uniform(random, baseRoom.getLeft(), baseRoom.getRight() - width);
            }
            case UP -> {
                // - 1 due to allocation is open range
                y = baseRoom.getTop() + (height / 2) - RandomUtils.uniform(random, 0, height / 3 + 1) - 1 - t;
                x = RandomUtils.uniform(random, baseRoom.getLeft(), baseRoom.getRight() - width);
            }
            default -> throw new IllegalStateException("Unexpected direction: " + direction);
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
        int t = getThicknessOfWall();

        for (int i = startX; i < endX; i++) {
            for (int j = startY; j < endY; j++) {

                // Belong to subroom wall
                boolean subWall = (i < startX + t) || (i >= endX - t) || (j < startY + t) || (j >= endY - t);
                boolean subFloor = !subWall;

                // Main room
                boolean inMainBounds = belongMainRoom(i, j, mainRoomPosition, mainRoomWidth, mainRoomHeight);
                boolean onMainFloor = (i >= mainRoom.getLeft() + t && i < mainRoom.getRight() - t) &&
                        (j >= mainRoom.getBottom() + t && j < mainRoom.getTop() - t);
                boolean onMainWall = inMainBounds && !onMainFloor;

                // Doorway only along the shared edge for this subroom's direction
                boolean onSharedEdgeBand =
                        switch (directionOnMain) {
                            case LEFT -> (i >= endX - t);
                            case RIGHT -> (i >= startX && i < startX + t);
                            case DOWN -> (j >= endY - t);
                            case UP -> (j >= startY && j < startY + t);
                        };

                boolean againstMainWallBand =
                        switch (directionOnMain) {
                            case LEFT -> (i >= mainRoom.getLeft() && i < mainRoom.getLeft() + t) && j > Math.max(startY,mainRoom.getBottom()) && j < Math.min(endY,mainRoom.getTop());
                            case RIGHT -> (i >= mainRoom.getRight() - t && i < mainRoom.getRight()) && j > Math.max(startY,mainRoom.getBottom()) && j < Math.min(endY,mainRoom.getTop());
                            case DOWN -> (j >= mainRoom.getBottom() && j < mainRoom.getBottom() + t) && i > Math.max(startX,mainRoom.getLeft()) && i < Math.min(endX,mainRoom.getRight());
                            case UP -> (j >= mainRoom.getTop() - t && j < mainRoom.getTop()) && i > Math.max(startX, mainRoom.getLeft()) && i < Math.min(endX,mainRoom.getRight());
                        };

                boolean onSharedDoorway = onSharedEdgeBand && againstMainWallBand;
                boolean placeFloor;
                if (onMainFloor || onSharedDoorway) {
                    placeFloor = true;
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
    public Direction getDirectionOnMain() {return directionOnMain;}


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
