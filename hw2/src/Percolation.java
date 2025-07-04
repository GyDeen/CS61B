import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import java.util.concurrent.ThreadLocalRandom;

public class Percolation {

    private final int n;
    private final int virtualTop;
    private final int virtualBottom;

    // one UF for percolates()
    private final WeightedQuickUnionUF uf;
    // one UF for fullness
    private final WeightedQuickUnionUF fullUf;

    private final boolean[][] open;
    private int openCount = 0;

    public Percolation(int n) {
        if (n <= 0) throw new IllegalArgumentException();
        this.n = n;

        // +2 for top and bottom in the first UF
        uf = new WeightedQuickUnionUF(n * n + 2);
        // +1 for top only in the second UF
        fullUf = new WeightedQuickUnionUF(n * n + 1);

        virtualTop = n * n;
        virtualBottom = n * n + 1;

        open = new boolean[n][n];
    }

    public void open(int row, int col) {
        validate(row, col);
        if (open[row][col]) return;

        open[row][col] = true;
        openCount++;

        int idx = index(row, col);

        // connect to neighbours that are already open
        for (int[] d : new int[][]{{-1,0},{1,0},{0,-1},{0,1}}) {
            int r = row + d[0], c = col + d[1];
            if (inBounds(r, c) && open[r][c]) {
                uf.union(idx, index(r, c));
                fullUf.union(idx, index(r, c));
            }
        }

        if (row == 0) {
            uf.union(idx, virtualTop);
            fullUf.union(idx, virtualTop);
        }

        if (row == n - 1) {
            uf.union(idx, virtualBottom);
        }
    }

    public boolean isOpen(int row, int col)  { validate(row, col); return open[row][col]; }
    public boolean isFull(int row, int col)  { validate(row, col); return fullUf.connected(index(row,col), virtualTop); }
    public int     numberOfOpenSites()       { return openCount; }
    public boolean percolates()              { return uf.connected(virtualTop, virtualBottom); }

    private int index(int row, int col)      { return row * n + col; }
    private boolean inBounds(int r,int c)    { return r >= 0 && r < n && c >= 0 && c < n; }
    private void validate(int r,int c) {if (!inBounds(r,c)) throw new IllegalArgumentException("row="+r+", col="+c);}
}
