package distributed;

import java.lang.Integer;
import java.util.ArrayList;
public class Main{
    public static void main(String[] args) {
        
        ArrayList<Edge> edges = new ArrayList<Edge>();
        edges.add(new Edge(0, 1, 1));
        edges.add(new Edge(1, 2, 2));
        edges.add(new Edge(2, 0, 3));
        edges.add(new Edge(0, 3, 4));
        
        // // Create three nodes on different ports
        // Dijkstra[] nodes = new Dijkstra[3];
        // DijkstraRoot root = new DijkstraRoot(0, 4, edges);
        // Thread rootThread = new Thread(root);
        // Thread[] nodeThreads = new Thread[3];
        
        // // Initialize and start nodes
        // for (int i = 0; i < 3; i++) {
        //     nodes[i] = new Dijkstra(i + 1, 4, 0, edges);
        //     nodeThreads[i] = new Thread(nodes[i]);
        //     nodeThreads[i].start();
        // }

        // // Wait for nodes to initialize
        // try {
        //     Thread.sleep(1000);
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }

        // rootThread.start();

        Chandy[] nodes = new Chandy[3];
        ChandyRoot root = new ChandyRoot(0, 4, edges);
        Thread rootThread = new Thread(root);
        Thread[] nodeThreads = new Thread[3];

        for (int i = 0; i < 3; i++) {
            nodes[i] = new Chandy(i + 1, 0, 4, edges);
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
            for(int i = 0; i < 3; i++){
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