package encryptdecrypt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

// https://www.baeldung.com/java-caesar-cipher

public class Main {

    private static final char[] ALPHABET_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final int START = ALPHABET_UPPER[0];
    private static final int END = ALPHABET_UPPER[ALPHABET_UPPER.length - 1];
    private static String operation;
    private static String message;
    private static String pathToReadFile;
    private static String pathToWriteFile;
    private static String typeOfAlgorithm;
    private static int key;
    private static String regexBigLetter = "[A-Z]";

    public static void main(String[] args) {
        parsInput(args);
        switch (operation) {
            case "enc":
                StringBuilder encryptedMess;

                if (typeOfAlgorithm.equals("unicode")) {
                    encryptedMess = encryptionUnicode(message, key);
                } else {
                    encryptedMess = encryptionShift(message, key);
                }

                if (!(pathToReadFile == null)) {
                    writeDataInFile(encryptedMess.toString(), pathToWriteFile);
                } else {
                    System.out.println(encryptedMess.toString());
                }
                break;
            case "dec":
                StringBuilder decryptedMess;
                if (typeOfAlgorithm.equals("unicode")) {
                    decryptedMess = decryptionUnicode(message, key);
                } else {
                    decryptedMess = decryptionShift(message, key);
                }

                if (!(pathToReadFile == null)) {
                    writeDataInFile(decryptedMess.toString(), pathToWriteFile);
                } else {
                    System.out.println(decryptedMess);
                }
                break;
        }
    }

    public static void parsInput(String[] args) {
        for (int i = 0; i < args.length; i += 2) {
            if (args[i].equals("-mode")) {
                operation = args[i + 1];
            }
            if (args[i].equals("-key")) {
                if (args[i].matches("\\d+")) {
                    key = 0;
                } else {
                    key = Integer.parseInt(args[i + 1]);
                }
            }
            if (args[i].equals("-data")) {
                if (args[i].matches("\\W+")) {
                    message = "";
                } else {
                    message = args[i + 1];
                }
            }
            if (args[i].equals("-in")) {
                pathToReadFile = args[i + 1];
                message = readDataFromFile(pathToReadFile);
            }

            if (args[i].equals("-out")) {
                pathToWriteFile = args[i + 1];
            }

            if (args[i].equals("-alg")) {
                typeOfAlgorithm = args[i + 1];
            }
        }
    }

    public static void writeDataInFile(String encMessage, String path) {
        File file = new File(path);
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(encMessage);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readDataFromFile(String path) {
        StringBuilder out = new StringBuilder();
        try {
            out.append(new String(Files.readAllBytes(Paths.get(path))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toString();
    }

    public static StringBuilder decryptionUnicode(String message, int key) {
        char[] chars = message.toCharArray();
        StringBuilder output = new StringBuilder();

        for (char aChar : chars) {
            int valueCurrSym = aChar - key;
            char convertInChar = (char) valueCurrSym;
            output.append(convertInChar);
        }

        return output;
    }

    public static StringBuilder decryptionShift(String message, int offset) {
        return encryptionShift(message, 26 - (offset % 26));
    }

    public static StringBuilder encryptionUnicode(String message, int key) {
        char[] chars = message.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();

        for (char aChar : chars) {
            String regexForUpLetters = "[A-Z]";
            String regexForLowLetters = "[a-z]";
            if (!String.valueOf(aChar).matches(regexForUpLetters) || !String.valueOf(aChar).matches(regexForLowLetters)) {
                char encNonSymbol = (char) ((int) aChar + key);
                stringBuilder.append(encNonSymbol);
            } else {
                if ((int) aChar <= END && (int) aChar >= START) {
                    int newIndex = (int) aChar + key;
                    if (newIndex > END) {
                        int sub = newIndex % 122;
                        newIndex = (sub + START) - 1;
                    }

                    char encryptSym = (char) (newIndex);
                    stringBuilder.append(encryptSym);
                }
            }
        }
        return stringBuilder;
    }

    public static StringBuilder encryptionShift(String message, int offset) {
        StringBuilder result = new StringBuilder();
        String regexSmallLetter = "[a-z]";

        for (char character : message.toCharArray()) {
            if (String.valueOf(character).matches(regexSmallLetter)) {
                int originalAlphabetPosition = character - 'a';
                int newAlphabetPosition = (originalAlphabetPosition + offset) % 26;
                char newCharacter = (char) ('a' + newAlphabetPosition);
                result.append(newCharacter);
            } else if (String.valueOf(character).matches(regexBigLetter)) {
                int originalAlphabetPosition = character - 'A';
                int newAlphabetPosition = (originalAlphabetPosition + offset) % 26;
                char newCharacter = (char) ('A' + newAlphabetPosition);
                result.append(newCharacter);
            } else if (character == ' ' || character == '!') {
                result.append(character);
            }
        }
        return result;
    }
}
