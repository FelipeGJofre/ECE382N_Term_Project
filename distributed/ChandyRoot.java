package distributed;

import java.util.ArrayList;

import distributed.Message.MessageTag;
public class ChandyRoot extends Chandy {

    public class Result {

        private int distance;

        private int parent_id;

        public Result(int distance, int parent_id) {
            this.distance = distance;
            this.parent_id = parent_id;
        }

        public int getDistance() {return this.distance; }
        public int getParent() {return this.parent_id; }
        public void println() {
            System.out.println("Distance: " + distance + " Parent: " + parent_id);
        }
    }

    ArrayList<Result> results = new ArrayList<>();

    private int num_final_recv = 1;
    
    public ChandyRoot(int id, int n, ArrayList<Edge> edges) {
        super(id, id, n, edges);
        this.distance_from_root = 0;
        this.num = out_edges.size();
        for(int i = 0; i < n; i++){
            /* Will be replaced with actual values at the end of the algorithm. */
            Result r = new Result(-1, -1);
            results.add(i, r);
        }
    }

    public ArrayList<Result> getResults(){
        return results;
    }
    
    @Override
    public void run() {
        System.out.println("ChandyRoot is running");
        Thread t = new Thread(comm);
        t.start();

        /* Initiate Phase 1 */
        for(Edge e : out_edges){
            String msgString = e.weight + "@" + id;
            Message m = new Message(convertToPort(id), convertToPort(e.dest), Message.MessageTag.TAG_0, msgString);
            comm.send("localhost", m.getDestPort(), m);
        }

        while(!termination_state){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("ChandyRoot is terminating");
        comm.shutdown();
    }

    @Override
    public Integer receive(Message message){
        MessageTag tag = message.getTag();

        if(termination_state){
            return 1;
        }
        switch(tag){
            case TAG_0: /* Length message. */
            {
                String[] parts = message.getData().split("@");
                int possible_path_distance = Integer.parseInt(parts[0]);
                int possible_path_parent = Integer.parseInt(parts[1]);

                if(possible_path_distance < 0){
                    /* Negative Cycle Detected, so start phase II. */
                    for(Edge e: out_edges){
                        /* Send Over- messages to all successors. */
                        Message over = new Message(convertToPort(id), convertToPort(e.dest), MessageTag.TAG_2, "OVER-");
                        comm.send("localhost", over.getDestPort(), over);
                    }
                }
                else {
                    /* Not a negative cycle, so return ack. */
                    Message ack = new Message(convertToPort(id), convertToPort(possible_path_parent), MessageTag.TAG_1, "ACK");
                    comm.send("localhost", ack.getDestPort(), ack);
                }
                break;
            }
            case TAG_1: /* Ack message. */
            {
                /* Decrement number of unacknowledged. */
                num--;
                if (num == 0){
                    /* End of Phase I as we received all acks. */
                    for(Edge e: out_edges){
                        /* Send Over? messages to all successors. */
                        Message over = new Message(convertToPort(id), convertToPort(e.dest), MessageTag.TAG_3, "OVER?");
                        comm.send("localhost", over.getDestPort(), over);
                    }
                }
                break;
            }
            case TAG_2: /* Over- Message. */
            {
                /* If received, should already be terminated since it started the chain. */
                break;
            }
            case TAG_3: /* Over? Message. */
            {
                /* If received, should already be terminated since it started the chain. */
                break;
            }
            case TAG_4:
            {
                int sender_id = convertToId(message.getSrcPort());
                String[] parts = message.getData().split("@");
                int distance_from_root = Integer.parseInt(parts[0]);
                int parent_id = Integer.parseInt(parts[1]);
                results.set(sender_id, new Result(distance_from_root, parent_id));
                if(++num_final_recv == num_processors){
                    completeTermination();
                }
                break;
            }
            default:
                break;
        }
        return termination_state ? 1 : 0;
    }

    @Override
    protected void completeTermination()
    {
        results.set(id, new Result(0, -1));
        termination_state = true;
    }
}