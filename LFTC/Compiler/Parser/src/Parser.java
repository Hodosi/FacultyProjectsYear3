import java.util.ArrayList;
import java.util.Optional;

public class Parser {
    final String fileContent;
    final ArrayList<String> separators;
    final ArrayList<String> parsedFileContent;

    int currentLine = 0;

    public Parser(String fileContent, ArrayList<String> separators) {
        this.fileContent = fileContent;
        this.separators = separators;
        this.parsedFileContent = new ArrayList<>();
    }

    public ArrayList<String> parse() {
        final String[] parsedBySpaces = this.fileContent.split("[ \n]+");
        for (String item : parsedBySpaces) {
            processItem(item);
        }
        return this.parsedFileContent;
    }

    private void processItem(String currentItem) {
        Optional<String> optional;
        String atom, separator;
        int indexSeparator;

        separator = " ";
        indexSeparator = -1;
        while (indexSeparator + separator.length() < currentItem.length()) {
            currentItem = currentItem.substring(indexSeparator + separator.length());
            atom = currentItem;
            indexSeparator = -1;

            optional = this.separators.stream().filter(atom::contains).findFirst();
            while (optional.isPresent()) {
                separator = optional.get();
                indexSeparator = atom.indexOf(separator);
                atom = atom.substring(0, indexSeparator);
                optional = this.separators.stream().filter(atom::contains).findFirst();
            }

            if (indexSeparator == -1) {
                this.parsedFileContent.add(atom);
                break;
            }

            if (indexSeparator > 0) {
                this.parsedFileContent.add(atom);
            }

            this.parsedFileContent.add(separator);
        }
    }

//    public ArrayList<String> parse() {
//        final String[] parsedByLines = this.fileContent.split("\n+");
//        for (String item : parsedByLines) {
//            parseBySpace(item);
//            currentLine++;
//        }
//        return this.parsedFileContent;
//    }
//
//    public void parseBySpace(String line) {
//        final String[] parsedByLine = line.split(" +");
//        for (String item : parsedByLine) {
//            processItem(item);
//        }
//    }
//
//    private void processItem(String currentItem) {
//        Optional<String> optional;
//        String atom, separator;
//        int indexSeparator;
//
//        separator = " ";
//        indexSeparator = -1;
//        while (indexSeparator + separator.length() < currentItem.length()) {
//            currentItem = currentItem.substring(indexSeparator + separator.length());
//            atom = currentItem;
//            indexSeparator = -1;
//
//            optional = this.separators.stream().filter(atom::contains).findFirst();
//            while (optional.isPresent()) {
//                separator = optional.get();
//                indexSeparator = atom.indexOf(separator);
//                atom = atom.substring(0, indexSeparator);
//                optional = this.separators.stream().filter(atom::contains).findFirst();
//            }
//
//            if (indexSeparator == -1) {
//                this.parsedFileContent.add(atom);
//                break;
//            }
//
//            if (indexSeparator > 0) {
//                this.parsedFileContent.add(atom);
//            }
//
//            this.parsedFileContent.add(separator);
//        }
//    }
}
