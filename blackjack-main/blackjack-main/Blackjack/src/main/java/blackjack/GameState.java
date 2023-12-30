package blackjack;

import java.util.ArrayList;
import java.util.HashMap;

/*
Responsibilities: Creating a new game, running a turn, declaring if a player has won,
initializing a game which includes, creating a deck, creating N number of players,
setting the players initial "wallet"(how much money each player starts with),
and shuffling the deck.
Responsible for handling the "AI dealer's" actions, stand if 17+ or hit if under 17.
Returns data to Controller.

 */
public class GameState {

    private Deck deck;
    private final ArrayList<Player> players = new ArrayList<>();
    public ArrayList<Card> discardPile = new ArrayList<>();
    private int playerCount;
    private String phase = "betting";
    public int dealersValue;

    /*
    acts as a player in terms of hand, handValue balance does not matter
    dealer does not gain or lose money
     */
    private final Player dealer = Player.of(0);

    public GameState(int numOfPlayers) {
        int startingBalance = 1000;

        /*
        says 2-7 players
         */
        /*if (numOfPlayers > 7 || numOfPlayers < 2) {
            throw new IllegalArgumentException("Game does not support more than 7 players");
        }
         */

        deck = Deck.of(6);
        deck.shuffle();

        playerCount = numOfPlayers;

        for (int i = 0; i < playerCount; i++) {
            players.add(Player.of(startingBalance));
        }
    }
    public HashMap<String, String> startGame() {
        return updateHashMap("betting", 0);
    }

    //receives info from front end
    public HashMap<String, String> receiveInfo(HashMap<String, String> inputData) {
        String phase = inputData.get("phase");
        int playerIndex = Integer.parseInt(inputData.get("playerIndex"));
        if(phase.equals("betting")) {
            double bet = Double.parseDouble(inputData.get("bet"));
            return bettingPhase(playerIndex, bet);
        }
        if(phase.equals("playerTurn")) {
            String action = inputData.get("action");
            return playerTurn(playerIndex, action);
        }
        throw new IllegalArgumentException("Not a valid phase");
    }


    /*
    Returns int representing game over status.
    An int > 0 means the player at the index won (gameover).
    0 means the game is not over.
    -1 means every player is out of money (gameover, no winner).
     */
    public int isGameOver() {
        for (Player player : players) {
            if (player.hasEnoughMoney(3000)) {
                return players.indexOf(player);
            }
        }
        for (Player player : players) {
            if (player.hasMoney()) {
                return 0;
            }
        }
        return -1;
    }

    /*
    runs the betting phase, if the player has enough money to make the bet it will set their bet
    and move on to the next player, if we're on the last player we move onto the playerTurn,
    if the player does not have enough money returns an exception
     */
    public HashMap<String, String> bettingPhase(int playerIndex, double bet) {
        Player currentPlayer = players.get(playerIndex);
        if (currentPlayer.hasEnoughMoney(bet)) {
            currentPlayer.updateBet(bet);
        } else {
            throw new IllegalArgumentException("Cannot bet more money than you have");
        }
        if (playerIndex == playerCount - 1) {
            if(players.get(0).hasBlackjack()) {
                blackjackPlayerTurn(0);
            }
            return updateHashMap("playerTurn", 0);
        } else {
            return updateHashMap("playerTurn", playerIndex+1);
        }
    }

    /*
    returns a hashmap that contains the given phase and playerIndex as strings with their corresponding key
     */
    public HashMap<String, String> updateHashMap(String phase, int playerIndex) {
        HashMap<String, String> hm = new HashMap<>();
        hm.put("phase", phase);
        hm.put("playerIndex", Integer.toString(playerIndex));
        hm.put("playerBalance", parseAllPlayerBalances());
        hm.put("playerHands", parseAllPlayerHands());
        hm.put("playerBets", parseCurrentBets());
        hm.put("dealerHand", dealer.parsePlayerHand());
        return hm;
    }

    // parses the balances of all players into a string for front end
    public String parseAllPlayerBalances() {
        String allPlayerBalances = "";
        for (Player player : players) {
            String playerBalance = Double.toString(player.getBalance());
            allPlayerBalances += playerBalance;
            if (players.indexOf(player)+1 != playerCount) {
                allPlayerBalances += ",";
            }
        }
        return allPlayerBalances;
    }

    /*
    parses the player hand, if the given hand is [Card(Ace Hearts), Card(Two Clubs)] returns Ace Hearts, Two Clubs/...
    cards are seperated by commas, and players are seperated by "/"s, does that for all players in players list
     */
    public String parseAllPlayerHands() {
        String allPlayerHandString = "";
        for(Player player: players) {
            String playerHandString = player.parsePlayerHand();
            allPlayerHandString = allPlayerHandString.concat(playerHandString);

            if (players.indexOf(player)+1 != playerCount) {
                allPlayerHandString = allPlayerHandString.concat("/");
            }
        }
        return allPlayerHandString;
    }
    /*
    parses each players bet into a string seperated by commas
     */
    public String parseCurrentBets() {
        String currentPlayerBets = "";
        for(Player player: players) {
            String playerBet = Double.toString(player.getCurrentBet());
            currentPlayerBets = currentPlayerBets.concat(playerBet);
            if (players.indexOf(player)+1 != playerCount) {
                currentPlayerBets = currentPlayerBets.concat(",");
            }
        }

        return currentPlayerBets;
    }

    /*
    runs the "dealersTurn" or payout phase,
    after all players have already taken their turns, if the dealer's hand value is below
    17, the dealer must get a new card, otherwise they always stand, if a dealer
    goes above 21, anyone who didn't go above 21 or hasn't already been paid out
    automatically wins, otherwise the player win their bet if they have less
    than 21 and more than the dealer, and loses otherwise
     */

    public HashMap<String, String> dealersTurn() {

        if(dealer.hasBlackjack()) {
            dealerBlackjack();
        }
        else {
            dealerNoBlackjack();
        }
        int isGameOver = isGameOver();
        if(isGameOver() != 0) {
            return updateHashMap("playerTurn", isGameOver);
        } else {
            return handOutCards();
        }
    }

    /*
    when a dealer has blackjack, all players without a blackjack lose their bet.
     */
    public void dealerBlackjack() {
        for(Player player: players) {
            if(player.getStatus() == Status.NOMONEY) {
                break;
            }
            if (!player.hasBlackjack()) {
                player.decreaseBalance(player.getCurrentBet());
            }
        }
    }
    /*
    if the dealer does not have a blackjack hits while below 17, stands if 17+, busts 21+
    if the dealer does not bust, players win their bet by having a higher handValue,
    otherwise they lose their bet
     */
    public void dealerNoBlackjack() {
        while (dealer.handValue() < 17) {
            dealOneCard(dealer);
        }
        if (dealer.handValue() > 21) {
            for (Player player : players) {
                if (player.getStatus() == Status.STAND) {
                    player.increaseBalance(player.getCurrentBet());
                }
            }
        } else {
            for (Player player : players) {
                if (player.getStatus() == Status.STAND) {
                    if (dealer.handValue() > player.handValue()) {
                        player.decreaseBalance(player.getCurrentBet());
                    } else if (dealer.handValue() < player.handValue()) {
                        player.increaseBalance(player.getCurrentBet());
                    }
                }
            }
        }
        dealersValue = dealer.handValue();
    }


    /*
        runs a turn for a player, based on their chose "hit" or "stand"
        if a player chose to hit, they will be dealt a card from the deck, if that results
        in them busting it will move on to the next player, otherwise they will be prompted again
        if they want to hit or stand.
     */

    public HashMap<String, String> playerTurn(int playerIndex, String action) {
        if (action.equals("hit")) {
            return playerHit(playerIndex);

        }
        else if(action.equals("stand")) {
            return playerStand(playerIndex);
        }
        throw new IllegalArgumentException("can only hit or stand in playerTurn");
    }
    public HashMap<String, String> playerStand(int playerIndex) {
        Player currentPlayer = players.get(playerIndex);
        currentPlayer.updateStatus(Status.STAND);

        if(playerIndex == playerCount-1) {
            return dealersTurn();
        }

        playerIndex += 1;
        return updateHashMap("playerTurn", playerIndex);
    }

    public HashMap<String, String> playerHit(int playerIndex) {
        Player currentPlayer = players.get(playerIndex);

        dealOneCard(currentPlayer);

        if (currentPlayer.handValue() > 21) {
            currentPlayer.decreaseBalance(currentPlayer.getCurrentBet());
            currentPlayer.updateStatus(Status.BUST);
            if (!currentPlayer.hasMoney()) {
                currentPlayer.updateStatus(Status.NOMONEY);
            }
            if (playerIndex == playerCount - 1) {
                return dealersTurn();
            }
            else {
                playerIndex += 1;
                if (players.get(playerIndex).hasBlackjack()) {
                    return blackjackPlayerTurn(playerIndex);
                }
                return updateHashMap("playerTurn", playerIndex);
            }
        }

        return updateHashMap("playerTurn", playerIndex);

    }

    /*
    If a player has a blackjack run their turn through this
     */
    public HashMap<String, String>  blackjackPlayerTurn(int playerIndex) {
        Player currentPlayer = players.get(playerIndex);

        currentPlayer.updateBet(currentPlayer.getCurrentBet() * 1.5);
        currentPlayer.increaseBalance(currentPlayer.getCurrentBet());
        currentPlayer.updateStatus(Status.PAID);
        if (playerIndex == playerCount - 1) {
            return dealersTurn();
        }
        playerIndex += 1;
        return updateHashMap("playerTurn", playerIndex);

    }
    /*
    hands out two cards per player and two to the dealer from the top of the deck,
    if the dealer gets a blackjack switches to the dealers turn, otherwise it goes to the first
    players turn
     */
    public HashMap<String, String> handOutCards() {
        clearOldInfo();
        for(Player player: players) {
            dealOneCard(player);
            dealOneCard(player);
        }

        dealOneCard(dealer); //one of these has to be "face down"
        dealOneCard(dealer);

        if(dealer.handValue() == 21) {
            return dealersTurn();
        }

        return updateHashMap("playerTurn", 0);


    }

    /*
    deals one card to the given player, if the deck is empty gets the cards from the discardPile
     */
    public void dealOneCard(Player player) {
        if(deck.isEmpty()) {
            discardPileToDeck();
            deck.shuffle();
        }
        player.addCard(deck.dealOneCard());
    }
    /*
    takes the cards from the discard pile, puts them in the deck and then shuffles the deck
     */
    public void discardPileToDeck() {
        deck.addCards(discardPile);
        discardPile.clear();
    }

    /*
    prepares game for a new round by resetting all players status back to ready, and the playerIndex to 0
     */
    public void clearOldInfo() {
        for(Player player: players) {
            player.updateBet(0);
            discardPile.addAll(player.getPlayerHand());
            player.discardHand();
            if (player.getStatus() != Status.NOMONEY) {
                player.updateStatus(Status.READY);
                player.isTurn = false;
            }
        }
        discardPile.addAll(dealer.getPlayerHand());
        dealer.discardHand();
    }

    /*
    getters, used for testing
     */
    Deck getDeck() {
        return deck;
    }
    Player getDealer() {
        return dealer;
    }
    ArrayList<Card> getDiscardPile() {
        return discardPile;
    }
    ArrayList<Player> getPlayers() {
        return players;
    }
}
