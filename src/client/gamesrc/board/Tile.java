package client.gamesrc.board;
import com.google.common.collect.ImmutableMap;

import client.gamesrc.pieces.Piece;

import java.util.HashMap;
import java.util.Map;

public abstract class Tile {
    protected final int tileCoordinate;
    private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createAllPossibleEmptyTiles();


    private static Map<Integer, EmptyTile> createAllPossibleEmptyTiles() {

        final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();
        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
            emptyTileMap.put(i, new EmptyTile(i));
        }
        return ImmutableMap.copyOf(emptyTileMap);
    }


    /**
     * A constructor, creates a Tile based on input parameters     *
     *
     * @param tileCoordinate coordinate at which a tile should be created
     * @param piece          a piece which should be put on that tile
     * @return if a piece is not null, calls OccupiedTile constructor, otherwise marks tile as empty
     */
    public static Tile createTile(final int tileCoordinate, final Piece piece) {
        return piece != null ? new OccupiedTile(tileCoordinate, piece) : EMPTY_TILES_CACHE.get(tileCoordinate);
    }


    private Tile(int tileCoordinate) {
        this.tileCoordinate = tileCoordinate;
    }
    public abstract Piece getPiece();
    public abstract boolean isTileOccupied();

    public int getTileCoordinate() {
        return this.tileCoordinate;
    }


    public static final class EmptyTile extends Tile {

        private EmptyTile(final int coordinate) {
            super(coordinate);
        }

        @Override
        public boolean isTileOccupied() {
            return false;
        }

        @Override
        public Piece getPiece() {
            return null;
        }

        @Override
        public String toString() {
            return "-";
        }
    }


    public static final class OccupiedTile extends Tile {

        private final Piece pieceOnTile;

        private OccupiedTile(final int coordinate, Piece pieceOnTile) {
            super(coordinate);
            this.pieceOnTile = pieceOnTile;
        }

        @Override
        public boolean isTileOccupied() {
            return true;
        }

        @Override
        public Piece getPiece() {
            return pieceOnTile;
        }

        @Override
        public String toString() {
            return getPiece().getPieceAlliance().isWhite() ? getPiece().toString().toLowerCase() : getPiece().toString();
        }
    }


}