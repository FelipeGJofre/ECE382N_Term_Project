package distributed;

import java.util.ArrayList;

import distributed.Message.MessageTag;

import java.lang.Boolean;

public class BellmanFordRoot extends BellmanFord {
    
    protected ArrayList<Boolean> forbidden;
    protected int num_processes;
    protected ArrayList<Integer> weights_of_nodes;

    public BellmanFordRoot(int n, ArrayList<Edge> edges) {  
        super(0, n, edges);
        forbidden = new ArrayList<>();
        weights_of_nodes = new ArrayList<>();
        this.num_processes = n;

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
        
        for (int i = 0; i < num_processes; i++) {
            forbidden.add(true); // Init all to forbidden
        }
    }
    
    @Override
    public void start() {
        // Start algorithm by transmitting source node
        Thread t = new Thread(comm);
        t.start();
        this.forbidden.set(0, false);
        for(Edge e : this.out_edges){
            Message msg = new Message(this.port, getPort(e.dest), MessageTag.TAG_2, Integer.toString(0)); // Tag 2 is transmit weight
            comm.send("localhost", getPort(e.dest), msg);
        }

        /* While there exists a forbidden node, keep going. */
        boolean forbidden_exists = true;
        while(forbidden_exists){
            forbidden_exists = false;
            for(int i = 0; i < forbidden.size(); i++) {
                if(forbidden.get(i) == true) {
                    forbidden_exists = true;
                }
            }
        }
        System.out.println(this.forbidden.toString());

        for(int i = 1; i < this.num_processes; i++){
            Message msg = new Message(this.port, getPort(i), MessageTag.TAG_3, Integer.toString(0)); // Tag 3 is prompt to return value
            comm.send("localhost", getPort(i), msg);
        }
        while(this.weights_of_nodes.size() != this.forbidden.size()){    }
        
    }

    public ArrayList<Integer> get_shortest_path(){
        return this.weights_of_nodes;
    }

    @Override
    public Integer receive(Message msg){
        switch (msg.getTag()) {
            case TAG_0:
                this.forbidden.set(getId(msg.getSrcPort()), true);
                break;
            
            case TAG_1:
                this.forbidden.set(getId(msg.getSrcPort()), false);
                break;

            case TAG_4:
                this.weights_of_nodes.add(getId(msg.getSrcPort()), Integer.valueOf(msg.getData()));
                break;
            case TAG_2:
                System.out.println("I send my weight to myself");
                break;

            default:
                System.out.println("TRYING TO UPDATE ROOTS VALUE");
                break;
        }
        return 0;
    }
}
