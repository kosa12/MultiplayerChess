package server;

import client.gamesrc.board.Board;
import client.gui.Table;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends NetworkEntity {

    private String hostName;

    private int serverPort;

    public Client(final String host, final int port) {
        super("CLIENT");
        hostName = host;
        serverPort = port;
    }

    public void run() {
        try {
            connectToServer();
            getStreams();
            processIncomingData();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void connectToServer() {
        try {
            connectionHandle = new Socket(InetAddress.getByName(hostName),
                    serverPort);
            //connectionEstablished = true;
            System.out.println("Successfully connected to " + connectionHandle.getInetAddress().getHostName());
        } catch (IOException e) {
            System.out.println("Failed to connect to: " + hostName);
        }
    }

    public static void main(String[] args) {
        Board board = Board.createStandardBoard();
        Table table = new Table();
        Client client = new Client("localhost", 5000);
        client.start();

    }

}