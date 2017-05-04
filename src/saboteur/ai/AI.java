package saboteur.ai;
import java.util.HashMap;
import java.util.Map;

import saboteur.model.OperationActionCardToBoard;
import saboteur.model.OperationActionCardToPlayer;
import saboteur.model.Player;

public abstract class AI extends Player {
	
	private Map<Player,Float> trust;
	
	public AI(int playerAmount){
		trust = new HashMap<Player,Float>();
		
	}
	
	public void updateTrust(OperationActionCardToBoard o){
		
	}
	
	public void updateTrust(OperationActionCardToPlayer o){
		
	}
	
}
