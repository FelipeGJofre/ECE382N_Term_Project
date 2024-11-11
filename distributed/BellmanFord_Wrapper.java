package distributed;

import java.util.ArrayList;

public class BellmanFord_Wrapper {
    
    private BellmanFordRoot root;

    private ArrayList<BellmanFord> nodes;

    public void start(int n, ArrayList<Edge> edges){
        
        root = new BellmanFordRoot(n, edges);
        // System.out.println("Root created!");
        Thread root_thread = new Thread(() -> root.start());
        root_thread.start();
        nodes = new ArrayList<>();
        for(int i = 1; i < n; i++){
            BellmanFord test = new BellmanFord(i, n, edges);
            // System.out.println("Node " + Integer.toString(i) + " created!");
            nodes.add(test);
            Thread thread = new Thread(() -> test.start());
            thread.start();
        }
        try{
            root_thread.join();
        } catch(InterruptedException e){}
        ArrayList<Integer> paths = root.get_shortest_path();
        System.out.println(paths.toString());
    }
}
