package server;

import client.gamesrc.board.Board;
import client.gamesrc.board.Move;
import client.gui.Table;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class NetworkEntity extends Thread {

    protected ObjectOutputStream outputStream;
    protected ObjectInputStream inputStream;
    protected Socket connectionHandle;
    protected Object receivedMessage;
    protected Table table;

    NetworkEntity(final String name) {
        super(name);
    }

    public abstract void run();

    public void getStreams() throws IOException {
        outputStream = new ObjectOutputStream(connectionHandle
                .getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(connectionHandle
                .getInputStream());
    }

    public void closeConnection() {

        try {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (connectionHandle != null) {
                connectionHandle.close();
                System.out.println("Connection closed with " + connectionHandle.getInetAddress().getHostName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void processIncomingData() throws IOException {
        try {
            while (true) {
                System.out.println("Waiting for incoming data...");
                receivedMessage = inputStream.readObject();
                if (receivedMessage == null) {
                    System.out.println("Received null. Exiting loop.");
                    break;
                }
                System.out.println("Received data: " + receivedMessage);

                // Your existing processing logic here...
                if (receivedMessage instanceof Move m) {
                    System.out.println(m.toString());
                    table.requestMove(m);
                    sendData(m);
                    table.repaintFrame();
                } else if (receivedMessage instanceof Board) {
                    final Board b = (Board) receivedMessage;
                    sendData(b);
                    // b.printCurrentBoardState();
                    table.repaintFrame();
                } else if (receivedMessage instanceof String) {
                    System.out.println((String) receivedMessage);
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("read() error: message from "
                    + connectionHandle.getInetAddress().getHostName() + " not received");
        }
    }

    public void sendData(final Object obj_to_send) {
        try {
            outputStream.writeObject(obj_to_send);
            outputStream.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


}
