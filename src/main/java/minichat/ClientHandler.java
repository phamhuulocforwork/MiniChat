package minichat;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final ChatServer server;
    private PrintWriter out;
    private BufferedReader in;
    private String roomCode;
    private String username;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String joinMessage = in.readLine();
            String[] parts = joinMessage.split("\\|");
            this.username = parts[0];
            this.roomCode = parts[1];
            
            server.addToRoom(roomCode, this);

            String message;
            while ((message = in.readLine()) != null) {
                server.broadcastToRoom(roomCode, username + ": " + message, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (roomCode != null) {
                server.removeFromRoom(roomCode, this);
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}