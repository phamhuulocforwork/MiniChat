package minichat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Room {
    private String code;
    private List<String> messages;
    private Set<ClientHandler> activeClients;

    public Room(String code) {
        this.code = code;
        this.messages = new ArrayList<>();
        this.activeClients = ConcurrentHashMap.newKeySet();
    }

    public void addMessage(String message) {
        messages.add(message);
    }

    public List<String> getRecentMessages(int count) {
        int start = Math.max(0, messages.size() - count);
        return new ArrayList<>(messages.subList(start, messages.size()));
    }

    public void addClient(ClientHandler client) {
        activeClients.add(client);
    }

    public void removeClient(ClientHandler client) {
        activeClients.remove(client);
    }

    public Set<ClientHandler> getActiveClients() {
        return activeClients;
    }

    public boolean isActive() {
        return !activeClients.isEmpty();
    }

    public String getCode() {
        return code;
    }
}
