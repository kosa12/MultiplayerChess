package gamesrc.board;

public class BoardUtils {

    public static final boolean[] FIRST_COLUMN = initCol(0);
    public static final boolean[] SECOND_COLUMN = initCol(1);
    public static final boolean[] SEVENTH_COLUMN = initCol(6);
    public static final boolean[] EIGHTH_COLUMN = initCol(7);

    public static final boolean[] SECOND_ROW = null;
    public static final boolean[] SEVENTH_ROW = null;

    public static final int NUM_TILES = 64;
    public static final int NUM_TILES_PER_ROW = 8;

    private static boolean[] initCol(int colNum){
        final boolean[] col = new boolean[NUM_TILES];
        do {
            col[colNum] = true;
            colNum += NUM_TILES_PER_ROW;
        } while(colNum<NUM_TILES);

        return col;
    }

    private BoardUtils(){
        throw new RuntimeException("LoL, you tried");
    }

    public static boolean isValidTileCoord(final int coord) {
        return coord >= 0 && coord < NUM_TILES;
    }


}
