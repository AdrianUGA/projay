package saboteur.state;

import java.util.LinkedList;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
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
	private LinkedList<Player> playerList;	
	private int toolValue1 = -1;
	private int toolValue2 = -1;

	private Player playerSelected;
	private Tool toolSelected;

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
    	this.trashAndPickStackContainer = (TrashAndPickStackContainer) this.primaryStage.getScene().lookup("#trashAndPickStackContainer");
        this.gameCardContainer = (GameCardContainer) this.primaryStage.getScene().lookup("#gameCardContainer");
    	this.playersArc = (PlayerArc) this.primaryStage.getScene().lookup("#playersArc");
    	this.playersArc.refreshPlayersArcsAndCircles();
        
    	EventHandler<MouseEvent> mouseEvent = this::selectedActionCardToPlayer;
    	
        //Put a correct value on toolValue depending of the selected card.
        this.toolValue1 = -1;
        this.toolValue2 = -1;
        Card selectedCard = this.gameCardContainer.getSelectedCard();
        this.card = (ActionCardToPlayer) selectedCard;
        if(selectedCard.isSabotageCard()) {
        	this.toolValue1 = ((SabotageCard)selectedCard).getSabotageType().getValue();
        	this.playersArc.activateHandicapCircle(this.toolValue1, this.game.getPlayers(this.card), mouseEvent, true);
		}
    	else if(selectedCard.isRescueCard()) {
    		this.toolValue1 = ((RescueCard)selectedCard).getTool().getValue();
			this.playersArc.activateHandicapCircle(this.toolValue1, this.game.getPlayers(this.card), mouseEvent, false);
		}
    	else if(selectedCard.isDoubleRescueCard()) {
    		this.toolValue1 = ((DoubleRescueCard)selectedCard).getTool1().getValue();
    		this.toolValue2 = ((DoubleRescueCard)selectedCard).getTool2().getValue();
			this.playersArc.activateHandicapCircle(this.toolValue1, this.game.getPlayers( new RescueCard(Tool.intToTool(this.toolValue1))), mouseEvent, false);
			this.playersArc.activateHandicapCircle(this.toolValue2, this.game.getPlayers( new RescueCard(Tool.intToTool(this.toolValue2))), mouseEvent, false);
		}
    }

    @Override
    public void onExit() {
		this.trashAndPickStackContainer.disablePickAndEndTurnButton();
		this.trashAndPickStackContainer.setEventToPickAndEndTurnButton(null);

		//Delete event on click
		this.playersArc.desactivateHandicapCircle(this.toolValue1, this.toolValue2);
    }
    
    private void selectedActionCardToPlayer(MouseEvent event) {
    	this.playersArc.refreshPlayersArcsAndCircles();
		if(this.gameCardContainer.getSelectedCard().isSabotageCard()) {
			Circle circle = (Circle) event.getTarget();

			this.playersArc.refreshCircles(circle, this.toolValue1, true);

			for(Player p : this.game.getPlayers(this.card)) {
				if( this.playersArc.getCircles(p)[this.toolValue1] == circle ){
					this.playerSelected = p;
				}
			}

		}
		else {
			Circle circle = (Circle) event.getTarget();

			for(Player p : this.game.getPlayers(this.card)) {
				if( this.playersArc.getCircles(p)[this.toolValue1] == circle ){
					this.playerSelected = p;
					this.toolSelected = Tool.intToTool(toolValue1);
					this.playersArc.refreshCircles(circle, this.toolValue1, false);
				}
				if( this.toolValue2 != -1 && this.playersArc.getCircles(p)[this.toolValue2] == circle ) {
					this.playerSelected = p;
					this.toolSelected = Tool.intToTool(toolValue2);
					this.playersArc.refreshCircles(circle, this.toolValue2, false);
				}
			}
		}
		
		this.trashAndPickStackContainer.enablePickAndEndTurnButton();
		this.trashAndPickStackContainer.setEventToPickAndEndTurnButton(e -> endOfTurn());
    }

    private void endOfTurn() {
		Operation op;
		if (this.toolSelected == null){
			op = this.game.getCurrentPlayer().playCard(this.playerSelected);
		} else{
			op = this.game.getCurrentPlayer().playCard(this.playerSelected, this.toolSelected);
		}
    	
    	this.gsm.changePeek("playerPlayCard", op);
	}
}

