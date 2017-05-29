package saboteur.view;

import java.util.LinkedHashMap;

import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import saboteur.model.Game;
import saboteur.model.Card.Card;
import saboteur.tools.GameComponentsSize;
import saboteur.tools.Resources;

public class GameCardContainer extends HBox {
	
	private ImageView[] handCardsImages = new ImageView[6];
	private ImageView imgSelectedCard = new ImageView();
	private Card selectedCard = null;
	private Game game;
	
	private LinkedHashMap<String, Image> allCards;
	
	public GameCardContainer(Game game){
		this.game = game;
        this.setPrefWidth(GameComponentsSize.getGameComponentSize().getGameCardContainerWidth());
        this.setAlignment(Pos.CENTER_RIGHT);
        this.setSpacing(GameComponentsSize.getGameComponentSize().getDefaultSpacing());
        
    	for(int i = 0; i < this.game.getCurrentPlayer().getHand().size(); i++) {
        	this.handCardsImages[i] = new ImageView();
        	this.handCardsImages[i].setFitWidth(GameComponentsSize.getGameComponentSize().getCardWidth()*GameComponentsSize.getGameComponentSize().getGameCardContainerMultiplier());
        	this.handCardsImages[i].setFitHeight(GameComponentsSize.getGameComponentSize().getCardHeight()*GameComponentsSize.getGameComponentSize().getGameCardContainerMultiplier());
        }
        
        this.allCards = Resources.getImage();
	}
	
    public void generateHandCardImage() {
    	this.getChildren().removeAll(this.handCardsImages);
    	for(int i = 0; i < this.game.getCurrentPlayer().getHand().size(); i++) {
    		this.handCardsImages[i].setImage(this.allCards.get(this.game.getCurrentPlayer().getHand().get(i).getFrontImage()));
        	this.getChildren().add(this.handCardsImages[i]);
        }
    }
    
    public void hideCards(){
    	this.getChildren().removeAll(this.handCardsImages);
    	for(int i = 0; i < this.game.getCurrentPlayer().getHand().size(); i++) {
    		this.handCardsImages[i].setImage(this.allCards.get(this.game.getCurrentPlayer().getHand().get(i).getBackImage()));
        	this.getChildren().add(this.handCardsImages[i]);
        }
    }
    
    public void showCards(){
    	this.generateHandCardImage();
    }

	public ImageView getImgSelectedCard() {
		return imgSelectedCard;
	}

	public Card getSelectedCard() {
		return selectedCard;
	}

	public void removeSelection() {
		if(this.selectedCard != null) {
			TranslateTransition tt = new TranslateTransition(Duration.millis(200), this.imgSelectedCard);
			tt.setByY(30);
			ScaleTransition st = new ScaleTransition(Duration.millis(200), this.imgSelectedCard);
			st.setByX(-0.2f);
			st.setByY(-0.2f);
			ParallelTransition pt = new ParallelTransition(tt, st);
			pt.play();
			imgSelectedCard = null;
			this.imgSelectedCard = null;
			this.selectedCard = null;
		}
	}

	public void addSelection(Card selectedCard, ImageView imgSelectedCard){
    	this.selectedCard = selectedCard;
    	this.imgSelectedCard = imgSelectedCard;
		TranslateTransition tt = new TranslateTransition(Duration.millis(200), this.imgSelectedCard);
		tt.setByY(-30);
		ScaleTransition st = new ScaleTransition(Duration.millis(200), this.imgSelectedCard);
		st.setByX(0.2f);
		st.setByY(0.2f);
		ParallelTransition pt = new ParallelTransition(tt, st);
		pt.play();
	}

}
