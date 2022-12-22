package ChatServer;

import ChatServer.components.Client;
import ChatServer.utilities.IDGenerator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;

public class Server {
    private final int CONCURRENCY_MIN = 2;
    private final int CONCURRENCY_MAX = 8;
    private final int KEEP_ALIVE = 3000;
    private final int SZ_TASK_BACKLOG = 128;
    private boolean shouldClose;
    private IDGenerator idGen;
    private Selector selector;
    private SelectionKey serverKey;
    private ConcurrentHashMap<Long, Client> clients;
    private ExecutorService tPoolExecutor;

    public Server(String addr, int port) throws IOException {
        this.shouldClose = false;
        this.idGen = new IDGenerator();

        this.selector = Selector.open();
        var ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(InetAddress.getByName(addr), port));
        this.serverKey = ssc.register(this.selector, SelectionKey.OP_ACCEPT);

        this.tPoolExecutor = new ThreadPoolExecutor(
                CONCURRENCY_MIN,
                CONCURRENCY_MAX,
                KEEP_ALIVE,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(SZ_TASK_BACKLOG)
        );

        this.clients = new ConcurrentHashMap<>();
    }

    public void start() {
        SelectionKey key;
        Iterator<SelectionKey> it;
        while (!this.shouldClose) {
            try {
                selector.selectNow();
                it = this.selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    key = it.next();
                    it.remove();
                    if (key.isAcceptable()) {
                        this.tPoolExecutor.submit(hAccept(key));
                    } else if (key.isWritable()) {
                        //this.tPoolExecutor.submit(hWriteToClient());
                    } else if (key.isReadable()) {
                        //this.tPoolExecutor.submit(hReadFromClient());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        this.shouldClose = true;
    }

    private ServerSocketChannel getServerChannel() {
        return (ServerSocketChannel) this.serverKey.channel();
    }

    private void addClient(SelectionKey key, long sessionId) {
        clients.put(sessionId, new Client(key, sessionId));
    }

    private Client getClient(long sessionId) {
        return this.clients.get(sessionId);
    }

    private void shutdown() {
        ;
    }

    private boolean hasReady() throws IOException {
        return selector.selectNow() > 0;
    }

    private synchronized Runnable hAccept(SelectionKey k) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    long id;
                    SocketChannel socketChannel;
                    SelectionKey key;

                    id = idGen.nextID();
                    socketChannel = getServerChannel().accept();
                    socketChannel.configureBlocking(false);
                    key = socketChannel.register(k.selector(), SelectionKey.OP_WRITE, id);
                    addClient(key, id);

                    System.out.printf("Accepted connection from %s.%n",
                            socketChannel.getRemoteAddress().toString().substring(1));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private Runnable hWriteToClient() {
        return new Runnable() {
            @Override
            public void run() {

            }
        };
    }

    private Runnable hReadFromClient() {
        return new Runnable() {
            @Override
            public void run() {

            }
        };
    }

    private Runnable hDisconnectClient() {
        return new Runnable() {
            @Override
            public void run() {

            }
        };
    }
}
