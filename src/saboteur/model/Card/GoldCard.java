package saboteur.model.Card;

public class GoldCard extends Card {

	private static final long serialVersionUID = 5809967009172390309L;
	private int value;

	public GoldCard(int value){
	    this.value = value;
    }
	
	public int getValue(){
		return this.value;
	}

	public GoldCard clone(){
		return (GoldCard) super.clone();
	}
}
