package gamesrc.pieces;

import client.gamesrc.Alliance;
import gamesrc.board.Board;
import gamesrc.board.BoardUtils;
import gamesrc.board.Move;
import gamesrc.board.Tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

public class Rook extends Piece {

    private final static int[] CANDIDATE_MOVE_VECTOR_COORDS = {-8, -1, 1, 8};

    /**
     * A piece constructor, creates a piece belonging
     * to a certain alliance from parameters.
     *
     * @param piecePosition coordinate at which it shall be put.
     * @param pieceAlliance an alliance to which the piece will belong - black or white.
     */
    public Rook(Alliance pieceAlliance, int piecePosition) {
        super(PieceType.ROOK, piecePosition, pieceAlliance, true);
    }

    public Rook(Alliance pieceAlliance, int piecePosition, boolean isFirstMove){
        super(PieceType.ROOK, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public String toString() {
        return PieceType.ROOK.toString();
    }

    @Override
    public Rook movePiece(Move move) {
        return new Rook(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate());
    }

    /**
     * Returns a list of legal moves which can be used
     * to determine which way can a piece move.
     * Overriden method from super class Piece.
     *
     * @param board a board at which the moves should be calculated
     * @return list of Move class objects
     */
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
                        legalMoves.add(new Move.PawnMove(board, this, candidateDestinationCoordinate));
                    } else {
                        final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                        final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
                        if (this.pieceAlliance != pieceAlliance) {
                            legalMoves.add(new Move.AttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
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
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -1);
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == 1);
    }

















}
