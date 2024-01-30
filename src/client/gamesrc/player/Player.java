package gamesrc.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import client.gamesrc.Alliance;
import gamesrc.board.Board;
import gamesrc.board.Move;
import gamesrc.pieces.King;
import gamesrc.pieces.Piece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Player {

    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    protected final boolean isInCheck;

    public Player(Board board, Collection<Move> legalMoves, Collection<Move> opponentMoves) {
        this.board = board;
        this.playerKing = establishKing();
        this.legalMoves = ImmutableList.copyOf(Iterables.concat(legalMoves, calculateKingCastles(legalMoves, opponentMoves)));
        this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentMoves).isEmpty();
    }

    protected static Collection<Move> calculateAttacksOnTile(int piecePosition, Collection<Move> moves) {
        final List<Move> attackMoves = new ArrayList<>();
        for (final Move move : moves) {
            if (piecePosition == move.getDestinationCoordinate()) {
                attackMoves.add(move);
            }
        }
        return ImmutableList.copyOf(attackMoves);
    }

    private King establishKing() {
        for (final Piece piece : getActivePieces()) {
            if (piece.getPieceType().isKing()) {
                return (King) piece;
            }
        }
        throw new RuntimeException("Invalid board");
    }


    /**
     * Returns a player's king.
     *
     * @return player's king.
     */
    public King getPlayerKing() {
        return this.playerKing;
    }


    //todo implement all these
    public boolean isMoveLegal(final Move move) {
        return this.legalMoves.contains(move);
    }

    /**
     * Checks whether a  player is in check or not
     *
     * @return true if is in check, false if isn't.
     */
    public boolean isInCheck() {
        return this.isInCheck;
    }

    /**
     * Checks whether a  player is in checkmate or not
     *
     * @return true if is in check, false if isn't.
     */
    public boolean isInCheckMate() {
        return this.isInCheck && !hasEscapeMoves();
    }

    protected boolean hasEscapeMoves() {
        for (final Move move : this.legalMoves) {
            final MoveTransition transition = makeMove(move);
            if (transition.getMoveStatus().isDone()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a  player is in stalemate or not
     *
     * @return true if is in check, false if isn't.
     */
    public boolean isInStaleMate() {
        return !this.isInCheck && !hasEscapeMoves();
    }

    public boolean isCastled() {
        return false;
    }

    /**
     * Returns a collection of player's legal moves.
     */
    public Collection<Move> getLegalMoves() {
        return legalMoves;
    }


    /**
     * Attempts to make a new move, has three outcomes, an
     * illegal move which is not possible or a move which leaves
     * player in check and is also not possible or a normal move with
     * a done status.
     */
    public MoveTransition makeMove(final Move move) {
        if (!isMoveLegal(move)) {
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
        }

        final Board transitionBoard = move.execute();
        final Collection<Move> kingAttacks = Player.calculateAttacksOnTile(transitionBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePosition(), transitionBoard.currentPlayer().getLegalMoves());
        if (!kingAttacks.isEmpty()) {
            return new MoveTransition(this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }
        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
    }


    public abstract Collection<Piece> getActivePieces();

    public abstract Alliance getAlliance();

    public abstract Player getOpponent();

    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentsLegals);


}
