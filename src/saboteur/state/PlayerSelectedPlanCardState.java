package saboteur.state;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import saboteur.GameStateMachine;
import saboteur.model.Game;
import saboteur.model.Position;
import saboteur.model.Card.Card;
import saboteur.tools.Icon;
import saboteur.view.GameCardContainer;

public class PlayerSelectedPlanCardState extends State{

	private Object[] svgEyes= new Object[3];
	private StackPane[] paneOfGoalCard = new StackPane[3];
	private VBox goalCardContainer;
	private Button endOfTurnButton;
	private Circle gameBoard;
	private Card selectedCard;
	private boolean goalCardSelect;
	
    public PlayerSelectedPlanCardState(GameStateMachine gsm, Game game, Stage primaryStage){
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
        System.out.println("plan card");
        
        
        this.selectedCard = (Card) param;
        this.goalCardContainer = (VBox) this.primaryStage.getScene().lookup("#goalCardContainer");
        this.goalCardSelect = false;
        this.gameBoard = (Circle)this.primaryStage.getScene().lookup("#gameBoard");
        
        this.gameBoard.toFront();
        this.goalCardContainer.toFront();
    	this.goalCardContainer.setVisible(true);
    	
    	
        //Put verso of goal card on created ImageView, create eye svg and for each MouseClick event.
        int i = 0;
    	for (Node n : this.goalCardContainer.getChildren()) {
    		this.paneOfGoalCard[i] = (StackPane) n;
    		
    		//Change Image
    		ImageView img = (ImageView) this.paneOfGoalCard[i].getChildren().get(0);
        	img.setImage(new Image("/resources/cards/goal_card_verso.png"));
        	
        	//Add SVG
        	SVGPath svg = new SVGPath();
        	svg.setFill(Color.WHITE);
        	svg.setContent(Icon.eye);
        	this.svgEyes[i] = svg;
        	this.paneOfGoalCard[i].getChildren().add(svg);
        	
        	//Add Action
        	this.paneOfGoalCard[i].setOnMouseClicked(new EventHandler<MouseEvent>(){
				@Override
				public void handle(MouseEvent event) {
					selectGoalCard(event);
				}
			});
        	i++;        	
    	}
    }

    @Override
    public void onExit() {
    	//Delete SVG and Mouse event
    	if(!this.goalCardSelect) {
	    	for(int i = 0; i < 3; i++){
	    		this.paneOfGoalCard[i].getChildren().remove(this.svgEyes[i]);
	    		this.paneOfGoalCard[i].setOnMouseClicked(null);
	    	}
	    	    	
			this.gameBoard.toBack();
			this.goalCardContainer.setVisible(false);
    	}
    }
    
    private void selectGoalCard(MouseEvent event) {
    	if(event.getTarget() instanceof ImageView || event.getTarget() instanceof SVGPath) {
    		int i = 0;
        	for (Node n : this.goalCardContainer.getChildren()) {
        		StackPane p = (StackPane) n;
        		
        		//Turn selected card
        		if(event.getSource() == p) {
        			ImageView img = (ImageView) p.getChildren().get(0);
        			Position posi = this.game.getBoard().getGoalCards().get(i);
        			img.setImage( new Image("resources/cards/" + this.game.getBoard().getCard(posi).getFrontImage()) );
                	this.beforEnd();
        			this.goalCardSelect = true;
                	break;
        		}
        		i++;
        	}
    	}
    }
    
    private void beforEnd() {
    	for(int i = 0; i < 3; i++){
    		this.paneOfGoalCard[i].getChildren().remove(this.svgEyes[i]);
    		this.paneOfGoalCard[i].setOnMouseClicked(null);
    	}
    	
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
    	this.gameBoard.toBack();
		this.goalCardContainer.setVisible(false);
    	this.endOfTurnButton.setOnAction(null);
    	this.gsm.pop();
    	this.gsm.push("playerEndOfTurn");
	}
}
