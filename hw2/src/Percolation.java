import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import java.util.concurrent.ThreadLocalRandom;

public class Percolation {
    private final int IS_FULL = 3;
    private final int IS_EMPTY = 1;
    private final int NOT_OPEN = 0;


    private final int boardSize;
    private final WeightedQuickUnionUF boardUF;
    private final int virtualTop; // Any site connects to this means they are full
    private final int virtualBottom; // Any site connects with virtualTop and virtualBottom means this module is percolation
    private final int[][] grid;
    private int openSite = 0;



    public Percolation(int N) {
        boardSize = N;
        /* Using two extra sites to represents connected to top and connected to bottom.
        *  This can avoid looping through the whole top row or bottom row */
        boardUF = new WeightedQuickUnionUF(N * N + 2);

        virtualTop = boardSize * boardSize;
        virtualBottom = boardSize * boardSize + 1;
        grid = new int[N][N];
    }



    public void open(int row, int col) {

        if (row < 0 || row >= boardSize || col < 0 || col >= boardSize) throw new IllegalArgumentException("Index out of bounds: (" + row + ", " + col + ")");


        // If it is already opened, do nothing
        if (isOpen(row, col)) {
            return;
        }

        int idxUF = getUFIdx(row, col);
        grid[row][col] = getRandomStatement(row);
        openSite++;

        // If it is row 0, make it connect with virtualTop
        if (row == 0) {
            boardUF.union(idxUF, virtualTop);
        }

        if (row == boardSize - 1) {
            boardUF.union(idxUF, virtualBottom);
        }

        // If it is not the first row
        if (row > 0 && grid[row - 1][col] != NOT_OPEN) {
            boardUF.union(idxUF, getUFIdx(row - 1, col));
        }

        // If it is not the bottom row
        if (row < boardSize - 1 && grid[row + 1][col] != NOT_OPEN) {
            boardUF.union(idxUF, getUFIdx(row + 1, col));
        }

        // If it is not the right-most column
        if (col > 0 && grid[row][col - 1] != NOT_OPEN) {
            boardUF.union(idxUF, getUFIdx(row, col - 1));
        }

        // If it is not the left-most column
        if (col < boardSize - 1 && grid[row][col + 1] != NOT_OPEN) {
            boardUF.union(idxUF, getUFIdx(row, col + 1));
        }



    }


    public boolean isOpen(int row, int col) {
        return grid[row][col] != NOT_OPEN;
    }

    // If this site is connecting with virtualTop, it is full
    public boolean isFull(int row, int col) {
        return boardUF.find(getUFIdx(row, col)) == boardUF.find(virtualTop);
    }

    public int numberOfOpenSites() {
        return openSite;
    }

    public boolean percolates() {
        return boardUF.connected(virtualTop, virtualBottom);
    }

    public int getRandomStatement(int row) {
        if (row == 0) {
            int[] validTopRow = {IS_FULL};
            return validTopRow[ThreadLocalRandom.current().nextInt(validTopRow.length)];
        } else {
            // For other rows, only BLOCKED or EMPTY
            int[] validOtherRows = {IS_EMPTY};
            return validOtherRows[ThreadLocalRandom.current().nextInt(validOtherRows.length)];
        }
    }


    private int getUFIdx(int row, int col) {
        return row * boardSize + col;
    }

}
