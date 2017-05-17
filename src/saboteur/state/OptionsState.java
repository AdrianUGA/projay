package saboteur.state;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import saboteur.App;
import saboteur.GameStateMachine;
import saboteur.model.Game;

public class OptionsState extends State {

    private GameStateMachine gsm;
    private Game game;
    private Stage primaryStage;

    public OptionsState(GameStateMachine gsm, Game game, Stage primaryStage){
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
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/saboteur/view/options.fxml"));
            loader.setController(this);
            Pane pane = loader.load();
            this.changeLayout(pane);
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
}
