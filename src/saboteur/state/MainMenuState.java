package saboteur.state;

import javafx.stage.Stage;
import saboteur.GameStateMachine;
import saboteur.controller.MainMenuController;
import saboteur.model.Game;

import java.io.IOException;

public class MainMenuState implements State {

    private GameStateMachine gsm;
    private Game game;
    private Stage primaryStage;

    public MainMenuState(GameStateMachine gsm, Game game, Stage primaryStage){
        this.gsm = gsm;
        this.game = game;
        this.primaryStage = primaryStage;
    }

    @Override
    public void update() {

    }

    @Override
    public void render() {

    }

    @Override
    public void onEnter(Object param) {
        MainMenuController controller = new MainMenuController(this.primaryStage);
    }

    @Override
    public void onExit() {

    }
}