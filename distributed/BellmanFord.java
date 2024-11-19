package distributed;

import java.util.ArrayList;
import distributed.Message.MessageTag;

public class BellmanFord implements Runnable {
    
public int id;

    public int port;

    protected ArrayList<Edge> in_edges;
    protected ArrayList<Edge> out_edges;
    protected ArrayList<Integer> weight_pred;

    protected DistributedNode comm;

    private int my_distance_from_root;
    protected boolean termination_state = false;

    public int num_messages_recv = 0;
    public int num_messages_sent = 0;

    
    public BellmanFord(int id, int n, ArrayList<Edge> edges) throws IllegalArgumentException {
        this.id = id;
        this.port = id + 8080;
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
    }

    @Override
    public void run(){
        Thread t = new Thread(comm);
        t.start();

        long start_time = System.nanoTime();
        while(!termination_state){
           try {
               Thread.sleep(10);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
        }
        comm.shutdown();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            long end_time = System.nanoTime();
            long time_taken = (end_time - start_time) / 1000000; // ms
            // System.out.println("BellmanFord Thread " + this.id + " has terminated.");
            System.out.println(this.id + ": Time taken: " + time_taken + " ms.");

            num_messages_sent = comm.getNumMessagesSent();
            num_messages_recv = comm.getNumMessagesRecv();
        }
        
    }

    public Integer receive(Message msg){
        switch (msg.getTag()) {
            case TAG_0:
            {
                if(forbidden()){
                    Message ack = new Message(this.port, getPort(0), MessageTag.TAG_0, String.valueOf(1));    // Tag 0 is forbidden
                    comm.send("localhost", getPort(0), ack);
                    advance();
                }
                else{
                    Message ack = new Message(this.port, getPort(0), MessageTag.TAG_0, String.valueOf(0)); // Tag 1 is not forbidden
                    comm.send("localhost", getPort(0), ack);
                }
                break;
            }
            case TAG_1:
                break;
            case TAG_2:
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
                break;
        }
        return termination_state ? 1: 0;
    }

    protected int getPort(int id){
        return id + 8080;
    }

    protected int getId(int port){
        return port - 8080;
    }

    protected boolean forbidden(){
        for(int i = 0; i < this.weight_pred.size(); i++){
            int new_weight = 0;
            if(this.weight_pred.get(i) == Integer.MAX_VALUE){
                new_weight = Integer.MAX_VALUE;
            }
            else{
                new_weight = this.in_edges.get(i).weight + this.weight_pred.get(i);
            }

            if(my_distance_from_root > new_weight){
                return true;
            } 
        }
        return false;
    }

    protected void advance(){
        for(int i = 0; i < this.weight_pred.size(); i++){
            int new_weight = 0;
            if(this.weight_pred.get(i) == Integer.MAX_VALUE){
                new_weight = Integer.MAX_VALUE;
            }
            else{
                new_weight = this.in_edges.get(i).weight + this.weight_pred.get(i);
            }
            if(my_distance_from_root > new_weight) 
                my_distance_from_root = new_weight;
        }

        for(Edge e : this.out_edges){
            Message msg = new Message(this.port, getPort(e.dest), MessageTag.TAG_2, Integer.toString(this.my_distance_from_root)); // Tag 2 is transmit weight
            comm.send("localhost", getPort(e.dest), msg); 
        }

    }

    private void sendDistanceToRoot(){
        Message msg = new Message(this.port, getPort(0), MessageTag.TAG_4, Integer.toString(this.my_distance_from_root));
        comm.send("localhost", getPort(0), msg);
        termination_state = true;
    }
    
}
