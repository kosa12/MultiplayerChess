package gui.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import gamesrc.Alliance;
import gamesrc.board.Board;
import gamesrc.board.BoardUtils;
import gamesrc.board.Move;
import gamesrc.board.Move.AttackMove;
import gamesrc.board.Move.MajorMove;
import gamesrc.board.Tile;

public class King extends Piece {
    private final static int[] CANDIDATE_MOVE_VECTOR_COORD = { -9, -8, -7, -1, 1, 7, 8, 9 };

    public King(Alliance pieceAlliance, int piecePosition) {
        super(PieceType.KING,piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {

        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentCandOffset : CANDIDATE_MOVE_VECTOR_COORD) {
            final int candDestCoord = this.piecePosition + currentCandOffset;

            if (isFirstColumnExcl(this.piecePosition, currentCandOffset) ||
                    isEighthColumnExcl(this.piecePosition, currentCandOffset)) {

                continue;
            }

            if (BoardUtils.isValidTileCoord(candDestCoord)) {
                final Tile candDestTile = board.getTile(candDestCoord);
                if (!candDestTile.isTileOccupied()) {
                    legalMoves.add(new MajorMove(board, this, candDestCoord));
                } else {
                    final Piece pieceAtDest = candDestTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDest.getPieceAlliance();
                    if (this.pieceAlliance != pieceAlliance) {
                        legalMoves.add(new AttackMove(board, this, candDestCoord, pieceAtDest));
                    }
                }
            }

        }

        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public King movePiece(Move move) {
        return new King(move.getMovedPiece().getPieceAlliance(), move.getDestCoord());
    }
    
    @Override
    public String toString(){
        return PieceType.KING.toString();
    }

    private static boolean isFirstColumnExcl(final int currentPos, final int candidateOffset) {
        return BoardUtils.FIRST_COLUMN[currentPos] && ((candidateOffset == -9) ||
                (candidateOffset == -1) ||
                (candidateOffset == 7));
    }

    private static boolean isEighthColumnExcl(final int currentPos, final int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPos] && ((candidateOffset == -7) ||
                (candidateOffset == 1) ||
                (candidateOffset == 9));
    }

}
