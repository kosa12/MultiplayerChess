package gamesrc.pieces;

import gamesrc.board.Board;
import gamesrc.board.BoardUtils;
import gamesrc.board.Move;
import gamesrc.board.Tile;
import gamesrc.board.Move.AttackMov;
import gamesrc.board.Move.MajorMov;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

public class Rook extends Piece{
    private final static int[] CANDIDATE_MOVE_VECTOR_COORD = {-8, -1, 1, 8};

    Rook(int piecePosition, Alliance pieceAlliance) {
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

    private static boolean isFirstColumnExl(final int currentPos, final int candOffset) {
        return BoardUtils.FIRST_COLUMN[currentPos] && (candOffset == -1);
    }

    private static boolean isEighthColumnExl(final int currentPos, final int candOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPos] && (candOffset == 1);
    }
}
