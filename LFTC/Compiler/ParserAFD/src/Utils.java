public class Utils {
    public static String getFilename(String inputPath) {
        final String[] pathParts = inputPath.split("/");
        return pathParts[pathParts.length - 1];
    }
}
