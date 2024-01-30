package client.gamesrc;

import client.gamesrc.board.Board;
import client.gui.Table;

public class JChess {

    public static void main(String[] args) {
        Board board = Board.createStandardBoard();
        System.out.println(board);
        Table table = new Table();

    }
}
