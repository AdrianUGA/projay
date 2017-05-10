package saboteur.model.Card;

public abstract class Card {

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
}
