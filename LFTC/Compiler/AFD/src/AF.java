import java.util.*;

public class AF {
    private final HashSet<String> stari;
    private final HashSet<String> stariInitiale;
    private final HashSet<String> stariFinale;
    private final HashSet<String> alfabet;
    private final Map<String, List<NodeAF>> tranzitii;
    private final String rules;

    public AF(final String rules) {
        this.stari = new HashSet<>();
        this.stariInitiale = new HashSet<>();
        this.stariFinale = new HashSet<>();
        this.alfabet = new HashSet<>();
        this.tranzitii = new HashMap<>();
        this.rules = rules;
        build();
    }

    public HashSet<String> getStari() {
        return stari;
    }

    public HashSet<String> getStariInitiale() {
        return stariInitiale;
    }

    public HashSet<String> getStariFinale() {
        return stariFinale;
    }

    public HashSet<String> getAlfabet() {
        return alfabet;
    }

    public Map<String, List<NodeAF>> getTranzitii() {
        return tranzitii;
    }

    // https://www.geeksforgeeks.org/implementing-generic-graph-in-java/
    // This function gives whether an edge is present or not.
//    public boolean hasEdge(final String s, final String d) {
//        return tranzitii.get(s).contains(d);
//    }

    // This function adds a new vertex to the graph
    public void addVertex(String s) {
        tranzitii.put(s, new LinkedList<>());
    }

    // This function adds the edge
    // between source to destination
    public void addEdge(final String source, final NodeAF destination) {

        if (!tranzitii.containsKey(source))
            addVertex(source);

        tranzitii.get(source).add(destination);
    }

    //    private final HashSet<Character> stari;
//    private final HashSet<Character> stariInitiale;
//    private final HashSet<Character> stariFinale;
//    private final HashSet<Character> alfabet;
//    private final Map<Character, List<Character>> tranzitii;
    private void build() {
        final String[] lines = rules.split("\n");

        //    private final HashSet<Character> stari;
        final String[] stariLine = lines[0].split(" ");
        stari.addAll(Arrays.asList(stariLine));

        //    private final HashSet<Character> stariInitiale;
        final String[] stariInitialeLine = lines[1].split(" ");
        stariInitiale.addAll(Arrays.asList(stariInitialeLine));

        //    private final HashSet<Character> stariFinale;
        final String[] stariFinaleLine = lines[2].split(" ");
        stariFinale.addAll(Arrays.asList(stariFinaleLine));

        //    private final HashSet<Character> alfabet;
        final String[] alfabetLine = lines[3].split(" ");
        alfabet.addAll(Arrays.asList(alfabetLine));

        //    private final Map<Character, List<Character>> tranzitii;
        for (int i = 4; i < lines.length; i++) {
            final String[] tranzitie = lines[i].split(" ");
            final String startVertex = tranzitie[0];
            final String endVertex = tranzitie[tranzitie.length - 1];

            final NodeAF nodeAF = new NodeAF(endVertex);
            for(int j = 1; j < tranzitie.length - 1; j++){
                nodeAF.addEdge(tranzitie[j]);
            }
            this.addEdge(startVertex, nodeAF);
        }
        verifyIfIsDeterminist();
    }

    private void verifyIfIsDeterminist(){
        for(final String tranzitie : this.tranzitii.keySet()){
            List<String> endpoints = new ArrayList<>();
            for(final NodeAF nodeAF : this.tranzitii.get(tranzitie)){
                endpoints.addAll(nodeAF.getEdges());
            }
            Set<String> enpointsSet = new HashSet<>(endpoints);
            if(enpointsSet.size() != endpoints.size()){
                throw new RuntimeException("Prea nedeterminist!");
            }
        }
    }
}
