package saboteur.view;

import java.util.LinkedHashMap;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import saboteur.model.Board;
import saboteur.model.Game;
import saboteur.model.Position;
import saboteur.model.Card.PathCard;
import saboteur.tools.GameComponentsSize;
import saboteur.tools.Resources;

public class GameBoardGridPane extends GridPane {

	private Board board;
	private Game game;
	private int xmin, xmax, ymin, ymax;
	
	private LinkedHashMap<String, Image> allCards;
	
	private ImageView[][] imagesOfGridPane;
	
	public GameBoardGridPane(Game game) {
		this.game = game;
		
        this.allCards = Resources.getImage();
        this.generateBoard();
        
        this.setAlignment(Pos.CENTER);
	}
	
    public void generateBoard() {
		this.board = game.getBoard();
        double cardWidth = GameComponentsSize.getGameComponentSize().getCardWidth()/3;
        double cardHeight = GameComponentsSize.getGameComponentSize().getCardHeight()/3;
        
        this.getChildren().clear();
        
        this.xmin = Board.getGridSize();
        this.xmax = 0;
        this.ymin = Board.getGridSize();
        this.ymax = 0;
        
        for(int i = 0; i < Board.getGridSize(); i++) {
			for (int j = 0; j < Board.getGridSize(); j++) {
				PathCard card = this.board.getCard(new Position(i,j));
				if( card != null) {
					if(this.xmin > i) {
						this.xmin = i;
					}
					if(this.xmax < i) {
						this.xmax = i;
					}
					if(this.ymin > j) {
						this.ymin = j;
					}
					if(this.ymax < j) {
						this.ymax = j;
					}
				}
			}
        }
        this.xmin--;
        this.xmax++;
        this.ymin--;
        this.ymax++; 
        
        this.imagesOfGridPane = new ImageView[Board.getGridSize()][Board.getGridSize()];
        this.getChildren().clear();
        
    	for(int i = this.xmin; i <= this.xmax; i++) {
    		for (int j = this.ymin; j <= this.ymax; j++) {
				ImageView img = new ImageView();
				PathCard card = this.board.getCard(new Position(i,j));
				
				if(i >= this.xmin && i <= this.xmax && j >= this.ymin && j <= this.ymax) {
					img.setFitHeight(cardHeight);
					img.setFitWidth(cardWidth);
					if( card != null) {
						if(card.isVisible()) {
	    					img.setImage(this.allCards.get(card.getFrontImage()));
						}
						else {
	    					img.setImage(this.allCards.get(card.getBackImage()));
						}
						
						if(card.isReversed()) {
							img.setRotate(180.0);
						}
					}
				}
				this.imagesOfGridPane[i][j] = img;
				this.add(img, i, j);
        	}
        }
    	// Set Layout acording to the number of card on the board
		double boardHeight = cardHeight * (this.ymax - this.ymin + 1);
		double boardWidth = cardWidth * (this.xmax - this.xmin+ 1);
    	this.setLayoutX(GameComponentsSize.getGameComponentSize().getCenterOfGameTable()-boardWidth/2);
    	this.setLayoutY(GameComponentsSize.getGameComponentSize().getCenterOfGameTable()-boardHeight/2);
    }
    
    public ImageView getImageOfPosition(Position posiCard) {
    	return this.imagesOfGridPane[posiCard.getcX()][posiCard.getcY()];
    }
    
    public LinkedHashMap<String, Image> getAllCards() {
    	return this.allCards;
    }    
}
