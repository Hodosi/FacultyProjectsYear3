import java.util.ArrayList;

public class Service {
    private final String INPUT_FILES_PATH = "./ParserAFD/resources/input/inputFilesPath.txt";
    private final String OUTPUT_FILES_PATH = "./ParserAFD/resources/output/";
    private final String SEPARATORS_FILES_PATH = "./ParserAFD/resources/lexicalItems/separators.txt";
    private final String TA_FILE_PATH = "./ParserAFD/resources/tables/TA.txt";

    private final String INPUT_FILE_RULES_CONST = "./ParserAFD/resources/input/ConstAF.txt";
    private final String INPUT_FILE_RULES_ID = "./ParserAFD/resources/input/IdAF8.txt";
    private final String INPUT_FILE_RULES_KEYWORD= "./ParserAFD/resources/input/KeywordAF.txt";
    public void run() {
        final ArrayList<String> separators = FileRead.loadInputLinesToArrayList(SEPARATORS_FILES_PATH);
        final ArrayList<String> inputFilesPathList = FileRead.loadInputLinesToArrayList(INPUT_FILES_PATH);
        final AF afConst = new AF(FileRead.readFile(INPUT_FILE_RULES_CONST));
        final AF afId = new AF(FileRead.readFile(INPUT_FILE_RULES_ID));
        final AF afKeyword = new AF(FileRead.readFile(INPUT_FILE_RULES_KEYWORD));
        final TA ta = new TA(TA_FILE_PATH);


        for (String filePath : inputFilesPathList) {
            final String currentFileContent = FileRead.readFile(filePath);
            final Parser parser = new Parser(currentFileContent, separators, afConst, afId, afKeyword);
            final ArrayList<String> programAtoms = parser.parse();
            final String filename = Utils.getFilename(filePath);
            FileWrite.writeArrayListToLines(OUTPUT_FILES_PATH + filename, programAtoms);

            final FIP fip = new FIP(programAtoms, ta, afConst, afId, afKeyword);
            fip.saveOnDisk(OUTPUT_FILES_PATH + "FIP" + filename);
            final TS ts = fip.getTS();
            ts.saveOnDisk(OUTPUT_FILES_PATH + "TS" + filename);
        }
    }
}
