package gamesrc.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import gamesrc.Alliance;
import gamesrc.board.Board;
import gamesrc.board.BoardUtils;
import gamesrc.board.Move;
import gamesrc.board.Tile;
import gamesrc.board.Move.AttackMov;
import gamesrc.board.Move.MajorMov;

public class Queen extends Piece {
    private final static int[] CANDIDATE_MOVE_VECTOR_COORD = {-9,-8, -7,-1, 1, 7, 8, 9};

    public Queen(Alliance pieceAlliance, int piecePosition) {
        super(piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {

        final List<Move> legalMoves = new ArrayList<>();

        for (int candCoordOffset : CANDIDATE_MOVE_VECTOR_COORD) {
            int candDestCoord = this.piecePosition;

            while (BoardUtils.isValidTileCoord(candDestCoord)) {

                if (isFirstColumnExl(candDestCoord, candCoordOffset) || isEighthColumnExl(candDestCoord, candCoordOffset)) {
                    break;
                }

                candDestCoord += candCoordOffset;

                if (BoardUtils.isValidTileCoord(candDestCoord)) {
                    final Tile candDestTile = board.getTile(candDestCoord);

                    if (!candDestTile.isTileOccupied()) {
                        legalMoves.add(new MajorMov(board, this, candDestCoord));
                    } else {
                        final Piece pieceAtDest = candDestTile.getPiece();
                        final Alliance pieceAlliance = pieceAtDest.getPieceAlliance();
                        if (this.pieceAlliance != pieceAlliance) {
                            legalMoves.add(new AttackMov(board, this, candDestCoord, pieceAtDest));
                        }
                        break;
                    }
                }
            }

        }

        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public String toString(){
        return PieceType.QUEEN.toString();
    }

    private static boolean isFirstColumnExl(final int currentPos, final int candOffset) {
        return BoardUtils.FIRST_COLUMN[currentPos] && (candOffset == -1 || candOffset == -9 || candOffset == 7);
    }

    private static boolean isEighthColumnExl(final int currentPos, final int candOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPos] && (candOffset == -7 || candOffset == 9 || candOffset == 1);
    }

}
