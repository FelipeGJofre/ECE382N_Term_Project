package distributed;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

import distributed.Message.MessageTag;

public class BellmanFord{
    
public int id;

    public int port;

    protected ArrayList<Edge> in_edges;
    protected ArrayList<Edge> out_edges;
    protected ArrayList<Integer> weight_pred;

    protected DistributedNode comm;

    private int my_distance_from_root;

    private boolean true_start;
    private boolean sent_forbidden;

    
    public BellmanFord(int id, int n, ArrayList<Edge> edges) throws IllegalArgumentException {
        this.id = id;
        this.port = id + 8080;
        this.true_start = false;
        this.sent_forbidden = false;
        in_edges = new ArrayList<>();
        out_edges = new ArrayList<>();
        weight_pred = new ArrayList<>();

        for(Edge e : edges){
            if(e.src == e.dest){
                throw new IllegalArgumentException("Self loops are not allowed");
            }
            if(e.dest == id){
                in_edges.add(e);
                weight_pred.add(Integer.MAX_VALUE);
            }
            if(e.src == id){
                out_edges.add(e);
            }
        }
        this.my_distance_from_root = Integer.MAX_VALUE;
        comm = new DistributedNode("localhost", port, this::receive);
        // System.out.println("At start from thread " + this.id + " :" + this.weight_pred.toString());
    }

    public void start(){
        Thread t = new Thread(comm);
        t.start();

        while(true){
            // if(this.id != 2) System.out.println("In thread " + this.id + ", true_start is " + this.true_start);
            if(forbidden()){
                Message msg = new Message(this.port, getPort(0), MessageTag.TAG_0, Integer.toString(0));    // Tag 0 is forbidden
                comm.send("localhost", getPort(0), msg);
                this.sent_forbidden = true;
                if(this.true_start) advance();
            }
            else{
                if(this.true_start && this.sent_forbidden){
                    Message msg = new Message(this.port, getPort(0), MessageTag.TAG_1, Integer.toString(0)); // Tag 1 is not forbidden
                    comm.send("localhost", getPort(0), msg);
                    this.sent_forbidden = false;
                }
            }

        }
    }

    public Integer receive(Message msg){
        switch (msg.getTag()) {
            case TAG_2:
                System.out.println("New Weight from " + getId(msg.getSrcPort()) + " of weight " + msg.getData() + " in thread " + this.id);
                for(int i = 0; i < this.in_edges.size(); i++){
                    if(this.in_edges.get(i).src == getId(msg.getSrcPort())){
                        this.weight_pred.set(i, Integer.valueOf(msg.getData()));
                    }
                }
                break;
            case TAG_3:
                sendDistanceToRoot();
                break;
        
            default:
            System.out.println("Something has gone horribly wrong!");
                break;
        }
        System.out.println("From thread " + this.id + ":" + this.weight_pred.toString());
        return 0;
    }

    protected int getPort(int id){
        return id + 8080;
    }

    protected int getId(int port){
        return port - 8080;
    }

    protected boolean forbidden(){
        for(int i = 0; i < this.weight_pred.size(); i++){
            if(my_distance_from_root == Integer.MAX_VALUE && this.weight_pred.get(i) == Integer.MAX_VALUE) continue;
            if(my_distance_from_root > this.in_edges.get(i).weight + this.weight_pred.get(i)){
                // System.out.println("My dist: " + my_distance_from_root + ", new dist: " + (this.in_edges.get(i).weight + this.weight_pred.get(i)));
                this.true_start = true;
                return true;
            } 
        }
        return false;
    }

    protected void advance(){
        for(int i = 0; i < this.weight_pred.size(); i++){
            if(my_distance_from_root > this.in_edges.get(i).weight + this.weight_pred.get(i)) 
                my_distance_from_root = this.in_edges.get(i).weight + this.weight_pred.get(i);
        }

        for(Edge e : this.out_edges){
            Message msg = new Message(this.port, getPort(e.dest), MessageTag.TAG_2, Integer.toString(this.my_distance_from_root)); // Tag 2 is transmit weight
            comm.send("localhost", getPort(e.dest), msg); 
        }

    }

    private void sendDistanceToRoot(){
        Message msg = new Message(this.port, getPort(0), MessageTag.TAG_4, Integer.toString(this.my_distance_from_root));
        comm.send("localhost", getPort(0), msg);
    }
    
}
