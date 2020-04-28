package utils;

import java.util.ArrayList;
import java.util.List;

public class Session {

    private String name;
    private ServerClient creator;
    private List<ServerClient> studentList;

    public Session(String name, ServerClient creator) {
        this.name = name;
        this.creator = creator;
        studentList = new ArrayList<ServerClient>();
    }

    public String getName() {
        return name;
    }

    public void addClient(ServerClient serverClient) {
        studentList.add(serverClient);
    }

    public void removeClient(ServerClient serverClient) {
        studentList.remove(serverClient);
    }

    public String getUsersNames() {
        String bufferString = "";
        for (ServerClient s : studentList
        ) {
            bufferString += s.getName().trim();
        }
        return bufferString;
    }

    public List<ServerClient> getStudentList() {
        return studentList;
    }

    public ServerClient getCreator() {
        return creator;
    }
}
