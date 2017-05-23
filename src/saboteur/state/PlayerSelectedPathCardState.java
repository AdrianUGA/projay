package saboteur.state;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
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
import saboteur.model.Player;
import saboteur.model.Position;
import saboteur.model.Card.Card;
import saboteur.model.Card.PathCard;
import saboteur.tools.Icon;
import saboteur.view.GameBoardGridPane;
import saboteur.view.GameCardContainer;

public class PlayerSelectedPathCardState extends State{

	private GameBoardGridPane gameBoardGridPane;
	private List<Position> possiblePositionList;
	private List<Object> boardEffect;
	private LinkedHashMap<Object, Position> positionOfImages;
	private Card selectedCard;
	private boolean positionSelected;
	private Button endOfTurnButton;
	
	
    public PlayerSelectedPathCardState(GameStateMachine gsm, Game game, Stage primaryStage){
        super(gsm, game, primaryStage);
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
        this.gameBoardGridPane = (GameBoardGridPane) this.primaryStage.getScene().lookup("#gameBoardGridPane");
        this.gameBoardGridPane.toFront();
        this.boardEffect = new LinkedList<>();

		this.positionOfImages = new LinkedHashMap<Object, Position>();
		this.positionSelected = false;
        
		EventHandler<MouseEvent> event = new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				selectPositionOnBoard(event);
			}
		};
		
		
		if(this.selectedCard.isCollapseCard()) {
			Map<Position, PathCard> pathCardsPosition = this.game.getBoard().getPathCardsPosition();
			
			//If the map content more than the 1st path card (entry)
			if(pathCardsPosition.size() > 1) {
				pathCardsPosition.remove(Board.getStart());
				
				for(Position posiCard : pathCardsPosition.keySet()) {
					SVGPath svg = new SVGPath();
					svg.setFill(Color.RED);
					svg.setContent(Icon.minus);
					GridPane.setHalignment(svg, HPos.CENTER);
					this.boardEffect.add(svg);
					
					this.positionOfImages.put(svg, posiCard);
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
			System.out.println("possible position list : " + possiblePositionList);
			for(Position posiCard : this.possiblePositionList) {
				SVGPath svg = new SVGPath();
				svg.setFill(Color.GREEN);
				svg.setContent(Icon.plus);
				GridPane.setHalignment(svg, HPos.CENTER);
				
				ImageView img = new ImageView("/resources/cards/"+card.getFrontImage());	
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
    	if(event.getTarget() instanceof ImageView || event.getTarget() instanceof SVGPath) {
    		
    		Position position = this.positionOfImages.get(event.getTarget());
    		if(position != null){
    			if(this.selectedCard.isCollapseCard()) {
    				this.gameBoardGridPane.removeCardOfBoard(position);
        			
        		}
        		else {
            		this.gameBoardGridPane.addCardToBoard((PathCard)this.selectedCard, position);
        		}
    			this.beforEnd();
    			this.positionSelected = true;
    		}
    	}
    }
    
    private void beforEnd() {
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
    	
    	this.endOfTurnButton = (Button) this.primaryStage.getScene().lookup("#endOfTurnButton");
    	this.endOfTurnButton.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override public void handle(ActionEvent e) {
    	        endOfTurn();
    	    }
    	});
    }
    
    private void endOfTurn() {
    	this.endOfTurnButton.setOnAction(null);
    	this.gsm.pop();
    	this.gsm.push("playerEndOfTurn");
	}
    
}
