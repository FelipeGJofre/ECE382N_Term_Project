package distributed;

import java.util.ArrayList;

public class BellmanFord {
    
    ArrayList<Edge> in_edges;
    ArrayList<Edge> out_edges;

    int id; /* ID 0 is root. */
    int num_processes; /* Number of processes in the network. */
}
