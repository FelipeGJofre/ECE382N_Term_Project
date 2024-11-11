package distributed;

import java.util.ArrayList;

public class DijkstraRoot extends Dijkstra {

    private int current_layer;

    private ArrayList<Integer> explored_ids;

    public DijkstraRoot(int id, int n, ArrayList<Edge> edges) throws IllegalArgumentException {
        super(id, n, edges);
        layer = 0;
        current_layer = 1;

        explored_ids = new ArrayList<>();
    }

    @Override
    public void start() {
        Thread t = new Thread(comm);
        t.start();

        /* Start BFS search for all nodes in the system. */
        for(Edge e : out_edges){
            /* TAG_0 is the exploration message. */
            Message msg = new Message(this.port, getPort(e.dest), Message.MessageTag.TAG_0, Integer.toString(0));
            comm.send("localhost", getPort(e.dest), msg);
        }
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
            default:
                break;
        }
        moveNextLayer();
        return 0;
    }

    private boolean moveNextLayer(){
        /* Check if all nodes in the current layer have been explored. */
        if(ackQueue.size() == out_edges.size()){
            /* Move to the next layer. */
            current_layer++;
            /* Reset the ackQueue. */
            ackQueue.clear();

            /* Send messages to all nodes in the next layer. */
            for(Edge e : out_edges){
                Message msg = new Message(this.port, getPort(e.dest), Message.MessageTag.TAG_0, Integer.toString(current_layer));
                comm.send("localhost", getPort(e.dest), msg);
            }
            return true;
        }
        return false;
    }


}
