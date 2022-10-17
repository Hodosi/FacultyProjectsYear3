import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class Service {
    private final String INPUT_FILES_PATH = "./Parser/resources/inputPrograms/inputFilesPath.txt";
    private final String OUTPUT_FILES_PATH = "./Parser/resources/parseOutput/";
    private final String SEPARATORS_FILES_PATH = "./Parser/resources/lexicalItems/separators.txt";
    private String currentFileContent = "";
    private ArrayList<String> parsedFileContent;
    private ArrayList<String> separators;

    public void run() {
        this.separators = ReaderFromFile.loadInputLinesToArrayList(SEPARATORS_FILES_PATH);
        final ArrayList<String> inputFilesPathList = ReaderFromFile.loadInputLinesToArrayList(INPUT_FILES_PATH);

        for (String filePath : inputFilesPathList) {
            this.currentFileContent = ReaderFromFile.readFile(filePath);
            this.parse();
            String outputFilePath = this.getOutputPath(filePath);
            WriterToFile.writeArrayListToLines(outputFilePath, this.parsedFileContent);
        }
    }

    private void parse() {
        this.parsedFileContent = new ArrayList<>();
        final String[] parsedBySpaces = this.currentFileContent.split("[ \n]+");
        for (String item : parsedBySpaces) {
            processItem(item);
        }
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

    private String getOutputPath(String inputPath) {
        final String[] pathParts = inputPath.split("/");
        return OUTPUT_FILES_PATH + pathParts[pathParts.length - 1];
    }
}
