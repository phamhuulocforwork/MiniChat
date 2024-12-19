package minichat;

import java.util.List;
import java.util.ArrayList;

public class ChatRoom {
  private final String roomCode;
  private final List<ChatClient> clients;

  public ChatRoom(String roomCode) {
      this.roomCode = roomCode;
      this.clients = new ArrayList<>();
  }

  public void addClient(ChatClient client) {
      clients.add(client);
  }

  public void removeClient(ChatClient client) {
      clients.remove(client);
  }

  public void broadcastMessage(String message, ChatClient sender) {
      for (ChatClient client : clients) {
          if (client != sender) {
              client.receiveMessage(message);
          }
      }
  }

  public String getRoomCode() {
      return roomCode;
  }

  public int getClientCount() {
      return clients.size();
  }
}