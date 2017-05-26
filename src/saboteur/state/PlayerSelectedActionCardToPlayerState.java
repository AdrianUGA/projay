package saboteur.state;

import java.util.LinkedList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import saboteur.GameStateMachine;
import saboteur.model.Game;
import saboteur.model.Operation;
import saboteur.model.Player;
import saboteur.model.Card.ActionCardToPlayer;
import saboteur.model.Card.Card;
import saboteur.model.Card.DoubleRescueCard;
import saboteur.model.Card.RescueCard;
import saboteur.model.Card.SabotageCard;
import saboteur.model.Card.Tool;
import saboteur.view.GameCardContainer;
import saboteur.view.PlayerArc;
import saboteur.view.TrashAndPickStackContainer;

public class PlayerSelectedActionCardToPlayerState extends State{
	
	private GameCardContainer gameCardContainer;
	private ActionCardToPlayer card;
	private PlayerArc playersArc;
	private TrashAndPickStackContainer trashAndPickStackContainer;
	private Button endOfTurnButton;
	private LinkedList<Player> playerList;	
	private int toolValue1 = -1;
	private int toolValue2 = -1;
	private boolean playerSelected;

	private Operation op;

    public PlayerSelectedActionCardToPlayerState(GameStateMachine gsm, Game game, Stage primaryStage){
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
        System.out.println("action card");

    	this.trashAndPickStackContainer = (TrashAndPickStackContainer) this.primaryStage.getScene().lookup("#trashAndPickStackContainer");
        this.gameCardContainer = (GameCardContainer) this.primaryStage.getScene().lookup("#gameCardContainer");
    	this.playersArc = (PlayerArc) this.primaryStage.getScene().lookup("#playersArc");
    	this.playerSelected = false;
        
    	EventHandler<MouseEvent> mouseEvent = new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				selectedActionCardToPlayer(event);
			}
		};
    	
        //Put a correct value on toolValue depending of the selected card.
        this.toolValue1 = -1;
        this.toolValue2 = -1;
        Card selectedCard = this.gameCardContainer.getSelectedCard();
        this.card = (ActionCardToPlayer) selectedCard;
        if(selectedCard.isSabotageCard()) {
        	this.toolValue1 = ((SabotageCard)selectedCard).getSabotageType().getValue();
        	this.playerList = this.game.getPlayers(this.card);
			for(Player p : this.game.getPlayers(this.card)) {
				this.playersArc.getCircles(p)[this.toolValue1].toFront();
				this.playersArc.getCircles(p)[this.toolValue1].setStroke(Color.RED);
				this.playersArc.getCircles(p)[this.toolValue1].setOnMouseClicked(mouseEvent);
			}
		}
    	else if(selectedCard.isRescueCard()) {
    		this.toolValue1 = ((RescueCard)selectedCard).getTool().getValue();
        	this.playerList = this.game.getPlayers(this.card);
			for(Player p : this.game.getPlayers(this.card)) {
					this.playersArc.getCircles(p)[toolValue1].setStroke(Color.GREEN);
					this.playersArc.getCircles(p)[this.toolValue1].setOnMouseClicked(mouseEvent);
			}
		}
    	else if(selectedCard.isDoubleRescueCard()) {
    		
    		this.toolValue1 = ((DoubleRescueCard)selectedCard).getTool1().getValue();
    		this.toolValue2 = ((DoubleRescueCard)selectedCard).getTool2().getValue();
        	this.playerList = this.game.getPlayers(this.card);
        	for(Player p : this.game.getPlayers( new RescueCard(this.intToTool(this.toolValue1)))) {
				this.playersArc.getCircles(p)[this.toolValue1].setStroke(Color.GREEN);
				this.playersArc.getCircles(p)[this.toolValue1].setOnMouseClicked(mouseEvent);
        	}
        	for(Player p : this.game.getPlayers( new RescueCard(this.intToTool(this.toolValue2)))) {
				this.playersArc.getCircles(p)[this.toolValue2].setStroke(Color.GREEN);
				this.playersArc.getCircles(p)[this.toolValue2].setOnMouseClicked(mouseEvent);
        	}
		}
    }

    @Override
    public void onExit() {
    	if(!this.playerSelected) {
        	//Delete event on click
            for(int i = 0; i < this.game.getPlayerList().size(); i++) {
    	    	this.playersArc.getCircles(this.game.getPlayerList().get(i))[this.toolValue1].setOnMouseClicked(null);
    	    	if(this.toolValue2 != -1){
    	    		this.playersArc.getCircles(this.game.getPlayerList().get(i))[this.toolValue2].setOnMouseClicked(null);
    	    	}
            }
            
    		for(Player p : this.playerList) {
    			for(int i = 0; i < 3; i++) {
    				this.playersArc.getCircles(p)[i].setStroke(Color.BLACK);
    			}
    		}
    	}
    }
    
    private void selectedActionCardToPlayer(MouseEvent event) {    	
    	if(event.getTarget() instanceof Circle) {
            if(this.gameCardContainer.getSelectedCard().isSabotageCard()) {
            	Circle circle = (Circle) event.getTarget();
            	
            	this.playersArc.refreshCircles(circle, this.toolValue1, true);
            	
            	for(Player p : this.game.getPlayers(this.card)) {
            		if( this.playersArc.getCircles(p)[this.toolValue1] == circle )
            			this.op = this.game.getCurrentPlayer().playCard(p);
    			}
            	
    		}
        	else {
        		Circle circle = (Circle) event.getTarget();
            	
            	for(Player p : this.game.getPlayers(this.card)) {
            		if( this.playersArc.getCircles(p)[this.toolValue1] == circle ){
            			this.op = this.game.getCurrentPlayer().playCard(p, this.intToTool(toolValue1));
                		this.playersArc.refreshCircles(circle, this.toolValue1, false);
            		}
            		if( this.toolValue2 != -1 && this.playersArc.getCircles(p)[this.toolValue2] == circle ) {
            			this.op = this.game.getCurrentPlayer().playCard(p, this.intToTool(toolValue2));
                		this.playersArc.refreshCircles(circle, this.toolValue2, false);
            		}
    			}
        	}
    	}
    	this.beforEnd();
    	this.playerSelected = true;
    }
    
    private void beforEnd() {
    	Button trashButton = (Button)this.primaryStage.getScene().lookup("#trashButton");
    	trashButton.setDisable(true);
    	
    	//Delete event on click
        for(int i = 0; i < this.game.getPlayerList().size(); i++) {
	    	this.playersArc.getCircles(this.game.getPlayerList().get(i))[this.toolValue1].setOnMouseClicked(null);
	    	if(this.toolValue2 != -1){
	    		this.playersArc.getCircles(this.game.getPlayerList().get(i))[this.toolValue2].setOnMouseClicked(null);
	    	}
        }
        
		for(Player p : this.playerList) {
			for(int i = 0; i < 3; i++) {
				this.playersArc.getCircles(p)[i].setStroke(Color.BLACK);
			}
		}

    	this.endOfTurnButton = (Button) this.primaryStage.getScene().lookup("#endOfTurnButton");
    	this.endOfTurnButton.setDisable(false);
    	this.endOfTurnButton.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override public void handle(ActionEvent e) {
    	        endOfTurn();
    	    }
    	});
    	
    	this.trashAndPickStackContainer.enablePickAndEndTurnButton();
    	this.trashAndPickStackContainer.setEventToPickAndEndTurnButton(new EventHandler<MouseEvent>() {
    	    @Override public void handle(MouseEvent e) {
    	        endOfTurn();
    	    }
    	});
    }

    private void endOfTurn() {
    	this.endOfTurnButton.setOnAction(null);
    	this.trashAndPickStackContainer.setEventToPickAndEndTurnButton(null);
    	this.gsm.changePeek("playerPlayCard", this.op);
	}

    private Tool intToTool(int intOfTool) {
    	Tool tool = null;
    	switch(intOfTool) {
    		case 0 :
    			tool = Tool.PICKAXE;
    			break;
    		case 1 :
    			tool = Tool.LANTERN;
    			break;
    		case 2 : 
    			tool = Tool.CART;
    			break;
    	}
    	return tool;
    }
}

