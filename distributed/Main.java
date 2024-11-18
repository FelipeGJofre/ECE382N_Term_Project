package distributed;

import java.lang.Integer;
import java.util.ArrayList;
public class Main{
    public static void main(String[] args) {
        
        String test_case = "Texas";
        int root_id = 0;
        
        Examples cases = new Examples(true, 20);
        ArrayList<Edge> edges = cases.getExample(test_case);
        int num_nodes = cases.getNumNodes(test_case);

        if(num_nodes == -1 || root_id >= num_nodes){
            System.out.println("Invalid test case");
            return;
        } 

        Chandy[] nodes = new Chandy[num_nodes];
        Thread[] nodeThreads = new Thread[num_nodes];
        nodes[root_id] = new ChandyRoot(root_id, num_nodes, edges);
        nodeThreads[root_id] = new Thread(nodes[root_id]);
        for (int i = 0; i < num_nodes; i++) {
            if (i == root_id) {
                continue;
            }
            nodes[i] = new Chandy(i, root_id, num_nodes, edges);
            nodeThreads[i] = new Thread(nodes[i]);
            nodeThreads[i].start();
        }
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        nodeThreads[root_id].start();

        try {
            nodeThreads[root_id].join();
            for(int i = 0; i < nodeThreads.length; i++){
                if(i == root_id){
                    continue;
                }
                nodeThreads[i].join();
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            ChandyRoot root = (ChandyRoot) nodes[root_id];
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