package gamesrc.board;

import gamesrc.pieces.Piece;

public abstract class Move {
    final Board board;
    final Piece movedPiece;
    final int destCoord;

    public Move(Board board, Piece movedPiece, int destCoord) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destCoord = destCoord;
    }

    public static final class MajorMov extends Move{

        public MajorMov(Board board, Piece movedPiece, int destCoord) {
            super(board, movedPiece, destCoord);
        }
    }

    public static final class AttackMov extends Move{
        final Piece attackedPiece;
        public AttackMov(Board board, Piece movedPiece, int destCoord, Piece attackedPiece) {
            super(board, movedPiece, destCoord);
            this.attackedPiece = attackedPiece;
        }
    }
}
