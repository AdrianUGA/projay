package saboteur.ai;
import saboteur.model.Game;
import saboteur.model.Operation;

import java.util.HashMap;
import java.util.Map;

import saboteur.model.OperationActionCardToBoard;
import saboteur.model.OperationActionCardToPlayer;
import saboteur.model.OperationPathCard;
import saboteur.model.Player;
import saboteur.model.Position;
import saboteur.model.Card.PathCard;

public abstract class AI extends Player {
	
	private Map<Player,Float> trust;	
	private Difficulty difficulty;
	private Map<Position,Boolean> estimatedGoldCardPosition;
	

	public AI(Game game) {
		super(game);
		trust = new HashMap<Player,Float>();
		for(Player p : game.getPlayerList()){
			trust.put(p, (float) 50);
		}
		if(isSaboteur()){
			trust.put(this, (float) -1073741824);
		}
		else{
			trust.put(this, (float) 1073741824);
		}
		estimatedGoldCardPosition = new HashMap<Position,Boolean>();
		estimatedGoldCardPosition.put(new Position(0,0), true);
		estimatedGoldCardPosition.put(new Position(1,1), true);
		estimatedGoldCardPosition.put(new Position(2,2), true);
		
		// TODO change goldCardPosition to middle of the 3 hidden cards
	}
	
	@Override
	public void notify(Operation o){
		switch(o.getClass().getName()){
		case "OperationActionCardToBoard":
			updateTrust((OperationActionCardToBoard) o);
			break;
		case "OperationActionCardToPlayer":
			updateTrust((OperationActionCardToPlayer) o);
			break;
		case "OperationPathCard":
			updateTrust((OperationPathCard) o);
			break;
		default:
			System.err.println("Opération non reconnue");
		}
	}
	
	// Collapse card
	public void updateTrust(OperationActionCardToBoard o){
		switch(o.getCard().getClass().getName()){
		case "PlanCard":
			// Nothing to update
			break;
		case "CollapseCard":
			if(((PathCard) o.getCard()).isCulDeSac()){
				trust.put(o.getSourcePlayer(), trust.get(o.getSourcePlayer()) + 20);
			}
			else{
				trust.put(o.getSourcePlayer(), trust.get(o.getSourcePlayer()) - 20);
			}
			break;
		default:
			System.err.println("Operation ActionCardToBoard undetected");
		}
	}
	
	// Sabotage & Rescue card
	public void updateTrust(OperationActionCardToPlayer o){
		switch(o.getCard().getClass().getName()){
		case "SobotageCard":
			if(trust.get(o.getSourcePlayer()) > trust.get(o.getDestinationPlayer()) && (trust.get(o.getDestinationPlayer()) <= 40)){
				// Ennemies of our ennemies are our allies
				trust.put(o.getSourcePlayer(), trust.get(o.getSourcePlayer()) + 10);
			}
			else if(trust.get(o.getSourcePlayer()) <= trust.get(o.getDestinationPlayer()) && (trust.get(o.getDestinationPlayer()) >= 60)){
				// Ennemies of our allies are our ennemies
				trust.put(o.getSourcePlayer(), trust.get(o.getSourcePlayer()) - 10);
			}
			break;
		case "RescueCard":
		case "DoubleRescueCard":
			if(!o.getSourcePlayer().equals(o.getDestinationPlayer())){
				if(trust.get(o.getDestinationPlayer()) <= 40){
					// Allies of our ennemies are our ennemies
					trust.put(o.getSourcePlayer(), trust.get(o.getSourcePlayer()) - 10);
				}
				else if(trust.get(o.getDestinationPlayer()) >= 60){
					// Allies of our allies are our allies
					trust.put(o.getSourcePlayer(), trust.get(o.getSourcePlayer()) + 10);
				}
			}
			break;
		default:
			System.err.println("Operation ActionCarToPlayer undetected");
		}
	}
	
	// Path card
	public void updateTrust(OperationPathCard o){
		int taxiDistance = this.getGame().getBoard().getTaxiDistance(o.getP(), getEstimatedGoldCardPosition());
		int neighborsAmount = this.getGame().getBoard().numberOfNeighbors(o.getP());
		
		if(((PathCard) o.getCard()).isCulDeSac()){ 
			// The closer the gold card, the heavier is the card.
			// The more neighbors, the merr... heavier is the card.
			trust.put(o.getSourcePlayer(), (float) (trust.get(o.getSourcePlayer()) - (40/(Math.pow(2, taxiDistance)))*(0.75+(neighborsAmount/4)) - 2));
		}
		else{
			trust.put(o.getSourcePlayer(), (float) (trust.get(o.getSourcePlayer()) + 40/(Math.pow(2, taxiDistance)) + 3));
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
		//TODO changer les positions par celles des cartes "objectif"
		if(estimatedGoldCardPosition.get(new Position(0,0))){
			return new Position(0,0);
		}
		if(estimatedGoldCardPosition.get(new Position(1,1))){
			return new Position(1,1);
		}
		return new Position(2,2);
	}
	
	public void changeEstimatedGoldCardPosition(Position p, Boolean b){
		//TODO changer les positions par celles des cartes "objectif"
		if(!b){
			this.estimatedGoldCardPosition.put(p, b);
		}else{
			this.estimatedGoldCardPosition.put(new Position(0,0), false);
			this.estimatedGoldCardPosition.put(new Position(1,1), false);
			this.estimatedGoldCardPosition.put(new Position(2,2), false);
			this.estimatedGoldCardPosition.put(p, b);
		}
	}
	
	public boolean knowsTheGoldCardPosition(){
		//TODO changer les positions par celles des cartes "objectif"
		// If at least 2 positions could contains the gold card, the AI isn't sure of its real position.
		if((estimatedGoldCardPosition.get(new Position(0,0)) && estimatedGoldCardPosition.get(new Position(1,1)))
			|| (estimatedGoldCardPosition.get(new Position(0,0)) && estimatedGoldCardPosition.get(new Position(2,2)))
			|| (estimatedGoldCardPosition.get(new Position(1,1)) && estimatedGoldCardPosition.get(new Position(2,2))))
			return false;
		return true;
	}
	
	@Override
	public void viewGoalCard(PathCard card){
		changeEstimatedGoldCardPosition(getGame().getBoard().getPositionCard(card), card.hasGold());
	}
}
