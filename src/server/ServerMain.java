package server;

import java.net.SocketException;

public class ServerMain {

    public static void main(String[] args) throws SocketException {
        int port = 7700;
        new Server(port);
    }
}
