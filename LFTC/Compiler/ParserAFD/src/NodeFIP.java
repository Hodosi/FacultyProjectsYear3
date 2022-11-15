public class NodeFIP {
    private final String atomLexical;
    private final int codAtom;
    private int codTS;

    public NodeFIP(String atomLexical, int codAtom, int codTS) {
        this.atomLexical = atomLexical;
        this.codAtom = codAtom;
        this.codTS = codTS;
    }

    public String getAtomLexical() {
        return atomLexical;
    }

    public int getCodAtom() {
        return codAtom;
    }

    public int getCodTS() {
        return codTS;
    }

    public void setCodTS(int codTS) {
        this.codTS = codTS;
    }
}
