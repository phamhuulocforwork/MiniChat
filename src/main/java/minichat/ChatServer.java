package minichat;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {
    private static final int PORT = 5000;
    private final ServerSocket serverSocket;
    private final Map<String, Room> rooms;

    public ChatServer() throws IOException {
        serverSocket = new ServerSocket(PORT);
        rooms = new ConcurrentHashMap<>();
        System.out.println("Server started on port " + PORT);
    }

    public void start() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                new Thread(clientHandler).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addToRoom(String roomCode, ClientHandler client) {
        Room room = rooms.computeIfAbsent(roomCode, Room::new);
        room.addClient(client);
        broadcastToRoom(roomCode, "MEMBER_JOIN|" + client.getUsername(), null);
    }

    public void removeFromRoom(String roomCode, ClientHandler client) {
        Room room = rooms.get(roomCode);
        if (room != null) {
            room.removeClient(client);
            broadcastToRoom(roomCode, "MEMBER_LEAVE|" + client.getUsername(), null);
        }
    }

    public Set<String> getRoomMembers(String roomCode) {
        Room room = rooms.get(roomCode);
        Set<String> members = new HashSet<>();
        if (room != null) {
            for (ClientHandler client : room.getActiveClients()) {
                members.add(client.getUsername());
            }
        }
        return members;
    }

    public void broadcastToRoom(String roomCode, String message, ClientHandler sender) {
        Room room = rooms.get(roomCode);
        if (room != null) {
            if (!message.startsWith("MEMBER_")) {
                room.addMessage(message);
            }
            for (ClientHandler client : room.getActiveClients()) {
                if (client != sender) {
                    client.sendMessage(message);
                }
            }
        }
    }

    public List<String> getRoomHistory(String roomCode) {
        Room room = rooms.get(roomCode);
        return room != null ? room.getRecentMessages(50) : new ArrayList<>();
    }

    public static void main(String[] args) {
        try {
            new ChatServer().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}