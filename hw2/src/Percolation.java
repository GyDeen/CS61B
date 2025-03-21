import edu.princeton.cs.algs4.WeightedQuickUnionUF;


public class Percolation {
    private final int IS_FULL = 1;
    private final int IS_BLOCK = -1;
    private final int IS_EMPTY = 0;
    private final int IS_OPEN = 2;
    private final int NOT_OPEN = -2;


    private final int boardSize;
    private boolean isPercolation;
    private WeightedQuickUnionUF board;

    private boolean isTop = false;
    private int statement = NOT_OPEN; // Statement for whether is full, block or empty
    private int x;
    private int y;



        public void setStatement(int i) {
            statement = i;
        }

        public int getStatement() {
            return statement;
        }

        public int getBoardIdx() {

        }
    }
    public Percolation(int N) {
        isPercolation = false;
        boardSize = N;
        board = new WeightedQuickUnionUF(N);
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
