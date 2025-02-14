package game2048logic;

import game2048rendering.Board;
import game2048rendering.Side;
import game2048rendering.Tile;
import net.sf.saxon.functions.ConstantFunction;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;


/** The state of a game of 2048.
 *  @author P. N. Hilfinger + Josh Hug
 */
public class Model {
    /** Current contents of the board. */
    private final Board board;
    /** Current score. */
    private int score;

    /* Coordinate System: column x, row y of the board (where x = 0,
     * y = 0 is the lower-left corner of the board) will correspond
     * to board.tile(x, y).  Be careful!
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = 0;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (x, y) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score) {
        board = new Board(rawValues);
        this.score = score;
    }

    /** Return the current Tile at (x, y), where 0 <= x < size(),
     *  0 <= y < size(). Returns null if there is no tile there.
     *  Used for testing. */
    public Tile tile(int x, int y) {
        return board.tile(x, y);
    }

    /** Return the number of squares on one side of the board. */
    public int size() {
        return board.size();
    }

    /** Return the current score. */
    public int score() {
        return score;
    }


    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        board.clear();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        return maxTileExists() || !atLeastOneMoveExists();
    }

    /** Returns this Model's board. */
    public Board getBoard() {
        return board;
    }

    /** Check whether the tile is empty */
    public boolean is_empty(int x,int y) {
        return tile(x, y) == null;
    }


    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public boolean emptySpaceExists() {
        int board_size = size();
        for (int x = 0; x < board_size ; x++){
            for (int y = 0; y < board_size; y++){
                if (is_empty(x, y)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by this.MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public boolean maxTileExists() {
        int board_size = size();
        for (int x = 0; x < board_size ; x++) {
            for (int y = 0; y < board_size; y++) {
                if (is_empty(x, y)) {
                    continue;
                }
                if (tile(x, y).value() == MAX_PIECE) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Check whether the tile has the same value with neighbour tiles */
    public boolean can_move(Tile tile0, Tile tile1) {
        return tile0.value() == tile1.value();
    }


    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public boolean atLeastOneMoveExists() {
        if (emptySpaceExists()){
            return true;
        }

        int board_size = size();
        for (int x = 0;x < board_size; x++) {
            for (int y =0; y < board_size; y++) {
                Tile current_tile = tile(x, y);

                // if current tile is right-most tile
                if (x == board_size - 1) {
                    // if it is the top-right tile
                    if (y == board_size - 1) {
                        continue;
                    }
                    Tile tile0 = tile(x, y + 1); // only need to check above tile
                    if (can_move(current_tile, tile0)) {
                        return true;
                    }
                }
                else {
                    // if current tile is not the top-most tile and not right-most tile
                    if (y < board_size - 1) {
                        Tile tile0 = tile(x, y + 1), tile1 = tile(x + 1, y); // need to check both above tile and RHS tile
                        if (can_move(current_tile, tile0) || can_move(current_tile, tile1)) {
                            return true;
                        }
                    }
                    // if current tile is the top-most tile and not right-most tile
                    else {
                        Tile tile1 = tile(x + 1, y); // only need to check the RHS tile
                        if (can_move(current_tile, tile1)) {
                            return true;
                        }
                    }
                }

            }
        }

        return false;
    }


    /** Getting all the tiles position based on the input, if the x is -1 means getting the column,
     * and if the y is -1 means getting the row. One of them have to be -1.
     */
    public List<int[]> get_row_or_column(int row, int column) {
        int i = 0, size = board.size();
        List<int[]> lst_positions = new ArrayList<>();
        if (row == -1) {
            // get the row tiles position
            while (i < size) {
                if (!is_empty(i, column)) {
                    lst_positions.add(new int[]{i, column});
                }
                i++;
            }
        }
        else if (column == -1) {
            // get the column tiles position
            while (i < size) {
                if (!is_empty(row, i)) {
                    lst_positions.add(new int[]{row, i});
                }
                i++;
            }

        }

        return lst_positions;
    }

    /** increment the score*/
    public void increment_score(int x) {
        this.score += x;
    }

    /**
     * Moves the tile at position (x, y) as far up as possible.
     *
     * Rules for Tilt:
     * 1. If two Tiles are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     */
    public void moveTileUpAsFarAsPossible(int x, int y) {
        Tile currTile = board.tile(x, y);
        int myValue = currTile.value();
        int targetY = y, board_size = board.size();
        if (targetY == board_size - 1) {
            return;
        }

        // move upper until there is tile or reach the edge
        while (targetY < board_size - 1) {
            // upper tile is not empty
            if (!is_empty(x , targetY + 1)) {
                Tile upper_tile = board.tile(x, targetY + 1);
                int upper_value = upper_tile.value();
                // if they have the same value, move and merge those two tile
                if (myValue == upper_value && !upper_tile.wasMerged()) {
                    targetY += 1;
                    increment_score(myValue * 2);
                    board.move(x , targetY, currTile);
                    return;
                } else {
                    break;
                }
            }
            // upper tile is empty, looking for next tile
            else {
                targetY += 1;
            }
        }
;
        // got the target Y we can move the tile now
        board.move(x, targetY, currTile);
    }

    /** Handles the movements of the tilt in column x of the board
     * by moving every tile in the column as far up as possible.
     * The viewing perspective has already been set,
     * so we are tilting the tiles in this column up.
     * */
    public void tiltColumn(int x) {
        List<int[]> column_tiles = get_row_or_column(x, -1); // get the tiles on column x
        int size_of_column_tiles = column_tiles.size();

        for (int i = size_of_column_tiles - 1; i >= 0; i--) {
            int m = column_tiles.get(i)[0], n = column_tiles.get(i)[1];
            moveTileUpAsFarAsPossible(m, n);
        }
    }

    public void tilt(Side side) {
        // TODO: Tasks 8 and 9. Fill in this function.
    }

    /** Tilts every column of the board toward SIDE.
     */
    public void tiltWrapper(Side side) {
        board.resetMerged();
        tilt(side);
    }


    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int y = size() - 1; y >= 0; y -= 1) {
            for (int x = 0; x < size(); x += 1) {
                if (tile(x, y) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(x, y).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (game is %s) %n", score(), over);
        return out.toString();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Model m) && this.toString().equals(m.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
