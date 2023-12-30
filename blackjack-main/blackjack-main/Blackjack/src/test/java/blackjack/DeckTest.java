package blackjack;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    @Test
    void ofAndSize() {
        Deck deck = Deck.of(1);
        assertEquals(52, deck.size());

    }

    @Test
    void getCard() {
        Deck deck = Deck.of(1);
        assertEquals(Card.of(Suit.SPADES, Face.ACE), deck.getCard(0));
        assertEquals(Card.of(Suit.CLUBS, Face.ACE), deck.getCard(1));
        assertEquals(Card.of(Suit.SPADES, Face.FOUR), deck.getCard(12));
        assertEquals(Card.of(Suit.DIAMONDS, Face.KING), deck.getCard(51));
    }

    @Test
    void removeCard() {
        Deck deck = Deck.of(1);
        deck.removeCard(0);
        assertEquals(Card.of(Suit.CLUBS, Face.ACE), deck.getCard(0));
        deck.removeCard(10);
        assertEquals(Card.of(Suit.SPADES, Face.FOUR), deck.getCard(10));

    }

    @Test
    void addCards() {
        Deck deck = Deck.of(1);
        ArrayList<Card> discardPile = new ArrayList<>();
        discardPile.add(Card.of(Suit.HEARTS, Face.JACK));
        discardPile.add(Card.of(Suit.SPADES, Face.NINE));
        deck.addCards(discardPile);
        assertEquals(Card.of(Suit.HEARTS, Face.JACK), deck.getCard(52));
        assertEquals(Card.of(Suit.SPADES, Face.NINE), deck.getCard(53));
    }
    @Test
    void isEmpty() {
        Deck deck = Deck.of(1);
        assertFalse(deck.isEmpty());
        for(int i = 0; i < 52; i++) {
            deck.removeCard(0);
        }
        assertTrue(deck.isEmpty());
    }

    @Test
    void dealOneCard() {
        Deck deck = Deck.of(1);
        deck.shuffle();
        Card card = deck.getCard(0);
        assertEquals(card, deck.dealOneCard());
        assertNotSame(card, deck.getCard(0));
    }
}