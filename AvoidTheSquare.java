import java.util.Arrays;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
public class AvoidTheSquare {
    public static final int GRID_SIZE = 5;
    public static final int GRID_TOTAL_CELLS = GRID_SIZE * GRID_SIZE;
    public static void main(String[] args) {
        int checked = 0;
        int solutions = 0;
        Grid grid = new Grid();
        for (int i = 0; i < Math.pow(2, GRID_TOTAL_CELLS); i++) {
            grid.inc();
            Set<Index[]> squares = grid.computeSquares(true);
            if (squares.isEmpty()) {
                System.out.println(grid);
                solutions++;
            }
            if (checked % 1000000 == 0) {
                System.out.println("Checked " + checked + " grids.");
            }
            checked++;
        }
        System.out.println("Total solutions: " + solutions);
    }
    public static class Index {
        public final int row, col;
        public Index(int index) {
            row = index / GRID_SIZE;
            col = index % GRID_SIZE;
        }
        public Index(int row, int col) { this.row = row; this.col = col; }
        public Index sub(Index other) {
            return new Index(this.row - other.row, this.col - other.col);
        }
        public Index sub(int otherIdx) {
            Index other = new Index(otherIdx);
            return sub(other);
        }
        public Index add(Index other) {
            return new Index(this.row + other.row, this.col + other.col);
        }
        public static Index sub(int idxA, int idxB) {
            return new Index(idxA).sub(idxB);
        }
        public Index l90() { return new Index(-this.col, this.row); }
        public Index turn(Index other) { return add(sub(other).l90()); }
        public Index turn(int other) {
            return add(sub(new Index(other)).l90());
        }
        public static Index turn(int idx, Index other) {
            return new Index(idx).turn(other);
        }
        public boolean inBounds() {
            return row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE;
        }
        public IndexPair squareCoords(Index other) {
            Index idxC = turn(other);
            Index idxD = idxC.turn(this);
            return new IndexPair(idxC, idxD);
        }
        public String toString() { return "(" + row + ", " + col + ')'; }
    }
    public static Index index(int row, int col) { return new Index(row, col); }
    public static Index index(int idx) {
        return index(idx / GRID_SIZE, idx % GRID_SIZE);
    }
    public static class IndexPair {
        private final Index fst;
        private final Index snd;
        public IndexPair(Index fst, Index snd) {
            this.fst = fst;
            this.snd = snd;
        }
        public boolean bothInBounds() {
            return fst.inBounds() && snd.inBounds();
        }
    }
    public static int idx(Index index) {
        return GRID_SIZE * index.row + index.col;
    }
    public static int idx(int row, int col) {
        return GRID_SIZE * row + col;
    }
    public static class Grid {
        private BitSet cells;
        public BitSet getCells() { return cells; }
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int r = 0; r < GRID_SIZE; r++) {
                for (int c = 0; c < GRID_SIZE; c++) {
                    builder.append((cells.get(idx(r, c)) ? "RR" : "GG") + " ");
                }
                builder.append('\n');
            }
            return builder.toString();
        }
        public Grid() { this.cells = new BitSet(GRID_TOTAL_CELLS); }
        public Grid(BitSet cells) { this.cells = cells; }
        public boolean get(int index) { return cells.get(index); }
        public boolean get(Index index) { return cells.get(idx(index)); }
        public void set(int index, boolean cell) { cells.set(index, cell); }
        public void set(Index index, boolean cell) { set(idx(index), cell); }
        public Grid with(int index, boolean cell) {
            set(index, cell);
            return this;
        }
        private boolean inBoundsWithType(IndexPair indexes, boolean type) {
            return indexes != null && indexes.bothInBounds()
                && cells.get(idx(indexes.fst)) == type
                && cells.get(idx(indexes.snd)) == type;
        }
        public void inc() {
            do { inc(0); }
            while (cells.cardinality() != GRID_TOTAL_CELLS / 2);
        }
        public void inc(int index) {
            if(!cells.get(index)) cells.set(index);
            else {
                cells.set(index, false);
                inc(index + 1);
            }
        }
        public Set<Index[]> computeSquares() { return computeSquares(false); }
        public Set<Index[]> computeSquares(boolean returnFirstOnly) {
            Set<Index[]> squares = new HashSet<>();
            for (int color = 0; color <= 1; color++) {
                boolean cellType = color == 0;
                for (int idxA = 0; idxA < GRID_TOTAL_CELLS; idxA++) {
                    if (get(idxA) != cellType) continue;
                    for (int idxB = idxA + 1; idxB < GRID_TOTAL_CELLS; idxB++) {
                        if (get(idxB) != cellType) continue;
                        IndexPair lr = index(idxA).squareCoords(index(idxB));
                        if (inBoundsWithType(lr, cellType)) {
                            squares.add(new Index[] {
                                index(idxA), index(idxB), lr.fst, lr.snd
                            });
                            if (returnFirstOnly) return squares;
                        }
                        IndexPair rl = index(idxB).squareCoords(index(idxA));
                        if (inBoundsWithType(rl, cellType)) {
                            squares.add(new Index[] {
                                index(idxA), index(idxB), rl.fst, rl.snd
                            });
                            if (returnFirstOnly) return squares;
                        }
                    }
                }
            }
            return squares;
        }
    }
}
