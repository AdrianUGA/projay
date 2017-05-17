package saboteur.state;

import javafx.stage.Stage;
import saboteur.GameStateMachine;
import saboteur.model.Game;

public class PlayerDrawCardState extends State{

    private GameStateMachine gsm;
    private Game game;
    private Stage primaryStage;

    public PlayerDrawCardState(GameStateMachine gsm, Game game, Stage primaryStage){
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

    }

    @Override
    public void onExit() {

    }
}
