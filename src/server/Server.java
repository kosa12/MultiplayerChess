package server;

import java.io.IOException;
import java.net.ServerSocket;

public class Server extends NetworkEntity {

    private ServerSocket server;

    private final int listenPort;

    public Server(final int listen_port) {
        super("SERVER");
        listenPort = listen_port;
    }

    public void run() {

        try {
            server = new ServerSocket(listenPort);
            System.out.println("Listening on port " + listenPort);
            try {
                waitForConnection();
                getStreams();
                processIncomingData();
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                closeConnection();
            }
        } catch (IOException e) {
            //    JOptionPane.showMessageDialog(gameFrame, "Network Error: " + e, "Notification",
            //            JOptionPane.ERROR_MESSAGE);
        }

    }

    private void waitForConnection() throws IOException {
        while (true) {
            connectionHandle = server.accept();
            System.out.println("Connection received from:" + connectionHandle.getInetAddress().getHostName());

            // Create a new thread to handle the connection
            Thread clientThread = new Thread(() -> {
                try {
                    getStreams();
                    processIncomingData();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            });

            clientThread.start();
        }
    }

    public void closeConnection() {
        super.closeConnection();
        try {
            server.close();
        } catch (IOException e) {
            System.out.println(getName() + "failed to disconnect from the network");
        }
    }

    public static void main(String[] args) {
        Server server = new Server(5000);
        server.start();
    }
}