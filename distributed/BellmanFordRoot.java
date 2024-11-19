package distributed;

import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.atomic.AtomicBoolean;

import distributed.Message.MessageTag;

import java.lang.Boolean;

public class BellmanFordRoot extends BellmanFord {
    
    protected ArrayList<Boolean> forbidden;
    protected int num_processes;
    protected ArrayList<Integer> weights_of_nodes;

    private int num_tag4_rcvd = 0;

    private boolean forbidden_exists = true;
    private int num_ack_forbidden = 0;

    public BellmanFordRoot(int n, ArrayList<Edge> edges) {  
        super(0, n, edges);
        forbidden = new ArrayList<>();
        weights_of_nodes = new ArrayList<>(n);
        this.num_processes = n;
        
        for (int i = 0; i < num_processes; i++) {
            forbidden.add(true); // Init all to forbidden
            if(i == 0) {
                weights_of_nodes.add(0);
            }
            else {
                weights_of_nodes.add(Integer.MAX_VALUE);
            }
        }
    }
    
    @Override
    public void run() {
        // Start algorithm by transmitting source node
        Thread t = new Thread(comm);
        t.start();
        this.forbidden.set(0, false);
        for(Edge e : this.out_edges){
            Message msg = new Message(this.port, getPort(e.dest), MessageTag.TAG_2, Integer.toString(0)); // Tag 2 is transmit weight
            comm.send("localhost", getPort(e.dest), msg);
        }

        /* While there exists a forbidden node, keep going. */
        while(forbidden_exists){
            num_ack_forbidden = 0;
            for(int i = 1; i < this.num_processes; i++){
                Message msg = new Message(this.port, getPort(i), MessageTag.TAG_0, "STARTCHECK"); // Tag 0 is starting to check for forbidden
                comm.send("localhost", getPort(i), msg);
            }
            while(num_ack_forbidden < this.num_processes - 1){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            forbidden_exists = false;
            for(int i = 1; i < this.num_processes; i++){
                if(this.forbidden.get(i)){
                    forbidden_exists = true;
                    break;
                }
            }

        }
        System.out.println(this.forbidden.toString());

        for(int i = 1; i < this.num_processes; i++){
            Message msg = new Message(this.port, getPort(i), MessageTag.TAG_3, Integer.toString(0)); // Tag 3 is prompt to return value
            comm.send("localhost", getPort(i), msg);
        }
        
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
            System.out.println("BellmanFordRoot Thread " + this.id + " has terminated.");
        }
    }

    public ArrayList<Integer> get_shortest_path(){
        return this.weights_of_nodes;
    }

    @Override
    public Integer receive(Message msg){
        switch (msg.getTag()) {
            case TAG_0:
                System.out.println("Received forbidden check from " + getId(msg.getSrcPort()));
                if(Integer.valueOf(msg.getData()) == 1){
                    this.forbidden.set(getId(msg.getSrcPort()), true);
                }
                else{
                    this.forbidden.set(getId(msg.getSrcPort()), false);
                }
                num_ack_forbidden++;
                break;
            case TAG_1:
                System.out.println("This is muy bad");
                break;

            case TAG_4:
                this.weights_of_nodes.set(getId(msg.getSrcPort()), Integer.valueOf(msg.getData()));
                if(++num_tag4_rcvd == this.num_processes - 1){
                    termination_state = true;
                }
                break;
            case TAG_2:
                System.out.println("I send my weight to myself");
                break;

            default:
                System.out.println("TRYING TO UPDATE ROOTS VALUE");
                break;
        }
        return termination_state ? 1: 0;
    }
}
