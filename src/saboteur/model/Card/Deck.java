package saboteur.model.Card;

import java.util.ArrayList;
import java.util.LinkedList;

public class Deck {

    private ArrayList<PathCard> startPathCard;
    private ArrayList<PathCard> goalPathCards;
    private LinkedList<GoldCard> goldCards;
    private LinkedList<Card> otherCards;

    public Deck(){

    }

    public void setOtherCards(LinkedList<Card> otherCards){
        this.otherCards = otherCards;
    }

    public void setStartPathCard(ArrayList<PathCard> startPathCard){
        this.startPathCard = startPathCard;
    }

    public void setGoalPathCards(ArrayList<PathCard> goalPathCards) {
        this.goalPathCards = goalPathCards;
    }

    public void setGoldCards(LinkedList<GoldCard> goldCards) {
        this.goldCards = goldCards;
    }

    public LinkedList<Card> getOtherCards() {
        return new LinkedList<Card>(otherCards);
    }

    public ArrayList<PathCard> getStartPathCard() {
        return new ArrayList<PathCard>(startPathCard);
    }

    public ArrayList<PathCard> getGoalPathCards() {
        return new ArrayList<PathCard>(goalPathCards);
    }

    public LinkedList<GoldCard> getGoldCards() {
        return new LinkedList<GoldCard>(goldCards);
    }
}
