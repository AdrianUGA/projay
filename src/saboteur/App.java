package saboteur;

import javafx.application.Application;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import saboteur.model.Game;
import saboteur.state.*;


public class App extends Application {

    private static Game game;

    public void run() {
        game = new Game();
        launch();
    }

    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Saboteur");
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.show();
        GameStateMachine gsm = new GameStateMachine();
        gsm.add("mainMenu", new MainMenuState(gsm, game, primaryStage));
        gsm.add("mainMenu", new MainMenuState(gsm, game, primaryStage));
        gsm.add("game", new GameState(gsm, game, primaryStage));
        gsm.add("pauseMenu", new PauseMenuState(gsm, game, primaryStage));
        gsm.add("loadGame", new LoadGameState(gsm, game, primaryStage));
        gsm.add("saveGame", new SaveGameState(gsm, game, primaryStage));
        gsm.add("options", new OptionsState(gsm, game, primaryStage));
        gsm.add("score", new ScoreState(gsm, game, primaryStage));
        gsm.add("help", new HelpState(gsm, game, primaryStage));

        gsm.change("mainMenu");

        GameLoop gameLoop = new GameLoop(gsm);
        gameLoop.start();
    }
}
