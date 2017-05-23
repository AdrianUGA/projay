package saboteur.model;

import java.io.Serializable;
import java.util.ArrayList;

import saboteur.model.Card.*;

public abstract class Player implements Serializable {

	private static final long serialVersionUID = -7234436527409826249L;
	protected Card selectedCard;
	protected String name;
	protected ArrayList<SabotageCard> handicaps;
	protected ArrayList<GoldCard> gold;
	protected ArrayList<Card> hand;
	transient protected Game game;
	protected Team team;
	


	public Player (Game game, String name){
		this.game = game;
		game.register(this);
		this.handicaps = new ArrayList<SabotageCard>();
		this.hand = new ArrayList<>();
		this.name = name;
		this.gold = new ArrayList<GoldCard>();
	}
	
	
/* Actions */
	
	public void playCard(Card card){
	}
	public void playCard(Player destinationPlayer){		
		this.game.playOperation(new OperationActionCardToPlayer(this, this.selectedCard, destinationPlayer));
	}
	public void playCard(Player destinationPlayer, Tool destinationTool){		
		this.game.playOperation(new OperationActionCardToPlayer(this, this.selectedCard, destinationPlayer, destinationTool));
	}
	public void playCard(PathCard destinationCard){
		this.game.playOperation(new OperationActionCardToBoard(this, this.selectedCard, destinationCard));
	}
	public void playCard(Position position){
		OperationPathCard o = new OperationPathCard(this, this.selectedCard, position);
		if(!this.game.getBoard().isPossible((PathCard)this.selectedCard, position)){
			o.setReversed(true);
		}
		this.game.playOperation(o);
	}
	
	public void playCard(){		
		this.game.playOperation(new OperationTrash(this, this.selectedCard));
	}
	
	public void pickCard(){
		if (!game.stackIsEmpty())			
			this.game.playOperation(new OperationPick(this, game.pick()));
	}
	
	
/* Callbacks */
	
	//Human : useless
	//IA : redefinition
	public void viewGoalCard(PathCard card){
	}
		
	public void notify(Operation o){	
	}
	
	
/* Tests. No side effects */
	
	public boolean canRescueItself(RescueCard card){
		return this.canRescue(card, this);
	}
	
	public boolean canRescue(RescueCard card, Player player){
		for (SabotageCard sabotageCard : player.handicaps){
			if (sabotageCard.getSabotageType() == card.getRescueType()){
				return true;
			}
		}
		return false;
	}
	
	public boolean canRescueWithDoubleRescueCard(DoubleRescueCard card){
		return this.canRescueWithDoubleRescueCard(card, this);
	}
	
	public boolean canRescueWithDoubleRescueCard(DoubleRescueCard card, Player player) {
		for (SabotageCard sabotageCard : player.handicaps){
			Tool currentType = sabotageCard.getSabotageType();
			if (currentType == card.getRescueType1() || currentType == card.getRescueType2()){
				return true;
			}
		}
		return false;
	}
	
	public boolean canRescueType(Tool rescueType) {
		return this.canRescueType(rescueType, this);
	}
	
	public boolean canRescueType(Tool rescueType, Player player) {
		for (SabotageCard sabotageCard : player.handicaps){
			Tool currentType = sabotageCard.getSabotageType();
			if (currentType == rescueType){
				return true;
			}
		}
		return false;
	}
	
	public boolean canHandicap(SabotageCard card, Player p){
		for (SabotageCard sabotageCard : p.handicaps){
			if (sabotageCard.getSabotageType() == card.getSabotageType()){
				return false;
			}
		}
		return true;
	}
	

	public boolean emptyHand(){
		return this.hand.isEmpty();
	}
	
	public boolean isHuman(){
		return false;
	}

	public boolean isAI(){
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		Player p = (Player) obj;
		return p.getName() == this.name
				&& p.getGold() == this.getGold()
				&& p.getHand().equals(this.hand)
				&& p.getHandicaps().equals(this.handicaps);
	}

	
/* Getters Setters Modifiers */
	
	public Card getSelectedCard() {
		return selectedCard;
	}

	public void setSelectedCard(Card selectedCard) {
		this.selectedCard = selectedCard;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public void setHandicaps(ArrayList<SabotageCard> handicaps) {
		this.handicaps = handicaps;
	}

	public void setGold(ArrayList<GoldCard> gold) {
		this.gold = gold;
	}

	public void setHand(ArrayList<Card> hand) {
		this.hand = hand;
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String nom) {
		this.name = nom;
	}	
	
	/* return null if not found */
		public SabotageCard getCardCorrespondingToRescueType(Tool tool){
			for (SabotageCard sabotageCard : this.handicaps){
				if (sabotageCard.getSabotageType() == tool){
					return sabotageCard;
				}
			}
			return null;
		}
		
	public void addGold(GoldCard goldCard){
		this.gold.add(goldCard);
	}
	
	public void setTeam(Team team){
		this.team = team;
	}
	
	public Team getTeam(){
		return this.team;
	}
	
	public ArrayList<SabotageCard> getHandicaps(){
		return this.handicaps;
	}
	
	public int getGold(){
		int total = 0;
		for (GoldCard card : this.gold){
			total += card.getValue();
		}
		return total;
	}
	
	public ArrayList<Card> getHand(){
		return this.hand;
	}
	
	public void removeHandCard(Card card){
		this.hand.remove(card);
	}
	
	public void removeHandicapCard(SabotageCard card){
		this.handicaps.remove(card);
	}
	
	public void addHandCard(Card card){
		this.hand.add(card);
	}
	
	public void addHandicapCard(SabotageCard card){
		this.handicaps.add(card);
	}
	
	public void removeGoldCard(GoldCard card){
		this.gold.remove(card);
	}
	
	public void resetHandicaps() {
		this.handicaps.clear();
	}

	public void removeHandicapWith(RescueCard card) {
		// TODO Auto-generated method stub
	}

}
