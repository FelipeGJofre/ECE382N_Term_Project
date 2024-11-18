package distributed;

import java.lang.Integer;
import java.util.ArrayList;
public class Main{
    public static void main(String[] args) {
        
        String test_case = "Texas";
        
        Examples cases = new Examples(true, 20);
        ArrayList<Edge> edges = cases.getExample(test_case);
        int num_nodes = cases.getNumNodes(test_case);
        int num_regular_nodes = num_nodes - 1;

        Chandy[] nodes = new Chandy[num_regular_nodes];
        ChandyRoot root = new ChandyRoot(0, num_nodes, edges);
        Thread rootThread = new Thread(root);
        Thread[] nodeThreads = new Thread[num_regular_nodes];

        for (int i = 0; i < num_regular_nodes; i++) {
            nodes[i] = new Chandy(i + 1, 0, num_nodes, edges);
            nodeThreads[i] = new Thread(nodes[i]);
            nodeThreads[i].start();
        }
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        rootThread.start();

        try {
            rootThread.join();
            for(int i = 0; i < nodeThreads.length; i++){
                nodeThreads[i].join();
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            ArrayList<ChandyRoot.Result> final_results = root.getResults();
            for(int i = 0; i < final_results.size(); i++){
                System.out.print("ID: " + i + ", ");
                final_results.get(i).println();
            }
        }
    }
}

/*
 * Ideas for implementing our detection algorithm:
 * - We have one process per boolean predicate
 * - Each one communicates to the others whether another process needs to
 * advance, as in stable marrige, or if the local predicate is false or not
 * - Algorithm only terminates once all the messages express to each other that there are no forbidden
 * states or that the local predicates can be resolved
 * 
 * 
 */