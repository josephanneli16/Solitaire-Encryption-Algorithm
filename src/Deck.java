
/*
/============================================================\
| Assignment: Program # 1: Solitaire Encryption               |
| Author: Joseph Anneli                                       |
|                                                             |
| This file implements the card deck operations required      |
| for the Solitaire encryption system. It includes            |
| parsing of cards into numeric values, movement of jokers,   |
| triple cut, bottom cut, and keystream value generation.     |
|                                                             |
| Language: Java (JDK 24)                                     |
| Ex. Packages:java.util                                      |
\============================================================/
*/


import java.util.*;


/************************************************************\
 * Class: Deck                                               *
 * Author: Joseph Anneli                                     *
 *                                                           *
 * Purpose: Provides all deck manipulation operations for    *
 * the Solitaire algorithm, including card parsing,          *
 * joker movement, triple cut, bottom move, and keystream    *
 * determination.                                            *
 *                                                           *
 * Class Methods:                                            *
 *  - parseCard(String): int                                 *
 *  - nextKeystream(ArrayList<Integer>): int                 *
 *                                                           *
 \************************************************************/



public class Deck {


    /************************************************************\
     * Method: parseCard                                         *
     * Purpose: Converts a string representation of a card       *
     * (e.g., "AC", "10D", "JB") into its numeric value. A=1,    *
     * J=11, Q=12, K=13. Clubs 1–13, Diamonds 14–26, Jokers 27–28*
     *                                                           *
     * Pre-condition: Input string must be a valid card label.   *
     *                                                           *
     * Post-condition: Returns an integer value representing the *
     * card’s position in the deck.                              *
     *                                                           *
     * Returns: int numeric value of the card                    *
     \************************************************************/


    static int parseCard(String raw) {
        String t = raw.trim().toUpperCase();
        if (t.equals("JA")) {
            return 27;
        }
        if (t.equals("JB")) {
            return 28;
        }

        char suit = t.charAt(t.length() - 1);
        String rank = t.substring(0, t.length() - 1);

        int base;
        switch (rank) {
            case "A":
                base = 1;
                break;
            case "J":
                base = 11;
                break;
            case "Q":
                base = 12;
                break;
            case "K":
                base = 13;
                break;
            default:
                base = Integer.parseInt(rank);
                if (base < 1 || base > 13) {
                    throw new IllegalArgumentException("Invalid rank: " + rank);
                }
        }

        if (suit == 'C') {
            return base;
        }
        else if(suit == 'D') {
            return base + 13;
        }
        else {
            throw new IllegalArgumentException("Invalid suit: " + suit);
        }
    }


    /************************************************************\
     * Method: nextKeystream                                     *
     * Purpose: Produces the next valid keystream value by       *
     * applying all Solitaire Cipher deck operations in order.   *
     *                                                           *
     * Pre-condition: Deck must be initialized with valid values *
     * (1–28).                                                   *
     *                                                           *
     * Post-condition: Returns a keystream value (1–26) that is  *
     * not a joker. If a joker is selected, the process repeats. *
     *                                                           *
     * Parameters:                                               *
     *   deckNumber - the current deck of integers               *
     *                                                           *
     * Returns: int keystream value (1–26)                       *
     \************************************************************/


    static int nextKeystream(ArrayList<Integer> deckNumber) {
        while (true) {
            moveJokerA(deckNumber);
            moveJokerB(deckNumber);
            tripleCut(deckNumber);
            moveBottom(deckNumber);

            int keystream = determineKeystream(deckNumber);
            if (keystream != -1) {   // only return if not a joker
                return keystream;
            }
        }
    }

    /************************************************************\
     * Method: moveJokerA                                        *
     * Purpose: Moves Joker A (27) down one position in the deck.*
     * If Joker A is at the bottom, it wraps around below the    *
     * top card.                                                 *
     *                                                           *
     * Pre-condition: Joker A must exist in the deck.            *
     *                                                           *
     * Post-condition: Deck order is updated with Joker A moved. *
     *                                                           *
     * Parameters:                                               *
     *   deckNumber - the current deck of integers               *
     *                                                           *
     * Returns: None                                             *
     \************************************************************/


    private static void moveJokerA(ArrayList<Integer> deckNumber) {
        int jokerPosition = deckNumber.indexOf(27);
        if (jokerPosition == -1) {
            throw new IllegalStateException("Joker A not found in deck.");
        }
        int swapPosition = (jokerPosition + 1) % deckNumber.size();
        Collections.swap(deckNumber, jokerPosition, swapPosition);
    }


    /************************************************************\
     * Method: moveJokerB                                        *
     * Purpose: Moves Joker B (28) down two positions in the deck*
     * with wrap-around if at the bottom.                        *
     *                                                           *
     * Pre-condition: Joker B must exist in the deck.            *
     *                                                           *
     * Post-condition: Deck order is updated with Joker B moved. *
     *                                                           *
     * Parameters:                                               *
     *   deckNumber - the current deck of integers               *
     *                                                           *
     * Returns: None                                             *
     \************************************************************/


    private static void moveJokerB(ArrayList<Integer> deckNumber) {
        for (int step = 0; step < 2; step++) {
            int jokerPosition = deckNumber.indexOf(28);
            if (jokerPosition == -1) {
                throw new IllegalStateException("Joker B (28) not found in deck.");
            }
            int swapPosition = (jokerPosition + 1) % deckNumber.size(); // wrap-around
            Collections.swap(deckNumber, jokerPosition, swapPosition);
        }
    }


    /************************************************************\
     * Method: tripleCut                                         *
     * Purpose: Performs a triple cut around both jokers. All    *
     * cards above the first joker move to the bottom, and all   *
     * cards below the second joker move to the top.             *
     *                                                           *
     * Pre-condition: Both jokers must exist in the deck.        *
     *                                                           *
     * Post-condition: Deck order is rearranged by triple cut.   *
     *                                                           *
     * Parameters:                                               *
     *   deckNumber - the current deck of integers               *
     *                                                           *
     * Returns: None                                             *
     \************************************************************/


    private static void tripleCut(ArrayList<Integer> deckNumber) {
        ArrayList<Integer> bottom = new ArrayList<>();
        ArrayList<Integer> mid = new ArrayList<>();
        ArrayList<Integer> top = new ArrayList<>();
        int position27 = deckNumber.indexOf(27);
        int position28 = deckNumber.indexOf(28);

        int a = Math.min(position27, position28);
        int b = Math.max(position27, position28);

        for (int i = 0; i < a; i++) {
            bottom.add(deckNumber.get(i));
        }
        for (int i = a; i <= b; i++) {
            mid.add(deckNumber.get(i));
        }
        for (int i = b + 1; i < deckNumber.toArray().length; i++) {
            top.add(deckNumber.get(i));
        }

        deckNumber.clear();
        deckNumber.addAll(top);
        deckNumber.addAll(mid);
        deckNumber.addAll(bottom);
    }

    /************************************************************\
     * Method: moveBottom                                        *
     * Purpose: Cuts the deck using the value of the bottom card *
     * (jokers count as 27). That many cards are moved from the  *
     * top to just above the bottom card.                        *
     *                                                           *
     * Pre-condition: Deck must contain at least one card.       *
     *                                                           *
     * Post-condition: Deck is updated with the bottom cut.      *
     *                                                           *
     * Parameters:                                               *
     *   deckNumber - the current deck of integers               *
     *                                                           *
     * Returns: None                                             *
     \************************************************************/


    private static void moveBottom(ArrayList<Integer> deckNumber) {
        ArrayList<Integer> firstNumbers = new ArrayList<>();
        ArrayList<Integer> holder = new ArrayList<>();

        int lastNumber = deckNumber.get(deckNumber.size() - 1);
        // Jokers count as 27
        int cutSize = Math.min(lastNumber, 27);

        for (int i = 0; i < cutSize; i++) {
            firstNumbers.add(deckNumber.get(i));
        }
        for (int i = cutSize; i < deckNumber.size() - 1; i++) {
            holder.add(deckNumber.get(i));
        }

        deckNumber.clear();
        deckNumber.addAll(holder);
        deckNumber.addAll(firstNumbers);
        deckNumber.add(lastNumber);
    }


    /************************************************************\
     * Method: determineKeystream                                *
     * Purpose: Determines the output keystream value by using   *
     * the top card to count into the deck. If the resulting     *
     * card is a joker, signals to skip (-1).                    *
     *                                                           *
     * Pre-condition: Deck must contain valid values 1–28.       *
     *                                                           *
     * Post-condition: Returns a valid keystream value or -1 if  *
     * a joker was selected.                                     *
     *                                                           *
     * Parameters:                                               *
     *   deckNumber - the current deck of integers               *
     *                                                           *
     * Returns: int keystream value (1–26) or -1 if joker        *
     \************************************************************/


    private static int determineKeystream(ArrayList<Integer> deckNumber) {
        int topCard = deckNumber.get(0);
        int t = Math.min(topCard, 27);  // jokers count as 27 for lookup
        int keystream = deckNumber.get(t);

        // if keystream is joker, signal skip
        if (keystream == 27 || keystream == 28) {
            return -1;
        }
        return keystream;
    }
}
