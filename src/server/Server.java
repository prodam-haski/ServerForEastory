package server;

import utils.ServerClient;
import utils.Session;
import utils.UniqueID;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private int port;
    private DatagramSocket socket;
    private Thread serverRun, manageTread, receiveThread;
    private boolean running = false;
    private List<Session> sessionList;

    public Server(int port) throws SocketException {
        this.port = port;
        socket = new DatagramSocket(port);
        sessionList = new ArrayList<Session>();

        serverRun = new Thread(new Runnable() {
            @Override
            public void run() {
                running = true;
                System.out.println("Server run on port" + port);
                receive();
            }
        }, "serverRun");
        serverRun.start();
    }

    private void receive() {
        receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    byte[] data = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    try {
                        socket.receive(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        decode(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        receiveThread.start();
    }


    private void decode(DatagramPacket packet) throws IOException {
        String str = new String(packet.getData());
        String bufferString = "";
        int counter = 0;

        if (str.startsWith("/cr")) {
            sessionList.add(new Session(str.substring(4), new ServerClient("creator", packet.getAddress(), packet.getPort(), UniqueID.getID())));
            bufferString = "successful%";
            packet.setData(bufferString.trim().getBytes());
            socket.send(packet);
        }
        if (str.startsWith("/ls")) {
            for (Session s : sessionList
            ) {
                bufferString += s.getName().trim() + "%";
                bufferString = bufferString.trim();
                System.out.println(bufferString);
            }
            packet.setData(bufferString.trim().getBytes());
            socket.send(packet);
        }
        if (str.startsWith("/ad")) {
            String serverName = str.substring(4, str.indexOf("%"));
            String userName = str.substring(str.indexOf("%") + 1, str.lastIndexOf("%"));
            System.out.println(serverName + " " + userName);

            for (Session s : sessionList
            ) {
                if (serverName.equals(s.getName().trim())) {
                    s.addClient(new ServerClient(userName, packet.getAddress(), packet.getPort(), UniqueID.getID()));

                    Socket tempSocket = new Socket(s.getCreator().getAddress(),s.getCreator().getPort());
                    OutputStream outputStream = tempSocket.getOutputStream();
                    outputStream.write(userName.getBytes());

                    bufferString = "successful%";
                    packet.setData(bufferString.trim().getBytes());
                    socket.send(packet);
                    break;
                }
            }

            for (Session s : sessionList
            ) {
                System.out.println(s.getName());
                System.out.println(s.getUsersNames());

            }


        }
        if (str.startsWith("/lt")) {
            String serverName = str.substring(4, str.indexOf("%"));
            for (Session s : sessionList
            ) {
                if (serverName.equals(s.getName().trim())) {
                    for (ServerClient c : s.getStudentList()
                    ) {
                        Socket tempSocket = new Socket(c.getAddress(),c.getPort());
                        OutputStream outputStream = tempSocket.getOutputStream();
                        outputStream.write(str.getBytes());

                    }
                    bufferString = "successful%";
                    packet.setData(bufferString.trim().getBytes());
                    socket.send(packet);
                    break;
                }
            }
        }

        if (str.startsWith("/re")) {
            String serverName = str.substring(4, str.indexOf("%"));
            for (Session s : sessionList
            ) {
                if (serverName.equals(s.getName().trim())) {
                    Socket tempSocket = new Socket(s.getCreator().getAddress(),s.getCreator().getPort());
                    OutputStream outputStream = tempSocket.getOutputStream();
                    outputStream.write(str.getBytes());
                    break;
                }
            }

        }

    }


}
