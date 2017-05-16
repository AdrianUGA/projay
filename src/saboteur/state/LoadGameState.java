package saboteur.state;

import java.io.IOException;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import saboteur.App;
import saboteur.GameStateMachine;
import saboteur.model.Game;
import saboteur.tools.Loader;

public class LoadGameState implements State {

	@FXML private VBox loadableGameContainer;
	
    private GameStateMachine gsm;
    private Game game;
    private Stage primaryStage;
    
    private final ToggleGroup radionButtonGroupe = new ToggleGroup();

    public LoadGameState(GameStateMachine gsm, Game game, Stage primaryStage){
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
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/saboteur/view/loadGame.fxml"));
            loader.setController(this);
            Pane rootLayout = loader.load();
            Scene scene = new Scene(rootLayout);
            this.primaryStage.setScene(scene);
            this.primaryStage.setFullScreen(true);
            this.primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            
            loadableGames();
            
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onExit() {

    }
    
    @FXML
    private void backButtonAction() {
    	this.gsm.change("mainMenu");
    }
    
    @FXML
    private void startLoadableGameButtonAction() {
    	RadioButton selectedRadioButton = (RadioButton) this.radionButtonGroupe.getSelectedToggle();
//    	this.game.load(selectedRadioButton.getText());
//    	this.gsm.change("game");
    }
    
    private void loadableGames() {
        Loader loaderSaves = new Loader();
        for (String savedFile : loaderSaves.loadSavedFile()) {
            addLoadableGame(savedFile);
        }
    }
    
    private void addLoadableGame(String gameName) {
    	RadioButton savedGame = new RadioButton(gameName);
    	savedGame.setToggleGroup(this.radionButtonGroupe);
    	savedGame.setTextFill(Color.WHITE);
    	savedGame.setStyle("-fx-padding:10px ; -fx-font-size:18px ;");
    	loadableGameContainer.getChildren().add(savedGame);
    }
}
