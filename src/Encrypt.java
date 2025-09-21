/*
/============================================================\
| Program # 1: Solitaire Encryption                           |
| Author: Joseph Anneli                                       |
|                                                             |
| Description:                                                |
| This program implements the encryption part of the          |
| Solitaire Cipher using a deck of cards. It reads in a       |
| shuffled deck from a file, processes a message from input,  |
| generates a keystream, and produces an encrypted message.   |
| The result is saved to `encrypted.txt`.                     |
|                                                             |
| Language: Java (JDK 24)                                     |
| Ex. Packages: java.io, java.nio.file, java.util             |
|                                                             |
\============================================================/
*/


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


/************************************************************\
 * Class: Encrypt                                            *
 * Author: Joseph Anneli                                     *
 *                                                           *
 * Purpose: This class handles the encryption process for    *
 * messages using the Solitaire Cipher algorithm. It reads   *
 * a deck file and message file, converts the message to     *
 * numbers, generates a keystream from the deck, encrypts    *
 * the message, and writes the output to a file.             *
 *                                                           *
 *                                                           *
 * Class Methods:                                            *
 *  - main(String[] args): void                              *
 *                                                           *
 * Inst. Methods:                                            *
 *  - readMessages(String path): List<String>                *
 *  - encryptMessage(List<Integer>, List<Integer>): String   *
 *  - lettersToNumbers(String): ArrayList<Integer>           *
 \************************************************************/


public class Encrypt {

    /************************************************************\
     * Method: main                                              *
     * Purpose: Entry point of the program. Validates input,     *
     * reads the deck and message files, generates the keystream,*
     * encrypts the message, and writes the result to file.      *
     *                                                           *
     * Pre-condition: args must include two valid file paths:    *
     *   - args[0]: deck file                                    *
     *   - args[1]: message file                                 *
     *                                                           *
     * Post-condition: Encrypted message is printed to console   *
     * and saved into `encrypted.txt`.                           *
     *                                                           *
     * Parameters:                                               *
     *   args - command line arguments for file paths            *
     *                                                           *
     * Returns: None                                             *
     \************************************************************/


    public static void main(String[] args) throws Exception {
        // Read file
        if (args.length != 2) {
            System.err.println("Error reading data");
            return;
        }

        // Get Deck tokens
        String deckLine = Files.readString(Path.of(args[0])).trim();
        List<String> deckTokens = Arrays.asList(deckLine.split("\\s+"));

        System.out.println("\nDeck tokens: " + deckTokens);

        int wordCount = 0;
        List<String> messages = readMessages(args[1]);
        for (String msg : messages) {
            wordCount = msg.length();
            System.out.println("\nMessage: " + msg);
        }

        // Make the messages to numbers
        ArrayList<Integer> lettersToNum= new ArrayList<>();
        lettersToNum = lettersToNumbers(String.valueOf(messages));
        System.out.println("\nConverted Letters: " + lettersToNum);

        List<Integer> deckNumbers = new ArrayList<>();
        for (String token : deckTokens) {
            int val = Deck.parseCard(token);
            deckNumbers.add(val);
        }
        System.out.println("\nDeck Numbers: " + deckNumbers);

        // Declare keystream result
        ArrayList<Integer> keystreamResult = new ArrayList<>();

        for (int i = 0; i < wordCount; i++) {
            int result = Deck.nextKeystream((ArrayList<Integer>) deckNumbers);
            keystreamResult.add(result);
        }

        System.out.println("\nKeystream result: " + keystreamResult);

        // Get the encrypted from Deck.java
        String encrypted = encryptMessage(lettersToNum, keystreamResult);

        // Save encrypted message to a file
        try (PrintWriter out = new PrintWriter("encrypted.txt")) {
            out.println(encrypted);
        } catch (IOException e) {
            System.err.println("Error writing encrypted message: " + e.getMessage());
        }

        System.out.println("\nEncrypted message: " + encrypted);

    }


    /************************************************************\
     * Method: readMessages                                      *
     * Purpose: Reads a plaintext message file, strips out non-  *
     * alphabetic characters, converts letters to uppercase, and *
     * pads each line to a multiple of 5 characters with 'X'.    *
     *                                                           *
     * Pre-condition: Input file exists and is readable.         *
     *                                                           *
     * Post-condition: Returns a list of cleaned and padded      *
     * message strings ready for encryption.                     *
     *                                                           *
     * Parameters:                                               *
     *   path - String path to the plaintext message file        *
     *                                                           *
     * Returns: List<String> containing processed message lines  *
     \************************************************************/


    private static List<String> readMessages(String path) {
        List<String> out = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(Path.of(path))) {
            for (String line; (line = br.readLine()) != null; ) {
                StringBuilder cleaned = new StringBuilder(line.length());
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    if (Character.isLetter(c)) {
                        cleaned.append(Character.toUpperCase(c));
                    }
                }
                while (cleaned.length() % 5 != 0) {
                    cleaned.append('X');
                }
                out.add(cleaned.toString());
            }
        } catch (IOException e) {
            System.err.println("Error reading messages file: " + e.getMessage());
        }
        return out;
    }


    /************************************************************\
     * Method: encryptMessage                                    *
     * Purpose: Encrypts a numeric message using a numeric       *
     * keystream by modular addition (mod 26), then converts     *
     * the result back to letters (A–Z).                        *
     *                                                           *
     * Pre-condition: decryptLetters and keystreamResult must be *
     * the same length.                                          *
     *                                                           *
     * Post-condition: Returns a string of uppercase encrypted   *
     * letters.                                                  *
     *                                                           *
     * Parameters:                                               *
     *   decryptLetters - List<Integer> numeric message (A=1..Z=26) *
     *   keystreamResult - List<Integer> keystream values        *
     *                                                           *
     * Returns: String encrypted message                        *
     \************************************************************/



    public static String encryptMessage(List<Integer> decryptLetters, List<Integer> keystreamResult) {
        if (decryptLetters.size() != keystreamResult.size()) {
            throw new IllegalArgumentException("Message and keystream lengths must match");
        }

        StringBuilder cipher = new StringBuilder();

        for (int i = 0; i < decryptLetters.size(); i++) {
            int sum = decryptLetters.get(i) + keystreamResult.get(i);
            sum = ((sum - 1) % 26) + 1;
            cipher.append((char) ('A' + sum - 1)); // convert back to A–Z
        }
        return cipher.toString();
    }


    /************************************************************\
     * Method: lettersToNumbers                                  *
     * Purpose: Converts a string of letters into numeric values *
     * where A=1, B=2, ..., Z=26. Ignores non-letter characters. *
     *                                                           *
     * Pre-condition: Input string may contain any characters.   *
     *                                                           *
     * Post-condition: Returns a list of integers corresponding  *
     * to uppercase alphabetic characters in the string.         *
     *                                                           *
     * Parameters:                                               *
     *   s - String containing message text                      *
     *                                                           *
     * Returns: ArrayList<Integer> numeric values                *
     \************************************************************/


    public static ArrayList<Integer> lettersToNumbers(String s) {
        ArrayList<Integer> out = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            char c = Character.toUpperCase(s.charAt(i));
            if (c >= 'A' && c <= 'Z') {
                out.add((c - 'A') + 1);  // A=1..Z=26
            }
        }
        return out;
    }
}
