package minichat;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;

public class ChatController {
    @FXML private TextField usernameField;
    @FXML private TextField roomCodeField;
    @FXML private TextArea chatArea;
    @FXML private TextField messageField;

    private ChatClient client;

    @FXML
    private void onJoinRoomClick() {
        String username = usernameField.getText().trim();
        String roomCode = roomCodeField.getText().trim();

        if (username.isEmpty() || roomCode.isEmpty() || roomCode.length() != 6) {
            showAlert("Error", "Please enter a valid username and 6-digit room code.");
            return;
        }

        if (client == null) {
            client = new ChatClient(username, chatArea);
        }

        client.joinRoom(roomCode);
        chatArea.clear();
        chatArea.appendText("Joined room " + roomCode + " as " + username + "\n");
        
        usernameField.setDisable(true);
        roomCodeField.setDisable(true);
    }

    @FXML
    private void onSendClick() {
        if (client == null) {
            showAlert("Error", "Please join a room first.");
            return;
        }

        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            client.sendMessage(message);
            messageField.clear();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}