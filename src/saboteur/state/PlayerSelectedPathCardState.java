package saboteur.state;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import saboteur.GameStateMachine;
import saboteur.model.Board;
import saboteur.model.Game;
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
	private LinkedHashMap<ImageView, Position> positionOfImages;
	private Card selectedCard;
	private boolean positionSelected;
	
	
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
		this.positionOfImages = new LinkedHashMap<ImageView, Position>();
		this.positionSelected = false;
        
		if(this.selectedCard.isCollapseCard()) {
			Map<Position, PathCard> pathCardsPosition = this.game.getBoard().getPathCardsPosition();
			pathCardsPosition.remove(Board.getStart());
			
			for(Map.Entry<Position,PathCard> m:pathCardsPosition.entrySet()) {
				SVGPath svg = new SVGPath();
				svg.setFill(Color.RED);
				svg.setContent(Icon.minus);
				GridPane.setHalignment(svg, HPos.CENTER);
				this.boardEffect.add(svg);
				Position p = (Position) m.getKey();
				this.gameBoardGridPane.add(svg, p.getcX(), p.getcY());
			}
		}
		else {
			System.out.println("la?");
			PathCard card  = (PathCard) this.selectedCard;
			System.out.println(card);
			this.possiblePositionList = this.game.getBoard().getPossiblePositionPathCard(card);
			for(Position posiCard : this.possiblePositionList) {
				System.out.println("in?");
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
		}
		
		this.gameBoardGridPane.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				selectPositionOnBoard(event);
			}
		});
		

    }

    @Override
    public void onExit() {
		for(Object obj : this.boardEffect) {
			this.gameBoardGridPane.getChildren().remove(obj);
		}
		
		if(this.positionSelected) {
    		this.game.getCurrentPlayer().getHand().remove(this.selectedCard);
        	
    		//Code : Go to EndOfTurn, generate new hand card image and delete event of the card selection
        	GameCardContainer cardContainer = (GameCardContainer)this.primaryStage.getScene().lookup("#cardContainer");
        	cardContainer.setOnMouseClicked(null);
        	cardContainer.generateHandCardImage(); 
        	this.gsm.push("playerEndOfTurn");
    	}
		
		this.gameBoardGridPane.setOnMouseClicked(null);
    }
    
    private void selectPositionOnBoard(MouseEvent event) {
    	if(event.getTarget() instanceof ImageView || event.getTarget() instanceof SVGPath) {
    		Position position = this.positionOfImages.get(event.getTarget());
    		if(this.selectedCard.isCollapseCard()) {
    			this.gameBoardGridPane.removeCardOfBoard(position);
    		}
    		else {
        		this.gameBoardGridPane.addCardToBoard((PathCard)this.selectedCard, position);
    		}
    		this.positionSelected = true;
    		this.gsm.pop();
    	}
    }
    
}
