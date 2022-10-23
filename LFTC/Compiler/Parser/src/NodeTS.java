public class NodeTS {
    public String key;
    public NodeTS left, right;
    public int position;

    public NodeTS(String item)
    {
        key = item;
        left = right = null;
    }
}
