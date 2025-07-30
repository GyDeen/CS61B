package core;

public enum TileType {
    FLOOR(true, 5),
    UNLOCKED_DOOR(true, 5),
    GRASS(true, 4),
    FLOWER(true, 4),
    SAND(true, 2),
    TREE(true, 2),
    WATER(true, 1),
    MOUNTAIN(false, Integer.MAX_VALUE),
    LOCKED_DOOR(false, Integer.MAX_VALUE),
    WALL(false, Integer.MAX_VALUE);

    public final boolean passable;
    public final int movementCost;

    TileType(boolean passable, int movementCost) {
        this.passable = passable;
        this.movementCost = movementCost;
    }
}
