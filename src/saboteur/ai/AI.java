package saboteur.ai;
import saboteur.model.Player;

public abstract class AI extends Player {
	
	private int trust[];
	
	public AI(int playerAmount){
		for(int i=0; i<playerAmount;i++){
			trust[i] = 50;
		}
	}
	
}
