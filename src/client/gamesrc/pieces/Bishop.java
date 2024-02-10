package client.gamesrc.pieces;

import client.gamesrc.Alliance;
import client.gamesrc.board.Board;
import client.gamesrc.board.BoardUtils;
import client.gamesrc.board.Move;
import client.gamesrc.board.Tile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static client.gamesrc.board.Move.MajorMove;

public class Bishop extends Piece {

    private final static int[] CANDIDATE_MOVE_VECTOR_COORDS = {-9, -7, 7, 9};

    public Bishop(final Alliance pieceAlliance, int piecePosition) {
        super(Piece.PieceType.BISHOP, piecePosition, pieceAlliance, true);
    }

    public Bishop(final Alliance pieceAlliance, int piecePosition, boolean isFirstMove) {
        super(Piece.PieceType.BISHOP, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public String toString() {
        return Piece.PieceType.BISHOP.toString();
    }

    @Override
    public Bishop movePiece(Move move) {
        return new Bishop(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate());
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {

        final List<Move> legalMoves = new ArrayList<>();

        for (final int candidateCoordinateOffset : CANDIDATE_MOVE_VECTOR_COORDS) {

            int candidateDestinationCoordinate = this.piecePosition;

            while (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {

                if (isFirstColumnExclusion(candidateDestinationCoordinate, candidateCoordinateOffset) ||
                        isEighthColumnExclusion(candidateDestinationCoordinate, candidateCoordinateOffset)) {
                    break;
                }
                candidateDestinationCoordinate += candidateCoordinateOffset;
                if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {

                    final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                    if (!candidateDestinationTile.isTileOccupied()) {
                        legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                    } else {
                        final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                        final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
                        if (this.pieceAlliance != pieceAlliance) {
                            legalMoves.add(new Move.MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                        }
                        // if it's occupied, no need to continue validating, break
                        break;
                    }
                }
            }

        }

        return ImmutableList.copyOf(legalMoves);
    }


    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return (BoardUtils.FIRST_COLUMN[currentPosition] &&
                ((candidateOffset == -9) || (candidateOffset == 7)));
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] &&
                ((candidateOffset == -7) || (candidateOffset == 9));
    }

}
