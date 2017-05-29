package saboteur.state;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import saboteur.GameStateMachine;
import saboteur.model.*;
import saboteur.model.Card.Card;
import saboteur.model.Card.PathCard;
import saboteur.tools.GameComponentsSize;
import saboteur.tools.Icon;
import saboteur.tools.Resources;
import saboteur.view.GameBoardGridPane;
import saboteur.view.GameCardContainer;
import saboteur.view.TrashAndPickStackContainer;
import saboteur.view.PlayerArc;

public class PlayerSelectedPathCardState extends State{

	private GameCardContainer gameCardContainer;
	private GameBoardGridPane gameBoardGridPane;
	private List<Position> possiblePositionList;
	private LinkedHashMap<Position, Object> newBoardEffect;
	private LinkedHashMap<ImageView, Position> positionOfImages;
	private LinkedHashMap<String, Image> allCards = Resources.getImage();
	private boolean positionSelected;
	private TrashAndPickStackContainer trashAndPickStackContainer;

	private ImageView selectedImagePosition;
	private SVGPath selectedSVGPosition;
	private boolean pathCardOnTheBoard;
	private SVGPath rotateSVG;

	private Operation op;
	
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
		PlayerArc playersArc = (PlayerArc) this.primaryStage.getScene().lookup("#playersArc");
		playersArc.refreshPlayersArcsAndCircles();

        double cardWidth = GameComponentsSize.getGameComponentSize().getCardWidth()/3;
        double cardHeight = GameComponentsSize.getGameComponentSize().getCardHeight()/3;
        

    	this.trashAndPickStackContainer = (TrashAndPickStackContainer) this.primaryStage.getScene().lookup("#trashAndPickStackContainer");
        this.gameCardContainer = (GameCardContainer) this.primaryStage.getScene().lookup("#gameCardContainer");
        this.gameBoardGridPane = (GameBoardGridPane) this.primaryStage.getScene().lookup("#gameBoardGridPane");
        this.gameBoardGridPane.toFront();
        this.newBoardEffect = new LinkedHashMap<>();

//        this.gameBoardGridPane.generateBoard();

        this.selectedImagePosition = null;
        this.selectedSVGPosition = null;
		this.positionOfImages = new LinkedHashMap<>();
		this.positionSelected = false;
        
		EventHandler<MouseEvent> event = new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				selectPositionOnBoard(event);
			}
		};
		
		
		if(this.gameCardContainer.getSelectedCard().isCollapseCard()) {
			Map<Position, PathCard> pathCardsPosition = game.getBoard().getPathCardsPositionWhichCanBeRemoved();
			
			//If the map content more than the 1st path card (entry)
			if(pathCardsPosition.size() > 0) {
				for(Position posiCard : pathCardsPosition.keySet()) {
					SVGPath svg = new SVGPath();
					svg.setFill(Color.RED);
					svg.setContent(Icon.minus);
					svg.setMouseTransparent(true);
					GridPane.setHalignment(svg, HPos.CENTER);
					this.newBoardEffect.put(posiCard, svg);
					
					this.positionOfImages.put(this.gameBoardGridPane.getImageOfPosition(posiCard), posiCard);
					
					this.gameBoardGridPane.add(svg, posiCard.getcX(), posiCard.getcY());
				}
				this.gameBoardGridPane.setOnMouseClicked(event);
			}
		}
		else {
			PathCard card  = (PathCard) this.gameCardContainer.getSelectedCard();
			this.possiblePositionList = this.game.getBoard().getPossiblePositionPathCard(card);
			if(this.game.getCurrentPlayer().hasHandicap()) {
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
				
				this.newBoardEffect.put(posiCard, svg);
				this.newBoardEffect.put(posiCard, img);
				
				this.positionOfImages.put(img, posiCard);

				this.gameBoardGridPane.add(svg, posiCard.getcX(), posiCard.getcY());
				this.gameBoardGridPane.add(img, posiCard.getcX(), posiCard.getcY());
			}
			this.gameBoardGridPane.setOnMouseClicked(event);
		}
    }

    @Override
    public void onExit() {
		this.trashAndPickStackContainer.disablePickAndEndTurnButton();
		this.trashAndPickStackContainer.setEventToPickAndEndTurnButton(null);

		if(!this.positionSelected) {
			for(Object obj : this.newBoardEffect.values()) {
				this.gameBoardGridPane.getChildren().remove(obj);
			}
    	}
		this.gameBoardGridPane.setOnMouseClicked(null);
    }
    
    private void selectPositionOnBoard(MouseEvent event) {
    	if(event.getTarget() instanceof ImageView) {
    		
    		Position position = this.positionOfImages.get(event.getTarget());
    		if(position != null){
    			if(this.gameCardContainer.getSelectedCard().isCollapseCard()) {
					if(this.selectedImagePosition != null) {
						this.selectedImagePosition.setOpacity(1.0);
						this.selectedSVGPosition.setVisible(true);
					}
					this.selectedImagePosition = (ImageView) event.getTarget();
					this.selectedImagePosition.setOpacity(0.0);
        			this.positionSelected = true;
        			
        			this.selectedSVGPosition = (SVGPath) this.newBoardEffect.get(position);
        			this.selectedSVGPosition.setVisible(false);
        			
        			this.trashAndPickStackContainer.enablePickAndEndTurnButton();
        			this.trashAndPickStackContainer.setEventToPickAndEndTurnButton(new EventHandler<MouseEvent>() {
        	    	    @Override public void handle(MouseEvent e) {
        	    	    	op = game.getCurrentPlayer().playCard(game.getBoard().getCard(position));
        	    			gsm.changePeek("playerPlayCard", op);
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
        			
        			if(!this.game.getBoard().isPossible((PathCard)this.gameCardContainer.getSelectedCard(), position)){
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
			this.newBoardEffect.put(position, rotateSVG);
		}
    	
    	Card selectedCard = this.gameCardContainer.getSelectedCard();
    	if(this.game.getBoard().isPossible((PathCard)selectedCard, position) && this.game.getBoard().isPossible(((PathCard)selectedCard).reversed(), position) ){
    		
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
    	
    	this.trashAndPickStackContainer.enablePickAndEndTurnButton();
    	this.trashAndPickStackContainer.setEventToPickAndEndTurnButton(new EventHandler<MouseEvent>() {
    	    @Override public void handle(MouseEvent e) {
    	    	PathCard card = (PathCard)selectedCard;
    	    	if(selectedImagePosition.getRotate() != 0){
    	    		card.reverse();
    	    	}
				op = game.getCurrentPlayer().playCard(position);
				gsm.changePeek("playerPlayCard", op);
    	    }
    	});
	}
}
