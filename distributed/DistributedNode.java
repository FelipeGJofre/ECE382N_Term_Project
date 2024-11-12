package distributed;

import java.util.function.Function;
import java.lang.Integer;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class DistributedNode implements Runnable {
    private final int port;
    private final String host;
    private volatile boolean running;
    
    private ServerSocket serverSocket;
    private final BlockingQueue<String> messageQueue;
    private final ExecutorService executorService;

    private final Function<Message, Integer> messageHandler;

    public DistributedNode(String host, int port, Function<Message, Integer> messageHandler) {
        this.host = host; /* Used for debugging. */
        this.port = port; 
        this.messageQueue = new LinkedBlockingQueue<>();
        this.executorService = Executors.newFixedThreadPool(2);
        this.running = true;
        this.messageHandler = messageHandler;
    }

    @Override
    public void run() {
        try {
            /* We use a server socket as a global socket (i.e., anyone can connect). */
            serverSocket = new ServerSocket(port);
            System.out.println("Node started on port: " + port);

            /* Have two threads: one processes messages' data, other receives messages from socket. */
            executorService.submit(this::processMessages);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(() -> handleConnection(clientSocket));
            }
        } catch (IOException e) {
            if(e.getMessage().equals("Socket closed")){
                System.out.println("Node shutdown.");
            } else {
                System.err.println("Error starting node: " + e.getMessage());
            }
        } finally {
            shutdown();
        }
    }

    private void handleConnection(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()))) {
            String message;
            while ((message = reader.readLine()) != null) {
                messageQueue.put(message);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error handling connection: " + e.getMessage());
        }
    }

    private void processMessages() {
        while (running) {
            try {
                String message = messageQueue.poll(1, TimeUnit.SECONDS);
                if (message != null) {
                    System.out.println("Received message: " + message);
                    Integer finish = messageHandler.apply(Message.fromString(message));
                    if(finish == 1){
                        running = false;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("Message processing stopped.");
        shutdown();
    }

    public void send(String destinationHost, int destinationPort, Message message) {
        System.out.println("Sending message: " + message.toString());
        try (Socket socket = new Socket(destinationHost, destinationPort); 
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
                String charMsg = message.toString();
                writer.println(charMsg);
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    public void shutdown() {
        running = false;
        executorService.shutdownNow();
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }

        System.out.println("Node shutdown.");
    }
}