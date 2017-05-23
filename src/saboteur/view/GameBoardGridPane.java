package saboteur.view;

import java.util.LinkedHashMap;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import saboteur.model.Board;
import saboteur.model.Game;
import saboteur.model.Position;
import saboteur.model.Card.PathCard;
import saboteur.tools.Resources;

public class GameBoardGridPane extends GridPane {

	private Game game;
	private Board board;
	private int xmin, xmax, ymin, ymax;
	
	private Resources resources = new Resources();
	private LinkedHashMap<String, Image> allCards;
	
	
	public GameBoardGridPane(Game game, double XstartInner, double YstartInner) {
		this.game = game;
		this.board = game.getBoard();
		
        this.resources.loadImage();
        this.resources.loadPicto();
        this.allCards = this.resources.getImageCard();
        
		this.setLayoutX(XstartInner);
        this.setLayoutY(YstartInner);
        this.generateBoard();
	}
	
	public void addCardToBoard(PathCard card, Position position){
		this.board.addCard(card, position);
		this.generateBoard();
	}
	
	public void removeCardOfBoard(Position position) {
		this.board.removeCard(position);
		this.generateBoard();
	}
	
	public ImageView getImageOfPosition(Position position) {
		return (ImageView) this.getChildren().get(this.getIndexOfGridPane(position));
	}
	
//	public List<ImageView> getCollapsPossibleImages(){
//		List<ImageView> list = new LinkedList<ImageView>();
//		
//		for(int x = this.xmin; x < this.xmax; x++) {
//			for(int y = this.ymin; y < ymax; y++) {
//				this.getChildren()
//			}
//		}
//		
//		return list;
//	}
	
    private void generateBoard() {
        double cardWidth = 108/3;
        double cardHeight = 166/3;
        
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
					}
				}
				this.add(img, i, j);
        	}
        }
    }
    
    private int getIndexOfGridPane(Position posiCard) {
		int x = posiCard.getcX() - xmin;
		int y = posiCard.getcY() - ymin;
		int dx;
		if (x == 0){
			dx = x * (ymax-ymin);
		}
		else {
			dx = x * (ymax-ymin+1);
		}
		return dx + y;
    }
    
    public LinkedHashMap<String, Image> getAllCards() {
    	return this.allCards;
    }    
}
