import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    private final static String FILE_NAME = "file.txt";
    private final static int LEFT_LIMIT = 48;
    private final static int RIGHT_LIMIT = 122;
    private final static int BUFFER_SIZE = 4;
    private static int numberOfLines;
    private static int countOfSaves;

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        numberOfLines = scanner.nextInt();
        int maxLineSize = scanner.nextInt();
        countOfSaves = 2*numberOfLines/BUFFER_SIZE;

        prepareFile(maxLineSize);
        readFile(FILE_NAME, "run_0.txt", 0);
    }

    private static void prepareFile(int maxLineSize) throws IOException {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            deleteFile(FILE_NAME);
        }
        List<String> randomLines = generateLines(maxLineSize);
        writeToFile(randomLines, FILE_NAME);
    }

    public static void readFile(String fileName, String newFile, int runNumber) throws IOException{
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            List<String> partLines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                partLines.add(line);
                if (partLines.size() == BUFFER_SIZE) {
                    partLines = sortAndSaveHalfLines(partLines, newFile);
                    partLines = removeSavedLines(partLines);
                }
            }
            sortAndSaveHalfLines(partLines, newFile);
        }
        if (runNumber < countOfSaves){
            deleteFile(fileName);
            runNumber++;
            File file = new File("run_" + runNumber + ".txt");
            if (!file.createNewFile()){
                System.out.println("File creating failure");
            }
            readFile(newFile, file.getPath(), runNumber);
        }else {
            deleteFile(fileName);
            File oldNameFile = new File(newFile);
            File newNameFile = new File(FILE_NAME);

            if (oldNameFile.exists()) {
                if (!oldNameFile.renameTo(newNameFile)) {
                    System.out.println("Rename failure");
                }
            }
        }
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
        return unsortedLines.stream().sorted().collect(Collectors.toList());
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
