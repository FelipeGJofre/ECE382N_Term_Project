package distributed;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.lang.*;

import distributed.Message.MessageTag;

public class Dijkstra {

    public int id;

    public int port;

    public int n;

    protected ArrayList<Edge> in_edges;
    protected ArrayList<Edge> out_edges;
    protected ArrayList<Integer> bfs_children_ids;

    protected DistributedNode comm;

    protected LinkedBlockingQueue<Message> ackQueue;

    protected int layer;

    private int distance_from_root;

    private int sssp_parent_id;

    private int bfs_parent_id;
    
    public Dijkstra(int id, int n, ArrayList<Edge> edges) throws IllegalArgumentException {
        this.id = id;
        this.port = id + 8080;
        this.n = n;

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

    public void start(){
        Thread t = new Thread(comm);
        t.start();
    }

    protected Integer receive(Message msg){
        return 0;
    }

    private void sendDistanceToRoot(){
        Message msg = new Message(this.port, getPort(sssp_parent_id), MessageTag.TAG_7, Integer.toString(this.distance_from_root));
        comm.send("localhost", getPort(sssp_parent_id), msg);
    }

    protected int getPort(int id){
        return id + 8080;
    }

    protected int getId(int port){
        return port - 8080;
    }
}
