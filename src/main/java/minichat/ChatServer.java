package minichat;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {
    private static final int PORT = 3001;
    private final ServerSocket serverSocket;
    private final Map<String, Set<ClientHandler>> rooms;

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
        rooms.computeIfAbsent(roomCode, k -> ConcurrentHashMap.newKeySet()).add(client);
    }

    public void removeFromRoom(String roomCode, ClientHandler client) {
        Set<ClientHandler> clients = rooms.get(roomCode);
        if (clients != null) {
            clients.remove(client);
            if (clients.isEmpty()) {
                rooms.remove(roomCode);
            }
        }
    }

    public void broadcastToRoom(String roomCode, String message, ClientHandler sender) {
        Set<ClientHandler> clients = rooms.get(roomCode);
        if (clients != null) {
            for (ClientHandler client : clients) {
                if (client != sender) {
                    client.sendMessage(message);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            new ChatServer().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}