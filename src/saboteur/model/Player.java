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
	
	/**
	 * Play a RescueCard or a SabotageCard on a player
	 * This method remove the selected card from the player's hand
	 * Before, the selectedCard must be update in the source player
	 * @param destinationPlayer
	 * @return the operation executed
	 */
	public Operation playCard(Player destinationPlayer){
		Operation op = new OperationActionCardToPlayer(this, this.selectedCard, destinationPlayer);
		this.game.playOperation(op);
		return op;
	}
	/**
	 * Play a DoubleRescueCard on a player
	 * This method remove the selected card from the player's hand
	 * Before, the selectedCard must be update in the source player
	 * @param destinationPlayer
	 * @param destinationTool
	 * @return the operation executed
	 */
	public Operation playCard(Player destinationPlayer, Tool destinationTool){
		Operation op = new OperationActionCardToPlayer(this, this.selectedCard, destinationPlayer, destinationTool);
		this.game.playOperation(op);
		return op;
	}
	/**
	 * Play a a PlanCard or a Collapse Card
	 * This method remove the selected card from the player's hand
	 * Before, the selectedCard must be update in the source player
	 * @param destinationCard
	 * @return the operation executed
	 */
	public Operation playCard(PathCard destinationCard){
		Operation op = new OperationActionCardToBoard(this, this.selectedCard, destinationCard);
		this.game.playOperation(op);
		return op;
	}
	/**
	 * Play a PathCard
	 * This method remove the selected card from the player's hand
	 * Before, the selectedCard must be update in the source player
	 * @param position
	 * @return the operation executed
	 */
	public Operation playCard(Position position){
		OperationPathCard o = new OperationPathCard(this, this.selectedCard, position);
		if(!this.game.getBoard().isPossible((PathCard)this.selectedCard, position)){
			o.setReversed(true);
		}
		this.game.playOperation(o);
		return o;
	}
	/**
	 * Trash a card
	 * This method remove the selected card from the player's hand
	 * Before, the selectedCard must be update in the source player
	 * @return the operation executed
	 */
	public Operation playCard(){		
		Operation op = new OperationTrash(this, this.selectedCard);
		this.game.playOperation(op);
		return op;
	}
	/**
	 * Pick a card
	 * @return the operation executed
	 */
	public Operation pickCard(){
		Operation op = null;
		if (!game.stackIsEmpty()){
			op = new OperationPick(this, game.pick());
			this.game.playOperation(op);
			return op;
		}	
		return null;
	}
	
	
/* Callbacks */
	
	//Human : useless
	//IA : redefinition
	public void viewGoalCard(PathCard card){
	}
		
	public void notify(Operation o){	
	}
	
	
/* Tests. No side effects */
	/**
	 * Know if we can apply the RescueCard to this player
	 * @param card
	 * @return
	 */
	public boolean canRescueItself(RescueCard card){
		return this.canRescue(card, this);
	}
	/**
	 * Know if we can apply a RescueCard to a player
	 * @param card
	 * @param player
	 * @return
	 */
	public boolean canRescue(RescueCard card, Player player){
		for (SabotageCard sabotageCard : player.handicaps){
			if (sabotageCard.getSabotageType() == card.getRescueType()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Know if we can apply the DoubleRescueCard to this player
	 * @param card
	 * @return
	 */
	public boolean canRescueWithDoubleRescueCard(DoubleRescueCard card){
		return this.canRescueWithDoubleRescueCard(card, this);
	}
	/**
	 * Know if we can apply a DoubleRescueCard to a player
	 * @param card
	 * @param player
	 * @return
	 */
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
	
	/**
	 * Know if we can apply the SabotageCard to a player
	 * @param card
	 * @param p
	 * @return
	 */
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

	
/* Getters LinkedHashSetters Modifiers */
	
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
	
	/**
	 * @param tool
	 * @return null if not found
	 */
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
	
	/**
	 * @return Total gold (and not the list of its GoldCards
	 */
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
}
