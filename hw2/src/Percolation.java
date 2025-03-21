import edu.princeton.cs.algs4.WeightedQuickUnionUF;


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

    public void open(int row, int col) {
        // TODO: Fill in this method.
    }

    public boolean isOpen(int row, int col) {
        // TODO: Fill in this method.
        return false;
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
