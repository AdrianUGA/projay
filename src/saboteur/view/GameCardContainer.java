package saboteur.view;

import java.util.HashMap;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import saboteur.model.Game;
import saboteur.tools.Resources;

public class GameCardContainer extends HBox {
	
	private ImageView[] handCardsImages = new ImageView[6];
	private Game game;
	
	private Resources resources = new Resources();
	private HashMap<String, Image> allCards;
	
	public GameCardContainer(Game game, double gameTableSize){
		this.game = game;
        this.setPrefWidth(gameTableSize);
        this.setAlignment(Pos.CENTER);
        this.setSpacing(20.0);
        AnchorPane.setBottomAnchor(this, 5.0);
        
        this.resources.loadImage();
        this.resources.loadPicto();
        this.allCards = this.resources.getImageCard();
        
	}
	
    public void generateHandCardImage() {
    	this.getChildren().removeAll(this.handCardsImages);
    	for(int i = 0; i < this.game.getCurrentPlayer().getHand().size(); i++) {
        	this.handCardsImages[i] = new ImageView(this.allCards.get(this.game.getCurrentPlayer().getHand().get(i).getFrontImage()));
        	this.handCardsImages[i].setFitWidth(108);
        	this.handCardsImages[i].setFitHeight(166);
        	this.getChildren().add(this.handCardsImages[i]);
        }
    }

}
