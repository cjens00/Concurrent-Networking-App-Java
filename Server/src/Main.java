import ChatServer.Server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Server server = new Server("127.0.0.1", 16770);
        server.start();
    }
}