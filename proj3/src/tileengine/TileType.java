package tileengine;

public enum TileType {
    FLOOR(true, 5),
    UNLOCKED_DOOR(true, 5),
    GRASS(true, 4),
    FLOWER(true, 4),
    SAND(true, 2),
    TREE(true, 2),
    WATER(true, 1),
    COIN(true, 1),
    MOUNTAIN(false, Integer.MAX_VALUE),
    LOCKED_DOOR(false, Integer.MAX_VALUE),
    WALL(false, Integer.MAX_VALUE),
    BOX(false, Integer.MAX_VALUE),
    NOTHING(false, Integer.MAX_VALUE);


    private final boolean passable;
    private final int movementCost;

    TileType(boolean passable, int movementCost) {
        this.passable = passable;
        this.movementCost = movementCost;
    }

    public TETile toTETile() {
        return switch (this) {
            case FLOOR, COIN -> Tileset.FLOOR;
            case UNLOCKED_DOOR -> Tileset.UNLOCKED_DOOR;
            case GRASS -> Tileset.GRASS;
            case FLOWER -> Tileset.FLOWER;
            case SAND -> Tileset.SAND;
            case TREE -> Tileset.TREE;
            case WATER -> Tileset.WATER;
            case MOUNTAIN -> Tileset.MOUNTAIN;
            case LOCKED_DOOR -> Tileset.LOCKED_DOOR;
            case WALL -> Tileset.WALL;
            case BOX -> Tileset.CELL;
            default -> throw new IllegalArgumentException("Unrecognized tile type: " + this);
        };
    }


    /** Convert input tile into TileType */
    public static TileType toType(TETile tile) {
        if (tile == Tileset.FLOOR) return FLOOR;
        if (tile == Tileset.UNLOCKED_DOOR) return UNLOCKED_DOOR;
        if (tile == Tileset.GRASS) return GRASS;
        if (tile == Tileset.FLOWER) return FLOWER;
        if (tile == Tileset.SAND) return SAND;
        if (tile == Tileset.TREE) return TREE;
        if (tile == Tileset.WATER) return WATER;
        if (tile == Tileset.MOUNTAIN) return MOUNTAIN;
        if (tile == Tileset.LOCKED_DOOR) return LOCKED_DOOR;
        if (tile == Tileset.WALL) return WALL;
        if (tile == Tileset.CELL) return BOX;
        if (tile == Tileset.NOTHING) return NOTHING;
        throw new IllegalArgumentException("Unrecognized tile: " + tile);
    }

    public boolean isPassable(){
        return passable;
    }
}
