package distributed;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Examples {

    public HashMap<String, ArrayList<Edge>> examples = new HashMap<String, ArrayList<Edge>>();
    public HashMap<String, Integer> num_nodes = new HashMap<String, Integer>();

    private Random rand = new Random();

    private boolean allow_negative_weights;

    private int max_magnitude_weights;

    public Examples(boolean allow_negative_weights, int max_magnitude_weights){
        this.allow_negative_weights = allow_negative_weights;
        this.max_magnitude_weights = max_magnitude_weights;
        
        Line();
        Triangle();
        NegCycle();
        NegCycle2();
        Texas();
    }

    public ArrayList<Edge> getExample(String example){
        if(examples.get(example) == null)
            return null;
        else
            return examples.get(example);
    }

    public int getNumNodes(String example){
        if(num_nodes.get(example) == null)
            return -1;
        else
            return num_nodes.get(example);
    }

    private void Line(){
        ArrayList<Edge> edges = new ArrayList<Edge>();
        ArrayList<Integer> weights = new ArrayList<>(Arrays.asList(-1, 2, -3, 4, -5, 6, -7, 8));
        for(int i = 0; i < weights.size(); i++)
        {
            edges.add(new Edge(i, i + 1, weights.get(i)));
        }
        examples.put("Line", edges);
        num_nodes.put("Line", edges.size() + 1);
    }

    /* Creates 4 nodes, 3 making a triangle, and the root node in the center. */
    private void Triangle(){
        ArrayList<Edge> edges = new ArrayList<Edge>();
        edges.add(new Edge(0, 1, 1));
        edges.add(new Edge(0, 2, 2));
        edges.add(new Edge(0, 3, 3));
        edges.add(new Edge(1, 3, 1));
        edges.add(new Edge(3, 2, -2));
        edges.add(new Edge(2, 1, 1));
        examples.put("Triangle", edges);
        num_nodes.put("Triangle", 4);
    }

    private void NegCycle(){
        ArrayList<Edge> edges = new ArrayList<Edge>();
        edges.add(new Edge(0, 1, 1));
        edges.add(new Edge(1, 2, 1));
        edges.add(new Edge(2, 3, 1));
        edges.add(new Edge(3, 0, -10));
        examples.put("NegCycle", edges);
        num_nodes.put("NegCycle", 4);
    }

    private void NegCycle2(){
        ArrayList<Edge> edges = new ArrayList<Edge>();
        edges.add(new Edge(0, 1, 1));
        edges.add(new Edge(0, 2, 2));
        edges.add(new Edge(0, 3, 3));
        edges.add(new Edge(1, 3, 1));
        edges.add(new Edge(3, 2, -2));
        edges.add(new Edge(2, 1, -1));
        examples.put("NegCycle2", edges);
        num_nodes.put("NegCycle2", 4);
    }

    private enum TexasNode {
        HOUSTON(0),
        DALLAS(1),
        AUSTIN(2),
        SAN_ANTONIO(3),
        EL_PASO(4),
        FORT_WORTH(5),
        CORPUS_CHRISTI(6),
        LAREDO(7),
        MAX_VALUE(8);

        private final int value;
        private TexasNode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    };

    /**
     *  @brief Map of the interstates in texas interacting with cities. 
     *  Node maps:
     *  - 0: Houston
     * - 1: Dallas
     * - 2: Austin
     * - 3: San Antonio
     * - 4: El Paso
     * - 5: Fort Worth
     * - 6: Corpus Christi
     * - 7: Laredo
     * 
     */
    private void Texas(){
        ArrayList<Edge> edges = new ArrayList<Edge>();
        /* Start with the big 4 cause I can't. */
        edges.add(new Edge(TexasNode.HOUSTON.getValue(), TexasNode.DALLAS.getValue(), 7));
        edges.add(new Edge(TexasNode.DALLAS.getValue(), TexasNode.HOUSTON.getValue(), 7));

        edges.add(new Edge(TexasNode.HOUSTON.getValue(), TexasNode.SAN_ANTONIO.getValue(), 7));
        edges.add(new Edge(TexasNode.SAN_ANTONIO.getValue(), TexasNode.HOUSTON.getValue(), 7));

        edges.add(new Edge(TexasNode.SAN_ANTONIO.getValue(), TexasNode.AUSTIN.getValue(), 3));
        edges.add(new Edge(TexasNode.AUSTIN.getValue(), TexasNode.SAN_ANTONIO.getValue(), 3));

        edges.add(new Edge(TexasNode.FORT_WORTH.getValue(), TexasNode.AUSTIN.getValue(), 8));
        edges.add(new Edge(TexasNode.AUSTIN.getValue(), TexasNode.FORT_WORTH.getValue(), 8));

        edges.add(new Edge(TexasNode.EL_PASO.getValue(), TexasNode.SAN_ANTONIO.getValue(), 17));
        edges.add(new Edge(TexasNode.SAN_ANTONIO.getValue(), TexasNode.EL_PASO.getValue(), 17));

        edges.add(new Edge(TexasNode.EL_PASO.getValue(), TexasNode.FORT_WORTH.getValue(), 14));
        edges.add(new Edge(TexasNode.FORT_WORTH.getValue(), TexasNode.EL_PASO.getValue(), 14));

        edges.add(new Edge(TexasNode.FORT_WORTH.getValue(), TexasNode.DALLAS.getValue(), 2));
        edges.add(new Edge(TexasNode.DALLAS.getValue(), TexasNode.FORT_WORTH.getValue(), 2));

        edges.add(new Edge(TexasNode.SAN_ANTONIO.getValue(), TexasNode.CORPUS_CHRISTI.getValue(), 4));
        edges.add(new Edge(TexasNode.CORPUS_CHRISTI.getValue(), TexasNode.SAN_ANTONIO.getValue(), 4));

        edges.add(new Edge(TexasNode.SAN_ANTONIO.getValue(), TexasNode.LAREDO.getValue(), 5));
        edges.add(new Edge(TexasNode.LAREDO.getValue(), TexasNode.SAN_ANTONIO.getValue(), 5));

        examples.put("Texas", edges);
        num_nodes.put("Texas", TexasNode.MAX_VALUE.getValue());
    }

    private int generateRandomWeight(){
        int new_weight = 0;
        if(allow_negative_weights)
            new_weight = rand.nextInt(2 * max_magnitude_weights) - max_magnitude_weights;
        else
            new_weight = rand.nextInt(max_magnitude_weights);
        
        if(new_weight == 0)
            new_weight = 1;
        
        return new_weight;
    }
}
