package distributed;

import java.util.ArrayList;
import java.util.HashSet;

import distributed.Message.MessageTag;
public class Chandy implements Runnable {
    
    protected int distance_from_root = Integer.MAX_VALUE;

    protected int id;

    protected int num = 0;

    protected int num_processors;

    protected boolean termination_state = false; /* Active. */

    protected HashSet<Edge> in_edges = new HashSet<Edge>();
    protected HashSet<Edge> out_edges = new HashSet<Edge>();

    protected DistributedNode comm;

    private int parent_id = -1;

    private int root_id;
    
    public Chandy(int id, int root_id, int n, ArrayList<Edge> edges) {
        this.id = id;
        this.num_processors = n;
        this.root_id = root_id;

        for (Edge e : edges) {
            if(e.src == e.dest){
                continue;
            }
            if (e.dest == id) {
                in_edges.add(e);
            }
            else if (e.src == id) {
                out_edges.add(e);
            }
        }

        this.comm = new DistributedNode("localhost", convertToPort(id), this::receive);
    }
    
    @Override
    public void run() {
        System.out.println("Chandy is running");
        Thread t = new Thread(comm);
        t.start();

        while(!termination_state){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Chandy has terminated.");
    }

    protected Integer receive(Message message){
        MessageTag tag = message.getTag();

        if(termination_state){
            return 1;
        }
        switch(tag){
            case TAG_0: /* Receive a length message. */
            {
                String[] parts = message.getData().split("@");
                int possible_path_distance = Integer.parseInt(parts[0]);
                int possible_parent_id = Integer.parseInt(parts[1]);
                if(distance_from_root > possible_path_distance){
                    /* Send ack to parent. */
                    if(num > 0){
                        Message ack = new Message(convertToPort(id), convertToPort(possible_parent_id), MessageTag.TAG_1, "ACK");
                        comm.send("localhost", ack.getDestPort(), ack);
                    }
                    
                    /* Update state values. */
                    distance_from_root = possible_path_distance;
                    parent_id = possible_parent_id;

                    /* Send update messages to all outward edges (successors). */
                    for(Edge e : out_edges){
                        int new_dist = distance_from_root + e.weight;
                        String msgString = new_dist + "@" + id;
                        Message update = new Message(convertToPort(id), convertToPort(e.dest), MessageTag.TAG_0, msgString);
                        comm.send("localhost", update.getDestPort(), update);
                    }

                    /* Update number of unacknowledged. */
                    num += out_edges.size();

                    /* Once we receive all acks, then send back an ack. */
                    if(num == 0){
                        Message ack = new Message(convertToPort(id), convertToPort(parent_id), MessageTag.TAG_1, "ACK");
                        comm.send("localhost", ack.getDestPort(), ack);
                    }
                }
                else {
                    Message ack = new Message(convertToPort(id), convertToPort(possible_parent_id), MessageTag.TAG_1, "ACK");
                    comm.send("localhost", ack.getDestPort(), ack);
                }
                break;
            }
            case TAG_1: /* Ack message. */
            {
                /* Decrement number of unacknowledged. */
                num--;
                if(num == 0){
                    /* Send ack to parent. */
                    Message ack = new Message(convertToPort(id), convertToPort(parent_id), MessageTag.TAG_1, "ACK");
                    comm.send("localhost", ack.getDestPort(), ack);
                }
                break;
            }
            case TAG_2: /* Over- Message (set all distances to -infinity). */
            {
                if(distance_from_root != Integer.MIN_VALUE){
                    distance_from_root = Integer.MIN_VALUE;
                    for(Edge e : out_edges){
                        /* Send an over- message to all successors. */
                        Message over = new Message(convertToPort(id), convertToPort(e.dest), MessageTag.TAG_2, "OVER-");
                        comm.send("localhost", over.getDestPort(), over);
                    }
                }
                
                completeTermination();
                break;
            }
            case TAG_3: /* Over? Message. */
            {
                if(num > 0){
                    if(distance_from_root != Integer.MIN_VALUE){
                        distance_from_root = Integer.MIN_VALUE;
                        for(Edge e : out_edges){
                            /* Send an over- message to all successors. */
                            Message over = new Message(convertToPort(id), convertToPort(e.dest), MessageTag.TAG_2, "OVER-");
                            comm.send("localhost", over.getDestPort(), over);
                        }
                    }
                }
                else { /* Num is 0. */
                    if(distance_from_root != Integer.MIN_VALUE){
                        for(Edge e : out_edges){
                            /* Send an over? message to all successors. */
                            Message over = new Message(convertToPort(id), convertToPort(e.dest), MessageTag.TAG_3, "OVER?");
                            comm.send("localhost", over.getDestPort(), over);
                        }
                    }
                }

                completeTermination();
                break;
            }
            default:
                break;
        }
        return termination_state ? 1: 0;
    }

    protected void completeTermination()
    {
        String resultStr = distance_from_root + "@" + parent_id;
        Message result = new Message(convertToPort(id), convertToPort(root_id), MessageTag.TAG_4, resultStr);
        comm.send("localhost", result.getDestPort(), result);
        termination_state = true;
    }

    protected int convertToPort(int id){
        return id + 8080;
    }

    protected int convertToId(int port){
        return port - 8080;
    }
}
