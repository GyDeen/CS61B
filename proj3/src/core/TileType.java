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
    HALLWAYFLOOR(true, 5),
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
        return switch (this) {
            case FLOOR, HALLWAYFLOOR -> Tileset.FLOOR;
            case UNLOCKED_DOOR -> Tileset.UNLOCKED_DOOR;
            case GRASS -> Tileset.GRASS;
            case FLOWER -> Tileset.FLOWER;
            case SAND -> Tileset.SAND;
            case TREE -> Tileset.TREE;
            case WATER -> Tileset.WATER;
            case MOUNTAIN -> Tileset.MOUNTAIN;
            case LOCKED_DOOR -> Tileset.LOCKED_DOOR;
            case WALL -> Tileset.WALL;
            default -> throw new IllegalArgumentException("Unrecognized tile type: " + this);
        };
    }
}
