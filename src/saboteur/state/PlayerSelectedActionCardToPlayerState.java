package saboteur.state;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
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
	private PlayerArc[] playersArc;

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
        
//		int toolValue = ((SabotageCard)this.selectedCard).getTool().getValue();
        int nbPlayer = this.game.getPlayerList().size();
        this.playersArc = new PlayerArc[nbPlayer];
        for(int i = 0; i < nbPlayer; i++) {
        	String name = "#" + this.game.getPlayerList().get(i).getName() + i;
        	name = name.replaceAll("\\s+","");
        	this.playersArc[i] = (PlayerArc) this.primaryStage.getScene().lookup(name);
//        	this.playersArc[i].getCircle()[toolValue].setOnMouseClicked(new EventHandler<MouseEvent>(){
//				@Override
//				public void handle(MouseEvent event) {
//					selectedActionCardToPlayer(event);
//				}
//			});
        }       
        
        

    	
    }

    @Override
    public void onExit() {

    }
    
    private void selectedActionCardToPlayer(MouseEvent event) {
    	System.out.println(event.getTarget());
    	
//    	if(event.getTarget() instanceof Circle) {
//    		int toolValue = ((SabotageCard)this.selectedCard).getTool().getValue();
//    		
//            if(this.selectedCard.isSabotageCard()) {
//        		for(PlayerArc player : this.playersArc) {
//        			if(player.getCircle()[toolValue] == event.getTarget()){
//        				System.out.println("prout");
//        			}
//        		}
//    		}
//            
//        	else if(this.selectedCard.isRescueCard()) {
//        		
//    		}
//        	else if(this.selectedCard.isDoubleRescueCard()) {
//        		
//    		}
//    	}
    }
}

