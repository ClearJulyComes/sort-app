import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    private final static String FILE_NAME = "file.txt";
    private final static String RESULT_FILE_NAME = "result.txt";
    private final static String END_OF_LOOP_FLAG = "-";
    private final static int LEFT_LIMIT = 48;
    private final static int RIGHT_LIMIT = 122;
    private final static int BUFFER_SIZE = 50;
    private static int numberOfLines;
    private static int maxNumOfLoops;

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            numberOfLines = scanner.nextInt();
            int maxLineSize = scanner.nextInt();
            maxNumOfLoops = 2 * numberOfLines / BUFFER_SIZE;

            prepareFiles(maxLineSize);
            readFile();
        }catch (IOException e){
            System.out.println("Sorry app is closing bc IOException");
            System.exit(1);
        }
    }

    private static void prepareFiles(int maxLineSize) throws IOException {
        File file = new File(FILE_NAME);
        File resultFile = new File(RESULT_FILE_NAME);
        if (file.exists()) {
            deleteFile(FILE_NAME);
        }
        if (resultFile.exists()) {
            deleteFile(RESULT_FILE_NAME);
        }
        List<String> randomLines = generateLines(maxLineSize);
        writeToFile(randomLines, FILE_NAME);
    }

    public static void readFile() throws IOException{
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            File newFile = new File(RESULT_FILE_NAME);
            List<String> partLines = new ArrayList<>();
            String line;
            int runNumber = 0;
            writeToFile(END_OF_LOOP_FLAG, FILE_NAME);
            while ((line = br.readLine()) != null) {
                if (line.equals(END_OF_LOOP_FLAG) && runNumber != maxNumOfLoops){
                    runNumber++;
                    saveLoopLastLinesWithFlag(partLines);
                    partLines.clear();
                }else if (line.equals(END_OF_LOOP_FLAG)) {
                    break;
                }else if(runNumber == maxNumOfLoops){
                    if (!newFile.exists()) {
                        if (!newFile.createNewFile()) {
                            System.out.println("File creating failure " + newFile.getPath());
                        }
                    }
                    writeToFile(line, RESULT_FILE_NAME);
                }else {
                    partLines.add(line);
                    if (partLines.size() == BUFFER_SIZE) {
                        partLines = sortAndSaveHalfLines(partLines, FILE_NAME);
                        partLines = removeSavedLines(partLines);
                    }
                }
            }
            deleteFile(FILE_NAME);
        }

    }

    private static void saveLoopLastLinesWithFlag(List<String> partLines) throws IOException {
        partLines = sortLines(partLines);
        partLines.add(END_OF_LOOP_FLAG);
        writeToFile(partLines, FILE_NAME);
    }

    public static List<String> removeSavedLines(List<String> sortedLines) {
        return sortedLines.stream().skip(BUFFER_SIZE/2).collect(Collectors.toList());
    }

    public static List<String> sortAndSaveHalfLines(List<String> lines, String file) throws IOException {
        lines = sortLines(lines);
        writeToFile(lines.stream().limit(BUFFER_SIZE/2).collect(Collectors.toList()), file);
        return lines;
    }

    public static List<String> sortLines(List<String> unsortedLines) {
        return unsortedLines.parallelStream().sorted().collect(Collectors.toList());
    }

    public static void deleteFile(String fileName){
        File file = new File(fileName);
        if(!file.delete()){
            System.out.println("Failed to delete " + fileName);
        }
    }

    public static void writeToFile(List<String> randomLines, String fileName) throws IOException {
        try(FileWriter fileWriter = new FileWriter(fileName, true);
            PrintWriter printWriter = new PrintWriter(fileWriter)) {
            for (String line : randomLines){
                printWriter.println(line);
            }
        }
    }

    public static void writeToFile(String line, String fileName) throws IOException {
        try(FileWriter fileWriter = new FileWriter(fileName, true);
            PrintWriter printWriter = new PrintWriter(fileWriter)) {
                printWriter.println(line);
        }
    }

    public static List<String> generateLines(int maxLineSize){
        Random random = new Random();
        List<String> lines = new ArrayList<>();
        for (int n = 0; n < numberOfLines; n++) {
            String generatedLine = random.ints(LEFT_LIMIT, RIGHT_LIMIT + 1).filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(random.nextInt(maxLineSize))
                    .mapToObj(s -> (char) s).map(Object::toString).collect(Collectors.joining());
            lines.add(generatedLine);
        }
        return lines;
    }
}
