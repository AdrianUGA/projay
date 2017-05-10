package saboteur.model.Card;

public abstract class Card {
    
    protected String frontImage;
    protected String backImage;

    protected void setFrontImage(String frontImage){
        this.frontImage = frontImage;
    }

    protected void setBackImage(String backImage){
        this.backImage = backImage;
    }

    protected String getFrontImage(){
        return this.frontImage;
    }

    protected String getBackImage() {
        return this.backImage;
    }

    public String getClassName() {
        return this.getClass().getName();
    }
}
