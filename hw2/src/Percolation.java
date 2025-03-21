import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import java.util.concurrent.ThreadLocalRandom;

import java.util.Random;


public class Percolation {
    private final int IS_FULL = 1;
    private final int IS_EMPTY = -1;
    private final int IS_BLOCK = 2;
    private final int NOT_OPEN = 0;


    private int boardSize;
    private boolean isPercolation;
    private WeightedQuickUnionUF boardUF;
    private int[][] grid;


    public Percolation(int N) {
        boardSize = N;
        boardUF = new WeightedQuickUnionUF(N);
        grid = new int[N][N];
    }

    public int getRandomStatement(int row) {
        if (row == 0) {
            int[] validTopRow = {IS_BLOCK, IS_EMPTY, IS_FULL};
            return validTopRow[ThreadLocalRandom.current().nextInt(validTopRow.length)];
        } else {
            // For other rows, only BLOCKED or EMPTY
            int[] validOtherRows = {IS_BLOCK, IS_EMPTY};
            return validOtherRows[ThreadLocalRandom.current().nextInt(validOtherRows.length)];
        }
    }


    private int getUFIdx(int row, int col) {
        return row * boardSize + col;
    }

    public void open(int row, int col) {

        if (row < 0 || row >= boardSize || col < 0 || col >= boardSize) throw new IllegalArgumentException("Index out of bounds: (" + row + ", " + col + ")");


        // If it is already opened, do nothing
        if (isOpen(row, col)) {
            return;
        }

        int idxUF = getUFIdx(row, col);
        grid[row][col] = getRandomStatement(row);

        // If it is not the first row
        if (row > 0 && grid[row - 1][col] != NOT_OPEN) {
            boardUF.union(idxUF, getUFIdx(row - 1, col));
        }

        if (row < boardSize - 1 && grid[row + 1][col] != NOT_OPEN) {
            boardUF.union(idxUF, getUFIdx(row + 1, col));
        }

        if (col > 0 && grid[row][col - 1] != NOT_OPEN) {
            boardUF.union(idxUF, getUFIdx(row, col - 1));
        }
        if (col < boardSize - 1 && grid[row][col + 1] != NOT_OPEN) {
            boardUF.union(idxUF, getUFIdx(row, col + 1));
        }




    }

    public boolean isOpen(int row, int col) {
        return grid[row][col] != NOT_OPEN;
    }

    public boolean isFull(int row, int col) {
        // TODO: Fill in this method.
        return false;
    }

    public int numberOfOpenSites() {
        // TODO: Fill in this method.
        return 0;
    }

    public boolean percolates() {
        // TODO: Fill in this method.
        return false;
    }

    // TODO: Add any useful helper methods (we highly recommend this!).
    // TODO: Remove all TODO comments before submitting.

}
