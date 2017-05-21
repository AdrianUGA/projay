package saboteur.state;

import javafx.stage.Stage;
import saboteur.GameStateMachine;
import saboteur.model.Game;

public class PlayerEndOfTurnState extends State{

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
    }

    @Override
    public void onExit() {

    }
}
