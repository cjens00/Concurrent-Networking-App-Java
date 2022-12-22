package ChatServer.components;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Client {
    SelectionKey key;
    long sessionId;

    public Client(SelectionKey key, long sessionId) {
        this.key = key;
        this.sessionId = sessionId;
    }

    public SelectionKey getKey() {
        return key;
    }

    public void setKey(SelectionKey key) {
        this.key = key;
    }

    public SocketChannel getChannel() {
        return (SocketChannel) this.key.channel();
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }
}
