package blackjack;

/*
representation of a card from a standard deck which contains
a number: 2-10, ace, king, queen, jack.
and one of four suits: spades, clubs, hearts, and diamonds.

responsiblities: getters for number, and suit of a card,
getter for the "value" of the card(for example the value of a jack is 10)
 */
public class Card {

    private final Suit suit;
    private final Face face;

    private Card(Suit suit, Face face) {
        this.suit = suit;
        this.face = face;
    }

    public static Card of(Suit suit, Face face) {return new Card(suit, face); }


    //getters
    public Suit getSuit() { return this.suit; }
    public Face getFace() { return this.face; }

    /*
    returns the int value based on the face of a card 2-10 for the cards 2-10, and 10 for
    jack and above. Throws an exception if given an ace card
     */
    public int getValue() {
        if (this.face.ordinal() <= 9 && this.face.ordinal() > 0) {
            return this.face.ordinal()+1;
        }
        if(this.face.ordinal() >= 10) {
            return 10;
        }
        throw new IllegalArgumentException("Cannot Get value of Ace");
    }
    /*
    following to methods return the string of the corresponding face and suit of the card
     */
    private String faceString() {
        switch (this.face) {
            case ACE -> {
                return "ACE";
            }
            case TWO -> {
                return "TWO";
            }
            case THREE -> {
                return "THREE";
            }
            case FOUR -> {
                return "FOUR";
            }
            case FIVE -> {
                return "FIVE";
            }
            case SIX -> {
                return "SIX";
            }
            case SEVEN -> {
                return "SEVEN";
            }
            case EIGHT -> {
                return "EIGHT";
            }
            case NINE -> {
                return "NINE";
            }
            case TEN -> {
                return "TEN";
            }
            case JACK -> {
                return "JACK";
            }
            case QUEEN -> {
                return "QUEEN";
            }
            case KING -> {
                return "KING";
            }
        }
        throw new IllegalArgumentException("Not a valid face");
    }
    private String suitString() {
        switch (this.suit) {
            case SPADES -> {
                return "SPADES";
            }
            case CLUBS -> {
                return "CLUBS";
            }
            case HEARTS -> {
                return "HEARTS";
            }
            case DIAMONDS -> {
                return "DIAMONDS";
            }
        }
        throw new IllegalArgumentException("Not a valid suit");
    }

    /*
    returns a string representation of the card, face then suit seperated by a space.
     */
    @Override
    public String toString() {
        return faceString() + " " + suitString();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Card otherCard) {
            return(this.suit == otherCard.suit && this.face == otherCard.face);
        } else {
            return false;
        }
    }
}
