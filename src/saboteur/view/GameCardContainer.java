package saboteur.view;

import java.util.LinkedHashMap;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import saboteur.model.Game;
import saboteur.tools.GameComponentsSize;
import saboteur.tools.Resources;

public class GameCardContainer extends HBox {
	
	private ImageView[] handCardsImages = new ImageView[6];
	private Game game;
	
	private LinkedHashMap<String, Image> allCards;
	
	public GameCardContainer(Game game, double gameTableSize){
		this.game = game;
        this.setPrefWidth(gameTableSize);
        this.setAlignment(Pos.CENTER);
        this.setSpacing(20.0);
        AnchorPane.setBottomAnchor(this, 5.0);
        AnchorPane.setLeftAnchor(this, 5.0);
        AnchorPane.setRightAnchor(this, 5.0);
        
    	for(int i = 0; i < this.game.getCurrentPlayer().getHand().size(); i++) {
        	this.handCardsImages[i] = new ImageView();
        	this.handCardsImages[i].setFitWidth(GameComponentsSize.getGameComponentSize().getCardWidth());
        	this.handCardsImages[i].setFitHeight(GameComponentsSize.getGameComponentSize().getCardHeight());
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

}
