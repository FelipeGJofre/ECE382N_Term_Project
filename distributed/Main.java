package distributed;
import java.lang.Integer;
import java.util.ArrayList;
public class Main{
    public static void main(String[] args) {
        
        String test_case = "StronglyConnected";
        int root_id = 0;
        
        Examples cases = new Examples(false, 1000,24);
        ArrayList<Edge> edges = cases.getExample(test_case);
        int num_nodes = cases.getNumNodes(test_case);

        // if(num_nodes == -1 || root_id >= num_nodes){
        //     System.out.println("Invalid test case");
        //     return;
        // } 

        // Chandy[] nodes = new Chandy[num_nodes];
        // Thread[] nodeThreads = new Thread[num_nodes];
        // nodes[root_id] = new ChandyRoot(root_id, num_nodes, edges);
        // nodeThreads[root_id] = new Thread(nodes[root_id]);
        // for (int i = 0; i < num_nodes; i++) {
        //     if (i == root_id) {
        //         continue;
        //     }
        //     nodes[i] = new Chandy(i, root_id, num_nodes, edges);
        //     nodeThreads[i] = new Thread(nodes[i]);
        //     nodeThreads[i].start();
        // }
        
        // try {
        //     Thread.sleep(10);
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }
        // nodeThreads[root_id].start();

        // try {
        //     nodeThreads[root_id].join();
        //     for(int i = 0; i < nodeThreads.length; i++){
        //         if(i == root_id){
        //             continue;
        //         }
        //         nodeThreads[i].join();
        //     }
        // }
        // catch (InterruptedException e) {
        //     e.printStackTrace();
        // }
        // finally {
        //     ChandyRoot root = (ChandyRoot) nodes[root_id];
        //     ArrayList<ChandyRoot.Result> final_results = root.getResults();
        //     for(int i = 0; i < final_results.size(); i++){
        //         System.out.print("ID: " + i + ", ");
        //         final_results.get(i).println();
        //     }
                // int numMsgsSent = 0;
                // for(Chandy node : nodes){
                //     numMsgsSent += node.num_messages_sent;
                // }
                // System.out.println("Total number of messages sent: " + numMsgsSent);
        // }

        // TODO: Add function to translate from ADJ matrix
        
        // Integer INF = Integer.MAX_VALUE;
        // final int[][] ADJ_MATRIX1 = {
        //     // 0,  1,  2,  3,  4,  5,  6,  7,  8 (vertex labels)
        //     {0,  0,  0,  0,  0,  0,  0,  1,  0}, // 0
        //     {0,  0,  7,  9,  0,  0,  14, 0,  0}, // 1
        //     {0,  7,  0,  10, 15, 0,  0,  0,  0}, // 2
        //     {0,  9,  10, 0,  11, 0,  2,  0,  0}, // 3
        //     {0,  0,  15, 11, 0,  6,  0,  0,  0}, // 4
        //     {0,  0,  0,  0,  6,  0,  9,  0,  0}, // 5
        //     {0,  14, 0,  2,  0,  9,  0,  0,  0}, // 6
        //     {1,  0,  0,  0,  0,  0,  0,  0,  7}, // 7
        //     {0,  0,  0,  0,  0,  0,  0,  7,  0}  // 8
        // };
    
        // final int[] COMPONENTS = {8, 6, 6, 6, 6, 6, 6, 8, 8};
        // final int[] SOURCE1_SPATH_COSTS = {INF, 0, 7, 9, 20, 20, 11, INF, INF};

        BellmanFord_Wrapper program = new BellmanFord_Wrapper();
        program.start(num_nodes, edges);
    }

}