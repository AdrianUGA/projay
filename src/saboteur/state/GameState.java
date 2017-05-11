package saboteur.state;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import saboteur.App;
import saboteur.GameStateMachine;
import saboteur.model.Game;

public class GameState implements State{

	@FXML private Canvas board;
    private GameStateMachine gsm;
    private Game game;
    private Stage primaryStage;

    public GameState(GameStateMachine gsm, Game game, Stage primaryStage){
        this.gsm = gsm;
        this.game = game;
        this.primaryStage = primaryStage;
    }

    @Override
    public void update() {
        if (this.game.gameIsFinished()){
            //fin de la partie
            //this.gsm.change("annonce vainqueur");
            System.out.println("fin de partie");
        } else {
            if (this.game.roundIsFinished()){
                //fin de la manche
                //Distribution des cartes gold
                this.game.newRound();
            } else{
                //la manche continue
                if (this.game.getCurrentPlayer().isAI()){
                    this.game.getCurrentPlayer().playCard();
                    try{
                        Thread.sleep(3000);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    this.game.nextPlayer();
                }
            }
        }
    }

    @Override
    public void render() {

    }

    @Override
    public void onEnter(Object param) {
    	/*this.game.getPlayerList().clear();
    	this.game.addPlayer(player);
    	*/
        this.game.newGame();
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/saboteur/view/boardGame.fxml"));
            loader.setController(this);
            Pane rootLayout = loader.load();
            Scene scene = new Scene(rootLayout);
            
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

            //set Stage boundaries to visible bounds of the main screen
            double size = primaryScreenBounds.getHeight();
            this.board.setHeight(size);
            this.board.setWidth(size);
            
            this.primaryStage.setScene(scene);
            this.primaryStage.setFullScreen(true);
            this.primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onExit() {

    }
}
