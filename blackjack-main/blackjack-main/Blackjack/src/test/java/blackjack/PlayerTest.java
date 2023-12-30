package blackjack;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    /*
    test cases for of, and getters
     */
    @Test
    void of() {
        Player player = Player.of(1000);
        ArrayList<Card> playerHand = new ArrayList<>();
        assertEquals(1000, player.getBalance());
        assertEquals(Status.READY, player.getStatus());
        assertEquals(playerHand, player.getPlayerHand());
    }

    @Test
    void addCard() {
        Player player = Player.of(1000);
        player.addCard(Card.of(Suit.SPADES, Face.ACE));
        player.addCard(Card.of(Suit.HEARTS, Face.SIX));
        ArrayList<Card> playerHand = player.getPlayerHand();
        assertEquals(Card.of(Suit.SPADES, Face.ACE), playerHand.get(0));
        assertEquals(Card.of(Suit.HEARTS, Face.SIX), playerHand.get(1));
    }
    @Test
    void handValue() {
        Player player = Player.of(1000);
        player.addCard(Card.of(Suit.SPADES, Face.ACE));
        player.addCard(Card.of(Suit.HEARTS, Face.SIX));
        assertEquals(17, player.handValue()); //Ace = 11 + 6 = 17

        player.addCard(Card.of(Suit.DIAMONDS, Face.ACE));// Ace = 11 + Ace = 1 + 6 = 18
        assertEquals(18, player.handValue());

        player.addCard(Card.of(Suit.CLUBS, Face.EIGHT));// Ace = 1 + Ace = 1 + 6 + 8 = 16
        assertEquals(16, player.handValue());


        Player player1 = Player.of(1000);
        player1.addCard(Card.of(Suit.SPADES, Face.ACE));
        player1.addCard(Card.of(Suit.DIAMONDS, Face.ACE));
        player1.addCard(Card.of(Suit.CLUBS, Face.SIX));
        player1.addCard(Card.of(Suit.CLUBS, Face.ACE));
        assertEquals(19, player1.handValue()); // Ace = 1 + Ace = 1 + Ace = 1 + 6 = 19

        Player player2 = Player.of(1000);
        player2.addCard(Card.of(Suit.SPADES, Face.ACE));
        player2.addCard(Card.of(Suit.DIAMONDS, Face.TEN));
        assertTrue(player2.hasBlackjack()); // Ace = 11 + 10  = 21



    }
    @Test
    void increaseBalance() {
        Player player = Player.of(1000);
        player.increaseBalance(150);
        assertEquals(1150, player.getBalance());
    }

    @Test
    void decreaseBalance() {
        Player player = Player.of(1000);
        player.decreaseBalance(200);
        assertEquals(800, player.getBalance());
    }

    @Test
    void updateStatus() {
        Player player = Player.of(1000);
        player.updateStatus(Status.BUST);
        assertEquals(Status.BUST, player.getStatus());
        player.updateStatus(Status.PAID);
        assertEquals(Status.PAID, player.getStatus());
    }

    @Test
    void updateBet() {
        Player player = Player.of(1000);
        player.updateBet(100);
        assertEquals(100, player.getCurrentBet());
        player.updateBet(0);
        assertEquals(0, player.getCurrentBet());
        player.updateBet(150);
        assertEquals(150, player.getCurrentBet());
    }

}