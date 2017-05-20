package saboteur.state;

import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import saboteur.GameStateMachine;
import saboteur.model.Game;
import saboteur.model.Card.Card;
import saboteur.model.Card.DoubleRescueCard;
import saboteur.model.Card.RescueCard;
import saboteur.model.Card.SabotageCard;
import saboteur.view.PlayerArc;

public class PlayerSelectedActionCardToPlayerState extends State{
	
	private Pane boardContainer;
	private Card selectedCard;
	private PlayerArc playersArc;
	private int toolValue1 = -1;
	private int toolValue2 = -1;	

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
        this.boardContainer = (Pane) this.primaryStage.getScene().lookup("#boardContainer");
        
        this.toolValue1 = -1;
        this.toolValue2 = -1;
        if(this.selectedCard.isSabotageCard()) {
    		this.toolValue1 = ((SabotageCard)this.selectedCard).getTool().getValue();
		}
        
    	else if(this.selectedCard.isRescueCard()) {
    		this.toolValue1 = ((RescueCard)this.selectedCard).getTool().getValue();
		}
    	else if(this.selectedCard.isDoubleRescueCard()) {
    		this.toolValue1 = ((DoubleRescueCard)this.selectedCard).getTool1().getValue();
    		this.toolValue2 = ((DoubleRescueCard)this.selectedCard).getTool2().getValue();
		}
        for(int i = 0; i < this.game.getPlayerList().size(); i++) {
        	this.playersArc = (PlayerArc) this.primaryStage.getScene().lookup("#playersArc");
        	
        	this.playersArc.getCircles(this.game.getPlayerList().get(i))[this.toolValue1].setOnMouseClicked(new EventHandler<MouseEvent>(){
				@Override
				public void handle(MouseEvent event) {
					selectedActionCardToPlayer(event);
				}
			});
        	
        	//TODO : a revoir
        	if(this.toolValue2 != -1){
        		this.playersArc.getCircles(this.game.getPlayerList().get(i))[this.toolValue2].setOnMouseClicked(new EventHandler<MouseEvent>(){
    				@Override
    				public void handle(MouseEvent event) {
    					selectedActionCardToPlayer(event);
    				}
    			});
        	}
        }       
    }

    @Override
    public void onExit() {
        for(int i = 0; i < this.game.getPlayerList().size(); i++) {
	    	this.playersArc.getCircles(this.game.getPlayerList().get(i))[this.toolValue1].setOnMouseClicked(null);
	    	if(this.toolValue2 != -1){
	    		this.playersArc.getCircles(this.game.getPlayerList().get(i))[this.toolValue2].setOnMouseClicked(null);
	    	}
        }
    }
    
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
            
        	else if(this.selectedCard.isRescueCard()) {
        		Circle circle = (Circle) event.getTarget();
            	circle.setStroke(Color.BLACK);
            	circle.setFill(null);
            	
    		}
        	else if(this.selectedCard.isDoubleRescueCard()) {
        		Circle circle = (Circle) event.getTarget();
            	circle.setStroke(Color.BLACK);
            	circle.setFill(null);
    		}
    	}
    }
}

