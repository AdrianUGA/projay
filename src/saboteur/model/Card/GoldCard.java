package saboteur.model.Card;

public class GoldCard extends Card {
	private int value;

	public GoldCard(int value){
	    this.value = value;
    }
	
	public int getValue(){
		return this.value;
	}
}
