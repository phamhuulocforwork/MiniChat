package minichat;

import javafx.scene.control.TextArea;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatClient {
    private final String username;
    private final TextArea messageArea;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final List<ChatMemberListener> memberListeners = new ArrayList<>();

    public interface ChatMemberListener {
        void onMemberJoin(String username);
        void onMemberLeave(String username);
        void onMemberListReceived(List<String> members);
    }

    public void addMemberListener(ChatMemberListener listener) {
        memberListeners.add(listener);
    }

    public ChatClient(String username, @SuppressWarnings("exports") TextArea messageArea) {
        this.username = username;
        this.messageArea = messageArea;
    }

    public void joinRoom(String roomCode) {
        try {
            if (socket != null) {
                socket.close();
            }
            
            socket = new Socket("localhost", 5000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            out.println(username + "|" + roomCode);
            
            // Bắt đầu thread nhận tin nhắn
            new Thread(this::receiveMessages).start();
            
        } catch (IOException e) {
            e.printStackTrace();
            messageArea.appendText("Error connecting to server\n");
        }
    }

    private void receiveMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("MEMBER_")) {
                    handleMemberMessage(message);
                } else {
                    final String finalMessage = message;
                    javafx.application.Platform.runLater(() -> 
                        messageArea.appendText(finalMessage + "\n")
                    );
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleMemberMessage(String message) {
        String[] parts = message.split("\\|");
        String type = parts[0];
        String data = parts[1];

        javafx.application.Platform.runLater(() -> {
            switch (type) {
                case "MEMBER_JOIN":
                    memberListeners.forEach(listener -> listener.onMemberJoin(data));
                    break;
                case "MEMBER_LEAVE":
                    memberListeners.forEach(listener -> listener.onMemberLeave(data));
                    break;
                case "MEMBER_LIST":
                    List<String> members = Arrays.asList(data.split(","));
                    memberListeners.forEach(listener -> listener.onMemberListReceived(members));
                    break;
                case "HISTORY":
                    messageArea.appendText(data + "\n");
                    break;
            }
        });
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
            messageArea.appendText(username + ": " + message + "\n");
        }
    }

    public String getUsername() {
        return username;
    }

    public void receiveMessage(String message) {
      throw new UnsupportedOperationException("Unimplemented method 'receiveMessage'");
    }

    public void leaveRoom() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                messageArea.appendText("Đã rời phòng\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            messageArea.appendText("Lỗi khi rời phòng: " + e.getMessage() + "\n");
        }
    }
}