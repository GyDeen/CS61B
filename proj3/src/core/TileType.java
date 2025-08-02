package core;

import tileengine.TETile;
import tileengine.Tileset;

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

    public TETile toTETile() {
        switch (this) {
            case FLOOR: return Tileset.FLOOR;
            case UNLOCKED_DOOR: return Tileset.UNLOCKED_DOOR;
            case GRASS: return Tileset.GRASS;
            case FLOWER: return Tileset.FLOWER;
            case SAND: return Tileset.SAND;
            case TREE: return Tileset.TREE;
            case WATER: return Tileset.WATER;
            case MOUNTAIN: return Tileset.MOUNTAIN;
            case LOCKED_DOOR: return Tileset.LOCKED_DOOR;
            case WALL: return Tileset.WALL;
            default: throw new IllegalArgumentException("Unrecognized tile type: " + this);
        }
    }
}
