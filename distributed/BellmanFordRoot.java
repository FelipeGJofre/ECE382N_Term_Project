package distributed;

import java.util.ArrayList;
import java.lang.Boolean;

public class BellmanFordRoot extends BellmanFord {
    
    ArrayList<Boolean> forbidden;

    public BellmanFordRoot(int n, ArrayList<Edge> edges) {  
        in_edges = new ArrayList<>();
        out_edges = new ArrayList<>();
        forbidden = new ArrayList<>();
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
            forbidden.add(true);
        }
    }

    public void Start() {
        /* While there exists a forbidden node. */
        boolean forbidden_exists = false;
        for(int i = 0; i < forbidden.size(); i++) {
            if(forbidden.get(i) == true) {
                forbidden_exists = true;
                break;
            }
        }

        while(forbidden_exists){
            
        }
    }
}
