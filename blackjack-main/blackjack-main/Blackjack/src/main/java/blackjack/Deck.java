package blackjack;

import java.util.ArrayList;
import java.util.Collections;

/*
representation of a Deck of cards: contains Card objects.
responsible for: getting, removing, and adding cards to the deck.
shuffling the deck
 */
public class Deck {
    private final ArrayList<Card> deck = new ArrayList<>();

    /*
    creates a deck with count number of each 52card standard deck so includes
    count number of each card.
    */
    private Deck(int count) {
        for(int i = 0; i < count; i++) {
            for (Face face : Face.values()) {
                for (Suit suit : Suit.values()) {
                    deck.add(Card.of(suit, face));
                }
            }
        }
    }

    public static Deck of(int count) { return new Deck(count);}
    //gets a card at the given index
    public Card getCard(int index) {
        return deck.get(index);
    }
    //remove a card at the given index
    public void removeCard(int index) {
        deck.remove(index);
    }
    /*
    returns the first card in the deck and removes it
     */
    public Card dealOneCard() {
        Card card = deck.get(0);
        deck.remove(0);
        return card;
    }
    //adds cards to the end of a pile
    public void addCards(ArrayList<Card> cardList) {
        deck.addAll(cardList);
    }
    //shuffles the pile
    public void shuffle() {
        Collections.shuffle(deck);
    }

    //checks if the pile is empty
    public boolean isEmpty() {
        return deck.isEmpty();
    }
    public int size() {return deck.size(); }


}
