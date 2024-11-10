package distributed;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Dijkstra {
    
    public class Edge {
        public final int src;
        public final int dest;
        public final int weight;
        
        public Edge(int src, int dest, int weight) {
            this.src = src;
            this.dest = dest;
            this.weight = weight;
        }
    }

    public ArrayList<Edge> in_edges;
    public ArrayList<Edge> out_edges;
    
    public int id; /* ID 0 is root. */
    public Integer distance_from_root;
    public boolean is_root;
    public boolean terminated;
    
    private DistributedNode comm_node;
    private LinkedBlockingQueue<Message> ack_message_queue = new LinkedBlockingQueue<>();

    public Dijkstra(int id, ArrayList<Edge> edges, boolean is_root) {
        in_edges = new ArrayList<>();
        out_edges = new ArrayList<>();
        this.id = id;
        this.is_root = is_root;
        this.terminated = false;

        comm_node = new DistributedNode("localhost", 8080 + id, this::ReceiveHandler);

        for (Edge edge : edges) {
            if(edge.src == edge.dest) {
                continue;
            }

            if(edge.dest == id) {
                in_edges.add(edge);
            }
            else if(edge.src == id) {
                out_edges.add(edge);
            }
        }
        
        if(is_root) {
            distance_from_root = 0;
        }
        else{
            distance_from_root = Integer.MAX_VALUE;
        }
    }

    protected Integer ReceiveHandler(Message message) {
        Message.MessageTag tag = message.getTag();

        if(tag == Message.MessageTag.TAG_0) { /* Receive an Explore message. */
            String data = message.getData();
            String[] parts = data.split(",");
            int distance = Integer.parseInt(parts[0]);
            int nodes_left = Integer.parseInt(parts[1]);
            int round_number = Integer.parseInt(parts[2]);
            
            distance_from_root = Math.min(distance_from_root, distance);
            if(nodes_left > 0){
                if(out_edges.size() == 0){

                }
            }
            else {
                String ack = round_number + "," + distance_from_root;
                for(Edge e: in_edges){
                    Message ack_message = new Message(id, e.src, Message.MessageTag.TAG_1, ack);
                    comm_node.send("localhost", e.src, ack_message);
                }
            }

            
        }
        else if(tag == Message.MessageTag.TAG_1) { /* Receive an Ack message. */
            ack_message_queue.add(message);

            if(ack_message_queue.size() == out_edges.size()){ /* Pass acks to predecessor edges. */
                for(Edge e: in_edges){
                    Message ack_message = new Message(id, e.src, Message.MessageTag.TAG_3, "");
                    comm_node.send("localhost", e.src + 1080, ack_message);
                }
            }
        }
        else if(tag == Message.MessageTag.TAG_2) { /* Receive a Broadcast message. */
            String data = message.getData();
            String[] parts = data.split(",");
            int distance = Integer.parseInt(parts[0]);
            int nodes_left = Integer.parseInt(parts[1]);
            int round_number = Integer.parseInt(parts[2]);
            
            distance_from_root = Math.min(distance_from_root, distance);
        }
        else if(tag == Message.MessageTag.TAG_3) { /* Receive a Terminate message. */
            popOutEdge(message.getSrcPort());
        }
        else {
            System.err.println("Invalid message tag received.");
        }
        return 0;
    }

    public void Start() {
        Thread nodeThread = new Thread(comm_node);
        nodeThread.start();
    }

    private Edge popInEdge(int src){
        for(int i = 0; i < in_edges.size(); i++) {
            if(in_edges.get(i).src == src) {
                return in_edges.remove(i);
            } 
        }
        return null;
    }

    private Edge popOutEdge(int dest){
        for(int i = 0; i < out_edges.size(); i++) {
            if(out_edges.get(i).dest == dest) {
                return out_edges.remove(i);
            } 
        }
        return null;
    }



    
}
