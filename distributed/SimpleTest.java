import org.junit.Test;
import static org.junit.Assert.*;

import java.io.FileWriter;

public class SimpleTest {
    final int INF = Integer.MAX_VALUE;

    final int[][] ADJ_MATRIX1_UNWEIGHTED = {
        // 0,  1,  2,  3,  4,  5,  6,  7,  8 (vertex labels)
          {0,  0,  0,  0,  0,  0,  0,  1,  0}, // 0
          {0,  0,  1,  1,  0,  0,  1,  0,  0}, // 1
          {0,  1,  1,  1,  1,  0,  0,  0,  0}, // 2
          {0,  1,  1,  0,  1,  0,  1,  0,  0}, // 3
          {0,  0,  1,  1,  0,  1,  0,  0,  0}, // 4
          {0,  0,  0,  0,  1,  0,  1,  0,  0}, // 5
          {0,  1,  0,  1,  0,  1,  0,  0,  0}, // 6
          {1,  0,  0,  0,  0,  0,  0,  0,  1}, // 7
          {0,  0,  0,  0,  0,  0,  0,  1,  0}  // 8
    };

    final int[][] ADJ_MATRIX1_TC = {
        // 0,  1,  2,  3,  4,  5,  6,  7,  8 (vertex labels)
          {1,  0,  0,  0,  0,  0,  0,  1,  1}, // 0
          {0,  1,  1,  1,  1,  1,  1,  0,  0}, // 1
          {0,  1,  1,  1,  1,  1,  1,  0,  0}, // 2
          {0,  1,  1,  1,  1,  1,  1,  0,  0}, // 3
          {0,  1,  1,  1,  1,  1,  1,  0,  0}, // 4
          {0,  1,  1,  1,  1,  1,  1,  0,  0}, // 5
          {0,  1,  1,  1,  1,  1,  1,  0,  0}, // 6
          {1,  0,  0,  0,  0,  0,  0,  1,  1}, // 7
          {1,  0,  0,  0,  0,  0,  0,  1,  1}  // 8
    };

    @Test
    public void checkTC_V1() {
        LLPTransitiveClosure TC = new LLPTransitiveClosure(ADJ_MATRIX1_UNWEIGHTED);
        TC.solve();
        int[][] sol = TC.getSolution();
        assertArrayEquals(sol, ADJ_MATRIX1_TC);
    }

    final int[][] ADJ_MATRIX1 = {
        // 0,  1,  2,  3,  4,  5,  6,  7,  8 (vertex labels)
          {0,  0,  0,  0,  0,  0,  0,  1,  0}, // 0
          {0,  0,  7,  9,  0,  0,  14, 0,  0}, // 1
          {0,  7,  0,  10, 15, 0,  0,  0,  0}, // 2
          {0,  9,  10, 0,  11, 0,  2,  0,  0}, // 3
          {0,  0,  15, 11, 0,  6,  0,  0,  0}, // 4
          {0,  0,  0,  0,  6,  0,  9,  0,  0}, // 5
          {0,  14, 0,  2,  0,  9,  0,  0,  0}, // 6
          {1,  0,  0,  0,  0,  0,  0,  0,  7}, // 7
          {0,  0,  0,  0,  0,  0,  0,  7,  0}  // 8
      };
  
    final int[] COMPONENTS = {8, 6, 6, 6, 6, 6, 6, 8, 8};
    final int[] SOURCE1_SPATH_COSTS = {INF, 0, 7, 9, 20, 20, 11, INF, INF};

    @Test
    public void testDijkstra() {
        int source = 1;
        Dijkstra d = new Dijkstra(ADJ_MATRIX1, source);
        int[] costs = d.solve();
        assertArrayEquals(costs, SOURCE1_SPATH_COSTS);
    }

    @Test
    public void testDelta(){
        int source = 1;
        DeltaStepping d = new DeltaStepping(ADJ_MATRIX1,  Integer.MAX_VALUE, source);
        int[] output = d.solve();
        assertArrayEquals(SOURCE1_SPATH_COSTS, output);
    }

    final int[][] ADJ_MATRIX2 = {
        //0, 1, 2, 3, 4   
        {0, 8, 1, -1, INF}, //0
        {INF, 0, INF, INF, 10}, //1
        {INF, 2, 0, INF, INF},  //2
        {INF, INF, 3, 0 , INF}, //3
        {INF, INF, INF, INF, 0}   //4
    };

    final int[][] shortest_cost_matrix2 = {
        //0, 1, 2, 3, 4   
        {0, 3, 1, -1, 13}, //0
        {INF, 0, INF, INF, 10},  //1
        {INF, 2, 0, INF, 12},  //2
        {INF, 5, 3, 0 , 15}, //3
        {INF, INF, INF, INF, 0}    //4
    };

    @Test
    public void testFloyd() {
        FloydWarshall f = new FloydWarshall(ADJ_MATRIX2);
        f.solve();
        int[][] costs = f.getSolution();
        assertArrayEquals(costs, shortest_cost_matrix2);
    }

    final int[][] ADJ_MATRIX3 = {
        //0, 1, 2, 3, 4   
        {0, 4, INF, 5, INF}, //0
        {INF, 0, 1, INF, 6}, //1
        {2, INF, 0, 3, INF},  //2
        {INF, INF, 1, 0, 2}, //3
        {1, INF, INF, 4, 0}   //4
    };

    final int[][] shortest_cost_matrix3 = {
        //0, 1, 2, 3, 4   
        {0, 4, 5, 5, 7}, //0
        {3, 0, 1, 4, 6}, //1
        {2, 6, 0, 3, 5}, //2
        {3, 7, 1, 0, 2}, //3
        {1, 5, 5, 4, 0}  //4
    };

    @Test
    public void testFloyd2() {
        FloydWarshall f = new FloydWarshall(ADJ_MATRIX3);
        f.solve();
        int[][] costs = f.getSolution();
        assertArrayEquals(costs, shortest_cost_matrix3);
    }
  
    @Test
      public void testLLPDijkstra() {
          int source = 1;
          LLPDijkstra d = new LLPDijkstra(ADJ_MATRIX1, source);
          int[] costs = d.solve();
          assertArrayEquals(SOURCE1_SPATH_COSTS, costs);
      } 

    @Test
    public void testRunTime() {
        int source = 1;
        int runs = 1024;
        try (FileWriter writer = new FileWriter("results.csv")){
            writer.write("LLP Dijkstra, Dijkstra, DeltaStepping, FloydWarshall, LLP Transitive Closure\n");
            for (int i = 0; i < runs; i++) {
                LLPDijkstra d = new LLPDijkstra(ADJ_MATRIX1, source);
                long startTime = System.nanoTime();
                d.solve();
                long endTime = System.nanoTime();
                double duration = (endTime - startTime) / 1000.0f;
                writer.write(duration + ", ");
    
                Dijkstra d2 = new Dijkstra(ADJ_MATRIX1, source);
                startTime = System.nanoTime();
                d2.solve();
                endTime = System.nanoTime();
                duration = (endTime - startTime) / 1000.0f;
                writer.write(duration + ", ");
    
                DeltaStepping d3 = new DeltaStepping(ADJ_MATRIX1, Integer.MAX_VALUE, source);
                startTime = System.nanoTime();
                d3.solve();
                endTime = System.nanoTime();
                duration = (endTime - startTime) / 1000.0f;
                writer.write(duration + ", ");
    
                FloydWarshall f = new FloydWarshall(ADJ_MATRIX1);
                startTime = System.nanoTime();
                f.solve();
                endTime = System.nanoTime();
                duration = (endTime - startTime) / 1000.0f;
                writer.write(duration + ", ");
    
                LLPTransitiveClosure t = new LLPTransitiveClosure(ADJ_MATRIX1);
                startTime = System.nanoTime();
                t.solve();
                endTime = System.nanoTime();
                duration = (endTime - startTime) / 1000.0f;
                writer.write(duration + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(true);
    }
}
