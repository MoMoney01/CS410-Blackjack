package blackjack;

import java.util.ArrayList;

/*
a representation of a human player in a game of blackjack
responsibilities: contains the players hand, the player name,
the players wallet(how much money the player has),
getter for the value of a players hand.
 */
public class Player {

    private ArrayList<Card> playerHand = new ArrayList<>();
    private double balance; // represents how much money the player has

    private Status status = Status.READY; //ready(has not taken their turn), stand, bust, paid

    private double currentBet = 0;
    public boolean isTurn; // tracks if it is currently the player's turn
    public boolean isLast; // tracks if this player is the last player in game order
    public boolean winner; // tracks if this player won, aka if increaseBalance() is called
    public boolean stillIn = true; // tracks if this player is still in the game, aka if their balance is not 0
    private Player(int balance) { this.balance = balance; }

    public static Player of(int balance) { return new Player(balance); }

    /*
    getters for status, balance playerHand, and currentBet
     */
    public Status getStatus() {
        return this.status;
    }
    public double getBalance() {
        return this.balance;
    }
    public ArrayList<Card> getPlayerHand() {
        return playerHand;
    }

    public double getCurrentBet() {
        return currentBet;
    }

    /*
    returns the value of a players hand based the sum of each card numeric value.
    the cards with numbers "2-10" will be worth their numeric values from 2-10,
    a 2 card will be worth 2, a 3 card will be worth 3 etc. a Jack, Queen, and King
    will be all worth 10, an Ace will be worth 11 if the value of the players hand excluding the ace
    is at most a 10, and 1 otherwise.
    For example:
    If a player has a jack and an ace, the ace will be worth 11
    If the player has a 6 a 9 and an ace, it will be
    worth 1 giving their hand a value of 16.
     */
    public int handValue() {
        int handValue = 0;
        int aces = 0;
        for(Card card: playerHand) {
            if(card.getFace() != Face.ACE) {
                handValue += card.getValue();
            }
            else {
                aces += 1;
            }
        }
        /*
        we should factor in aces last, if a player has the following hand:
        [Ace, 6, 5, 7] they did not bust, since the ace would be registered as a 1
        so first 6, 5, 7 are calculated as 18, and then an ace as 1, likewise if a player
        has the following cards:
        [Ace, 6, Ace, Ace] we must factor in the fact that one Ace is worth 11 and the others are
        worth one, giving a hand value of 19.
         */
        for(int i = 0; i < aces; i++) {
            if (handValue <= (11-aces)) {
                handValue += 11;
            }
            else {
                handValue += 1;
            }
        }
        return handValue;
    }

    /*
    increase and decrease the balance of a player, used for winning/losing a bet
     */
    public void increaseBalance(double amount) {
        this.balance += amount;
        this.winner = true;
    }
    public void decreaseBalance(double amount) {
        this.balance -= amount;
    }
    /*
    returns true if player has >0 money, false if they have 0
     */
    public boolean hasMoney() {
        return balance > 0;
    }
    /*
    updates the bet of the player
     */
    public void updateBet(double bet) {
        currentBet = bet;
    }
    /*
    updates the players status
     */
    public void updateStatus(Status status) {
        this.status = status;
    }
    /*
    adds a card to the players hand
     */
    public void addCard(Card card) {
        this.playerHand.add(card);
    }
    /*
    clears the players hand
     */
    public void discardHand() {
        playerHand.clear();
    }

    /*
    if player has equal or more balance, return true, false otherwise
     */
    public boolean hasEnoughMoney(double amount) {
        return amount <= balance;
    }

    public boolean hasBlackjack() {
        return handValue() == 21 && playerHand.size() == 2;
    }

    public String parsePlayerHand() {
        String playerHandString = "";
        for(Card card: playerHand) {
            String cardString = card.toString();
            playerHandString = playerHandString.concat(cardString);
            if (playerHand.indexOf(card) != playerHand.size()) {
                playerHandString = playerHandString.concat(",");
            }
        }
        return playerHandString;
    }
}
