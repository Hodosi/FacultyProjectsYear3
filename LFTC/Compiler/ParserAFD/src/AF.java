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

    public void addVertex(String s) {
        tranzitii.put(s, new LinkedList<>());
    }

    public void addEdge(final String source, final NodeAF destination) {

        if (!tranzitii.containsKey(source))
            addVertex(source);

        tranzitii.get(source).add(destination);
    }

    private void build() {
        final String[] lines = rules.split("\n");

        final String[] stariLine = lines[0].split(" ");
        stari.addAll(Arrays.asList(stariLine));

        final String[] stariInitialeLine = lines[1].split(" ");
        stariInitiale.addAll(Arrays.asList(stariInitialeLine));

        final String[] stariFinaleLine = lines[2].split(" ");
        stariFinale.addAll(Arrays.asList(stariFinaleLine));

        final String[] alfabetLine = lines[3].split(" ");
        alfabet.addAll(Arrays.asList(alfabetLine));

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

    public String getPrefixMaxim(final String secventa) {
        String maxPrefix = "";
        final StringBuilder crtPrefix = new StringBuilder();
        final Map<String, List<NodeAF>> tranzitii = this.getTranzitii();
        final HashSet<String> stariInitiale = this.getStariInitiale();
        final HashSet<String> stariFinale = this.getStariFinale();

        for(final String stareInitiala : stariInitiale){
            for (final String stareFinala: stariFinale){
                if (stareInitiala.equals(stareFinala)) {
                    maxPrefix = "Epsilon";
                    break;
                }
            }
        }

        int currentIndex = 0;
        String currentPoint = String.valueOf(secventa.charAt(currentIndex));

        String stareCurrenta;
        boolean foundStartPoint = false;
        for (final String stareInitiala : stariInitiale) {
            if (foundStartPoint) {
                break;
            }

            stareCurrenta = stareInitiala;

            boolean finished = false;
            while (!finished) {

                boolean foundNewWay = false;

                for (final NodeAF nodeAF : tranzitii.get(stareCurrenta)) {

                    final List<String> edges = nodeAF.getEdges();
                    if (edges.contains(currentPoint)) {
                        foundNewWay = true;
                        foundStartPoint = true;

                        crtPrefix.append(currentPoint);
                        stareCurrenta = nodeAF.getNode();
                        if (stariFinale.contains(stareCurrenta)) {
                            maxPrefix = crtPrefix.toString();
                        }

                        currentIndex++;
                        if (currentIndex >= secventa.length()) {
                            finished = true;
                            break;
                        }
                        currentPoint = String.valueOf(secventa.charAt(currentIndex));

                        break;
                    }
                }

                if (!foundNewWay) {
                    finished = true;
                }
            }
        }

        return maxPrefix;
    }

    public String secventaValida(final String secventa) {
        if (secventa.isEmpty()) {
            final HashSet<String> stariInitiale = this.getStariInitiale();
            final HashSet<String> stariFinale = this.getStariFinale();
            for(final String stareInitiala : stariInitiale){
                for (final String stareFinala: stariFinale){
                    if(stareInitiala.equals(stareFinala)){
                        return "Epsilon";
                    }
                }
            }
            return "NU";
        }
        final String prefixMaxim = getPrefixMaxim(secventa);
        if (prefixMaxim.equals(secventa)) {
            return "DA";
        }
        return "NU";
    }
}
