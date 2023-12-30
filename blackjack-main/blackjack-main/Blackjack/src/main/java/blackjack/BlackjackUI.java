package blackjack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

public class BlackjackUI extends JFrame {
    private GameState gameState;
    private int currentPlayerIndex;
    private JButton hitButton;
    private JButton standButton;
    private JLabel[] playerHandLabels;
    private JLabel[] playerStatusLabel;
    private JLabel dealerHandLabel;
    private JLabel statusLabel;
    private JLabel winnerLabel;
    boolean prebet;
    private double[] betCheck;
    private double[] balCheck;
    private int dealersLastValue;

    public BlackjackUI() {
        //  Input the number of players
        String numPlayersString = JOptionPane.showInputDialog("Enter the number of players:");
        int numPlayers;
        try {
            numPlayers = Integer.parseInt(numPlayersString);
        } catch (NumberFormatException e) {
            // Handle invalid input
            numPlayers = 2; // Default to 2 players
        }

        // Initialize the game state with the n-number of players
        gameState = new GameState(numPlayers);
        betCheck = new double[numPlayers];
        balCheck = new double[numPlayers];

        getBets(numPlayers);
        gameState.getPlayers().get(numPlayers - 1).isLast = true;

        // Deal two cards to each player
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < numPlayers; j++) {
                gameState.dealOneCard(gameState.getPlayers().get(j));
            }
            gameState.dealOneCard(gameState.getDealer());
        }

        // Set up the UI components
        hitButton = new JButton("Hit");
        standButton = new JButton("Stand");
        playerHandLabels = new JLabel[numPlayers];
        playerStatusLabel = new JLabel[numPlayers];
        dealerHandLabel = new JLabel("Dealer Hand: ");
        winnerLabel = new JLabel();

        // Set layout
        setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(hitButton);
        buttonPanel.add(standButton);

        // Players' panels
        JPanel playerPanel = new JPanel(new GridLayout(gameState.getPlayers().size(), 2));
        for (int i = 0; i < numPlayers; i++) {
            playerStatusLabel[i] = new JLabel("Status: ");
            playerHandLabels[i] = new JLabel("Player " + (i + 1) + " Hand: ");
            playerPanel.add(playerStatusLabel[i]);
            playerPanel.add(playerHandLabels[i]);
        }
        add(playerPanel, BorderLayout.NORTH);

        // Dealer's panel
        JPanel dealerPanel = new JPanel(new GridLayout(2, 1));
        dealerPanel.add(dealerHandLabel);
        add(dealerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // tells gamestate that it's the first player's turn by default
        gameState.getPlayers().get(currentPlayerIndex).isTurn = true; 

        // Processes player's actions hit or stand
        hitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleHit();
            }
        });
        standButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleStand();
            }
        });

        // Set up the initial game state
        currentPlayerIndex = 0;
        updateUI();

        // Set JFrame properties
        setTitle("Blackjack Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
        setVisible(true);
    }

    private void handlePlayerAction(String action) {
        // Implement logic for the specified player action for the current player
        // Update the UI
        gameState.getPlayers().get(currentPlayerIndex).isTurn = true; 
        updateUI();
        gameState.playerTurn(currentPlayerIndex, action);
        updateUI();
        

        // Move to the next player if all players haven't taken their turn
        if (gameState.getPlayers().get(currentPlayerIndex).getStatus() != Status.READY && 
            currentPlayerIndex < gameState.getPlayers().size() - 1) {            
                gameState.getPlayers().get(currentPlayerIndex).isTurn = false;           
                currentPlayerIndex++;   
                while (gameState.getPlayers().get(currentPlayerIndex).getBalance() <= 0)
                {
                    if (gameState.getPlayers().get(currentPlayerIndex).getBalance() <= 0)
                    {
                        currentPlayerIndex++;
                    }
                }
                gameState.getPlayers().get(currentPlayerIndex).isTurn = true;    
                updateUI();    
                           

        } if (gameState.getPlayers().get(currentPlayerIndex).isTurn == false && 
             gameState.getPlayers().get(currentPlayerIndex).isLast == true) {
                prebet = true;
                updateUI();
                prebet = false;

            // All players have taken their turn, move to the dealer's turn (not working)
            // Reset to the first player for the next round
            currentPlayerIndex = 0;
            for (Player player : gameState.getPlayers())
            {
                player.isTurn = false;
                if (player.getBalance() <= 0)
                {
                    player.stillIn = false;
                }
            }
            dealerHandLabel.setText("Dealer hand was: " + dealersLastValue);    
            getBets(gameState.getPlayers().size());  
            while (gameState.getPlayers().get(currentPlayerIndex).getBalance() <= 0)
            {
                if (gameState.getPlayers().get(currentPlayerIndex).getBalance() <= 0)
                {
                    currentPlayerIndex++;
                }
            }
            updateUI();          
        }
    }

    private void getBets(int numPlayers) {
        boolean validgame = false;
        String invalid = "";
        for (int i = 0; i < numPlayers; i++)
        {
            String betString = "";
            if (gameState.getPlayers().get(i).winner && gameState.getPlayers().get(i).getBalance() > 0)
            {                             
                betString = JOptionPane.showInputDialog(invalid + "Player " + (i + 1) + ", you won $" + (gameState.getPlayers().get(i).getBalance() - balCheck[i]) + ", enter your bet (you have: $" + 
                gameState.getPlayers().get(i).getBalance() + ")"); 
            }
            else if (gameState.getPlayers().get(i).getBalance() > 0)
            {
                betString = JOptionPane.showInputDialog(invalid + "Player " + (i + 1) + ", enter your bet (you have: $" + gameState.getPlayers().get(i).getBalance() + ")");
            }
            invalid = "";
            double numbet;
            if (gameState.getPlayers().get(i).getBalance() > 0)
            {
                try {
                numbet = Double.parseDouble(betString);
                gameState.getPlayers().get(i).updateBet(numbet);
                validgame = true;
                if (numbet > gameState.getPlayers().get(i).getBalance())
                {
                    invalid = "Bet cannot be more than current balance. ";
                    validgame = false;
                    gameState.getPlayers().get(i).updateBet(0);
                    i--;
                }
                else if (numbet == 0)
                {
                    invalid = "Bet must be greater than 0. ";
                    validgame = false;
                    i--;
                }
            } catch (NumberFormatException e) {
                // Handle invalid input
                invalid = "Invalid input, try again. ";
                i--;
                validgame = false;
            }
                

            }
            
           
            if (validgame == true)
            {
                balCheck[i] = gameState.getPlayers().get(i).getBalance();
                gameState.getPlayers().get(i).winner = false;
            }            
        }
    }

    private void handleHit() {
        handlePlayerAction("hit");
    }

    private void handleStand() {
        handlePlayerAction("stand");
    }

    private void updateUI() {
        // Update the UI components based on the current game state
        // Display information for each player
        for (int i = 0; i < gameState.getPlayers().size(); i++) {     
            if (gameState.getPlayers().get(i).stillIn == true)
            {
                playerStatusLabel[i].setText("Status: " + gameState.getPlayers().get(i).getStatus());        
                playerHandLabels[i].setText("Player " + (i + 1) + " Hand: " + gameState.getPlayers().get(i).getPlayerHand() + " = " + gameState.getPlayers().get(i).handValue() + " | " +  
                "Bet: $" + gameState.getPlayers().get(i).getCurrentBet());                
            }
            
            else
            {
                playerStatusLabel[i].setText("Status: " + gameState.getPlayers().get(i).getStatus());     
                playerHandLabels[i].setText("Player " + (i + 1) + " is out of the game.");
            }
            if (prebet == true)
            {
                playerStatusLabel[i].setText("Betting phase active");
                playerHandLabels[i].setText("Betting phase active");
            }
        }
        playerStatusLabel[currentPlayerIndex].setText("Status: " + gameState.getPlayers().get(currentPlayerIndex).getStatus() + " (currently their turn)");
        if (currentPlayerIndex > 0)
        {
            playerStatusLabel[currentPlayerIndex - 1].setText("Status: " + gameState.getPlayers().get(currentPlayerIndex - 1).getStatus()); 
        }  
        if (prebet == true)
            {
                playerStatusLabel[currentPlayerIndex - 1].setText("Betting phase active");                
            }
    


        // Display information for the dealer
        String dealerHand = gameState.getDealer().getPlayerHand().toString();
        String[] dealerHand2;
        System.out.println(dealerHand);
        dealerHand2 = dealerHand.split(",");
        dealerHand2[0] = dealerHand2[0] + "]";
        System.out.println(dealerHand2[0]);
        dealerHandLabel.setText("Dealer Hand: " + dealerHand2[0]);
        if (prebet == true)
        {
            dealerHandLabel.setText("Betting phase active");
        }

        // Display information for the last player
        int lastPlayerIndex = gameState.getPlayers().size() - 1;
        Player lastPlayer = gameState.getPlayers().get(lastPlayerIndex);
        if (gameState.getPlayers().get(lastPlayerIndex).isTurn == true)
        {
            playerStatusLabel[lastPlayerIndex].setText("Status: " + lastPlayer.getStatus() + " (currently their turn)");

        }
        else 
        {
            playerStatusLabel[lastPlayerIndex].setText("Status: " + lastPlayer.getStatus()); 
        }        
        playerHandLabels[lastPlayerIndex].setText("Player " + (lastPlayerIndex + 1) + " Hand: " + lastPlayer.getPlayerHand() + " = " + 
        gameState.getPlayers().get(lastPlayerIndex).handValue() + " | " +" Bet: $" + lastPlayer.getCurrentBet());
        if (prebet == true)
            {
                playerStatusLabel[lastPlayerIndex].setText("Betting phase active");
                playerHandLabels[lastPlayerIndex].setText("Betting phase active");
            }
        dealersLastValue = gameState.dealersValue;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new BlackjackUI();
            }
        });
    }
}
