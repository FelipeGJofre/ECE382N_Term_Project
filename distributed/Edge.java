package distributed;

/**
 * Represents an edge in a graph. Edges are directed and have a weight.
 */
public class Edge {
    public final int src;
    public final int dest;
    public final int weight;
    
    public Edge(int src, int dest, int weight) {
        this.src = src;
        this.dest = dest;
        this.weight = weight;
    }
}
