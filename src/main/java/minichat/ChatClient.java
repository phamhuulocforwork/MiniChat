package minichat;

import javafx.scene.control.TextArea;
import java.io.*;
import java.net.*;

public class ChatClient {
    private final String username;
    private final TextArea messageArea;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
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
            
            // Start message receiving thread
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
                final String finalMessage = message;
                javafx.application.Platform.runLater(() -> 
                    messageArea.appendText(finalMessage + "\n")
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'receiveMessage'");
    }
}