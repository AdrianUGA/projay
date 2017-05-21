package saboteur.state;

import java.util.LinkedList;

import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import saboteur.GameStateMachine;
import saboteur.model.Game;
import saboteur.model.Player;
import saboteur.model.Card.ActionCardToPlayer;
import saboteur.model.Card.Card;
import saboteur.model.Card.DoubleRescueCard;
import saboteur.model.Card.RescueCard;
import saboteur.model.Card.SabotageCard;
import saboteur.view.GameCardContainer;
import saboteur.view.PlayerArc;

public class PlayerSelectedActionCardToPlayerState extends State{
	
	private Card selectedCard;
	private PlayerArc playersArc;
	private LinkedList<Player> playerList;	
	private int toolValue1 = -1;
	private int toolValue2 = -1;
	private boolean playerSelected;

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
        
        this.selectedCard = (Card) param;
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
        if(this.selectedCard.isSabotageCard()) {
        	this.toolValue1 = ((SabotageCard)this.selectedCard).getSabotageType().getValue();
        	ActionCardToPlayer card = (ActionCardToPlayer) this.selectedCard;
        	this.playerList = this.game.getPlayers(card);
			for(Player p : this.game.getPlayers(card)) {
				this.playersArc.getCircles(p)[this.toolValue1].setStroke(Color.RED);
				this.playersArc.getCircles(p)[this.toolValue1].setOnMouseClicked(mouseEvent);
			}
		}
    	else if(this.selectedCard.isRescueCard()) {
    		this.toolValue1 = ((RescueCard)this.selectedCard).getTool().getValue();
    		ActionCardToPlayer card = (ActionCardToPlayer) this.selectedCard;
        	this.playerList = this.game.getPlayers(card);
			for(Player p : this.game.getPlayers(card)) {
					this.playersArc.getCircles(p)[toolValue1].setStroke(Color.GREEN);
					this.playersArc.getCircles(p)[this.toolValue1].setOnMouseClicked(mouseEvent);
			}
		}
        //TODO : a revoir
    	else if(this.selectedCard.isDoubleRescueCard()) {
    		this.toolValue1 = ((DoubleRescueCard)this.selectedCard).getTool1().getValue();
    		this.toolValue2 = ((DoubleRescueCard)this.selectedCard).getTool2().getValue();
    		ActionCardToPlayer card = (ActionCardToPlayer) this.selectedCard;
        	this.playerList = this.game.getPlayers(card);
			for(Player p : this.game.getPlayers(card)) {
				this.playersArc.getCircles(p)[toolValue1].setStroke(Color.GREEN);
				this.playersArc.getCircles(p)[toolValue2].setStroke(Color.GREEN);
				this.playersArc.getCircles(p)[this.toolValue1].setOnMouseClicked(mouseEvent);
				this.playersArc.getCircles(p)[this.toolValue2].setOnMouseClicked(mouseEvent);
			}
		}
    }

    @Override
    public void onExit() {
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
				this.playersArc.setOnMouseClicked(null);
			}
		}
		
    	if(this.playerSelected) {
    		this.game.getCurrentPlayer().getHand().remove(this.selectedCard);
        	
    		//Code : Go to EndOfTurn, generate new hand card image and delete event of the card selection
        	GameCardContainer cardContainer = (GameCardContainer)this.primaryStage.getScene().lookup("#cardContainer");
        	cardContainer.setOnMouseClicked(null);
        	cardContainer.generateHandCardImage(); 
        	this.gsm.push("playerEndOfTurn");
    	}
        
    }
    //TODO : ici juste l'ihm affiche le bonus / malus. Manque le joueur a affectee
    private void selectedActionCardToPlayer(MouseEvent event) {    	
    	if(event.getTarget() instanceof Circle) {
            if(this.selectedCard.isSabotageCard()) {
            	Circle circle = (Circle) event.getTarget();
            	circle.setStroke(Color.BLACK);
            	Image img = null;
            	switch(this.toolValue1) {
            		case 0 : 
                    	img = new Image("/resources/picto/broken_pickaxe_picto.png");
                    	break;
            		case 1 : 
                    	img = new Image("/resources/picto/broken_lantern_picto.png");
                    	break;
            		case 2 : 
                    	img = new Image("/resources/picto/broken_cart_picto.png");
                    	break;
            	}
            	circle.setFill(new ImagePattern(img));
    		}
            
        	else {
        		Circle circle = (Circle) event.getTarget();
            	circle.setStroke(Color.BLACK);
            	circle.setFill(null);
        	}
            	
    	}
    	this.playerSelected = true;
    	this.gsm.pop();
    }
}

