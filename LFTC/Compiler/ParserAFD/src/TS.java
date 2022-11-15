import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

// https://www.geeksforgeeks.org/binary-search-tree-set-1-search-and-insertion/
public class TS {
    private NodeTS root;

    private ArrayList<NodeTS> nodesInorder;

    private boolean needRefresh;

    public TS() {
        root = null;
        needRefresh = false;
    }

    public void saveOnDisk(String filePath) {
        FileWrite.createFile(filePath);
        try {
            final FileWriter fileWriter = new FileWriter(filePath);
            final ArrayList<NodeTS> nodes = this.getNodesInorder();
            for (final NodeTS nodeTS : nodes) {
                fileWriter.write(nodeTS.key + "\t\t\t\t" + nodeTS.position + "\n");
            }
            fileWriter.close();
            System.out.println("Successfully wrote to the file: " + filePath);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            System.out.println("Can't write into file: " + filePath);
            e.printStackTrace();
        }
    }

    // This method mainly calls insertRec()
    public void insert(String key) {
        needRefresh = true;
        root = insertRec(root, key);
    }

    /* A recursive function to
       insert a new key in BST */
    private NodeTS insertRec(NodeTS root, String key) {

        /* If the tree is empty,
           return a new node */
        if (root == null) {
            root = new NodeTS(key);
            return root;
        }

        /* Otherwise, recur down the tree */
        else if (key.compareTo(root.key) < 0)
            root.left = insertRec(root.left, key);
        else if (key.compareTo(root.key) > 0)
            root.right = insertRec(root.right, key);

        /* return the (unchanged) node pointer */
        return root;
    }

    private int position;

    public ArrayList<NodeTS> getNodesInorder() {
        if (needRefresh) {
            refresh();
        }
        return nodesInorder;
    }

    public NodeTS findByCode(int code) {
        if (needRefresh) {
            refresh();
        }

        return nodesInorder.get(code);
    }

    private void refresh(){
        nodesInorder = new ArrayList<>();
        this.position = 0;
        inorderRec(root);
        needRefresh = false;
    }

    private void inorderRec(NodeTS root) {
        if (root != null) {
            inorderRec(root.left);
            root.position = this.position;
            this.position++;
            nodesInorder.add(root);
            inorderRec(root.right);
        }
    }
}
