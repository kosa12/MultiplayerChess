package gamesrc.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import gamesrc.board.Board;
import gamesrc.board.BoardUtils;
import gamesrc.board.Move;

public class Pawn extends Piece {

    private final static int[] CANDIDATE_MOVE_VECTOR_COORD = { 8, 16, 7, 9 };

    Pawn(int piecePosition, Alliance pAlliance) {
        super(piecePosition, pAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (int currentCandOffset : CANDIDATE_MOVE_VECTOR_COORD) {
            final int candDestCoord = this.piecePosition + (this.pieceAlliance.getDirection() * currentCandOffset);

            if (!BoardUtils.isValidTileCoord(candDestCoord)) {
                continue;
            }

            if (currentCandOffset == 8 && board.getTile(currentCandOffset).isTileOccupied()) {
                // todo xD (deal with promotions)
                legalMoves.add(new Move.MajorMov(board, null, candDestCoord));
            } else if (currentCandOffset == 16 && this.isFirstMove() &&
                    (((BoardUtils.SECOND_ROW[this.piecePosition] && this.getPieceAlliance().isBlack())) ||
                            ((BoardUtils.SEVENTH_ROW[this.piecePosition] && this.getPieceAlliance().isWhite())))) {

                final int behindCandCoord = this.piecePosition + (this.pieceAlliance.getDirection() * 8);
                if (!board.getTile(behindCandCoord).isTileOccupied()
                        && !board.getTile(candDestCoord).isTileOccupied()) {
                    legalMoves.add(new Move.MajorMov(board, null, candDestCoord));
                }

            } else if (currentCandOffset == 7 &&
                    !((BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite()) ||
                            (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack()))) {
                if (board.getTile(candDestCoord).isTileOccupied()) {
                    final Piece pieceOnCand = board.getTile(candDestCoord).getPiece();
                    if (this.pieceAlliance != pieceOnCand.getPieceAlliance()) {
                        // TODO move to here
                        legalMoves.add(new Move.MajorMov(board, null, candDestCoord));
                    }
                }

            } else if (currentCandOffset == 9 &&
                    !((BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite()) ||
                            (BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack()))) {
                if (board.getTile(candDestCoord).isTileOccupied()) {
                    final Piece pieceOnCand = board.getTile(candDestCoord).getPiece();
                    if (this.pieceAlliance != pieceOnCand.getPieceAlliance()) {
                        // TODO move to here
                        legalMoves.add(new Move.MajorMov(board, null, candDestCoord));
                    }
                }
            }
        }

        return ImmutableList.copyOf(legalMoves);
    }
}
