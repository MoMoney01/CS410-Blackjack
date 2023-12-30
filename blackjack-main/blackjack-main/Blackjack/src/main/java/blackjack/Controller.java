package blackjack;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

/*
Intermediate class that parses json files between front end and back end following json structures specified in
milestone 2.
Input json file from front end has "gameid" and "playerid" while sending the following to GameState:
    int playerNum, String phase, int playerIndex, int bet, String action
Output json file tracks "_revision" and the following data from GameState to update the state:
    String phase, Card[] dealerHand, int[] playerBalance, int[] playerBets,
    Card[Card[]] playerHand, int playerIndex
 */
public class Controller {
    // inputJson is parsed and has its data passed to GameState.java
    private JSONObject inputJson;
    // outputJson takes data from GameState to be brought to front end
    private JSONObject outputJson;
    private int revision;
    private int playerNum;
    private final GameState gs;

    // initializes a GameState using playerNum from json file input
    public Controller(String inputFile) throws IOException, ParseException {
        // receive json from server
        Object obj = new JSONParser().parse(new FileReader(inputFile));
        inputJson = (JSONObject) obj;
        outputJson = new JSONObject();
        revision = 0;
        playerNum = Integer.valueOf(String.valueOf(inputJson.get("playerNum")));
        gs = new GameState(playerNum);
        updateState(gs.startGame());
    }

    // feeds json file input into GameState and creates file json file output
    public void nextState(String inputFile) throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader(inputFile));
        inputJson = (JSONObject) obj;
        updateState(gs.receiveInfo(this.toGameState()));
    }

    // using given input json file, provide map of data from file to GameState
    public HashMap toGameState() {
        HashMap<String, String> inputData = new HashMap();
        inputData.put("phase", (String) inputJson.get("phase"));
        inputData.put("playerIndex", (String) inputJson.get("playerIndex"));
        // need to cast bet to int in GameState
        inputData.put("bet", (String) inputJson.get("bet"));
        inputData.put("action", (String) inputJson.get("action"));
        return inputData;
    }

    // updates output Json with data from GameState
    public void updateState(HashMap<String, String> update) throws FileNotFoundException {
        outputJson.put("phase", update.get("phase"));
        if (update.get("dealerHand") != null) {
            JSONArray ja = new JSONArray();
            String[] cards = (update.get("dealerHand")).split(",");
            for (String c : cards) {
                ja.add(c);
            }
            outputJson.put("dealerHand", ja);
        } else {
            outputJson.put("dealerHand", "notDef");
        }
        JSONArray jaBalances = new JSONArray();
        String[] balances = (update.get("playerBalance")).split(",");
        for (String b : balances) {
            jaBalances.add(b);
        }
        outputJson.put("playerBalance", jaBalances);
        if (update.get("playerBets") != null) {
            JSONArray jaBets = new JSONArray();
            String[] bets = (update.get("playerBets")).split(",");
            for (String bet : bets) {
                if (bet == null) {
                    jaBets.add("-1");
                } else {
                    jaBets.add(bet);
                }
            }
            outputJson.put("playerBets", jaBets);
        } else {
            outputJson.put("playerBets", "notDef");
        }
        outputJson.put("playerIndex", update.get("playerIndex"));
        if (update.get("playerHand") != null) {
            JSONArray jaHands = new JSONArray();
            String[] hands = (update.get("dealerHand")).split("/");
            for (String hand : hands) {
                String[] cards = hand.split(",");
                jaHands.add(cards);
            }
            outputJson.put("playerHand", jaHands);
        }
        outputJson.put("_revision", ++revision);

        PrintWriter pw = new PrintWriter("toFrontEnd.json");
        pw.write(outputJson.toJSONString());
        pw.flush();
        pw.close();
        // send outputJSON to server
    }

    public void printOutputJson() {
        System.out.println(outputJson.toJSONString());
    }
}
