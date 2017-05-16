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

    public LinkedList<Card> getCopyOtherCards() {
        LinkedList<Card> newOtherCards = new LinkedList<>();
        for(Card card: otherCards){
            newOtherCards.add(card.clone());
        }
        return newOtherCards;
    }

    public LinkedList<Card> getOtherCards(){
        return new LinkedList<>(otherCards);
    }

    public ArrayList<PathCard> getCopyStartPathCard() {
        ArrayList<PathCard> newStartPathCard = new ArrayList<>();
        for(PathCard card: startPathCard){
            newStartPathCard.add(card.clone());
        }
        return newStartPathCard;
    }

    public ArrayList<PathCard> getStartPathCard() {
        return new ArrayList<>(startPathCard);
    }

    public ArrayList<PathCard> getCopyGoalPathCards() {
        ArrayList<PathCard> newGoalPathCards = new ArrayList<>();
        for(PathCard card: goalPathCards){
            newGoalPathCards.add(card.clone());
        }
        return newGoalPathCards;
    }

    public ArrayList<PathCard> getGoalPathCards() {
        return new ArrayList<>(goalPathCards);
    }

    public LinkedList<GoldCard> getCopyGoldCards() {
        LinkedList<GoldCard> newGoldCards = new LinkedList<>();
        for(GoldCard card: goldCards){
            newGoldCards.add(card.clone());
        }
        return newGoldCards;
    }

    public LinkedList<GoldCard> getGoldCards() {
        return new LinkedList<>(goldCards);
    }
}
