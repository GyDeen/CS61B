package core;

import utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static core.Config.*;
import static java.lang.Math.clamp;

public class MainRoom extends Room {
    // Only initialise when there is a subroom attach to this room
    private ArrayList<SubRoom> subRooms;



    public MainRoom(int height, int width, int x, int y, int thicknessOfWall, boolean isCornered) {
        super(height, width, x, y, thicknessOfWall, isCornered);
    }


    /** Factory: generate a main room */
    public static MainRoom generate(int idealSize, Random random) {
        double aspectRatio = random.nextDouble(0.8, 1.3);
        int width  = (int) Math.sqrt(idealSize * aspectRatio);
        int height = (int) (idealSize / (double) width);

        width  += RandomUtils.uniform(random, -2, 3);
        height += RandomUtils.uniform(random, -2, 3);

        width  = clamp(width,  MIN_MAIN_ROOM_WIDTH,  MAX_MAIN_ROOM_WIDTH);
        height = clamp(height, MIN_MAIN_ROOM_HEIGHT, MAX_MAIN_ROOM_HEIGHT);

        int x = RandomUtils.uniform(random, 1, WINDOW_WIDTH - width / 2 - 1);
        int y = RandomUtils.uniform(random, 1, WORLD_HEIGHT - height / 2 - 1);

        int wallThickness = (random.nextDouble() < WALL_THICKNESS_1_PROBABILITY) ? BLOCK_WIDTH1 : BLOCK_WIDTH2;
        boolean isCornered = random.nextInt(100) % 4 != 0;

        MainRoom room = new MainRoom(height, width, x, y, wallThickness, isCornered);
        room.getRandomPassable(random);
        room.getRandomImpassable(random);
        return room;
    }



    /** Attach the input subRoom to current MainRoom
     * @param subRoom the subRoom that attach to current MainRoom */
    public void attachRoom(SubRoom subRoom) {
        if (subRooms == null) subRooms = new ArrayList<>();
        subRooms.add(subRoom);
    }


    /** Getter for subrooms, return a safe copy of it */
    public List<SubRoom> getSubRooms() {
        if (subRooms == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(subRooms);
    }


    /** Return the left edge x based on given y
     * @param n the axis of given direction line (y for horizontal direction; x for vertical direction)
     * @param direction edge's direction */
    public int getEdgeOn(int n, Direction direction) {
        int edge;
        switch (direction) {
            case LEFT: {
                edge = getLeft(); // start with main room
                if (subRooms != null) {
                    // consider any subroom that covers this row
                    for (SubRoom s : subRooms) {
                        if (n > s.getBottom() && n < s.getTop()) {
                            edge = Math.min(edge, s.getLeft());
                        }
                    }
                }
                return edge;
            }
            case RIGHT: {
                edge = getRight();
                if (subRooms != null) {
                    for (SubRoom s : subRooms) {
                        if (n > s.getBottom() && n < s.getTop()) {
                            edge = Math.max(edge, s.getRight());
                        }
                    }
                }
                return edge;
            }
            case UP: {
                edge = getTop();
                if (subRooms != null) {
                    for (SubRoom s : subRooms) {
                        if (n > s.getLeft() && n < s.getRight()) {
                            edge = Math.max(edge, s.getTop());
                        }
                    }
                }
                return edge;
            }
            case DOWN: {
                edge = getBottom();
                if (subRooms != null) {
                    for (SubRoom s : subRooms) {
                        if (n > s.getLeft() && n < s.getRight()) {
                            edge = Math.min(edge, s.getBottom());
                        }
                    }
                }
                return edge;
            }
            default:
                throw new IllegalArgumentException("Unknown direction: " + direction);
        }
    }
}
