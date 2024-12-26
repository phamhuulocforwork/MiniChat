package minichat;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import java.util.List;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;

public class ChatController {
    @FXML private TextField usernameField;
    @FXML private TextField roomCodeField;
    @FXML private TextArea chatArea;
    @FXML private TextField messageField;
    @FXML private TextField searchField;
    @FXML private HBox joinDialog;
    @FXML private Label usernameLabel;
    @FXML private Label roomNameLabel;
    @FXML private ListView<String> roomList;
    @FXML private ListView<String> membersList;
    @FXML private VBox loginForm;
    @FXML private BorderPane chatInterface;
    
    private ChatClient client;
    private final ObservableList<String> rooms = FXCollections.observableArrayList();
    private final ObservableList<String> members = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        roomList.setItems(rooms);
        membersList.setItems(members);
        
        //NOTE: mặc định hiển thị ui chọn phòng
        loginForm.setVisible(true);
        chatInterface.setVisible(false);
    }

    @FXML
    private void onJoinRoomClick() {
        String username = usernameField.getText().trim();
        String roomCode = roomCodeField.getText().trim();

        if (username.isEmpty() || roomCode.isEmpty() || roomCode.length() != 6) {
            showAlert("Error", "Vui lòng nhập tên người dùng và mã phòng 6 chữ số.");
            return;
        }

        if (client == null) {
            client = new ChatClient(username, chatArea);
            client.addMemberListener(new ChatClient.ChatMemberListener() {
                @Override
                public void onMemberJoin(String username) {
                    members.add("🟢 " + username);
                    chatArea.appendText(username + " đã tham gia vào phòng\n");
                }

                @Override
                public void onMemberLeave(String username) {
                    members.removeIf(member -> member.endsWith(username));
                    chatArea.appendText(username + " đã rời phòng\n");
                }

                @Override
                public void onMemberListReceived(List<String> memberList) {
                    members.clear();
                    for (String member : memberList) {
                        members.add("🟢 " + member);
                    }
                }
            });
        }

        client.joinRoom(roomCode);
        
        loginForm.setVisible(false);
        chatInterface.setVisible(true);
        
        usernameLabel.setText(username);
        roomNameLabel.setText("# Phòng-" + roomCode);
        rooms.add("# Phòng-" + roomCode);
        
        chatArea.clear();
        chatArea.appendText("Chào mừng đến phòng-" + roomCode + "!\n");
    }

    @FXML
    private void onLeaveRoom() {
        if (client != null) {
            client.leaveRoom();
            
            //NOTE: Quay về giao diện chọn phòng
            loginForm.setVisible(true);
            chatInterface.setVisible(false);
            
            //NOTE: Xóa dữ liệu
            chatArea.clear();
            messageField.clear();
            rooms.clear();
            members.clear();
            
            client = null;
        }
    }

    @FXML
    private void onSendMessage() {
        if (client == null) {
            showAlert("Error", "Vui lòng tham gia phòng trước.");
            return;
        }

        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            client.sendMessage(message);
            messageField.clear();
        }
    }

    @FXML
    private void onSettingsClick() {
        //NOTE: Chức năng cài đặt
        System.out.println("Settings clicked");
    }

    @FXML
    private void onSearch(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            String searchTerm = searchField.getText().trim();
            //NOTE: Chức năng tìm kiếm
            System.out.println("Tìm kiếm: " + searchTerm);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}