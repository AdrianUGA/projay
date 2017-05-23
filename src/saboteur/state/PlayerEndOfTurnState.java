package saboteur.state;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import saboteur.GameStateMachine;
import saboteur.model.Game;
import saboteur.view.GameCardContainer;

public class PlayerEndOfTurnState extends State{

    private Button endOfTurnButton;

	public PlayerEndOfTurnState(GameStateMachine gsm, Game game, Stage primaryStage){
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
    	System.out.println("End of turn");
    	
    	this.endOfTurnButton = (Button) this.primaryStage.getScene().lookup("#endOfTurnButton");
    	this.endOfTurnButton.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override public void handle(ActionEvent e) {
    	        endOfTurn();
    	    }
    	});
    	
    }

    @Override
    public void onExit() {
    	this.gsm.push("playerWait");
    }
    
    private void endOfTurn() {
    	//take a new card
    	GameCardContainer cardContainer = (GameCardContainer)this.primaryStage.getScene().lookup("#cardContainer");
    	cardContainer.generateHandCardImage();     	
    	this.endOfTurnButton.setOnAction(null);
    	
    	this.game.nextPlayer();    	
    	this.gsm.pop();
	}
}
