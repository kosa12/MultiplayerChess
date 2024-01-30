package gamesrc.player;

import com.google.common.collect.ImmutableList;
import gamesrc.Alliance;
import gamesrc.board.Board;
import gamesrc.board.Move;
import gamesrc.board.Move.KingSideCastleMove;
import gamesrc.board.Tile;
import gamesrc.pieces.Piece;
import gamesrc.pieces.Rook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BlackPlayer extends Player {
    public BlackPlayer(final Board board, final Collection<Move> whiteStandardLegalMoves, final Collection<Move> blackStandardLegalMoves) {
        super(board,  whiteStandardLegalMoves, blackStandardLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.blackPlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals, final Collection<Move> opponentsLegal) {

        final List<Move> kingCastles = new ArrayList<>();

        if(this.playerKing.isFirstMove() && !this.isInCheck()){
            //black king side castle
            if(!this.board.getTile(5).isTileOccupied() && this.board.getTile(6).isTileOccupied()){
                final Tile rookTile = this.board.getTile(7);

                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()){

                    if(Player.calculateAttacksOnTile(5, opponentsLegal).isEmpty() &&
                            Player.calculateAttacksOnTile(6, opponentsLegal).isEmpty() &&
                            rookTile.getPiece().getPieceType().isRook()){


                        kingCastles.add(new KingSideCastleMove(this.board, this.playerKing, 6, (Rook) rookTile.getPiece(), rookTile.getTileCoord(), 5));
                    }

                }
            }

            if(!this.board.getTile(1).isTileOccupied() &&
                    this.board.getTile(2).isTileOccupied() &&
                    this.board.getTile(3).isTileOccupied()){

                final Tile rookTile = this.board.getTile(0);

                if(rookTile.isTileOccupied() &&
                        rookTile.getPiece().isFirstMove() &&
                        Player.calculateAttacksOnTile(2, opponentsLegal).isEmpty() &&
                        Player.calculateAttacksOnTile(3, opponentsLegal).isEmpty() &&
                        rookTile.getPiece().getPieceType().isRook()){

                    kingCastles.add(new Move.QueenSideCastleMove(this.board, this.playerKing, 2, (Rook) rookTile.getPiece(), rookTile.getTileCoord(), 3));
                }
            }
        }

        return ImmutableList.copyOf(kingCastles);
    }
}

