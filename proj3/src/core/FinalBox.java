package core;

import tileengine.TETile;
import tileengine.TileType;

import java.util.Random;

import static core.Config.WIN;

public class FinalBox extends LootBox {

    /* Using the top left tile as the position with 2 x 2 size */
    private FinalBox(MainRoom room, int x, int y, int width, int height) {
        super(room, x, y, width, height);
        setImagePath("resources/loot box/Final Box");
    }

    public FinalBox generateFinalBox(MainRoom initialRoom, Random rand, TETile[][] world) {
        int maxAttempt = Config.MAX_ATTEMPT_PIVOT;

        int minX = initialRoom.getLeft();
        int minY = initialRoom.getBottom();
        int maxX = initialRoom.getRight();
        int maxY = initialRoom.getTop();

        for (SubRoom s : initialRoom.getSubRooms()) {
            minX = Math.min(minX, s.getLeft());
            maxX = Math.max(maxX, s.getRight());
            minY = Math.min(minY, s.getBottom());
            maxY = Math.max(maxY, s.getTop());
        }

        for (int i = 0; i < maxAttempt; i++) {
            int x = rand.nextInt(minX, maxX);
            int y = rand.nextInt(minY, maxY);

            if (initialRoom.isInRoom(x, y) && TileType.toType(world[x][y]).isPassable()) return new FinalBox(initialRoom, x, y, 1, 1);
        }

        // Starting from the center of the initial room to different direction to find the first available spot
        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                if (initialRoom.isInRoom(x, y)
                        && TileType.toType(world[x][y]).isPassable()) {
                    return new FinalBox(initialRoom, x, y, 1, 1);
                }
            }
        }

        return new FinalBox(initialRoom, initialRoom.getLocation().x, initialRoom.getLocation().y, 2, 2);
    }

    public int update(World world) {
        return WIN;
    }
}
