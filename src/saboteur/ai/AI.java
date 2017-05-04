package saboteur.ai;
import saboteur.model.Game;
import saboteur.model.Operation;

import java.util.HashMap;
import java.util.Map;

import saboteur.model.OperationActionCardToBoard;
import saboteur.model.OperationActionCardToPlayer;
import saboteur.model.Player;

public abstract class AI extends Player {
	
	private Map<Player,Float> trust;

	public AI(Game game) {
		super(game);
		trust = new HashMap<Player,Float>();
	}
	
	public void updateTrust(OperationActionCardToBoard o){
		
	}
	
	public void updateTrust(OperationActionCardToPlayer o){
		
	}
	
	
	
}
