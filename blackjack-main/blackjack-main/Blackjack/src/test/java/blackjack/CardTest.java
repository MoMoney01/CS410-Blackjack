package blackjack;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class CardTest {

    @Test
    void getSuit() {
        Card card = Card.of(Suit.SPADES, Face.TWO);
        Card card1 = Card.of(Suit.HEARTS, Face.TEN);

        assertEquals(Suit.SPADES, card.getSuit());
        assertEquals(Suit.HEARTS, card1.getSuit());
    }

    @Test
    void getFace() {
        Card card = Card.of(Suit.SPADES, Face.TWO);
        Card card1 = Card.of(Suit.HEARTS, Face.TEN);
        Card card3 = Card.of(Suit.CLUBS, Face.JACK);

        assertEquals(Face.TWO, card.getFace());
        assertEquals(Face.TEN, card1.getFace());
        assertEquals(Face.JACK, card3.getFace());

    }

    @Test
    void getValue() {
        Card card = Card.of(Suit.SPADES, Face.TWO);
        Card card1 = Card.of(Suit.DIAMONDS, Face.KING);
        Card card2 = Card.of(Suit.CLUBS, Face.JACK);
        Card card3 = Card.of(Suit.CLUBS, Face.ACE);

        assertEquals(2, card.getValue());
        assertEquals(10, card1.getValue());
        assertEquals(10, card2.getValue());
        assertThrows(IllegalArgumentException.class, card3::getValue);
    }

}