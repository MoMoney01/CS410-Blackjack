package blackjack;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {
    GameState makeGame() {
        return new GameState(2);
    }
    @Test
    void startGame() {
        GameState game = makeGame();
        assertEquals(2, game.getPlayers().size());
        assertEquals(0, game.getDiscardPile().size());
    }

    @Test
    void placeABet() {
        GameState game = makeGame();
        double playerBet = game.getPlayers().get(0).getBalance();
        game.bettingPhase(0, 100);
        assertEquals(100, game.getPlayers().get(0).getCurrentBet());
        assertThrows(IllegalArgumentException.class, () -> game.bettingPhase(0, playerBet+1));
    }
    @Test
    void playerHit() {
        GameState game = makeGame();
        game.playerTurn(0, "hit");
        assertEquals(1, game.getPlayers().get(0).getPlayerHand().size());
        int handValue = game.getPlayers().get(0).handValue();
        game.playerTurn(0, "stand");
        assertEquals(1, game.getPlayers().get(0).getPlayerHand().size());
        assertEquals(handValue, game.getPlayers().get(0).handValue());
    }
    @Test
    void playerStand() {
        GameState game = makeGame();
        int handValue = game.getPlayers().get(0).handValue();
        game.playerTurn(0, "stand");
        assertEquals(0, game.getPlayers().get(0).getPlayerHand().size());
        assertEquals(handValue, game.getPlayers().get(0).handValue());
    }


    @Test
    void playerBlackjack() {
        GameState game = makeGame();
        game.bettingPhase(0, 100);
        game.blackjackPlayerTurn(0);
        assertEquals(1150, game.getPlayers().get(0).getBalance());
    }

    /*
    makes sure a dealer hits if 16 or below, and stands if above 17
     */
    @Test
    void dealerHit() {
        GameState game = makeGame();
        Player dealer = game.getDealer();
        dealer.discardHand();
        dealer.addCard(Card.of(Suit.SPADES, Face.SIX));
        dealer.addCard(Card.of(Suit.DIAMONDS, Face.TEN));
        game.dealerNoBlackjack();
        assertTrue(dealer.handValue() > 16);
        dealer.discardHand();
        dealer.addCard(Card.of(Suit.SPADES, Face.EIGHT));
        dealer.addCard(Card.of(Suit.DIAMONDS, Face.TEN));
        game.dealerNoBlackjack();
        assertEquals(18, dealer.handValue());

    }
    @Test
    void dealerStand() {
        GameState game = makeGame();
        Player dealer = game.getDealer();
        dealer.discardHand();
        dealer.addCard(Card.of(Suit.SPADES, Face.EIGHT));
        dealer.addCard(Card.of(Suit.DIAMONDS, Face.TEN));
        game.dealerNoBlackjack();
        assertEquals(18, dealer.handValue());

    }
    /*
    player who has less than dealer, loses their bet
     */
    @Test
    void dealerTurnWin() {
        GameState game = makeGame();
        Player dealer = game.getDealer();
        Player player0 = game.getPlayers().get(0);

        player0.discardHand();
        player0.updateBet(100);
        player0.addCard(Card.of(Suit.HEARTS, Face.THREE));
        player0.addCard(Card.of(Suit.CLUBS, Face.EIGHT));
        player0.addCard(Card.of(Suit.CLUBS, Face.FOUR));
        player0.updateStatus(Status.STAND);

        dealer.discardHand();
        dealer.addCard(Card.of(Suit.SPADES, Face.EIGHT));
        dealer.addCard(Card.of(Suit.DIAMONDS, Face.TEN));


        game.dealerNoBlackjack();
        assertEquals(18, dealer.handValue());
        assertEquals(900, player0.getBalance());
    }
    /*
    player who has equal to dealer, balance stays same(tie)
    */
    @Test
    void dealerTurnTie() {
        GameState game = makeGame();
        Player dealer = game.getDealer();
        Player player1 = game.getPlayers().get(1);

        player1.discardHand();
        player1.updateBet(100);
        player1.addCard(Card.of(Suit.HEARTS, Face.NINE));
        player1.addCard(Card.of(Suit.CLUBS, Face.NINE));
        player1.updateStatus(Status.STAND);

        dealer.discardHand();
        dealer.addCard(Card.of(Suit.SPADES, Face.EIGHT));
        dealer.addCard(Card.of(Suit.DIAMONDS, Face.TEN));


        game.dealerNoBlackjack();
        assertEquals(18, dealer.handValue());
        assertEquals(1000, player1.getBalance());
    }
    /*
    player who has more than dealer, wins bet
    */
    @Test
    void dealerTurnLoss() {
        GameState game = makeGame();
        Player dealer = game.getDealer();
        Player player1 = game.getPlayers().get(0);

        player1.discardHand();
        player1.updateBet(100);
        player1.addCard(Card.of(Suit.HEARTS, Face.NINE));
        player1.addCard(Card.of(Suit.CLUBS, Face.NINE));
        player1.addCard(Card.of(Suit.CLUBS, Face.ACE));
        player1.updateStatus(Status.STAND);

        dealer.discardHand();
        dealer.addCard(Card.of(Suit.SPADES, Face.EIGHT));
        dealer.addCard(Card.of(Suit.DIAMONDS, Face.TEN));


        game.dealerNoBlackjack();
        assertEquals(18, dealer.handValue());
        assertEquals(1100, player1.getBalance());
    }
    /*
    player who has more than dealer, wins bet
    */
    @Test
    void dealerTurnPlayerBlackjack() {
        GameState game = makeGame();
        Player dealer = game.getDealer();
        Player player1 = game.getPlayers().get(0);

        player1.discardHand();
        player1.updateBet(100);
        player1.addCard(Card.of(Suit.HEARTS, Face.TEN));
        player1.addCard(Card.of(Suit.CLUBS, Face.ACE));
        player1.increaseBalance(100*1.5);
        player1.updateStatus(Status.PAID);

        dealer.discardHand();
        dealer.addCard(Card.of(Suit.SPADES, Face.EIGHT));
        dealer.addCard(Card.of(Suit.DIAMONDS, Face.TEN));


        game.dealerNoBlackjack();
        assertEquals(18, dealer.handValue());
        assertEquals(1150, player1.getBalance());
    }

    /*
        The dealer has blackjack,
        player0 does not, lose bet
        player1 does, tie
     */
    @Test
    void dealerHasBlackjack() {
        GameState game = makeGame();
        Player dealer = game.getDealer();
        Player player0 = game.getPlayers().get(0);
        Player player1 = game.getPlayers().get(1);

        player0.discardHand();
        player0.updateBet(100);
        player0.addCard(Card.of(Suit.HEARTS, Face.TEN));
        player0.addCard(Card.of(Suit.CLUBS, Face.SEVEN));

        player1.discardHand();
        player1.updateBet(100);
        player1.addCard(Card.of(Suit.HEARTS, Face.TEN));
        player1.addCard(Card.of(Suit.CLUBS, Face.ACE));
        assertEquals(2 , player1.getPlayerHand().size());
        assertEquals(21 , player1.handValue());
        assertTrue(player1.hasBlackjack());

        dealer.discardHand();
        dealer.addCard(Card.of(Suit.SPADES, Face.ACE));
        dealer.addCard(Card.of(Suit.DIAMONDS, Face.TEN));


        game.dealerBlackjack();
        assertEquals(900, player0.getBalance());
        assertEquals(1000, player1.getBalance());
    }

    @Test
    void discardPileToDeck() {
        GameState game = makeGame();
        HashMap<String, String> temp = game.handOutCards();
        temp = game.handOutCards();
        int deckSize = game.getDeck().size();
        assertEquals(6, game.getDiscardPile().size());
        game.discardPileToDeck();
        assertEquals(0, game.getDiscardPile().size());
        assertEquals(deckSize+6, game.getDeck().size());
    }

    @Test
    void clearOldInfo() {
        GameState game = makeGame();
        game.clearOldInfo();
        assertEquals(0, game.getDealer().handValue());
        assertEquals(0, game.getPlayers().get(0).handValue());
        assertEquals(Status.READY, game.getPlayers().get(0).getStatus());
        game.getPlayers().get(0).updateStatus(Status.NOMONEY);
        game.clearOldInfo();
        assertNotSame(Status.READY, game.getPlayers().get(0).getStatus());
    }
}