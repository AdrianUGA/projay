package saboteur.model.Card;

public class DoubleRescueCard extends ActionCardToPlayer {
	public int rescueType1;
	public int rescueType2;
	
	public DoubleRescueCard(int type1, int type2){
		this.rescueType1 = type1;
		this.rescueType2 = type2;
	}

	public int getRescueType1() {
		return this.rescueType1;
	}

	public int getRescueType2() {
		return this.rescueType2;
	}
	
	
}
