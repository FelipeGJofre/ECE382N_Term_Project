package distributed;

import java.util.ArrayList;
public class ChandyRoot extends Chandy {
    
    public ChandyRoot(int id, int n, ArrayList<Edge> edges) {
        super(id, id, n, edges);
        this.distance_from_root = 0;
    }
    
    
    @Override
    public void run() {
        System.out.println("ChandyRoot is running");
    }
}