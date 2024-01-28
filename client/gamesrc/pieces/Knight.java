package gamesrc.pieces;

import com.google.common.collect.ImmutableList;

import gamesrc.Alliance;
import gamesrc.board.Board;
import gamesrc.board.BoardUtils;
import gamesrc.board.Move;
import gamesrc.board.Tile;

import java.util.ArrayList;
import java.util.List;

import static gamesrc.board.Move.*;

public class Knight extends Piece {

    private final static int[] CANDIDATE_MOVE_COORD = {-17, -15, 10, -6, 6, 10, 15, 17};

    public Knight(final Alliance pieceAlliance, final int piecePosition) {
        super(piecePosition, pieceAlliance);
    }

    @Override
    public List<Move> calculateLegalMoves(final Board board) {

        final List<Move> legalMoves = new ArrayList<>();

        for (int currentCand : CANDIDATE_MOVE_COORD) {
            int candDestCoord = this.piecePosition + currentCand;
            candDestCoord = this.piecePosition + currentCand;

            if (BoardUtils.isValidTileCoord(candDestCoord)) {

                if (    isFirstColumnExcl(this.piecePosition, currentCand)   ||
                        isSecondColumnExcl(this.piecePosition, currentCand)  ||
                        isSeventhColumnExcl(this.piecePosition, currentCand) ||
                        isEighthColumnExcl(this.piecePosition, currentCand)) {

                    continue;
                }

                final Tile candDestTile = board.getTile(candDestCoord);
                if (!candDestTile.isTileOccupied()) {
                    legalMoves.add(new MajorMov(board, this, candDestCoord));
                } else {
                    final Piece pieceAtDest = candDestTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDest.getPieceAlliance();
                    if (this.pieceAlliance != pieceAlliance) {
                        legalMoves.add(new AttackMov(board, this, candDestCoord, pieceAtDest));
                    }
                }
            }
        }

        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public String toString(){
        return PieceType.KNIGHT.toString();
    }

    private static boolean isFirstColumnExcl(final int currentPos, final int candidateOffset) {
        return BoardUtils.FIRST_COLUMN[currentPos] &&   ((candidateOffset == -17) ||
                                                        (candidateOffset == -10)  ||
                                                        (candidateOffset == 6)    ||
                                                        (candidateOffset == 15));
    }

    private static boolean isSecondColumnExcl(final int currentPos, final int candidateOffset) {
        return BoardUtils.SECOND_COLUMN[currentPos] && ((candidateOffset == -10) ||
                                                        (candidateOffset == 6));
    }

    private static boolean isSeventhColumnExcl(final int currentPos, final int candidateOffset) {
        return BoardUtils.SEVENTH_COLUMN[currentPos] && ((candidateOffset == -6) ||
                                                        (candidateOffset == 10));
    }

    private static boolean isEighthColumnExcl(final int currentPos, final int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPos] && ((candidateOffset == -15) ||
                                                        (candidateOffset == -6)) ||
                                                        ((candidateOffset == 17) ||
                                                        (candidateOffset == -10));
    }

}
