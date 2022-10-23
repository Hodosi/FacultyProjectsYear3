import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class TA {
    private final String filePath;
    private final ArrayList<NodeTA> nodes = new ArrayList<>();

    public TA(String filePath) {
        this.filePath = filePath;
        readNodes();
    }

    public NodeTA find(final String atomLexical) {
        for (NodeTA node : nodes) {
            if (node.getAtomLexcial().equals(atomLexical)) {
                return node;
            }
        }
        return null;
    }

    private void readNodes() {
        try {
            final File file = new File(filePath);
            final Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] atomCode = line.split(" ");
                final NodeTA nodeTA = new NodeTA(atomCode[0], Integer.parseInt(atomCode[1]));
                nodes.add(nodeTA);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
