package saboteur.ai;
import saboteur.model.Game;
import saboteur.model.Operation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import saboteur.model.OperationActionCardToBoard;
import saboteur.model.OperationActionCardToPlayer;
import saboteur.model.OperationPathCard;
import saboteur.model.Player;
import saboteur.model.Position;
import saboteur.model.Card.Card;
import saboteur.model.Card.PathCard;

public abstract class AI extends Player {
	
	protected final int AVERAGE_TRUST = 50;
	
	protected Map<Player,Float> isDwarf;	
	protected Difficulty difficulty;
	protected Map<Position,Float> estimatedGoldCardPosition;
	protected Map<Operation, Float> operationsWeight;
	

	public AI(Game game, String name) {
		super(game, name);
		isDwarf = new HashMap<Player,Float>();
		this.difficulty = Difficulty.EASY;
		this.operationsWeight = new HashMap<Operation, Float>();
		this.estimatedGoldCardPosition = new HashMap<Position, Float>();
	}
	
	public void initializeAI(){
		//Initialize estimated gold card position
		for (Position position : game.getBoard().getGoldCards()){
			this.estimatedGoldCardPosition.put(position, 1f/3f);
		}
		//Initialize trust
		for(Player p : game.getPlayerList()){
			isDwarf.put(p, (float) AVERAGE_TRUST);
		}
		if(isSaboteur()){
			isDwarf.put(this, (float) -1073741824);
		}
		else{
			isDwarf.put(this, (float) 1073741824);
		}
	}

	@Override
	public void notify(Operation o){
		switch(o.getClass().getName()){
		case "saboteur.model.Operation.OperationActionCardToBoard":
			updateTrust((OperationActionCardToBoard) o);
			break;
		case "saboteur.model.Operation.OperationActionCardToPlayer":
			updateTrust((OperationActionCardToPlayer) o);
			break;
		case "saboteur.model.Operation.OperationPathCard":
			updateTrust((OperationPathCard) o);
			break;
		default:
			System.err.println("Opération non reconnue");
		}
	}

	// Collapse card
	public void updateTrust(OperationActionCardToBoard o){
		switch(o.getCard().getClass().getName()){
		case "saboteur.model.Card.PlanCard":
			// Nothing to update
			break;
		case "saboteur.model.Card.CollapseCard":
			if(((PathCard) o.getCard()).isCulDeSac()){
				isDwarf.put(o.getSourcePlayer(), isDwarf.get(o.getSourcePlayer()) + 20);
			}
			else{
				isDwarf.put(o.getSourcePlayer(), isDwarf.get(o.getSourcePlayer()) - 20);
			}
			break;
		default:
			System.err.println("Operation ActionCardToBoard undetected");
		}
	}
	
	// Sabotage & Rescue card
	public void updateTrust(OperationActionCardToPlayer o){
		switch(o.getCard().getClassName()){
		case "saboteur.model.Card.SobotageCard":
			if(isDwarf.get(o.getSourcePlayer()) > isDwarf.get(o.getDestinationPlayer()) && (isDwarf.get(o.getDestinationPlayer()) <= 40)){
				// Ennemies of our ennemies are our allies
				isDwarf.put(o.getSourcePlayer(), isDwarf.get(o.getSourcePlayer()) + 10);
			}
			else if(isDwarf.get(o.getSourcePlayer()) <= isDwarf.get(o.getDestinationPlayer()) && (isDwarf.get(o.getDestinationPlayer()) >= 60)){
				// Ennemies of our allies are our ennemies
				isDwarf.put(o.getSourcePlayer(), isDwarf.get(o.getSourcePlayer()) - 10);
			}
			break;
		case "saboteur.model.Card.RescueCard":
		case "saboteur.model.Card.DoubleRescueCard":
			if(!o.getSourcePlayer().equals(o.getDestinationPlayer())){
				if(isDwarf.get(o.getDestinationPlayer()) <= 40){
					// Allies of our ennemies are our ennemies
					isDwarf.put(o.getSourcePlayer(), isDwarf.get(o.getSourcePlayer()) - 10);
				}
				else if(isDwarf.get(o.getDestinationPlayer()) >= 60){
					// Allies of our allies are our allies
					isDwarf.put(o.getSourcePlayer(), isDwarf.get(o.getSourcePlayer()) + 10);
				}
			}
			break;
		default:
			System.err.println("Operation ActionCarToPlayer undetected");
		}
	}
	
	// Path card
	public void updateTrust(OperationPathCard o){
		int taxiDistance = o.getP().getTaxiDistance(getEstimatedGoldCardPosition());
		int neighborsAmount = this.getGame().getBoard().numberOfNeighbors(o.getP());
		
		if(((PathCard) o.getCard()).isCulDeSac()){ 
			// The closer the gold card, the heavier is the card.
			// The more neighbors, the merr... heavier is the card.
			isDwarf.put(o.getSourcePlayer(), (float) (isDwarf.get(o.getSourcePlayer()) - (40/(Math.pow(2, taxiDistance)))*(0.75+(neighborsAmount/4)) - 2));
		}
		else{
			isDwarf.put(o.getSourcePlayer(), (float) (isDwarf.get(o.getSourcePlayer()) + 40/(Math.pow(2, taxiDistance)) + 3));
		}
	}
	
	public AI setDifficulty(Difficulty difficulty){
		this.difficulty = difficulty;
		return this;
	}
	
	public Difficulty getDifficulty(){
		return difficulty;
	}
	
	public Position getEstimatedGoldCardPosition(){
		float max = -28091994;
		LinkedList<Position> equiprobableGoldCardPosition = new LinkedList<Position>();
		Random r = new Random(getGame().getSeed());
		
		for(Position p : estimatedGoldCardPosition.keySet()){
			if(estimatedGoldCardPosition.get(p) > max){
				max = estimatedGoldCardPosition.get(p);
			}
		}
		for(Position p : estimatedGoldCardPosition.keySet()){
			if(estimatedGoldCardPosition.get(p) == max){
				equiprobableGoldCardPosition.add(p);
			}
		}
		return equiprobableGoldCardPosition.get(r.nextInt(equiprobableGoldCardPosition.size()));
	}
	
	public void changeEstimatedGoldCardPosition(Position p, Boolean b){
		if(b){
			this.estimatedGoldCardPosition.put(p, 1f);
			this.setAllEstimatedGoldCardPositionExept(p, 0f);
		}else{
			this.estimatedGoldCardPosition.put(p, 0f);
		}
	}
	
	public void setAllEstimatedGoldCardPositionExept(Position position, Float probability){
		for(Position p : this.estimatedGoldCardPosition.keySet()){
			if(!p.equals(position)){
				this.estimatedGoldCardPosition.put(p, probability);
			}
		}
	}
	
	public void setAllEstimatedGoldCardPositionExept(List<Position> positions, Float probability){
		for(Position position : positions){
			this.setAllEstimatedGoldCardPositionExept(position, probability);
		}
	}
	
	public Position getMostProbableGoldCard(){
		Position[] positions = (Position[]) this.estimatedGoldCardPosition.values().toArray();
		Arrays.sort(positions);
		return positions[0];
	}
	
	public boolean knowsTheGoldCardPosition(){
		return this.estimatedGoldCardPosition.containsValue(1f);
	}
	
	@Override
	public void viewGoalCard(PathCard card){
		changeEstimatedGoldCardPosition(getGame().getBoard().getPosition(card), card.hasGold());
	}
	
	protected void resetProbabilitiesToPlayEachOperation(){
		operationsWeight.clear();
		for(Card c : getHand()){
			System.out.println("name = " +c.getClassName());
			switch(c.getClassName()){
			case "saboteur.model.Card.PathCard" :
				operationsWeight.put(new OperationPathCard(this, c, null), 0f);
				break;
			case "saboteur.model.Card.CollapseCard" :
			case "saboteur.model.Card.PlanCard" :
				operationsWeight.put(new OperationActionCardToBoard(this, c, null), 0f);
				break;
			case "saboteur.model.Card.RescueCard":
			case "saboteur.model.Card.DoubleRescueCard":
			case "saboteur.model.Card.SabotageCard":
				operationsWeight.put(new OperationActionCardToPlayer(this, c, null), 0f);
				break;
			}
		}
	}
	
	protected void removeOperationWithNullTarget(){
		Map<Operation, Float> cloneOperationsWeight = new HashMap<Operation,Float>(operationsWeight);
		for(Operation o : cloneOperationsWeight.keySet()){
			if(o.getClass().getName() != "saboteur.model.OperationTrash"){
				switch(o.getCard().getClassName()){
				case "saboteur.model.Card.PathCard" :
					if(((OperationPathCard) o).getP() == null)
						operationsWeight.remove((OperationPathCard) o);
					break;
				case "saboteur.model.Card.CollapseCard" :
				case "saboteur.model.Card.PlanCard" :
					if(((OperationActionCardToBoard) o).getDestinationCard() == null)
						operationsWeight.remove((OperationActionCardToBoard) o);
					break;
				case "saboteur.model.Card.RescueCard":
				case "saboteur.model.Card.DoubleRescueCard":
				case "saboteur.model.Card.SabotageCard":
					if(((OperationActionCardToPlayer) o).getDestinationPlayer() == null)
						operationsWeight.remove((OperationActionCardToPlayer) o);
					break;
				}
			}
		}
	}
	
	//TODO move this method somewhere
	public float positiveOrZero(float i){
		if(i>0){
			return i;
		}
		return 0;
	}
	
	//TODO move this method somewhere
	public float ifNegativeZeroElseOne(float i){
		if(i<=0){
			return 0;
		}
		else{
			return 1;
		}
	}
	
	protected Player mostLikelyADwarf(){
		float maxTrust=-1073741824;
		Player mostTrustfulPlayer = null;
		for(Player p : isDwarf.keySet()){
			if(isDwarf.get(p) > maxTrust && p != this){
				maxTrust = isDwarf.get(p);
				mostTrustfulPlayer = p;
			}
		}
		return mostTrustfulPlayer;
	}
	
	protected Player mostLikelyASaboteur(){
		float leastTrust=1073741824;
		Player leastTrustfulPlayer = null;
		for(Player p : isDwarf.keySet()){
			if(isDwarf.get(p) < leastTrust && p != this){
				leastTrust = isDwarf.get(p);
				leastTrustfulPlayer = p;
			}
		}
		return leastTrustfulPlayer;
	}

	public boolean isAI(){
		return true;
	}
	
	protected Operation bestOperationToPlay(){
		float max = -435365;
		LinkedList<Operation> bestOperations = new LinkedList<Operation>();
		Random r = new Random(getGame().getSeed());
		
		for(Operation o : this.operationsWeight.keySet()){
			if(this.operationsWeight.get(o) > max){
				max = this.operationsWeight.get(o);
			}
		}
		for(Operation o : this.operationsWeight.keySet()){
			if(this.operationsWeight.get(o) == max){
				bestOperations.add(o);
			}
		}
		System.out.println(hand.size());
		return bestOperations.get(r.nextInt(bestOperations.size()));
	}
	
	@Override
	public void playCard(){
		Operation o = selectOperation();
		System.out.println("AI " + this.name +" turn " + getGame().getTurn() + " played operation " + o.getClass().getName() + " with card + "+ o.getCard().getClassName());
		this.getGame().playOperation(o);
	}
	
	public Operation selectOperation(){
		resetProbabilitiesToPlayEachOperation();
		switch(this.getDifficulty()){
		case EASY:
			computeOperationWeightEasyAI();
			break;
		case MEDIUM:
			computeOperationWeightMediumAI();
			break;
		case HARD:
			computeOperationWeightHardAI();
			break;
		}
		removeOperationWithNullTarget();
		
		return bestOperationToPlay();
	}

	protected abstract void computeOperationWeightHardAI();

	protected abstract void computeOperationWeightMediumAI();

	protected abstract void computeOperationWeightEasyAI();
}
