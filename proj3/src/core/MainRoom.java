package core;

import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static core.Config.*;
import static java.lang.Math.clamp;

public class MainRoom extends Room {
    // Only initialise when there is a subroom attach to this room
    private ArrayList<SubRoom> subRooms;



    private MainRoom(int height, int width, int x, int y, int thicknessOfWall, boolean isCornered) {
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


    /** Return true if the given position belongs to this room (including wall part of the room) */
    public boolean isInRoom(int x, int y) {
        int startX = getLeft();
        int startY = getBottom();
        int endX = startX + getWidth();
        int endY = startY + getHeight();
        int t = getThicknessOfWall();

        if (x >= startX + t && x < endX - t
                && y >= startY + t && y < endY - t) {
            return true;
        }

        if (subRooms != null) {
            for (SubRoom subRoom : subRooms) {
                int sx = subRoom.getLeft();
                int sy = subRoom.getBottom();
                int ex = sx + subRoom.getWidth();
                int ey = sy + subRoom.getHeight();
                int st = subRoom.getThicknessOfWall();

                if (x >= sx + st && x < ex - st
                        && y >= sy + st && y < ey - st) {
                    return true;
                }
            }
        }

        return false;
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
                edge = getRight() - 1;
                if (subRooms != null) {
                    for (SubRoom s : subRooms) {
                        if (n > s.getBottom() && n < s.getTop()) {
                            edge = Math.max(edge, s.getRight() - 1);
                        }
                    }
                }
                return edge;
            }
            case UP: {
                edge = getTop() - 1;
                if (subRooms != null) {
                    for (SubRoom s : subRooms) {
                        if (n > s.getLeft() && n < s.getRight()) {
                            edge = Math.max(edge, s.getTop() - 1);
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


    private static MainRoom generateFullFIllRoom(TETile[][] world, int minX, int minY, int maxX, int maxY, int minW, int minH, int maxW, int maxH, Random random) {
        int w = RandomUtils.uniform(random, minW, maxW + 1);
        int h = RandomUtils.uniform(random, minH, maxH + 1);
        int halfW = w / 2, rightHalf = w - halfW;
        int halfH = h / 2, topHalf = h - halfH;

        int minRoomX = minX + halfW, maxRoomX = (maxX + 1) - rightHalf;
        int minRoomY = minY + halfH, maxRoomY = (maxY + 1) - topHalf;
        if (minRoomX > maxRoomX || minRoomY > maxRoomY) return null;

        int roomX = RandomUtils.uniform(random, minRoomX, maxRoomX + 1), roomY = RandomUtils.uniform(random, minRoomY, maxRoomY + 1);
        int left = roomX - halfW;
        int bottom = roomY - halfH;
        if (!rectIsNothing(left, bottom, w, h, world)) return null;
        boolean isCornered = random.nextBoolean();
        MainRoom filler = new MainRoom(h, w, roomX, roomY, 1, isCornered);
        filler.getRandomPassable(random);
        filler.getRandomImpassable(random);
        return filler;
    }


    public static void fullFillRooms(TETile[][] world, ArrayList<MainRoom> fullFillRooms, ArrayList<MainRoom> majorRooms, Random random) {
        int width = world.length, height = world[0].length;
        boolean[][] visited =  new boolean[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (world[x][y] != Tileset.NOTHING|| visited[x][y]) continue;

                // start counting the NOTHING area size
                int minX = x, maxX = x, minY = y, maxY = y, area = 0;
                ArrayDeque<Point> dq = new ArrayDeque<>();
                dq.add(new Point(x, y));
                visited[x][y] = true;

                while (!dq.isEmpty()) {
                    Point p = dq.removeFirst();
                    int currentX = p.x, currentY = p.y;
                    area++;

                    // Expand the NOTHING area
                    if (currentX < minX) minX = currentX;
                    if (currentX > maxX) maxX = currentX;
                    if (currentY < minY) minY = currentY;
                    if (currentY > maxY) maxY = currentY;


                    // Explore the neighbour tiles
                    if (currentX > 0 && !visited[currentX - 1][currentY] && world[currentX - 1][currentY] == Tileset.NOTHING) {
                        visited[currentX - 1][currentY] = true;
                        dq.add(new Point(currentX - 1, currentY));
                    }

                    // Add neighbour NOTHING tiles for further explore
                    if (currentX + 1 < width &&  !visited[currentX + 1][currentY] && world[currentX + 1][currentY] == Tileset.NOTHING) {
                        visited[currentX + 1][currentY] = true;
                        dq.add(new Point(currentX + 1, currentY));
                    }

                    if (currentY > 0 && !visited[currentX][currentY - 1] && world[currentX][currentY - 1] == Tileset.NOTHING) {
                        visited[currentX][currentY - 1] = true;
                        dq.add(new Point(currentX, currentY - 1));
                    }

                    if (currentY + 1 < height && !visited[currentX][currentY + 1] && world[currentX][currentY + 1] == Tileset.NOTHING) {
                        visited[currentX][currentY + 1] = true;
                        dq.add(new Point(currentX, currentY + 1));
                    }

                    if (area < MIN_VOID_AREA) continue;

                    // Have each fulfill room around 40 size big and leave some room for hallway
                    int numberOfRoom = (int) Math.max(1, (double) area / MIN_VOID_AREA);
                    int maxAttempt = 1000, placedRoom = 0;
                    int boxW = maxX - minX + 1, boxH = maxY - minY + 1;
                    int roomMinW = MIN_FILL_ROOM_WIDTH, roomMaxW = Math.min(MAX_FILL_ROOM_WIDTH, boxW);
                    int roomMinH = MIN_FILL_ROOM_HEIGHT, roomMaxH = Math.min(MAX_FILL_ROOM_HEIGHT, boxH);
                    if (roomMinW > roomMaxW || roomMinH > roomMaxH) continue;

                    while (placedRoom < numberOfRoom && maxAttempt -- > 0) {
                        MainRoom fullFillRoom = generateFullFIllRoom(world, minX, minY, maxX, maxY, roomMinW, roomMinH, roomMaxW, roomMaxH, random);
                        if (fullFillRoom == null) continue;
                        if (!Room.validRoom(fullFillRoom, majorRooms, null)) continue;
                        if (!Room.validRoom(fullFillRoom, fullFillRooms, null)) continue;

                        fullFillRooms.add(fullFillRoom);
                        System.out.println("Successfully added room "   );
                        placedRoom++;
                    }
                }

            }
        }
    }


    /* Return true iff the given area only has NOTHING tile */
    private static boolean rectIsNothing(int left, int bottom, int width, int height, TETile[][] world) {
        if (left < 0 || bottom < 0) return false;
        if (left + width > world[0].length || bottom + height > world.length) return false;

        for (int i = left; i < left+width; i++) {
            for (int j = bottom; j < bottom+height; j++) {
                if (world[i][j] != Tileset.NOTHING) return false;
            }
        }
        return true;
    }


    /** Return the distance between two rooms */
    public static double distanceBetween(MainRoom a, MainRoom b) {
        return Math.sqrt(Math.pow(Math.abs(a.getLocation().x - b.getLocation().x), 2) + Math.pow(Math.abs(a.getLocation().y - b.getLocation().y), 2));
    }
}
