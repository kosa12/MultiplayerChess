package gamesrc.pieces;

import gamesrc.board.Board;
import gamesrc.board.Move;

import java.util.Collection;

public class Rook extends Piece{
    Rook(int piecePosition, Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        return null;
    }
}
