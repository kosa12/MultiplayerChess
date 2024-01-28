package gamesrc;

import gamesrc.board.Board;

public class JChess{

    public static void main(String[] args) {
        Board board = Board.createStandardBoard();

        System.out.println(board);
    }

}