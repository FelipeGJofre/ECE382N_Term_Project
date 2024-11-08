import java.util.ArrayList;
import java.lang.Integer;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

package ECE382N_Term_Project;

public class DistributedNode implements Runnable {
    private final int port;
    private final String host;
    private volatile boolean running;
    private ServerSocket serverSocket;
    private final BlockingQueue<String> messageQueue;
    private final ExecutorService executorService;

    public DistributedNode(String host, int port) {
        this.host = host;
        this.port = port;
        this.messageQueue = new LinkedBlockingQueue<>();
        this.executorService = Executors.newFixedThreadPool(2);
        this.running = true;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Node started on port: " + port);

            // Start message processor
            executorService.submit(this::processMessages);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(() -> handleConnection(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Error in node operation: " + e.getMessage());
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
                    // Process message here
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void send(String destinationHost, int destinationPort, String message) {
        try (Socket socket = new Socket(destinationHost, destinationPort);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
            writer.println(message);
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
    }
}