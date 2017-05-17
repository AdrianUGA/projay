package saboteur.model.Card;

import java.io.Serializable;

public abstract class Card implements Cloneable, Serializable{

	//To load config
	protected int id;
	
    protected String frontImage;
    protected String backImage;

    public void setId(int id){
    	this.id = id;
    }
    
    public void setFrontImage(String frontImage){
        this.frontImage = frontImage;
    }

    public void setBackImage(String backImage){
        this.backImage = backImage;
    }

    public int getId(){
    	return this.id;
    }
    
    public String getFrontImage(){
        return this.frontImage;
    }

    public String getBackImage() {
        return this.backImage;
    }

    public String getClassName() {
        return this.getClass().getName();
    }

    public Card clone(){
        Card card = null;
        try{
            card = (Card) super.clone();
        } catch(CloneNotSupportedException cnse){
            cnse.printStackTrace();
        }
        return card;
    }
    
    public boolean isCollapseCard(){
    	return false;
    }
    
    public boolean isRescueCard(){
    	return false;
    }
    
    public boolean isDoubleRescueCard(){
    	return false;
    }
    
    public boolean isPathCard(){
    	return false;
    }
    
    public boolean isPlanCard(){
    	return false;
    }
    
    public boolean isSabotageCard(){
    	return false;
    }
    
}
