package client.gui;


import client.gamesrc.Alliance;
import client.gamesrc.board.Board;
import client.gamesrc.board.BoardUtils;
import client.gamesrc.board.Move;
import client.gamesrc.board.Tile;
import client.gamesrc.pieces.Piece;
import client.gamesrc.player.MoveTransition;
import com.google.common.collect.Lists;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.*;
import java.util.concurrent.Executors;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table implements ActionListener {

    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 500);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);
    private static final String defaultPieceImagesPath = "src/client/gui/chessPiece/";

    private static Board chessBoard;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private static BoardDirection boardDirection;
    private boolean highlightLegalMoves;
    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;
    private final Color lightTileColor = Color.decode("#ffce9e");
    private final Color darkTileColor = Color.decode("#d18b47");

    public final JFrame gameFrame;
    private static BoardPanel boardPanel;
    private final MoveLog moveLog;

    private final JPanel topPanel;
    private final JLabel currentPlayerLabel;
    private JButton serverBtn;
    private JButton clientBtn;

    private ServerSocket listener;
    private Socket socket;
    private PrintWriter printWriter;

    private String SOCKET_SERVER_ADDR = "localhost";
    private int PORT = 50000;

    public Table() {
        this.gameFrame = new JFrame("chessGame");
        this.gameFrame.setLayout(new BorderLayout());
        boardDirection = BoardDirection.NORMAL;
        this.highlightLegalMoves = false;
        final JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        chessBoard = Board.createStandardBoard(Alliance.WHITE);
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.moveLog = new MoveLog();
        boardPanel = new BoardPanel();
        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(gameHistoryPanel, BorderLayout.EAST);
        this.gameFrame.add(boardPanel, BorderLayout.CENTER);


        this.topPanel = new JPanel(new BorderLayout());
        this.topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        this.currentPlayerLabel = new JLabel("Current Player: " + chessBoard.currentPlayer().getAlliance());
        this.currentPlayerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.topPanel.add(currentPlayerLabel, BorderLayout.CENTER);
        this.gameFrame.add(this.topPanel, BorderLayout.NORTH);

        var buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        serverBtn = new JButton("Server");
        buttonsPanel.add(serverBtn);
        serverBtn.addActionListener(this);

        clientBtn = new JButton("Client");
        buttonsPanel.add(clientBtn);
        clientBtn.addActionListener(this);

        this.gameFrame.add(buttonsPanel, BorderLayout.PAGE_END);

        this.gameFrame.setVisible(true);
    }


    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(preferenceMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("Menu");
        final JMenuItem exitMenuItem = new JMenuItem("Exit game");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);
        return fileMenu;


    }

    private void runSocketClient() {
        try {
            socket = new Socket(SOCKET_SERVER_ADDR, PORT);
            System.out.println("client connected to port " + PORT);
            var scanner = new Scanner(socket.getInputStream());
            printWriter = new PrintWriter(socket.getOutputStream(), true);

            Executors.newFixedThreadPool(1).execute(new Runnable() {
                @Override
                public void run() {
                    receiveMove(scanner);
                }
            });
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void runSocketServer() {
        Executors.newFixedThreadPool(1).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    listener = new ServerSocket(PORT);
                    System.out.println("server is listening on port " + PORT);
                    socket = listener.accept();
                    System.out.println("connected from " + socket.getInetAddress());
                    printWriter = new PrintWriter(socket.getOutputStream(), true);
                    var scanner = new Scanner(socket.getInputStream());
                    receiveMove(scanner);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void receiveMove(Scanner scanner) {
        while (scanner.hasNextLine()) {
            var moveStr = scanner.nextLine();
            System.out.println("chess move received: " + moveStr);

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    String[] moveParts = moveStr.split(": ");

                    String destinationSquare = moveParts[moveParts.length - 1].trim(); // Extract destination square
                    if (destinationSquare.length() >= 2) {
                        // Extract only the last two characters
                        destinationSquare = destinationSquare.substring(destinationSquare.length() - 2);
                    } else {
                        System.out.println("Invalid destination square: " + destinationSquare);
                        return; // Exit if destination square is too short
                    }

                    int destCoord = BoardUtils.getCoordinateAtPosition(destinationSquare);

                    for (final Tile tile : chessBoard.getAllTiles()) {
                        if (tile.isTileOccupied() &&
                                tile.getPiece().getPieceAlliance() == chessBoard.currentPlayer().getAlliance() &&
                                tile.getPiece().calculateLegalMoves(chessBoard).stream()
                                        .anyMatch(move -> move.getDestinationCoordinate() == destCoord)) {
                            int sourceCoord = tile.getTileCoordinate();

                            Move move = Move.MoveFactory.createMove(chessBoard, sourceCoord, destCoord);
                            if(!move.toString().equals(moveStr)){
                                continue;
                            }
                            MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                            if (transition.getMoveStatus().isDone()) {
                                chessBoard = transition.getTransitionBoard();
                                moveLog.addMoves(move);
                            }
                            boardPanel.drawBoard(chessBoard);
                            break;
                        }
                    }
                }
            });
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == serverBtn) {
            serverBtn.setEnabled(false);
            clientBtn.setEnabled(false);
            this.gameFrame.setTitle("Chess Server");
            runSocketServer();
            JOptionPane.showMessageDialog(this.gameFrame, "listening on port " + PORT);
        } else if (e.getSource() == clientBtn) {
            serverBtn.setEnabled(false);
            clientBtn.setEnabled(false);
            this.gameFrame.setTitle("Chess Client");
            runSocketClient();
            JOptionPane.showMessageDialog(this.gameFrame, "connected to port " + PORT);
        }
    }


    private class BoardPanel extends JPanel {
        final List<TilePanel> boardTiles;

        public BoardPanel() {
            super(new GridLayout(8, 8));
            this.boardTiles = new ArrayList<>();
            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        void drawBoard(final Board board) {
            removeAll();
            for (final TilePanel boardTile : boardDirection.traverse(boardTiles)) {
                boardTile.drawTile(board);
                add(boardTile);
            }
            validate();
            repaint();
        }

    }

    public static class MoveLog {
        private final List<Move> moves;

        MoveLog() {
            this.moves = new ArrayList<>();
        }

        public List<Move> getMoves() {
            return this.moves;
        }

        public Move returnCurrentMove() {
            if (this.moves.isEmpty()) {
                return null;
            }
            return this.moves.getLast();
        }

        public void addMoves(final Move move) {
            this.moves.add(move);
        }

        public int size() {
            return this.moves.size();
        }

        public void clear() {
            this.moves.clear();
        }

        public Move removeMove(int index) {
            return this.moves.remove(index);
        }

        public boolean removeMove(final Move move) {
            return this.moves.remove(move);
        }
    }

    private class TilePanel extends JPanel {

        private final int tileId;

        TilePanel(final BoardPanel boardPanel, final int tileId) {
            super(new GridBagLayout());
            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceIcon(chessBoard);

            addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(final MouseEvent e) {
                    if (isRightMouseButton(e)) {
                        sourceTile = null;
                        destinationTile = null;
                        humanMovedPiece = null;
                    } else if (isLeftMouseButton(e)) {
                        if (sourceTile == null) {
                            sourceTile = chessBoard.getTile(tileId);
                            humanMovedPiece = sourceTile.getPiece();
                            if (humanMovedPiece == null) {
                                sourceTile = null;
                            }
                        } else {
                            destinationTile = chessBoard.getTile(tileId);
                            final Move move = Move.MoveFactory.createMove(chessBoard, sourceTile.getTileCoordinate(), destinationTile.getTileCoordinate());
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                            if (transition.getMoveStatus().isDone()) {
                                chessBoard = transition.getTransitionBoard();
                                moveLog.addMoves(move);

                                // Send the move to the server
                                String moveString = move.toString(); // Convert the move to a string
                                printWriter.println(moveString); // Send the move string to the server

                                // Update the board and GUI
                                boardPanel.drawBoard(chessBoard);
                                gameHistoryPanel.redo(chessBoard, moveLog);
                                takenPiecesPanel.redo(moveLog);
                            }
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                    }
                }

                @Override
                public void mousePressed(final MouseEvent e) {

                }

                @Override
                public void mouseReleased(final MouseEvent e) {

                }

                @Override
                public void mouseEntered(final MouseEvent e) {

                }

                @Override
                public void mouseExited(final MouseEvent e) {

                }
            });


            validate();
        }

        public void drawTile(final Board board) {
            assignTileColor();
            assignTilePieceIcon(board);
            highlightLegals(board);
            updateCurrentPlayerLabel();
            validate();
            repaint();
        }

        private void updateCurrentPlayerLabel() {
            currentPlayerLabel.setText("Current Player: " + chessBoard.currentPlayer().getAlliance());
        }


        private void assignTilePieceIcon(final Board board) {
            this.removeAll();
            if (board.getTile(this.tileId).isTileOccupied()) {
                try {
                    final BufferedImage image =
                            ImageIO.read(new File(defaultPieceImagesPath + board.getTile(this.tileId).getPiece().getPieceAlliance().toString().substring(0, 1) + board.getTile(this.tileId).getPiece().toString() + ".png"));
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void assignTileColor() {
            if (BoardUtils.EIGHTH_RANK[this.tileId] ||
                    BoardUtils.SIXTH_RANK[this.tileId] ||
                    BoardUtils.FOURTH_RANK[this.tileId] ||
                    BoardUtils.SECOND_RANK[this.tileId]) {
                setBackground(this.tileId % 2 == 0 ? lightTileColor : darkTileColor);
            } else if (BoardUtils.SEVENTH_RANK[this.tileId] ||
                    BoardUtils.FIFTH_RANK[this.tileId] ||
                    BoardUtils.THIRD_RANK[this.tileId] ||
                    BoardUtils.FIRST_RANK[this.tileId]) {
                setBackground(this.tileId % 2 != 0 ? lightTileColor : darkTileColor);
            }

        }

        private void highlightLegals(final Board board) {
            if (highlightLegalMoves) {
                for (final Move move : pieceLegalMoves(board)) {
                    if (move.getDestinationCoordinate() == this.tileId) {
                        try {
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("src/client/gui/green_dot.png")))));
                        } catch (final IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private Collection<Move> pieceLegalMoves(final Board board) {
            if (humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()) {
                return humanMovedPiece.calculateLegalMoves(board);
            }
            return Collections.emptyList();
        }
    }

    enum BoardDirection {
        NORMAL {
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles) {
                return boardTiles;
            }

            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
        },
        FLIPPED {
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles) {
                return Lists.reverse(boardTiles);
            }

            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
        };

        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);

        abstract BoardDirection opposite();

    }

    private JMenu preferenceMenu() {
        final JMenu preferenceMenu = new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
        flipBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                flipBoardMenuItem.addActionListener(e -> {
                    boardDirection = boardDirection.opposite();
                    boardPanel.drawBoard(chessBoard);
                });
            }
        });

        preferenceMenu.add(flipBoardMenuItem);

        final JCheckBoxMenuItem LegalMoveHighlighter = new JCheckBoxMenuItem("Highlight Legal Moves", false);

        LegalMoveHighlighter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                highlightLegalMoves = LegalMoveHighlighter.isSelected();
            }
        });

        preferenceMenu.add(LegalMoveHighlighter);
        return preferenceMenu;
    }
}
