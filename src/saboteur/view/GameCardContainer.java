package saboteur.view;

import java.util.LinkedHashMap;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.transform.Scale;
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
	private GameComponentsSize gameComponentsSize;
	
	private LinkedHashMap<String, Image> allCards;
	
	public GameCardContainer(Game game){
		this.game = game;
		this.gameComponentsSize = GameComponentsSize.getGameComponentSize();
        this.setPrefWidth(this.gameComponentsSize.getGameCardContainerWidth());
        this.setAlignment(Pos.CENTER_RIGHT);
        this.setSpacing(this.gameComponentsSize.getDefaultSpacing());
        
    	for(int i = 0; i < this.game.getCurrentPlayer().getHand().size(); i++) {
        	this.handCardsImages[i] = new ImageView();
        	this.handCardsImages[i].setFitWidth(this.gameComponentsSize.getCardWidth() * this.gameComponentsSize.getGameCardContainerMultiplier());
        	this.handCardsImages[i].setFitHeight(this.gameComponentsSize.getCardHeight() * this.gameComponentsSize.getGameCardContainerMultiplier());
        }
        
        this.allCards = Resources.getImage();
	}
	
    public void generateHandCardImage() {
    	this.getChildren().removeAll(this.handCardsImages);
    	for(int i = 0; i < this.game.getCurrentPlayer().getHand().size(); i++) {
			this.handCardsImages[i] = new ImageView();
			this.handCardsImages[i].setFitWidth(this.gameComponentsSize.getCardWidth() * this.gameComponentsSize.getGameCardContainerMultiplier());
			this.handCardsImages[i].setFitHeight(this.gameComponentsSize.getCardHeight() * this.gameComponentsSize.getGameCardContainerMultiplier());
    		this.handCardsImages[i].setImage(this.allCards.get(this.game.getCurrentPlayer().getHand().get(i).getFrontImage()));
        	this.getChildren().add(this.handCardsImages[i]);
        }
    }
    
    public void hideCards(){
    	this.getChildren().removeAll(this.handCardsImages);
    	for(int i = 0; i < this.game.getCurrentPlayer().getHand().size(); i++) {
			this.handCardsImages[i] = new ImageView();
			this.handCardsImages[i].setFitWidth(this.gameComponentsSize.getCardWidth() * this.gameComponentsSize.getGameCardContainerMultiplier());
			this.handCardsImages[i].setFitHeight(this.gameComponentsSize.getCardHeight() * this.gameComponentsSize.getGameCardContainerMultiplier());
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
			tt.setByY(50);
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
		tt.setByY(-50);
		ScaleTransition st = new ScaleTransition(Duration.millis(200), this.imgSelectedCard);
		st.setByX(0.2f);
		st.setByY(0.2f);
		ParallelTransition pt = new ParallelTransition(tt, st);
		pt.play();
	}

	public void animateCardToTrash(int indexOfCard, EventHandler<ActionEvent> onFinished){
		ParallelTransition pt = new ParallelTransition();
		ScaleTransition st = new ScaleTransition(Duration.millis(400), this.handCardsImages[indexOfCard]);
		st.setByX(0.47f);
		st.setByY(0.47f);
		st.setInterpolator(Interpolator.EASE_IN);

		ScaleTransition st2 = new ScaleTransition(Duration.millis(400), this.handCardsImages[indexOfCard]);
		st2.setByX(-0.2f);
		st2.setByY(-0.2f);
		st2.setInterpolator(Interpolator.EASE_OUT);

		SequentialTransition sequence = new SequentialTransition(st, st2);

		TranslateTransition tt = new TranslateTransition(Duration.millis(800), this.handCardsImages[indexOfCard]);
		tt.setFromX(0);
		tt.setFromY(0);
		tt.setToX(this.gameComponentsSize.getLayoutXOfTrashStack() - (this.gameComponentsSize.getLayoutXOfGameCardContainer() + this.handCardsImages[indexOfCard].getLayoutX()) + 25);
		tt.setToY(this.gameComponentsSize.getLayoutYOfTrashStack() - (this.gameComponentsSize.getLayoutYOfGameCardContainer() + this.handCardsImages[indexOfCard].getLayoutY()) + 35);

		pt.getChildren().addAll(sequence, tt);
		pt.setOnFinished(onFinished);
		pt.play();
	}

	public void animateCard(Card card, int indexOfCard, EventHandler<ActionEvent> onFinished){
		TranslateTransition tt = new TranslateTransition(Duration.millis(600), this.handCardsImages[indexOfCard]);
		tt.setFromX(0);
		tt.setFromY(0);
		tt.setToX(-this.handCardsImages[indexOfCard].getLayoutX() + 100);
		tt.setToY(-400);

		ScaleTransition st = new ScaleTransition(Duration.millis(600), this.handCardsImages[indexOfCard]);
		st.setByX(0.2f);
		st.setByY(0.2f);

		ParallelTransition pt = new ParallelTransition(tt, st);
		pt.setOnFinished(event -> {
			this.handCardsImages[indexOfCard].setImage(Resources.getImage().get(card.getFrontImage()));
			PauseTransition pause = new PauseTransition(Duration.millis(1000));
			pause.setOnFinished(onFinished);
			pause.play();
		});
		pt.play();
	}
}
