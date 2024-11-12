package distributed;

import java.util.ArrayList;

public class DijkstraRoot extends Dijkstra {

    private int current_layer_exploring;

    private int num_ids_explored;

    private int final_messages;

    public DijkstraRoot(int id, int n, ArrayList<Edge> edges) throws IllegalArgumentException {
        super(id, n, id, edges);
        layer = 0;
        current_layer_exploring = 1;

        num_ids_explored = 0;
        final_messages = 0;
    }

    @Override
    public void run() {
        Thread t = new Thread(comm);
        t.start();

        /* Start BFS search for all nodes in the system. */
        for(Edge e : out_edges){
            /* TAG_0 is the exploration message. */
            String msgString = Integer.toString(current_layer_exploring) + "@" + Integer.toString(e.weight);
            Message msg = new Message(this.port, getPort(e.dest), Message.MessageTag.TAG_0, msgString);
            comm.send("localhost", getPort(e.dest), msg);
        }

        while(!terminated){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("DijkstraRoot has terminated.");
    }

    @Override
    protected Integer receive(Message msg) {
        Message.MessageTag tag = msg.getTag();
        switch(tag){
            case TAG_1: /* Ack Positive. */
                ackQueue.add(msg);
                break;
            case TAG_2: /* Ack Negative. */
                ackQueue.add(msg);
                break;
            case TAG_3:
                int num_new_children = Integer.parseInt(msg.getData());
                num_ids_explored += num_new_children;
                break;
            case TAG_4:
                /* Received ack from all nodes. */
                String[] msgParts = msg.getData().split("@");
                System.out.println("ID " + getId(msg.getSrcPort()) + ": " + msgParts[0] + " " + msgParts[1] + " " + msgParts[2]);
                final_messages++;
                break;
            default:
                break;
        }
        moveNextLayer();
        if(final_messages == n - 1){
            /* All nodes have sent their final messages. */
            System.out.println("All nodes have sent their final messages.");
            terminated = true;
            return 1;
        }
        return 0;
    }

    private boolean moveNextLayer(){
        /* Check if all nodes have been explored. */
        if(num_ids_explored == n - 1 && final_messages == 0){
            /* All nodes have been explored. */
            for(int i = 1; i < n; i++){
                Message msg = new Message(this.port, getPort(i), Message.MessageTag.TAG_4, "FINISHED");
                comm.send("localhost", getPort(i), msg);
            }
            return false;
        }
        
        /* Check if all nodes in the current layer have been explored. */
        if(ackQueue.size() == out_edges.size()){
            /* Move to the next layer. */
            current_layer_exploring++;
            /* Increase the number of nodes explored */
            num_ids_explored += ackQueue.size();
            /* Reset the ackQueue. */
            ackQueue.clear();
            
            System.out.println("We have " + num_ids_explored + " nodes explored.");
            
            /* Send messages to all nodes in the next layer. */
            for(Edge e : out_edges){
                String msgString = Integer.toString(current_layer_exploring) + "@" + Integer.toString(e.weight);
                /* Send an explore message. It wil actually explore once it encounters a node in l - 1. */
                Message msg = new Message(this.port, getPort(e.dest), Message.MessageTag.TAG_0, msgString);
                comm.send("localhost", getPort(e.dest), msg);
            }
            return true;
        }
        return false;
    }


}
