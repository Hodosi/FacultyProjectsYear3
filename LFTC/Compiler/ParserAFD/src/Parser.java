import java.util.ArrayList;

public class Parser {
    final private String fileContent;
    final private ArrayList<String> separators;
    final private ArrayList<String> parsedFileContent;

    final private AF afConst;
    final private AF afId;
    final private AF afKeyword;

    public Parser(final String fileContent, final ArrayList<String> separators, final AF afConst, final AF afId, final AF afKeyword) {
        this.fileContent = fileContent;
        this.separators = separators;
        this.afConst = afConst;
        this.afId = afId;
        this.afKeyword = afKeyword;
        this.parsedFileContent = new ArrayList<>();
    }

    public ArrayList<String> parse() {
        String currentContent = fileContent;
        while (!currentContent.isEmpty()) {

            while (currentContent.startsWith(" ") || currentContent.startsWith("\n")) {
                currentContent = currentContent.substring(1);
            }

            if(currentContent.isEmpty()){
                break;
            }

            boolean startsWithSeparator = false;
            for (final String separator : separators) {
                if (currentContent.startsWith(separator)) {
                    this.parsedFileContent.add(separator);
                    currentContent = currentContent.substring(separator.length());
                    startsWithSeparator = true;
                    break;
                }
            }
            if(startsWithSeparator){
                continue;
            }

            String atom;
            atom = this.afConst.getPrefixMaxim(currentContent);
            if(atom.isEmpty()){
                final String isId = this.afId.getPrefixMaxim(currentContent);
                final String isKeyword = this.afKeyword.getPrefixMaxim(currentContent);
                if(isKeyword.length() > isId.length() && isKeyword.contains(".")){
                    atom = isKeyword;
                }
                else {
                    atom = isId;
                }
            }
            if(atom.isEmpty()){
                throw new RuntimeException("Mare error\n a mai ramas de procesat: \n" + currentContent);
            }
            this.parsedFileContent.add(atom);
            currentContent = currentContent.substring(atom.length());
        }

        return this.parsedFileContent;
    }
}
