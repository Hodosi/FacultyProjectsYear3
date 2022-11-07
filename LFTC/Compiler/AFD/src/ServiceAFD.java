import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ServiceAFD {

    private AF af;

    public void buildAF(final String rules) {
        af = new AF(rules);
    }

    public String getStari() {
        final HashSet<String> stari = af.getStari();
        StringBuilder builder = new StringBuilder();
        for (String stare : stari) {
            builder.append(stare).append(" ");
        }
        return (builder.toString());
    }

    public String getStariInitiale() {
        final HashSet<String> stari = af.getStariInitiale();
        StringBuilder builder = new StringBuilder();
        for (String stare : stari) {
            builder.append(stare).append(" ");
        }
        return (builder.toString());
    }

    public String getStariFinale() {
        final HashSet<String> stari = af.getStariFinale();
        StringBuilder builder = new StringBuilder();
        for (String stare : stari) {
            builder.append(stare).append(" ");
        }
        return (builder.toString());
    }

    public String getAlfabet() {
        final HashSet<String> alfabet = af.getAlfabet();
        StringBuilder builder = new StringBuilder();
        for (String caracter : alfabet) {
            builder.append(caracter).append(" ");
        }
        return (builder.toString());
    }


    public String getTranzitii() {
        final Map<String, List<NodeAF>> tranzitii = af.getTranzitii();
        final StringBuilder builder = new StringBuilder();

        for (final String v : tranzitii.keySet()) {
            builder.append(">> ").append(v).append("\n");
            for (final NodeAF w : tranzitii.get(v)) {
                StringBuilder edges = new StringBuilder();
                for (final String edge : w.getEdges()){
                    edges.append(edge).append(", ");
                }
                builder.append(w.getNode()).append(": ").append(edges).append("\n");
            }
            builder.append("\n\n");
        }

        return (builder.toString());
    }

    public String secventaValida(final String secventa) {
        if (secventa.isEmpty()) {
            final HashSet<String> stariInitiale = af.getStariInitiale();
            final HashSet<String> stariFinale = af.getStariFinale();
            for(final String stareInitiala : stariInitiale){
                for (final String stareFinala: stariFinale){
                    if(stareInitiala.equals(stareFinala)){
                        return "Epsilon";
                    }
                }
            }
            return "";
        }
        final String prefixMaxim = getPrefixMaxim(secventa);
        if (prefixMaxim.equals(secventa)) {
            return "DA";
        }
        return "NU";
    }

    public String getPrefixMaxim(final String secventa) {
        String maxPrefix = "";
        final StringBuilder crtPrefix = new StringBuilder();
        final Map<String, List<NodeAF>> tranzitii = af.getTranzitii();
        final HashSet<String> stariInitiale = af.getStariInitiale();
        final HashSet<String> stariFinale = af.getStariFinale();

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
}
