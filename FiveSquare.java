import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
public class FiveSquare {
    public static final int GRID_SIZE = 5;
    public static final int GRID_TOTAL_CELLS = GRID_SIZE * GRID_SIZE;
    public static void main(String[] args) {
        /*
        Grid grid = new Grid();
        grid.set(index(0            , 0            ), Cell.BROWN);
        grid.set(index(0            , GRID_SIZE - 1), Cell.BROWN);
        grid.set(index(GRID_SIZE - 1, 0            ), Cell.BROWN);
        grid.set(index(GRID_SIZE - 1, GRID_SIZE - 1), Cell.BROWN);
        System.out.println(grid);
        Set<Index[]> squares = grid.computeSquares();
        for (Index[] square : squares) {
            System.out.print("[ ");
            for (Index index : square)
                System.out.print(index + " ");
            System.out.println(']');
        }
        */
        drawSearch();
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
        public static Index turn(int idx, int other) {
            return Index.turn(idx, new Index(other));
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
    public enum Cell {
        EMPTY, BROWN, GREEN;
        public String toString() {
            switch (this) {
                case EMPTY: return "  ";
                case BROWN: return "RR";
                case GREEN: return "GG";
            }
            throw new IllegalStateException("Invalid Cell");
        }
    }
    public static class Grid {
        private final Cell[] cells;
        public Cell[] getCells() { return cells; }
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int r = 0; r < GRID_SIZE; r++) {
                for (int c = 0; c < GRID_SIZE; c++) {
                    builder.append(cells[idx(r, c)] + " ");
                }
                builder.append('\n');
            }
            return builder.toString();
        }
        public Grid() {
            this.cells = new Cell[GRID_SIZE * GRID_SIZE];
            for (int i = 0; i < GRID_SIZE * GRID_SIZE; i++) {
                cells[i] = Cell.EMPTY;
            }
        }
        public Grid(Cell[] cells) { this.cells = cells; }
        public Cell get(int index) { return this.cells[index]; }
        public Cell get(Index index) { return this.cells[idx(index)]; }
        public void set(int index, Cell cell) { this.cells[index] = cell; }
        public void set(Index index, Cell cell) {
            this.cells[idx(index)] = cell;
        }
        public Grid with(int index, Cell cell) {
            set(index, cell);
            return this;
        }
        private boolean inBoundsWithType(IndexPair indexes, Cell type) {
            return indexes != null && indexes.bothInBounds()
                && cells[idx(indexes.fst)] == type
                && cells[idx(indexes.snd)] == type;
        }
        public Set<Index[]> computeSquares() { return computeSquares(false); }
        public Set<Index[]> computeSquares(boolean returnFirstOnly) {
            Set<Index[]> squares = new HashSet<>();
            for (Cell cellType : Cell.values()) {
                if (cellType == Cell.EMPTY) continue;
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
        @Override
        public int hashCode() { return cells.hashCode(); }
        @Override
        public boolean equals(Object other) {
            return other instanceof Grid &&
                Arrays.equals(cells, ((Grid) other).getCells());
        }
        public ArrayList<Grid> daughters() {
            ArrayList<Grid> daughters = new ArrayList<>();
            for (int idx = 0; idx < cells.length; idx++) {
                if (cells[idx] != Cell.EMPTY) continue;
                for (Cell newToken : Cell.values()) {
                    if (newToken == Cell.EMPTY) continue;
                    daughters.add(new Grid(Arrays.copyOf(cells, cells.length))
                            .with(idx, newToken));
                }
            }
            return daughters;
        }
    }
    public static /*HashSet<Grid>*/void drawSearch() {
        int checked = 0;
        //HashSet<Grid> solutions = new HashSet<>();
        ArrayList<Grid> todo = new ArrayList<>();
        todo.add(new Grid());
        while (!todo.isEmpty()) {
            Grid current = todo.remove(todo.size() - 1);
            Set<Index[]> squares = current.computeSquares(true);
            if (!squares.isEmpty()) continue;
            ArrayList<Grid> daughters = current.daughters();
            if (daughters.isEmpty()) System.out.println(current);//solutions.add(current);
            else todo.addAll(daughters);
            checked++;
            if (checked > 999999) {
                checked = 0;
                System.out.print(".");
            }
        }
        System.out.println();
        //return solutions;
    }
    // Right now we're checking unfinished games, which is unnecessary.
    // We can simplify the grid to an (equivalent of an) array of booleans with 
    // 13 trues and 12 falses. Then we have far fewer states to check, and each
    // state is more compact - we can get a state into an int32.
}
