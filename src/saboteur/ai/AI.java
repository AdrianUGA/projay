package saboteur.ai;
import saboteur.model.Game;
import saboteur.model.Operation;

import java.util.HashMap;
import java.util.Map;

import saboteur.model.OperationActionCardToBoard;
import saboteur.model.OperationActionCardToPlayer;
import saboteur.model.OperationPathCard;
import saboteur.model.Player;
import saboteur.model.Card.PathCard;

public abstract class AI extends Player {
	
	private Map<Player,Float> trust;	
	private Difficulty difficulty;
	

	public AI(Game game) {
		super(game);
		trust = new HashMap<Player,Float>();
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
	
	public void updateTrust(OperationActionCardToBoard o){
		switch(o.getCard().getClass().getName()){
		case "PlanCard":
			// Nothing to update
			break;
		case "CollapseCard":
			if(((PathCard) o.getCard()).isCulDeSac()){
				trust.put(o.getSourcePlayer(), trust.get(o.getSourcePlayer()) + 10);
			}
			else{
				trust.put(o.getSourcePlayer(), trust.get(o.getSourcePlayer()) - 10);
			}
		}
	}
	
	public void updateTrust(OperationActionCardToPlayer o){
		
	}
	
	public void updateTrust(OperationPathCard o){
		
		
	}
	
	public AI setDifficulty(Difficulty difficulty){
		this.difficulty = difficulty;
		return this;
	}
	
	
	
}
