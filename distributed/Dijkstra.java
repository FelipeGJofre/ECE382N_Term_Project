package distributed;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import distributed.Message.MessageTag;

public class Dijkstra extends Thread{

    public int id;

    public int port;

    public int n;

    public boolean terminated;

    protected ArrayList<Edge> in_edges = new ArrayList<Edge>();
    protected ArrayList<Edge> out_edges = new ArrayList<Edge>();
    protected HashSet<Integer> bfs_children_ids = new HashSet<Integer>();

    protected DistributedNode comm;

    protected LinkedBlockingQueue<Message> ackQueue;

    protected int layer;

    private int distance_from_root;

    private int sssp_parent_id;

    private int bfs_parent_id;

    private int root_id;
    
    public Dijkstra(int id, int n, int root_id, ArrayList<Edge> edges) throws IllegalArgumentException {
        this.id = id;
        this.port = id + 8080;
        this.n = n;
        this.root_id = root_id;
        this.terminated = false;

        for(Edge e : edges){
            if(e.src == e.dest){
                throw new IllegalArgumentException("Self loops are not allowed");
            }
            if(e.dest == id){
                in_edges.add(e);
            }
            if(e.src == id){
                out_edges.add(e);
            }
        }

        this.distance_from_root = Integer.MAX_VALUE;
        this.sssp_parent_id = -1;
        this.bfs_parent_id = -1;
        comm = new DistributedNode("localhost", port, this::receive);
        ackQueue = new LinkedBlockingQueue<>();
        layer = -1;
    }

    @Override
    public void run(){
        Thread t = new Thread(comm);
        t.start();

        while(!terminated){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Dijkstra has terminated.");
    }

    public void shutdown(){
        comm.shutdown();
    }

    protected Integer receive(Message msg){
        Message.MessageTag tag = msg.getTag();
        String[] msgParts = msg.getData().split("@");
        switch(tag){
            case TAG_0: /* Explore. */
            {
                int current_layer_exploring = Integer.parseInt(msgParts[0]);
                int this_distance = Integer.parseInt(msgParts[1]);
                if(distance_from_root > this_distance){
                    distance_from_root = this_distance;
                    sssp_parent_id = getId(msg.getSrcPort());
                }
                
                if(layer == -1){ /* Not discovered yet. */
                    /* Send back positive ack. */
                    bfs_parent_id = getId(msg.getSrcPort());
                    layer = current_layer_exploring;
                    
                    String msgString = Integer.toString(layer); /* Current layer. */
                    Message msgNew = new Message(this.port, msg.getSrcPort(), MessageTag.TAG_1, msgString);
                    comm.send("localhost", msg.getSrcPort(), msgNew);
                    
                }
                else if(layer == current_layer_exploring){ /* Already discovered. */
                    /* Send negative ack. */
                    String msgString = Integer.toString(layer);
                    Message msgNew = new Message(this.port, msg.getSrcPort(), MessageTag.TAG_2, msgString);
                    comm.send("localhost", msg.getSrcPort(), msgNew);
                }
                else {
                    /* Pass message. */
                    for(Edge e: out_edges){
                        String msgString = Integer.toString(current_layer_exploring) + "@" + Integer.toString(this_distance + e.weight);
                        Message msgNew = new Message(this.port, getPort(e.dest), MessageTag.TAG_0, msgString);
                        comm.send("localhost", getPort(e.dest), msgNew);
                    }
                }
                break;
            }
            case TAG_1: /* Positive ack. */
            {
                ackQueue.add(msg);
                bfs_children_ids.add(getId(msg.getSrcPort()));
                break;
            }
            case TAG_2: /* Negative ack. */
            {
                ackQueue.add(msg);
                bfs_children_ids.remove(getId(msg.getSrcPort()));   
                break;
            }
            case TAG_3: /* Number of children. */
            {
                int num_new_children = Integer.parseInt(msg.getData());
                Message msgNew = new Message(this.port, getPort(bfs_parent_id), MessageTag.TAG_3, Integer.toString(num_new_children));
                comm.send("localhost", getPort(bfs_parent_id), msgNew);
                break;
            }
            case TAG_4:
            {
                String msgString = Integer.toString(bfs_parent_id) + "@" + Integer.toString(distance_from_root) + "@" + Integer.toString(sssp_parent_id);
                Message msgNew = new Message(this.port, getPort(root_id), MessageTag.TAG_4, msgString);
                comm.send("localhost", getPort(root_id), msgNew);
                terminated = true;
                return 1;
            }
            default:
                break;   
        }
        if(sendbackAck()){
            String msgString = Integer.toString(bfs_children_ids.size());
            Message msgNew = new Message(this.port, getPort(bfs_parent_id), MessageTag.TAG_3, msgString);
            comm.send("localhost", getPort(bfs_parent_id), msgNew);
        }
        return 0;
    }

    protected int getPort(int id){
        return id + 8080;
    }

    protected int getId(int port){
        return port - 8080;
    }

    private boolean sendbackAck(){
        if(ackQueue.size() == out_edges.size()){
            return true;
        }
        return false;
    }
}
