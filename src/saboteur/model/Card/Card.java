package saboteur.model.Card;

public abstract class Card {

    private String frontImage;
    private String backImage;
    
    public String getClassName(){
    	return this.getClass().getName();
    }
}
