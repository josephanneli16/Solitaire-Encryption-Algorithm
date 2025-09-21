
/*
/============================================================\
| Assignment: Program # 1: Solitaire Encryption               |
| Author: Joseph Anneli                                       |
|                                                             |
| This file implements the decryption process for the         |
| Solitaire. It reads in a deck configuration,                |
| retrieves the encrypted message from file, generates the    |
| corresponding keystream, and subtracts keystream values     |
| from ciphertext values to recover the original plaintext.   |
|                                                             |
| Language: Java (JDK 24)                                     |
| Ex. Packages: java.io, java.nio.file, java.util             |
\============================================================/
*/

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/************************************************************\
 * Class: Decrypt                                            *
 * Author: Joseph Anneli                                     *
 *                                                           *
 * Purpose: Provides functionality to decrypt a message      *
 * encrypted using the Solitaire algorithm. The class        *
 * reads the encrypted text, generates a matching keystream, *
 * and applies the decryption calculation to recover the     *
 * original message.                                         *
 *                                                           *
 * Class Methods:                                            *
 *  - main(String[]): void                                   *
 *  - lettersToNumbers(String): ArrayList<Integer>           *
 *  - decryptMessage(List<Integer>, List<Integer>): String   *
 *                                                           *
 \************************************************************/


public class Decrypt {


    /************************************************************\
     * Method: main                                              *
     * Purpose: Entry point of the decryption program. Reads the *
     * deck file, encrypted message file, converts message to    *
     * numbers, generates keystream, and produces decrypted text.*
     *                                                           *
     * Pre-condition: Two arguments provided (deck file, message)*
     * and encrypted.txt exists with a valid encrypted message.  *
     *                                                           *
     * Post-condition: Prints decrypted message to console.      *
     *                                                           *
     * Parameters:                                               *
     *   args - command line arguments: deck file path, message  *
     *                                                           *
     * Returns: None                                             *
     \************************************************************/


    public static void main (String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println("Error reading data");
            return;
        }

        String deckLine = Files.readString(Path.of(args[0])).trim();
        List<String> deckTokens = Arrays.asList(deckLine.split("\\s+"));

        List<Integer> deckNumbers = new ArrayList<>();
        for (String token : deckTokens) {
            int val = Deck.parseCard(token);
            deckNumbers.add(val);
        }
        System.out.println("\nDeck Numbers:" + deckNumbers);

        // Read encrypted from file
        String encrypted = "";
        try {
            List<String> lines = Files.readAllLines(Paths.get("encrypted.txt"));
            encrypted = lines.get(0).trim();
        } catch (IOException e) {
            System.err.println("Error reading encrypted message: " + e.getMessage());
        }

        System.out.println("\nEncrypted Messsage: " + encrypted);


        // Convert ciphertext letters to numbers
        ArrayList<Integer> encryptedNumbers = new ArrayList<>();
        encryptedNumbers = lettersToNumbers(encrypted);
        System.out.println("\nEncrypted to numbers: " + encryptedNumbers);

        ArrayList<Integer> keystreamResult = new ArrayList<>();
        for (int i = 0; i < encrypted.length(); i++) {
            int result = Deck.nextKeystream((ArrayList<Integer>) deckNumbers);
            keystreamResult.add(result);
        }

        System.out.println("\nKeystreams: " + keystreamResult);

        String decrypted = decryptMessage(encryptedNumbers, keystreamResult);
        System.out.println("\nDecrypted: " + decrypted);

    }

    /************************************************************\
     * Method: lettersToNumbers                                  *
     * Purpose: Converts a string of letters (A–Z) into numeric  *
     * values where A=1, B=2, ..., Z=26.                         *
     *                                                           *
     * Pre-condition: Input string contains only valid letters.  *
     *                                                           *
     * Post-condition: Returns a list of integers representing   *
     * each character in the string.                             *
     *                                                           *
     * Parameters:                                               *
     *   s - input string to convert                             *
     *                                                           *
     * Returns: ArrayList<Integer> numeric values of letters     *
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

    /************************************************************\
     * Method: decryptMessage                                    *
     * Purpose: Decrypts the given ciphertext numbers using the  *
     * generated keystream. Each cipher value has the keystream  *
     * value subtracted, wrapping around if negative.            *
     *                                                           *
     * Pre-condition: cipherNumbers and keystreamResult must be  *
     * the same length.                                          *
     *                                                           *
     * Post-condition: Returns the decrypted plaintext message   *
     * as uppercase letters.                                     *
     *                                                           *
     * Parameters:                                               *
     *   cipherNumbers - List of integers representing ciphertext*
     *   keystreamResult - List of integers for keystream values *
     *                                                           *
     * Returns: String plaintext message                         *
     \************************************************************/


    public static String decryptMessage(List<Integer> cipherNumbers, List<Integer> keystreamResult) {
        if (cipherNumbers.size() != keystreamResult.size()) {
            throw new IllegalArgumentException("Encrypted message and keystream lengths must match");
        }

        StringBuilder plain = new StringBuilder();
        for (int i = 0; i < cipherNumbers.size(); i++) {
            int diff = cipherNumbers.get(i) - keystreamResult.get(i);
            if (diff <= 0) diff += 26;  // wrap if negative or zero
            plain.append((char) ('A' + diff - 1));
        }
        return plain.toString();
    }

}
