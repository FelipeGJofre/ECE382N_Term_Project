package distributed;
public class Main{
    public static void main(String[] args) {
        // Create three nodes on different ports
        DistributedNode[] nodes = new DistributedNode[3];
        Thread[] nodeThreads = new Thread[3];
        
        // Initialize and start nodes
        for (int i = 0; i < 3; i++) {
            final int port = 8080 + i;
            nodes[i] = new DistributedNode("localhost", port);
            nodeThreads[i] = new Thread(nodes[i]);
            nodeThreads[i].start();
        }

        // Wait for nodes to initialize
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Demonstrate message passing between nodes
        try {
            // Node 0 sends message to Node 1
            nodes[0].send("localhost", 8081, "Hello from Node 0 to Node 1!");
            Thread.sleep(500);

            // Node 1 sends message to Node 2
            nodes[1].send("localhost", 8082, "Hello from Node 1 to Node 2!");
            Thread.sleep(500);

            // Node 2 sends message to Node 0
            nodes[2].send("localhost", 8080, "Hello from Node 2 to Node 0!");
            Thread.sleep(500);

            // Broadcast from Node 0 to all other nodes
            System.out.println("\nBroadcasting message from Node 0:");
            for (int i = 1; i < nodes.length; i++) {
                nodes[0].send("localhost", 8080 + i, "Broadcast message from Node 0!");
            }

            // Keep the system running for a while to observe messages
            Thread.sleep(2000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Shutdown all nodes
            System.out.println("\nShutting down all nodes...");
            for (DistributedNode node : nodes) {
                node.shutdown();
            }
        }
    }
}

/*
 * Ideas for implementing our detection algorithm:
 * - We have one process per boolean predicate
 * - Each one communicates to the others whether another process needs to
 * advance, as in stable marrige, or if the local predicate is false or not
 * - Algorithm only terminates once all the messages express to each other that there are no forbidden
 * states or that the local predicates can be resolved
 * 
 * 
 */