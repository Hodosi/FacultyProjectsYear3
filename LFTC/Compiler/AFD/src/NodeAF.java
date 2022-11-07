import java.util.ArrayList;
import java.util.List;

public class NodeAF {
    private final String node;
    private final List<String> edges;

    public NodeAF(String node) {
        this.node = node;
        this.edges = new ArrayList<>();
    }

    public String getNode() {
        return node;
    }

    public List<String> getEdges() {
        return edges;
    }

    public void addEdge(final String e){
        edges.add(e);
    }
}
