package saboteur.state;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import saboteur.GameStateMachine;
import saboteur.model.Board;
import saboteur.model.Game;
import saboteur.model.OperationPathCard;
import saboteur.model.Position;
import saboteur.model.Card.Card;
import saboteur.model.Card.PathCard;
import saboteur.tools.Icon;
import saboteur.tools.Resources;
import saboteur.view.GameBoardGridPane;
import saboteur.view.GameCardContainer;

public class PlayerSelectedPathCardState extends State{

	private GameBoardGridPane gameBoardGridPane;
	private List<Position> possiblePositionList;
	private List<Object> boardEffect;
	private LinkedHashMap<ImageView, Position> positionOfImages;
	private LinkedHashMap<String, Image> allCards = Resources.getImage();
	private Card selectedCard;
	private boolean positionSelected;
	private Button endOfTurnButton;

	private ImageView selectedImagePosition;
	private boolean pathCardOnTheBoard;
	private SVGPath rotateSVG;
	
    public PlayerSelectedPathCardState(GameStateMachine gsm, Game game, Stage primaryStage){
        super(gsm, game, primaryStage);
        
        this.rotateSVG = new SVGPath();
        this.rotateSVG.setFill(Color.BLACK);
        this.rotateSVG.setContent(Icon.repeat);
        this.rotateSVG.setMouseTransparent(true);
		GridPane.setHalignment(this.rotateSVG, HPos.CENTER);
    }

    @Override
    public void update() {

    }

    @Override
    public void render() {

    }

    @Override
    public void onEnter(Object param) {
        System.out.println("path card");
        double cardWidth = 108/3;
        double cardHeight = 166/3;
        

        this.selectedCard = (Card) param;
        this.endOfTurnButton = (Button) this.primaryStage.getScene().lookup("#endOfTurnButton");
        this.gameBoardGridPane = (GameBoardGridPane) this.primaryStage.getScene().lookup("#gameBoardGridPane");
        this.gameBoardGridPane.toFront();
        this.boardEffect = new LinkedList<>();

        this.selectedImagePosition = null;
		this.positionOfImages = new LinkedHashMap<ImageView, Position>();
		this.positionSelected = false;
        
		EventHandler<MouseEvent> event = new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				selectPositionOnBoard(event);
			}
		};
		
		
		if(this.selectedCard.isCollapseCard()) {
			Map<Position, PathCard> pathCardsPosition = new LinkedHashMap<>(game.getBoard().getPathCardsPosition());
			
			//If the map content more than the 1st path card (entry)
			if(pathCardsPosition.size() > 1) {
				pathCardsPosition.remove(Board.getStart());
//				for(Position p : this.game.getBoard().getGoalCards()) {
//					System.out.println(p);
//					System.out.println(this.game.getBoard().getCard(p));
//					pathCardsPosition.remove(p);
//				}
				System.out.println(pathCardsPosition);
				for(Position posiCard : pathCardsPosition.keySet()) {
					SVGPath svg = new SVGPath();
					svg.setFill(Color.RED);
					svg.setContent(Icon.minus);
					svg.setMouseTransparent(true);
					GridPane.setHalignment(svg, HPos.CENTER);
					this.boardEffect.add(svg);
					
					this.positionOfImages.put(this.gameBoardGridPane.getImageOfPosition(posiCard), posiCard);
					
					this.gameBoardGridPane.add(svg, posiCard.getcX(), posiCard.getcY());
				}
				this.gameBoardGridPane.setOnMouseClicked(event);
			}
		}
		else {
			PathCard card  = (PathCard) this.selectedCard;
			this.possiblePositionList = this.game.getBoard().getPossiblePositionPathCard(card);
			if(this.game.getCurrentPlayer().getHandicaps().size() != 0) {
				this.possiblePositionList.clear();
			}
			for(Position posiCard : this.possiblePositionList) {
				SVGPath svg = new SVGPath();
				svg.setFill(Color.GREEN);
				svg.setContent(Icon.plus);
				svg.setMouseTransparent(true);
				GridPane.setHalignment(svg, HPos.CENTER);
				
				ImageView img = new ImageView(this.allCards.get(card.getFrontImage()));	
				img.setFitHeight(cardHeight);
				img.setFitWidth(cardWidth);
				img.setOpacity(0);
				
				this.boardEffect.add(svg);
				this.boardEffect.add(img);
				this.positionOfImages.put(img, posiCard);

				this.gameBoardGridPane.add(svg, posiCard.getcX(), posiCard.getcY());
				this.gameBoardGridPane.add(img, posiCard.getcX(), posiCard.getcY());
			}
			this.gameBoardGridPane.setOnMouseClicked(event);
		}
    }

    @Override
    public void onExit() {
		if(!this.positionSelected) {
			for(Object obj : this.boardEffect) {
				this.gameBoardGridPane.getChildren().remove(obj);
			}
    	}
		this.gameBoardGridPane.setOnMouseClicked(null);
    }
    
    private void selectPositionOnBoard(MouseEvent event) {
    	if(event.getTarget() instanceof ImageView) {
    		
    		Position position = this.positionOfImages.get(event.getTarget());
    		if(position != null){
    			if(this.selectedCard.isCollapseCard()) {
    				this.gameBoardGridPane.removeCardOfBoard(position);
        			this.beforEnd();
        			this.positionSelected = true;
        			this.endOfTurnButton.setDisable(false);
        			this.endOfTurnButton.setOnAction(new EventHandler<ActionEvent>() {
        	    	    @Override public void handle(ActionEvent e) {
        	    	        endOfTurn();
        	    	    }
        	    	});
        		}
        		else {
        			this.pathCardOnTheBoard = false;
        			if(this.selectedImagePosition != null) {
        				this.selectedImagePosition.setOpacity(0.0);
        				this.selectedImagePosition.setOnMouseClicked(null);
            			this.pathCardOnTheBoard = true;
        			}
        			this.selectedImagePosition = (ImageView) event.getTarget();
        			this.selectedImagePosition.setOpacity(1.0);
        			
        			if(!this.game.getBoard().isPossible((PathCard)this.selectedCard, position)){
        				this.selectedImagePosition.setRotate(180.0);
        			}
        			
        			this.selectRotationOfCard(position);
        		}
    		}
    	}
    }
        
    private void selectRotationOfCard(Position position) {
    	if(this.pathCardOnTheBoard) { 
			this.gameBoardGridPane.getChildren().remove(this.rotateSVG);
		}
    	else{
			this.boardEffect.add(this.rotateSVG);
		}
    	
    	if(this.game.getBoard().isPossible((PathCard)this.selectedCard, position) && this.game.getBoard().isPossible(((PathCard)this.selectedCard).reversed(), position) ){
    		
    		//Manage rotation SVG    		
			this.gameBoardGridPane.add(this.rotateSVG, position.getcX(), position.getcY());
			
			//Set mouse event on the selected card on the board
			this.selectedImagePosition.setOnMouseClicked(new EventHandler<MouseEvent>(){
				@Override
				public void handle(MouseEvent event) {
					selectedImagePosition.setRotate((selectedImagePosition.getRotate()+180.0)%360);
				}
			});
		}    
    	
    	this.endOfTurnButton.setDisable(false);
    	this.endOfTurnButton.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override public void handle(ActionEvent e) {
    	    	PathCard card = (PathCard)selectedCard;
    	    	if(selectedImagePosition.getRotate() != 0){
    	    		card.reverse();
    	    	}
    	    	gameBoardGridPane.addCardToBoard(card, position);
    	        beforEnd();
    	        endOfTurn();
    	    }
    	});
	}
    

	private void beforEnd() {
		Button trashButton = (Button)this.primaryStage.getScene().lookup("#trashButton");
    	trashButton.setDisable(true);
    	
    	for(Object obj : this.boardEffect) {
			this.gameBoardGridPane.getChildren().remove(obj);
		}
    	
    	this.gameBoardGridPane.setOnMouseClicked(null);
    	
    	this.game.getCurrentPlayer().getHand().remove(this.selectedCard);
    	
		//Code : Go to EndOfTurn, generate new hand card image and delete event of the card selection
    	this.game.getCurrentPlayer().pickCard();
    	GameCardContainer cardContainer = (GameCardContainer)this.primaryStage.getScene().lookup("#cardContainer");
    	cardContainer.setOnMouseClicked(null);
    	cardContainer.generateHandCardImage(); 
    }
    
    private void endOfTurn() {
    	this.endOfTurnButton.setOnAction(null);
		this.gsm.changePeek("playerEndOfTurn");
	}
    
}
