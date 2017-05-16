package saboteur.model.Card;

public abstract class Card implements Cloneable{

    protected String frontImage;
    protected String backImage;

    public void setFrontImage(String frontImage){
        this.frontImage = frontImage;
    }

    public void setBackImage(String backImage){
        this.backImage = backImage;
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
}
