import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Pattern;

public class FIP {
    private final ArrayList<String> programAtoms;
    private final TA ta;
    final private AF afConst;
    final private AF afId;
    final private AF afKeyword;
    private final TS ts = new TS();
    private final ArrayList<NodeFIP> nodes = new ArrayList<>();

    public FIP(final ArrayList<String> programAtoms, final TA ta, AF afConst, AF afId, AF afKeyword) {
        this.programAtoms = programAtoms;
        this.ta = ta;
        this.afConst = afConst;
        this.afId = afId;
        this.afKeyword = afKeyword;
        build();
    }

    public TS getTS() {
        return this.ts;
    }

    public void saveOnDisk(String filePath) {
        FileWrite.createFile(filePath);
        try {
            final FileWriter fileWriter = new FileWriter(filePath);
            for (final NodeFIP nodeFIP : nodes) {
                fileWriter.write(nodeFIP.getAtomLexical() + "\t\t\t\t" + nodeFIP.getCodAtom() + "\t\t\t\t" + nodeFIP.getCodTS() + "\n");
            }
            fileWriter.close();
            System.out.println("Successfully wrote to the file: " + filePath);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            System.out.println("Can't write into file: " + filePath);
            e.printStackTrace();
        }
    }

    private void build() {
        for (final String atom : this.programAtoms) {
            final NodeTA nodeTA = ta.find(atom);
            final NodeFIP nodeFIP;
            if (nodeTA != null) {
                nodeFIP = new NodeFIP(nodeTA.getAtomLexcial(), nodeTA.getCodAtom(), -1);
            } else {
                if (afId.secventaValida(atom).equals("DA")) {
                    nodeFIP = new NodeFIP(atom, 0, -1);
                } else if (afConst.secventaValida(atom).equals("DA")) {
                    nodeFIP = new NodeFIP(atom, 1, -1);
                } else {
                    throw new RuntimeException("No an atom: " + atom);
                }
                ts.insert(atom);
            }
            this.nodes.add(nodeFIP);
        }

        final ArrayList<NodeTS> nodeTSInorder = ts.getNodesInorder();

        for (final NodeFIP nodeFIP : this.nodes) {
            if (nodeFIP.getCodAtom() == 0 || nodeFIP.getCodAtom() == 1) {
                Optional<NodeTS> nodeTSOptional = nodeTSInorder.stream().filter(x -> x.key.equals(nodeFIP.getAtomLexical())).findFirst();
                if (nodeTSOptional.isEmpty()) {
                    throw new RuntimeException("Can't find atom in TS: " + nodeFIP.getAtomLexical());
                }
                nodeFIP.setCodTS(nodeTSOptional.get().position);
            }
        }
    }
}
