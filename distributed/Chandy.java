package distributed;

import java.util.ArrayList;
import java.util.HashSet;
public class Chandy implements Runnable {
    
    protected int distance_from_root;

    protected int id;

    protected int num;

    protected HashSet<Edge> in_edges = new HashSet<Edge>();
    protected HashSet<Edge> out_edges = new HashSet<Edge>();

    private int parent_id;

    private int root_id;
    
    public Chandy(int id, int root_id, int n, ArrayList<Edge> edges) {
        this.distance_from_root = Integer.MAX_VALUE;
        this.id = id;
        this.num = n;
        this.root_id = root_id;
        this.parent_id = -1;

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

    }
    
    @Override
    public void run() {
        System.out.println("Chandy is running");
    }
}
