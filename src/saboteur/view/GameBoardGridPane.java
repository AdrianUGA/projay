package saboteur.view;

import java.util.LinkedHashMap;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import saboteur.model.Board;
import saboteur.model.Card.Card;
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
        double cardWidth = GameComponentsSize.getGameComponentSize().getCardWidth()/2.7;
        double cardHeight = GameComponentsSize.getGameComponentSize().getCardHeight()/2.7;
        
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

    public void animatePathCard(Position pos, EventHandler<ActionEvent> onFinished){
		ImageView card = this.getImageOfPosition(pos);
		ScaleTransition st = new ScaleTransition(Duration.millis(500), card);
		st.setByX(0.3f);
		st.setByY(0.3f);
		st.setCycleCount(2);
		st.setAutoReverse(true);
		st.setOnFinished(onFinished);
		st.play();
	}

	public void animateCollapseCard(Card card, Position pos, EventHandler<ActionEvent> onFinished){
		ImageView collapseCard = this.getImageOfPosition(pos);
		collapseCard.setImage(Resources.getImage().get(card.getFrontImage()));
		ScaleTransition st = new ScaleTransition(Duration.millis(200), collapseCard);
		st.setByX(0.3f);
		st.setByY(0.3f);

		ScaleTransition st2 = new ScaleTransition(Duration.millis(300), collapseCard);
		st2.setByX(-0.6f);
		st2.setByY(-0.6f);

		FadeTransition ft = new FadeTransition(Duration.millis(300), collapseCard);
		ft.setFromValue(1.0);
		ft.setToValue(0);

		ParallelTransition pt = new ParallelTransition(st2, ft);

		SequentialTransition seq = new SequentialTransition(st, pt);
		seq.setOnFinished(onFinished);
		seq.play();
	}

	public void animateGoalCard(Position pos, EventHandler<ActionEvent> onFinished){
		ImageView card = this.getImageOfPosition(pos);
		ScaleTransition st = new ScaleTransition(Duration.millis(500), card);
		st.setByX(0.3f);
		st.setByY(0.3f);
		st.setCycleCount(2);
		st.setAutoReverse(true);

		RotateTransition rt = new RotateTransition(Duration.millis(500), card);
		rt.setAxis(Rotate.Y_AXIS);
		rt.setByAngle(360);

		ParallelTransition pt = new ParallelTransition(rt, st);
		pt.setOnFinished(onFinished);
		pt.play();
	}
}
