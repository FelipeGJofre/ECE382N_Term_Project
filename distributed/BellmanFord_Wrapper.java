package distributed;

import java.util.ArrayList;

public class BellmanFord_Wrapper {
    
    private BellmanFordRoot root;

    private ArrayList<BellmanFord> nodes;

    public void start(int n, ArrayList<Edge> edges){
        
        root = new BellmanFordRoot(n, edges);
        Thread root_thread = new Thread(root);
        root_thread.start();
        nodes = new ArrayList<>();
        for(int i = 1; i < n; i++){
            BellmanFord test = new BellmanFord(i, n, edges);
            nodes.add(test);
            Thread thread = new Thread(test);
            thread.start();
        }
        try{
            root_thread.join();
        } catch(InterruptedException e){}
        ArrayList<Integer> paths = root.get_shortest_path();
        System.out.println(paths.toString());

        int numMsgsSent = root.num_messages_sent;
        for(BellmanFord node : nodes){
            numMsgsSent += node.num_messages_sent;
        }
        System.out.println("Total number of messages sent: " + numMsgsSent);
    }
}
