import java.util.ArrayList;

public class Service {
    private final String INPUT_FILES_PATH = "./Parser/resources/input/inputFilesPath.txt";
    private final String OUTPUT_FILES_PATH = "./Parser/resources/output/";
    private final String SEPARATORS_FILES_PATH = "./Parser/resources/lexicalItems/separators.txt";
    private final String TA_FILE_PATH = "./Parser/resources/tables/TA.txt";

    public void run() {
        final ArrayList<String> separators = FileRead.loadInputLinesToArrayList(SEPARATORS_FILES_PATH);
        final ArrayList<String> inputFilesPathList = FileRead.loadInputLinesToArrayList(INPUT_FILES_PATH);
        final TA ta = new TA(TA_FILE_PATH);

        for (String filePath : inputFilesPathList) {
            final String currentFileContent = FileRead.readFile(filePath);
            final Parser parser = new Parser(currentFileContent, separators);
            final ArrayList<String> programAtoms = parser.parse();
            final String filename = Utils.getFilename(filePath);
            FileWrite.writeArrayListToLines(OUTPUT_FILES_PATH + filename, programAtoms);

            final FIP fip = new FIP(programAtoms, ta);
            fip.saveOnDisk(OUTPUT_FILES_PATH + "FIP" + filename);
            final TS ts = fip.getTS();
            ts.saveOnDisk(OUTPUT_FILES_PATH + "TS" + filename);
        }
    }
}
