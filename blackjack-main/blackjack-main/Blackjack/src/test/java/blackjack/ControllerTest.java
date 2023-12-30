package blackjack;

import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    @Test
    void setUpGame() throws IOException, ParseException {
        // check "toFrontEnd.json" for results
        Controller blackjack = new Controller("StartGame.json");
    }
}