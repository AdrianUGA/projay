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
	private Position estimatedGoldCardPosition;
	

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
		estimatedGoldCardPosition = new Position(0,0);
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
		int taxiDistance = this.getGame().getBoard().getTaxiDistance(o.getP(), estimatedGoldCardPosition);
		int neighborsAmount = this.getGame().getBoard().numberOfNeighbors(o.getP());
		
		if(((PathCard) o.getCard()).isCulDeSac()){ 
			// The closer the gold card, the heavier is the card.
			// The more, the merr... heavier.
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
	
}
