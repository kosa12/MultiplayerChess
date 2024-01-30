package gamesrc.player;

import com.google.common.collect.ImmutableList;
import gamesrc.Alliance;
import gamesrc.board.Board;
import gamesrc.board.Move;
import gamesrc.board.Move.KingSideCastleMove;
import gamesrc.board.Move.QueenSideCastleMove;
import gamesrc.board.Tile;
import gamesrc.pieces.Piece;
import gamesrc.pieces.Rook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WhitePlayer extends Player {
    public WhitePlayer(final Board board, final Collection<Move> whiteStandardLegalMoves, final Collection<Move> blackStandardLegalMoves) {
        super(board, blackStandardLegalMoves, whiteStandardLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.whitePlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals, final Collection<Move> opponentsLegal) {
        final List<Move> kingCastles = new ArrayList<>();

        if(this.playerKing.isFirstMove() && !this.isInCheck()){
            //white king side castle
            if(!this.board.getTile(61).isTileOccupied() && this.board.getTile(62).isTileOccupied()){
                final Tile rookTile = this.board.getTile(63);

                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()){

                    if(Player.calculateAttacksOnTile(61, opponentsLegal).isEmpty() &&
                            Player.calculateAttacksOnTile(62, opponentsLegal).isEmpty() &&
                            rookTile.getPiece().getPieceType().isRook()){

                        kingCastles.add(new KingSideCastleMove(this.board, this.playerKing, 62, (Rook) rookTile.getPiece(), rookTile.getTileCoord(), 61));
                    }
                }
            }

            if(!this.board.getTile(59).isTileOccupied() && this.board.getTile(58).isTileOccupied() && this.board.getTile(57).isTileOccupied()){
                final Tile rookTile = this.board.getTile(56);

                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()){
                    kingCastles.add(new QueenSideCastleMove(this.board, this.playerKing, 58, (Rook) rookTile.getPiece(), rookTile.getTileCoord(), 59));
                }
            }
        }

        return ImmutableList.copyOf(kingCastles);
    }
}
